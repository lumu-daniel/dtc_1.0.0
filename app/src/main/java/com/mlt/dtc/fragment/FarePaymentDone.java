package com.mlt.dtc.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.mlt.dtc.R;
import com.mlt.dtc.activity.MainActivity;
import com.mlt.dtc.common.Common;
import com.mlt.dtc.common.PreferenceConnector;
import com.mlt.dtc.interfaces.FareAfterPaymentListener;
import com.mlt.dtc.interfaces.MerchantDetailsCallback;
import com.mlt.dtc.interfaces.ServiceCallback;
import com.mlt.dtc.interfaces.ServiceCallbackCyberSource;
import com.mlt.dtc.model.PushDetails;
import com.mlt.dtc.model.request.HTMLRequestAndRequest;
import com.mlt.dtc.model.request.PaymentRequest;
import com.mlt.dtc.model.response.NipsiCounterInquiryResponse;
import com.mlt.dtc.model.response.PaymentResponse;
import com.mlt.dtc.services.ServiceCallLogService;
import com.mlt.dtc.services.UpdatePaymentService;
import com.mlt.dtc.utility.ConfigrationDTC;
import com.mlt.dtc.utility.ConfigurationClass;
import com.mlt.dtc.utility.Constant;
import com.mlt.dtc.utility.EncryptDecrpt;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


import java.io.IOException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import static android.content.ContentValues.TAG;
import static com.mlt.dtc.MainApp.AndroidSerialNo;
import static com.mlt.dtc.common.Common.RemoveEscapeSequence;
import static com.mlt.dtc.common.Common.currentDateTime;
import static com.mlt.dtc.common.Common.getFormattedExpiryDateRTA;
import static com.mlt.dtc.common.Common.shortUUID;


public class FarePaymentDone extends Fragment {

    Fragment mFragment;
    private String CardType;
    private String Amount;
    private String DashDTC;
    private String getEncryptedCardNumber;
    private String getEncryptedCardType;
    private String getEncryptedCardCVV;
    private String getEncryptedCardExpiry;
    private RelativeLayout ll_Payment;
    private String DeviceSerialNumber;
    String ClassName, Fare;
    String UUID, CVV;
    private static PaymentResponse paymentResponse;
    private NipsiCounterInquiryResponse resObject;
    RelativeLayout ll_Webview, ll_Paymentpaid;
    boolean webviewShown = true, webviewCrossCheck;
    private Handler handler;
    private TextView tv_paymentissuccessFull;
    private TextView tv_paymentthankyouDTC;
    Button btnrtapaycomHomeDTC;
    private static FareAfterPaymentListener fareAfterPaymentListener;
    PushDetails pushDetailsTripEnd;
    ProgressBar progressBar;


    ProgressDialog progressDialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    public static FarePaymentDone newInstance() {
        return new FarePaymentDone();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        View view = inflater.inflate(R.layout.farepaymentdone, null);

        progressBar=view.findViewById(R.id.progressbar);
        ll_Payment = view.findViewById(R.id.ll_paymentdtc);
        ll_Webview = view.findViewById(R.id.ll_webviewdtc);
        ll_Paymentpaid = view.findViewById(R.id.ll_paymentpaiddtc);
        tv_paymentissuccessFull = view.findViewById(R.id.tv_paymentissuccessfull);
        tv_paymentthankyouDTC = view.findViewById(R.id.tv_paymentthankyoudtc);
        btnrtapaycomHomeDTC = view.findViewById(R.id.btnrtapaycomhomedtc);
        TextView tv_timer = view.findViewById(R.id.rta_mainServicestimer);
        TextView tv_Seconds = view.findViewById(R.id.textView4);


        try {

            pushDetailsTripEnd = (PushDetails) Common.readObject(getContext(), Constant.PushDetailsTripEnd);


        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        btnrtapaycomHomeDTC.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        });

        //get Class Name
        ClassName = getClass().getCanonicalName();
        //Random UUID
        UUID = Common.getUUID();
        WebView webView = view.findViewById(R.id.webviewdtc);
        //Get CVV form EnterCVV class
        CVV = bundle.getString(ConfigrationDTC.CardCVV);
        String firstName = bundle.getString(ConfigrationDTC.CardFirstName);
        String lastName = bundle.getString(ConfigrationDTC.CardLastName);
        String cardExpiry = bundle.getString(ConfigrationDTC.CardExpiry);
        cardExpiry = getFormattedExpiryDateRTA(cardExpiry);
//        CardExpiry = "06-2026";

