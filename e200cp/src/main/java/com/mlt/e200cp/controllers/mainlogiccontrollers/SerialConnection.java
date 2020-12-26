package com.mlt.e200cp.controllers.mainlogiccontrollers;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.mlt.e200cp.interfaces.ResultsCallback;
import com.mlt.e200cp.interfaces.SerialCallback;

import saioapi.comm.v2.ComManager;
import saioapi.comm.v2.OnComEventListener;

import static android.content.ContentValues.TAG;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_ACCEPTOR_LIGHT;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_ACHIEVED;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_ACK;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_ALL_DEV_STATUS;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_CANCELLED_ACCEPTANCE;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_CANCELLED_NFC;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_CANCEL_ACCEPTANCE_FAILED;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_CASH_ACCEPTED;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_CASH_DEV_INIT;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_CASH_DEV_STATUS;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_CASH_DISPENSED;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_CITGETSUMMARY;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_CITRESETCARDS;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_CITRESETCASH;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_CITUPDATE;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_EID_CANCEL;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_EID_DATA;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_EID_INSERTED;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_EID_STATUS;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_EVOLIS_PRINT_JOB;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_EVOLIS_READY;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_FSE_CARD_DETAILS;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_GET_POS_STATUS;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_GET_TERM_DET;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_NFC_DEV_STATUS;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_START_TXN;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_STICKER_PRINT_JOB;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_STICKER_STATUS;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_TRANS_LOG;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_UPDATESESSIONVARIABLES;
import static com.mlt.e200cp.models.StringConstants.STATUS_NOT_READY;
import static com.mlt.e200cp.models.StringConstants.STATUS_READY;
import static com.mlt.e200cp.models.StringConstants.STATUS_TRUE;
import static com.mlt.e200cp.utilities.helper.util.ISOConstant.serialPortOpened;
import static com.mlt.e200cp.utilities.helper.util.Logger.LINE_OUT;
import static com.mlt.e200cp.utilities.helper.util.Logger.log;
import static com.mlt.e200cp.utilities.helper.util.Utility.appendLog;

public class SerialConnection {

    private Context context=null;
    private static ComManager comManager=null;
    private TextView textView = null;
    private static SerialConnection serialConnection;
    private Boolean isSent = false;

    private SerialConnection(Context context){
        this.context = context;
        comManager = new ComManager(context);
    }

