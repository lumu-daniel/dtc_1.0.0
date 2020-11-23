package com.mlt.dtc.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import com.google.gson.Gson;
import com.mlt.dtc.R;
import com.mlt.dtc.activity.MainActivity;
import com.mlt.dtc.common.Common;
import com.mlt.dtc.interfaces.ServiceCallback;
import com.mlt.dtc.model.Response.UserLoginDetailsResponse;
import com.mlt.dtc.services.TaxiMeterInfoService;
import com.mlt.dtc.utility.Constant;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.util.UUID;
import static com.mlt.dtc.MainApp.AndroidSerialNo;
import static com.mlt.dtc.MainApp.internetCheck;
import static com.mlt.dtc.utility.Constant.TAG;

public
class SettingsFramgnet  extends DialogFragment {

    private AlertDialog dialog;
    EditText edtTaxiPlateNo, edtTaxiCode, edtDeviceSerialNo;
    Button btnSubmit;
    Context context;
    Fragment mFragment;
    MainActivity mainActivity;
    String getTaxiPlateNo, getTaxiCode, getDeviceSerialNo;
    BufferedInputStream is;
    Gson gson = new Gson();

    UserLoginDetailsResponse userLoginDetailsResponse;
    String ServiceCode, ServiceMessage;
    String ClassName;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public static SettingsFramgnet newInstance() {
        return new SettingsFramgnet();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder customDialogMain = new AlertDialog.Builder(getActivity(), R.style.CustomDialog);
        context = getContext();
        mainActivity = new MainActivity();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_dialog_settingpop, null);
        ImageView iv_close = view.findViewById(R.id.iv_closeemailpop);
        edtTaxiPlateNo = view.findViewById(R.id.edttaxiplateno);
        edtTaxiCode = view.findViewById(R.id.edttaxicode);
        edtDeviceSerialNo = view.findViewById(R.id.edtdeviceserialno);
        btnSubmit = view.findViewById(R.id.btnsubmit);




        //get Class Name
        ClassName = getClass().getCanonicalName();

        //Get Token, this token is the device ID that will be placed at the server level
//        String getDevicetoken = FirebaseInstanceId.getInstance().getToken();

        customDialogMain.setView(view);

        dialog = customDialogMain.show();

        btnSubmit.setOnClickListener(v -> {
            try {
                getTaxiPlateNo = edtTaxiPlateNo.getText().toString();
                getTaxiCode = edtTaxiCode.getText().toString();
                getDeviceSerialNo = edtDeviceSerialNo.getText().toString();
                if(getTaxiPlateNo.equalsIgnoreCase("")||getTaxiCode.equalsIgnoreCase("")||getDeviceSerialNo.equalsIgnoreCase("")){
                    getActivity().runOnUiThread(()->{
                        Toast.makeText(getContext(),"Please enter valid Details.",Toast.LENGTH_LONG).show();
                    });
                }else {
                    //Randomly generated no for taxi meter info service request ID
                    UUID uuid = UUID.randomUUID();
                    String uuidInString = uuid.toString();

                    if (internetCheck) {
                        TaxiMeterInfoService taxiMeterInfoService = new TaxiMeterInfoService(getContext());
                        taxiMeterInfoService.CallTaxiMeterInfoService(Constant.ServiceId, uuidInString, Constant.SourceApplication,
                                Constant.RequestType, Constant.RequestCategory, Common.getdateTime(), Constant.LoginID, Constant.Password,
                                "getDevicetoken", AndroidSerialNo, getDeviceSerialNo,
                                getTaxiPlateNo, getTaxiCode, Common.getdateTime(), new ServiceCallback() {
                                    @Override
                                    public void onSuccess(JSONObject obj) throws JSONException {
                                        Log.d(TAG, "CallTaxiMeterInfoService: "+obj.toString());
                                        userLoginDetailsResponse = gson.fromJson(obj.toString(), UserLoginDetailsResponse.class);
                                        ServiceCode = userLoginDetailsResponse.getServiceAttributesList().getACKeyValuePair().get(0).getValue();
                                        ServiceMessage = userLoginDetailsResponse.getServiceAttributesList().getACKeyValuePair().get(2).getValue();
                                        if (ServiceCode.equals("0000")) {

                                            mFragment = SettingsMenuFragmnent.newInstance();
                                            addFragment();
                                            Toast.makeText(getContext(), ServiceMessage, Toast.LENGTH_LONG).show();

                                        } else {
                                            Toast.makeText(getContext(), ServiceMessage, Toast.LENGTH_LONG).show();
                                        }

                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onFailure(String obj) {
                                        Toast.makeText(getContext(), "Service is temporarily unavailable.", Toast.LENGTH_LONG).show();
                                    }
                                });

                    } else {
                        Toast.makeText(getContext(), "Sorry for the inconvenience, system is unavailable", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {

            }
        });
        iv_close.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }

  /*  public void forceCrash(View view) {
        throw new RuntimeException("This is a crash");
    }*/

    public void addFragment() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, mFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();

        Window window = dialog.getWindow();
        window.setLayout(1000, 700);
        window.setGravity(Gravity.CENTER);
    }
}