        String cardCVV = CVV;

        String cardNumber = bundle.getString(ConfigrationDTC.CardNumber); //4000000000000002  <--- with otp //"4111111111111111" <--- without otp
//        String cardNumber = "4242424242424242";//"5200000000000007";//"4000000000000002";//"5555555555554444"; //4000000000000002  <--- with otp //"4111111111111111" <--- without otp
        //This will be uncommented when the testing with 1 dhs amount is done
        //Amount = "96";
        Amount = Fare = PreferenceConnector.readString(getContext(), Constant.Fare, null);
//        Amount =Amount;


        String transactionID = ConfigrationDTC.DashDTC + Common.getRandomNo();
        //TransactionID = UUID;//"12345678989";  //driverID-TripID -->for production
        if (cardNumber.charAt(0) == '4') {
            //if card starts from 4 its VISA, 001
            CardType = "001";
        } else if (cardNumber.charAt(0) == '5') {
            //if card starts from 5 its MASTER, 002
            CardType = "002";
        }
        String IPAddress = "127.0.0.1";
        String deviceID = AndroidSerialNo;
        String timeStampInMilli = Common.getdateTimeInMilli();
        String customerCountry = ConfigrationDTC.Country;
        String customerCity = ConfigrationDTC.City;
        String customerState = ConfigrationDTC.City;
        String GMAILCOM = ConfigrationDTC.GMAILCOM;
        String customerAddress = deviceID + " " + timeStampInMilli + " " + firstName + " " + lastName + " " + customerCity;
        String customerEmail = firstName + ConfigrationDTC.DOTSEP + lastName + GMAILCOM;
        String postalCode = ConfigrationDTC.PostalCode;
        String customerContactNumber = ConfigrationDTC.ContactNumber;

        UpdatePaymentService updatePaymentService=new UpdatePaymentService(getContext());
//        try {
//            ServiceLayer.callInquiryCardPayment(new ServiceCallback() {
//                @Override
//                public void onSuccess(JSONObject obj) throws JSONException {
//                    Log.d(TAG, "callInquiryCardPayment: "+obj.toString());
//                    //Work for timer
////                    cTimer = Common.SetCountDownTimer(cTimer, 60000, 1000, tv_timer, MainBannerVideoFragment.newInstance(), getFragmentManager());

