package com.mlt.dtc.services;

import android.content.Context;
import android.util.Log;

import com.mlt.dtc.interfaces.ServiceCallback;
import com.mlt.dtc.model.CKeyValuePair;
import com.mlt.dtc.model.TaxiMeterInfoRequestDetails;
import com.mlt.dtc.networking.ServicesUrls;
import com.mlt.dtc.utility.AsyncServiceCall;
import com.mlt.dtc.utility.Constant;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;
import java.util.List;

public
class TaxiMeterInfoService {
    TaxiMeterInfoRequestDetails taxiMeterInfoRequestDetails;
    Context mContext;

    public TaxiMeterInfoService(Context context) {
        mContext = context;
    }

    public void CallTaxiMeterInfoService(String ServiceId, String RequestId, String SourceApplication, String RequestType,
                                         String RequestCategory, String TimeStamp, String LoginId, String Password,
                                         String androidDeviceIdvalue, String androidSerialNovalue, String deviceSerialNovalue,
                                         String plateNovalue, String taxiCodevalue, String datetimevalue, ServiceCallback serviceCallback) {


        try {
            taxiMeterInfoRequestDetails = new TaxiMeterInfoRequestDetails();
            taxiMeterInfoRequestDetails.setServiceId(ServiceId);
            taxiMeterInfoRequestDetails.setRequestId(RequestId);
            taxiMeterInfoRequestDetails.setSourceApplication(SourceApplication);
            taxiMeterInfoRequestDetails.setRequestType(RequestType);
            taxiMeterInfoRequestDetails.setRequestCategory(RequestCategory);
            taxiMeterInfoRequestDetails.setTimeStamp(TimeStamp);
            TaxiMeterInfoRequestDetails.Login login = new TaxiMeterInfoRequestDetails.Login();
            login.setLoginID(LoginId);
            login.setPassword(Password);
            taxiMeterInfoRequestDetails.setLogin(login);
            List<CKeyValuePair> ServiceKeyValues = new ArrayList<CKeyValuePair>() {
                {
                    add(new CKeyValuePair(Constant.androidDeviceId, androidDeviceIdvalue));
                    add(new CKeyValuePair(Constant.androidSerialNo, androidSerialNovalue));
                    add(new CKeyValuePair(Constant.deviceSerialNo, deviceSerialNovalue));
                    add(new CKeyValuePair(Constant.plateNo, plateNovalue));
                    add(new CKeyValuePair(Constant.taxiCode, taxiCodevalue));
                    add(new CKeyValuePair(Constant.datetime, datetimevalue));
                }
            };
            taxiMeterInfoRequestDetails.setCustomerUniqueNo(ServiceKeyValues);

            AsyncServiceCall asyncCall = new AsyncServiceCall(output -> {
                Log.d("Response  Asynchronous:", (String) output);

                if (output != null) {
                    JSONObject jsonObj = null;

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
            }, mContext);

            // Pass the Object Here and Get the XML
            String SOAPRequestXML = createTaxiMeterInfoService(taxiMeterInfoRequestDetails);

            // Pass the Object Here For the Service
            asyncCall.execute(SOAPRequestXML, ServicesUrls.URL, ServicesUrls.URLSOAPAction);

        } catch (Exception e) {
            e.getMessage();
        }
    }

    public String createTaxiMeterInfoService(TaxiMeterInfoRequestDetails request) {

        String SOAPRequestXML = null;

        try {
            SOAPRequestXML = String.format("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:nip=\"http://schemas.datacontract.org/2004/07/NipsGateway.Component.DTC\">\n" +
                            "   <soapenv:Header>\n" +
                            "      <tem:TimeStamp>%s</tem:TimeStamp>\n" +
                            "      <tem:SourceApplication>%s</tem:SourceApplication>\n" +
                            "      <tem:SignatureFields>sdfadfsd</tem:SignatureFields>\n" +
                            "      <tem:ServiceId>%s</tem:ServiceId>\n" +
                            "      <tem:SecureHash>asd</tem:SecureHash>\n" +
                            "      <tem:RequestType>%s</tem:RequestType>\n" +
                            "      <tem:RequestId>%s</tem:RequestId>\n" +
                            "      <tem:RequestCategory>%s</tem:RequestCategory>\n" +
                            "      <tem:MerchantId>asadffdf</tem:MerchantId>\n" +
                            "      <tem:Login>\n" +
                            "         <!--Optional:-->\n" +
                            "         <nip:Description>?</nip:Description>\n" +
                            "         <!--Optional:-->\n" +
                            "         <nip:LoginID>%s</nip:LoginID>\n" +
                            "         <!--Optional:-->\n" +
                            "         <nip:Password>%s</nip:Password>\n" +
                            "      </tem:Login>\n" +
                            "      <tem:BankId>?</tem:BankId>\n" +
                            "   </soapenv:Header>\n" +
                            "   <soapenv:Body>\n" +
                            "      <tem:InquiryRequest>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:CustomerUniqueNo>\n" +
                            "            <!--Zero or more repetitions:-->\n" +generateCKValuePairs(request.getCustomerUniqueNo())+/*
                            "            <nip:CKeyValuePair>\n" +
                            "               <!--Optional:-->\n" +
                            "               <nip:Key>%s</nip:Key>\n" +
                            "               <!--Optional:-->\n" +
                            "               <nip:Value>%s</nip:Value>\n" +
                            "            </nip:CKeyValuePair>\n" +
                            "\n" +
                            "\t\t<nip:CKeyValuePair>\n" +
                            "               <!--Optional:-->\n" +
                            "               <nip:Key>%s</nip:Key>\n" +
                            "               <!--Optional:-->\n" +
                            "               <nip:Value>%s</nip:Value>\n" +
                            "            </nip:CKeyValuePair>\n" +
                            "\n" +
                            "\t\t<nip:CKeyValuePair>\n" +
                            "               <!--Optional:-->\n" +
                            "               <nip:Key>%s</nip:Key>\n" +
                            "               <!--Optional:-->\n" +
                            "               <nip:Value>%s</nip:Value>\n" +
                            "            </nip:CKeyValuePair>\n" +
                            "\n" +
                            "\t\t<nip:CKeyValuePair>\n" +
                            "               <!--Optional:-->\n" +
                            "               <nip:Key>%s</nip:Key>\n" +
                            "               <!--Optional:-->\n" +
                            "               <nip:Value>%s</nip:Value>\n" +
                            "            </nip:CKeyValuePair>\n" +
                            "\n" +
                            "\t\t<nip:CKeyValuePair>\n" +
                            "               <!--Optional:-->\n" +
                            "               <nip:Key>%s</nip:Key>\n" +
                            "               <!--Optional:-->\n" +
                            "               <nip:Value>%s</nip:Value>\n" +
                            "            </nip:CKeyValuePair>\n" +
                            "\n" +
                            "\t\t<nip:CKeyValuePair>\n" +
                            "               <!--Optional:-->\n" +
                            "               <nip:Key>%s</nip:Key>\n" +
                            "               <!--Optional:-->\n" +
                            "               <nip:Value>%s</nip:Value>\n" +
                            "            </nip:CKeyValuePair>\n" +
                            "\n" +*/
                            "         </tem:CustomerUniqueNo>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:ServiceAttributesList>\n" +
                            "            <!--Zero or more repetitions:-->\n" +
                            "            <nip:CKeyValuePair>\n" +
                            "               <!--Optional:-->\n" +
                            "               <nip:Key>?</nip:Key>\n" +
                            "               <!--Optional:-->\n" +
                            "               <nip:Value>?</nip:Value>\n" +
                            "            </nip:CKeyValuePair>\n" +
                            "         </tem:ServiceAttributesList>\n" +
                            "      </tem:InquiryRequest>\n" +
                            "   </soapenv:Body>\n" +
                            "</soapenv:Envelope>", request.getTimeStamp(), request.getSourceApplication(), request.getServiceId(),
                    request.getRequestType(),request.getRequestId(), request.getRequestCategory(),
                    request.getLogin().LoginID, request.getLogin().Password);

        } catch (Exception Ex) {
            Log.d("The Exception is", Ex.getLocalizedMessage());
        }

        return SOAPRequestXML;
    }






    private String generateCKValuePairs(List<CKeyValuePair> ckp){
        String resp = "";
        for (CKeyValuePair cKeyValuePair : ckp){
            resp+=String.format(
                    "            <nip:CKeyValuePair>\n" +
                            "               <!--Optional:-->\n" +
                            "               <nip:Key>%s</nip:Key>\n" +
                            "               <!--Optional:-->\n" +
                            "               <nip:Value>%s</nip:Value>\n" +
                            "            </nip:CKeyValuePair>\n" +
                            "\n",cKeyValuePair.getKey(),cKeyValuePair.getValue());
        }

        return resp;
    }
}
