package com.mlt.e200cp.utilities.helper.shell;
import com.mlt.e200cp.utilities.helper.ports.PortManager;
import com.mlt.e200cp.utilities.helper.protocol.TLV;
import com.mlt.e200cp.utilities.helper.protocol.VNG;
import com.mlt.e200cp.utilities.helper.util.Crypto;
import com.mlt.e200cp.utilities.helper.util.Utility;

import java.util.Arrays;

import static com.mlt.e200cp.utilities.helper.util.Logger.LINE_OUT;
import static com.mlt.e200cp.utilities.helper.util.Logger.log;

public class P2PEManager extends BaseShell {

    private int host = 0;
    private int client = 0;

    public char lastState = 0x00;

    public P2PEManager() {}

    public P2PEManager(PortManager portManager) {
        BaseShell.portManager = portManager;
    }

    public enum STATE { INIT, RUN, TAMPER, STOP, UA, SHIPPING, MAINTENACE, LOADER, OTHRES }

    public STATE geteEnumState(int target) {
        char status = getState(target);
        switch (status) {
            case '0' : return STATE.INIT;
            case '1' : return STATE.RUN;
            case '2' : return STATE.TAMPER;
            case '4' : return STATE.STOP;
            case '5' : return STATE.UA;
            case 'S' : return STATE.SHIPPING;
            case 'W' : return STATE.MAINTENACE;
            case 'G' : return STATE.LOADER;
            default: return STATE.OTHRES;
        }
    }

    /*
        {State}: device operation state
        ‘0’ = Initial (Manufacturing)
        ‘1’ = Run
        ‘2’ = Tamper
        ‘4’ = Stop
        ‘5’ = UA (Un-authenticated)
        ‘S’ = Shipping
        ‘W’ = Maintenance (WFA)
        ‘G’= Loader (Device Firmware Update)
    */
    public char getState(int target) {
        portManager.sendTarget = target;
        portManager.purge();    // purge firstly to avoid existing data in queue

        byte [] rsp = portManager.exchangeData("R1".getBytes(), 2, 500 );
        if (rsp == null)
            return 0x00;
        else if (rsp.length == 3 && rsp[0] == (byte)'R' && rsp[1] == (byte)'1')
            lastState = (char)(rsp[2] & 0xFF);

        return lastState;
    }

    public interface ActiveHandler {
        enum TYPE { PWD, NEW_PWD, TIMEOUT_CANCEL, ERROR }
        boolean toEnable(boolean isShipping);

        void stoppted(int custodian);
        void onClose();
    }

    public enum SYSTEM_STATE { RUN, AUTH_FAILED, HOST_STOP, CLIENT_STOP, HOST_ERROR, CLIENT_ERROR }

    public SYSTEM_STATE tryToActiveDevice(int host, int client) {

        // check host status
        STATE state = geteEnumState(host);

        if (state == STATE.RUN) {
            // ok
        } else if (state == STATE.SHIPPING || state == STATE.MAINTENACE) {

            enableRemovalDetection(host);
            try { Thread.sleep(500); } catch (Exception ex) {}
            if (geteEnumState(host) != STATE.RUN) return SYSTEM_STATE.HOST_ERROR;

        } else if (state == STATE.STOP) {
            return SYSTEM_STATE.HOST_STOP;
        } else {
            return SYSTEM_STATE.HOST_ERROR;
        }

        state = geteEnumState(client);

        if (state == STATE.SHIPPING || state == STATE.MAINTENACE) {

            enableRemovalDetection(client);
            try {
                Thread.sleep(500);
            } catch (Exception ex) {
            }
            state = geteEnumState(client);
        }

        // UA or missing message encryption key
        if (state == STATE.UA || (state == STATE.RUN && portManager.ports[client].msgEncrytHandler == null)) {

            if (!mutualAuthentication(host, client))
                return SYSTEM_STATE.AUTH_FAILED;

        } else if (state == STATE.RUN) {
            // ok
        } else if (state == STATE.STOP) {
            return SYSTEM_STATE.CLIENT_STOP;
        } else {
            return SYSTEM_STATE.CLIENT_ERROR;
        }

        return SYSTEM_STATE.RUN;
    }