    public static SerialConnection getInstanceSerial(Context context){
        if(serialConnection !=null){
            if(comManager!=null){
                comManager.close();
            }
            serialConnection=null;
        }
        serialConnection = new SerialConnection(context);
        return serialConnection;
    }
    String intentData = "";
    String commandData = "";
    String jsonObjData = "";
    private OnComEventListener getListner(final ResultsCallback callback) {
        return new OnComEventListener() {
            @Override
            public void onEvent(int event) {
                try{

                    byte [] eventbuffer = new byte[1024];
                    Thread.sleep(40);
                    int read = comManager.read(eventbuffer,eventbuffer.length,0);
                    if(read>0){
                        intentData = new String(eventbuffer,0,read).trim();
                        if(intentData.contains("starttransaction")){
//                            Log.e(TAG, "onEvent:12323232 "+intentData );
                        }
//                        Log.e(TAG, "onEvent:1 "+intentData );
                        if(intentData!=null){
//                            Log.e(TAG,intentData);
                            intentData = intentData.toLowerCase();
                            if(intentData.toLowerCase().startsWith("response")){
                                commandData = intentData.toLowerCase().split("-")[1];
                                if(commandData.toLowerCase().equalsIgnoreCase(RESPONSE_CASH_DEV_INIT)){
//                                Log.e(TAG(context), "onEvent: "+intentData);
                                    log("onEvent"+intentData,LINE_OUT());
                                    callback.onResponseSuccess(RESPONSE_CASH_DEV_INIT);
//                                    Log.e(TAG, "onEvent:2 "+intentData );
                                    intentData = "";
                                }
                                else if(commandData.toLowerCase().equals(RESPONSE_CASH_DEV_STATUS)){//cashdevice-ready
                                    callback.onResponseSuccess(intentData);
//                                    Log.e(TAG, "onEvent:3 "+intentData );
                                    intentData = "";
                                }
                                else if(commandData.toLowerCase().contains(RESPONSE_ACK)){//"ack"
                                    isSent = true;
//                                    Log.e(TAG, "onEvent:4 "+intentData );
                                    intentData = "";
                                }
                                else if(commandData.toLowerCase().equalsIgnoreCase(RESPONSE_CANCELLED_ACCEPTANCE)){//"cancelled acceptance"
                                    callback.onResponseSuccess(RESPONSE_CANCELLED_ACCEPTANCE);
//                                    Log.e(TAG, "onEvent:5 "+intentData );
                                    intentData = "";
                                }
                                else if(commandData.toLowerCase().equalsIgnoreCase(RESPONSE_STICKER_PRINT_JOB)){
                                    callback.onResponseSuccess(intentData);
//                                    Log.e(TAG, "onEvent:6 "+intentData );
                                    intentData = "";
                                }
                                else if(commandData.toLowerCase().equalsIgnoreCase(RESPONSE_STICKER_STATUS)){
                                    callback.onResponseSuccess(intentData);
//                                    Log.e(TAG, "onEvent:7 "+intentData );
                                    intentData = "";
                                }
                                else if(commandData.equalsIgnoreCase(RESPONSE_CANCEL_ACCEPTANCE_FAILED)){//"not cancelled acceptance"
                                    callback.onResponseSuccess(RESPONSE_CANCEL_ACCEPTANCE_FAILED);
//                                    Log.e(TAG, "onEvent:8 "+intentData );
                                    intentData = "";
                                }
                                else if(commandData.toLowerCase().equalsIgnoreCase(RESPONSE_EVOLIS_READY)){
                                    if(intentData.split("-")[2].equalsIgnoreCase(STATUS_READY)){
                                        callback.onResponseSuccess(RESPONSE_EVOLIS_READY);
                                    }else {
                                        callback.onResponseFailure("Evolis printer not ready.");
                                    }
//                                    Log.e(TAG, "onEvent:9 "+intentData );
                                    intentData = "";
                                }
                                else if(commandData.toLowerCase().equalsIgnoreCase(RESPONSE_EID_STATUS)){
                                    if(intentData.split("-")[2].equals(STATUS_NOT_READY)){
                                        callback.onResponseFailure(intentData);
                                    }else if(intentData.split("-")[2].equals(STATUS_READY)){
                                        callback.onResponseSuccess(intentData);
                                    }else{
                                        callback.onResponseFailure(intentData);
                                    }
//                                    Log.e(TAG, "onEvent:10 "+intentData );
                                    intentData = "";
                                }
                                else if(commandData.toLowerCase().equalsIgnoreCase(RESPONSE_ACHIEVED)){
                                    callback.onResponseSuccess(intentData);
//                                    Log.e(TAG, "onEvent:11 "+intentData );
                                    intentData = "";
                                }
                                else if(commandData.equalsIgnoreCase(RESPONSE_EID_DATA)){
                                    jsonObjData +=intentData;
//                                    Log.e(TAG, "onEvent:12 "+intentData );
                                    intentData = "";
                                }
                                else if(commandData.toLowerCase().equalsIgnoreCase(RESPONSE_EID_CANCEL)){
                                    callback.onResponseSuccess(intentData);
//                                    Log.e(TAG, "onEvent:13 "+intentData );
                                    intentData = "";
                                }
                                else if(commandData.toLowerCase().equalsIgnoreCase(RESPONSE_CASH_ACCEPTED)){
                                    callback.onResponseSuccess(intentData);
//                                    Log.e(TAG, "onEvent:14 "+intentData );
                                    intentData = "";
                                }
                                else if(commandData.toLowerCase().equalsIgnoreCase(RESPONSE_CASH_DISPENSED)){
                                    callback.onResponseSuccess(intentData);
//                                    Log.e(TAG, "onEvent:15 "+intentData );
                                    intentData = "";
                                }
                                else if(intentData.contains(RESPONSE_EID_INSERTED)){
                                    callback.onResponseSuccess(intentData);
//                                    Log.e(TAG, "onEvent:16 "+intentData );
                                    intentData = "";
                                }
                                else if(commandData.toLowerCase().equalsIgnoreCase(RESPONSE_EVOLIS_PRINT_JOB)){
                                    callback.onResponseSuccess(intentData);
//                                    Log.e(TAG, "onEvent:17 "+intentData );
                                    intentData = "";
                                }
                                else if(commandData.toLowerCase().equalsIgnoreCase(RESPONSE_ACCEPTOR_LIGHT)){
                                    callback.onResponseSuccess(intentData);
//                                    Log.e(TAG, "onEvent:18 "+intentData );
                                    intentData = "";
                                }
                                else if(commandData.toLowerCase().equalsIgnoreCase(RESPONSE_ALL_DEV_STATUS)){
                                    callback.onResponseSuccess(intentData);
//                                    Log.e(TAG, "onEvent:19 "+intentData );
                                    intentData = "";
                                }
                                else if(commandData.toLowerCase().equalsIgnoreCase(RESPONSE_TRANS_LOG)){
                                    if(intentData.toLowerCase().contains(STATUS_TRUE)){
                                        callback.onResponseSuccess(intentData);
                                    }else{
                                        callback.onResponseFailure("Logging Failed.");
                                    }
//                                    Log.e(TAG, "onEvent:20 "+intentData );
                                    intentData = "";
                                }
                                else if(commandData.toLowerCase().equalsIgnoreCase(RESPONSE_NFC_DEV_STATUS)){
                                    if(intentData.toLowerCase().split("-")[2].equals(STATUS_NOT_READY)){
                                        callback.onResponseFailure(intentData);
                                    }else if(intentData.toLowerCase().split("-")[2].equals(STATUS_READY)){
                                        callback.onResponseSuccess(intentData);
                                    }else{
                                        callback.onResponseFailure(intentData);
                                    }
//                                    Log.e(TAG, "onEvent:21 "+intentData );
                                    intentData="";
                                }
                                else if(commandData.toLowerCase().equalsIgnoreCase(RESPONSE_FSE_CARD_DETAILS)){
                                    if(intentData.split("-")[2].equalsIgnoreCase("Failed")){
                                        callback.onResponseSuccess(intentData);
                                    }else{
                                        if(intentData.contains("endofjsonparameters")){
                                            callback.onResponseSuccess(intentData);
                                        }else{
                                            jsonObjData+=intentData;
                                        }
                                    }
//                                    Log.e(TAG, "onEvent:22 "+intentData );
                                    intentData="";
                                }
                                else if(commandData.toLowerCase().equalsIgnoreCase(RESPONSE_CANCELLED_NFC)){
                                    callback.onResponseSuccess(intentData);
//                                    Log.e(TAG, "onEvent:23 "+intentData );
                                    intentData="";
                                }
                                else if(commandData.toLowerCase().equalsIgnoreCase(RESPONSE_CITGETSUMMARY)){
                                    callback.onResponseSuccess(intentData);
//                                    Log.e(TAG, "onEvent:24 "+intentData );
                                    intentData = "";
                                }
                                else if(commandData.equalsIgnoreCase(RESPONSE_CITRESETCARDS)){
                                    callback.onResponseSuccess(intentData);
//                                    Log.e(TAG, "onEvent:25 "+intentData );
                                    intentData="";
                                }
                                else if(commandData.equalsIgnoreCase(RESPONSE_CITRESETCASH)){
                                    callback.onResponseSuccess(intentData);
                                    intentData="";
                                }else if(commandData.equalsIgnoreCase(RESPONSE_CITUPDATE)){
                                    if(intentData.split("-")[2].equalsIgnoreCase("cash")){
                                        callback.onResponseSuccess(intentData);
                                    }else{
                                        callback.onResponseSuccess(intentData);
                                    }
//                                    Log.e(TAG, "onEvent:26 "+intentData );
                                    intentData="";
                                }else if(commandData.equalsIgnoreCase(RESPONSE_UPDATESESSIONVARIABLES)){
                                    intentData = intentData.toLowerCase();
                                    if(intentData.contains(STATUS_TRUE)){
                                        callback.onResponseSuccess(intentData);
                                    }else{
                                        callback.onResponseFailure("transaction not initialized.");
                                    }
                                }
                                else if(commandData.equalsIgnoreCase(RESPONSE_GET_POS_STATUS)){
                                    callback.onResponseSuccess(intentData);
                                }
                                else if(commandData.equalsIgnoreCase(RESPONSE_START_TXN)){
                                    if(intentData.endsWith("endofjsonparameters")){
                                        callback.onResponseSuccess(intentData);
                                    }else{
                                        jsonObjData +=intentData;
                                        intentData = "";
                                    }

                                }
                                else if(commandData.equalsIgnoreCase(RESPONSE_GET_TERM_DET)){
                                    callback.onResponseSuccess(intentData);
                                }
                                else{
                                    callback.onResponseFailure("Wrong response");
//                                    Log.e(TAG, "onEvent:27 "+intentData );
                                    intentData = "";
                                }
                            }
                            else{
                                if(!intentData.contains("endofjsonparameters")){
                                    jsonObjData +=intentData;
                                    intentData = "";
                                }
                                else if(intentData.contains("endofjsonparameters")){
                                    if(jsonObjData.toLowerCase().contains("response")){
                                        jsonObjData +=intentData;
                                        log("Passed"+jsonObjData,LINE_OUT());
                                        callback.onResponseSuccess(jsonObjData);
                                    }else{
//                                        Log.e(TAG, "onEvent: fialed case "+jsonObjData+"fail" );
                                    }
                                    jsonObjData = "";
                                    intentData = "";
                                }
                                else{
                                    log("onEvent: "+intentData+"Fail2",LINE_OUT());
                                }
                            }
                        }
                    }



                }catch(Exception ex){
                    ex.printStackTrace();
                    appendLog(ex.getLocalizedMessage());
                    Log.e("CARDDE", "onSuccess: "+ex.getLocalizedMessage() );
                }
            }
        };
    }

