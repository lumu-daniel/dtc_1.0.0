package com.mlt.dtc.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.mlt.dtc.R;
import com.mlt.dtc.activity.MainFragment;
import com.mlt.dtc.activity.MainActivity;
import com.mlt.dtc.common.Common;
import com.mlt.dtc.utility.ConfigrationDTC;
import com.mlt.e200cp.controllers.deviceconfigcontrollers.ConfigurationClass;
import com.mlt.e200cp.controllers.mainlogiccontrollers.EmvTransactionPresenter;
import com.mlt.e200cp.controllers.mainlogiccontrollers.HelperEMVClass;
import com.mlt.e200cp.controllers.presenters.PresenterClasses;
import com.mlt.e200cp.interfaces.ResultsCallback;
import com.mlt.e200cp.interfaces.ViewInterface;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import static com.mlt.e200cp.utilities.helper.shell.BaseShell.portManager;

public class SwipeCard extends Fragment implements ViewInterface {

    Fragment mFragment;
    TextView tv_Swipecard, tvProcess, tv_timer;
    Button btnBackSwipeCard;
    ImageView iv_swpiecard_Animation;
    private String Track1Data;
    private String Track2Data;
    String[]  Track2DataArr, GetFullNameArr, getFirstLastName;
    ArrayList<String> Track1DataArr;
    String expirydateStart, expirydateEnd, FirstName, LastName, CardExpiry, CardNumber;
    String ClassName;
    public static String TYPE = "swipe";
    public static Resources resources;
    AlertDialog swipeBuilder;
    PresenterClasses classes;
    private CountDownTimer cTimer;
    ImageView iv_gov_dubai,iv_mltlogo,iv_rta;
    public static SwipeCard swipeCard;
    android.app.AlertDialog dialog;
    Runnable runnable;
    AsyncTask<Object, Object, Object> task;

