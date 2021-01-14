package com.mlt.dtc.ISO_Payment.satefragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import com.mlt.dtc.ISO_Payment.BaseClass;
import com.mlt.dtc.R;
import com.mlt.dtc.common.Common;
import com.mlt.dtc.utility.Constant;
import com.mlt.e200cp.interfaces.TransactionDoneCallback;
import com.mlt.e200cp.models.PosDetails;
import com.mlt.e200cp.utilities.helper.util.PreferenceConnector;

import static com.mlt.dtc.common.Common.isEmailValid;
import static com.mlt.dtc.utility.Constant.TSTripIdKey;


public class AddEmail extends DialogFragment implements View.OnClickListener {

    private TransactionDoneCallback callback;
    private PosDetails posDetails;
    View view;
    CountDownTimer countDownTimer;
    TextView timer;
    EditText ctx_email;
    Button submit;

    public AddEmail(TransactionDoneCallback callback,PosDetails posDetails) {
        this.callback = callback;
        this.posDetails = posDetails;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.add_email,container,false);
        initView();
        return view;
    }

    private void initView() {

        timer = (TextView)view.findViewById(R.id.timer);
        submit = (Button) view.findViewById(R.id.submit);
        ctx_email = (EditText)view.findViewById(R.id.ctx_email);
        ((ImageView)view.findViewById(R.id.cancel_action)).setOnClickListener(this);
        submit.setOnClickListener(this);
        ((TextView) view.findViewById(R.id.tripID)).setText("TripID: "+ PreferenceConnector.readString(getContext(),TSTripIdKey,""));

    }

    @Override
    public void onStart() {
        super.onStart();
        countDownTimer = Common.SetCountDownTimer(61000,1000,timer,this);
        ctx_email.addTextChangedListener(Common.getTextWatcher(countDownTimer));
    }

    @Override
    public void onPause() {
        super.onPause();
        countDownTimer.cancel();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit:
                if(ctx_email.getText()==null||ctx_email.getText().toString().equalsIgnoreCase("")){
                    getActivity().runOnUiThread(()->{
                        Toast.makeText(getContext(),"Please enter email address to proceed.",Toast.LENGTH_LONG).show();
                    });
                    return;
                }else if (!isEmailValid(ctx_email.getText().toString().trim())){
                    getActivity().runOnUiThread(()->{
                        Toast.makeText(getContext(), "Please enter correct email address", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }
                else{
                    dismiss();
                }
                String Fare = PreferenceConnector.readString(getContext(), Constant.Fare, "");
//                PreferenceConnector.RemoveItem(getContext(),Constant.Fare);
                if(Fare.contains(".")){
                    Fare = Fare.replace(".","");
                }
                posDetails.setCustomerEmail(ctx_email.getText().toString().trim());
                BaseClass.getInstance((AppCompatActivity) getActivity(),posDetails.getTxnAmt(),20000,posDetails,true,null,callback);
                break;
            case R.id.cancel_action:
                dismiss();
                break;
        }
    }
}
