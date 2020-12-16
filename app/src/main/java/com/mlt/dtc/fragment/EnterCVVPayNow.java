package com.mlt.dtc.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.mlt.dtc.R;
import com.mlt.dtc.common.Common;
import com.mlt.dtc.utility.ConfigrationDTC;
import com.mlt.dtc.utility.Constant;

public class EnterCVVPayNow extends Fragment {

    public static Fragment mFragment;
    EditText edt_CVV;
    Button btnBackEnterCVV, btn_submitEnterCVV;
    TextView tv_timer;
    private String FirstName, LastName, CardExpiry, CardNumber, CardCVV;
    String ClassName;
    CountDownTimer cTimer;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public static EnterCVVPayNow newInstance() {
        return new EnterCVVPayNow();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        View view = inflater.inflate(R.layout.entercvv, null);
        tv_timer = view.findViewById(R.id.rta_mainServicestimer);
        btnBackEnterCVV = view.findViewById(R.id.btnbackentercvv);
//        btn_submitEnterCVV = view.findViewById(R.id.btn_submitentercvv);
        edt_CVV = view.findViewById(R.id.edt_cvv);


        Bundle bundlefragment = getArguments();
        try {
            //Set Values for Service Call
            FirstName = bundlefragment.getString(ConfigrationDTC.CardFirstName);
            LastName = bundlefragment.getString(ConfigrationDTC.CardLastName);
            CardNumber = bundlefragment.getString(ConfigrationDTC.CardNumber);
            CardExpiry = bundlefragment.getString(ConfigrationDTC.CardExpiry);
        } catch (Exception e) {

        }


        //get Class Name
        ClassName = getClass().getCanonicalName();

        btnBackEnterCVV.setOnClickListener(v ->
                getFragmentManager().popBackStack());
//                getFragmentManager().beginTransaction()
//                .replace(R.id.mainViewLinear, SwipeCard.newInstance())
//                .addToBackStack(null)
//                .commit());

//        edt_CVV.addTextChangedListener(Common.getTextWatcher(cTimer));

        edt_CVV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(cTimer!=null){
                    cTimer.start();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                CardCVV = edt_CVV.getText().toString();
                if (s.length()>=3) {
                    if(cTimer!=null){
                        cTimer.cancel();
                    }
                    //Hide Keyboard
                    @Nullable
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                    if(!CardCVV.equals("")){
                        mFragment = FarePaymentDone.newInstance();
                        bundle.putString(ConfigrationDTC.CardCVV, CardCVV);
                        bundle.putString(ConfigrationDTC.CardFirstName, FirstName);
                        bundle.putString(ConfigrationDTC.CardLastName, LastName);
                        bundle.putString(ConfigrationDTC.CardNumber, CardNumber);
                        bundle.putString(ConfigrationDTC.CardExpiry, CardExpiry);
                        mFragment.setArguments(bundle);
                        addFragment();

                        Constant.paymentDialog = ProgressDialog.show(getContext(),getResources().getString(R.string.progress_titledtc),getResources().getString(R.string.page_is_loadingdtc));

                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }else{
                        Toast.makeText(getContext(),"Enter Cvv", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });



//        btn_submitEnterCVV.setOnClickListener(v -> {
//            if (cTimer!=null){
//                cTimer.cancel();
//            }
//            CardCVV = edt_CVV.getText().toString();
//            if (CardCVV.length() == 3) {
//                //Hide Keyboard
//                @Nullable
//                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
//
//                mFragment = FarePaymentDone.newInstance();
//                bundle.putString(ConfigrationDTC.CardCVV, CardCVV);
//                bundle.putString(ConfigrationDTC.CardFirstName, FirstName);
//                bundle.putString(ConfigrationDTC.CardLastName, LastName);
//                bundle.putString(ConfigrationDTC.CardNumber, CardNumber);
//                bundle.putString(ConfigrationDTC.CardExpiry, CardExpiry);
//                mFragment.setArguments(bundle);
//                addFragment();
//
//                Constant.paymentDialog = ProgressDialog.show(getContext(),getResources().getString(R.string.progress_titledtc),getResources().getString(R.string.page_is_loadingdtc));
//
//                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//
//            } else {
//                Toast.makeText(getContext(), "Please enter correct CVV", Toast.LENGTH_LONG).show();
//            }
//
//        });

        try {
            //Keeping the CVV masked
            edt_CVV.setTransformationMethod(new Common.MyPasswordTransformationMethod());
        } catch (Exception e) {

        }


        return view;
    }

    public void addFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.mainViewLinear, mFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(cTimer!=null){
            cTimer.cancel();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        cTimer = Common.SetCountDownTimerDtc(60000, 1000, tv_timer, SwipeCard.newInstance(), getFragmentManager());
    }
}