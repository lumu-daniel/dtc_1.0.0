package com.mlt.dtc.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.mlt.dtc.R;
import com.mlt.dtc.activity.MainFragment;
import com.mlt.dtc.common.Common;
import com.mlt.dtc.common.Configuration;
import com.mlt.dtc.utility.Constant;
import java.util.LinkedHashMap;

import example.CustomKeyboard.Components.CustomKeyboardView;
import static com.mlt.dtc.MainApp.internetCheck;
import static com.mlt.dtc.common.Common.isEmailValid;

public
class ContactUsFragment extends DialogFragment  implements View.OnClickListener, Observer<Object> {
    private ImageView iv_Complaints,iv_regulatory;
    private Button iv_Aboutus,iv_Contactus,iv_Feedback,ivSend;
    private LinearLayout layout_Contact, layout_Feedback, layout_Regulatory, ll_hideKeyboard, layout_Aboutus, layoutConfirmation,linear_feedback;
    private EditText edtName, edtEmail, edtMobileno,edtMessage;
    private TextView confirmationText,tv_title,confirmationMessage;

    private Animation zoomin, zoomout;
    private String buttonPressed = null;
    int count;
    private String Name, Email, phoneNo, Message, ServiceID;
    Spinner sp_issues_Feedback;
    LinkedHashMap<String, String> hmServiceID = new LinkedHashMap<>();
    String getFirstsixchar;
    public static String issueType = "Feedback";
    public static String resolverGroup = "1";
    View activityRootView;
    View view;
    Resources resources;
    ContactUsViewModel viewModel;
    AlertDialog alertDialog;
    FragmentManager fragmentManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.contact_us, container, false);

        resources = getContext().getResources();

        viewModel = new ViewModelProvider(this,new ContactUsViewModel(getActivity().getApplication(),view, getActivity())).get(ContactUsViewModel.class);

        findviewById(view);

        fragmentManager = this.getFragmentManager();


        return view;
    }

    private void findviewById(View view) {
        linear_feedback = view.findViewById(R.id.linear_feedback);
        iv_Contactus = view.findViewById(R.id.iv_contactus);
        iv_Feedback = view.findViewById(R.id.iv_feedback);
        iv_Complaints = view.findViewById(R.id.iv_complaints);
        iv_regulatory = view.findViewById(R.id.iv_regulatory);
        iv_Aboutus = view.findViewById(R.id.iv_aboutus);
        layout_Contact = view.findViewById(R.id.ll_contact);
        layout_Feedback = view.findViewById(R.id.ll_feedback);
        layout_Regulatory = view.findViewById(R.id.ll_regulatory);
        layout_Aboutus = view.findViewById(R.id.ll_aboutus);
        ll_hideKeyboard = view.findViewById(R.id.ll_hidekeyboard);
        layoutConfirmation = view.findViewById(R.id.ll_confirmation);
        edtName = view.findViewById(R.id.edtname);
        edtEmail = view.findViewById(R.id.edtemail);
        edtMobileno = view.findViewById(R.id.edtno);
        edtMessage = view.findViewById(R.id.edtmsg);
        ivSend = view.findViewById(R.id.ivsend);
        tv_title = view.findViewById(R.id.tv_title);
        confirmationText = view.findViewById(R.id.confirmation_text);
        view.findViewById(R.id.btnback).setOnClickListener(this);
        view.findViewById(R.id.confirmation_button).setOnClickListener(this);
        sp_issues_Feedback = view.findViewById(R.id.sp_issues_feedback);

        confirmationMessage = view.findViewById(R.id.confirmation_message);
        MainFragment.keyboard.registerEditText(CustomKeyboardView.KeyboardType.QWERTY, edtMessage);
        MainFragment.keyboard.registerEditText(CustomKeyboardView.KeyboardType.NUMBER_DECIMAL, edtMobileno);
        MainFragment.keyboard.registerEditText(CustomKeyboardView.KeyboardType.QWERTY, edtEmail);
        MainFragment.keyboard.registerEditText(CustomKeyboardView.KeyboardType.QWERTY, edtName);
    }

    public void ZoomOut() {
        /*if (buttonPressed != "null") {
            buttonPressed = PreferenceConnector.readString(getContext(), Constant.ButtonPressed, null);
        }*/
        if (buttonPressed == Constant.Contactus)
            iv_Contactus.startAnimation(zoomout);
        else if (buttonPressed == Constant.FeedBack)
            iv_Feedback.startAnimation(zoomout);
        else if (buttonPressed == Constant.Complaint)
            iv_Complaints.startAnimation(zoomout);
        else if (buttonPressed == Constant.Regulatory)
            iv_regulatory.startAnimation(zoomout);
        else if (buttonPressed == Constant.Aboutus)
            iv_Aboutus.startAnimation(zoomout);
    }


    private void postIncident(){
        alertDialog = Common.setAvi(new AlertDialog.Builder(getActivity()).create(),getActivity());
        viewModel.postIncidentSuccess().observe(getViewLifecycleOwner(),this);
        viewModel.postIncidentFail().observe(getViewLifecycleOwner(),this);
    }


    @Override
    public void onClick(View view) {
        viewModel.doClickAction(view.getId());
    }

    @Override
    public void onChanged(Object response) {
        if(response instanceof retrofit2.Response){
            if(((retrofit2.Response)response).isSuccessful()){
                confirmationPage();
                if( issueType.equalsIgnoreCase( Configuration.INCIDENT ) ) {
                    confirmationText.setText( Constant.complaintSuccessMessage );
                    confirmationMessage.setText( Constant.successMessage );
                }else{
                    confirmationText.setText( Constant.feedbackSuccessMessage );
                    confirmationMessage.setText( Constant.successMessage );
                }
            }else{
                confirmationPage();
                if( issueType.equalsIgnoreCase( Configuration.INCIDENT ) ) {
                    confirmationText.setText( Constant.complaintFailureMessage );
                    confirmationMessage.setText( Constant.failureMessage );
                }else{
                    confirmationText.setText( Constant.feedbackFailureMessage );
                    confirmationMessage.setText( Constant.failureMessage );
                }
            }
        }
        else if(response instanceof Throwable){
            confirmationPage();
            if( issueType.equalsIgnoreCase( Configuration.INCIDENT ) ) {
                confirmationText.setText( Constant.complaintFailureMessage );
                confirmationMessage.setText( Constant.failureMessage );
            }else{
                confirmationText.setText( Constant.feedbackFailureMessage );
                confirmationMessage.setText( Constant.failureMessage );
            }
        }

        if(alertDialog!=null){
            if ( alertDialog.isShowing() ) {
                alertDialog.dismiss();
            }
        }
    }

    public void confirmationPage(){
        layout_Feedback.setVisibility( View.GONE );
        layout_Contact.setVisibility( View.GONE );
        layout_Regulatory.setVisibility( View.GONE );
        layout_Aboutus.setVisibility( View.GONE );
        layoutConfirmation.setVisibility( View.VISIBLE );
    }


    @Override
    public void onStart() {
        super.onStart();
        count = 0;
        buttonPressed = null;

        zoomin = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in_contactus);
        zoomout = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_out_contactus);

        tv_title.setText(Constant.Aboutus);

        iv_Aboutus.setOnClickListener(view1 -> {
            //buttonPressed = PreferenceConnector.readString(getContext(), Constant.ButtonPressed, null);
            if (buttonPressed != Constant.Aboutus || !buttonPressed.equals(Constant.Aboutus)) {
                layout_Feedback.setVisibility(View.GONE);
                layout_Contact.setVisibility(View.GONE);
                layout_Regulatory.setVisibility(View.GONE);
                layoutConfirmation.setVisibility(View.GONE);
                layout_Aboutus.setVisibility(View.VISIBLE);
                tv_title.setText(Constant.Aboutus);
                iv_Aboutus.startAnimation(zoomin);
                ZoomOut();
                buttonPressed = Constant.Aboutus;
                //PreferenceConnector.writeString(getContext(), Constant.ButtonPressed, Constant.Aboutus);
            }

        });

        String UUID = Common.getUUID();
        getFirstsixchar = UUID.substring(0, 7).toUpperCase();

        edtMobileno.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!s.toString().startsWith("9715")) {
                    edtMobileno.setText("9715");
                    Selection.setSelection(edtMobileno.getText(), edtMobileno.getText().length());

                }
            }
        });

        iv_Contactus.setOnClickListener(v -> {
            //buttonPressed = PreferenceConnector.readString(getContext(), Constant.ButtonPressed, null);
            if (buttonPressed != Constant.Contactus || !buttonPressed.equals(Constant.Contactus)) {
                layout_Feedback.setVisibility(View.GONE);
                layout_Contact.setVisibility(View.VISIBLE);
                layout_Regulatory.setVisibility(View.GONE);
                layout_Aboutus.setVisibility(View.GONE);
                layoutConfirmation.setVisibility(View.GONE);
                tv_title.setText(Constant.Contactus);
                iv_Contactus.startAnimation(zoomin);
                ZoomOut();
                buttonPressed = Constant.Contactus;
                //PreferenceConnector.writeString(getContext(), Constant.ButtonPressed, Constant.Contactus);
            }

        });

        iv_Feedback.setOnClickListener(v -> {
            //buttonPressed = PreferenceConnector.readString(getContext(), Constant.ButtonPressed, null);
            if (buttonPressed != Constant.FeedBack || !buttonPressed.equals(Constant.FeedBack)) {
                edtEmail.setText("");
                edtMessage.setText("");
                edtMobileno.setText("");
                edtName.setText("");
                layout_Feedback.setVisibility(View.VISIBLE);
                layout_Contact.setVisibility(View.GONE);
                layout_Regulatory.setVisibility(View.GONE);
                layout_Aboutus.setVisibility(View.GONE);
                layoutConfirmation.setVisibility(View.GONE);
                tv_title.setText(Constant.FeedBack);
                ZoomOut();
                iv_Feedback.startAnimation(zoomin);
                buttonPressed = Constant.FeedBack;
                //PreferenceConnector.writeString(getContext(), Constant.ButtonPressed, Constant.FeedBack);

                Name = edtName.getText().toString();
                Email = edtEmail.getText().toString();
                phoneNo = edtMobileno.getText().toString();
                Message = edtMessage.getText().toString();
                issueType = Configuration.FEEDBACK;
                resolverGroup = Configuration.RESOLVERGROUP_FB_VALUE;

                //Make a dictionary to get a Service ID
                hmServiceID.put(Constant.DriverDetails, "");
                hmServiceID.put(Constant.tripDetails, "");
                hmServiceID.put(Constant.AboutUs, "");
                hmServiceID.put(Constant.RTAservices, "");
                hmServiceID.put(Constant.DTCservices, "");
                hmServiceID.put(Constant.OthergovernmentServices, "");
                hmServiceID.put(Constant.Utilitiesandotherservices, "");
                hmServiceID.put(Constant.ECommerceonlineshop, "");
                hmServiceID.put(Constant.ClassifiedAdvertisement, "");
                hmServiceID.put(Constant.Offers, "");
                hmServiceID.put(Constant.Banners, "");
                hmServiceID.put(Constant.News, "101");
                hmServiceID.put(Constant.Maps, "98");
                hmServiceID.put(Constant.Weather, "100");
                hmServiceID.put(Constant.Entertainment, "99");
                hmServiceID.put(Constant.HappyMeter, "102");

                ServiceID = hmServiceID.get(sp_issues_Feedback.getSelectedItem().toString());
                ServiceID = hmServiceID.get("");


            }
        });

        iv_Complaints.setOnClickListener(v -> {
            //buttonPressed = PreferenceConnector.readString(getContext(), Constant.ButtonPressed, null);
            if (buttonPressed != Constant.Complaint || !buttonPressed.equalsIgnoreCase(Constant.Complaint)) {
                edtEmail.setText("");
                edtMessage.setText("");
                edtMobileno.setText("");
                edtName.setText("");
                layout_Feedback.setVisibility(View.VISIBLE);
                layout_Contact.setVisibility(View.GONE);
                layout_Regulatory.setVisibility(View.GONE);
                layout_Aboutus.setVisibility(View.GONE);
                layoutConfirmation.setVisibility(View.GONE);
                tv_title.setText(Constant.Complaint);
                issueType = Configuration.INCIDENT;
                resolverGroup = Configuration.RESOLVERGROUP_VALUE;
                ZoomOut();
                iv_Complaints.startAnimation(zoomin);
                buttonPressed = Constant.Complaint;
                //PreferenceConnector.writeString(getContext(), Constant.ButtonPressed, Constant.Complaint);
            }
        });

        iv_regulatory.setOnClickListener(v -> {
            //buttonPressed = PreferenceConnector.readString(getContext(), Constant.ButtonPressed, null);
            if (buttonPressed != Constant.Regulatory || !buttonPressed.equalsIgnoreCase(Constant.Regulatory)) {
                layout_Regulatory.setVisibility(View.VISIBLE);
                layout_Feedback.setVisibility(View.GONE);
                layout_Contact.setVisibility(View.GONE);
                layout_Aboutus.setVisibility(View.GONE);
                layoutConfirmation.setVisibility(View.GONE);
                tv_title.setText(Constant.Regulatory);
                ZoomOut();
                iv_regulatory.startAnimation(zoomin);
                buttonPressed = Constant.Regulatory;
                //PreferenceConnector.writeString(getContext(), Constant.ButtonPressed, Constant.Regulatory);
            }
        });


        ivSend.setOnClickListener(v -> {
            if(!internetCheck){
                Toast.makeText(getContext(), "Service is currently unavailable", Toast.LENGTH_SHORT).show();
            }else {
                if (edtName.getText().toString().equals("") || edtEmail.getText().toString().equals("")
                        || edtMobileno.getText().toString().equals("") || edtMessage.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Kindly fill all the fields", Toast.LENGTH_LONG).show();
                }else if (!isEmailValid(edtEmail.getText().toString())){
                    Toast.makeText(getContext(), "Kindly provide a correct email address", Toast.LENGTH_SHORT).show();
                }
                else if (edtMobileno.length() < 12 || edtMobileno.length() > 12 ){

                    Toast.makeText(getContext(), "Enter the correct mobile number", Toast.LENGTH_LONG).show();
                }  else{
//                    if (isEmailValid(edtEmail.getText().toString())) {
                        String [] arguments = {
                                edtName.getText().toString(),
                                edtEmail.getText().toString(),
                                edtMobileno.getText().toString(),
                                edtMessage.getText().toString(),
                                hmServiceID.get(sp_issues_Feedback.getSelectedItem().toString()),
                                getFirstsixchar,
                                issueType
                        };
                        viewModel.sendIncident(arguments);
                        postIncident();
                        edtEmail.setText("");
                        edtMessage.setText("");
                        edtMobileno.setText("");
                        edtName.setText("");
                        sp_issues_Feedback.setSelection(1);
                    }
//                else {
//
//                    }
//                }
            }

        });

        activityRootView = view.findViewById(R.id.llcontactus);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            //r will be populated with the coordinates of your view that area still visible.
            activityRootView.getWindowVisibleDisplayFrame(r);

            int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
            if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
                ll_hideKeyboard.setVisibility(View.GONE);
                ll_hideKeyboard.setOnClickListener(view13 -> {
                    //Hide Keyboard
                    @Nullable
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                });
            } else if (heightDiff < 100) {
                ll_hideKeyboard.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        count = 0;
    }

}