    public boolean openComPort() {
        if (comManager.isOpened()) {
            return true;
        }

        if (comManager.open(comManager.DEVICE_COM0) != ComManager.ERR_OPERATION) {
            return true;
        } else {
            return false;
        }

    }

    /*Reads from the serial port
     * And puts the values into the string intentData
     * It must contain the transactionId and Amount*/
    public void readBytesFromSerial(final SerialCallback callback){
        if(serialPortOpened){
            if (openComPort()){
                if(comManager.connect(0)==0){
                    comManager.setOnComEventListener(getListner(new ResultsCallback() {
                        @Override
                        public String onResponseSuccess(String data) {
                            callback.success(data);
                            return null;
                        }

                        @Override
                        public String onResponseFailure(String t) {
                            callback.failure(t);
                            return null;
                        }
                    }));
                }
            }
        }
    }


    int flag = 0;
    public synchronized int sendCommand(final String details) {

        try {
            if(sendData(details)==0){
                Thread.sleep(100);
                if(isSent){
                    isSent = false;
                    flag = 0;
                }else{
                    flag = 1;
                }
            }else{
                flag = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            appendLog(e.getLocalizedMessage());
            flag = 1;
        }
        int count = 0;
        while(flag!=0&&count<10){
            try {
                Thread.sleep(20);
                count++;
            } catch (InterruptedException e) {
                Log.e("CARDDE", "onSuccess: "+e.getLocalizedMessage() );
            }
        }
        Log.e(TAG, "sendCommand: "+String.valueOf(flag)+" "+details );
        return flag;
    }

    public synchronized int sendData(String details){
        /*Since the file will be in an XML String format
         * Create an XML string with that will be consumed by the
         * Desktop application*/
        try {
            byte [] strinbuffer = details.getBytes();
            if(openComPort()){
                if(comManager.connect(0)==0){
                    Thread.sleep(500);
                    comManager.write(strinbuffer,strinbuffer.length,0);
                    details = null;
                    return 0;
                }else return 1;
            }else {
                return -1;
            }

        } catch (Exception e) {
            e.printStackTrace();
            appendLog(e.getLocalizedMessage());
            return 1;
        }
    }

    public void closeCom(){
        if(comManager!=null){
            if(comManager.isOpened()){
                comManager.close();
            }
        }
    }
}
