package com.mlt.dtc.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


import com.mlt.dtc.R;
import com.mlt.dtc.activity.MainActivity;
import com.mlt.dtc.common.Common;
import com.mlt.dtc.common.PreferenceConnector;
import com.mlt.dtc.model.PushDetails;
import com.mlt.dtc.utility.Constant;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.LinkedHashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class DriverFragment extends DialogFragment {

    private AlertDialog dialog;
    Context context;

    PushDetails pushDetailsShiftStart;
    PushDetails pushDetailsShiftEnd;
    PushDetails pushDetailsTripStart;
    PushDetails pushDetailsTripEnd;
    PushDetails pushDetails;
    LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
    TextView tv_driverName, tv_driverNationality, tv_driverID, tv_Family_Name, tv_carPlateno, tv_vehicle_Type, tv_shift_Start, tv_shift_End;
    CircleImageView iv_driverImage;
    String base64 = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJb" +
            "WFnZVJlYWR5ccllPAAAAp1JREFUeNqEU21IU1EYfu7unW5Ty6aBszYs6MeUjGVYokHYyH5E1B9rZWFEFPQnAwm" +
            "y6Hc/oqhfJsRKSSZGH1JIIX3MNCsqLTD9o1Oj6ebnnDfvvefezrnbdCHhCw/n433P8z7nPe/hBEEAtX0U7hc1" +
            "64uwuvVSXKwZLoOmaRDim+7m9vZa0WiEKSUFFpNpCWlmMyypqTDRuYn6t3k8vmQ2gRDCxs0t9fW45F52aBTRO" +
            "JLtZl7nEZad2m+KtoQCQ0FBARyOCGRZ/q92I1WgqqXlfdd95VsrK8/pChIEqqpCkiQsiCII0aBQZZoWl8lzFDw" +
            "sFjMl0DBLY8Lj41hBwK4jSQrWOIphL6xYyhwJDWGo6wFSaH1Y3PTCAsITE1oyAa8flhWkbSiCLX8vun11eiGIpi" +
            "J/z2nYdx5HqLdVV7elrOzsuqysL3rmBIGiKPizKCHHWY4PLVeQbnXAdegqdhy+hu8dDTBnbqQJZJ1A7u+vz7RaiymWC" +
            "ZgCRSF6Edk8b9cx+B/W6WuVxPaZnyiqXoPpyUmVYvkKTIFClHigEieKjYuSvETUllaF4GAUM1NT6ooaJDKx+aDfC" +
            "9fByxj90REb+9ppmIoAscH/6leg8MS9DJXPAM9xHCM443K57C6biMjcHDaVVCHw9RmCA2/RGC5C00AqXk/m4p20HZK4C" +
            "M/J3Zk9n0ecMBhDQnJHcrTisyMfdQXOilrdMfxcwoHq/fg5R59TiQV3hYGKo6X2J/c7LyQIjOx9GXhOw/zoJ8wEevRGyp" +
            "53o/lGMNYsBgPtEwLecwov7/jGDKa1twT6o3KpL4MdZgGsWZLtfPr7f1q58k1JNHy7YYaM+J+K3Y2PmAIbRavX66229hrGV" +
            "vvL5uzsHDEUvUu+NT1my78CDAAMK1a8/QaZCgAAAABJRU5ErkJggg==";
    String ShiftEventCode, ShiftEventDateTime, ShiftStartDateTime, ShiftEndDateTime, ErrorMessage;

    String ClassName;
    AlertDialog.Builder customDialogMain;
    View view;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        customDialogMain = new AlertDialog.Builder(getActivity(), R.style.CustomDialog);
        context = getContext();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.driverfragment, null);
        ImageView iv_close = view.findViewById(R.id.iv_closefarefragment);
        tv_driverName = view.findViewById(R.id.tv_driver_name);
        tv_driverID = view.findViewById(R.id.tv_driver_ID);
        tv_driverNationality = view.findViewById(R.id.tv_driver_nationality);