    public static SwipeCard newInstances () {
        return swipeCard;
    }
    public static SwipeCard activeInstance () {
        return swipeCard;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public static SwipeCard newInstance() {
        return new SwipeCard();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.swipecard, null);
        tv_timer = view.findViewById(R.id.rta_mainServicestimer);
//        tv_Swipecard = view.findViewById(R.id.tv_swipecard);
//        tvProcess = view.findViewById(R.id.tvprocesspaynow);
        btnBackSwipeCard = view.findViewById(R.id.btnbackswipecardpaynow);
        String terminalid= ConfigurationClass.TERMINAL_ID;


        swipeCard = this;
        classes = PresenterClasses.getInstance(getContext(),this);
        runnable = new Runnable() {
            @Override
            public void run() {
                HelperEMVClass.cancelFlag = true;
                portManager.sendData("72".getBytes());
            }
        };

        //get Class Name
        ClassName = getClass().getCanonicalName();

        classes.getDataTrackData(new ResultsCallback() {
            @Override
            public String onResponseSuccess(String s) {
                if (cTimer!=null){
                    cTimer.cancel();
                }

                try {
                    JSONObject obj = new JSONObject(s);
                    String track1 = obj.optString("track1");
                    if (!track1.equals("")) {
                        if (track1.contains("^")) {
                            try {
                                Track1DataArr = new ArrayList<>();
                                Track1Data = track1;
                                //Track2Data = track2;
                                //Track2Data = "B4806660101035938^/TALAL ABSAR SHAIKH AB^20122211185700163000000";
                                //Track2Data = "B4266110035080009^ABSAR/TALAL^21092011145900160000000";
                                //Track2Data = "B4582097101179194^ALI/NAAFII FAHAR^21032011964600682000000";
                                Track1DataArr = new ArrayList<>(Arrays.asList(Common.MySplit(Track1Data, "^")));
                                CardNumber = getCardNumberOnCard(Track1DataArr.get(0));
                                CardExpiry = getExpiryDateOnCard(Track1DataArr.get(2));
                                getFirstLastName = getNameOnCard(Track1DataArr.get(1));
                                if (getFirstLastName.equals(null) || getFirstLastName.equals("")) {
                                    FirstName = getFirstLastName[0];
                                    LastName = getFirstLastName[1];
                                }

                                FirstName = getFirstLastName[0];
                                try{
                                    LastName = getFirstLastName[1];
                                }catch (Exception ex){
                                    ex.printStackTrace();
                                    LastName = FirstName;
                                }
                                if(FirstName.matches(".*[0-9].*")){
                                    getActivity().runOnUiThread(() -> {

                                        if(cTimer!=null){
                                            cTimer.cancel();
                                        }
                                        // Utilities utilities = new Utilities();
                                        //utilities.AlertDialog(getContext(), "Card is not swiped properly,kindly swipe it again", mFragment);
                                        //Class<?> clazz = null;
                                        generateDialog("Card is not supported,kindly use another card.");
                                    });
                                }else{
                                    Bundle bundle = new Bundle();
                                    bundle.putString(ConfigrationDTC.CardFirstName, FirstName);
                                    bundle.putString(ConfigrationDTC.CardLastName, LastName);
                                    bundle.putString(ConfigrationDTC.CardExpiry, CardExpiry);
                                    bundle.putString(ConfigrationDTC.CardNumber, CardNumber);
                                    mFragment = EnterCVVPayNow.newInstance();
                                    mFragment.setArguments(bundle);
                                    addFragment();
                                }

                            } catch (Exception e) {
                                Log.d("myIssue2020", e.getLocalizedMessage());
                                getActivity().runOnUiThread(() -> {

                                    if(cTimer!=null){
                                        cTimer.cancel();
                                    }
                                    // Utilities utilities = new Utilities();
                                    //utilities.AlertDialog(getContext(), "Card is not swiped properly,kindly swipe it again", mFragment);
                                    //Class<?> clazz = null;
                                    generateDialog("Card is not swiped properly,kindly swipe it again.");
                                });

                            }
                    /*} else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (cTimer != null)
                                    cTimer.start();
                                Utilities.AlertDialog(getContext(), "Only Master and Visa cards are accepted");
                                Class<?> clazz = null;
                                try {
                                    clazz = Class.forName("com.xac.demo.Presenter.EmvTransactionPresenter");
                                    presenter = (PresenterInterface) clazz.newInstance();
                                    //Calling CardValueListener interface to get the card values in this class
                                    presenter.initPresenter(getContext(), CardPaymentSwipe.this, CardPaymentSwipe.this);

                                } catch (Exception e) {
                                    if (cTimer != null)
                                        cTimer.start();
                                    ExceptionService.ExceptionLogService(getContext(), e.getMessage(), ClassName, AndroidSerialNo);
                                }
                            }
                        });*/
                        }
                    } else {

                        getActivity().runOnUiThread(() -> {

                            if(cTimer!=null){
                                cTimer.cancel();
                            }
                            // Utilities utilities = new Utilities();
                            //utilities.AlertDialog(getContext(), "Card is not swiped properly,kindly swipe it again", mFragment);
                            //Class<?> clazz = null;
                            try {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setMessage("Card is not swiped properly,kindly swipe it again")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", (dialog, id) -> {
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    HelperEMVClass.cancelFlag = true;
                                                    portManager.sendData("72".getBytes());
                                                }
                                            }.run();
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();

                            } catch (Exception e) {

                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public String onResponseFailure(String s) {
                try {
                    getActivity().runOnUiThread(() -> {

                        if (!getActivity().isFinishing()) {
                            if(s.equalsIgnoreCase("Error3: Auto Report : Cancel")){
                                runnable.run();
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }else{
                                swipeBuilder = new AlertDialog.Builder(getActivity())
                                        .setTitle("Sorry for the inconvienience.")
                                        .setMessage("Please swipe again")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", (dialog, which) -> {
                                            runnable.run();
                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                        }).show();
                            }

                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
//                            ExceptionService.ExceptionLogService(getContext(), e.getMessage(), ClassName, AndroidSerialNo);
                }
                return null;
            }
        },"14");

        btnBackSwipeCard.setOnClickListener(V->{

            runnable.run();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            getActivity().finish();

            Intent intent = new Intent(getContext(), MainActivity.class);
            getActivity().startActivity(intent);

        });

        cTimer = Common.SetCountDownTimerDtc(20000, 1000, tv_timer, MainFragment.getInstance(),getFragmentManager());

        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return view;
    }

    public void addFragment() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainViewLinear, mFragment)
                .addToBackStack(null)
                .commit();
    }

    public void changeFragment(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .replace(R.id.paymentView, fragment)
                .addToBackStack(null)
                .commit();
    }

    public String[] getNameOnCard(String name) {
        name = name.trim();
        String namecharatfirstIndex = name.substring(0, 1);
        String namecharatlastIndex = name.substring(name.length() - 1);
        if (namecharatfirstIndex.equals("/") || namecharatlastIndex.equals("/"))
            name = name.replace("/", "");
        else if (name.contains("/")) {
            GetFullNameArr = Common.MySplit(name, "/");
            name = name.replace("/", " ");
            GetFullNameArr = Common.MySplit(name, " ");
            FirstName = GetFullNameArr[1];
            LastName = GetFullNameArr[GetFullNameArr.length - 1];
            name = FirstName + " " + LastName;
        }
        getFirstLastName = Common.MySplit(name, " ");
        return getFirstLastName;
    }

    public String getCardNumberOnCard(String cardno) {
        cardno = cardno.trim();
        cardno = cardno.substring(1);
        return cardno;
    }

    public String getExpiryDateOnCard(String expirydate) {
        expirydate = expirydate.trim();
        expirydate = expirydate.substring(0, 4);
        expirydateStart = expirydate.substring(0, 2);
        expirydateEnd = expirydate.substring(2, 4);
        expirydate = expirydateEnd + expirydateStart;
        return expirydate;
    }

//    public String getExpiryDateOnCard(String expirydate) {
//        expirydate = expirydate.trim();
//        expirydate = expirydate.substring(0, 4);
//        try {
//            Date prevDOB = new SimpleDateFormat("yyMM").parse(expirydate);
//            expirydate  = new SimpleDateFormat("MMyyyy").format(prevDOB);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        /*expirydateStart = expirydate.substring(0, 2);
//        expirydateEnd = expirydate.substring(2, 4);
//        expirydate = expirydateEnd + expirydateStart;*/
//        return expirydate;
//    }

    @Override
    public void onStart() {
        if (cTimer != null)
            cTimer.start();
        super.onStart();
    }
    @Override
    public void onResume() {
        if (cTimer != null)
            cTimer.start();
        super.onResume();
    }

    @Override
    public void onPause() {

        if (cTimer != null)
            cTimer.cancel();
        super.onPause();
        EmvTransactionPresenter.emvTransactionPresenter.procTaskThread.interrupt();
    }

    @Override
    public void onDestroy() {
        if (cTimer != null)
            cTimer.cancel();
        super.onDestroy();
    }

    @Override
    public void setActionTable(String[] actionList) {

    }

    @Override
    public void addLog(String msg) {

    }

    private void generateDialog(String message){
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, id) -> {
                        new Runnable() {
                            @Override
                            public void run() {
                                HelperEMVClass.cancelFlag = true;
                                portManager.sendData("72".getBytes());
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                getActivity().finish();

                                Intent intent = new Intent(getContext(), MainActivity.class);
                                getActivity().startActivity(intent);

                            }
                        }.run();
                    });
            AlertDialog alert = builder.create();
            alert.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}