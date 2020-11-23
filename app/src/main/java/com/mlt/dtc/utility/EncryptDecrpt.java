package com.mlt.dtc.utility;


import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import static com.mlt.dtc.common.Common.RemoveEscapeSequence;


/**
 * @author raheel NIPS
 */
public class EncryptDecrpt {


    // Default Constructor
    public EncryptDecrpt() {

    }

    /**
     * Algorithm is used For Encryption of the String*
     *
     * @param sPlainText -> plain text to be encrypted
     * @param sSecretKey -> Key which is used in the encryption
     * @return -> Encrypted Values
     * @throws InvalidKeySpecException
     */
    public static String Encrypt(String sPlainText, String sSecretKey) throws InvalidKeySpecException {
        try {

            // Initiaize the key byte array
            byte[] keyArray;
            byte[] toEncryptArray = sPlainText.getBytes(StandardCharsets.UTF_8);

            // Convert the secret key in to MD5 hashing
            MessageDigest hashMD5 = MessageDigest.getInstance("MD5");
            keyArray = hashMD5.digest(sSecretKey.getBytes(StandardCharsets.UTF_8));

            // Check if the key is less than 16 byte
            if (keyArray.length == 16) {
                byte[] tmpKey = new byte[24];
                System.arraycopy(keyArray, 0, tmpKey, 0, 16);
                System.arraycopy(keyArray, 0, tmpKey, 16, 8);
                keyArray = tmpKey;
            }

            // Make the Cipher here
            Cipher cipher = Cipher.getInstance("DESEDE/ECB/PKCS5Padding");
            SecretKeySpec myKey = new SecretKeySpec(keyArray, "DESede");

            // Initialize the Encryption mode
            cipher.init(Cipher.ENCRYPT_MODE, myKey);

            byte[] cipherText = cipher.doFinal(toEncryptArray);
            String encodedCipherText;

            // Convert the text into base 64 string
            encodedCipherText = Base64.encodeToString(cipherText, Base64.DEFAULT);

            // Return the base 64 encrypted string
            return encodedCipherText;

        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException oException) {

            return oException.toString();

        }
    }

    /**
     * Algorithm is used for the decryption of the text
     *
     * @param sCipherText -> Cipher Text which is the result of the encrypted
     *                    Text
     * @param sSecretKey  -> Same secret key is used in both encryption and
     *                    decryption
     * @return -> Decrypted Text
     */
    public static String Decrypt(String sCipherText, String sSecretKey) {

        try {

            // Initiaize the key byte array
            byte[] keyArray;

            //Convert the byte array into base 64
            byte[] toEncryptArray = Base64.decode(sCipherText, Base64.DEFAULT);
            // MD5 hashing
            byte[] bytesOfMessage = sSecretKey.getBytes(StandardCharsets.UTF_8);

            MessageDigest hashMD5 = MessageDigest.getInstance("MD5");
            keyArray = hashMD5.digest(bytesOfMessage);

            keyArray = hashMD5.digest(sSecretKey.getBytes(StandardCharsets.UTF_8));

            // Check if the key is less than 16 byte
            if (keyArray.length == 16) {
                byte[] tmpKey = new byte[24];
                System.arraycopy(keyArray, 0, tmpKey, 0, 16);
                System.arraycopy(keyArray, 0, tmpKey, 16, 8);
                keyArray = tmpKey;
            }

            // Reset the hash
            hashMD5.reset();

            // Create the DES instance here
            Cipher cipher = Cipher.getInstance("DESEDE/ECB/PKCS5Padding");
            SecretKeySpec myKey = new SecretKeySpec(keyArray, "DESede");

            cipher.init(Cipher.DECRYPT_MODE, myKey);

            //Decrption perform here
            byte[] cipherText = cipher.doFinal(toEncryptArray);
            String encodedCipherText;

            // Change the final byte array to string
            encodedCipherText = new String(cipherText, StandardCharsets.UTF_8);

            // Return the decrypted string
            return encodedCipherText;

            // Exception are there
        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException oException) {

            return oException.toString();
        }
    }

    //RequestId,TimeStamp,TerminalIds(as a secret key)
    public static String GetSecureHashDeviceSerialNumber(String RequestId, String TimeStamp, String DeviceSerialNumber) {
        try {
            String plainText = "";
            String signatureFields = "RequestId,TimeStamp,DeviceSerialNumber";

            String[] SignedFieldArray = signatureFields.split(",");

            plainText = "RequestId=" + RequestId + ",TimeStamp=" + TimeStamp + ",DeviceSerialNumber=" + DeviceSerialNumber;

            String secureHash = Encrypt(plainText, DeviceSerialNumber);

            //Removing the escape sequences from tshe string
            String a = secureHash.replaceAll("\r\n", "");
            String b = a.replaceAll("\r", "");
            String c = b.replaceAll("\n", "");
            String d = c.replaceAll("\n\r", "");
            String e = d.replaceAll("\t", "");
            String f = e.replaceAll("\f", "");
            String g = f.replaceAll("\b", "");


            return g;

        } catch (Exception ex) {
            return "";
        }
    }



