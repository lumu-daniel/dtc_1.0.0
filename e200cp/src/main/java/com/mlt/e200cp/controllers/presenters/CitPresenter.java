//package com.mlt.e200cp.controllers.presenters;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.os.CountDownTimer;
//import android.os.Handler;
//import android.os.Looper;
//
//import com.mlt.e200cp.controllers.backgroundcontrollers.IPROMonitorHandler;
//import com.mlt.e200cp.interfaces.CancelResultCallBack;
//import com.mlt.e200cp.interfaces.ResultsCallback;
//import com.mlt.e200cp.models.PrinterModal;
//
//import static com.mlt.e200cp.models.enums.CommandCenter.CANCEL_NFC_READER;
//import static com.mlt.e200cp.models.enums.CommandCenter.CIT_UPDATE_CASH;
//import static com.mlt.e200cp.models.enums.CommandCenter.CIT_UPDATE_VLCARDS;
//import static com.mlt.e200cp.models.enums.CommandCenter.GET_FSE_CARD_DETAILS;
//import static com.mlt.e200cp.models.enums.CommandCenter.GET_SUMMARY;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESET_CARDS;
//import static com.mlt.e200cp.models.enums.CommandCenter.RESET_CASH;
//
//public class CitPresenter {
//
//    private Context baseContext;
//    private IPROMonitorHandler handler;
//    public static boolean stopTimer;
//
//    private CitPresenter(Context context) {
//        this.baseContext = context;
//    }
//
//    public static CitPresenter getInstance(Context context){
//        return new CitPresenter(context);
//    }
//
//    public void citLogin(ResultsCallback callback){
//        handler = IPROMonitorHandler.getInstance(baseContext);
//        handler.execute(GET_FSE_CARD_DETAILS.label,callback);
//    }
//
//    public void cancelNFC(CancelResultCallBack callback){
//        handler = IPROMonitorHandler.getInstance(baseContext);
//        handler.execute(CANCEL_NFC_READER.label,callback);
//        stopTimer = true;
//        timeOutThread(callback);
//    }
//
//    public void resetCash(PrinterModal modal, ResultsCallback callback){
//        handler = IPROMonitorHandler.getInstance(baseContext);
//        handler.execute(RESET_CASH.label,callback,modal);
//    }
//
//    public void resetCards(PrinterModal modal,ResultsCallback callback){
//        handler = IPROMonitorHandler.getInstance(baseContext);
//        handler.execute(RESET_CARDS.label,callback,modal);
//    }
//
//    public void getSummary(ResultsCallback callback, PrinterModal modal){
//        handler = IPROMonitorHandler.getInstance(baseContext);
//        handler.execute(GET_SUMMARY.label,callback,modal);
//    }
//
//    public void updateCards(ResultsCallback callback){
//        handler = IPROMonitorHandler.getInstance(baseContext);
//        handler.execute(CIT_UPDATE_VLCARDS.label,callback);
//    }
//
//    public void updateCash(ResultsCallback callback, PrinterModal modal){
//        handler = IPROMonitorHandler.getInstance(baseContext);
//        handler.execute(CIT_UPDATE_CASH.label,callback,modal);
//    }
//
//    private void timeOutThread(CancelResultCallBack callback){
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    new CountDownTimer(30000, 1000) {
//                        @Override
//                        public void onTick(long millisUntilFinished) {
//
//                        }
//
//                        @Override
//                        public void onFinish() {
//                            if(stopTimer){
//                                callback.onResult("timeOut");
//                            }
//                        }
//                    }.start();
//                } catch (Exception e) {
//                    handler.cancel(true);
//                    callback.onResult("timeOut");
//                }
//            }
//        });
//    }
//}
