package com.mlt.e200cp.models;

import com.mlt.e200cp.controllers.deviceconfigcontrollers.KeyManagement;
import com.mlt.e200cp.utilities.helper.util.Utility;

/**
 * Created by simon_chen on 2018/6/13.
 */

public class TestData {

    public static class TestKey extends KeyManagement.Key {
        public String data = "";
    }

    public static TestKey BKLK = new TestKey();
    public static TestKey DK4PIN = new TestKey();
    public static TestKey DK4P2PE = new TestKey();
    public static TestKey IAK = new TestKey();

    static {

        BKLK.kek = '0';
        BKLK.slot = '0';
        BKLK.data = "30313233343536373839414243444546";
        BKLK.ekey = "494D16FB2860C464850B3D7643861A27";
        BKLK.params = "00TB";
        BKLK.kcv = "33C4CB";

        DK4PIN.kek = '0';
        DK4PIN.slot = 'X';
        DK4PIN.data = "6AC292FAA1315B4D858AB3A3D7D5933A";
        DK4PIN.ekey = "97D706D3BA192114B3DAAA7910E241B7";
        DK4PIN.params = "10TE";
        DK4PIN.option = Utility.hex2Byte("4B53FFFF9876543210E00000");
        DK4PIN.kcv = "AF8C07";

        DK4P2PE.kek = '0';
        DK4P2PE.slot = 'Y';
        DK4P2PE.data = "50515253545556575859616263646566";
        DK4P2PE.ekey = "E45419AD21809F1C3A3EEFB361C4AA41";
        DK4P2PE.params = "34TE";
        DK4P2PE.option = Utility.hex2Byte("4B53FFFF9876543210E00000");
        DK4P2PE.kcv = "67F838";

        IAK.kek = '0';
        IAK.slot = 'L';
        IAK.data = "101112131415161718191A1B1C1D1E1F";
        IAK.ekey = "7497A906FA0088C6064C34A5BCD564D6";
        IAK.params = "00TB";
        IAK.kcv = "FE8A09";
    }
}