    /**
     * To validate the Encryption and Decryption
     *
     * @param text
     * @param key
     * @param compareString
     * @param isEncrypt
     * @return
     * @throws InvalidKeySpecException
     */
    public static boolean validate(String text, String key, String compareString, boolean isEncrypt) throws InvalidKeySpecException {

        // Set to false
        boolean result = false;

        // Check if Encryption validation occurs
        if (isEncrypt) {

            if (compareString.equals(Encrypt(text, key))) {

                result = true;
            }

            // Else Decryption validation occurs
        } else {

            if (compareString.equals(Decrypt(text, key))) {

                result = true;
            }
        }

        return result;
    }

    //RequestId,TimeStamp,MerchantId,SourceApplication,BankId
    public static String GetSecureHash(String RequestId, String TimeStamp, String MerchantId, String SourceApplication,
                                       String BankId, String secretKey) {
        try {
            String plainText = "";
            String signatureFields = "RequestId,TimeStamp,MerchantId,SourceApplication,BankId";

            String[] SignedFieldArray = signatureFields.split(",");

            plainText = "RequestId=" + RequestId + ",TimeStamp=" + TimeStamp + ",MerchantId=" + MerchantId + ",SourceApplication=" + SourceApplication + ",BankId=" + BankId;

            String secureHash = Encrypt(plainText, secretKey);

            return secureHash;

        } catch (Exception ex) {
            return "";
        }
    }

    //RequestId,TimeStamp,TerminalIds(as a secret key)
    public static String GetSecureHashCardPayment(String RequestId, String TimeStamp, String TerminalId) {
        try {
            String plainText = "";
            String signatureFields = "RequestId,TimeStamp,TerminalId";

            String[] SignedFieldArray = signatureFields.split(",");

            plainText = "RequestId=" + RequestId + ",TimeStamp=" + TimeStamp + ",TerminalId=" + TerminalId;

            String secureHash = Encrypt(plainText, TerminalId);

            //Removing the escape sequences from tshe string
            String a = secureHash.replaceAll("\r\n", "");
            String b = a.replaceAll("\r", "");
            String c = b.replaceAll("\n", "");
            String d = c.replaceAll("\n\r", "");
            String e = d.replaceAll("\t", "");
            String f = e.replaceAll("\f", "");
            String g = f.replaceAll("\b", "");


            return g;

        } catch (Exception ex) {
            return "";
        }
    }

    //RequestId,TimeStamp,ReferenceNumber,MerchantId,Amount
    public static String GetSecureHash3DSecure(String RequestId, String TimeStamp, String ReferenceNumber, String MerchantId,
                                               String Amount, String secretKey) {
        try {
            String plainText = "";
            String signatureFields = "RequestId,TimeStamp,ReferenceNumber,MerchantId,Amount";

            String[] SignedFieldArray = signatureFields.split(",");

            plainText = "RequestId=" + RequestId + ",TimeStamp=" + TimeStamp + ",ReferenceNumber=" + ReferenceNumber + ",MerchantId=" + MerchantId + ",Amount=" + Amount;

            String secureHash = Encrypt(plainText, secretKey);
            secureHash = RemoveEscapeSequence(secureHash);

            return secureHash;

        } catch (Exception ex) {
            return "";
        }
    }

    //RequestId,TimeStamp,MerchantId,SourceApplication,BankId
    public static String GetSecureHashUpdatePayment(String RequestId, String TimeStamp, String MerchantId, String SourceApplication,
                                                    String BankId, String secretKey) {
        try {
            String plainText;
            String signatureFields = "RequestId,TimeStamp,MerchantId,SourceApplication,BankId";

            String[] SignedFieldArray = signatureFields.split(",");

            plainText = "RequestId=" + RequestId + ",TimeStamp=" + TimeStamp + ",MerchantId=" + MerchantId + ",SourceApplication=" + SourceApplication + ",BankId=" + BankId;

            String secureHash = Encrypt(plainText, secretKey);

            //Removing the escape sequences from tshe string
            String a = secureHash.replaceAll("\r\n", "");
            String b = a.replaceAll("\r", "");
            String c = b.replaceAll("\n", "");
            String d = c.replaceAll("\n\r", "");
            String e = d.replaceAll("\t", "");
            String f = e.replaceAll("\f", "");
            String g = f.replaceAll("\b", "");

            return g;

        } catch (Exception ex) {
            return "";
        }
    }

}











