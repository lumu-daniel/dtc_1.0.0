package com.mlt.dtc.ISO_Payment.satefragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.bumptech.glide.Glide;
import com.mlt.dtc.R;
import com.mlt.dtc.common.Common;
import com.mlt.e200cp.controllers.mainlogiccontrollers.HelperEMVClass;
import com.mlt.e200cp.utilities.helper.util.PreferenceConnector;
import static com.mlt.dtc.utility.Constant.TSTripIdKey;


public class PinEntryScreen extends DialogFragment {

    CountDownTimer countDownTimer;
    View view;
    TextView pintv,timer;

    public static PinEntryScreen pinEntryScreen;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_emviso8583,container,false);
        pinEntryScreen = this;
        initView();
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

    private void initView(){
        timer = view.findViewById(R.id.timer);
        pintv = view.findViewById(R.id.actionType);
        pintv.setBackground(getContext().getDrawable(R.drawable.edittextstyle));
        pintv.setText(R.string.please_enter_pin);
        HelperEMVClass.tv_pin = pintv;
        ((TextView) view.findViewById(R.id.actionTitle)).setText(getString(R.string.tripId)+ PreferenceConnector.readString(getContext(),TSTripIdKey,""));
        ((ImageView)view.findViewById(R.id.cancel_action)).setVisibility(View.GONE);
        ((Button)view.findViewById(R.id.paynow_btn)).setVisibility(View.GONE);
        Glide.with(getContext()).asGif().load(R.drawable.pinentry).into((ImageView)view.findViewById(R.id.gifImageView));
    }
}
