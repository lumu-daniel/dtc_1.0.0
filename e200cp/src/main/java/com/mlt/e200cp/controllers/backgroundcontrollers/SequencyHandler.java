package com.mlt.e200cp.controllers.backgroundcontrollers;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mlt.e200cp.controllers.deviceconfigcontrollers.ConfigurationClass;
import com.mlt.e200cp.controllers.mainlogiccontrollers.HelperEMVClass;
import com.mlt.e200cp.controllers.mainlogiccontrollers.SerialConnection;
import com.mlt.e200cp.interfaces.PosSequenceInterface;
import com.mlt.e200cp.interfaces.ResultsCallback;
import com.mlt.e200cp.interfaces.SerialCallback;
import com.mlt.e200cp.interfaces.ViewInterface;
import com.mlt.e200cp.models.PosDetails;
import com.mlt.e200cp.models.repository.response.ISOPaymentResponse;

import java.lang.ref.WeakReference;
import static android.content.ContentValues.TAG;
import static com.mlt.e200cp.models.StringConstants.CHIP_FALLBACK;
import static com.mlt.e200cp.models.StringConstants.FALLBACKCHIPORTAP;
import static com.mlt.e200cp.models.StringConstants.PORT_OPEN;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_GET_POS_STATUS;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_GET_TERM_DET;
import static com.mlt.e200cp.models.StringConstants.RESPONSE_START_TXN;
import static com.mlt.e200cp.models.StringConstants.SELECT_APP;
import static com.mlt.e200cp.models.StringConstants.SENDSTATE;
import static com.mlt.e200cp.models.StringConstants.START_INT_TXN;
import static com.mlt.e200cp.models.StringConstants.STATUS_NOT_READY;
import static com.mlt.e200cp.models.StringConstants.STATUS_READY;
import static com.mlt.e200cp.models.StringConstants.STOP_INT_TXN;
import static com.mlt.e200cp.models.StringConstants.SWIPE_FALLBACK;
import static com.mlt.e200cp.models.StringConstants.TXN_ERROR;
import static com.mlt.e200cp.models.StringConstants.TXN_PROCESSING;
import static com.mlt.e200cp.models.StringConstants.TXN_REVERSAL;
import static com.mlt.e200cp.models.StringConstants.TXN_REVERSED;
import static com.mlt.e200cp.models.StringConstants.TXN_SUCCESSFUL;
import static com.mlt.e200cp.utilities.helper.util.ISOConstant.serialPortOpened;
import static com.mlt.e200cp.utilities.helper.util.Utility.appendLog;

public class SequencyHandler extends HandlerThread {

    private PosSequenceInterface sequenceInterfacecallBack;
    private static SequencyHandler sequencyHandler = null;
    private String errorMsg;
    private HelperEMVClass helperEMVClass;
    private CharSequence [] args;
    private int timeSeconds;
    private AppCompatActivity baseContext;
    private ViewInterface vi;
    private String amount;
    private int timeOut;
    private PosDetails posDetails;
    private boolean isIntergrated;
    private SerialConnection connection;
    private ResultsCallback callback;
    private ObjectWriter ow;
    private String message;
    private Handler mHandler;
    private ISOPaymentResponse responseData;
    private String jsonInString = null;
    private HandlerThread handlerThread;
    private String nextSequenceFlag;

    private SequencyHandler(String name,PosSequenceInterface callback){
        super(name);
        this.sequenceInterfacecallBack = new WeakReference<PosSequenceInterface>(callback).get();
        nextSequenceFlag = name;
    }

    private SequencyHandler(String name,ResultsCallback callback1){
        super(name);
        this.callback = new WeakReference<ResultsCallback>(callback1).get();
        nextSequenceFlag = name;
    }

    public static SequencyHandler getInstance(String name,PosSequenceInterface callback){
        if(sequencyHandler!=null)
            sequencyHandler.quitSafely();
        sequencyHandler = new SequencyHandler(name,callback);
        return sequencyHandler;
    }

    public static SequencyHandler getInstance(String name,ResultsCallback callback){
        if(sequencyHandler!=null)
            sequencyHandler.quitSafely();
        sequencyHandler = new SequencyHandler(name,callback);
        return sequencyHandler;
    }

