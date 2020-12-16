package com.mlt.e200cp.utilities.helper.util;

public class ServiceUrls {

    //****************************************soap action********************************************************

    //public static final String TFR_INQUIRY_URL_SOAP_ACTION = "http://tempuri.org/IPPSServiceBridge/TFRInquiryForICounter";
    public static final String TFR_INQUIRY_URL_SOAP_ACTION = "http://tempuri.org/IPPSServiceBridge/TFRInquiryForICounter";

    //public static final String TFR_PAYMENT_URL_SOAP_ACTION = "http://tempuri.org/IPPSServiceBridge/MLTPaymentGateway";
    public static final String TFR_PAYMENT_URL_SOAP_ACTION = "http://tempuri.org/IPPSServiceBridge/MLTPaymentGateway";


    //public static final String ADJD_INQUIRY_URL_SOAP_ACTION_CARD_PAYMENT = "http://tempuri.org/INIPSiCounterService/Inquiry"; //UAT
      public static final String ADJD_INQUIRY_URL_SOAP_ACTION_CARD_PAYMENT = "http://tempuri.org/INIPSiCounterService/Inquiry"; //PROD

    //public static final String ADJD_INQUIRY_URL_SOAP_ACTION_CARD_PAYMENT_MERCHANT = "http://tempuri.org/INIPSiCounterService/MerchantInquiry_MultipleMerchants"; //UAT
      public static final String ADJD_INQUIRY_URL_SOAP_ACTION_CARD_PAYMENT_MERCHANT = "http://tempuri.org/INIPSiCounterService/MerchantInquiry_MultipleMerchants"; //PROD


    //public static final String MLT_PORTAL_UPDATE_SOAPACTION = "http://tempuri.org/IService/MLTPaymentPortalUpdate";
    public static final String MLT_PORTAL_UPDATE_SOAPACTION = "http://tempuri.org/IService/MLTPaymentPortalUpdate";


    public static final String GetJudgementDecisionDocument = "http://tempuri.org/IPPSServiceBridge/GetJudgementDecisionDocument";//development

    public static final String Generate_MLT_Receipt_SOAP_ACTION = "http://tempuri.org/IPPSServiceBridge/GenerateMLTReceipt"; //development


    public static final String DIGITAL_PASS_INQUIRY_URL_SOAP_ACTION = "http://tempuri.org/ITheBookPROAPI/Inquiry";//development


    //SOAP Action
    public static final String URLSOAPAction = "http://tempuri.org/IDTCPushService/Inquiry";

//****************************************soap action********************************************************



    //****************************************Service URL********************************************************
    //URL for Inquiry and MLTPortal



    public static final String MLT_PORTAL_UPDATE_URL = "https://adjdbridgeservice.networkips.com:813/Service.svc"; //uat nips
//    public static final String MLT_PORTAL_UPDATE_URL = "https://webtest.adjd.gov.ae:8443/Service.svc"; //uat adjd
//    public static final String MLT_PORTAL_UPDATE_URL = "https://www.adjd.gov.ae:8443/Service.svc"; //prod adjd





//       public static final String TFR_INQUIRY_URL = "https://webtest.adjd.gov.ae:8443/PPSServiceBridge/PPSServiceBridge.svc"; //uat adjd
//    public static final String TFR_INQUIRY_URL = "https://www.adjd.gov.ae:8443/MLTPPSBridge/PPSServiceBridge.svc"; //production adjd
    public static final String TFR_INQUIRY_URL = "https://webtest.adjd.gov.ae:8443/PPSServiceBridgeUATTesting/PPSServiceBridge.svc";//uat adjd accessible ->inquiry,receipt,stop search


    //url for nipsgateway

    public static final String ADJD_INQUIRY_URL_CARD_PAYMENT = "https://testpg.networkips.com/NIPSICounterService/NIPSICounterService.svc"; //UAT
    public static final String ADJD_INQUIRY_URL_CARD_PAYMENTRetro = "https://testpg.networkips.com/NIPSICounterService"; //UAT
//    public static final String ADJD_INQUIRY_URL_CARD_PAYMENT = "https://nipsgateway.networkips.com/NIPSICounterService/NIPSICounterService.svc"; //Prod



