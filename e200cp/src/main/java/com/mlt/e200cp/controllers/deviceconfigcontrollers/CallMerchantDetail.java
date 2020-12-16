package com.mlt.e200cp.controllers.deviceconfigcontrollers;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.mlt.e200cp.controllers.servicecallers.GenericServiceCall;
import com.mlt.e200cp.interfaces.ResultsCallback;
import com.mlt.e200cp.models.requests.NipsMerchantDetailsRequest;
import com.mlt.e200cp.models.requests.NipsiCounterInquiryRequest;
import com.mlt.e200cp.models.response.MerchantDetailsService.MerchantDetails;
import com.mlt.e200cp.models.response.MerchantDetailsService.NipsMerchantDetailsResponse;
import com.mlt.e200cp.models.response.MerchantDetailsService.NipsMerchantDetailsResponseArr;
import com.mlt.e200cp.utilities.helper.util.EncryptDecrpt;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.mlt.e200cp.models.XMLRequestAndResponse.createInquiryCardPayment;
import static com.mlt.e200cp.models.XMLRequestAndResponse.createInquiryDeviceSerialNumber;
import static com.mlt.e200cp.utilities.helper.util.ISOConstant.currentDateTime;
import static com.mlt.e200cp.utilities.helper.util.ISOConstant.shortUUID;
import static com.mlt.e200cp.utilities.helper.util.Utility.SIGNATURE_FIELDS_CARD_PAYMENT;
import static com.mlt.e200cp.utilities.helper.util.Utility.SIGNATURE_FIELDS_DEVICE_SERIAL_NUMBER;
import static com.mlt.e200cp.utilities.helper.util.Utility.appendLog;
import static com.mlt.e200cp.utilities.helper.util.Utility.convertXMLtoJSON;

public class CallMerchantDetail {

    private GenericServiceCall genericServiceCall;

    JSONObject body;
    JSONObject obj;
    NipsMerchantDetailsResponse resObjectMerchantdetails;
    NipsMerchantDetailsResponseArr resObjectMerchantdetailsArr;

