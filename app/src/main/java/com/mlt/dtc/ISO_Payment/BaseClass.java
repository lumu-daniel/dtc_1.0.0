package com.mlt.dtc.ISO_Payment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.mlt.dtc.ISO_Payment.satefragments.ChipFallBack;
import com.mlt.dtc.ISO_Payment.satefragments.PinEntryScreen;
import com.mlt.dtc.ISO_Payment.satefragments.PortsOpened;
import com.mlt.dtc.R;
import com.mlt.dtc.common.Common;
import com.mlt.e200cp.controllers.backgroundcontrollers.SequencyHandler;
import com.mlt.e200cp.controllers.mainlogiccontrollers.HelperEMVClass;
import com.mlt.e200cp.controllers.presenters.PresenterClasses;
import com.mlt.e200cp.controllers.servicecallers.RefundAndVoidServices;
import com.mlt.e200cp.interfaces.PosSequenceInterface;
import com.mlt.e200cp.interfaces.TransactionDoneCallback;
import com.mlt.e200cp.interfaces.ViewInterface;
import com.mlt.e200cp.models.GetTransactionDetails;
import com.mlt.e200cp.models.PosDetails;
import com.mlt.e200cp.models.enums.TxnState;
import com.mlt.e200cp.models.response.ISOPaymentResponse;

import static com.mlt.dtc.ISO_Payment.satefragments.PinEntryScreen.pinEntryScreen;
import static com.mlt.e200cp.controllers.deviceconfigcontrollers.ConfigurationClass.serviceFlag;
import static com.mlt.e200cp.models.MessageFlags.PORT_OPEN;
import static com.mlt.e200cp.models.enums.EmvTransactionType.VOID_PURCHASE_TRANSACTION;
import static com.mlt.e200cp.models.enums.EmvTransactionType.VOID_REFUND_TRANSACTION;
import static com.mlt.e200cp.utilities.helper.util.Logger.LINE_OUT;
import static com.mlt.e200cp.utilities.helper.util.Logger.log;
import static com.mlt.e200cp.utilities.helper.util.Utility.appendLog;
import static com.mlt.e200cp.utilities.helper.util.Utility.getTranasctionDateAndTime;
import static com.mlt.e200cp.utilities.helper.util.Utility.txn_type;


public class BaseClass implements PosSequenceInterface, ViewInterface {

    private AppCompatActivity compatActivity;
    public static BaseClass baseClass;
//    private ProgressDialog progDialog;
    private AlertDialog progDialog;
    private DialogFragment fragment;
    private String txnAmount;
    private String transactionID;
    public GetTransactionDetails details;
    public static boolean flagChipOrTap = false;
    TransactionDoneCallback callback;

    private BaseClass(AppCompatActivity appCompatActivity, String amount, int timeOut, PosDetails posDetails, boolean isIntergrated, String data, TransactionDoneCallback callback) {
        this.compatActivity = appCompatActivity;
        this.txnAmount = amount;
        this.transactionID = data;
        serviceFlag = "Stand alone";
        this.callback = callback;

        if(txn_type.equals(VOID_PURCHASE_TRANSACTION)||txn_type.equals(VOID_REFUND_TRANSACTION)){
            details = new GetTransactionDetails();
            details.setTransactionInitiationDateAndTime(getTranasctionDateAndTime().replace("T"," ").replace("Z",""));
            RefundAndVoidServices.getInstance(details,appCompatActivity,posDetails,this).initiateParams();
        }else{
            SequencyHandler.getInstance(PORT_OPEN,this).execute(appCompatActivity,this,amount,timeOut, posDetails,isIntergrated);
        }

//        PresenterClasses.callEMVISO8583(this,appCompatActivity,this,amount,timeOut,posDetails,isIntergrated);
    }

    public static BaseClass getInstance(AppCompatActivity appCompatActivity, String amount, int timeOut, PosDetails posDetails, boolean isIntergrated, @Nullable Object data, TransactionDoneCallback callback){
        if(baseClass!=null){
           baseClass =null;
            System.gc();
        }
        baseClass = new BaseClass(appCompatActivity,amount,timeOut,posDetails,isIntergrated,(String)data,callback);
        return baseClass;
    }

    @Override
    public void onCardInserted() {
        if(serviceFlag.equalsIgnoreCase("Integrated")){
            PresenterClasses.sendState(TxnState.CARD_INSERTED.label);
        }
        log("card inserted",LINE_OUT());

        compatActivity.runOnUiThread(()->{
            progDialog = Common.setAvi(new AlertDialog.Builder(compatActivity).create(),compatActivity);
//            progDialog = ProgressDialog.show(compatActivity,"Processing ...","");
        });

    }

    @Override
    public void onCardTapped() {
        log("card tapped",LINE_OUT());
    }

    @Override
    public void onCardSwipped() {
        log("card swipped",LINE_OUT());
    }

    @Override
    public void onPortOpened() {
        if(serviceFlag.equalsIgnoreCase("Integrated")){
            PresenterClasses.sendState(TxnState.ENTER_CARD.label);
        }
        dismissDialog(fragment);
        fragment = new PortsOpened();
        addFragment(fragment,"PortsOpened");

    }