    public boolean enableRemovalDetection(int target) {
        portManager.sendTarget = target;
        return new VNG(portManager.exchangeData("AD4".getBytes()), "AD4", true, true).isSuccess;
    }

    public boolean forceRsaMaKeyLoading(int hostDevice, int clientDevice) {
        configHostClient(hostDevice, clientDevice);
        return rsa_ma_key_exchange();
    }

    public boolean mutualAuthentication(int hostDevice, int clientDevice) {

        configHostClient(hostDevice, clientDevice);

        if (!checkMak()) {
            if (!rsa_ma_key_exchange())
                return false;
        }

        if (!aes_ma())
            return false;

        return true;
    }

    public boolean entryClientMaintenance(int device) {
        client = device;
        return challenge_install();
    }

    public boolean changeMaintenancePassword(int device, byte [] e_pwdA, byte [] e_pwdB) {

        portManager.sendTarget = device;

        VNG req = new VNG();

        // AD0A{ TDES-ECB-Encrypt [IAK, ID | PWD-A | New PWD-A] }
        req.addData("AD0A");
        req.addData(Utility.bytes2Hex(e_pwdA));

        VNG rsp = portManager.exchangeData(req);

        if (rsp == null || !rsp.parseHeader("AD02")) {
            log("Fail to update password A" , LINE_OUT());
            return false;
        }

        req.clear();

        req.addData("AD0B");
        req.addData(Utility.bytes2Hex(e_pwdB));

        rsp = portManager.exchangeData(req);

        if (rsp == null || !rsp.parseHeader("AD00")) {
            log("Fail to update password B" , LINE_OUT());
            return false;
        }

        return true;
    }

    public interface PasswordInput {
        enum TYPE { PWD, NEW_PWD, TIMEOUT_CANCEL, ERROR }
        void onInput(int len, TYPE type, int custodian);
        void onReady(int custodian);
        void onClose();
    }

    public String passwordInputLoop(String cmd, PasswordInput updatePasswordInput, int custodian) {

        updatePasswordInput.onReady(custodian);

        // KC0 PIN input loop
        while(true) {

            // The timeout between each key press is 30 seconds.
            // The timeout for the whole password input is 5 minutes
            VNG rsp = portManager.waitVngData(30000);

            if (rsp.size == 0 || !rsp.parseHeader("KC")) {
                updatePasswordInput.onClose();
                return "1";
            }

            if (rsp.parseHeader("KC0")) {
                int len = 0;
                // “00” ~ “99” = current keypad entry count
                // “E0” = length < minimal length
                // “E1” = length > maximum length
                String lenInfo = rsp.parseString(2);
                if (lenInfo.equals("E0")) {
                    // Digit Too Less
                } else if (lenInfo.equals("E1")) {
                    // Digit Exceed Max
                } else {
                    updatePasswordInput.onInput(Integer.parseInt(lenInfo), PasswordInput.TYPE.PWD, custodian);
                }
            } else if (rsp.parseHeader(cmd)) {

                updatePasswordInput.onClose();
                return rsp.parseString(1);

            } else {
                updatePasswordInput.onClose();
                return "1";
            }
        }
    }

    public boolean updatePasswordFlow(String cmd, PasswordInput updatePasswordInput) {

        int step = 0;

        while(true) {

            int custodian = cmd.equals("KC3") ? 1 : 2;

            // input old password -> input new password
            // KC30 or KC40 -> KC31 or KC41

            if (!portManager.exchangeData(new VNG(cmd + step)).parseHeader(cmd + "0")) {
                log(cmd + " No Response or error", LINE_OUT());
                return false;
            }

            String status = passwordInputLoop(cmd, updatePasswordInput, custodian);

            /*
                0: please enter password
                1: invalid parameter
                2: cancel by user
                3: timeout
                4: old password verified, please enter new password
                5: old password verified fail
                6: new password verified ok
                7: new password verified fail
                8: invalid new password (Password all digits cannot be the same)
            */
            switch (status) {
                case "4":   // old password ok
                    updatePasswordInput.onInput(0, PasswordInput.TYPE.NEW_PWD, custodian);
                    break;
                case "2":
                case "3":
                    updatePasswordInput.onInput(0, PasswordInput.TYPE.TIMEOUT_CANCEL, custodian);
                    return false;
                case "6":   // new password ok
                    return true;
                default:
                    updatePasswordInput.onInput(0, PasswordInput.TYPE.ERROR, custodian);
                    return false;
            }

            step++;
        }
    }

