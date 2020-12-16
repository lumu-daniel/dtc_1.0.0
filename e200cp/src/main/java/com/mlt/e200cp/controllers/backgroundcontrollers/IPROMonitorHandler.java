//package com.mlt.e200cp.controllers.backgroundcontrollers;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.util.Log;
//
//import com.mlt.e200cp.controllers.mainlogiccontrollers.SerialConnection;
//import com.mlt.e200cp.controllers.presenters.CitPresenter;
//import com.mlt.e200cp.interfaces.CancelResultCallBack;
//import com.mlt.e200cp.interfaces.ResultsCallback;
//import com.mlt.e200cp.interfaces.SerialCallback;
//import com.mlt.e200cp.interfaces.SerialEIDReadCallback;
//import com.mlt.e200cp.models.PrinterModal;
//
//import java.util.ArrayList;
//
//import static android.content.ContentValues.TAG;
//import static com.mlt.e200cp.models.enums.CommandCenter.ACCEPT_CASH;
//import static com.mlt.e200cp.models.enums.CommandCenter.CANCEL_ACCEPTANCE;
//import static com.mlt.e200cp.models.enums.CommandCenter.CANCEL_NFC_READER;
//import static com.mlt.e200cp.models.enums.CommandCenter.CIT_UPDATE_CASH;
//import static com.mlt.e200cp.models.enums.CommandCenter.CIT_UPDATE_VLCARDS;
//import static com.mlt.e200cp.models.enums.CommandCenter.EID_GET_STATUS;
//import static com.mlt.e200cp.models.enums.CommandCenter.EID_READ_DATA;
//import static com.mlt.e200cp.models.enums.CommandCenter.GET_ALL_DEV_STATUS;
//import static com.mlt.e200cp.models.enums.CommandCenter.GET_CARD_PRINTER_STATUS;
//import static com.mlt.e200cp.models.enums.CommandCenter.GET_CASH_DEVICE_STATUS;
//import static com.mlt.e200cp.models.enums.CommandCenter.GET_FSE_CARD_DETAILS;
//import static com.mlt.e200cp.models.enums.CommandCenter.GET_NFC_DEV_STATUS;
//import static com.mlt.e200cp.models.enums.CommandCenter.GET_STICKER_PRINTER_STATUS;
//import static com.mlt.e200cp.models.enums.CommandCenter.GET_SUMMARY;
//import static com.mlt.e200cp.models.enums.CommandCenter.INIT_CASH_DEVICE;
//import static com.mlt.e200cp.models.enums.CommandCenter.PC_RESTART;
//import static com.mlt.e200cp.models.enums.CommandCenter.PC_SHUTDOWN;
//import static com.mlt.e200cp.models.enums.CommandCenter.PRINT_CARD;
//import static com.mlt.e200cp.models.enums.CommandCenter.PRINT_STICKER;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESET_CARDS;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESET_CASH;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESPONSE_ACCEPTOR_LIGHT;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESPONSE_ACHIEVED;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESPONSE_ALL_DEV_STATUS;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESPONSE_CANCELLED_ACCEPTANCE;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESPONSE_CANCELLED_NFC;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESPONSE_CANCEL_ACCEPTANCE_FAILED;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESPONSE_CASH_ACCEPTED;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESPONSE_CASH_DEV_INIT;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESPONSE_CASH_DEV_STATUS;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESPONSE_CASH_DISPENSED;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESPONSE_CITGETSUMMARY;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESPONSE_CITRESETCARDS;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESPONSE_CITUPDATE;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESPONSE_EID_CANCEL;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESPONSE_EID_DATA;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESPONSE_EID_INSERTED;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESPONSE_EID_STATUS;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESPONSE_EVOLIS_PRINT_JOB;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESPONSE_EVOLIS_READY;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESPONSE_FSE_CARD_DETAILS;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESPONSE_NFC_DEV_STATUS;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESPONSE_STICKER_PRINT_JOB;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESPONSE_STICKER_STATUS;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESPONSE_TRANS_LOG;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESPONSE_UPDATESESSIONVARIABLES;
//import static com.mlt.e200cp.models.enums.CommandCenter.STATUS_NOT_READY;
//import static com.mlt.e200cp.models.enums.CommandCenter.STATUS_TRUE;
//import static com.mlt.e200cp.models.enums.CommandCenter.UPDATE_SESSION;
//import static com.mlt.e200cp.models.enums.CommandCenter.UPDATE_TRANS_PARAMS;
//
//public class IPROMonitorHandler extends AsyncTask {
//
//    private Context buildContext;
//    SerialConnection connection = null;
//    private Double amount;
//    private static IPROMonitorHandler iproMonitorHandler ;
//    static String actionType;
////    private ProgressDialog dialog = null;
//
//    private IPROMonitorHandler(Context buildContext) {
//        this.buildContext = buildContext;
//    }
//
//    public static IPROMonitorHandler getInstance(Context context){
//        if(iproMonitorHandler !=null){
//            iproMonitorHandler.cancel(true);
//        }
//
//        return new IPROMonitorHandler(context);
//    }
//
//    @Override
//    protected void onPreExecute() {
//        connection = SerialConnection.getInstanceSerial(buildContext);
//    }
//    SerialEIDReadCallback serialEIDReadCallback;
//    ResultsCallback callback;
//    CancelResultCallBack cancelResultCallBack;
//    String printCommand;
//    PrinterModal modal;
//
//
//    @Override
//    protected Object doInBackground(Object[] objects) {
//        actionType = (String) objects[0];
//        if(actionType.contains(EID_READ_DATA.label)){//EMIRATESIDREADERDATA
//            serialEIDReadCallback = (SerialEIDReadCallback) objects[1];
//            sendMessage(EID_GET_STATUS.label);//GETEMIRATESIDREADERSTATUS
//        }else if(actionType.contains(CANCEL_NFC_READER.label)){
//            cancelResultCallBack = (CancelResultCallBack) objects[1];
//            sendMessage(CANCEL_NFC_READER.label);
//        }else if(actionType.equalsIgnoreCase(RESET_CARDS.label)){
//            callback = (ResultsCallback) objects[1];
//            modal = (PrinterModal) objects[2];
//            sendMessage(RESET_CARDS.label+"-"+modal.getFSECardNumber()+"-"+modal.getSessionId()+"-"+modal.getKioskId()+"-"+modal.getKioskLocation()+"-"+modal.getUserId());
//        }else if(actionType.equalsIgnoreCase(RESET_CASH.label)){
//            callback = (ResultsCallback) objects[1];
//            modal = (PrinterModal) objects[2];
//            sendMessage(RESET_CASH.label+"-"+modal.getFSECardNumber()+"-"+modal.getSessionId()+"-"+modal.getKioskId()+"-"+modal.getKioskLocation()+"-"+modal.getUserId());
//        }else  if(actionType.equalsIgnoreCase(GET_SUMMARY.label)){
//            callback = (ResultsCallback) objects[1];
//            modal = (PrinterModal) objects[2];
//            sendMessage(GET_SUMMARY.label+"-"+modal.getFSECardNumber()+"-"+modal.getSessionId()+"-"+modal.getKioskId()+"-"+modal.getKioskLocation()+"-"+modal.getUserId());
//        }else if(actionType.equalsIgnoreCase(CIT_UPDATE_CASH.label)){
//            callback = (ResultsCallback) objects[1];
//            modal = (PrinterModal) objects[2];
//            sendMessage(CIT_UPDATE_CASH.label+"-"+modal.getDenomination()+"-"+modal.getNumNotes()+"-"+modal.getBarcode()+"-"+modal.getFSECardNumber()+"-"+modal.getSessionId()+"-"+modal.getKioskId()+"-"+modal.getKioskLocation()+"-"+modal.getUserId());
//        }else if(actionType.equalsIgnoreCase(UPDATE_SESSION.label)){
//            callback = (ResultsCallback) objects[1];
//            sendMessage(UPDATE_SESSION.label+"-"+objects[2]+"-"+objects[3]+"-"+objects[4]+"-"+objects[5]+"-"+objects[6]+"-"+objects[7]);
//        }
//        else{
//            callback = (ResultsCallback) objects[1];
//            if(actionType.equalsIgnoreCase(INIT_CASH_DEVICE.label)){//"INITIALIZECASHDEVICE"
//                sendMessage(INIT_CASH_DEVICE.label);
//            }else if(actionType.contains(ACCEPT_CASH.label)){
//                sendMessage(GET_CASH_DEVICE_STATUS.label);
//            } else if(actionType.contains(PC_RESTART.label)){
//                sendMessage(PC_RESTART.label);
//            }
//            else if(actionType.contains(PC_SHUTDOWN.label)){
//                sendMessage(PC_SHUTDOWN.label);
//            }
//            else if(actionType.contains(PRINT_STICKER.label)){
//                sendMessage(GET_STICKER_PRINTER_STATUS.label);
//            }
//            else if(actionType.contains(PRINT_CARD.label)){
//                sendMessage(GET_CARD_PRINTER_STATUS.label);
//                printCommand = actionType;
//            }
//            else if(actionType.contains("noData")){
//                sendMessage("HEARTBEATSTRING");
//            }
//            else if(actionType.contains(CANCEL_ACCEPTANCE.label)){
//                sendMessage(CANCEL_ACCEPTANCE.label);
//            }
//            else if(actionType.contains(GET_ALL_DEV_STATUS.label)){
//                sendMessage(GET_ALL_DEV_STATUS.label);
//            }
//            else if(actionType.contains(UPDATE_TRANS_PARAMS.label)){
//                sendMessage(actionType);
//                Log.i(TAG, "doInBackground: "+actionType);
//            }
//            else if(actionType.contains(GET_FSE_CARD_DETAILS.label)){
//                sendMessage(GET_NFC_DEV_STATUS.label);
//            }/*
//            else if(actionType.contains(RESET_CASH.label)){
//                sendMessage(GET_CASH_DEVICE_STATUS.label);
//            }
//            else if(actionType.contains(RESET_CARDS.label)){
//                sendMessage(actionType);
//            }
//            else if(actionType.contains(GET_SUMMARY.label)){
//                sendMessage(actionType);
//            }*/
//            else if(actionType.contains(CIT_UPDATE_VLCARDS.label)){
//                sendMessage(actionType);
//            }
//            else{
//                Log.e(TAG, "doInBackground: "+actionType );
//            }
//        }
//
//        connection.readBytesFromSerial(new SerialCallback() {
//            @Override
//            public void success(String details) {
//                try{
//                    if (details.toLowerCase().contains(RESPONSE_EID_DATA.label)) {
//                        serialEIDReadCallback.success(details);
//                    }
//                    else if (details.toLowerCase().contains(RESPONSE_EID_CANCEL.label)) {//"EmiratesIDReaderCancelReading"
//                        serialEIDReadCallback.EIDInserted(RESPONSE_EID_CANCEL.label);
//                    }
//                    else if (details.toLowerCase().contains(RESPONSE_CASH_DISPENSED.label)) {//"dispense-true"
//                        if( details.toLowerCase().contains( STATUS_TRUE.label ) ){
//                            callback.onResponseSuccess(details.replace("response-",""));
//                        }else{
//                            callback.onResponseFailure(details.replace("response-",""));
//                        }
//                    }
//                    else if (details.toLowerCase().contains(RESPONSE_CASH_ACCEPTED.label)) {
//                        callback.onResponseSuccess("billaccepted-" + details.split("-")[3] + ".00");
//                        amount = Double.valueOf(details.split("-")[3]);
//                    }
//                    else if(details.equalsIgnoreCase(RESPONSE_CASH_DEV_INIT.label)){
//                        callback.onResponseSuccess(details);
//                    }
//                    else if(details.toLowerCase().contains(RESPONSE_EVOLIS_PRINT_JOB.label)){
//                        if(details.toLowerCase().contains(STATUS_TRUE.label)){
//                            callback.onResponseSuccess("Printed");
//                        }else{
//                            callback.onResponseFailure(details);
//                        }
//                    }
//                    else if(details.toLowerCase().contains(RESPONSE_CASH_DEV_STATUS.label)){//cashdevice-ready
//                        if(actionType.contains(ACCEPT_CASH.label)){
//                            if(details.toLowerCase().contains(STATUS_NOT_READY.label)){
//                                callback.onResponseFailure("Cash Device inactive");
//                            }else{
//                                amount = 0.0;
//                                sendMessage(actionType+"-"+objects[2]+"-"+objects[3]);
//                            }
//                        }else if(actionType.contains(RESET_CASH.label)){
//                            if(details.toLowerCase().contains(STATUS_NOT_READY.label)){
//                                callback.onResponseFailure("Cash Device inactive");
//                            }else{
//                                sendMessage(actionType);
//                            }
//                        }
//                    }
//                    else if(details.equals(RESPONSE_EVOLIS_READY.label)){
//                        sendMessage(actionType);
//                    }
//                    else if(details.toLowerCase().contains(RESPONSE_STICKER_STATUS.label)){
//                        if(details.toLowerCase().contains(STATUS_NOT_READY.label)){
//                            callback.onResponseFailure("Stricker printer not ready.");
//                        }else{
//                            sendMessage(actionType);//STICKERPRINTERJOB
//                        }
//                    }
//                    else if(details.equals(RESPONSE_CANCELLED_ACCEPTANCE.label)){
//                        callback.onResponseSuccess(RESPONSE_CANCELLED_ACCEPTANCE.label);
//                    }
//                    else if(details.equals(RESPONSE_CANCEL_ACCEPTANCE_FAILED.label)){
//                        callback.onResponseSuccess(RESPONSE_CANCEL_ACCEPTANCE_FAILED.label);
//                    }
//                    else if(details.toLowerCase().contains(RESPONSE_ACCEPTOR_LIGHT.label)){
//                        if(details.toLowerCase().contains(STATUS_TRUE.label)){
//                            callback.onResponseSuccess("cashacceptorlight-true");
//                        }else{
//                            callback.onResponseFailure("cashacceptorlight-false");
//                        }
//                    }
//                    else if(details.toLowerCase().contains(RESPONSE_EID_STATUS.label)){
//                        if(details.toLowerCase().contains(STATUS_NOT_READY.label)){
//                            serialEIDReadCallback.failure("EID Reader not connected.");
//                        }else{
//                            sendMessage(actionType);
//                        }
//                    }
//                    else if(details.toLowerCase().contains(RESPONSE_EID_INSERTED.label)){
//                        serialEIDReadCallback.EIDInserted("Inserted");
//                        Log.e(TAG, "success: "+details );
//                    }
//                    else if(details.toLowerCase().contains(RESPONSE_ALL_DEV_STATUS.label)){
//                        callback.onResponseSuccess(details);
//                        Log.e(TAG, "success: "+details );
//                    }
//                    else if (details.toLowerCase().contains(RESPONSE_ACHIEVED.label)){
//                        amount = Double.valueOf(details.split("-")[2]);
//                        Double dueAmount = Double.valueOf(actionType.split("-")[1]);
//                        if(amount>dueAmount){
//                            sendMessage("DISPENSECASH-"+String.valueOf(amount-dueAmount));
//                        }else{
//                            callback.onResponseSuccess("Fine");
//                            Log.e(TAG, "success: Do not dispense");
//                        }
//                        callback.onResponseSuccess("BillCountAchieved-"+String.valueOf(amount));
//                    }
//                    else if (details.toLowerCase().contains(RESPONSE_STICKER_PRINT_JOB.label)){
//                        if(details.toLowerCase().contains(STATUS_TRUE.label)){
//                            callback.onResponseSuccess("printed");
//                        }else{
//                            callback.onResponseFailure("not printed.");
//                        }
//                    }
//                    else if(details.toLowerCase().contains(RESPONSE_NFC_DEV_STATUS.label)){
//                        sendMessage(GET_FSE_CARD_DETAILS.label);
//                    }
//                    else if(details.toLowerCase().contains(RESPONSE_FSE_CARD_DETAILS.label)){
//                        if(details.split("-")[2].equalsIgnoreCase("Failed")){
//                            callback.onResponseFailure("Failed");
//                        }else{
//                            String opdata = details.replace("endofjsonparameters","").substring(details.indexOf("{"),details.indexOf("}")+1);
//                            callback.onResponseSuccess(opdata);
//                        }
//                    }
//                    else if(details.toLowerCase().contains(RESPONSE_CANCELLED_NFC.label)){
//                        if(CitPresenter.stopTimer){
//                            cancelResultCallBack.onResult("Cancelled.");
//                        }
//                    }
//                    else if(details.toLowerCase().split("-")[1].equalsIgnoreCase(RESPONSE_CITRESETCARDS.label)){
//                        if(details.toLowerCase().contains(STATUS_TRUE.label)){
//                            callback.onResponseSuccess(details.toLowerCase());
//                        }else{
//                            callback.onResponseFailure(details.toLowerCase());
//                        }
//                    }else if(details.toLowerCase().contains(RESPONSE_CITGETSUMMARY.label)){
////                        callback.onResponseSuccess(details.replace("endofjsonparameters","").substring(details.indexOf("[{"),details.indexOf("}]")+2));
//                        if(details.toLowerCase().split("-")[2].equalsIgnoreCase(STATUS_TRUE.label)){
//                            callback.onResponseSuccess("citgetsummary-true");
//                        }else{
//                            callback.onResponseFailure("citgetsummary-false");
//                        }
//                    }
//                    else if(details.toLowerCase().contains(RESPONSE_CITUPDATE.label)){
//                        if (details.toLowerCase().split("-")[2].equalsIgnoreCase("cash")){
//                            if(details.toLowerCase().split("-")[3].equalsIgnoreCase(STATUS_TRUE.label)){
//                                callback.onResponseSuccess("citcashupdated-true");
//                            }else{
//                                callback.onResponseFailure("citcashupdated-false");
//                            }
//                        }else {
//
//                        }
//                    }else if(details.toLowerCase().contains(RESPONSE_UPDATESESSIONVARIABLES.label)){
//                        if( details != null ) {
//                            String[] responseData = details.split("-");
//                            if (responseData[responseData.length - 1].equalsIgnoreCase(STATUS_TRUE.label)) {
//                                callback.onResponseSuccess(responseData[responseData.length - 1]);
//                            }else{
//                                callback.onResponseFailure(responseData[1]);
//                            }
//                        }
//                    }else if( details.toLowerCase().contains(RESPONSE_TRANS_LOG.label) ){
//                        if( details != null ) {
//                            String[] responseData = details.toLowerCase().split("-");
//                            ArrayList<String> responseArray = new ArrayList<>();
//                            for(String data: responseData){
//                                responseArray.add( data );
//                            }
//                            if (responseArray.contains("true")) {
//                                callback.onResponseSuccess(responseData[responseData.length - 1]);
//                            }else{
//                                callback.onResponseFailure(responseData[1]);
//                            }
//                        }
//                    } else {
//                        if(actionType.contains(EID_READ_DATA.label)){//"EmiratesIDReaderData"
//                            serialEIDReadCallback.failure(details);
//                        }
//                        else{
//                            callback.onResponseFailure(details);
//                        }
//                        Log.e(TAG, "success: "+details);
//                    }
//                }catch (Exception ex){
//                    ex.printStackTrace();
//                    Log.e("IPROMoniter", "onSuccess: "+ex.getLocalizedMessage() );
//                    if(actionType.contains(EID_READ_DATA.label)){
//                        serialEIDReadCallback.failure(details);
//                    }else{
//                        callback.onResponseFailure("Please contact MLT.");
//                    }
//                }
//
//            }
//
//            @Override
//            public void failure(String details) {
//                if(actionType.contains(EID_READ_DATA.label)){
//                    serialEIDReadCallback.failure(details);
//                }else{
//                    if(details.toLowerCase().contains(RESPONSE_NFC_DEV_STATUS.label)){
//                        callback.onResponseFailure("NFC device not connected.");
//                    }else{
//                        callback.onResponseFailure(details);
//                    }
//                }
//            }
//
//        });
//
//        return null;
//    }
//
//    private synchronized void sendMessage(String msg){
//        if(connection.sendCommand(msg)==0){
//            Log.i(TAG, "sendMessage: "+msg );
//        }else{
//            if(msg.contains(EID_READ_DATA.label)||msg.contains(EID_GET_STATUS.label)){
//                serialEIDReadCallback.failure("PC not connected.");
//            }else if(actionType.equalsIgnoreCase(CANCEL_NFC_READER.label)){
//                cancelResultCallBack.onResult("Failed");
//            }else{
//                callback.onResponseFailure("PC not connected.");
//            }
//        }
//    }
//}
