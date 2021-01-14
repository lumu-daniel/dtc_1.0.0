package com.mlt.dtc.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.mlt.dtc.ISO_Payment.BaseClass;
import com.mlt.dtc.ISO_Payment.satefragments.PaymentSuccessfull;
import com.mlt.dtc.ISO_Payment.satefragments.RecieptCheck;
import com.mlt.dtc.ISO_Payment.satefragments.TxnError;
import com.mlt.dtc.MainApp;
import com.mlt.dtc.R;
import com.mlt.dtc.activity.MainActivity;
import com.mlt.dtc.common.Common;
import com.mlt.dtc.common.PreferenceConnector;
import com.mlt.dtc.interfaces.Dialogdismisslistener;
import com.mlt.dtc.interfaces.FareAfterPaymentListener;
import com.mlt.dtc.interfaces.FireBaseNotifiers;
import com.mlt.dtc.interfaces.OverwriteTripFragmentListener;
import com.mlt.dtc.location.GetReverseGeoCoding;
import com.mlt.dtc.model.PushDetails;
import com.mlt.dtc.utility.Constant;
import com.mlt.e200cp.interfaces.TransactionDoneCallback;
import com.mlt.e200cp.models.EmvTransactionType;
import com.mlt.e200cp.models.PosDetails;
import com.mlt.e200cp.models.repository.response.ISOPaymentResponse;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;
import static com.mlt.dtc.ISO_Payment.BaseClass.baseClass;
import static com.mlt.dtc.MainApp.internetCheck;
import static com.mlt.dtc.MainApp.mainApp;
import static com.mlt.dtc.activity.MainFragment.mainActivity;
import static com.mlt.dtc.common.Common.getAddressFromLocation;
import static com.mlt.e200cp.utilities.helper.util.Utility.txn_type;

public class TripFragment extends DialogFragment implements FareAfterPaymentListener {

    private View view;
    private ImageView iv_close;
    private TextView tv_dest_start_Address,tv_dest_end_Address,tv_duration,tv_fare,tv_start_fare,tv_start_DateTime,tv_kilometers,tv_end_DateTime;
    private Button btn_Pay;
    private PushDetails pushDetails;
    private GetReverseGeoCoding reverseGeoCoding;
    private Handler handler;
    private HandlerThread handlerThread;
    private AlertDialog customDialogMain;

    public TripFragment(PushDetails pushDetails1) {
        this.pushDetails = pushDetails1;
    }

    @Override
    public void FareAfterPaymentCallBackMethod(Context context, String IsPaid) {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        view = getActivity().getLayoutInflater().inflate(R.layout.farefragment, null);
        customDialogMain = new AlertDialog.Builder(getActivity(), R.style.CustomDialog).create();
        reverseGeoCoding = new GetReverseGeoCoding();
        handlerThread = new HandlerThread("EndTrip");
        importViews();
        return customDialogMain;
    }

    private void importViews(){
        iv_close = view.findViewById(R.id.iv_closefarefragment);
        tv_dest_start_Address = view.findViewById(R.id.tv_dest_start_address);
        tv_dest_end_Address = view.findViewById(R.id.tv_dest_end_address);
        tv_duration = view.findViewById(R.id.tv_duration);
        tv_fare = view.findViewById(R.id.tv_fare);
        tv_start_fare = view.findViewById(R.id.start_fare);
        tv_start_DateTime = view.findViewById(R.id.start_datetime);
        tv_kilometers = view.findViewById(R.id.tv_kilometers);
        tv_end_DateTime = view.findViewById(R.id.end_datetime);
        btn_Pay = view.findViewById(R.id.btn_paynow);
        try{
            setViews();
        }catch (Exception exception){
            exception.printStackTrace();
        }
        customDialogMain.setView(view);
    }

