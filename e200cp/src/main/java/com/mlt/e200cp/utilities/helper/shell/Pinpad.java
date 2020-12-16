package com.mlt.e200cp.utilities.helper.shell;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.mlt.e200cp.controllers.mainlogiccontrollers.HelperEMVClass;
import com.mlt.e200cp.utilities.helper.protocol.VNG;
import com.mlt.e200cp.utilities.helper.util.PinpadManager;
import com.mlt.e200cp.utilities.helper.util.Utility;


public class Pinpad extends BaseShell {

    public static boolean isVirtualPinpad = false;

    public enum TYPE { ONLINE, OFFLINE, UPT_OFFLINE }

    public PinpadManager pinpadManager;

    private int MAX_INPUT_TIMEOUT = 60000;

    public static class Params {
        public TYPE type = TYPE.ONLINE;
        private char key = '0';
        private char format = '0';
        private String config = "0412060";  // {Min}{Max}{Timeout}
        private String pan = "0000000000";
        public byte [] dataOfEncryptedPin = null;

        public Params(TYPE type) {
            this.type = type;
        }

        public Params(TYPE type, char key, char format, String pan) {
            this.type = type;
            this.key = key;
            this.format = format;
            this.pan = pan;
        }

        public Params(TYPE type, byte [] dataOfEncryptedPin) {
            this.type = type;
            this.dataOfEncryptedPin = dataOfEncryptedPin;
        }
    }

    public enum Status { OK, CANCEL, TIMEOUT, BYPASS, ERR }
    public static class Result {
        public Status status = Status.ERR;
        public String data = "";
    }

    public Pinpad() {
        pinpadManager = new PinpadManager( isVirtualPinpad);
        pinpadManager.ready();
    }

    public Result input(Params params) {

        Result result = new Result();

        if (!pinpadManager.start()){
            return result;
        }

        MAX_INPUT_TIMEOUT = Integer.parseInt(params.config.substring(3)) * 1000;

        String ret = "1";

        if (params.type == TYPE.ONLINE)
            ret = onlinePin(params);
        else if (params.type == TYPE.OFFLINE)
            ret = offlinePin(params);
        else if (params.type == TYPE.UPT_OFFLINE)
            ret = offlinePinUPT(params);



        switch (ret.charAt(0)) {
            case '0':
                result.status = Status.OK;
                result.data = ret.substring(1);
                break;
            case '2':
                result.status = Status.CANCEL;
                break;
            case '3':
                result.status = Status.TIMEOUT;
                break;
            default:
                result.status = Status.ERR;
                break;
        }

        pinpadManager.end();

        return result;
    }

    private VNG pinInputLoop() {

        long startTime = System.currentTimeMillis();

        // EP0 PIN input loop
        while(true) {

            int elapsedTime = (int)(System.currentTimeMillis() - startTime);
            if (elapsedTime < 0) return null;

            VNG rsp = portManager.waitVngData(MAX_INPUT_TIMEOUT - elapsedTime);

            if (rsp.size == 0 || !rsp.parseHeader("EP"))
                return rsp;

            if (rsp.parseByte() == (byte)'0') {
                int len = 0;
                // “00” ~ “99” = current keypad entry count
                // “E0” = length < minimal length
                // “E1” = length > maximum length
                String lenInfo = rsp.parseString(2);
                if (lenInfo.equals("E0")) {
                    new Handler(Looper.getMainLooper()).post(()->{
                        Toast.makeText(HelperEMVClass.appCompatActivity,"Pin can't be less than 4 digits",Toast.LENGTH_LONG).show();
                    });
                    // Pin Digit Too Less
                } else if (lenInfo.equals("E1")) {
                    new Handler(Looper.getMainLooper()).post(()->{
                        Toast.makeText(HelperEMVClass.appCompatActivity,"Pin can't be more than 12 digits",Toast.LENGTH_LONG).show();
                    });
                    // Pin Digit Exceed Max
                } else {
                    pinpadManager.updatePinLength(Integer.parseInt(lenInfo));
                }
            } else
                return rsp;
        }
    }

    // return 1 digit status + 4 digit RPDU status word if success
    private String offlinePinUPT(Params params) {

        // - EPO{Min}{Max}{Timeout}{Data}
        // - Start PIN Process
        // - {Data} = {PK Len}{PK Modulus}{Challenge}{PK Exponent}

        VNG req = new VNG("EPO" + params.config);
        if (params.dataOfEncryptedPin != null)
            req.addRSLenData(params.dataOfEncryptedPin);

        if (!portManager.sendData(req.getCmdBuffer()))
            return "1";

        byte [] eofflinePinBlock = null;

        VNG rsp = pinInputLoop();

        if (rsp == null || rsp.parseHeader("EPO"))
            return "1";

        char status = (char)rsp.parseByte();

        if (status != '0')
            return "" + status;

        eofflinePinBlock = rsp.parseRSLenData();

        if (eofflinePinBlock == null)
            return "1"; // unexpected result. see as command error

        // Smart Card Device
        // - QSG<RS>{Len}{Plaintext/Encrypted Offline PIN Block}
        // - send encrypted PIN data to Reader for further verification

        req.clear();
        req.addData(eofflinePinBlock);

        rsp = portManager.exchangeData(req);
        // QSF{status}<RS>{LEN}{R-APDU}

        if (rsp != null && rsp.parseHeader("QSF", true, true)) {
            byte [] rpdu = rsp.parseRSLenData();
            return "0" + Utility.bytes2Hex(rpdu);
        }

        return "1";
    }

    // return 1 digit status + 4 digit RPDU status word if success
    private String offlinePin(Params params) {

        // - EP4{Min}{Max}{Timeout}{Data}
        // - Start PIN Process
        // - {Data} = {PK Len}{PK Modulus}{Challenge}{PK Exponent}

        VNG req = new VNG("EP4" + params.config);

        if (params.dataOfEncryptedPin != null)
            req.addRSLenData(params.dataOfEncryptedPin);

        if (!portManager.sendData(req.getCmdBuffer()))
            return "1";

        VNG rsp = pinInputLoop();

        if (rsp == null || !rsp.parseHeader("EP4"))
            return "1";

        return rsp.parseString();
    }

    // return 1 digit status + 16+20 digit EPB+KSN if success
    private String onlinePin(Params params) {

        // PIN Device
        // - EP1{PEK#}{PBF}{Min}{Max}{Timeout}{Account}

        if (!portManager.sendData(("EP1" + params.key + params.format + params.config + params.pan).getBytes()))
            return "1";

        VNG rsp = pinInputLoop();

        // EP1{Status}{PIN Len}{EPB}{KSN}

        if (rsp == null || !rsp.parseHeader("EP1"))
            return "1";

        char status = (char)rsp.parseByte();

        if (status != '0')
            return "" + status;

        String pinLen = rsp.parseString(2);
        String epb = rsp.parseString();
        HelperEMVClass.getTransactionDetails.setEPB(epb.substring(0,16));
        HelperEMVClass.getTransactionDetails.setKSN(epb.substring(16));
        return "0" + epb;
    }

}
