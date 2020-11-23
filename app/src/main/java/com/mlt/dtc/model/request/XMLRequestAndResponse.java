package com.mlt.dtc.model.request;

import android.util.Log;

import com.mlt.dtc.model.CKeyValuePair;

import java.util.List;



/**
 * Created by raheel on 3/6/2018.
 */

public class XMLRequestAndResponse {

    public String createInquiryCardPayment(NipsiCounterInquiryRequest request) {

        String SOAPRequestXML = null;

        try {
            SOAPRequestXML = String.format("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">\n" +
                            "   <soapenv:Header/>\n" +
                            "   <soapenv:Body>\n" +
                            "      <tem:NipsiCounterInquiryRequest>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:RequestId>%s</tem:RequestId>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:TerminalId>%s</tem:TerminalId>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:TimeStamp>%s</tem:TimeStamp>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:SecureHash>%s</tem:SecureHash>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:SignatureField>%s</tem:SignatureField>\n" +
                            "      </tem:NipsiCounterInquiryRequest>\n" +
                            "   </soapenv:Body>\n" +
                            "</soapenv:Envelope>", request.getRequestId(), request.getTerminalId(), request.getTimeStamp(),
                    request.getSecureHash(), request.getSignatureField());

        } catch (Exception Ex) {
            Log.d("The Exception is", Ex.getLocalizedMessage());
        }

        return SOAPRequestXML;
    }

    //Creating XML for the device serial number and more useful values
    public String createInquiryDeviceSerialNumber(NipsMerchantDetailsRequest request) {

        String SOAPRequestXML = null;

        try {
            SOAPRequestXML = String.format("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">\n" +
                            "   <soapenv:Header/>\n" +
                            "   <soapenv:Body>\n" +
                            "      <tem:NipsMerchantDetailsRequest_MultipleMerchants>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:RequestId>%s</tem:RequestId>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:TimeStamp>%s</tem:TimeStamp>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:SecureHash>%s</tem:SecureHash>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:SignatureField>%s</tem:SignatureField>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:DeviceSerialNumber>%s</tem:DeviceSerialNumber>\n" +
                            "      </tem:NipsMerchantDetailsRequest_MultipleMerchants>\n" +
                            "   </soapenv:Body>\n" +
                            "</soapenv:Envelope>", request.getRequestId(), request.getTimeStamp(), request.getSecureHash(),
                    request.getSignatureField(), request.getDeviceSerialNumber());

        } catch (Exception Ex) {
            Log.d("The Exception is", Ex.getLocalizedMessage());
        }

        return SOAPRequestXML;
    }