        try {
            Gson gson = new Gson();
//                        resObject = gson.fromJson(obj.toString(), NipsiCounterInquiryResponse.class);

            String getCipherText = EncryptDecrpt.Decrypt(ConfigurationClass.plainText, ConfigurationClass.TERMINAL_ID);

            getEncryptedCardNumber =RemoveEscapeSequence(EncryptDecrpt.Encrypt(cardNumber, getCipherText)); //UAT123456987303A000578UAT123456987R
            getEncryptedCardExpiry =RemoveEscapeSequence(EncryptDecrpt.Encrypt(cardExpiry, getCipherText));
            getEncryptedCardType = RemoveEscapeSequence(EncryptDecrpt.Encrypt(CardType, getCipherText));
            getEncryptedCardCVV = RemoveEscapeSequence(EncryptDecrpt.Encrypt(cardCVV, getCipherText));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        //Calling the service to log this services
        ServiceCallLogService serviceCallLogService = new ServiceCallLogService(getContext());
        serviceCallLogService.CallServiceCallLogService(Constant.nameDTCServices, Constant.name_RTA_ThreeD_Secure_Service_desc);



        try {

            threeDSecurePayment(getEncryptedCardNumber, getEncryptedCardExpiry, Amount, transactionID,
                    transactionID, getEncryptedCardType, getEncryptedCardCVV, IPAddress,
                    webView, firstName, lastName, customerAddress, customerCity, postalCode,
                    customerState, customerCountry,
                    customerEmail, customerContactNumber, new ServiceCallbackCyberSource() {
                        @Override
                        public void onSuccess(PaymentResponse obj) {
                            Log.e("TTT", obj.Message);
                            if (obj.ReasonCode.equals("100")) {
                                ll_Paymentpaid.setVisibility(View.VISIBLE);
                                ll_Payment.setVisibility(View.GONE);
                                tv_paymentthankyouDTC.setText(R.string.PaymentThankyoudtc);
                                tv_paymentissuccessFull.setText(R.string.PaymentisCompleteddtc);
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                PreferenceConnector.writeBoolean(getContext(),Constant.PaymentStatus,true);

                                updatePaymentService.callUpdatepaymentService(pushDetailsTripEnd.getTripId(), Amount, obj.TransactionStatus, Common.getdateTimeUpdatePay(), new ServiceCallback() {
                                    @Override
                                    public void onSuccess(JSONObject obj) throws JSONException {
                                        Log.d(TAG, "callUpdatepaymentService: "+obj.toString());
                                        dismissDialog();
                                    }

                                    @Override
                                    public void onFailure(String obj) {
                                        dismissDialog();
                                    }
                                },getContext());


//                                            If the payment is paid call the function to set the fare in FareFragment class
                                if (fareAfterPaymentListener != null) {
                                    fareAfterPaymentListener.FareAfterPaymentCallBackMethod(getContext(), "100");
                                }
                                Timer timer = new Timer();

                            } else {


                                dismissDialog();

                                PreferenceConnector.writeBoolean(getContext(),Constant.PaymentStatus,false);

                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                updatePaymentService.callUpdatepaymentService(pushDetailsTripEnd.getTripId(), Amount, obj.TransactionStatus, Common.getdateTimeUpdatePay(), new ServiceCallback() {
                                    @Override
                                    public void onSuccess(JSONObject obj) throws JSONException {
                                        Log.d(TAG, "callUpdatepaymentService: "+obj.toString());
                                                dismissDialog();

                                    }

                                    @Override
                                    public void onFailure(String obj) {

                                                dismissDialog();

                                    }
                                },getContext());

                                ll_Paymentpaid.setVisibility(View.VISIBLE);
                                ll_Payment.setVisibility(View.GONE);
                                tv_paymentissuccessFull.setText(R.string.PaymentisFaileddtc);
                                tv_paymentthankyouDTC.setText(R.string.PaymentSorrydtc);
                            }

                        }

                        @Override
                        public void onFailure(String obj) {
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    dismissDialog();
                            ll_Paymentpaid.setVisibility(View.VISIBLE);
                            ll_Payment.setVisibility(View.GONE);
                            tv_paymentissuccessFull.setText(R.string.PaymentisFaileddtc);
                            tv_paymentthankyouDTC.setText(R.string.PaymentSorrydtc);

                        }
                    });

        } catch (Exception e) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    dismissDialog();

        }


//                @Override
//                public void onFailure(String obj) {
////                    progressDialog.dismiss();
//                    ll_Paymentpaid.setVisibility(View.VISIBLE);
//                    ll_Payment.setVisibility(View.GONE);
//
//                            dismissDialog();
//                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//
//                    tv_paymentissuccessFull.setText(R.string.PaymentisFaileddtc);
//                    tv_paymentthankyouDTC.setText(R.string.PaymentSorrydtc);
//
//                }

//            }, ConfigurationClass.TERMINAL_ID, ConfigurationClass.SIGNATURE_FIELDS_CARD_PAYMENT, getContext());


//        } catch (Exception ex) {
//            dismissDialog();
//
//        }