    //Common service
    public static final String URL = "https://dtc.networkips.com:6105/ServiceModule/DTCPushService.svc/";//uat
//    public static final String URL = "https://dtcpushservice.networkips.com:6104/ServiceModule/DTCPushService.svc/"; //production

    // dp services uat
    public static final String createdp = "https://testpg.networkips.com/DigitalPass/CreateDigitalPass"; //uat
    public static final String updatedp = "https://testpg.networkips.com/DigitalPass/UpdateDigitalPass"; //uat
    public static final String getdp = "https://testpg.networkips.com/DigitalPass/GetStatusbyRef"; //uat


    // dp services production
//    public static final String createdp = "https://nipsgateway.networkips.com/DigitalPass/CreateDigitalPass"; //production
//    public static final String updatedp = "https://nipsgateway.networkips.com/DigitalPass/UpdateDigitalPass"; //production
//    public static final String getdp = "https://nipsgateway.networkips.com/DigitalPass/GetStatusbyRef"; //production


    //for stop search request domain name

    public static String stopURLDomainName="https://webtest.adjd.gov.ae:8443/PPSServiceBridgeUATTesting/ADJDDocument.aspx?DocId="; //uat
//    public static String stopURLDomainName="https://www.adjd.gov.ae:8443/MLTPPSBridge/ADJDDocument.aspx?DocId="; //production

    // mlt receipt url
    public static final String MltReceipt = "https://testpg.networkips.com/PPSService/GenerateMLTReceipt"; //uat
//    public static final String MltReceipt = "https://nipsgateway.networkips.com/ADJD/GenerateMLTReceipt"; //production

    //****************************************Service URL********************************************************

    public static final String ADJD_RMS_URL_RETRO = "https://adjdrmsservices.networkips.com:814";//UAT
//    public static final String ADJD_RMS_URL_RETRO = "https://adjdrmsservices.networkips.com:8040";//Prod
//    public static final String ADJD_RMS_END_RETRO = "AdjdRMSService.svc";//UAT
    public static final String ADJD_RMS_END_RETRO = "AdjdRMSService.svc";//Prod
    public static final String ADJD_RMS_URL = "https://adjdrmsservices.networkips.com:814/AdjdRMSService.svc";//UAT
//    public static final String ADJD_RMS_URL = "https://adjdrmsservices.networkips.com:8040/AdjdRMSService.svc";//PROD

    public static final String ADJD_RMS_SOAPACTION_GET_Transaction = "http://tempuri.org/IAdjdRMSService/GetTransactionDetail";//UAT
//    public static final String ADJD_RMS_SOAPACTION_GET_Transaction = "http://tempuri.org/IAdjdRMSService/GetTransactionDetail";//PROD

    public static final String ADJD_RMS_SOAPACTION_InitiateTransactionRequest = "http://tempuri.org/IAdjdRMSService/InitiateTransaction";

    public static final String ADJD_RMS_GetDigitalPassDetails = "http://tempuri.org/IAdjdRMSService/GetDigitalPassDetails";

    public static final String ADJD_RMS_SOAPACTION_UpdatePaymentDetail = "http://tempuri.org/IAdjdRMSService/UpdatePaymentDetail";

    public static final String ADJD_RMS_SOAPACTION_ReceiptPrinted = "http://tempuri.org/IAdjdRMSService/ReceiptPrinted";

    public static final String ADJD_RMS_SOAPACTION_RedeemDigitalPass = "http://tempuri.org/IAdjdRMSService/RedeemDigitalPass";




    public static final String ISO_PAYMENT_URL = "https://isomessageservice.networkips.com:724/IsoMessageService.svc";
    public static final String ISO_PAYMENT_URLRetro = "https://isomessageservice.networkips.com:724";
//    public static final String ISO_PAYMENT_URLRetro = "http://192.168.1.213:3345";
    public static final String ISO_PAYMENT_URLRoute = "IsoMessageService.svc";
    public static final String ISO_SOAP_FUNCTION = "http://tempuri.org/IIsoMessageService/Inquiry";
    public static final String PAYMENT_METHOD_FUNCTION = "http://tempuri.org/INIPSiCounterService/MerchantPaymentConfiguration";

}

