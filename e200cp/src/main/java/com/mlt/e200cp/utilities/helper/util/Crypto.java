package com.mlt.e200cp.utilities.helper.util;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.params.KeyParameter;

import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.mlt.e200cp.utilities.helper.util.Utility.appendLog;

public class Crypto {

    public enum ALGORITHM {
        TDES, AES,
    }

    private static byte[] crypto(ALGORITHM alg, String method, boolean toEncrypt, byte[] data, byte[] key, byte[] iv) {

        SecretKeySpec skeySpec;
        Cipher cipher;
        IvParameterSpec ivspec;

        if (iv == null) {
            if (alg == ALGORITHM.TDES)
                iv = new byte[8];
            else
                iv = new byte[16];
        }
        ivspec = new IvParameterSpec(iv);

        try {
            if (alg == ALGORITHM.TDES) {
                cipher = Cipher.getInstance("DESede/" + method + "/NoPadding");
                skeySpec = new SecretKeySpec(key, "DESede");
            } else if (alg == ALGORITHM.AES) {
                cipher = Cipher.getInstance("AES/" + method + "/NoPadding");
                skeySpec = new SecretKeySpec(key, "AES");
            } else
                return null;

            int mode = toEncrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE;

            if (method.equals("ECB"))
                cipher.init(mode, skeySpec);
            else
                cipher.init(mode, skeySpec, ivspec);

            return cipher.doFinal(data);

        } catch (Exception e) {
            e.printStackTrace();
            appendLog(e.getLocalizedMessage());
        }

        return null;
    }

    public static byte [] getKcv(ALGORITHM alg, byte [] key) {

        if (alg == ALGORITHM.TDES) {
            byte [] kcv = encrypt(alg, "ECB", new byte[8] , key);
            if (kcv != null && kcv.length == 8)
                return Arrays.copyOfRange(kcv, 0, 3);
        } else if (alg == ALGORITHM.AES) {
            byte [] kcv = aesCmac(new byte[16], key);
            if (kcv != null && kcv.length >= 8)
                return Arrays.copyOfRange(kcv, 0, 3);
        }

        return new byte[3];
    }

    /**
     * AES [FIPS 197] SHALL be used in CMAC-mode [SP 800-38B] with a MAC length of 8 bytes.
     *
     * @param data the data to MAC
     * @param key the key to use
     * @return the 8 byte MAC of the data
     */
    public static byte[] aesCmac(byte[] data, byte[] key) {

        // mac size in bits (64 bits = 8 bytes)
        final Mac cbc8 = new CMac(new AESEngine(), 64);
        CipherParameters params = new KeyParameter(key);
        cbc8.init(params);

        byte[] result = new byte[8];
        cbc8.update(data, 0, data.length);
        cbc8.doFinal(result, 0);

        return result;
    }


    public static String getKcv(ALGORITHM alg, String key) {
        byte [] kcv = getKcv(alg, Utility.hex2Byte(key));
        if (kcv != null)
            return Utility.bytes2Hex(kcv);
        return "000000";
    }

    public static String encrypt(ALGORITHM alg, String method, String data, String key) {
        return encrypt(alg, method, data, key, null);
    }


    public static String encrypt(ALGORITHM alg, String method, String data, String key, String iv) {
        byte [] ret = encrypt(alg, method, Utility.hex2Byte(data), Utility.hex2Byte(key), Utility.hex2Byte(iv));
        if (ret == null)
            return null;
        else
            return Utility.bytes2Hex(ret);
    }

    public static byte[] encrypt(ALGORITHM alg, String method, byte[] data, byte[] key) {
        return crypto(alg, method, true, data, key, null);
    }

    public static byte[] encrypt(ALGORITHM alg, String method, byte[] data, byte[] key, byte[] iv) {
        return crypto(alg, method, true, data, key, iv);
    }

    public static String decryt(ALGORITHM alg, String method, String data, String key) {
        return encrypt(alg, method, data, key, null);
    }


    public static String decryt(ALGORITHM alg, String method, String data, String key, String iv) {
        byte [] ret = encrypt(alg, method, Utility.hex2Byte(data), Utility.hex2Byte(key), Utility.hex2Byte(iv));
        if (ret == null)
            return null;
        else
            return Utility.bytes2Hex(ret);
    }

    public static byte[] decryt(ALGORITHM alg, String method, byte[] data, byte[] key) {
        return crypto(alg, method, false, data, key, null);
    }

    public static byte[] decryt(ALGORITHM alg, String method, byte[] data, byte[] key, byte[] iv) {
        return crypto(alg, method, false, data, key, iv);
    }

    public static byte[] cmac() {
        // not implement yet
        return null;
    }

    public static byte[] digestSHA256(byte [] data, int len) {
        MessageDigest shaCode = null;
        try {
            shaCode = MessageDigest.getInstance("SHA-256");
            shaCode.update(data, 0, len);
            return shaCode.digest();
        } catch (Exception e) {
            e.printStackTrace();
            appendLog(e.getLocalizedMessage());
        }
        return null;
    }
}
