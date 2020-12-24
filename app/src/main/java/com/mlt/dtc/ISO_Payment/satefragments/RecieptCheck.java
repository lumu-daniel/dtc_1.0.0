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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import com.mlt.dtc.ISO_Payment.BaseClass;
import com.mlt.dtc.R;
import com.mlt.dtc.common.Common;
import com.mlt.dtc.utility.Constant;
import com.mlt.e200cp.interfaces.TransactionDoneCallback;
import com.mlt.e200cp.models.PosDetails;
import com.mlt.e200cp.utilities.helper.util.PreferenceConnector;
import static com.mlt.dtc.utility.Constant.TSTripIdKey;


public class RecieptCheck extends DialogFragment implements View.OnClickListener {

    private TransactionDoneCallback callback;
    private PosDetails posDetails;
    private String TripId;

    public RecieptCheck(TransactionDoneCallback callback,PosDetails posDetails) {
        this.callback = callback;
        this.posDetails = posDetails ;
    }

    CountDownTimer countDownTimer;
    TextView timer;
    View view;
    Button btn_yes,btn_no;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.payment_successful_ctx,container,false);

        initView();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        countDownTimer = Common.SetCountDownTimer(31000,1000,timer,this);

    }

    @Override
    public void onPause() {
        super.onPause();
        if(countDownTimer!=null){
            countDownTimer.cancel();
        }
    }

    private void initView() {
        btn_yes = (Button)view.findViewById(R.id.btn_yes);
        btn_no = (Button)view.findViewById(R.id.btn_no);
        timer = (TextView)view.findViewById(R.id.timer);
        btn_yes.setText(R.string.yes);
        btn_no.setText(R.string.no);
        ((ImageView)view.findViewById(R.id.cancel_action)).setOnClickListener(this);
        ((TextView) view.findViewById(R.id.tripID)).setText("TripID: "+ PreferenceConnector.readString(getContext(),TSTripIdKey,""));
        ((TextView) view.findViewById(R.id.thank_you)).setVisibility(View.INVISIBLE);
        ((TextView) view.findViewById(R.id.ctx_msg)).setText(R.string.want_a_reciept);
        btn_yes.setOnClickListener(this);
        btn_no.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btn_yes: //YES - add email
                dismiss();
                addFragment(new AddEmail(callback,posDetails),"AddEmail");
                break;

            case R.id.btn_no: //No - tap card/insert
                String Fare = PreferenceConnector.readString(getContext(), Constant.Fare, "");
                if(Fare.contains(".")){
                    Fare = Fare.replace(".","");
                }
                dismiss();
                BaseClass.getInstance((AppCompatActivity) getContext(),posDetails.getTxnAmt(),20000,posDetails,true,null,callback);
                break;

            case R.id.cancel_action:
                dismiss();
                break;

        }
    }

    private PosDetails setPosDetails(){
        PosDetails details = new PosDetails();
        details.setCustomerAddress("Dubai PLot");
        details.setCustomerCity("Dubai");
        details.setCustomerContactNumber("0527000000");
        details.setCustomerCountry("UAE");
        details.setCustomerEmail("");
        details.setHostDeviceName("ICounter");
        details.setLanguage("English");
        details.setRequestingApplicationID("2");
        details.setReversalReason("Card Removed");
        details.setServiceID("110");
        details.setServiceName("Test Name");
        details.setServiceNameAR("Test Name");
        details.setSignatureFields("ServiceCode,TerminalID,TimeStamp");
        details.setTransactionCurreny("784");
        details.setSourceApplication("1");
        details.setUserName("");
        details.setTransactionReferenceNumber("MLT_EC200CP_001900_002");
        details.setTransactionType("Purchase");
        details.setpOSDeviceName("MLT_EC200CP");
        return details;
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
