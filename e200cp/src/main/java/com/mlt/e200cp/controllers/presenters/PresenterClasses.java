package com.mlt.e200cp.controllers.presenters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mlt.e200cp.controllers.backgroundcontrollers.SequencyHandler;
import com.mlt.e200cp.controllers.deviceconfigcontrollers.CallMerchantDetail;
import com.mlt.e200cp.controllers.deviceconfigcontrollers.ConfigurationClass;
import com.mlt.e200cp.controllers.mainlogiccontrollers.EmvTransactionPresenter;
import com.mlt.e200cp.interfaces.ChipCardCallBack;
import com.mlt.e200cp.interfaces.PosSequenceInterface;
import com.mlt.e200cp.interfaces.ResultsCallback;
import com.mlt.e200cp.interfaces.ViewInterface;
import com.mlt.e200cp.models.PaymentDetailsModel;
import com.mlt.e200cp.models.PosDetails;
import com.mlt.e200cp.models.response.ISOPaymentResponse;
import com.mlt.e200cp.utilities.helper.util.Utility;

import static com.mlt.e200cp.models.MessageFlags.CANCEL_INT_TXN;
import static com.mlt.e200cp.models.MessageFlags.PORT_OPEN;
import static com.mlt.e200cp.models.MessageFlags.SENDSTATE;
import static com.mlt.e200cp.models.MessageFlags.START_INT_TXN;
import static com.mlt.e200cp.models.MessageFlags.STOP_INT_TXN;
import static com.mlt.e200cp.utilities.helper.util.Utility.appendLog;

public class PresenterClasses {

    private Context baseContext;
    private static PresenterClasses presenterClasses = null;
    //    private IPROMonitorHandler handler = null;
    private EmvTransactionPresenter emvTransactionPresenter = null;
    private ViewInterface viewInterface;
    private static ObjectWriter ow;
    private static String message;
    private static Handler mHandler;
    private static ISOPaymentResponse.TransactionDetailsData transactionDetailsData;
    private static String jsonInString = null;
    private static HandlerThread handlerThread;

    /*Create a private constructor to ensure a singleton*/
    private PresenterClasses(Context context, ViewInterface viewInterface){
        this.baseContext = context;
        this.viewInterface = viewInterface;
    }

    private PresenterClasses(Context context){
        this.baseContext = context;
    }

    /*Returns a singleton instance of the Presenter class.*/
    public static PresenterClasses getInstance(Context context, ViewInterface viewInterface){
        return presenterClasses==null?new PresenterClasses(context,viewInterface):presenterClasses;
    }

    public static PresenterClasses getInstance(Context context){
        return presenterClasses==null?new PresenterClasses(context):presenterClasses;
    }
    /*Initialises the cash acceptor using serial Connection
     * Here, a C-type cable is used.
     * Port is opened and a reconciliation service is supposed to be created for logging
     * failed connect during a transaction.*/
//    public void initiallizeAcceptorCommand(ResultsCallback callback1) {
//        try{
//            handler = IPROMonitorHandler.getInstance(baseContext);
//            handler.execute(INIT_CASH_DEVICE.label,callback1);
//        }catch (Exception e){
//            Log.e(TAG, "initiallizeAcceptorCommand: "+e.getMessage() );
//        }
//    }

    /*Used to send a command to the IPro for cash acceptance,
     * Takes in the String denoting the cash to be accepted.
     * Incase of an excess, the dispense command is called
     * And it is encapsulated from the consumer of the API*/
//    public void acceptCash(ResultsCallback callback, String cash, String booleanAccept, String tag){
//        try{
//            if(cash.contains(".")){
//                cash = cash.replace('.','-').split("-")[0];
//            }
//            handler = IPROMonitorHandler.getInstance(baseContext);
//            handler.execute(ACCEPT_CASH.label+"-"+cash,callback, booleanAccept, tag);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    /*Is used to read all emirates ID cards
     * Returns a JSONString of card values*/
    public void readEmiratesID(ChipCardCallBack callback){
        Utility.CHIPTYPE = "EID";
        emvTransactionPresenter = EmvTransactionPresenter.getCHIPInstance(callback,baseContext,viewInterface);
        emvTransactionPresenter.onAction("Read Emirates ID");
    }

//    public void sendInitialCommand(){
//        handler = IPROMonitorHandler.getInstance(baseContext);
//        handler.execute("noData", new ResultsCallback() {
//            @Override
//            public String onResponseSuccess(String data) {
//
//                return data;
//            }
//
//            @Override
//            public String onResponseFailure(String t) {
//
//                return t;
//            }
//        });
//    }

    /*Please add a loader till the success or failure callback.*/
//    public void dbWritter( PaymentDetailsModel detailsModel, boolean initial, ResultsCallback callback ){
//
//        try {
//            handler = IPROMonitorHandler.getInstance(baseContext);
//            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
//            String jsonInString = ow.writeValueAsString(detailsModel);
//            if(initial){
//                handler.execute(UPDATE_TRANS_PARAMS.label+"-TRUE"+"-#$%"+jsonInString, callback);
//            }else{
//                handler.execute(UPDATE_TRANS_PARAMS.label+"-FALSE"+"-#$%"+jsonInString, callback);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//    }