        return view;
    }


    //Set Call back method to set the fare in Fareframent
    public static void setFareAfterPaymentCallBackMethod(FareAfterPaymentListener CallBack) {
        fareAfterPaymentListener = CallBack;
    }

    //Set Call back method to get the merchant details values from the service
    public static void setMerchantDetailsCallBackMethod(MerchantDetailsCallback CallBack) {
    }

    /**
     * 3D secure Service For Card Payment
     * WITH OTP Processing
     *
     * @param CardNumber
     * @param CardExpiry
     * @param Amount
     * @param TransactionReferenceNo
     * @param ReferenceNumber
     * @param CardType
     * @param CVV
     * @param IpAddress
     * @param webView
     * @param FirstName
     * @param LastName
     * @param CustomerAddress
     * @param CustomerCity
     * @param PostalCode
     * @param CustomerState
     * @param CustomerCountry
     * @param CustomerEmail
     * @param CustomerContactNumber
     * @param callback
     */
    public void threeDSecurePayment(String CardNumber, String CardExpiry, String Amount, String TransactionReferenceNo,
                                    String ReferenceNumber, String CardType, String CVV, String IpAddress,
                                    WebView webView, String FirstName, String LastName, String CustomerAddress,
                                    String CustomerCity, String PostalCode, String CustomerState, String CustomerCountry,
                                    String CustomerEmail, String CustomerContactNumber,
                                    final ServiceCallbackCyberSource callback) {

        // Web View Setting Object here
        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        //webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onLoadResource(WebView view, String url) {

                if (url.contains("networkips.com")) {

                    webviewCrossCheck = false;


                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                if (!url.contains("networkips.com")) {

                    if(url.equalsIgnoreCase("Error")){
                        callback.onFailure("Transaction error");
                    }else{
                        webviewCrossCheck = true;
                        if (webviewShown) {

                            handler = new Handler();
                            handler.postDelayed(() -> {
                                if (webviewCrossCheck) {

                                    ll_Payment.setVisibility(View.GONE);
                                    ll_Webview.setVisibility(View.VISIBLE);
                                    webviewShown = false;
                                    webviewCrossCheck = false;
                                    dismissDialog();

                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                }
                            }, 10000);
                        }
                    }

                } else {
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    ll_Payment.setVisibility(View.VISIBLE);
                    ll_Webview.setVisibility(View.GONE);
                    ShowWebView(view, callback);
                }
            }
        });

        // Start the Request Object from Here
        PaymentRequest request = new PaymentRequest();

        // Get the Random Generated UUID
        String RequestId = shortUUID();
        //String RequestId = "95fea68d-22f3-471a-ab9b-17e9a9433bb2";
        String currectDateTime = currentDateTime();

        // Set the action here
        request.setAction(ConfigurationClass.PAYMENT_ACTION);

        // Set the request ID
        request.setRequestId(RequestId);

        // Set the Request Category
        request.setRequestCategory("sale");

        // Set the Source Application here
        //request.setSourceApplication(ConfigurationClass.PAYMENT_SOURCE_APPLICATION);//UAT
        request.setSourceApplication(ConfigurationClass.PAYMENT_SOURCE_APPLICATION);//Prod

        // Set the Device Finger Print
        request.setDeviceFingerPrint(ConfigurationClass.PAYMENT_DEVICE_FINGER_PRINT);

        // set the Merchant ID
        request.setMerchantId(ConfigurationClass.MERCHANT_ID);

        // Set the User ID
        request.setUserId(ConfigurationClass.PAYMENT_USER_ID);

        // Set the Password
        request.setPassword(ConfigurationClass.PAYMENT_PASSWORD);

        // Set the Ip ASSIGNED
        request.setIpAssigned(ConfigurationClass.PAYMENT_IP_ASSIGNED);
        //request.setIpAssigned(DeviceInformation.getLocalIpAddress());

        // Set the Version
        request.setVersion(ConfigrationDTC.PAYMENT_VERSION);

        // Set the Service Id
        request.setServiceId(ConfigurationClass.PAYMENT_SERVICE_ID);

        // Set the Service Name
        request.setServiceName(ConfigurationClass.PAYMENT_SERVICE_NAME);

        // Set the TimeStamp
        request.setTimeStamp(currectDateTime);
        //request.setTimeStamp("19062017 12:11:34");

        // Set the Transaction Reference Number
        request.setTransactionReferenceNumber(TransactionReferenceNo);

        // Set the Reference Number
        request.setReferenceNumber(ReferenceNumber);

        // Set the Currency
        request.setCurrency(ConfigurationClass.PAYMENT_CURRENCY);

        // Set the amount to be passed from the service
        request.setAmount(Amount);

        // Set the First Name
        request.setFirstName(FirstName);

        // Set the Second Name
        request.setLastName(LastName);

        // Set the Customer Address
        request.setCustomerAddress(CustomerAddress);

        // Set the Customer City
        request.setCustomerCity(CustomerCity);

        // Set the Customer Postal Code
        request.setCustomerPostalCode(PostalCode);

        // Set the Customer State
        request.setCustomerState(CustomerState);

        // Set the Customer Country
        request.setCustomerCountry(CustomerCountry);

        // Set the Customer Email
        request.setCustomerEmail(CustomerEmail);

        // Set the Customer Contact Number
        request.setCustomerContactNumber(CustomerContactNumber);

        // Set the Callback Url
        request.setCallBackUrl(ConfigurationClass.PAYMENT_CALL_BACK_URL);

        // Set the Payment Channel
        request.setPaymentChannel(ConfigurationClass.PAYMENT_CHANNEL);

        // Set the Language
        request.setLanguage(ConfigurationClass.PAYMENT_LANGUAGE);

        // Set the Sub Merchant Id
        request.setSubMerchantID(ConfigrationDTC.PAYMENT_SUB_MERCHANT_ID);

        // Set the Signature Fields
        request.setSignatureFields(ConfigrationDTC.PAYMENT_SIGNATURE_FIELDS);

        // Set the secure hash
        request.setSecureHash(EncryptDecrpt.GetSecureHash3DSecure(RequestId, currectDateTime, ReferenceNumber, ConfigurationClass.MERCHANT_ID, Amount, ConfigurationClass.PAYMENT_SECRET_KEY));