//        tv_Family_Name = view.findViewById(R.id.tv_family_name);
        iv_driverImage = view.findViewById(R.id.iv_driver_image);
        tv_carPlateno = view.findViewById(R.id.tv_car_palteno);
        tv_vehicle_Type = view.findViewById(R.id.tv_vehicle_type);
        tv_shift_Start = view.findViewById(R.id.shift_start_time);
        tv_shift_End = view.findViewById(R.id.shift_end_time);

        iv_driverImage.setImageResource(R.drawable.dtcdriverphoto);

        //get Class Name
        ClassName = getClass().getCanonicalName();

       /* pushDetailsShiftStart = new PushDetails();
        pushDetailsShiftEnd = new PushDetails();*/

        customDialogMain.setView(view);
        dialog = customDialogMain.show();
        dialog.setCanceledOnTouchOutside(false);

        iv_close.setOnClickListener(v -> {
            MainActivity.mainActivity.onFinished("finish");
            dialog.dismiss();
        });

        return dialog;
    }

    private void EmptyString() {
        tv_driverName.setText("");
//        tv_Family_Name.setText("");
        tv_driverID.setText("");
        tv_driverNationality.setText("");
        tv_carPlateno.setText("");
        tv_vehicle_Type.setText("");
        tv_shift_Start.setText("");
        tv_shift_End.setText("");
    }



    @Override
    public void onStart() {
        super.onStart();
        pushDetailsTripStart = new PushDetails();
        pushDetailsTripEnd = new PushDetails();

        try {
            hashMap = (LinkedHashMap<String, String>) Common.readObject(getContext(), Constant.PushDetailsHashMap);
            pushDetailsTripStart = (PushDetails) Common.readObject(getContext(), Constant.PushDetailsTripStart);
            pushDetailsTripEnd = (PushDetails) Common.readObject(getContext(), Constant.PushDetailsTripEnd);
            pushDetailsShiftStart = (PushDetails) Common.readObject(getContext(), Constant.PushDetailsShiftStart);
            pushDetailsShiftEnd = (PushDetails) Common.readObject(getContext(), Constant.PushDetailsShiftEnd);
            ShiftEventDateTime = PreferenceConnector.readString(getContext(), Constant.SSEventDateTimeKey, null);

            //Calling the service to log this service
//            ServiceCallLogService serviceCallLogService = new ServiceCallLogService(getContext());
//            serviceCallLogService.CallServiceCallLogService(Constant.nameDriver, Constant.name_Driver_details_desc);

        } catch (IOException | ClassNotFoundException e) {
//            ExceptionService.ExceptionLogService(getContext(), e.getMessage(), ClassName, AndroidSerialNo);
        }

        try {
            if (hashMap.get(Constant.ErrorMessage) != null) {
                ErrorMessage = hashMap.get(Constant.ErrorMessage);
                Toast.makeText(getContext(), ErrorMessage, Toast.LENGTH_LONG).show();
                //Shows Empty data in textviews
                EmptyString();
            } else {

                if (hashMap.size() > 0) {
                    //if (hashMap.get(Constant.TSEventCodeKey).equals(Constant.ShiftStartEventCode))//Shift Start
                    //{
                    pushDetailsTripStart.setFamilyName(hashMap.get(Constant.SSFamilyNameKey));
                    pushDetailsTripStart.setGivenName(hashMap.get(Constant.SSGivenNameKey));
                    pushDetailsTripStart.setNationality(hashMap.get(Constant.SSNationalityKey));
                    pushDetailsTripStart.setVehicleType(hashMap.get(Constant.SSVehicleTypeKey));
                    pushDetailsTripStart.setPicture(hashMap.get(Constant.SSPictureKey));
                    pushDetailsTripStart.setPlateNo(hashMap.get(Constant.SSPlateNoKey));
                    pushDetailsTripStart.setUsername(hashMap.get(Constant.SSUsernameKey));
                    pushDetailsTripStart.setShiftseq(hashMap.get(Constant.SSShiftSequenceKey));
                    pushDetailsTripStart.setDriverId(hashMap.get(Constant.SSDriverIDKey));
                    pushDetailsTripStart.setEventName(hashMap.get(Constant.SSEventNameKey));
                    //pushDetailsTripStart.setEventDateTime(hashMap.get(Constant.SSEventDateTimeKey));
                    pushDetailsTripStart.setEventDateTime(ShiftEventDateTime);
                    pushDetailsTripStart.setEventCode(hashMap.get(Constant.SSEventCodeKey));
                    pushDetailsTripStart.setEventDescription(hashMap.get(Constant.SSEventDescriptionKey));

                    try {
                        //Get shift event codes
                        ShiftEventCode = hashMap.get(Constant.SSEventCodeKey);
                        //Get the shift start and shift end date time
                        ShiftEventDateTime = hashMap.get(Constant.SSEventDateTimeKey);
                        if (ShiftEventCode.equals(Constant.ShiftStartEventCode)) {
                            PreferenceConnector.writeString(getContext(), Constant.ShiftStartDateTime, ShiftEventDateTime);
                            //Shift end has to be empty in shift start
                            PreferenceConnector.writeString(getContext(), Constant.ShiftendDateTime, "");
                        } else if (ShiftEventCode.equals(Constant.ShiftEndEventCode))
                            PreferenceConnector.writeString(getContext(), Constant.ShiftendDateTime, ShiftEventDateTime);
                    } catch (Exception e) {

                    }

                    try {
                        Common.writeObject(getContext(), Constant.PushDetailsTripStart, pushDetailsTripStart);
                    } catch (IOException e) {

                    }
                    pushDetails = pushDetailsTripStart;


                    try {
                        ShiftStartDateTime = PreferenceConnector.readString(getContext(), Constant.ShiftStartDateTime, "");
                        ShiftEndDateTime = PreferenceConnector.readString(getContext(), Constant.ShiftendDateTime, "");

                    } catch (Exception e) {

                    }

                    tv_driverName.setText(pushDetails.getGivenName());
//                    tv_Family_Name.setText(pushDetails.getFamilyName());
                    tv_driverID.setText(pushDetails.getDriverId());
                    tv_driverNationality.setText(pushDetails.getNationality());
                    tv_carPlateno.setText(pushDetails.getPlateNo());
                    tv_vehicle_Type.setText(pushDetails.getVehicleType());


                    tv_shift_Start.setText(ShiftStartDateTime);
                    tv_shift_End.setText(ShiftEndDateTime);
                    //Picasso.with(getContext()).load(pushDetails.getPicture()).placeholder(R.drawable.dtcdriverphoto).into(iv_driverImage);
                    Picasso.with(getContext()).load(pushDetails.getPicture()).into(iv_driverImage);
//                    Glide.with(getContext()).asBitmap().load(pushDetails.getPicture()).apply( new RequestOptions().placeholder(R.drawable.dtcdriverphoto)).into(iv_driverImage);


                }
            }
        } catch (Exception e) {

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Window window = getDialog().getWindow();
        window.setLayout(1000, 700);
        window.setGravity(Gravity.CENTER);
    }
}