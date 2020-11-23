package com.mlt.dtc.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.mlt.dtc.R;

import com.mlt.dtc.common.Common;
import com.mlt.dtc.common.PreferenceConnector;
import com.mlt.dtc.utility.Constant;

import static android.content.ContentValues.TAG;
import static com.mlt.dtc.utility.Constant.TSTripIdKey;

public class PayNowFragmnent extends DialogFragment {

    CountDownTimer countDownTimer;
    TextView timer;
    ImageView btnBack;
    Bundle bundle;
    String Fare;
    TextView tv_Fare;
    String ClassName;
    Button paynow_btn;

    public PayNowFragmnent() {
        // Required empty public constructor
    }

    public static PayNowFragmnent newInstance() {
        return new PayNowFragmnent();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dialog_emviso8583, container, false);
        bundle = getArguments();
//        iv_Card = view.findViewById(R.id.iv_card);
        //iv_DigitalPass = (ImageView) view.findViewById(R.id.iv_digitalpass);
        //paynow_btn = view.findViewById(R.id.paynow_btn);
        btnBack = view.findViewById(R.id.cancel_action);
        //tv_Fare = view.findViewById(R.id.actionType);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        params.setMargins(200,10,200,10);
//        tv_Fare.setLayoutParams(params);
        timer = view.findViewById(R.id.timer);
        //((LinearLayout) view.findViewById(R.id.gif_layout)).setVisibility(View.GONE);
        //((TextView)view.findViewById(R.id.yellowbutton)).setVisibility(View.GONE);
        //((TextView)view.findViewById(R.id.redbutton)).setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.actionTitle)).setText("TripID: "+ PreferenceConnector.readString(getContext(),TSTripIdKey,""));
//        ((ImageView)view.findViewById(R.id.gifImageView)).setVisibility(View.GONE);



        //get Class Name
        ClassName = getClass().getCanonicalName();
        try {
            Fare = PreferenceConnector.readString(getContext(), Constant.Fare, "0");
            if(Fare.contains(".")){
                Fare = Fare.replace('.','-');
                if(Fare.split("-")[1].length()<2){
                    Fare = (Fare+"0").replace('-','.');
                }else if(Fare.split("-")[1].length()>2){
                    Fare = Fare+(Fare.split("-")[1].substring(0,2)).replace('-','.');
                }else{
                    Fare = Fare.replace('-','.');
                }
            }else {
                Fare = Fare+".00";
            }
            tv_Fare.setText(Fare + " AED");
            PreferenceConnector.writeString(getContext(), Constant.Fare, Fare);
        } catch (Exception e) {

        }


        paynow_btn.setOnClickListener(v -> {
            try {
                dismiss();
//                addFragment(new RecieptCheck(),"RecieptCheck");
//                getActivity().startActivity(new Intent(getContext(), DTCPGActivityView.class));
                Toast.makeText(getContext(), "Proceed to payment gateway", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {

            }
        });


        btnBack.setOnClickListener(view1 -> {
            dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        countDownTimer = Common.SetCountDownTimer(61000,1000,timer,this);
    }

    @Override
    public void onPause() {
        super.onPause();
        countDownTimer.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: " );
    }

    private void addFragment(DialogFragment fragment, String name){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                compatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.mainview,fragment).addToBackStack(name).commitAllowingStateLoss();
                fragment.setCancelable(false);
                fragment.show(getActivity().getSupportFragmentManager(),name);
            }
        });
    }
}