    public void getConfguratioParameters(String merchant,String deviceSN,SharedPreferences shared, ResultsCallback callback ){
        String params;
        if(shared.getString(merchant,null)!=null){
            callback.onResponseSuccess(shared.getString(merchant,null));
        }
        else{
            try {

                String DeviceSerialNumber = deviceSN;//"E2C1000590";//E2C1000954 /<-**Prod**/ //"E2C1000590";  /<-**UAT**/

                genericServiceCall = GenericServiceCall.getInstance("https://testpg.networkips.com/NIPSICounterService", createInquiryDeviceSerialNumber(generateRequest(DeviceSerialNumber)), "NIPSICounterService.svc", "http://tempuri.org/INIPSiCounterService/MerchantInquiry_MultipleMerchants");
                genericServiceCall.callService(new ResultsCallback() {
                    @Override
                    public String onResponseSuccess(String object) {
                        try {

                            if (object != null) {
                                try {
                                    body = convertXMLtoJSON(object).getJSONObject("s:Envelope").getJSONObject("s:Body").getJSONObject("NipsMerchantDetailsResponse_MultipleMerchants");
                                } catch (JSONException e) {
                                    appendLog(e.getLocalizedMessage());
                                    Log.e("JSON exception", e.getMessage());
                                    callback.onResponseFailure(
                                            e.getLocalizedMessage());
                                }

                            } else {

                                callback.onResponseFailure("error");
                            }

                            Gson gson = new Gson();
                            try{
                                resObjectMerchantdetails   = gson.fromJson(body.toString(), NipsMerchantDetailsResponse.class);
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }

                            try{
                                resObjectMerchantdetailsArr = gson.fromJson(body.toString(), NipsMerchantDetailsResponseArr.class);
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }

                            if(resObjectMerchantdetails == null){
                                List<MerchantDetails> merchantDetails = resObjectMerchantdetailsArr.getMerchantDetail().getMerchantMultipleDetails();
                                for (int i = 0; i < merchantDetails.size(); i++) {
                                    String Merchant = merchantDetails.get(i).getMerchantServiceType();
                                    if (merchant.split("-")[0].equalsIgnoreCase(Merchant)) {
                                        obj = new JSONObject();
                                        obj.put("DeviceSerialNumber",merchantDetails.get(i).getItemDetails().getField1());
                                        obj.put("MerchantId",merchantDetails.get(i).getItemDetails().getField2());
                                        obj.put("TerminalId",merchantDetails.get(i).getItemDetails().getField3());
                                        obj.put("SecretKey",merchantDetails.get(i).getItemDetails().getField4());
                                        obj.put("BankID",merchantDetails.get(i).getItemDetails().getField5());
                                        obj.put("PaymentPassword",merchantDetails.get(i).getItemDetails().getField6());
                                        obj.put("PaymentUserId",merchantDetails.get(i).getItemDetails().getField7());
                                        obj.put("PaymentAction",merchantDetails.get(i).getItemDetails().getField8());
                                        obj.put("Channel",merchantDetails.get(i).getItemDetails().getField9());
                                        obj.put("RequestTypePayment",merchantDetails.get(i).getItemDetails().getField10());
                                        obj.put("RequestCategory",merchantDetails.get(i).getItemDetails().getField11());
                                        obj.put("CallBackURL",merchantDetails.get(i).getItemDetails().getField12());
                                        obj.put("Language_Merchant",merchantDetails.get(i).getItemDetails().getField13());
                                        obj.put("Language_Merchant",merchantDetails.get(i).getItemDetails().getField13());
                                        obj.put("SourceApplication",merchantDetails.get(i).getItemDetails().getField14());
                                        obj.put("Currency",merchantDetails.get(i).getItemDetails().getField15());
                                        obj.put("DeviceFingerPrint",merchantDetails.get(i).getItemDetails().getField16());
                                        obj.put("Is3DSecure",merchantDetails.get(i).getItemDetails().getField17());
                                        obj.put("SilentOrderAPIURL",merchantDetails.get(i).getItemDetails().getField18());
                                        obj.put("LoginId",merchantDetails.get(i).getItemDetails().getField28());
                                        obj.put("Password",merchantDetails.get(i).getItemDetails().getField29());
                                        obj.put("PaymentSecretKey",merchantDetails.get(i).getItemDetails().getField30());


                                    }/*else{
                                    callback.onResponseFailure("Error Configuration not found");
                                }*/


                                }
                            }else {
                                MerchantDetails details = resObjectMerchantdetails.getMerchantDetail().getMerchantMultipleDetails();
                                obj = new JSONObject();
                                obj.put("DeviceSerialNumber",details.getItemDetails().getField1());
                                obj.put("MerchantId",details.getItemDetails().getField2());
                                obj.put("TerminalId",details.getItemDetails().getField3());
                                obj.put("SecretKey",details.getItemDetails().getField4());
                                obj.put("BankID",details.getItemDetails().getField5());
                                obj.put("PaymentPassword",details.getItemDetails().getField6());
                                obj.put("PaymentUserId",details.getItemDetails().getField7());
                                obj.put("PaymentAction",details.getItemDetails().getField8());
                                obj.put("Channel",details.getItemDetails().getField9());
                                obj.put("RequestTypePayment",details.getItemDetails().getField10());
                                obj.put("RequestCategory",details.getItemDetails().getField11());
                                obj.put("CallBackURL",details.getItemDetails().getField12());
                                obj.put("Language_Merchant",details.getItemDetails().getField13());
                                obj.put("Language_Merchant",details.getItemDetails().getField13());
                                obj.put("SourceApplication",details.getItemDetails().getField14());
                                obj.put("Currency",details.getItemDetails().getField15());
                                obj.put("DeviceFingerPrint",details.getItemDetails().getField16());
                                obj.put("Is3DSecure",details.getItemDetails().getField17());
                                obj.put("SilentOrderAPIURL",details.getItemDetails().getField18());
                                obj.put("LoginId",details.getItemDetails().getField28());
                                obj.put("Password",details.getItemDetails().getField29());
                                obj.put("PaymentSecretKey",details.getItemDetails().getField30());
                            }
                            genericServiceCall = GenericServiceCall.getInstance("https://testpg.networkips.com/NIPSICounterService", createInquiryCardPayment(generatePlainTextRequest(obj.optString("TerminalId"))), "NIPSICounterService.svc", "http://tempuri.org/INIPSiCounterService/Inquiry");
                            genericServiceCall.callService(new ResultsCallback() {
                                @Override
                                public String onResponseSuccess(String data) {
                                    if (data != null) {
                                        try {
                                            body = convertXMLtoJSON(data).getJSONObject("s:Envelope").getJSONObject("s:Body").getJSONObject("NipsiCounterInquiryResponse");
                                            if(body.getString("ReasonCode").equals("0000")){
                                                obj.put("plaintext",body.getString("PlainText"));
                                                shared.edit().putString(merchant,obj.toString()).commit();
                                                callback.onResponseSuccess(obj.toString());
                                            }else{
                                                callback.onResponseFailure("Failed");
                                            }
                                        } catch (JSONException e) {
                                            appendLog(e.getLocalizedMessage());
                                            Log.e("JSON exception", e.getMessage());
                                            callback.onResponseFailure(e.getLocalizedMessage());
                                            e.printStackTrace();
                                            appendLog(e.getLocalizedMessage());
                                        }

                                    } else {

                                        callback.onResponseFailure("error");
                                    }
                                    return null;
                                }

                                @Override
                                public String onResponseFailure(String t) {
                                    callback.onResponseFailure("error");
                                    return null;
                                }
                            });

                        }
                        catch (Exception e) {
                            appendLog(e.getLocalizedMessage());
                            e.getMessage();
                        }
                        return null;
                    }

                    @Override
                    public String onResponseFailure(String t) {
                        callback.onResponseFailure("error");
                        return null;
                    }

                });
            } catch (Exception ex) {
                ex.getStackTrace();

            }

        }
    }