    /*Is used for all swipe implementations.
     * Returns JSONString with
     * @Params -track1,
     *         -track2,
     *         -track3*/
    public void getDataTrackData(ResultsCallback callback,String timeout){
        emvTransactionPresenter = EmvTransactionPresenter.getInstance(callback,baseContext,viewInterface,timeout);
        emvTransactionPresenter.onAction("Run Transaction");
    }

    /*
     * Closes an opened port
     * Usually implemented in the back or home buttons
     * */
    public void closePort(){
        emvTransactionPresenter.onAction("Close Port");
    }

    /*Read chip card data*/
    public void readTransactionCard(ChipCardCallBack callback){
        Utility.CHIPTYPE = "Transaction";
        emvTransactionPresenter = EmvTransactionPresenter.getCHIPInstance(callback,baseContext,viewInterface);
        emvTransactionPresenter.onAction("Read Transaction Cards");
    }

    /*Read emirates id from serial port.
     * */
//    public void readEIDFromSerial(SerialEIDReadCallback callback){
//        try{
//            handler = IPROMonitorHandler.getInstance(baseContext);
//            handler.execute(EID_READ_DATA.label,callback);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

//    public void printCards(String transactionType, String frontURL,String backURL, ResultsCallback callback){
//
//        String command = "";
//        switch(transactionType){
//            /*case"DL":
//                command="printjob-"+transactionType+"-"+customerEmailAddress+"-"+recieptServiceName+"-"+terminalID+"-"+transactionID+"-"+serviceName;
//                break;*/
//            case"VL":
//                command=PRINT_CARD.label+"-VL-"+frontURL+"-"+backURL;
//                //printjob-VL-https://kp.networkips.com:6103/RTACardImages/Vehicle/90540431F.jpg-https://kp.networkips.com:6103/RTACardImages/Vehicle/90540431B.jpg
//                break;
//            /*case"NOC":
//                command="printjob-"+transactionType+"-"+customerEmailAddress+"-"+recieptServiceName+"-"+terminalID+"-"+transactionID+"-"+serviceName;
//                break;*/
//                default:
//                  callback.onResponseFailure("Unrecognised parameter values.");
//        }
//        handler = IPROMonitorHandler.getInstance(baseContext);
//        handler.execute(command,callback);
//    }
//
//    public void printStickers(String year, String month, ResultsCallback callback){
//        try{
//            handler = IPROMonitorHandler.getInstance(baseContext);
//            handler.execute(PRINT_STICKER.label+"-"+month+"-"+year,callback);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    /*Cancels the cash acceptance,
     * called on timeouts and cancels*/
//    public void cancelAcceptance(ResultsCallback callback){
//        try{
//            handler = IPROMonitorHandler.getInstance(baseContext);
//            handler.execute(CANCEL_ACCEPTANCE.label, callback);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

//    private void writeDataToLocalDB(String data, ResultsCallback callback){
//        handler = IPROMonitorHandler.getInstance(baseContext);
//        handler.execute(data,callback);
//    }

