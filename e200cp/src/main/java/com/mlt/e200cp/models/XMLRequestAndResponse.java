package com.mlt.e200cp.models;

import android.util.Log;

import com.mlt.e200cp.models.repository.requests.ISOPaymentRequest;
import com.mlt.e200cp.models.repository.requests.NipsMerchantDetailsRequest;
import com.mlt.e200cp.models.repository.requests.NipsiCounterInquiryRequest;
import com.mlt.e200cp.models.repository.requests.PaymentConfiguration;

import static com.mlt.e200cp.utilities.helper.util.Utility.appendLog;

public class XMLRequestAndResponse {

    public static String createInquiryDeviceSerialNumber(NipsMerchantDetailsRequest request) {

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

    public static String createInquiryCardPayment(NipsiCounterInquiryRequest request) {

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

    //RMS Digital pass

    public String ISOPaymentRequest(ISOPaymentRequest request){
        String SOAPRequestXML = null;
        try{
            SOAPRequestXML = String.format("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:isom=\"http://schemas.datacontract.org/2004/07/ISOMessage.Models.Authentication\">\n" +
                            "   <soapenv:Header/>\n" +
                            "   <soapenv:Body>\n" +
                            "      <tem:InquiryRequest>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:Amount>%s</tem:Amount>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:ApplicationVersion>%s</tem:ApplicationVersion>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:CVM>%s</tem:CVM>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:CardNumber>%s</tem:CardNumber>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:CardType>%s</tem:CardType>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:CustomerAddress>%s</tem:CustomerAddress>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:CustomerCity>%s</tem:CustomerCity>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:CustomerContactNumber>%s</tem:CustomerContactNumber>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:CustomerCountry>%s</tem:CustomerCountry>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:CustomerEmail>%s</tem:CustomerEmail>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:DateOfTransactionForReceiptReprint>%s</tem:DateOfTransactionForReceiptReprint>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:DeviceSerialNumber>%s</tem:DeviceSerialNumber>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:ExpiryDate>%s</tem:ExpiryDate>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:FirstName>%s</tem:FirstName>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:GenerateReceiptOnly>%s</tem:GenerateReceiptOnly>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:HashPassword>%s</tem:HashPassword>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:HostDeviceName>%s</tem:HostDeviceName>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:ICC>%s</tem:ICC>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:IsReversalTransaction>%s</tem:IsReversalTransaction>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:IsSendReceiptEmail>%s</tem:IsSendReceiptEmail>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:KSN>%s</tem:KSN>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:Language>%s</tem:Language>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:LastName>%s</tem:LastName>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:Login>\n" +
                            "            <!--Optional:-->\n" +
                            "            <isom:LoginID>%s</isom:LoginID>\n" +
                            "            <!--Optional:-->\n" +
                            "            <isom:Password>%s</isom:Password>\n" +
                            "         </tem:Login>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:MerchantID>%s</tem:MerchantID>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:MerchantName>%s</tem:MerchantName>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:OriginalTransactionTypeForReceiptReprint>%s</tem:OriginalTransactionTypeForReceiptReprint>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:PINBlock>%s</tem:PINBlock>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:POSDeviceName>%s</tem:POSDeviceName>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:POSEntryMode>%s</tem:POSEntryMode>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:RequestId>%s</tem:RequestId>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:RequestingApplicationID>%s</tem:RequestingApplicationID>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:ReversalReason>%s</tem:ReversalReason>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:SecureHash>%s</tem:SecureHash>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:ServiceCode>%s</tem:ServiceCode>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:ServiceID>%s</tem:ServiceID>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:ServiceName>%s</tem:ServiceName>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:ServiceNameAR>%s</tem:ServiceNameAR>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:SignatureFields>%s</tem:SignatureFields>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:SourceApplication>%s</tem:SourceApplication>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:TerminalID>%s</tem:TerminalID>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:Timestamp>%s</tem:Timestamp>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:Track2Data>%s</tem:Track2Data>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:TransactionCurreny>%s</tem:TransactionCurreny>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:TransactionInitiationDate>%s</tem:TransactionInitiationDate>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:TransactionInitiationTime>%s</tem:TransactionInitiationTime>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:TransactionReceiptToBeVoidedORReversedOrReprinted>%s</tem:TransactionReceiptToBeVoidedORReversedOrReprinted>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:TransactionReferenceNumber>%s</tem:TransactionReferenceNumber>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:TransactionType>%s</tem:TransactionType>\n" +
                            "         <!--Optional:-->\n" +
                            "         <tem:UserName>%s</tem:UserName>\n" +
                            "      </tem:InquiryRequest>\n" +
                            "   </soapenv:Body>\n" +
                            "</soapenv:Envelope>",
                    (request.getAmount()!=null?request.getAmount():""),
                    (request.getApplicationVersion()!=null?request.getApplicationVersion():""),
                    (request.getcVM()!=null?request.getcVM():""),
                    (request.getCardNumber()!=null?request.getCardNumber():""),
                    (request.getCardType()!=null?request.getCardType():""),
                    (request.getCustomerAddress()!=null?request.getCustomerAddress():""),
                    (request.getCustomerCity()!=null?request.getCustomerCity():""),
                    (request.getCustomerContactNumber()!=null?request.getCustomerContactNumber():""),
                    (request.getCustomerCountry()!=null?request.getCustomerCountry():""),
                    (request.getCustomerEmail()!=null?request.getCustomerEmail():""),
                    (request.getDateOfTransactionForReceiptReprint()!=null?request.getDateOfTransactionForReceiptReprint():""),
                    (request.getDeviceSerialNumber()!=null?request.getDeviceSerialNumber():""),
                    (request.getExpiryDate()!=null?request.getExpiryDate():""),
                    (request.getFirstName()!=null?request.getFirstName():""),
                    (request.getGenerateReceiptOnly()!=null?request.getGenerateReceiptOnly():""),
                    (request.getHashPassword()!=null?request.getHashPassword():""),
                    (request.getHostDeviceName()!=null?request.getHostDeviceName():""),
                    (request.getiCC()!=null?request.getiCC():""),
                    (request.getIsReveralTransaction()!=null?request.getIsReveralTransaction():""),
                    (request.getIsSendReceiptEmail()!=null?request.getIsSendReceiptEmail():""),
                    (request.getkSN()!=null?request.getkSN():""),
                    (request.getLanguage()!=null?request.getLanguage():""),
                    (request.getLastName()!=null?request.getLastName():""),
                    (request.getLoginID()!=null?request.getLoginID():""),
                    (request.getPassword()!=null?request.getPassword():""),
                    (request.getMerchantID()!=null?request.getMerchantID():""),
                    (request.getMerchantName()!=null?request.getMerchantName():""),
                    (request.getOriginalTransactionTypeForReceiptReprint()!=null?request.getOriginalTransactionTypeForReceiptReprint():""),
                    (request.getpINBlock()!=null?request.getpINBlock():""),
                    (request.getpOSDeviceName()!=null?request.getpOSDeviceName():""),
                    (request.getpOSEntryMode()!=null?request.getpOSEntryMode():""),
                    (request.getRequestId()!=null?request.getRequestId():""),
                    (request.getRequestingApplicationID()!=null?request.getRequestingApplicationID():""),
                    (request.getReversalReason()!=null?request.getReversalReason():""),
                    (request.getSecureHash()!=null?request.getSecureHash():""),
                    (request.getServiceCode()!=null?request.getServiceCode():""),
                    (request.getServiceID()!=null?request.getServiceID():""),
                    (request.getServiceName()!=null?request.getServiceName():""),
                    (request.getServiceNameAR()!=null?request.getServiceNameAR():""),
                    (request.getSignatureFields()!=null?request.getSignatureFields():""),
                    (request.getSourceApplication()!=null?request.getSourceApplication():""),
                    (request.getTerminalID()!=null?request.getTerminalID():""),
                    (request.getTimestamp()!=null?request.getTimestamp():""),
                    (request.getTrack2Data()!=null?request.getTrack2Data():""),
                    (request.getTransactionCurreny()!=null?request.getTransactionCurreny():""),
                    (request.getTransactionInitiationDate()!=null?request.getTransactionInitiationDate():""),
                    (request.getTransactionInitiationTime()!=null?request.getTransactionInitiationTime():""),
                    (request.getTransactionReceiptToBeVoidedORReversedOrReprinted()!=null?request.getTransactionReceiptToBeVoidedORReversedOrReprinted():""),
                    (request.getTransactionReferenceNumber()!=null?request.getTransactionReferenceNumber():""),
                    (request.getTransactionType()!=null?request.getTransactionType():""),
                    (request.getUserName()!=null?request.getUserName():""));
        }catch (Exception ex){
            ex.printStackTrace();
            appendLog(ex.getLocalizedMessage());
        }

        return SOAPRequestXML;
    }


    public String getPaymentMethodConfig(PaymentConfiguration request){

        String SOAPRequestXML = null;
        try{
            SOAPRequestXML = String.format("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <tem:NipsMerchantDetailsRequest_PaymentConfiguration>\n" +
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
                    "         <!--Optional:-->\n" +
                    "         <tem:ServiceId>%s</tem:ServiceId>\n" +
                    "      </tem:NipsMerchantDetailsRequest_PaymentConfiguration>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>",request.getRequestId(),request.getTimeStamp(),request.getSecureHash(),request.getSignatureField(),
                    request.getDeviceSerialNumber(),request.getServiceId());
        }catch (Exception ex){
            ex.printStackTrace();
            appendLog(ex.getLocalizedMessage());
        }

        return SOAPRequestXML;
    }



}