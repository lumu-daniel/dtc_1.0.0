package com.mlt.dtc.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.gson.Gson;
import com.mlt.dtc.R;
import com.mlt.dtc.activity.MainActivity;
import com.mlt.dtc.common.Common;
import com.mlt.dtc.common.PreferenceConnector;
import com.mlt.dtc.common.SystemUIService;
import com.mlt.dtc.interfaces.ServiceCallback;
import com.mlt.dtc.model.response.UserLoginDetailsResponse;
import com.mlt.dtc.services.UserLoginService;
import com.mlt.dtc.utility.Constant;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Objects;
import java.util.UUID;
import example.CustomKeyboard.Components.CustomKeyboardView;
import static com.mlt.dtc.MainApp.AndroidSerialNo;
import static com.mlt.dtc.MainApp.internetCheck;
import static com.mlt.dtc.utility.Constant.LOGIN_FLAG;

public
class SettingsLoginFragment extends Fragment {
    Fragment mFragment;

    UserLoginDetailsResponse userLoginDetailsResponse;
    Gson gson = new Gson();
    String ServiceCode, ServiceMessage;
    String ClassName;
    Button btnBack;

    private static final String TAG = "LoginActivity";


    // @BindView(R.id.input_email)
    EditText _emailText;
    // @BindView(R.id.input_password)
    EditText _passwordText;
    //  @BindView(R.id.btn_login)
    Button _loginButton;
    //  @BindView(R.id.link_signup)
    TextView _signupLink;
    int count = 0;
    public CustomKeyboardView keyboard;


    public SettingsLoginFragment() {
        // Required empty public constructor
    }

    public static SettingsLoginFragment newInstance() {
        return new SettingsLoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.settingslogin, container, false);


//        keyboard = view.findViewById(R.id.customKeyboardView);
        _emailText = view.findViewById(R.id.input_email);
        _passwordText = view.findViewById(R.id.input_password);
        _loginButton = view.findViewById(R.id.btn_login);
        btnBack = view.findViewById(R.id.btnback_login);
        TextView tv_text_adminLogin = view.findViewById(R.id.tv_text_adminlogin);
        //_signupLink = (TextView) view.findViewById(R.id.link_signup);

        //get Class Name
        ClassName = getClass().getCanonicalName();

        //Randomly generated no for taxi meter info service request ID
        UUID uuid = UUID.randomUUID();
        String uuidInString = uuid.toString();

//        _emailText.setText("talal.a@networkips.com");
//        _passwordText.setText("talalabsar");


        tv_text_adminLogin.setOnClickListener(v -> {
            count++;
            if (count == 30) {
                SystemUIService.setNaviButtonVisibility(getContext(), SystemUIService.NAVIBUTTON_NAVIBAR, View.VISIBLE);
                //It exits to the installer where the apk gets installed
                Intent intent = new Intent();
                intent.setClassName("com.xac.util.saioutility", "com.xac.util.saioutility.Main");
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(view1 ->{
            MainActivity.count = 0;
            Objects.requireNonNull(getFragmentManager()).beginTransaction()
                    .replace(R.id.content, AdminFragment.newInstance())
                    .addToBackStack(null)
                    .commit();
        });


        _loginButton.setOnClickListener(v -> {
            try {
                if (!validate()) {
                    onLoginFailed();
                    return;
                }

                String email = _emailText.getText().toString();
                String password = _passwordText.getText().toString();

                if (internetCheck) {
                    UserLoginService loginService = new UserLoginService(getContext());
                    loginService.CallUserLoginService(Constant.ServiceIdAdminLogin, uuidInString, Constant.SourceApplication,
                            Constant.RequestType, Constant.RequestCategory, Common.getdateTime(), Constant.LoginID, Constant.Password,
                            email, password, AndroidSerialNo, Common.getdateTime(), new ServiceCallback() {
                                @Override
                                public void onSuccess(JSONObject obj) throws JSONException {
                                    Log.d(TAG, "CallUserLoginService: "+obj.toString());
                                    userLoginDetailsResponse = gson.fromJson(obj.toString(), UserLoginDetailsResponse.class);
                                    ServiceCode = userLoginDetailsResponse.getServiceAttributesList().getACKeyValuePair().get(0).getValue();
                                    ServiceMessage = userLoginDetailsResponse.getServiceAttributesList().getACKeyValuePair().get(2).getValue();

                                    if (ServiceCode.equals("0000")) {
                                        PreferenceConnector.writeString(getContext(),LOGIN_FLAG,"true");
                                        mFragment = SettingsMenuFragmnent.newInstance();
                                        addFragment();
                                        Toast.makeText(getContext(), ServiceMessage, Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(getContext(), ServiceMessage, Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onFailure(String obj) {
                                    Toast.makeText(getContext(), "An error has occured", Toast.LENGTH_SHORT).show();

                                }
                            }
                    );

                } else {
                    Toast.makeText(getContext(), "Sorry for the inconvenience, system is unavailable", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {

            }
        });


        return view;
    }


    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                () -> {
                    // On complete call either onLoginSuccess or onLoginFailed
                    onLoginSuccess();

                    mFragment = SettingsMenuFragmnent.newInstance();
                    addFragment();

                    // onLoginFailed();
                    progressDialog.dismiss();
                }, 3000);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        //finish();
        Toast.makeText(getContext(), "Login success", Toast.LENGTH_LONG).show();
    }

    public void onLoginFailed() {/* @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }*/
        Toast.makeText(getContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            Toast.makeText(getContext(), "enter a valid email address", Toast.LENGTH_LONG).show();
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("Please enter a correct password between 4 and 10 characters");
            Toast.makeText(getContext(), "enter a valid password address", Toast.LENGTH_LONG).show();
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public void addFragment() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, mFragment)
                .addToBackStack(null)
                .commit();
    }
}