    public void callUIThreadGeneric(String title,String message, boolean home){
        try{
            ((AppCompatActivity)baseContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(baseContext)
                            .setMessage(message)
                            .setTitle(title)
                            .setCancelable(false)
                            .setPositiveButton("ok",(dialog, which) ->{
                                dialog.dismiss();

                                if(home){
                                    try {
                                        ((AppCompatActivity)baseContext).startActivity(new Intent(baseContext,Class.forName("rtaservices.RTAMainActivity")));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        appendLog(e.getLocalizedMessage());
                                    }
                                }
                            }).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            appendLog(e.getLocalizedMessage());
        }

//        new Handler().
    }

    /*Must have the Due amount populated at this point.
     * For cash, Set the:
     * -Due amount
     * -Paid Amount
     * -Remaining amount,
     * -Missing amounts,
     * -BalanceAmount,
     * -Remaining amount,
     * -setCashAccepted,
     * -CashReturned,
     * -PaidAmount,
     * */
    public PaymentDetailsModel getCardOrCashInstance(PaymentDetailsModel detailsModel, String dueAmount, String TerminalID, String viseCode, String merchantID, String cardOrCash){
        detailsModel.setChequeAmount(null);
        detailsModel.setCashRejected(null);
        detailsModel.setChequeBackScan(null);
        detailsModel.setChequeFrontScan(null);
        detailsModel.setChequeNo(null);
        detailsModel.setChequeScanFileExtension(null);
        detailsModel.setChequeSerial(null);
        detailsModel.setChequeType(null);
        detailsModel.setTerminalId(TerminalID);
        detailsModel.setMerchant(merchantID);
        detailsModel.setSetviseCode(viseCode);
        switch (cardOrCash){
            case "CARD":
                detailsModel.setBalanceAmount(0.0);
                detailsModel.setMissingAmount(0.0);
                detailsModel.setCoinAccepted(0.0);
                detailsModel.setDueAmount(Double.valueOf(dueAmount));
                detailsModel.setServiceCharges(0.0);
                detailsModel.setCoinRejected(0.0);
                detailsModel.setCointRetruned(0.0);
                detailsModel.setRemainingAmount(0.0);
                detailsModel.setServiceAmount(detailsModel.getServiceCharges()+detailsModel.getDueAmount());
                detailsModel.setCashAccepted(0.0);
                detailsModel.setCashRejected(0.0);
                detailsModel.setCashRetruned(0.0);
                detailsModel.setPaidAmount(0.0);
                detailsModel.setPaymentTypeId(0);
//                detailsModel.setChequeDate(null);
                return detailsModel;
            case "CASH":
                detailsModel.setPaymentTypeId(1);
                detailsModel.setCoinRejected(0.0);
                detailsModel.setCointRetruned(0.0);
                detailsModel.setCoinAccepted(0.0);
                detailsModel.setServiceCharges(0.0);
                detailsModel.setCashRejected(0.0);
                detailsModel.setBalanceAmount(0.0);

//                detailsModel.setChequeDate(null);
                detailsModel.setDueAmount(Double.valueOf(dueAmount));
                detailsModel.setServiceAmount(detailsModel.getServiceCharges()+detailsModel.getDueAmount());
                return detailsModel;
            default:
                return null;
        }
    }

//    public void getConnectedDevices(ResultsCallback callback){
//        handler = IPROMonitorHandler.getInstance(baseContext);
//        handler.execute(GET_ALL_DEV_STATUS.label,callback);
//        //"Response-GETAllDEVICESTATUS-false,false,false,false"
//    }

    public static void getConfigData(String serviceType,String deviceSN, SharedPreferences shared, ResultsCallback callback){
        CallMerchantDetail getDeviceConfigurations = new CallMerchantDetail();
        getDeviceConfigurations.getConfguratioParameters(serviceType,deviceSN,shared,callback );
    }

//    public void restartPC(ResultsCallback callback){
//        handler = IPROMonitorHandler.getInstance(baseContext);
//        handler.execute(PC_RESTART.label,callback);
//    }
//
//    public void shutDownPC(ResultsCallback callback){
//        handler = IPROMonitorHandler.getInstance(baseContext);
//        handler.execute(PC_SHUTDOWN.label,callback);
//    }

    //UPDATESESSIONVARIABLES-ServiceId-SessionId-TerminalId-MerchantId-Classname-TransactionId
//    public void updateSessionVariable(ResultsCallback callback, String serviceId, String sessionId, String terminalId, String merchantId, String className, String transactionId){
//        handler = IPROMonitorHandler.getInstance( baseContext );
//        handler.execute( UPDATE_SESSION.label, callback, serviceId, sessionId, terminalId, merchantId, className, transactionId );
//
//    }

    public void sendCTLSNotification(ResultsCallback callback,String ctlsTimeOutHex){

        emvTransactionPresenter = EmvTransactionPresenter.getInstance(callback,baseContext,viewInterface,ctlsTimeOutHex);

        emvTransactionPresenter.onAction("CTLS Notification");

    }

    public static void callEMVISO8583(PosSequenceInterface callback, AppCompatActivity appCompatActivity, ViewInterface viewInterface, String amount, int timeOut, PosDetails details,boolean isIntergrated,ResultsCallback resultsCallback){
        SequencyHandler.getInstance(PORT_OPEN,callback).execute(appCompatActivity,viewInterface,amount,timeOut, details,isIntergrated,resultsCallback);
    }

    public static void startTxn(ResultsCallback callback){
        SequencyHandler.getInstance(START_INT_TXN,callback).execute( callback);
    }

    public static void stopTxn( ISOPaymentResponse transactionDetailsData,ResultsCallback callback){
        if(ConfigurationClass.serviceFlag.equalsIgnoreCase("Integrated")){
            SequencyHandler.getInstance(STOP_INT_TXN,callback).execute( transactionDetailsData);
//            ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
//            try {
//                jsonInString = ow.writeValueAsString(transactionDetailsData);
//                handlerThread = new HandlerThread("params");
//                handlerThread.start();
//                mHandler = new Handler(handlerThread.getLooper());
//                mHandler.post(()->{
//                    if(sendMessage(/*SEND_TXN_DATA.label+"-#$%"+*/"startofjsonparameters"+jsonInString+"endofjsonparameters")==0){
//                        connection.closeCom();
//                    }
//                });
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }
        }
    }

    public static void cancelTxn(ResultsCallback callback){
        if(ConfigurationClass.serviceFlag.equalsIgnoreCase("Integrated")){
            SequencyHandler.getInstance(CANCEL_INT_TXN,callback).execute( );
        }
    }

    public static void sendState( String state){
        if(ConfigurationClass.serviceFlag.equalsIgnoreCase("Integrated")){
            SequencyHandler.getInstance(SENDSTATE,new ResultsCallback() {
                @Override
                public String onResponseSuccess(String data) {
                    return null;
                }

                @Override
                public String onResponseFailure(String t) {
                    return null;
                }
            }).execute( state);
        }

    }

}
