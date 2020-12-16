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
import com.mlt.e200cp.utilities.helper.util.PreferenceConnector;
import static com.mlt.dtc.utility.Constant.TSTripIdKey;


public class ChipFallBack extends DialogFragment implements View.OnClickListener {

    CountDownTimer countDownTimer;
    TextView timer;
    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_emviso8583,container,false);
        initView();
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        countDownTimer = Common.SetCountDownTimer(21000,1000,timer,this);
    }

    @Override
    public void onPause() {
        super.onPause();
        countDownTimer.cancel();
    }

    private void initView(){
        timer = view.findViewById(R.id.timer);
        ((TextView) view.findViewById(R.id.actionTitle)).setText("TripID: "+ PreferenceConnector.readString(getContext(),TSTripIdKey,""));
        ((TextView)view.findViewById(R.id.actionType)).setText("Use Chip.");
//            ((TextView)view.findViewById(R.id.actionTitle)).setText("FallBack to chip.");
        ((ImageView)view.findViewById(R.id.cancel_action)).setVisibility(View.GONE);
        ((TextView)view.findViewById(R.id.yellowbutton)).setVisibility(View.GONE);
        ((Button)view.findViewById(R.id.paynow_btn)).setVisibility(View.GONE);
        Glide.with(getContext()).asGif().load(R.drawable.insertcard).into((ImageView)view.findViewById(R.id.gifImageView));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }
}
