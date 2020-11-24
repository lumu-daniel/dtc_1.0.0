package com.mlt.dtc.fragment;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.gson.Gson;
import com.mlt.dtc.R;
import com.mlt.dtc.activity.MainActivity;
import com.mlt.dtc.common.Common;
import com.mlt.dtc.common.PreferenceConnector;
import com.mlt.dtc.interfaces.Dialogdismisslistener;
import com.mlt.dtc.interfaces.FareAfterPaymentListener;
import com.mlt.dtc.interfaces.OverwriteTripFragmentListener;
import com.mlt.dtc.interfaces.ServiceCallback;
import com.mlt.dtc.interfaces.TripStartedForPaymentListener;
import com.mlt.dtc.location.getReverseGeoCoding;
import com.mlt.dtc.model.ClickLogServiceResponseObject;
import com.mlt.dtc.model.ConfigurationServiceResponseArray;
import com.mlt.dtc.model.ConfigurationServiceResponseObject;
import com.mlt.dtc.model.PushDetails;
import com.mlt.dtc.services.ConfigurationService;
import com.mlt.dtc.services.ServiceCallLogService;
import com.mlt.dtc.utility.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;
import static com.mlt.dtc.MainApp.internetCheck;
import static com.mlt.dtc.activity.MainActivity.mainActivity;


public class TripEndFragment extends DialogFragment implements Dialogdismisslistener, OverwriteTripFragmentListener, FareAfterPaymentListener {

    private AlertDialog dialog;

    AlertDialog.Builder customDialogMain;

    Context context;
    DialogFragment mFragment;

    TextView tv_duration, tv_fare, tv_kilometers,
            tv_start_fare, tv_start_DateTime, tv_end_DateTime, tv_dest_start_Address, tv_dest_end_Address, payment_status_reslt, paymentstaus;
    Location startLocation, endLocation;
    PushDetails pushDetails = new PushDetails();
    PushDetails pushDetailsTripStart = null;
    PushDetails pushDetailsTripEnd = null;
    LinkedHashMap<String, String> hashMap = null;
    String TripStartLatitude, TripStartLongitude, TripEndLat, TripEndLong, StartDate, TripTimeDuration,
            TripID, getTripStartAddress, getTripEndAddress, Fare, ErrorMessage;
    Button btn_Pay;
    Bundle bundle;

    ClickLogServiceResponseObject clickLogServiceResponseObject;
    Gson gson = new Gson();
    String ServiceCode, ServiceMessage;
    Boolean TripEndServiceCall;
    ConfigurationServiceResponseArray configurationServiceResponseArray;
    ConfigurationServiceResponseObject configurationServiceResponseObject;
    boolean PaynowCheck;
    String ClassName;
    getReverseGeoCoding reverseGeoCoding;
    ImageView iv_close;
    static private Dialogdismisslistener dialogdismisslistener;
    static private TripStartedForPaymentListener tripStartedForPaymentListener;

    HandlerThread handlerThread;
    private Handler handler;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        context = getContext();

