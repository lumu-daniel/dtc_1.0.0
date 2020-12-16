package com.mlt.e200cp.utilities.helper.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mlt.e200cp.models.GetTransactionDetails;
import com.mlt.e200cp.models.enums.EmvTransactionType;
import com.mlt.e200cp.models.response.ISOPaymentResponse;
import com.mlt.e200cp.utilities.FetchDeviceSerialNumber.ULTests;

import org.bouncycastle.util.encoders.Hex;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;
import saioapi.base.Misc;

public class Utility {


    public static final String SIGNATURE_FIELDS_DEVICE_SERIAL_NUMBER = "RequestId,TimeStamp,DeviceSerialNumber";
    public static final String SIGNATURE_FIELDS_CARD_PAYMENT = "RequestId,TimeStamp,TerminalId";
    public static String CHIPTYPE = "";
    public static EmvTransactionType txn_type;
    public static final Integer CVMReqLimit = 10000;
    public static  String recieptNumber;

    public static byte[] toByteArray(List<Byte> in) {
        final int n = in.size();
        byte ret[] = new byte[n];
        for (int i = 0; i < n; i++) {
            ret[i] = in.get(i);
        }
        return ret;
    }

    // Convert Hex String to Byte Array
    public static byte[] hex2Byte(String str)
    {
        try {
            str = str.replace(" ", "");

            byte[] bytes = new byte[str.length() / 2];
            for (int i = 0; i < bytes.length; i++)
            {
                bytes[i] = (byte) Integer
                        .parseInt(str.substring(2 * i, 2 * i + 2), 16);
            }
            return bytes;
        }catch (Exception e){
            appendLog(e.getLocalizedMessage());
            e.printStackTrace();

            return null;
        }
    }

    public static String bytes2Hex(byte[] b, int offset, int length)
    {
        return bytes2Hex(Arrays.copyOfRange(b, offset, offset + length));
    }

