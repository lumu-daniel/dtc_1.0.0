//package com.mlt.e200cp.controllers.backgroundcontrollers;
//
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.os.Message;
//import android.util.Log;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.bumptech.glide.Glide;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectWriter;
//import com.mlt.e200cp.controllers.deviceconfigcontrollers.ConfigurationClass;
//import com.mlt.e200cp.controllers.mainlogiccontrollers.HelperEMVClass;
//import com.mlt.e200cp.controllers.mainlogiccontrollers.SerialConnection;
//import com.mlt.e200cp.controllers.servicecallers.CallIsoServiceClass;
//import com.mlt.e200cp.interfaces.PosSequenceInterface;
//import com.mlt.e200cp.interfaces.ResultsCallback;
//import com.mlt.e200cp.interfaces.SerialCallback;
//import com.mlt.e200cp.interfaces.ViewInterface;
//import com.mlt.e200cp.models.PosDetails;
//import com.mlt.e200cp.models.enums.UIFLAG;
//import com.mlt.e200cp.models.enums.UIHNDLRFLAG;
//import com.mlt.e200cp.models.response.ISOPaymentResponse;
//import com.mlt.e200cp.utilities.helper.util.ISOConstant;
//
//import static android.content.ContentValues.TAG;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESPONSE_GET_POS_STATUS;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESPONSE_GET_TERM_DET;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESPONSE_START_TXN;
//import static com.mlt.e200cp.models.enums.CommandCenter.STATUS_NOT_READY;
//import static com.mlt.e200cp.models.enums.CommandCenter.STATUS_READY;
//import static com.mlt.e200cp.models.enums.UIFLAG.CHIP_FALLBACK;
//import static com.mlt.e200cp.models.enums.UIFLAG.FALLBACKCHIPORTAP;
//import static com.mlt.e200cp.models.enums.UIFLAG.SELECT_APP;
//import static com.mlt.e200cp.models.enums.UIFLAG.SENDSTATE;
//import static com.mlt.e200cp.models.enums.UIFLAG.START_INT_TXN;
//import static com.mlt.e200cp.models.enums.UIFLAG.STOP_INT_TXN;
//import static com.mlt.e200cp.models.enums.UIFLAG.SWIPE_FALLBACK;
//import static com.mlt.e200cp.models.enums.UIFLAG.TXN_ERROR;
//import static com.mlt.e200cp.models.enums.UIFLAG.TXN_PROCESSING;
//import static com.mlt.e200cp.models.enums.UIFLAG.TXN_REVERSAL;
//import static com.mlt.e200cp.models.enums.UIFLAG.TXN_REVERSED;
//import static com.mlt.e200cp.models.enums.UIFLAG.TXN_SUCCESSFUL;
//import static com.mlt.e200cp.models.enums.UIHNDLRFLAG.PORT_OPEN;
//import static com.mlt.e200cp.utilities.helper.util.ISOConstant.intergratedMode;
//
//public class BaseHandlerThread extends HandlerThread {
//
//    Handler mHandler;
//    Object[] paramList;
//
//    private PosSequenceInterface sequenceInterfacecallBack;
////    private static SequencyHandler sequencyHandler = null;
//    private String errorMsg;
//    private HelperEMVClass helperEMVClass;
//    private CharSequence [] args;
//    private int timeSeconds;
//    private AppCompatActivity baseContext;
//    private ViewInterface vi;
//    private String amount;
//    private int timeOut;
//    private PosDetails posDetails;
//    private boolean isIntergrated;
//    private SerialConnection connection;
//    private ResultsCallback callback;
//    private ObjectWriter ow;
//    private String message;
//    private ISOPaymentResponse.TransactionDetailsData transactionDetailsData;
//
//    public BaseHandlerThread(String name, Object[] params) {
//        super(name);
//        this.paramList=params;
//
//    }
//
//    @Override
//    protected void onLooperPrepared()
//    {
//        initHandler(paramList);
//    }
//
//    private void initHandler(Object... objects)
//    {
//        connection = SerialConnection.getInstanceSerial(baseContext);
//        UIFLAG nextSequenceFlag = (UIFLAG) objects[0];
//        if(nextSequenceFlag.equals(TXN_ERROR)){
//            errorMsg = (String) objects[1];
//        }else if(nextSequenceFlag.equals(CHIP_FALLBACK)|| nextSequenceFlag.equals(SWIPE_FALLBACK)|| nextSequenceFlag.equals(FALLBACKCHIPORTAP)){
//            helperEMVClass = (HelperEMVClass) objects[1];
//        }else if(nextSequenceFlag.equals(SELECT_APP)){
//            args = (CharSequence[]) objects[1];
//            timeSeconds = (int) objects[2];
//            boolean waitFlag = (Boolean) objects[3];
//        }else if (nextSequenceFlag.equals(UIFLAG.PORT_OPEN)){
//            baseContext = (AppCompatActivity) objects[1];
//            vi = (ViewInterface) objects[2];
//            amount = (String) objects[3];
//            timeOut = (int) objects[4];
//            posDetails = (PosDetails) objects[5];
//            isIntergrated = (boolean) objects[6];
//
//        }else if(nextSequenceFlag.equals(TXN_PROCESSING)|| nextSequenceFlag.equals(TXN_REVERSAL)){
//            baseContext = (AppCompatActivity) objects[1];
//            posDetails = (PosDetails) objects[2];
//            sequenceInterfacecallBack = (PosSequenceInterface) objects[3];
//        }else if(nextSequenceFlag.equals(TXN_SUCCESSFUL)|| nextSequenceFlag.equals(TXN_REVERSED)){
////            transactionDetailsData = (TransactionDetailsData) objects[1];
//        }else if(nextSequenceFlag.equals(START_INT_TXN)){
//            callback = (ResultsCallback) objects[1];
//        }else if(nextSequenceFlag.equals(SENDSTATE)){
//            message = (String) objects[1];
//        }else if(nextSequenceFlag.equals(STOP_INT_TXN)){
//            transactionDetailsData = (ISOPaymentResponse.TransactionDetailsData) objects[1];
//        }
//        mHandler = new Handler(getLooper()) {
//            @Override
//            public void handleMessage(Message msg)
//            {
//               switch (msg.toString()){
//                   case "PORT_OPEN":
//                       Glide.get(baseContext).clearDiskCache();
//                       helperEMVClass = new HelperEMVClass(baseContext,amount,sequenceInterfacecallBack,timeOut,vi,posDetails,isIntergrated);
//                       helperEMVClass.onAction("Run Transaction");
//                       sequenceInterfacecallBack.onPortOpened();
//                       quitSafely();
//                       break;
//                   case "CARD_INSERTED":
//                       sequenceInterfacecallBack.onCardInserted();
//                       quitSafely();
//                       break;
//                   case "CARD_SWIPPED":
//                       sequenceInterfacecallBack.onCardSwipped();
//                       quitSafely();
//                       break;
//                   case "CARD_TAPPED":
//                       sequenceInterfacecallBack.onCardTapped();
//                       quitSafely();
//                       break;
//                   case "PROMPT_PHONE":
//                       sequenceInterfacecallBack.onPromptCheckPhone();
//                       quitSafely();
//                       break;
//                   case "CHIP_FALLBACK":
//                       try {
//                           Thread.sleep(1000);
//                       } catch (InterruptedException e) {
//                           e.printStackTrace();
//                       }
//                       helperEMVClass.onAction("Chip fallback");
//                       sequenceInterfacecallBack.onChipFallBack();
//                       quitSafely();
//                       break;
//                   case "SWIPE_FALLBACK":
//                       try {
//                           Thread.sleep(1000);
//                       } catch (InterruptedException e) {
//                           e.printStackTrace();
//                       }
//                       helperEMVClass.onAction("Swipe fallback");
//                       sequenceInterfacecallBack.onSwipeFallBack();
//                       quitSafely();
//                       break;
//                   case "SELECT_APP":
//                       sequenceInterfacecallBack.selectApp(args,timeSeconds);
//                       quitSafely();
//                       break;
//                   case "START_PINENTRY":
//                       sequenceInterfacecallBack.onStartPinEntry();
//                       quitSafely();
//                       break;
//                   case "TXN_PROCESSING":
//                       sequenceInterfacecallBack.onStartProcessing();
//                       CallIsoServiceClass.getInstance(HelperEMVClass.getTransactionDetails,baseContext, posDetails,sequenceInterfacecallBack).initiateParams();
//                       quitSafely();
//                       break;
//                   case "PINENTRY_FAIL":
//
//                       break;
//                   case "TXN_SUCCESSFUL":
//                       sequenceInterfacecallBack.onTransactionSuccessful();
//                       quitSafely();
//                       break;
//                   case "TXN_ERROR":
//                       sequenceInterfacecallBack.onTrasactionError(errorMsg);
//                       quitSafely();
//                       break;
//                   case "TXN_REVERSAL":
//                       ISOConstant.reversal = true;
//                       CallIsoServiceClass.getInstance(HelperEMVClass.getTransactionDetails,baseContext, posDetails,sequenceInterfacecallBack).initiateParams();
//                       sequenceInterfacecallBack.onTrasactionError("Reversing transaction");
//                       quitSafely();
//                       break;
//                   case "START_INT_TXN":
//                       intergratedMode = true;
//                       quitSafely();
//                       break;
//                   case "TXN_REVERSED":
//                       sequenceInterfacecallBack.onTrasactionError("Reversed transaction");
//                       quitSafely();
//                       break;
//                   case "STOP_INT_TXN":
//                       ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
//                       String jsonInString = null;
//                       try {
//                           jsonInString = ow.writeValueAsString(transactionDetailsData);
//                           if(sendMessages(/*SEND_TXN_DATA.label+"-#$%"+*/"startofjsonparameters"+jsonInString+"endofjsonparameters")==0){
//                               connection.closeCom();
//                           }
//
//                       } catch (JsonProcessingException e) {
//                           e.printStackTrace();
//                       }
//                       break;
//                   case "CANCEL_INT_TXN":
//                       intergratedMode = false;
//                       connection.closeCom();
//                       break;
//                   case "FALLBACKCHIPORTAP":
//                       helperEMVClass.onAction("Run Transaction");
//                       sequenceInterfacecallBack.onPortOpened();
//                       break;
//                   case "SENDSTATE":
//                       sendMessages("poseventlog:"+message);
//                       connection.closeCom();
//                       break;
//                   case "RMV_CARD_DISP":
//                       sequenceInterfacecallBack.removeCardDisplayed();
//                       break;
//                   default:
//                       throw new IllegalStateException("Unexpected value: " + msg.toString());
//               }
//
//                connection.readBytesFromSerial(new SerialCallback() {
//                    @Override
//                    public void success(String details) {
//                        String command = details.split("-")[1];
//
//                        if(command.equals(RESPONSE_GET_POS_STATUS.label)){
//                            if(intergratedMode){
//                                sendMessages(STATUS_READY.label);
//                            }else{
//                                sendMessages(STATUS_NOT_READY.label);
//                            }
//                        }
//                        else if(command.equals(RESPONSE_START_TXN.label)){
//                            try {
//                                callback.onResponseSuccess(details);
//                                quitSafely();
//                            } catch (Exception e) {
//                                callback.onResponseFailure(e.getLocalizedMessage());
//                                quitSafely();
//                                e.printStackTrace();
//                            }
//                        }
//                        else if(command.equals(RESPONSE_GET_TERM_DET.label)){
//                            Log.e("RESPONSE_GET_TERM_DET", ConfigurationClass.TERMINAL_ID+"-"+ConfigurationClass.MERCHANT_ID);
//                            sendMessages(ConfigurationClass.TERMINAL_ID+"-"+ConfigurationClass.MERCHANT_ID);
//                        }else {
//                            callback.onResponseFailure("unknown command.");
//                            quitSafely();
//                        }
//                    }
//
//                    @Override
//                    public void failure(String details) {
//                        callback.onResponseFailure(details);
//                    }
//                });
//
//            }
//        };
//    }
//
//    private synchronized int sendMessages(String msg){
//        try {
//            if(connection.sendCommand(msg)==0){
//                Log.i(TAG, "sendMessage: "+msg );
//                if(msg.contains("poseventlog")){
//                    Log.e("POSSTATUSMSGPASS",message);
//                }
//                return 0;
//            }else{
//                if(msg.contains("poseventlog")){
//                    Log.e("POSSTATUSMSGFAIL",message);
//                }
//                callback.onResponseFailure("Computer not connected");
//                return -1;
//            }
//        }catch (Exception ex){
//            ex.printStackTrace();
//            return -1;
//        }
//
//    }
//}