    public void execute(Object... objects) {
        start();
        Runnable runnable = ()->{
            try{
                //Todo add check on indexes.
                connection = SerialConnection.getInstanceSerial(baseContext);
                if(nextSequenceFlag.equals(TXN_ERROR)){
                    errorMsg = (String) objects[0];
                    if(objects.length>=2){
                        responseData = (ISOPaymentResponse) objects[1];
                    }

                }else if(nextSequenceFlag.equals(CHIP_FALLBACK)|| nextSequenceFlag.equals(SWIPE_FALLBACK)|| nextSequenceFlag.equals(FALLBACKCHIPORTAP)){
                    helperEMVClass = (HelperEMVClass) objects[0];
                }else if(nextSequenceFlag.equals(SELECT_APP)){
                    args = (CharSequence[]) objects[0];
                    timeSeconds = (int) objects[1];
                    boolean waitFlag = (Boolean) objects[2];
                }else if (nextSequenceFlag.equals(PORT_OPEN)){
                    baseContext = (AppCompatActivity) objects[0];
                    vi = (ViewInterface) objects[1];
                    amount = (String) objects[2];
                    timeOut = (int) objects[3];
                    posDetails = (PosDetails) objects[4];
                    isIntergrated = (boolean) objects[5];

                }else if(nextSequenceFlag.equals(TXN_PROCESSING)|| nextSequenceFlag.equals(TXN_REVERSAL)){
                    baseContext = (AppCompatActivity) objects[0];
                    posDetails = (PosDetails) objects[1];
                }else if(nextSequenceFlag.equals(TXN_SUCCESSFUL)|| nextSequenceFlag.equals(TXN_REVERSED)){
                    responseData = (ISOPaymentResponse) objects[0];
                }else if(nextSequenceFlag.equals(START_INT_TXN)){
                    callback = (ResultsCallback) objects[0];
                }else if(nextSequenceFlag.equals(SENDSTATE)){
                    message = (String) objects[0];
                }else if(nextSequenceFlag.equals(STOP_INT_TXN)){
                    responseData = (ISOPaymentResponse) objects[0];
                }

                switch (nextSequenceFlag){

                    case "PORT_OPEN":
                        Glide.get(baseContext).clearDiskCache();
                        helperEMVClass = HelperEMVClass.getInstance(baseContext,amount,sequenceInterfacecallBack,timeOut,vi,posDetails,isIntergrated);
                        helperEMVClass.onAction("Run Transaction");
                        sequenceInterfacecallBack.onPortOpened();
                        break;

                    case "CARD_INSERTED":
                        sequenceInterfacecallBack.onCardInserted();
                        break;

                    case "CARD_TAPPED":
                        sequenceInterfacecallBack.onCardTapped();
                        break;

                    case "CARD_SWIPPED":
                        sequenceInterfacecallBack.onCardSwipped();
                        break;

                    case "CHIP_FALLBACK":
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            appendLog(e.getLocalizedMessage());
                        }
                        helperEMVClass.onAction("Chip fallback");
                        sequenceInterfacecallBack.onChipFallBack();
                        break;

                    case "SWIPE_FALLBACK":
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            appendLog(e.getLocalizedMessage());
                        }
                        helperEMVClass.onAction("Swipe fallback");
                        sequenceInterfacecallBack.onSwipeFallBack();
                        break;

                    case "SELECT_APP":
                        sequenceInterfacecallBack.selectApp(args,timeSeconds);
                        break;

                    case "START_PINENTRY":
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            appendLog(e.getLocalizedMessage());
                        }
                        sequenceInterfacecallBack.onStartPinEntry();
                        break;

                    case "PROMPT_PHONE":
                        sequenceInterfacecallBack.onPromptCheckPhone();
                        break;

                    case "TXN_PROCESSING":
                        sequenceInterfacecallBack.onStartProcessing(posDetails,HelperEMVClass.emvTransactionDetails);

                        break;

                    //Todo call transactionDetails object
                    case "TXN_SUCCESSFUL":
                        sequenceInterfacecallBack.onTransactionSuccessful(responseData);
                        break;

                    //Todo call transactionDetails object
                    case "TXN_ERROR":
                        if(responseData !=null){
                            sequenceInterfacecallBack.onTrasactionSvcError(errorMsg, responseData);
                            responseData = null;
                        }else{
                            sequenceInterfacecallBack.onTrasactionError(errorMsg);
                        }
                        break;

                    case "TXN_REVERSAL":
                        sequenceInterfacecallBack.onReverseTxn(HelperEMVClass.emvTransactionDetails,posDetails);
                        break;

                    //Todo call transactionDetails object
                    case "TXN_REVERSED":
                        sequenceInterfacecallBack.onTrasactionSvcError("Reversed transaction",responseData);
                        break;

                    case "START_INT_TXN":
                        serialPortOpened = true;
                        break;

                    case "STOP_INT_TXN":
                        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                        try {
                            jsonInString = ow.writeValueAsString(responseData.getTransactionDetailsData());

                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                appendLog(e.getLocalizedMessage());
                            }
                            if(sendMessage(/*SEND_TXN_DATA.label+"-#$%"+*/"startofjsonparameters"+jsonInString+"endofjsonparameters")==0){
                                connection.closeCom();
                            }
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                            appendLog(e.getLocalizedMessage());
                        }
                        break;