    public boolean updatePasswordByPinpad(int device, PasswordInput updatePasswordInput) {

        portManager.sendTarget = device;

        // update first password
        if (!updatePasswordFlow("KC3", updatePasswordInput))
            return false;

        // update second password
        if (!updatePasswordFlow("KC4", updatePasswordInput))
            return false;

        return true;
    }

    public boolean entryHostMaintenanceByPinpad(int device, PasswordInput updatePasswordInput) {
        portManager.sendTarget = device;

        // first password
        if (!portManager.exchangeData(new VNG("KC1")).parseHeader("KC10")) {
            log("KC1 No Response or error", LINE_OUT());
            return false;
        }

        // 4: old password verified
        if (!passwordInputLoop("KC1", updatePasswordInput, 1).equals("4"))
            return false;

        // second password
        if (!portManager.exchangeData(new VNG("KC2")).parseHeader("KC20")) {
            log("KC2 No Response or error", LINE_OUT());
            return false;
        }

        if (!passwordInputLoop("KC2", updatePasswordInput, 2).equals("4"))
            return false;

        return true;
    }



    public String entryHostMaintenance_1(int device, byte [] secret) {

        portManager.sendTarget = device;

        // [KA1_PASSWORD]=KA1{Method_Password}{IAK#}{Secret}
        // Secret = TDES-ECB-Encrypt [IAK, PWD-A]
        VNG req = new VNG();
        req.addData("KA1" + Def.PASSWORD_INSTALL_METHOD);
        req.addData((byte)Def.HOST_IAK_SLOT);
        req.addData(Utility.bytes2Hex(secret));

        VNG rsp = portManager.exchangeData(req);

        if (rsp == null) {
            log("KA1 No Response", LINE_OUT());
            return "";
        }

        if (!rsp.parseHeader("KA1", true, true)) {
            log("KA1 Error : " + rsp.status, LINE_OUT());
            return "";
        }

        // PASSWORD
        // <STX> KA1{Status}{Method}{Return}<ETX>{LRC}
        // Return = {RND_A}{ID VNG}

        String method = rsp.parseString(1);
        String randomA = rsp.parseString(16);
        String idVng = rsp.parseString(16);

        return randomA;
    }

    public boolean entryHostMaintenance_2(byte [] proofB) {

        // [KA2_AES]=KA2{Method_PASSWORD}{IAK#}{PROOF_B}
        VNG req = new VNG();
        req.addData("KA2" + Def.PASSWORD_INSTALL_METHOD);
        req.addData((byte)Def.HOST_IAK_SLOT);
        req.addData(Utility.bytes2Hex(proofB));

        VNG rsp = portManager.exchangeData(req);

        if (rsp == null) {
            log("KA2 No Response", LINE_OUT());
            return false;
        }

        if (!rsp.parseHeader("KA2", true, true)) {
            log("KA1 Error : " + rsp.status, LINE_OUT());
            return false;
        }

        return true;
    }

    //region Private Functions

    private void configHostClient(int hostDevice, int clientDevice) {
        host = hostDevice;
        client = clientDevice;
    }

    private boolean checkMak() {
        String[] kcv = new String[2];
        int i = 0;
        for(int dev : new int[] { host, client}) {
            portManager.sendTarget = dev;

            VNG rsp = new VNG(portManager.exchangeData(("KM1" + Def.configs[dev].MAK_SLOT).getBytes()), "KM1", true, true);
            if (rsp.isSuccess)
                kcv[i++] = rsp.parseString();
            else
                return false;
        }
        return kcv[0].equals(kcv[1]);
    }