    private NipsMerchantDetailsRequest generateRequest(String DeviceSerialNumber){
        // Set the Object for the Inquiry
        NipsMerchantDetailsRequest request = new NipsMerchantDetailsRequest();

        // ********* Object For the Headers ***************//

        // Get the Random Generated UUID
        String RequestId = shortUUID();
        String currectDateTime = currentDateTime();

        // Set the Request Id
        request.setRequestId(RequestId);

        // Set the Terminal ID from Configration file
        request.setDeviceSerialNumber(DeviceSerialNumber);

        // Take the current date time format
        request.setTimeStamp(currectDateTime);

        // Set the secure hash
        request.setSecureHash(EncryptDecrpt.GetSecureHashDeviceSerialNumber(RequestId, currectDateTime, DeviceSerialNumber));

        // Set the Signature field
        request.setSignatureField(SIGNATURE_FIELDS_DEVICE_SERIAL_NUMBER);

        return request;
    }

    private NipsiCounterInquiryRequest generatePlainTextRequest( String terminalID) {

        // Set the Object for the Inquiry
        NipsiCounterInquiryRequest request = new NipsiCounterInquiryRequest();

        // ********* Object For the Headers ***************//

        // Get the Random Generated UUID
        String RequestId = shortUUID();
        String currectDateTime = currentDateTime();

        // Set the Request Id
        request.setRequestId(RequestId);

        // Set the Terminal ID from Configration file
        request.setTerminalId(terminalID);

        // Take the current date time format
        request.setTimeStamp(currectDateTime);

        // Set the secure hash
        request.setSecureHash(EncryptDecrpt.GetSecureHashCardPayment(RequestId, currectDateTime, terminalID));

        // Set the Signature field
        request.setSignatureField(SIGNATURE_FIELDS_CARD_PAYMENT);

        return request;

    }
}
