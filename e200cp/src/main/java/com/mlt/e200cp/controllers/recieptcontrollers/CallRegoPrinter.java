package com.mlt.e200cp.controllers.recieptcontrollers;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.RectF;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.mlt.e200cp.controllers.backgroundcontrollers.SequencyHandler;
import com.mlt.e200cp.interfaces.PosSequenceInterface;
import com.mlt.e200cp.models.response.ISOPaymentResponse;
import com.mlt.e200cp.utilities.FetchDeviceSerialNumber.ULTests;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import rego.printlib.export.regoPrinter;

import static com.mlt.e200cp.models.MessageFlags.TXN_ERROR;
import static com.mlt.e200cp.utilities.helper.util.Utility.appendLog;

public class CallRegoPrinter {

    private ULTests printerInstance;
    private Boolean mBconnect;
    private regoPrinter regoPrinter1;
    private static CallRegoPrinter callRegoPrinter;
    private PosSequenceInterface posSequenceInterface;
    private ISOPaymentResponse rsp;
    private ProgressDialog dialog;

    private CallRegoPrinter(PosSequenceInterface sequenceInterface,ISOPaymentResponse resp){
        this.posSequenceInterface = sequenceInterface;
        this.rsp = resp;
    }

    public static CallRegoPrinter getInstance(PosSequenceInterface sequenceInterface,ISOPaymentResponse response){
        if(callRegoPrinter!=null){
            callRegoPrinter = null;
        }
        callRegoPrinter = new CallRegoPrinter(sequenceInterface,response);
        return callRegoPrinter;
    }

    //Initiate the printer Objects
    public void initiatePrinters(Context context, Boolean bothReciepts,boolean reversal){
        ((AppCompatActivity)context).runOnUiThread(()->{
            dialog = ProgressDialog.show(context,"","Printing receipt");
        });
        HandlerThread thread = new HandlerThread("printing");
        thread.start();
        Handler mHandler = new Handler(thread.getLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                printerInstance = ULTests.getInstance(context);
                printerInstance.setObject();
                regoPrinter1 = printerInstance.getObject();
                ArrayList<String> connectedDevices = regoPrinter1.CON_GetWirelessDevices(0);
                mBconnect = false;
                if(connectedDevices.size()>0){
                    boolean contains = false;
                    for(String s : connectedDevices){
                        if(s.contains("RG-MT")){
                            contains = true;
                            getRecieptDescision(bothReciepts,s, rsp,reversal);
                            break;
                        }
                    }
                    if(!contains){
                        dismissDialog();
                    }

                }
                else{
                    dismissDialog();
                    SequencyHandler.getInstance(TXN_ERROR,posSequenceInterface).execute("No Printer Available.");
                }

                mHandler.removeCallbacks(this);
                Looper.myLooper().quit();
            }
        };
        mHandler.post(runnable);
    }

    //Get the print decision.
    //Whether both merchant and customer...Or Merchant only
    private void getRecieptDescision(Boolean bothReciepts, String s, ISOPaymentResponse response, boolean reversal){

        if(reversal){
            if(bothReciepts){
                connect(s.split(",")[1], s.split(",")[0], response.getReversalCauseReceiptMerchantCopy());
                connect(s.split(",")[1], s.split(",")[0], response.getReceiptMerchantCopy());
                connect(s.split(",")[1], s.split(",")[0], response.getReceiptCustomerCopy());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    appendLog(e.getLocalizedMessage());
                }
                connect(s.split(",")[1], s.split(",")[0], response.getReversalCauseReceiptCustomerCopy());
            }else{

                connect(s.split(",")[1], s.split(",")[0], response.getReversalCauseReceiptMerchantCopy());
                connect(s.split(",")[1], s.split(",")[0], response.getReceiptMerchantCopy());
                connect(s.split(",")[1], s.split(",")[0], response.getReceiptCustomerCopy());
            }
        }else{
            if(bothReciepts){
                connect(s.split(",")[1], s.split(",")[0], response.getReceiptMerchantCopy());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    appendLog(e.getLocalizedMessage());
                }
                connect(s.split(",")[1], s.split(",")[0], response.getReceiptCustomerCopy());
            }else{
                connect(s.split(",")[1], s.split(",")[0], response.getReceiptMerchantCopy());
            }
        }

    }

    //Connect to the reciept printer.
    private void connect(String port, String name, String print) {
        if (mBconnect) {
            convertToPDF(print);
            printText();
        } else {

            int k = regoPrinter1.CON_ConnectDevices(name, port, 200);
            if (k > 0) {
                Log.d("Tag","Connected");
                printerInstance.setState(k);
                mBconnect = true;
                printerInstance.setPrintway(0);
                convertToPDF(print);
                printText();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    appendLog(e.getLocalizedMessage());
                }
                regoPrinter1.CON_CloseDevices(printerInstance.getState());
                mBconnect = false;

            } else {
                mBconnect = false;
                dismissDialog();
                Log.d("Tag","Failed to connect");
            }
        }
    }

    //method to print the reciept
    private void printText(){
//        Handler mHandler = new Handler(Looper.getMainLooper());
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        };
//        mHandler.post(runnable);
//        mHandler.postDelayed(()->{
//            mHandler.removeCallbacks(runnable);
//        },20000);
        int width=576;
        regoPrinter1.CON_PageStart(printerInstance.getState(), true,
                width, 2000);// 1480*1.4
        regoPrinter1.DRAW_PrintPDF(printerInstance.getState(), "/sdcard/Download/reciept.pdf", new RectF(0.16f, 0f, 1.2f, 2f),
                1, width, width == 1480 ? 0 : -1);
        regoPrinter1.CON_PageEnd(printerInstance.getState(),printerInstance.getPrintway());
        Log.e("","Printed");
        regoPrinter1.CON_PageStart(printerInstance.getState(),false,576,100);
        regoPrinter1.ASCII_CtrlFeedLines(printerInstance.getState(), 3);
        regoPrinter1.CON_PageEnd(printerInstance.getState(),0);
        File file = new File("/sdcard/Download/reciept.pdf");
        if(file.exists()){
            file.delete();
        }
        dismissDialog();
    }

    //convert the base 64 to pdf
    private void convertToPDF(String data){
        try {
            byte[] imageEncode = Base64.decode( data, Base64.DEFAULT );
            FileOutputStream fop = new FileOutputStream("/sdcard/Download/reciept.pdf");
            fop.write(imageEncode);
            fop.flush();
            fop.close();
        } catch (Exception e) {
            e.printStackTrace();
            appendLog(e.getLocalizedMessage());
            dismissDialog();
        }

    }

    private void dismissDialog(){
        if(dialog!=null){
            dialog.dismiss();
        }
    }
}
