package com.mlt.dtc.services;

import android.content.Context;
import android.util.Log;

import com.mlt.dtc.interfaces.ServiceCallback;
import com.mlt.dtc.model.AsyncServiceCallNoDialog;
import com.mlt.dtc.model.CKeyValuePair;
import com.mlt.dtc.model.request.UpdatePaymentRequest;
import com.mlt.dtc.model.request.XMLRequestAndResponse;
import com.mlt.dtc.utility.ConfigrationDTC;
import com.mlt.dtc.utility.Constant;
import com.mlt.dtc.utility.EncryptDecrpt;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;
import java.util.List;

import static com.mlt.dtc.common.Common.currentDateTime;
import static com.mlt.dtc.common.Common.shortUUID;


public class UpdatePaymentService {

    Context mContext;

    public UpdatePaymentService(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Taxifare payment successfull update the payment
     * @param tripid
     * @param paidamount
     * @param paymentstatus
     * @param paymenttime
     * @param serviceCallback
     * @param context
     */
    public  void callUpdatepaymentService(String tripid, String paidamount, String paymentstatus, String paymenttime, ServiceCallback serviceCallback, Context context) {

        // Set the Object for the Inquiry
        UpdatePaymentRequest request = new UpdatePaymentRequest();

//        // Get the Random Generated UUID
        String RequestId = shortUUID();

        String currectDateTime = currentDateTime();

        // ********* Object For the Headers ***************//


        // Set the current Date and time
        request.setTimeStamp(currectDateTime);


        // Set the Source Application
        request.setSourceApplication(ConfigrationDTC.UPDATE_PAYMENT_SOURCE_APPLICATION);



        // Set the signature fields
        request.setSignatureFields(ConfigrationDTC.UPDATE_PAYMENT_SIGNATURE_FIELDS);

        // Set the service Id
        request.setServiceId(ConfigrationDTC.SERVICE_ID_UPDATE_PAYMENT);

        // Set the secure hash
        request.setSecureHash(EncryptDecrpt.GetSecureHashUpdatePayment(RequestId,currectDateTime,ConfigrationDTC.MERCHANT_ID,ConfigrationDTC.UPDATE_PAYMENT_SOURCE_APPLICATION,ConfigrationDTC.BANK_ID,ConfigrationDTC.UPDATE_PAYMENT_SECRET_KEY));


        // Set the Request type
        request.setRequestType(ConfigrationDTC.UPDATE_PAYMENT_REQUEST_TYPE_PAYMENT);

        // Set the Request Id
        request.setRequestId(RequestId);

        // Set the Request Category
        request.setRequestCategory(ConfigrationDTC.REQUEST_CATEGORY_UPDATE__PAYMENT);

        // Set the Merchant Id
        request.setMerchantId(ConfigrationDTC.MERCHANT_ID);

        // Set the Login
        UpdatePaymentRequest.Login login = new UpdatePaymentRequest.Login();
        login.setLoginID(Constant.LoginID);
        login.setPassword(Constant.Password);
        request.setLogin(login);


        // Set the Bank Id
        request.setBankId(ConfigrationDTC.BANK_ID);




        // Set the Body Content Here
        List<CKeyValuePair> ServiceKeyValues = new ArrayList<CKeyValuePair>() {
            {
                add(new CKeyValuePair(ConfigrationDTC.TRIPID_KEY,tripid));
                add(new CKeyValuePair(ConfigrationDTC.PAID_AMOUNT_KEY,paidamount));
                add(new CKeyValuePair(ConfigrationDTC.PAYMENT_STATUS_KEY,paymentstatus));
                add(new CKeyValuePair(ConfigrationDTC.PAYMENT_TIME_KEY,paymenttime));

            }
        };

        request.setCustomerUniqueNo(ServiceKeyValues);


        AsyncServiceCallNoDialog asyncCall = new AsyncServiceCallNoDialog(output -> {
            Log.d("Response  Asynchronous:", (String) output);

            if (output != null) {
                JSONObject jsonObj;

                try {
                    jsonObj = XML.toJSONObject((String) output);
                    JSONObject body = jsonObj.getJSONObject("s:Envelope").getJSONObject("s:Body").getJSONObject("InquiryResponse");
                    JSONObject header = jsonObj.getJSONObject("s:Envelope").getJSONObject("s:Header");

                    // Assign the response to result variable
                    serviceCallback.onSuccess(body);

                } catch (JSONException e) {
                    Log.e("JSON exception", e.getMessage());
                    serviceCallback.onFailure(e.getLocalizedMessage());
                    e.printStackTrace();
                }

            } else {
                Log.d("Response  Asynchronous:", "error");
                serviceCallback.onFailure("error");
            }
        }, context);

        // Pass the Object Here and Get the XML
        XMLRequestAndResponse req = new XMLRequestAndResponse();
        String SOAPRequestXML = req.createTaxiUpdatePaymentDetailsService(request);

        // Pass the Object Here For the Service
        asyncCall.execute(SOAPRequestXML, ServicesUrls.URL, ServicesUrls.URLSOAPAction);
    }
}