        customDialogMain = new AlertDialog.Builder(getActivity(), R.style.CustomDialog);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.farefragment, null);
        //set the view to the dialog
        customDialogMain.setView(view);

        handlerThread = new HandlerThread("endlocation");
        handlerThread.start();

        handler = new Handler(handlerThread.getLooper());

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

        startLocation = new Location("start");
        endLocation = new Location("end");

        //Show the dialog
        dialog = customDialogMain.show();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    @Override
    public void FareAfterPaymentCallBackMethod(Context context, String IsPaid) {

        //Check the fare is already paid
        if (Constant.PaymentPaidCode.equalsIgnoreCase(IsPaid)) {

            PreferenceConnector.writeString(context, Constant.Fare, "0");


            //Trip Starts and set the value to true
            if (tripStartedForPaymentListener != null) {
                tripStartedForPaymentListener.TripStartedForPaymentCallBackMethod(false);
            }

        } else {
            PreferenceConnector.writeString(context, Constant.Fare, pushDetails.getFare());
        }


    }

    public interface OnCompleteListener {
        void onComplete(final Bundle bundle);
    }

    //Set Call back method tobe called to close the startfragment
    public static void dialogdismissCallBackMethod(Dialogdismisslistener CallBack) {
        dialogdismisslistener = CallBack;
    }

    public static void setTripStartedForPaymentCallBackMethod(TripStartedForPaymentListener CallBack) {
        tripStartedForPaymentListener = CallBack;
    }

   /* public static void setMethodcallbackmultipleViewClick(MultipleViewClickListener callback) {
        multipleViewClickListenercallBack = callback;
    }*/

    private void EmptyString(String emptyString) {
        tv_dest_start_Address.setText(emptyString);
        tv_dest_end_Address.setText(emptyString);
        /*tv_Start_Lat.setText(emptyString);
        tv_Start_Lng.setText(emptyString);
        tv_End_Lat.setText(emptyString);
        tv_End_Lng.setText(emptyString);

         */
        tv_fare.setText(emptyString);
        tv_start_DateTime.setText(emptyString);
        tv_end_DateTime.setText(emptyString);
        tv_start_fare.setText(emptyString);
        tv_duration.setText(emptyString);

    }

    @Override
    public void DialogdismisslistenerBackMethod() {
        try {

            while (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }

            while (dialogdismisslistener != null) {
                dialogdismisslistener.DialogdismisslistenerBackMethod();
                dialogdismisslistener = null;
            }

        } catch (Exception ex) {
            Log.e("TTTT", ex.getLocalizedMessage() + "");
        }
    }

    @Override
    public void OverwriteTripFragmentListenerBackMethod() {
        //Close this dialog when the trip is started again and the previous instance of a dialog in not closed
        try {

            while (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }

            while (dialogdismisslistener != null) {
                dialogdismisslistener.DialogdismisslistenerBackMethod();
                dialogdismisslistener = null;
            }

        } catch (Exception ex) {
            Log.e("TTTT", ex.getLocalizedMessage() + "");
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

    private void addFragment(DialogFragment fragment, String name) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                compatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.mainview,fragment).addToBackStack(name).commitAllowingStateLoss();
                fragment.setCancelable(false);
                fragment.show(getActivity().getSupportFragmentManager(), name);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();


        bundle = getArguments();


        reverseGeoCoding = new getReverseGeoCoding();


        //get class name
        ClassName = getClass().getCanonicalName();

        //Intialize the Fraegment and set the FareAfterPayment call back method
        FarePaymentDone.setFareAfterPaymentCallBackMethod(this);

        //Callbak Method to trigger in the main activity
        //dismiss trip start dialog
        MainActivity.dialogdismissCallBackMethod(this);


        try {
            hashMap = (LinkedHashMap<String, String>) Common.readObject(getContext(), Constant.PushDetailsHashMap);
            pushDetailsTripStart = (PushDetails) Common.readObject(getContext(), Constant.PushDetailsTripStart);
            pushDetailsTripEnd = (PushDetails) Common.readObject(getContext(), Constant.PushDetailsTripEnd);
            PreferenceConnector.writeString(context, Constant.TSTripIdKey, pushDetailsTripEnd.getTripId());

        } catch (IOException | ClassNotFoundException e) {

        }


        try {

            if (hashMap.get(Constant.ErrorMessage) != null) {

                ErrorMessage = hashMap.get(Constant.ErrorMessage);
                Toast.makeText(getContext(), ErrorMessage, Toast.LENGTH_LONG).show();
                //Shows Empty data in textviews
                EmptyString("");

            } else {

                if (hashMap.size() > 0) {
                    if (hashMap.get(Constant.TSEventCodeKey).equals(Constant.TripStartEventCode))//Trip Start
                    {
                        pushDetailsTripStart = new PushDetails();
                        pushDetailsTripStart.setFamilyName(hashMap.get(Constant.TSFamilyNameKey));
                        pushDetailsTripStart.setGivenName(hashMap.get(Constant.TSGivenNameKey));
                        pushDetailsTripStart.setVehicleType(hashMap.get(Constant.TSVehicleTypeKey));
                        pushDetailsTripStart.setPicture(hashMap.get(Constant.TSPictureKey));
                        pushDetailsTripStart.setPlateNo(hashMap.get(Constant.TSPlateNoKey));
                        pushDetailsTripStart.setFlagfall(hashMap.get(Constant.TSFlagFallKey));
                        pushDetailsTripStart.setShiftseq(hashMap.get(Constant.TSShiftSeqKey));
                        pushDetailsTripStart.setDriverId(hashMap.get(Constant.TSDriverIdKey));
                        pushDetailsTripStart.setEventName(hashMap.get(Constant.TSEventNameKey));
                        pushDetailsTripStart.setTripseq(hashMap.get(Constant.TSTripSeqKey));
                        pushDetailsTripStart.setEventCode(hashMap.get(Constant.TSEventCodeKey));
                        pushDetailsTripStart.setFare(hashMap.get(Constant.TSFareKey));
                        pushDetailsTripStart.setNationality(hashMap.get(Constant.TSNationalityKey));
                        pushDetailsTripStart.setEventDescription(hashMap.get(Constant.TSEventDescriptionKey));
                        pushDetailsTripStart.setJobnumber(hashMap.get(Constant.TSJobNumberKey));
                        pushDetailsTripStart.setUsername(hashMap.get(Constant.TSUsernameKey));
                        pushDetailsTripStart.setEventName(hashMap.get(Constant.TSEventNameKey));
                        pushDetailsTripStart.setTripId(hashMap.get(Constant.TSTripIdKey));
                        pushDetailsTripStart.setVehicleType(hashMap.get(Constant.TSVehicleTypeKey));
                        //Trip Start
                        pushDetailsTripStart.setStartdatetime(hashMap.get(Constant.TSStartDateTimeKey));
                        pushDetailsTripStart.setStartlatitude(hashMap.get(Constant.TSStartLatitudeKey));
                        pushDetailsTripStart.setStartlongitude(hashMap.get(Constant.TSStartLongitudeKey));
                        startLocation.setLatitude(Double.parseDouble(hashMap.get(Constant.TSStartLatitudeKey)));
                        startLocation.setLongitude(Double.parseDouble(hashMap.get(Constant.TSStartLongitudeKey)));
                        try {
                            Common.writeObject(getContext(), Constant.PushDetailsTripStart, pushDetailsTripStart);
                        } catch (IOException e) {

                        }
                        pushDetails = pushDetailsTripStart;

                    } else if (hashMap.get(Constant.TSEventCodeKey).equals(Constant.TripEndEventCode))//Trip End
                    {
                        pushDetailsTripEnd = new PushDetails();
                        pushDetailsTripEnd.setFamilyName(hashMap.get(Constant.TSFamilyNameKey));
                        pushDetailsTripEnd.setGivenName(hashMap.get(Constant.TSGivenNameKey));
                        pushDetailsTripEnd.setVehicleType(hashMap.get(Constant.TSVehicleTypeKey));
                        pushDetailsTripEnd.setPicture(hashMap.get(Constant.TSPictureKey));
                        pushDetailsTripEnd.setPlateNo(hashMap.get(Constant.TSPlateNoKey));
                        pushDetailsTripEnd.setFlagfall(hashMap.get(Constant.TSFlagFallKey));
                        pushDetailsTripEnd.setShiftseq(hashMap.get(Constant.TSShiftSeqKey));
                        pushDetailsTripEnd.setDriverId(hashMap.get(Constant.TSDriverIdKey));
                        pushDetailsTripEnd.setEventName(hashMap.get(Constant.TSEventNameKey));
                        pushDetailsTripEnd.setTripseq(hashMap.get(Constant.TSTripSeqKey));
                        pushDetailsTripEnd.setEventCode(hashMap.get(Constant.TSEventCodeKey));
                        pushDetailsTripEnd.setFare(hashMap.get(Constant.TSFareKey));
                        pushDetailsTripEnd.setNationality(hashMap.get(Constant.TSNationalityKey));
                        pushDetailsTripEnd.setEventDescription(hashMap.get(Constant.TSEventDescriptionKey));
                        pushDetailsTripEnd.setJobnumber(hashMap.get(Constant.TSJobNumberKey));
                        pushDetailsTripEnd.setUsername(hashMap.get(Constant.TSUsernameKey));
                        pushDetailsTripEnd.setEventName(hashMap.get(Constant.TSEventNameKey));
                        pushDetailsTripEnd.setTripId(hashMap.get(Constant.TSTripIdKey));
                        pushDetailsTripEnd.setVehicleType(hashMap.get(Constant.TSVehicleTypeKey));
                        //Trip End
                        pushDetailsTripEnd.setEnddatetime(hashMap.get(Constant.TSEndDateTimeKey));
                        pushDetailsTripEnd.setEndlatitude(hashMap.get(Constant.TSEndLatitudeKey));
                        pushDetailsTripEnd.setEndlongitude(hashMap.get(Constant.TSEndlongitudeKey));
                        endLocation.setLatitude(Double.parseDouble(hashMap.get(Constant.TSEndLatitudeKey)));
                        endLocation.setLongitude(Double.parseDouble(hashMap.get(Constant.TSEndlongitudeKey)));

                        try {

                            Common.writeObject(getContext(), Constant.PushDetailsTripEnd, pushDetailsTripEnd);

                        } catch (IOException e) {



                        }
                        pushDetails = pushDetailsTripEnd;

                    }
                    //Trip Start
                    if (hashMap.get(Constant.TSEventCodeKey).equals(Constant.TripStartEventCode)) {
                        getTripStartAddress = PreferenceConnector.readString(getContext(), Constant.TripStartAddress, "");
//                        getTripStartAddress = Common.getAddressFromLocation(Double.parseDouble(pushDetails.getStartlatitude()), Double.parseDouble(pushDetails.getStartlongitude()), getContext(), new GeocoderHandler());
                        tv_dest_start_Address.setText(getTripStartAddress);

                        tv_duration.setText("");
                        tv_fare.setText(0 + " AED");
                        tv_start_fare.setText(pushDetails.getFlagfall() + " AED");

                        try {
                            DateFormat f = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                            Date datestarttime = f.parse(pushDetails.getStartdatetime());
                            Date dateendtime = f.parse(pushDetails.getEnddatetime());
                            DateFormat date = new SimpleDateFormat("MM/dd/yyyy");
                            DateFormat time = new SimpleDateFormat("hh:mm:ss a");

                            tv_start_DateTime.setText(date.format(datestarttime) + "\n" + time.format(datestarttime));

                            tv_end_DateTime.setText(date.format(dateendtime) + "\n" + time.format(dateendtime));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
//                        tv_start_DateTime.setText(pushDetails.getStartdatetime());
//                        tv_end_DateTime.setText(pushDetails.getEnddatetime());
                        //Get this in the string and save it in a cache to show in the trip end
                        TripStartLatitude = pushDetails.getStartlatitude();
                        TripStartLongitude = pushDetails.getStartlongitude();
                        StartDate = pushDetails.getStartdatetime();

                        PreferenceConnector.writeString(getContext(), Constant.TripStartlatitude, TripStartLatitude);
                        PreferenceConnector.writeString(getContext(), Constant.TripStartlongitude, TripStartLongitude);
                        PreferenceConnector.writeString(getContext(), Constant.StartDate, StartDate);
                        //PreferenceConnector.writeString(getContext(), Constant.Fare, Fare);
                        //btn_Pay.setVisibility(View.GONE);

                        //Trip End
                    } else if (hashMap.get(Constant.TSEventCodeKey).equals(Constant.TripEndEventCode)) {
                        TripStartLatitude = PreferenceConnector.readString(getContext(), Constant.TripStartlatitude, null);
                        TripStartLongitude = PreferenceConnector.readString(getContext(), Constant.TripStartlongitude, null);
                        getTripStartAddress = PreferenceConnector.readString(getContext(), Constant.TripStartAddress, "");
                        //getTripStartAddress = Common.getAddressFromLocation(Double.parseDouble(TripStartLatitude), Double.parseDouble(TripStartLongitude), getContext(), new GeocoderHandler());
                        //getTripEndAddress = Common.getAddressFromLocation(Double.parseDouble(pushDetails.getEndlatitude()), Double.parseDouble(pushDetails.getEndlatitude()), getContext(), new GeocoderHandler());
                        StartDate = PreferenceConnector.readString(getContext(), Constant.StartDate, null);
                        //Fare = pushDetails.getFare();
                        if (TripStartFragment.tripStarted) {
                            PreferenceConnector.writeString(getContext(), Constant.Fare, pushDetails.getFare());
                        }

                        TripEndLat = pushDetails.getEndlatitude();
                        TripEndLong = pushDetails.getEndlongitude();

                        if (PreferenceConnector.readBoolean(getContext(), Constant.PaymentStatus, true)){
//                            paymentstaus.setText("PAID");
                            btn_Pay.setVisibility(View.INVISIBLE);
                            btn_Pay.setEnabled(false);

                        }else {

//                            paymentstaus.setText("PENDING");
                            btn_Pay.setVisibility(View.VISIBLE);
                            btn_Pay.setClickable(true);
                        }
//                        btn_Pay.setVisibility(View.GONE);
                        tv_dest_start_Address.setText(getTripStartAddress);
//                        reverseGeoCoding.getAddress(Double.parseDouble(TripEndLat), Double.parseDouble(TripEndLong));
//                        getTripEndAddress = reverseGeoCoding.getAddress1();
                        handler.post(runnable);

//                        tv_dest_start_Address.setText(getTripStartAddress);
//                        tv_dest_end_Address.setText(getTripEndAddress);


                        float myKilometers = Common.DistanceBetween(Float.parseFloat(TripStartLatitude), Float.parseFloat(TripStartLongitude), Float.parseFloat(TripEndLat), Float.parseFloat(TripEndLong));
                        String stringKilometers = String.format(Locale.ENGLISH, "%.2f", myKilometers);
                        tv_kilometers.setText(stringKilometers + " Km(s)");

                        tv_fare.setText(pushDetails.getFare() + " AED");

                        try {
                            DateFormat f = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                            Date datestarttime = f.parse(StartDate);
                            Date dateendtime = f.parse(pushDetails.getEnddatetime());
                            DateFormat date = new SimpleDateFormat("MM/dd/yyyy");
                            DateFormat time = new SimpleDateFormat("hh:mm:ss a");

                            tv_start_DateTime.setText(date.format(datestarttime) + "\n" + time.format(datestarttime));

                            tv_end_DateTime.setText(date.format(dateendtime) + "\n" + time.format(dateendtime));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
//                        tv_start_DateTime.setText(StartDate);
//                        tv_end_DateTime.setText(pushDetails.getEnddatetime());
                        tv_start_fare.setText(pushDetails.getFlagfall() + " AED");
                        TripTimeDuration = Common.GetTimeDifference(StartDate, pushDetails.getEnddatetime());
                        tv_duration.setText(TripTimeDuration);
                        //btn_Pay.setVisibility(View.VISIBLE);
                        //PreferenceConnector.writeString(getContext(), Constant.Fare, Fare);


                    }
                }
            }
        } catch (Exception e) {

        }
        try {
            TripEndServiceCall = bundle.getBoolean(Constant.TripEndServiceCall);
        } catch (Exception e) {

        }

        //Calling the service to log this service
        ServiceCallLogService serviceCallLogService = new ServiceCallLogService(getContext());
        serviceCallLogService.CallServiceCallLogService(Constant.nameFare, Constant.name_Fare_details_desc);

        if (TripEndServiceCall) {

            try {

                if (hashMap.get(Constant.EventCodeLog).equals(Constant.TripEndEventCode)) {
//                    try {
//                        ArrayList<CKeyValuePair> clickLogDetailsList = (ArrayList<CKeyValuePair>) MainActivity.readObject(getContext(), Constant.GetListClickLog);
//                        ClickLogService clickLogService = new ClickLogService(getContext());
//                        clickLogService.CallClickLogService(hashMap.get(Constant.TripIDLog), hashMap.get(Constant.SSDriverIDKey), hashMap.get(Constant.EventCodeLog), clickLogDetailsList, new ServiceCallback() {
//                            @Override
//                            public void onSuccess(JSONObject obj) throws JSONException {
//                                Log.d(TAG, "CallClickLogService: "+obj.toString());
//                                try {
//                                    String data = obj.optJSONObject("CustomerUniqueNo").getString("a:CKeyValuePair");
//                                    Object json = new JSONTokener(data).nextValue();
//                                    //if (json instanceof JSONObject) {
//                                    clickLogServiceResponseObject = gson.fromJson(obj.toString(), ClickLogServiceResponseObject.class);
//                                    ServiceCode = clickLogServiceResponseObject.getServiceAttributesList().getACKeyValuePair().get(0).getValue();
//                                    ServiceMessage = clickLogServiceResponseObject.getServiceAttributesList().getACKeyValuePair().get(2).getValue();
////                        }
////                        //you have an object
////                        else if (json instanceof JSONArray) {
////                            clickLogServiceResponseArray = gson.fromJson(obj.toString(), ClickLogServiceResponseArray.class);
////                            ServiceCode = clickLogServiceResponseArray.getServiceAttributesList().getACKeyValuePair().get(0).getAValue();
////                            ServiceMessage = clickLogServiceResponseArray.getServiceAttributesList().getACKeyValuePair().get(2).getAValue();
////                        }
//
//                                } catch (Exception e) {
//                                    ExceptionService.ExceptionLogService(getContext(), e.getMessage(), ClassName, AndroidSerialNo);
//                                }
//                            }
//
//
//                            @Override
//                            public void onFailure(String obj) {
//                                Log.d(TAG, obj);
//                            }
//                        });
//                    } catch (IOException | ClassNotFoundException e) {
//                        ExceptionService.ExceptionLogService(getContext(), e.getMessage(), ClassName, AndroidSerialNo);
//                    }
                    try {
                        ConfigurationService configurationService = new ConfigurationService(getContext());
                        configurationService.CallConfigurationService(new ServiceCallback() {
                            @Override
                            public void onSuccess(JSONObject obj) throws JSONException {
                                Log.d(TAG, "onSuccess: " + obj.toString());
                                String data = obj.optJSONObject("CustomerUniqueNo").getString("a:CKeyValuePair");
                                Object json = new JSONTokener(data).nextValue();
                                if (json instanceof JSONObject) {
                                    configurationServiceResponseObject = gson.fromJson(obj.toString(), ConfigurationServiceResponseObject.class);
                                    PaynowCheck = configurationServiceResponseObject.getCustomerUniqueNo().getACKeyValuePair().getAValue();
                                } else if (json instanceof JSONArray) {
                                    configurationServiceResponseArray = gson.fromJson(obj.toString(), ConfigurationServiceResponseArray.class);
                                    PaynowCheck = configurationServiceResponseArray.getCustomerUniqueNo().getaCKeyValuePair().get(0).getAValue();
                                }


                                Log.d(TAG, "configurationService" + obj.toString());
                            }

                            @Override
                            public void onFailure(String obj) {

                            }
                        });
                    } catch (Exception e) {

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        //bundle = new Bundle();
        btn_Pay.setOnClickListener(v -> {
            mainActivity.onFinished("finish");

            boolean isNull = false;
            try {

                if (!internetCheck) {
                    Toast.makeText(getContext(), "Sorry for the inconvenience, system is unavailable", Toast.LENGTH_LONG).show();
                } else {
                    Fare = PreferenceConnector.readString(getContext(), Constant.Fare, null);

                    if (Fare == null || Fare.equals("") || Fare.equals("0")) {

                        Toast.makeText(getContext(), "Fare Cannot be zero", Toast.LENGTH_LONG).show();
                    } else {

//                        if (getActivity().getLocalClassName().equals("rtaservices.RTAMainActivity")){
//
//
//                            Intent intent=new Intent(getActivity(), DTCFareActivity.class);
//                            context.startActivity(intent);
//
//
//
//                        }else {

                        mFragment = new PayNowFragmnent();
                        addFragment(mFragment, "PayNowFragmnent");

//                        }


                        final Timer t = new Timer();
                        t.schedule(new TimerTask() {
                            public void run() {
                                dialog.dismiss();// when the task active then close the dialog
                                t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                            }
                        }, 100);


                        //Call Back function to close the start fragment
                        if (dialogdismisslistener != null) {
                            dialogdismisslistener.DialogdismisslistenerBackMethod();
                        }
                    }
                }
            } catch (Exception npe) {
                isNull = true;
            }
        });


        iv_close.setOnClickListener(v -> {

            mainActivity.onFinished("finish");
            dialog.dismiss();
            //Call Back function to close the start fragment
            if (dialogdismisslistener != null) {
                dialogdismisslistener.DialogdismisslistenerBackMethod();
            }
            /*if (multipleViewClickListenercallBack != null) {
                multipleViewClickListenercallBack.MultipleViewClickListenerCallBackMethod(ClassName);
            }*/
        });
    }

    private final Runnable runnable = () -> {

        reverseGeoCoding.getAddress(Double.parseDouble(TripEndLat), Double.parseDouble(TripEndLong));
        getTripEndAddress = reverseGeoCoding.getAddress1();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_dest_end_Address.setText(getTripEndAddress);

            }
        });

    };


    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Window window = getDialog().getWindow();
        window.setLayout(1000, 700);
        window.setGravity(Gravity.CENTER);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


}