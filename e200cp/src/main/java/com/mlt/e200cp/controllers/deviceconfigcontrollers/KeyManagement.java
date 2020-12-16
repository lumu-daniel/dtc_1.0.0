package com.mlt.e200cp.controllers.deviceconfigcontrollers;

import com.mlt.e200cp.utilities.helper.protocol.VNG;
import com.mlt.e200cp.utilities.helper.shell.BaseShell;

import static com.mlt.e200cp.utilities.helper.ports.PortManager.TARGET_EPP;

/**
 * Created by simon_chen on 2018/6/11.
 */

public class KeyManagement extends BaseShell {

    public static class Key {
        public char kek = 0x00;
        public char slot = 0x00;
        public String ekey  = "";
        public String params = "00TB";  // {Usage}{Alg}{Mode}
        public String method = "DES";   // DES / AES
        public String kcv = "000000";
        public byte [] option = null;
    }


    public static String getKcv(char slot, int... target) {

        portManager.sendTarget = target.length > 0 ? target[0] : TARGET_EPP;

        VNG rsp = new VNG(portManager.exchangeData(("KM1" + slot).getBytes()));

        if (rsp.parseHeader("KM1", true, true))
            return rsp.parseString();

        return "";
    }

    public static boolean loadKey(Key key, int... target) {

        portManager.sendTarget = target.length > 0 ? target[0] : TARGET_EPP;

        VNG req = new VNG();

        // KM3{kek#}{Method}{TK#}{Usage}{Alg}{Mode}
        req.addData("KM3");
        req.addData((byte)key.kek);   // kek - 0
        if(key.method.equals("DES"))
            req.addData("0");   // Method - 0 : TDES CBC (IV=0) decryption method
        else if (key.method.equals("AES"))
            req.addData("3");   // Method - 3 : AES CBC (IV=0) decryption mode
        else
            return false;

        req.addData((byte)key.slot);   // Target TestData - X

        req.addData(key.params);

        // {Multiplier}{Key_Data}{KCV Type}{KCV}
        req.addData(Integer.toString(key.ekey.length() / 16));
        req.addData(key.ekey);
        req.addData("0");
        req.addData(key.kcv);

        if (key.option != null)
            req.addRSLenData(key.option);

        VNG rsp = portManager.exchangeData(req);

        if (rsp != null)
            return rsp.parseHeader("KM3", true, true);
        return false;
    }
}
