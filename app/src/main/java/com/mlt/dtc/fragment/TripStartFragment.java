package com.mlt.dtc.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.mlt.dtc.R;
import com.mlt.dtc.common.Common;
import com.mlt.dtc.common.PreferenceConnector;
import com.mlt.dtc.interfaces.Dialogdismisslistener;
import com.mlt.dtc.interfaces.ServiceCallback;
import com.mlt.dtc.interfaces.TripStartedForPaymentListener;
import com.mlt.dtc.location.getStartReverseGeoCoding;
import com.mlt.dtc.model.CKeyValuePair;
import com.mlt.dtc.model.ClickLogServiceResponseObject;
import com.mlt.dtc.model.PushDetails;
import com.mlt.dtc.pushnotification.MyFirebaseMessagingService;
import com.mlt.dtc.services.ClickLogService;
import com.mlt.dtc.services.ServiceCallLogService;
import com.mlt.dtc.utility.Constant;


import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.mlt.dtc.utility.Constant.TSEventDescriptionKey;


public class TripStartFragment extends DialogFragment implements Dialogdismisslistener, TripStartedForPaymentListener {
    private AlertDialog dialog;
    AlertDialog.Builder customDialogMain;
    AlertDialog.Builder customDialogMainTripStart;
    Context context;
    Fragment mFragment;
    TextView tv_duration, tv_fare,
            tv_start_fare, tv_start_DateTime, tv_end_DateTime, tv_dest_start_Address, tv_dest_end_Address, paymentstaus;
    PushDetails pushDetails = new PushDetails();
    PushDetails pushDetailsTripStart = new PushDetails();
    PushDetails pushDetailsTripEnd = new PushDetails();
    LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
    String TripStartLatitude, TripStartLongitude, StartDate, TripTimeDuration,
            TripID, getTripStartAddress, getTripEndAddress, Fare, ErrorMessage;