    private byte [] rsa_ma_key_exchange_flow(int target, String cmd, byte [] reqTlvData) {

        portManager.sendTarget = target;

        // KRx<RS>{Len}{TLV Data}
        VNG req = new VNG(cmd);
        req.addRSLenData(reqTlvData);

        VNG rsp = portManager.exchangeData(req, 5000);
        if (rsp == null || !rsp.parseHeader(cmd))
            return null;

        // <- KRx<RS>{Len}{Status}{TLV Data}
        int len = rsp.parseRSLen();
        byte status = rsp.parseByte();
        if (status != 0) {
            log(cmd + " Error : " + Integer.toString(status, 16), LINE_OUT());
            return null;
        }
        return rsp.parseBytes(len-1);
    }

    private boolean rsa_ma_key_exchange() {
        log("RSA MA and TestData Exchange", LINE_OUT());

        byte [] rspTlvData = null;
        TLV tlv = new TLV();

        // KR7 -> host : Generate RSA Mutual Authentication Exchange Request
        tlv.clear();
        tlv.addData(0xC7, new byte[] { Def.ids[host] });
        tlv.addData(0xC8, new byte[] { Def.ids[client] });

        rspTlvData = rsa_ma_key_exchange_flow(host, "KR7", tlv.toByteArray());
        if (rspTlvData == null)
            return false;

        // KR4 -> client : RSA Mutual Authentication Exchange
        rspTlvData = rsa_ma_key_exchange_flow(client, "KR4", rspTlvData);
        if (rspTlvData == null)
            return false;

        // KR8 -> host : Verify RSA MA Exchange Response & Generate Master TestData Import Request
        tlv.clear();
        tlv.addData(0xCF, new byte[] { 0 });
        tlv.addData(rspTlvData);
        tlv.addData(0xCD, new byte[] { (byte)Def.configs[host].MAK_SLOT});
        tlv.addData(0xCE, new byte[] { (byte)Def.configs[client].MAK_SLOT});

        // prepare key derivation template
        TLV derivationTemplate = new TLV();
        derivationTemplate.addData(0xE3, new byte[] {
                (byte)0xCD, 0x01, (byte)Def.configs[host].IAK_SLOT,
                (byte)0xCE, 0x01, (byte)Def.configs[client].IAK_SLOT,
                (byte)0xD0, 0x01, 0x00
        });

        derivationTemplate.addData(0xE3, new byte[] {
                (byte)0xCD, 0x01, (byte)Def.configs[host].OFFPIN_SLOT,
                (byte)0xCE, 0x01, (byte)Def.configs[client].OFFPIN_SLOT,
                (byte)0xD0, 0x01, 0x01
        });
        derivationTemplate.putInTemplate(0xE2);

        tlv.addData(derivationTemplate);

        rspTlvData = rsa_ma_key_exchange_flow(host, "KR8", tlv.toByteArray());
        if (rspTlvData == null)
            return false;

        // KR5 -> client : Import Master TestData and Signature
        rspTlvData = rsa_ma_key_exchange_flow(client, "KR5", rspTlvData);
        if (rspTlvData == null)
            return false;

        // KR9 -> host : RSA Import TestData Completion
        tlv.clear();
        tlv.addData(0xC8, new byte[] { Def.ids[client] });
        tlv.addData(rspTlvData);
        rspTlvData = rsa_ma_key_exchange_flow(host, "KR9", tlv.toByteArray());
        if (rspTlvData == null)
            return false;

        return true;
    }

