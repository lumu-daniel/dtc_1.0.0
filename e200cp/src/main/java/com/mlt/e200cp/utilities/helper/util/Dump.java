package com.mlt.e200cp.utilities.helper.util;

import com.mlt.e200cp.utilities.helper.listener.PrintLineCallback;
import com.mlt.e200cp.utilities.helper.protocol.EMV;
import com.mlt.e200cp.utilities.helper.protocol.TLV;
import com.mlt.e200cp.utilities.helper.shell.EmvL2;

public class Dump {

    public static PrintLineCallback callback = null;

    public static void pin(byte pinType, byte pinTryCounter) {

        if (callback == null) return;

        if (pinType == 1) {
            callback.run("Online PIN");
        } else if (pinType == 2) {
            callback.run("Offline Plaintext PIN");
        } else if (pinType == 3) {
            callback.run("Offline Encrypted PIN");
        }

        if (pinTryCounter > 0) {
            callback.run("PIN Try Counter : " + pinTryCounter);
        }
    }

    public static void tlv(TLV tlv) {

        if (tlv.index == 0 || callback == null)
            return;

        int parseIndex = 0;
        while(true) {
            TLV.ENTRY item = tlv.parseTLV(parseIndex);
            if (item == null)
                return;
            callback.run("<" + Integer.toHexString(item.tag) + "> " + Utility.bytes2Hex(item.value));
            parseIndex += item.itemLen;
        }
    }

    public static void emvInfo() {

        // Kernel version
        EMV req = new EMV((byte) EMV.CMD_ID._31_EmvGetKernelVersion, (byte)EMV.CMD_TYPE.REQUEST, (byte)0, (byte)0);
        EMV rsp = EmvL2.exchange(req);
        if (!rsp.isSuccess)
            return;

        callback.run("Kernel version : " + Utility.bytes2Hex(rsp.dataField.toByteArray()));

        // API version
        req = new EMV((byte)EMV.CMD_ID._30_EmvGetApiVersion, (byte)EMV.CMD_TYPE.REQUEST, (byte)0, (byte)0);
        rsp = EmvL2.exchange(req);
        if (!rsp.isSuccess)
            return;

        callback.run("API version : " + Utility.bytes2Hex(rsp.dataField.toByteArray()));

        // Checksum version of profile
        // YM<RS>{Len}{0x35}C{P1:0x00}{P2:0x00}{Len:0x0000}
        //        P1:Length of Checksum
        //           0x00~0x06 : 6 bytes SHA1 checksum
        //           others : full 20 bytes SHA1 checksum
        //        P2:Checksum Item Flag
        //           bit-1 : return ICS profile checksum
        //           bit-2 : return terminal profile checksum
        //           bit-3 : return application profile checksum
        //           bit-4 : return CAPK profile checksum

        req = new EMV((byte)EMV.CMD_ID._35_EmvGetCurrentCheckSum, (byte)EMV.CMD_TYPE.REQUEST, (byte)0, (byte)0xF);
        rsp = EmvL2.exchange(req);
        if (!rsp.isSuccess)
            return;

        byte [] checksum_data = rsp.dataField.toByteArray();
        if (checksum_data.length != 24) {
            callback.run("Invalid Response Data Format");
            return;
        }

        callback.run("ICS checksum : " + Utility.bytes2Hex(checksum_data, 0, 6));
        callback.run("Terminal checksum : " + Utility.bytes2Hex(checksum_data, 6, 6));
        callback.run("Application checksum : " + Utility.bytes2Hex(checksum_data, 12, 6));
        callback.run("CAPK checksum : " + Utility.bytes2Hex(checksum_data, 18, 6));

        req = new EMV((byte)EMV.CMD_ID._B1_EmvGetApConfiguration, (byte)EMV.CMD_TYPE.REQUEST, (byte)0, (byte)0);
        rsp = EmvL2.exchange(req);
        if (!rsp.isSuccess)
            return;

        EMV.APP_CONFIG appConfig = new EMV.APP_CONFIG(rsp.dataField.toByteArray());
        callback.run(appConfig.toString());
    }

}