    Button btn_Pay;
    Bundle bundle;
    ClickLogServiceResponseObject clickLogServiceResponseObject;
    Gson gson = new Gson();
    String ServiceCode, ServiceMessage;
    Boolean TripEndServiceCall;
    String ClassName;
    public static boolean tripStarted = false;
    getStartReverseGeoCoding reverseGeoCoding;
    ImageView iv_close;
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
        customDialogMainTripStart = new AlertDialog.Builder(getActivity(), R.style.CustomDialog);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.farefragment, null);
        bundle = getArguments();

        //set the view to the dialog
        customDialogMain.setView(view);

        handlerThread = new HandlerThread("location");
        handlerThread.start();

        handler = new Handler(handlerThread.getLooper());


        //get class name
        ClassName = getClass().getCanonicalName();

        iv_close = view.findViewById(R.id.iv_closefarefragment);


        tv_dest_start_Address = view.findViewById(R.id.tv_dest_start_address);
        tv_dest_end_Address = view.findViewById(R.id.tv_dest_end_address);
        tv_duration = view.findViewById(R.id.tv_duration);
        tv_fare = view.findViewById(R.id.tv_fare);
        tv_start_fare = view.findViewById(R.id.start_fare);
        tv_start_DateTime = view.findViewById(R.id.start_datetime);
        tv_end_DateTime = view.findViewById(R.id.end_datetime);
        btn_Pay = view.findViewById(R.id.btn_paynow);


        //Show the dialog
        dialog = customDialogMain.show();
        dialog.setCanceledOnTouchOutside(false);


        return dialog;
    }


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
        //Close this dialog when the TripEndFragment is closed
        if (dialog != null)
            dialog.dismiss();

    }

    @Override
    public void TripStartedForPaymentCallBackMethod(boolean tripStartedforpayment) {
        tripStarted = tripStartedforpayment;
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

    public void addFragment() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, mFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onStart() {
        super.onStart();

        //dismiss trip start dialog
        TripEndFragment.dialogdismissCallBackMethod(this);

        reverseGeoCoding = new getStartReverseGeoCoding();

        //set the value to true
        MyFirebaseMessagingService.setTripStartedForPaymentCallBackMethod(this);

        //TripEndFragment se the value to false when the payment is done
        TripEndFragment.setTripStartedForPaymentCallBackMethod(this);

        try {
            hashMap = (LinkedHashMap<String, String>) Common.readObject(getContext(), Constant.PushDetailsHashMap);
            pushDetailsTripStart = (PushDetails) Common.readObject(getContext(), Constant.PushDetailsTripStart);
            pushDetailsTripEnd = (PushDetails) Common.readObject(getContext(), Constant.PushDetailsTripEnd);

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
                        pushDetailsTripStart.setEventDescription(hashMap.get(TSEventDescriptionKey));
                        pushDetailsTripStart.setJobnumber(hashMap.get(Constant.TSJobNumberKey));
                        pushDetailsTripStart.setUsername(hashMap.get(Constant.TSUsernameKey));
                        pushDetailsTripStart.setEventName(hashMap.get(Constant.TSEventNameKey));
                        pushDetailsTripStart.setTripId(hashMap.get(Constant.TSTripIdKey));
                        pushDetailsTripStart.setVehicleType(hashMap.get(Constant.TSVehicleTypeKey));
                        //Trip Start
                        pushDetailsTripStart.setStartdatetime(hashMap.get(Constant.TSStartDateTimeKey));
                        pushDetailsTripStart.setStartlatitude(hashMap.get(Constant.TSStartLatitudeKey));
                        pushDetailsTripStart.setStartlongitude(hashMap.get(Constant.TSStartLongitudeKey));
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
                        pushDetailsTripEnd.setEventDescription(hashMap.get(TSEventDescriptionKey));
                        pushDetailsTripEnd.setJobnumber(hashMap.get(Constant.TSJobNumberKey));
                        pushDetailsTripEnd.setUsername(hashMap.get(Constant.TSUsernameKey));
                        pushDetailsTripEnd.setEventName(hashMap.get(Constant.TSEventNameKey));
                        pushDetailsTripEnd.setTripId(hashMap.get(Constant.TSTripIdKey));
                        pushDetailsTripEnd.setVehicleType(hashMap.get(Constant.TSVehicleTypeKey));
                        //Trip End
                        pushDetailsTripEnd.setEnddatetime(hashMap.get(Constant.TSEndDateTimeKey));
                        pushDetailsTripEnd.setEndlatitude(hashMap.get(Constant.TSEndLatitudeKey));
                        pushDetailsTripEnd.setEndlongitude(hashMap.get(Constant.TSEndlongitudeKey));

                        try {
                            Common.writeObject(getContext(), Constant.PushDetailsTripEnd, pushDetailsTripEnd);
                        } catch (IOException e) {

                        }
                        pushDetails = pushDetailsTripEnd;

                    }
                    //Trip Start
                    if (hashMap.get(Constant.TSEventCodeKey).equals(Constant.TripStartEventCode)) {
                        handler.post(runnable);
//                        reverseGeoCoding.getAddress(Double.parseDouble(pushDetails.getStartlatitude()), Double.parseDouble(pushDetails.getStartlongitude()));
//                        getTripStartAddress = reverseGeoCoding.getAddress1();
//                        //getTripStartAddress = Common.getAddressFromLocation(Double.parseDouble(pushDetails.getStartlatitude()), Double.parseDouble(pushDetails.getStartlongitude()), getContext(), new GeocoderHandler());
//                        tv_dest_start_Address.setText(getTripStartAddress);

                        PreferenceConnector.writeBoolean(getContext(), Constant.PaymentStatus, false);

                        tv_duration.setText("");

                        tv_fare.setText("0" + " AED");


                        tv_start_fare.setText(pushDetails.getFlagfall() + " AED");
                        tv_start_DateTime.setText(pushDetails.getStartdatetime());

                        tv_end_DateTime.setText(pushDetails.getEnddatetime());
                        //Get this in the string and save it in a cache to show in the trip end
                        TripStartLatitude = pushDetails.getStartlatitude();
                        TripStartLongitude = pushDetails.getStartlongitude();
                        StartDate = pushDetails.getStartdatetime();
                        PreferenceConnector.writeString(getContext(), Constant.TripStartAddress, getTripStartAddress);
                        PreferenceConnector.writeString(getContext(), Constant.TripStartlatitude, TripStartLatitude);
                        PreferenceConnector.writeString(getContext(), Constant.TripStartlongitude, TripStartLongitude);
                        PreferenceConnector.writeString(getContext(), Constant.StartDate, StartDate);
                        //PreferenceConnector.writeString(getContext(), Constant.Fare, Fare);
                        btn_Pay.setVisibility(View.GONE);

                        //Trip End
                    } else if (hashMap.get(Constant.TSEventCodeKey).equals(Constant.TripEndEventCode)) {
                        TripStartLatitude = PreferenceConnector.readString(getContext(), Constant.TripStartlatitude, null);
                        TripStartLongitude = PreferenceConnector.readString(getContext(), Constant.TripStartlongitude, null);
                        getTripStartAddress = Common.getAddressFromLocation(Double.parseDouble(TripStartLatitude), Double.parseDouble(TripStartLongitude), getContext(), new GeocoderHandler());
                        getTripEndAddress = Common.getAddressFromLocation(Double.parseDouble(pushDetails.getEndlatitude()), Double.parseDouble(pushDetails.getEndlatitude()), getContext(), new GeocoderHandler());
                        StartDate = PreferenceConnector.readString(getContext(), Constant.StartDate, null);
                        Fare = pushDetails.getFare();
                        tv_dest_start_Address.setText(getTripStartAddress);
                        tv_dest_end_Address.setText(getTripEndAddress);


                        tv_fare.setText(Fare + " AED");
                        tv_start_DateTime.setText(StartDate);
                        tv_end_DateTime.setText(pushDetails.getEnddatetime());
                        tv_start_fare.setText(pushDetails.getFlagfall() + " AED");
                        TripTimeDuration = Common.GetTimeDifference(StartDate, pushDetails.getEnddatetime());
                        tv_duration.setText(TripTimeDuration);
                        btn_Pay.setVisibility(View.VISIBLE);
                        PreferenceConnector.writeString(getContext(), Constant.Fare, Fare);


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

        try {
            if (TripEndServiceCall) {
                if (hashMap != null) {
                    if (hashMap.get(Constant.EventCodeLog).equals(Constant.TripEndEventCode)) {
                        try {
                            ArrayList<CKeyValuePair> clickLogDetailsList = (ArrayList<CKeyValuePair>) Common.readObject(getContext(), Constant.GetListClickLog);
                            ClickLogService clickLogService = new ClickLogService(getContext());
                            clickLogService.CallClickLogService(hashMap.get(Constant.TripIDLog), hashMap.get(Constant.SSDriverIDKey), hashMap.get(Constant.EventCodeLog), clickLogDetailsList, new ServiceCallback() {
                                @Override
                                public void onSuccess(JSONObject obj) throws JSONException {
                                    try {
                                        String data = obj.optJSONObject("CustomerUniqueNo").getString("a:CKeyValuePair");
                                        Object json = new JSONTokener(data).nextValue();
                                        //if (json instanceof JSONObject) {
                                        clickLogServiceResponseObject = gson.fromJson(obj.toString(), ClickLogServiceResponseObject.class);
                                        ServiceCode = clickLogServiceResponseObject.getServiceAttributesList().getACKeyValuePair().get(0).getValue();
                                        ServiceMessage = clickLogServiceResponseObject.getServiceAttributesList().getACKeyValuePair().get(2).getValue();
//
                                    } catch (Exception e) {

                                    }
                                }


                                @Override
                                public void onFailure(String obj) {
                                    Log.d(Constant.TAG, obj);
                                }
                            });
                        } catch (IOException | ClassNotFoundException e) {

                        }

                    }
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Technical service error. Trip not started.", Toast.LENGTH_LONG).show();
                    });
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }


        iv_close.setOnClickListener(v -> {
            dialog.dismiss();

        });
    }

    private final Runnable runnable = () -> {

        reverseGeoCoding.getAddress(Double.parseDouble(pushDetails.getStartlatitude()), Double.parseDouble(pushDetails.getStartlongitude()));
        getTripStartAddress = reverseGeoCoding.getAddress1();
        PreferenceConnector.writeString(getContext(), Constant.TripStartAddress, getTripStartAddress);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_dest_start_Address.setText(getTripStartAddress);
            }
        });

    };

    @Override
    public void onResume() {
        super.onResume();
//        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Window window = getDialog().getWindow();
        window.setLayout(1000, 700);
        window.setGravity(Gravity.CENTER);
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        });

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

    @Override
    public void onDetach() {
        super.onDetach();
    }
}