package com.mlt.e200cp.utilities.helper.util;

import android.app.ProgressDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

/**
 * Created by work on 10/03/2019.
 */
public class ISOConstant {
    public static final String TAG = "Tag";
    public static final String CustomerEmail = "EmailAddress";
    public static final String RequestId = "3c805a2f0e0a448eb9c78624a";
    public static final String Language = "Language";
    public static ProgressDialog progressDialog = null;
    public static boolean SUCCESSFLAG = false;
    public static boolean SUCCESSDIALOGFLAG = false;
    public static boolean reversal = false;
    public static boolean calledService;
    public static boolean printReversal = false;
    public static boolean serialPortOpened = false;
    public static boolean payIso;
    public static String shortUUID() {
        char[] chars = "abcdefghijklmnopqrstuvwxyz123456789".toCharArray();
        StringBuilder sb = new StringBuilder(24);
        Random random = new Random();
        for (int i = 0; i < 24; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output = sb.toString();
        System.out.println(output);
        return output;
    }

    //For RMS Services
    public static String GUUID() {
        String uniqueID = UUID.randomUUID().toString();
        return uniqueID;
    }


    public static String currentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy HH:mm:ss", Locale.ENGLISH);
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    //Get Formatted ExpiryDate RTA     09-2021
    public static String getFormattedExpiryDateADJD(String expiryDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.ENGLISH);
        String currentYear = sdf.format(new Date());
        String Century = currentYear.substring(0, 2);
        expiryDate = expiryDate.substring(0, 2) + "-" + Century + expiryDate.substring(2, 4);
        return expiryDate;
    }

    public static String maskCardNumber(String cardNumber, String mask) {

        // format the number
        int index = 0;
        StringBuilder maskedNumber = new StringBuilder();
        for (int i = 0; i < mask.length(); i++) {
            char c = mask.charAt(i);
            if (c == '#') {
                maskedNumber.append(cardNumber.charAt(index));
                index++;
            } else if (c == 'x') {
                maskedNumber.append(c);
                index++;
            } else {
                maskedNumber.append(c);
            }
        }

        // return the masked number
        return maskedNumber.toString();
    }

    public static String RemoveEscapeSequence(String text) {
        //Removing the escape sequences from tshe string
        String a = text.replaceAll("\r\n", "");
        String b = a.replaceAll("\r", "");
        String c = b.replaceAll("\n", "");
        String d = c.replaceAll("\n\r", "");
        String e = d.replaceAll("\t", "");
        String f = e.replaceAll("\f", "");
        String g = f.replaceAll("\b", "");
        return g;
    }

}
