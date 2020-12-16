package com.mlt.dtc.ISO_Payment.satefragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import com.mlt.dtc.R;
import com.mlt.dtc.common.Common;
import com.mlt.e200cp.utilities.helper.util.PreferenceConnector;
import static com.mlt.dtc.utility.Constant.TSTripIdKey;


public class PaymentSuccessfull extends DialogFragment implements View.OnClickListener {


    CountDownTimer countDownTimer;
    TextView timer;
    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.payment_successful_ctx,container,false);
        initView();
        return view;
    }

    private void initView(){
        timer = view.findViewById(R.id.timer);
        ((TextView) view.findViewById(R.id.tripID)).setText("TripID: "+ PreferenceConnector.readString(getContext(),TSTripIdKey,""));
        ((Button)view.findViewById(R.id.home_btn1)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.home_btn2)).setVisibility(View.GONE);
        ((LinearLayout)view.findViewById(R.id.separator)).setVisibility(View.GONE);
    }

    private void addFragment(Fragment fragment, String name){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content,fragment).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        countDownTimer = Common.SetCountDownTimer(15000,1000,timer,this);
    }

    @Override
    public void onPause() {
        super.onPause();
        countDownTimer.cancel();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home_btn1:
                dismiss();
                break;
        }
    }
}