                    case "CANCEL_INT_TXN":
                        serialPortOpened = false;
                        connection.closeCom();
                        break;

                    case "FALLBACKCHIPORTAP":
                        helperEMVClass.onAction("Run Transaction");
                        sequenceInterfacecallBack.onPortOpened();
                        break;
                    case "SENDSTATE":
//                handlerThread = new HandlerThread("states");
//                handlerThread.start();
//                mHandler = new Handler(handlerThread.getLooper());
//                mHandler.post(()->{
//
//                });
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            appendLog(e.getLocalizedMessage());
                        }
                        if(sendMessage("poseventlog:"+message)==0){
                            connection.closeCom();
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                appendLog(e.getLocalizedMessage());
                            }
                        }
                        break;

                    case "RMV_CARD_DISP":
                        sequenceInterfacecallBack.removeCardDisplayed();
                        break;

                    default:
                        break;
                }

                connection.readBytesFromSerial(new SerialCallback() {
                    @Override
                    public void success(String details) {
                        String command = details.split("-")[1];

                        if(command.equals(RESPONSE_GET_POS_STATUS)){
                            if(serialPortOpened){
                                sendMessage(STATUS_READY);
                            }else{
                                sendMessage(STATUS_NOT_READY);
                            }
                        }
                        else if(command.equals(RESPONSE_START_TXN)){
                            try {
                                Thread.sleep(100);
                                callback.onResponseSuccess(details);
                            } catch (Exception e) {
                                e.printStackTrace();
                                appendLog(e.getLocalizedMessage());
                                callback.onResponseFailure(e.getLocalizedMessage());
                            }
                        }
                        else if(command.equals(RESPONSE_GET_TERM_DET)){
                            Log.e("RESPONSE_GET_TERM_DET",ConfigurationClass.TERMINAL_ID+"-"+ConfigurationClass.MERCHANT_ID);
                            sendMessage(ConfigurationClass.TERMINAL_ID+"-"+ConfigurationClass.MERCHANT_ID);
                        }else {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                appendLog(e.getLocalizedMessage());
                            }
                            callback.onResponseFailure("unknown command.");
                        }
                    }

                    @Override
                    public void failure(String details) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            appendLog(e.getLocalizedMessage());
                        }
                        callback.onResponseFailure(details);
                    }
                });
            } catch (Exception ex){
                ex.printStackTrace();
                sequenceInterfacecallBack.onTrasactionError("Transaction declined by card.");
            }
        };

        new Handler(this.getLooper()).post(runnable);
    }

    private synchronized int sendMessage(String msg){
        try {
            if(connection!=null){
                if(connection.sendCommand(msg)==0){
                    Log.i(TAG, "sendMessage: "+msg );
                    if(msg.contains("poseventlog")){
                        Log.e("POSSTATUSMSGPASS",message);
                    }
                    return 0;
                }else{
                    if(msg.contains("poseventlog")){
                        Log.e("POSSTATUSMSGFAIL",message);
                    }
                    callback.onResponseFailure("Computer not connected");
                    return -1;
                }
            }else{
                return -1;
            }

        }catch (Exception ex){
            ex.printStackTrace();
            appendLog(ex.getLocalizedMessage());
            return -1;
        }

    }
}