    public static String bytes2Hex(byte[] b, int length)
    {
        return bytes2Hex(Arrays.copyOfRange(b, 0, length));
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String byte2Hex(byte b)
    {
        return bytes2Hex(new byte [] {b});
    }

    // Convert Byte Arrary to Hex String
    public static String bytes2Hex(byte[] bytes)
    {
        char [] hexChars = new char[bytes.length * 2];
        for(int j = 0 ; j < bytes.length ; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String byte2String(byte [] data) {
        for(int i = 0 ; i < data.length ; i++) {
            if (data[i] == 0)
                return new String(data).substring(0, i);
        }
        return new String(data);
    }

    public static String numeric2string(byte [] data)
    {
        String ret = "";
        for(int i = 0; i < data.length; i++)
        {
            byte hb = (byte)((data[i] & 0xF0) >> 4);
            byte lb = (byte)(data[i] & 0x0F);

            if (hb >= 0 && hb <= 9)
                ret += Byte.toString(hb);
            else if (hb == 0xF)
                ret += 'F';
            else
                break;

            if (lb >= 0 && lb <= 9)
                ret += Byte.toString(lb);
            else if (hb == 0xF)
                ret += 'F';
            else
                break;
        }
        return ret;
    }

    private static void putFileToMediaStore(Context ctx, File file) {
        try {
            new MediaScannerConnection.MediaScannerConnectionClient() {
                private MediaScannerConnection mMs;

                public void init() {
                    mMs = new MediaScannerConnection(ctx, this);
                    mMs.connect();
                }

                @Override
                public void onMediaScannerConnected() {

                    File pathFile = Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                    mMs.scanFile(file.getAbsolutePath(), null); // <-- repeat for all files

                    mMs.disconnect();
                }

                @Override
                public void onScanCompleted(String path, Uri uri) {
                }

            }.init();
        } catch(Exception e) {
        }
    }

    private static String getLogDirectory() {
        if (new File("/storage/udisk/Download/").isDirectory())
            return "/storage/udisk/Download/";
        else
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
    }

    private static String getLogPath(String logName, boolean hasTime) {
        String path = getLogDirectory() + logName;
        if (hasTime)
            path += android.text.format.DateFormat.format(" yyyy-MM-dd_hhmmss.txt", new java.util.Date()).toString();
        else
            path += ".txt";
        return path;
    }

    public static String saveLogToFile(Context ctx, String logName, String log) {

        String logPath = getLogPath(logName, true);

        try {
            File logFile = new File(logPath);
            FileWriter writer = new FileWriter(logFile, false);
            writer.append(log);
            writer.flush();
            writer.close();
            putFileToMediaStore(ctx, logFile);
        } catch (IOException e) {
            e.printStackTrace();
            appendLog(e.getLocalizedMessage());
            return null;
        }
        return logPath;
    }

    public static byte [] concateArray(byte [] array1, int len1, byte [] array2, int len2) {
        byte [] newArray = new byte[len1 + len2];
        System.arraycopy(array1, 0, newArray, 0, len1);
        System.arraycopy(array2, 0, newArray, len1, len2);
        return newArray;
    }

    public static byte [] xorArray(byte [] array1, byte [] array2, int len) {

        byte [] xorArray = new byte[len];

        for(int i = 0 ; i < len ; i++) {
            xorArray[i] = (byte)(array1[i]^array2[i]);
        }
        return xorArray;
    }


    public static byte [] long2Bytes(long v) {
        byte [] data = new byte[8];
        for (int i = 7; i >= 0; i--)
        {
            data[i] = (byte)(v & 0xFF);
            v = v >> 8;
        }
        return data;
    }

    public static long bytes2Long(byte[] v)
    {
        long k = 0;

        for (int i = 0; i < 8; i++)
        {
            k = (k << 8) + (v[i] & 0xFF);
        }
        return k;
    }

    public static String repeat(String val, int count){
        StringBuilder buf = new StringBuilder(val.length() * count);
        while (count-- > 0) {
            buf.append(val);
        }
        return buf.toString();
    }

    public static void copyAssets(Context ctx, String src, String des) {

        AssetManager assetManager = ctx.getAssets();
        InputStream in = null;
        OutputStream out = null;

        try {

            in = assetManager.open(src);
            File outFile = new File(des);
            out = new FileOutputStream(outFile);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;

        } catch(IOException e) {
            Log.e("tag", "Failed to copy asset file: " + src, e);
        }
    }

    public static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    public static int count(String text, String find) {
        int index = 0, count = 0, length = find.length();
        while( (index = text.indexOf(find, index)) != -1 ) {
            index += length; count++;
        }
        return count;
    }

    public static String hex2string(String h )
    {
        byte[] bytes = Hex.decode(h);
        try {
            String dav = new String(bytes, "UTF-8");
            return (dav);

        }catch (UnsupportedEncodingException e)
        {
            appendLog(e.getLocalizedMessage());
            e.getMessage();
        };
        return null;
    }

    public static String getExpiryDateOnCard(String expirydate) {
        expirydate = expirydate.trim();
        expirydate = expirydate.substring(0, 4);
        String expirydateStart = expirydate.substring(0, 2);
        String expirydateEnd = expirydate.substring(2, 4);
        expirydate = expirydateEnd +"-20"+ expirydateStart;
        return expirydate;
    }

    public static String getTranasctionDateAndTime(){
        //This time format is for ADJD Services
        TimeZone tz = TimeZone.getTimeZone("GMT+4");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());
//        String nowAsISO = format(new Date());
        return nowAsISO;
    }

    public static String processDate(String date, String type){

        //2019-10-03 06:35:50
        date =  date.replace("-","").replace(":","").replace(" ","");
        if(type.equals("date")){
            return date.substring(4,8);
        }else if(type.equals("time")){
            return date.substring(8,date.length());
        }else{
            return null;
        }

    }

    public static String getProductName(Context context) {

        byte [] buf = new byte[32];

        if (new Misc().getSystemInfo(Misc.INFO_PRODUCT, buf) == 0)
            ULTests.getInstance(context).product = Utility.byte2String(buf);

        return ULTests.getInstance(context).product;
    }

    public static JSONObject convertXMLtoJSON(Object xml){
        if(xml!=null){
            XmlToJson xmlToJson = new XmlToJson.Builder((String) xml).build();
            return xmlToJson.toJson();
        }else{
            return null;
        }

    }

    public static void setTransactionType(TextView txn_flag){
        switch (txn_type){
            case REFUND_TRANSACTION:
                txn_flag.setText("REFUND");
                break;
            case PURCHASE_TRANSACTION:
                txn_flag.setText("PURCHASE");
                break;
            case VOID_REFUND_TRANSACTION:
                txn_flag.setText("VOID REFUND");
                break;
            case VOID_PURCHASE_TRANSACTION:
                txn_flag.setText("VOID PURCHASE");
                break;
            case REPRINT_RECEIPT:
                txn_flag.setText("REPRINT RECEIPT");
                break;
        }
    }

    public static boolean checkExpiry(String expiry){
        String time = getTranasctionDateAndTime().replace("T"," ").replace("Z"," ");
        time = time.split("-")[0]+"-"+time.split("-")[0];
        if(Integer.parseInt(time.split("-")[0])>Integer.parseInt(expiry.split("-")[0])){
            return false;
        }else if(Integer.parseInt(time.split("-")[0])==Integer.parseInt(expiry.split("-")[0])){
            if(Integer.parseInt(time.split("-")[1])>Integer.parseInt(expiry.split("-")[1])){
                return false;
            }else {
                return true;
            }
        }else{
            return true;
        }
    }

    // 06-2026
    public static String reFormatExpiry(String expiry) {
        StringBuilder sb = new StringBuilder(expiry);
        sb.replace(2,5 ,"");
        return sb.toString();

    }

    public static HashMap<String, Object> setVariables(JSONObject obj, GetTransactionDetails getTransactionDetails){
        HashMap<String, Object> data = new HashMap();
        ISOPaymentResponse response = new Gson().fromJson(obj.toString(),ISOPaymentResponse.class);
        data.put("ISOPaymentResponse",response);
        Log.e("Response command",response.getErrorDescription());
        getTransactionDetails.setIssuerAuthorisationData(response.getIAD());
        getTransactionDetails.setApprovalCode(response.getTransactionDetailsData().getApprovalCode());
        getTransactionDetails.setCardType(response.getTransactionDetailsData().getCardType());
        String merchantReciept = response.getReceiptMerchantCopy();
        String customerReciept = response.getReceiptCustomerCopy();
        String IssuerScriptingData71 = response.getIssuerScriptingData71();
        String IssuerScriptingData72 = response.getIssuerScriptingData72();
        getTransactionDetails.setReceiptMerchantCopy(merchantReciept);
        getTransactionDetails.setReceiptCustomerCopy(customerReciept);
        getTransactionDetails.setIssuerScriptingData71(IssuerScriptingData71);
        getTransactionDetails.setIssuerScriptingData72(IssuerScriptingData72);
        data.put("GetTransactionDetails",getTransactionDetails);
        if(data.size()<2){
            return null;
        }else{
            return data;
        }
    }

    public static String getUUID() {
        //get Randomly generated value
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static void appendLog(String text )
    {
        int level = 3;
        StackTraceElement[] traces;
        traces = Thread.currentThread().getStackTrace();
        String fullClassName = traces[level].getClassName();

        text = fullClassName.substring(fullClassName.lastIndexOf(".") + 1) + "." + traces[level].getMethodName()  + "[" + traces[level] + "]" + text;
        File logFile = new File("/sdcard/DCIM/log.file");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