    private String auth_flow(int target, String cmd, String data) {

        portManager.sendTarget = target;

        // KAx{ASCII Data}
        VNG req = new VNG(cmd);
        req.addData(data);

        VNG rsp = portManager.exchangeData(req);
        if (rsp == null || !rsp.parseHeader(cmd)) {
            if (rsp != null)
                log(cmd + " Mismatch Cmd", LINE_OUT());
            else
                log(cmd + " No Response", LINE_OUT());
            return null;
        }

        char status = (char)(rsp.parseByte() & 0xFF);
        if (status != '0') {
            log(cmd + " Error : " + status, LINE_OUT());
            return null;
        }

        // <- KAx{Status}{ASCII Data}
        String rspData = rsp.parseString();

        // KA7{Status}{Method}<RS>{LEN}{Session TestData}
        if (cmd.equals("KA7")) {

            // key + 3 bytes kcv
            int len = rsp.parseRSLen();
            byte [] sk = rsp.parseBytes(len - 3);
            byte [] kcv = rsp.parseBytes(3);

            byte [] verifiedKcv = Crypto.getKcv(Crypto.ALGORITHM.AES, sk);

            // verify kcv
            if (!Arrays.equals(kcv, verifiedKcv)) {
                log(cmd + " Verify KCV fail - KEY : " + Utility.bytes2Hex(sk)
                        + " , KCV " + Utility.bytes2Hex(kcv), LINE_OUT());
                return null;
            }
            portManager.ports[client].msgEncrytHandler = new MsgEncrytHandlerE5(sk);
        }

        return rspData;
    }

    private boolean mutual_auth(char method, char hostKey, char clientKey) {

        String rspData = "";
        String reqData = "";

        // KA4 –> host : Create Client Authentication Request
        // -> KA4{Method}{TestData Slot}
        // <- KA4{Status}{Method}{Return}
        rspData = auth_flow(host, "KA4", "" + method + hostKey + rspData);
        if (rspData == null)
            return false;

        // KA1 –> client : Client Authentication
        // -> KA1{Method}{MAK#}{Secret}
        // <- KA1{Status}{Method}{Return}
        rspData = auth_flow(client, "KA1", "" + method + clientKey + rspData.substring(1));
        if (rspData == null)
            return false;

        // KA5 –> host : Verify Client Authentication Response & Create Host Authentication Request
        // -> KA5{Method}{Input}
        // <- KA5{Status}{Method}{Return}
        rspData = auth_flow(host, "KA5", rspData);
        if (rspData == null)
            return false;

        // KA2 -> client : Host Authentication
        // -> KA2{Method}{MAK#}{PROOF_B}
        // <- KA2{Status}
        rspData = auth_flow(client, "KA2", "" + method + clientKey + rspData.substring(1));
        if (rspData == null)
            return false;

        return true;
    }

    private boolean aes_ma() {
        log("AES MA", LINE_OUT());

        char method = Def.AES_MA_METHOD;
        char hostKey = Def.configs[host].MAK_SLOT;
        char clientKey = Def.configs[client].MAK_SLOT;

        String rspData = "";

        if (!mutual_auth(method, hostKey, clientKey))
            return false;

        // KA6 –> host : Verify Host Authentication Response & Create Session TestData Exchange Request
        // -> KA6{Method}{Input}
        // <- KA6{Status}{Method}{Return}
        rspData = auth_flow( host, "KA6", "" + method + "0" + rspData);
        if (rspData == null)
            return false;

        // KA3 –> client : Session TestData Exchange
        // -> KA3{Method}{MAK#}{E_RND_S}{KCV}{ME Format}
        // <- KA3{Status}
        rspData = auth_flow( client, "KA3", "" + method + clientKey + rspData.substring(1) + "5");
        if (rspData == null)
            return false;

        // KA7 – Verify Session TestData Exchange Response
        // -> KA7{Method}{Input}
        // <- KA7{Status}{Method}<RS>{LEN}{Session TestData}{KCV}
        rspData = auth_flow( host, "KA7", "" + method + "0" + rspData);
        if (rspData == null)
            return false;

        return true;
    }

    private boolean challenge_install() {
        log("Challenge Install", LINE_OUT());

        char method = Def.CHALLENGE_INSTALL_METHOD;
        char hostKey = Def.configs[host].IAK_SLOT;
        char clientKey = Def.configs[client].IAK_SLOT;

        return mutual_auth(method, hostKey, clientKey);
    }

    //endregion

}
