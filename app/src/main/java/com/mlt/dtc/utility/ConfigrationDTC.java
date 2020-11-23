package com.mlt.dtc.utility;


import com.mlt.dtc.activity.MainActivity;
import com.mlt.dtc.fragment.FarePaymentDone;

/**
 * Created by raheel on 3/26/2018.
 */

public class ConfigrationDTC {


    private FarePaymentDone farePaymentDone = new FarePaymentDone();
    private MainActivity mainActivity=new MainActivity();
    //To make id dynamic
    //private MerchantConfiguration merchantConfiguration = new MerchantConfiguration();


    /***************These are the values fetched from the merchant detials service**************/

    // Terminal Id
    public static String TERMINAL_ID; //will vary per device 61100009

    // Merchant ID
    public static String MERCHANT_ID; //dtc01<-(this is for UAT) //RTA0000001(for production)

    //DeviceSerialNumber
    public static String DEVICE_SERIAL_NUMBER;

    //Payment User ID
    public static String PAYMENT_USER_ID; //"DtcOcdUser";

    //Payment Password
    public static String PAYMENT_PASSWORD; //"PM8uVaYjwCIZYukXVVWRgg==";

    //Payment secret key
    public static String PAYMENT_SECRET_KEY; //"Nips@2018#!@#$"; //"nips@2016%" <- for 3dsecure payment;

    // Bank Id
    public static String BANK_ID; //EBI//DIB

    //******************** Payment Cyber Source Service ****************************************//

    // Payment Action
    public static String PAYMENT_ACTION = "";//"https://3dsecure.networkips.com/Gateway/PG";

    // Request Category
    public static String PAYMENT_REQUEST_CATEGORY = "";//"sale";

    //DTC Payment Request Category
    public static final String REQUEST_CATEGORY = "sale";

    // Source Application
    public static String PAYMENT_SOURCE_APPLICATION = "";//"61100007";

    // Finger Print
    public static String PAYMENT_DEVICE_FINGER_PRINT = "";//"PC999999";

    // IP Assigned
    public static final String PAYMENT_IP_ASSIGNED = "127.0.0.1";//"127.0.0.1";

    // Version
    public static final String PAYMENT_VERSION = "1.0";

    // Service Id
    public static final String PAYMENT_SERVICE_ID = "DTC";

    // Service Name
    public static final String PAYMENT_SERVICE_NAME = "DTCPayment";

    // Payment Currency
    public static String PAYMENT_CURRENCY = "AED";//"AED";

    // Callback URL
    public static String PAYMENT_CALL_BACK_URL = "";//"https://3dsecure.networkips.com/Home/TestMerchantResponse";

    // Payment Channel
    public static String PAYMENT_CHANNEL = "ksk";//"ksk";

    // Language
    public static String PAYMENT_LANGUAGE = "EN";//"EN";

    // Sub Merchant Id
    public static final String PAYMENT_SUB_MERCHANT_ID = "";

    // Signature Fields
    public static final String PAYMENT_SIGNATURE_FIELDS = "RequestId,TimeStamp,ReferenceNumber,MerchantId,Amount";

    // Signature Fields for card payment
    public static final String SIGNATURE_FIELDS_CARD_PAYMENT = "RequestId,TimeStamp,TerminalId";

    // Is ThreeD Secure
    public static String IsThreeDSecure = "";

    // Silent API URL
    public static String SilentAPIURL = "";

    public static String PlainText="";

    //Card Values
    public static final String CardFirstName = "CardFirstName";
    public static final String CardLastName = "CardLastName";
    public static final String CardNumber = "CardNumber";
    public static final String CardExpiry = "CardExpiry";
    public static final String Country = "AE";
    public static final String City = "Dubai";
    public static final String DashDTC = "-dtc";
    public static final String GMAILCOM = "@gmail.com";
    public static final String DOTSEP = ".";
    public static final String PostalCode = "123456";
    public static final String ContactNumber = "12345678910";
    public static final String CardCVV = "CardCVV";

    //Device Serial Number key
    public static final String DEVICE_SERIAL_NUMBER_MERCHANT_DETAILS_KEY = "DeviceSerialNumber";
    //BankID key
    public static final String BANK_ID_MERCHANT_DETAILS_KEY = "BankID";
    //Terminal Id key
    public static final String TERMINAL_ID_MERCHANT_DETAILS_KEY = "TerminalId";
    //Merchant Id key
    public static final String MERCHANT_ID_MERCHANT_DETAILS_KEY = "MerchantId";
    //payment user id key
    public static final String PAYMENT_USER_ID_MERCHANT_DETAILS_KEY = "PaymentUserId";
    //payment secretkey key
    public static final String PAYMENT_SECRETKEY_MERCHANT_DETAILS_KEY = "PaymentSecretKey";
    //payment password key
    public static final String PAYMENT_PASSWORD_MERCHANT_DETAILS_KEY = "PaymentPassword";
    //payment action
    public static final String PAYMENT_ACTION_MERCHANT_DETAILS_KEY = "PaymentAction";
    //Source Application
    public static final String SOURCE_APPLICATION_MERCHANT_DETAILS_KEY = "SourceApplication";
    //Call back URL
    public static final String CALLBACK_URL_MERCHANT_DETAILS_KEY = "CallbackURL";
    //Device Finger Print
    public static final String DEVICE_FINGER_PRINT_MERCHANT_DETAILS_KEY = "DeviceFingerPrint";
    //Currency
    public static final String CURRENCY_MERCHANT_DETAILS_KEY = "Currency";
    //Channel
    public static final String CHANNEL_MERCHANT_DETAILS_KEY = "Channel";
    //Language
    public static final String LANGUAGE_MERCHANT_DETAILS_KEY = "LanguageMerhant";
    //Request
    public static final String REQUEST_CATEGORY_MERCHANT_DETAILS_KEY = "RequestCategory";
    //Is3DSecure
    public static final String IS_THREED_SECURE_MERCHANT_DETAILS_KEY = "Is3DSecure";
    //SilentOrderAPIURL
    public static final String SILENT_ORDER_API_URL_MERCHANT_DETAILS_KEY = "SilentOrderAPIURL";


    //tripid
    public static final String TRIPID_KEY = "tripId";

    public static final String REQUEST_TYPE="requestedType";

    //paidamount
    public static final String PAID_AMOUNT_KEY = "paidAmount";

    //paymentstatus
    public static final String PAYMENT_STATUS_KEY = "paymentStatus";

    //paymenttime
    public static final String PAYMENT_TIME_KEY = "paymentTime";

    // Source Application
    public static String UPDATE_PAYMENT_SOURCE_APPLICATION = "DTCInteractiveScreen";

    // Signature Field
    public static final String UPDATE_PAYMENT_SIGNATURE_FIELDS ="RequestId,TimeStamp,MerchantId,SourceApplication,BankId";

    // Request Type
    public static String UPDATE_PAYMENT_REQUEST_TYPE_PAYMENT = "REQ_INQ";

    // Request Category update Payment
    public static final String REQUEST_CATEGORY_UPDATE__PAYMENT ="PushInquiry";

    // Service Id
    public static final String SERVICE_ID_UPDATE_PAYMENT = "1122";

    // Login Id
    public static String LOGIN_ID;

    // Password
    public static String PASSWORD;

    public static String UPDATE_PAYMENT_SECRET_KEY= "Nips@2018%";


    public static final String APK_VERSION="apkversion";

    public static final String ADV_VERSION="advversion";

    public static final String MAINVIDEO_VERSION="mainvideoversion";

    public static final String TOPBANNER_VERSION="topbannerversion";


}