//        // Set the secure hash
//        request.setSecureHash(EncryptDecrpt.GetSecureHash3DSecure(RequestId, currectDateTime, ReferenceNumber, ConfigurationClass.MERCHANT_ID, Amount, "Nips@2018#!@#$"));

        // Set the Card Number
        request.setCardNumber(CardNumber);

        // Set the Card Type
        request.setCardType(CardType);

        // Set the CVV
        request.setCVV(CVV);

        // Set the Card Expiry
        request.setCardExpiry(CardExpiry);

        // Set the Customer Ip Address
        request.setCustomerIPAddress(IpAddress);

        // Pass the Object Here and Get the XML
        HTMLRequestAndRequest req = new HTMLRequestAndRequest();
        String SOAPRequestXML = req.paymentRequest(request);

        webView.loadData(SOAPRequestXML, "text/html", "UTF-8");


//        Handler handler=new Handler();
//
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if(progressBar.isShown()){
//
//
//                    Utilities.TimeoutAlertDialogDelay(getContext());
//                }
//
//            }
//        },180000);

    }



    private void ShowWebView(WebView view, ServiceCallbackCyberSource callback) {

        // do your stuff here
        view.evaluateJavascript("(function(){return document.getElementById(\"FullResponse\").innerHTML})();",
                html -> {
                    try {

                        if (!TextUtils.isEmpty(html) && !html.equals("null")) {
                            html = StringEscapeUtils.unescapeJava(html);
                            Document doc = Jsoup.parse(html);
                            Map<String, Object> data = new HashMap<String, Object>();

                            for (Element input : doc.select("input")) {

                                data.put(input.attr("id"), input.attr("value"));

                            }

                            JSONObject json = new JSONObject(data);
                            Gson gson = new Gson();

                            paymentResponse = gson.fromJson(json.toString(), PaymentResponse.class);

                            ll_Webview.setVisibility(View.GONE);
                            ll_Payment.setVisibility(View.VISIBLE);
                            Constant.paymentDialog = ProgressDialog.show(getContext(),"","");

                           /* ll_Paymentpaid.setVisibility(View.VISIBLE);
                            tv_Referenceno.setText(" " + TransactionID);*/

                            System.out.println(paymentResponse);
                            //Set the response in the call back
                            callback.onSuccess(paymentResponse);

                                    dismissDialog();





                        }
                        else {
                            dismissDialog();
                        }

                    } catch (Exception ex) {

                        dismissDialog();
                        // Assign the response to result variable
                        callback.onFailure(ex.getLocalizedMessage());

                    }

                });
    }

    public void addFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.mainview, mFragment)
                .addToBackStack(null)
                .commit();
    }

    public void onBackPressed() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.popBackStack(fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-4).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void dismissDialog(){
        if(Constant.paymentDialog!=null){
            if(Constant.paymentDialog.isShowing()){

                        Constant.paymentDialog.dismiss();


            }
        }
    }


}