    private void setViews()throws Exception{

        customDialogMain.setCanceledOnTouchOutside(false);
        iv_close.setOnClickListener(v -> {
            mainActivity.onFinished("finish");
            dismiss();
        });


        btn_Pay.setOnClickListener(v -> {
            mainActivity.onFinished("finish");

            boolean isNull = false;
            try {

                if (!internetCheck) {
                    Toast.makeText(getContext(), "Sorry for the inconvenience, system is unavailable", Toast.LENGTH_LONG).show();
                } else {

                    if (pushDetails.getFare() == null || pushDetails.getFare().equals("") || pushDetails.getFare().equals("0")) {
                        Toast.makeText(getContext(), "Fare Cannot be zero", Toast.LENGTH_LONG).show();
                    } else {

//                        if (getActivity().getLocalClassName().equals("rtaservices.RTAMainActivity")){
//                            Intent intent=new Intent(getActivity(), DTCFareActivity.class);
//                            context.startActivity(intent);
//                        }else {
//                        mFragment = new PayNowFragmnent();
//                        addFragment(mFragment, "PayNowFragmnent");
//                        }

                        PosDetails posDetails = setPosDetails(pushDetails.getTripId(),pushDetails.getFare());

                        txn_type= EmvTransactionType.PURCHASE_TRANSACTION;

                        addFragment(new RecieptCheck(new TransactionDoneCallback() {
                            @Override
                            public void onSuccess(ISOPaymentResponse response) {
                                Log.d(TAG, "ISOPaymentResponse: "+response);
//                                    Log.d("NewNewResponse",response.getTransactionDetailsData().toString());

                                if(response.getTransactionDetailsData()!=null && response.getTransactionDetailsData().getResponseCode().equals("00"))
                                {

                                    PreferenceConnector.writeBoolean(mainApp,Constant.PaymentStatus,true);
                                    //Success
                                    Log.d("FragmentSuccess","Fragment Success");

                                    addFragment(new PaymentSuccessfull(), "PayNowFragmnent");

                                }

                                if(response.getTransactionDetailsData().getResponseCode().equals("104") || response.getTransactionDetailsData().getResponseCode().equals("91"))
                                {
                                    //Move To PG
                                    swipeCard();
                                }
                                else
                                {
                                    //Error
                                    Log.d("ISO_Payment",response.getTransactionDetailsData().getResponseCode());

//                                    Toast.makeText(compatActivity, "Payment Failed", Toast.LENGTH_SHORT).show();
                                }


                            }

                            @Override
                            public void onfailure(String error) {
                                PreferenceConnector.writeBoolean(mainApp,Constant.PaymentStatus,false);
                                Log.e("NewNewResponse",error);/*
                                if (error.equals("Restart Activity")){
                                    getActivity().startActivity(new Intent(getContext(), MainActivity.class));
                                    return;
                                }*/
                                if (error.equals("Transaction reversed.")||
                                        error.equals("Operation Cancelled...")||
                                        error.equals("Pin timed out.")||
                                        error.equals("Timed out.")||
                                        error.equals("Pin error.")||
                                        error.equals("Restart Activity")||
                                        error.equals("Error in magnetic stripe reading")||
                                        error.equals("Card removed.")){
                                    addFragment(new TxnError(error),"");
                                }else {
                                    if (!error.equalsIgnoreCase("Reader time out") && !error.equalsIgnoreCase("Failed to read card.")) {
                                        swipeCard();
                                    }else {
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getContext(), "Transaction Error Please Try Again", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }

                            }
                        },posDetails),"RecieptCheck");


                        final Timer t = new Timer();
                        t.schedule(new TimerTask() {
                            public void run() {
                                dismiss();// when the task active then close the dialog
                                t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                            }
                        }, 100);
                    }
                }
            } catch (Exception npe) {
                isNull = true;
            }
        });
        if(pushDetails!=null){
            if (pushDetails.getEventCode().equals(Constant.TripStartEventCode)) {
                PreferenceConnector.writeBoolean(getContext(), Constant.PaymentStatus, false);
                reverseGeoCoding.getAddress(Double.parseDouble(pushDetails.getStartlatitude()), Double.parseDouble(pushDetails.getStartlongitude()));
                pushDetails.setTripStartAddress(reverseGeoCoding.getAddress1());
                ;
                tv_dest_start_Address.setText(pushDetails.getTripStartAddress());
                tv_duration.setText("");
                tv_fare.setText(0 + " AED");
                tv_start_fare.setText(pushDetails.getFlagfall() + " AED");
                setTime();
                //Trip End
            } else if (pushDetails.getEventCode().equals(Constant.TripEndEventCode)) {
                PreferenceConnector.writeString(getContext(), Constant.Fare, pushDetails.getFare());
                if (PreferenceConnector.readBoolean(getContext(), Constant.PaymentStatus, false)){
                    btn_Pay.setVisibility(View.INVISIBLE);
                }else {
                    btn_Pay.setVisibility(View.VISIBLE);
                    btn_Pay.setClickable(true);
                }

                tv_dest_start_Address.setText(pushDetails.getTripStartAddress());

                handlerThread.start();
                handler = new Handler(handlerThread.getLooper());
                handler.post(runnable);
                String stringKilometers = "";
                if(pushDetails.getEnddatetime()!=null){
                    float myKilometers = Common.DistanceBetween(Float.parseFloat(pushDetails.getStartlatitude()), Float.parseFloat(pushDetails.getStartlongitude()), Float.parseFloat(pushDetails.getEndlatitude()), Float.parseFloat(pushDetails.getEndlongitude()));
                    stringKilometers = String.format(Locale.ENGLISH, "%.2f", myKilometers);
                }

                tv_kilometers.setText(stringKilometers + " Km(s)");

                tv_fare.setText(pushDetails.getFare() + " AED");

                setTime();
                tv_start_fare.setText(pushDetails.getFlagfall() + " AED");
                if(pushDetails.getEnddatetime()!=null){
                    tv_duration.setText(Common.GetTimeDifference(pushDetails.getStartdatetime(), pushDetails.getEnddatetime()));
                }
            }
        }
    }

    //Handler
    private static class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            if (message.what == 1) {
                Bundle bundle = message.getData();
                locationAddress = bundle.getString("address");
            } else {
                locationAddress = null;
            }
        }
    }

    Date dateendtime;
    private void setTime(){
        try {
            DateFormat f = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
            Date datestarttime = f.parse(pushDetails.getStartdatetime());
            if(pushDetails.getEnddatetime()!=null){
                dateendtime  = f.parse(pushDetails.getEnddatetime());
            }
            DateFormat date = new SimpleDateFormat("MM/dd/yyyy");
            DateFormat time = new SimpleDateFormat("hh:mm:ss a");

            tv_start_DateTime.setText(date.format(datestarttime) + "\n" + time.format(datestarttime));
            if(pushDetails.getEnddatetime()!=null){
                tv_end_DateTime.setText(date.format(dateendtime) + "\n" + time.format(dateendtime));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private final Runnable runnable = () -> {

        if(pushDetails.getEnddatetime()!=null){
            reverseGeoCoding.getAddress(Double.parseDouble(pushDetails.getEndlatitude()), Double.parseDouble(pushDetails.getEndlongitude()));
        }
        new Handler(Looper.getMainLooper()).post(()->{
            if(pushDetails.getEnddatetime()!=null){
                tv_dest_end_Address.setText(reverseGeoCoding.getAddress1());
            }
        });

    };

    @Override
    public void onStart() {
        super.onStart();
        onResume();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Window window = getDialog().getWindow();
        window.setLayout(1000, 700);
        window.setGravity(Gravity.CENTER);
    }


    private PosDetails setPosDetails( String txnId,String amount){
        PosDetails details = new PosDetails();

        if (!amount.contains(".")){
            amount +=".00";
        }else{
            if(amount.substring(amount.indexOf(".")).length()<2){
                amount+="0";
            }
        }

        details.setCustomerAddress("Dubai PLot");
        details.setTxnAmt(StringUtils.leftPad(String.valueOf(amount.replace(".", "")), 12, '0'));
        details.setCustomerCity("Dubai");
        details.setCustomerContactNumber("0527000000");
        details.setCustomerCountry("UAE");
        details.setHostDeviceName("ICounter");
        details.setLanguage("English");
        details.setRequestingApplicationID("2");
        details.setReversalReason("");
        details.setServiceID("110");
        details.setServiceName("Test Name");
        details.setServiceNameAR("Test Name");
        details.setSignatureFields("ServiceCode,TerminalID,TimeStamp");
        details.setTransactionCurreny("784");
        details.setSourceApplication("1");
        details.setUserName("Test User");
        details.setTransactionReferenceNumber(txnId);
        details.setTransactionType("Purchase");
        details.setpOSDeviceName("MLT_EC200CP");

        return details;
    }

    private void addFragment(DialogFragment fragment, String name) {
        new Handler(Looper.getMainLooper()).post(()->{
            fragment.setCancelable(false);
            if(getActivity()!=null){
                fragment.show(getActivity().getSupportFragmentManager(), name);
            }else{
                fragment.show(baseClass.compatActivity.getSupportFragmentManager(), name);
            }
        });

    }

    private void swipeCard(){
        Fragment mFragment =SwipeCard.newInstance();
        baseClass.compatActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainViewLinear, mFragment)
                .addToBackStack(null)
                .commit();
    }
}
