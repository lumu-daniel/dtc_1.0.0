package com.mlt.dtc.ISO_Payment.satefragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.mlt.dtc.R;
import com.mlt.dtc.common.Common;
import com.mlt.e200cp.models.response.ISOPaymentResponse;
import com.mlt.e200cp.utilities.helper.util.ISOConstant;
import com.mlt.e200cp.utilities.helper.util.PreferenceConnector;
import static com.mlt.dtc.utility.Constant.TSTripIdKey;
import static com.mlt.e200cp.models.enums.ErrorCenter.CANCEL_TXN;
import static com.mlt.e200cp.utilities.helper.util.ISOConstant.payIso;

public class TxnError extends DialogFragment implements View.OnClickListener {

    private String args;
    View view;
    CountDownTimer countDownTimer;
    public TextView timer,actionType,yellowbutton,redbutton;
    ImageView iv_flag, cancel_action,gifImageView;
    Button paynow_btn;
    FrameLayout btns;
    LinearLayout gif_layout;
//    private CallRegoPrinter regoPrinter;
    ISOPaymentResponse.TransactionDetailsData data;

    public TxnError(String data){
        this.args = data;
    }
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
        if(!args.equalsIgnoreCase("Reversing transaction.")){
            countDownTimer = Common.SetCountDownTimer(15000,1000,timer,this);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(countDownTimer!=null){
            countDownTimer.cancel();
        }
        ISOConstant.calledService = false;

    }

    @Override
    public void onClick(View v) {
        if(countDownTimer!=null){
            countDownTimer.cancel();
        }
        switch (v.getId()){
            case R.id.cancel_action:
                dismiss();
                break;
        }
    }

    private void initView(){
        timer = view.findViewById(R.id.timer);
        cancel_action = view.findViewById(R.id.cancel_action);
        actionType = view.findViewById(R.id.actionType);
        paynow_btn = view.findViewById(R.id.paynow_btn);
        gif_layout = view.findViewById(R.id.gif_layout);
        yellowbutton = view.findViewById(R.id.yellowbutton);
        redbutton = view.findViewById(R.id.redbutton);
        gifImageView = view.findViewById(R.id.gifImageView);
        if(args.equalsIgnoreCase("Reversing transaction.")){
            if(countDownTimer!=null){
                countDownTimer.cancel();
            }
            timer.setVisibility(View.GONE);
            cancel_action.setVisibility(View.GONE);
        }else if(args.equalsIgnoreCase("Transaction reversed.")){
            args = CANCEL_TXN.label;
        }else if(args.equalsIgnoreCase("Successful")){
            args = "Transaction Successful";
        }else{
            cancel_action.setOnClickListener(this);
        }
        actionType.setText(args);
        if(payIso){
            ((TextView) view.findViewById(R.id.actionTitle)).setText(R.string.txn_err);
        }else{
            ((TextView) view.findViewById(R.id.actionTitle)).setText(getString(R.string.tripId)+ PreferenceConnector.readString(getContext(),TSTripIdKey,""));
        }

        paynow_btn.setVisibility(View.GONE);
        gif_layout.setVisibility(View.GONE);
        yellowbutton.setVisibility(View.GONE);
        redbutton.setVisibility(View.GONE);
        Glide.with(getContext()).asGif().load(R.drawable.icounter_nol_tap).into(gifImageView);

    }

    public void addFragment(Fragment fragment, String name) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, fragment)
                .addToBackStack(null)
                .commit();
    }
}