    public String createTaxiUpdatePaymentDetailsService(UpdatePaymentRequest request) {

        String SOAPRequestXML = null;

        try {
            SOAPRequestXML = String.format("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:nip=\"http://schemas.datacontract.org/2004/07/NipsGateway.Component.DTC\">\n" +
                            "   <soapenv:Header>\n" +
                            "      <tem:TimeStamp>%s</tem:TimeStamp>\n" +
                            "      <tem:SourceApplication>%s</tem:SourceApplication>\n" +
                            "      <tem:SignatureFields>%s</tem:SignatureFields>\n" +
                            "      <tem:ServiceId>%s</tem:ServiceId>\n" +
                            "      <tem:SecureHash>%s</tem:SecureHash>\n" +
                            "      <tem:RequestType>%s</tem:RequestType>\n" +
                            "      <tem:RequestId>%s</tem:RequestId>\n" +
                            "      <tem:RequestCategory>%s</tem:RequestCategory>\n" +
                            "      <tem:MerchantId>%s</tem:MerchantId>\n" +
                            "      <tem:Login>\n" +
                            "         <!--Optional:-->\n" +
                            "         <nip:Description>?</nip:Description>\n" +
                            "         <!--Optional:-->\n" +
                            "         <nip:LoginID>%s</nip:LoginID>\n" +
                            "         <!--Optional:-->\n" +
                            "         <nip:Password>%s</nip:Password>\n" +
                            "      </tem:Login>\n" +
                            "      <tem:BankId>%s</tem:BankId>\n" +
                            "   </soapenv:Header>\n" +
                            "   <soapenv:Body>\n" +
                            "      <tem:InquiryRequest>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:CustomerUniqueNo>\n" +
                            "            <!--Zero or more repetitions:-->\n" +generateCKValuePairs(request.getCustomerUniqueNo())+
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
                            "</soapenv:Envelope>", request.getTimeStamp(), request.getSourceApplication(), request.getSignatureFields(), request.getServiceId(), request.getSecureHash(), request.getRequestType(), request.getRequestId(), request.getRequestCategory(), request.getMerchantId(),
                    request.getLogin().LoginID, request.getLogin().Password, request.getBankId());

            /*SOAPRequestXML = String.format("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:dtc=\"http://schemas.datacontract.org/2004/07/DTC_Push_Services.Common\">\n" +
                            "   <soapenv:Header>\n" +
                            "      <tem:TimeStamp>%s</tem:TimeStamp>\n" +
                            "      <tem:SourceApplication>%s</tem:SourceApplication>\n" +
                            "      <tem:SignatureFields></tem:SignatureFields>\n" +
                            "      <tem:ServiceId>%s</tem:ServiceId>\n" +
                            "      <tem:SecureHash></tem:SecureHash>\n" +
                            "      <tem:RequestType>%s</tem:RequestType>\n" +
                            "      <tem:RequestId>%s</tem:RequestId>\n" +
                            "      <tem:RequestCategory>%s</tem:RequestCategory>\n" +
                            "      <tem:MerchantId></tem:MerchantId>\n" +
                            "      <tem:Login>\n" +
                            "         <!--Optional:-->\n" +
                            "         <dtc:Description></dtc:Description>\n" +
                            "         <!--Optional:-->\n" +
                            "         <dtc:LoginID>%s</dtc:LoginID>\n" +
                            "         <!--Optional:-->\n" +
                            "         <dtc:Password>%s</dtc:Password>\n" +
                            "      </tem:Login>\n" +
                            "      <tem:BankId>?</tem:BankId>\n" +
                            "   </soapenv:Header>\n" +
                            "   <soapenv:Body>\n" +
                            "      <tem:InquiryRequest>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:CustomerUniqueNo>\n" +
                            "            <!--Zero or more repetitions:-->\n" +
                            "            <dtc:CKeyValuePair>\n" +
                            "               <!--Optional:-->\n" +
                            "               <dtc:Key>%s</dtc:Key>\n" +
                            "               <!--Optional:-->\n" +
                            "               <dtc:Value>%s</dtc:Value>\n" +
                            "            </dtc:CKeyValuePair>\n" +
                            "\n" +
                            "\t\t<dtc:CKeyValuePair>\n" +
                            "               <!--Optional:-->\n" +
                            "               <dtc:Key>%s</dtc:Key>\n" +
                            "               <!--Optional:-->\n" +
                            "               <dtc:Value>%s</dtc:Value>\n" +
                            "            </dtc:CKeyValuePair>\n" +
                            "\n" +
                            "\t\t<dtc:CKeyValuePair>\n" +
                            "               <!--Optional:-->\n" +
                            "               <dtc:Key>%s</dtc:Key>\n" +
                            "               <!--Optional:-->\n" +
                            "               <dtc:Value>%s</dtc:Value>\n" +
                            "            </dtc:CKeyValuePair>\n" +
                            "\n" +
                            "\t\t<dtc:CKeyValuePair>\n" +
                            "               <!--Optional:-->\n" +
                            "               <dtc:Key>%s</dtc:Key>\n" +
                            "               <!--Optional:-->\n" +
                            "               <dtc:Value>%s</dtc:Value>\n" +
                            "            </dtc:CKeyValuePair>\n" +
                            "\n" +
                            "\t\t<dtc:CKeyValuePair>\n" +
                            "               <!--Optional:-->\n" +
                            "               <dtc:Key>%s</dtc:Key>\n" +
                            "               <!--Optional:-->\n" +
                            "               <dtc:Value>%s</dtc:Value>\n" +
                            "            </dtc:CKeyValuePair>\n" +
                            "\n" +
                            "\t\t<dtc:CKeyValuePair>\n" +
                            "               <!--Optional:-->\n" +
                            "               <dtc:Key>%s</dtc:Key>\n" +
                            "               <!--Optional:-->\n" +
                            "               <dtc:Value>%s</dtc:Value>\n" +
                            "            </dtc:CKeyValuePair>\n" +
                            "\n" +
                            "            \n" +
                            "         </tem:CustomerUniqueNo>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:ServiceAttributesList>\n" +
                            "            <!--Zero or more repetitions:-->\n" +
                            "            <dtc:CKeyValuePair>\n" +
                            "               <!--Optional:-->\n" +
                            "               <dtc:Key>?</dtc:Key>\n" +
                            "               <!--Optional:-->\n" +
                            "               <dtc:Value>?</dtc:Value>\n" +
                            "            </dtc:CKeyValuePair>\n" +
                            "         </tem:ServiceAttributesList>\n" +
                            "      </tem:InquiryRequest>\n" +
                            "   </soapenv:Body>\n" +
                            "</soapenv:Envelope>", request.getTimeStamp(), request.getSourceApplication(), request.getServiceId(),
                    request.getRequestType(),request.getRequestId(), request.getRequestCategory(),
                    request.getLogin().LoginID, request.getLogin().Password,
                    request.getCustomerUniqueNo().get(0).getKey(), request.getCustomerUniqueNo().get(0).getValue(),
                    request.getCustomerUniqueNo().get(1).getKey(), request.getCustomerUniqueNo().get(1).getValue(),
                    request.getCustomerUniqueNo().get(2).getKey(), request.getCustomerUniqueNo().get(2).getValue(),
                    request.getCustomerUniqueNo().get(3).getKey(), request.getCustomerUniqueNo().get(3).getValue(),
                    request.getCustomerUniqueNo().get(4).getKey(), request.getCustomerUniqueNo().get(4).getValue(),
                    request.getCustomerUniqueNo().get(5).getKey(), request.getCustomerUniqueNo().get(5).getValue());*/

        } catch (Exception Ex) {
            Log.d("The Exception is", Ex.getLocalizedMessage());
        }

        return SOAPRequestXML;
    }

