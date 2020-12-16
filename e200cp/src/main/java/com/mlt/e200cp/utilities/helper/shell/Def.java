package com.mlt.e200cp.utilities.helper.shell;

public class Def {

    /*
        ‘4’ = XAC Method 4 (AES)
        ‘5’ = XAC Method 5 (Challenge)
     */
    public static char PASSWORD_INSTALL_METHOD = '3';
    public static char AES_MA_METHOD = '4';
    public static char CHALLENGE_INSTALL_METHOD = '5';

    public static char HOST_IAK_SLOT = 'L';

    private static byte ID_T305 = 1;
    private static byte ID_P95 = 2;

    public static class Config {
        public char MAK_SLOT = 0x00;
        public char IAK_SLOT = 0x00;
        public char OFFPIN_SLOT = 0x00;
    }

    public static Config t305 = new Config();
    public static Config p95 = new Config();

    public static Config configs [] = { t305, p95 };
    public static byte ids [] = { ID_T305, ID_P95 };

    public static boolean forceEncryption = false;

    public static boolean requiredEncryption(byte [] data) {

        if (data != null && data.length >= 2) {
            // QM (card status), QR (NFC), QS (chip L1), Q1* (MSR)
            // YM (chip L2)
            // CR* (crypto)
            if (data[0] == (byte)'Q' || data[0] == (byte)'Y' || data[0] == (byte)'C' || forceEncryption)
                return true;
        }

        return false;
    }

    static {

        t305.MAK_SLOT = '6';
        t305.IAK_SLOT = '7';
        t305.OFFPIN_SLOT = '5';

        p95.MAK_SLOT = 'K';
        p95.IAK_SLOT = 'L';
        p95.OFFPIN_SLOT = '5';
    }

}
