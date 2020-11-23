package com.mlt.dtc.services;

import android.content.Context;
import android.util.Log;

import com.mlt.dtc.common.Common;
import com.mlt.dtc.interfaces.ServiceCallback;
import com.mlt.dtc.model.AsyncServiceCallNoDialog;
import com.mlt.dtc.model.CKeyValuePair;
import com.mlt.dtc.model.request.ClickLogServiceRequest;
import com.mlt.dtc.utility.Constant;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;
import java.util.List;




public class ClickLogService {

    ClickLogServiceRequest clickLogDetails;
    Context mContext;
    String clicklogXML, clicklogcopyXML = "", clicklogconcatenatedString = "";
    String ClassName;


    public ClickLogService(Context context) {
        mContext = context;
    }

    public void CallClickLogService(String tripID, String driverID, String eventCode, ArrayList<CKeyValuePair> listclickLog, ServiceCallback serviceCallback) {
        //get Class Name
        ClassName = getClass().getCanonicalName();

        try {
            clickLogDetails = new ClickLogServiceRequest();
            clickLogDetails.setServiceId(Constant.ServiceIdLog);
            clickLogDetails.setRequestId(Common.getUUID());
            clickLogDetails.setSourceApplication(Constant.SourceApplication);
            clickLogDetails.setRequestType(Constant.RequestType);
            clickLogDetails.setRequestCategory(Constant.RequestCategory);
            clickLogDetails.setTimeStamp(Common.getdateTime());
            ClickLogServiceRequest.Login login = new ClickLogServiceRequest.Login();
            login.setLoginID(Constant.LoginID);
            login.setPassword(Constant.Password);
            clickLogDetails.setLogin(login);
            List<CKeyValuePair> ServiceKeyValues = new ArrayList<CKeyValuePair>() {
                {
                    add(new CKeyValuePair(Constant.TripIDLog, tripID));
                    add(new CKeyValuePair(Constant.DriverIDLog, driverID));
                    add(new CKeyValuePair(Constant.EventCodeLog, eventCode));
                }
            };
            clickLogDetails.setServiceAttributeList(ServiceKeyValues);

            //Work to make a XML to the size of the list of button click log
            clickLogDetails.setCustomerUniqueNo(listclickLog);
            clicklogXML = "\t\t<nip:CKeyValuePair>\n" +
                    "               <!--Optional:-->\n" +
                    "               <nip:Key>%s</nip:Key>\n" +
                    "               <!--Optional:-->\n" +
                    "               <nip:Value>%s</nip:Value>\n" +
                    "            </nip:CKeyValuePair>\n" +
                    "\n" + "";
            for (int i = 0; i <= listclickLog.size() - 1; i++) {
                clicklogconcatenatedString = String.format(clicklogXML, clickLogDetails.getCustomerUniqueNo().get(i).getKey(),
                        clickLogDetails.getCustomerUniqueNo().get(i)
                                .getValue());
                clicklogcopyXML += clicklogconcatenatedString;
            }

            AsyncServiceCallNoDialog asyncCall = new AsyncServiceCallNoDialog(output -> {
               // Log.d("Response From Asynchronous:", (String) output);

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

                    }

                } else {
                    Log.d("Response  Asynchronous:", "error");
                    serviceCallback.onFailure("error");
                }
            }, mContext);

            // Pass the Object Here and Get the XML
            String SOAPRequestXML = createClickLogService(clickLogDetails);

            // Pass the Object Here For the Service
            asyncCall.execute(SOAPRequestXML, ServicesUrls.URL, ServicesUrls.URLSOAPAction);

        } catch (Exception e) {

        }
    }

    public String createClickLogService(ClickLogServiceRequest request) {

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
                            "            <!--Zero or more repetitions:-->\n" + clicklogcopyXML +
                            "         </tem:CustomerUniqueNo>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:ServiceAttributesList>\n" +
                            "            <!--Zero or more repetitions:-->\n" +
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
                            "         </tem:ServiceAttributesList>\n" +
                            "      </tem:InquiryRequest>\n" +
                            "   </soapenv:Body>\n" +
                            "</soapenv:Envelope>", request.getTimeStamp(), request.getSourceApplication(), request.getServiceId(),
                    request.getRequestType(), request.getRequestId(), request.getRequestCategory(),
                    request.getLogin().LoginID, request.getLogin().Password,
                    request.getServiceAttributeList().get(0).getKey(), request.getServiceAttributeList().get(0).getValue(),
                    request.getServiceAttributeList().get(1).getKey(), request.getServiceAttributeList().get(1).getValue(),
                    request.getServiceAttributeList().get(2).getKey(), request.getServiceAttributeList().get(2).getValue());

        } catch (Exception Ex) {
            Log.d("The Exception is", Ex.getLocalizedMessage());

        }

        return SOAPRequestXML;
    }


}