    private String generateCKValuePairs(List<CKeyValuePair> ckp){
        String resp = "";
        for (CKeyValuePair cKeyValuePair : ckp){
            resp+= String.format(
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


    public String generalInquiryService(InquiryRequest request) {

        String SOAPRequestXML = null;
        try {
            SOAPRequestXML = String.format("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">\n" +
                            "   <soapenv:Header>\n" +
                            "      <tem:Version>%s</tem:Version>\n" +
                            "      <tem:TimeStamp>%s</tem:TimeStamp>\n" +
                            "      <tem:Terminalid>%s</tem:Terminalid>\n" +
                            "      <tem:SourceApplication>%s</tem:SourceApplication>\n" +
                            "      <tem:SignatureFields>%s</tem:SignatureFields>\n" +
                            "      <tem:ServiceName>%s</tem:ServiceName>\n" +
                            "      <tem:ServiceId>%s</tem:ServiceId>\n" +
                            "      <tem:SecureHash>%s</tem:SecureHash>\n" +
                            "      <tem:RequestType>%s</tem:RequestType>\n" +
                            "      <tem:RequestId>%s</tem:RequestId>\n" +
                            "      <tem:RequestCategory>%s</tem:RequestCategory>\n" +
                            "      <tem:MerchantId>%s</tem:MerchantId>\n" +
                            "      <tem:Login Description=\"%s\" LoginId=\"%s\" Password=\"%s\"/>\n" +
                            "      <tem:Language>%s</tem:Language>\n" +
                            "      <tem:IpAssigned>%s</tem:IpAssigned>\n" +
                            "      <tem:BankId>%s</tem:BankId>\n" +
                            "   </soapenv:Header>\n" +
                            "   <soapenv:Body>\n" +
                            "      <tem:NPGKskInquiryRequest>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:CustomerUniqueNo>\n" +
                            "            <!--Zero or more repetitions:-->\n" +getckKeyValuePairs(request)+
                            "         </tem:CustomerUniqueNo>\n" +
                            "         <!--Optional:-->\n" +
                            "        \n" +
                            "      </tem:NPGKskInquiryRequest>\n" +
                            "   </soapenv:Body>\n" +
                            "</soapenv:Envelope>\n", request.getVersion(), request.getTimeStamp(), request.getTerminalid(),
                    request.getSourceApplication(), request.getSignatureFields(), request.getServiceName(), request.getServiceId(),
                    request.getSecureHash(), request.getRequestType(), request.getRequestId(), request.getRequestCategory(),
                    request.getMerchantId(), request.getLogin().Description, request.getLogin().LoginId, request.getLogin().Password,
                    request.getLanguage(), request.getIpAssigned(), request.getBankId());/*
                    request.getCustomerUniqueNo().get(0).getKey(), request.getCustomerUniqueNo().get(0).getValue(),
                    request.getCustomerUniqueNo().get(1).getKey(), request.getCustomerUniqueNo().get(1).getValue(),
                    request.getCustomerUniqueNo().get(2).getKey(), request.getCustomerUniqueNo().get(2).getValue(),
                    request.getCustomerUniqueNo().get(3).getKey(), request.getCustomerUniqueNo().get(3).getValue(),
                    request.getCustomerUniqueNo().get(4).getKey(), request.getCustomerUniqueNo().get(4).getValue(),
                    request.getCustomerUniqueNo().get(5).getKey(), request.getCustomerUniqueNo().get(5).getValue());*/

        } catch (Exception Ex) {
            Ex.printStackTrace();
            Log.d("The Exception is", Ex.getLocalizedMessage());
        }

        return SOAPRequestXML;
    }

    private String getckKeyValuePairs(InquiryRequest request){
        String finalString = "";
        for(CKeyValuePair cKeyValuePair :request.getCustomerUniqueNo()){
            finalString+= String.format("            <tem:CKeyValuePair Key=\"%s\" Value=\"%s\"/>\n",cKeyValuePair.getKey(), cKeyValuePair.getValue());
        }
        return finalString;
    }


}