    @Override
    public void onChipFallBack() {
        addFragment(new ChipFallBack(),"ChipFallBack");
    }

    @Override
    public void selectApp(CharSequence[] args, int Timeout) {
        new Handler(Looper.getMainLooper()).post(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(compatActivity);
            builder.setCancelable(false);
            builder.setTitle("Select Application");
            builder.setItems(args, (dialog, which) -> {
                HelperEMVClass.appSelIndex = which;
                HelperEMVClass.waitFlag = false;
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.cancel();
                HelperEMVClass.waitFlag = false;
            });

            final AlertDialog dialog = builder.create();
            dialog.show();

            final Handler handler  = new Handler();
            final Runnable runnable = () -> {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                HelperEMVClass.waitFlag = false;
            };

            dialog.setOnDismissListener(dialog1 -> {
                handler.removeCallbacks(runnable);
                HelperEMVClass.waitFlag = false;
            });

            handler.postDelayed(runnable, 10000);
        });
    }

    @Override
    public void onSwipeFallBack() {
        dismissDialog(fragment);
        new Handler(Looper.getMainLooper()).post(()->{
            Toast.makeText(compatActivity,"Not supported",Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onStartPinEntry() {

        dismissDialog(fragment);
        dismissDialog(progDialog);

        if(serviceFlag.equalsIgnoreCase("Integrated")){
            PresenterClasses.sendState(TxnState.PIN_ENTERED.label);
        }

        fragment = new PinEntryScreen();
        addFragment(fragment,"PinEntryScreen");
    }

    @Override
    public void onPromptCheckPhone() {

        new Handler(Looper.getMainLooper()).post(()->{

            AlertDialog dialog = new AlertDialog.Builder(compatActivity)
                    .setMessage("Please check phone...")
                    .setCancelable(false)
                    .setNegativeButton("Ok",(dialog1, which) -> {
                        HelperEMVClass.checkFlag = true;
                        dialog1.dismiss();
                    }).create();
            dialog.show();

            final Handler mHandler = new Handler();
            final Runnable mRunnable = new Runnable() {
                @Override
                public void run() {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    HelperEMVClass.waitFlag = false;
                }
            };
            dialog.setOnDismissListener(dialog1 -> {
                mHandler.removeCallbacks(mRunnable);
                HelperEMVClass.waitFlag = false;
            });

            mHandler.postDelayed(mRunnable,20000);
        });

    }

    @Override
    public void onStartProcessing() {
        dismissDialog(progDialog);
        if(serviceFlag.equalsIgnoreCase("Integrated")){
            PresenterClasses.sendState(TxnState.PROC_ONLINE.label);
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                //Todo stop timer if any
                dismissDialog(pinEntryScreen);
                dismissDialog(progDialog);
                progDialog = Common.setAvi(new AlertDialog.Builder(compatActivity).create(),compatActivity);
//                progDialog = ProgressDialog.show(compatActivity,"Processing ...","");
                dismissDialog(fragment);
            }
        });

    }

    @Override
    public void onTransactionSuccessful(ISOPaymentResponse response) {
        //Todo Call Reciept function
        Log.d("TransactionSuccess",response.getResponseMessage());
        dismissDialog(progDialog);
//        fragment = new TxnError("Successful");
//        addFragment(fragment,"Error");
        onTransactionEnded("Success",response);

    }



    @Override
    public void onTransactionEnded(String ErrorMessage,ISOPaymentResponse response) {
        dismissDialog(progDialog);
        dismissDialog(fragment);
        if (response==null){
            callback.onfailure(ErrorMessage);
        }else {
            callback.onSuccess(response);
        }
    }

    @Override
    public void removeCardDisplayed() {
        if(serviceFlag.equalsIgnoreCase("Integrated")){
            PresenterClasses.sendState(TxnState.RMV_CRD_DSP.label);
        }
    }

    @Override
    public void onTrasactionError(String error) {
        dismissDialog(progDialog);
        dismissDialog(fragment);

        callback.onfailure(error);
    }

    @Override
    public void onTrasactionSvcError(String error, ISOPaymentResponse response) {
        dismissDialog(progDialog);
        dismissDialog(fragment);
        callback.onSuccess(response);
    }

    @Override
    public void setActionTable(String[] actionList) {

    }

    @Override
    public void addLog(String msg) {

    }

    private void dismissDialog(ProgressDialog dialog){
        if(dialog!=null){
            if(dialog.isShowing()){
                dialog.dismiss();
            }
        }
    }

    private void dismissDialog(AlertDialog dialog){
        if(dialog!=null){
            if(dialog.isShowing()){
                dialog.dismiss();
            }
        }
    }

    private void addFragment(DialogFragment fragment, String name){
        compatActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                compatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.mainview,fragment).addToBackStack(name).commit();
                fragment.setCancelable(false);
                fragment.show(compatActivity.getSupportFragmentManager(),name);
            }
        });
    }

    private void dismissDialog(DialogFragment dialog){
        try{
            if(dialog!=null){
                dialog.dismiss();
            }
        }catch (Exception ex){
            ex.printStackTrace();
            appendLog(ex.getLocalizedMessage());
        }
    }

    public void addFragment(Fragment fragment, String name) {
        compatActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, fragment)
                .addToBackStack(null)
                .commit();
    }
}
