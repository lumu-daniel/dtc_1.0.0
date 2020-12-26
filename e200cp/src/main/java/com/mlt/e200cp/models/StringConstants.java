package com.mlt.e200cp.models;

public class StringConstants {

    /////////////////////////////Service URLS///////////////////////////
    public static final String ISO_PAYMENT_URLRetro = "https://isomessageservice.networkips.com:724";
//    public static final String ISO_PAYMENT_URLRetro = "http://192.168.1.213:3345";
    public static final String ISO_PAYMENT_URLRoute = "IsoMessageService.svc";
    public static final String ISO_SOAP_FUNCTION = "http://tempuri.org/IIsoMessageService/Inquiry";
    public static final String PAYMENT_METHOD_FUNCTION = "http://tempuri.org/INIPSiCounterService/MerchantPaymentConfiguration";
    public static final String BITMAP_DIR = "/sdcard/Download/receipt_imgs/";
    public static final String PDF_PATH = "/sdcard/Download/receipt.pdf";
    public static final String CT_PROFILES_PATH = "/sdcard/Download/profiles/ct_profiles.txt";
    public static final String CTLS_PROFILES_PATH = "/sdcard/Download/profiles/ctls_profiles.txt";
    public static final String CPK_KEYS_PATH = "/sdcard/Download/profiles/cpk_keys.txt";

    ///////////////////Ticket//////////////////////////////////////
    public static final String QR_IMG_PATH = "/sdcard/Download/receipt_imgs/rct_imgs/qr_holder.png";
    public static final String RCPT_PDF_PATH = "/sdcard/Download/receipt_imgs/rct_imgs/rcp_holder.pdf";
    public static final String TICKET_PNG_PATH = "/sdcard/Download/receipt_imgs/ticket_holder.png";

    ///////////////////////////Configs//////////////////////////////
    public static final String TXN_CURRENCY = "784";
    public static final String SERVICE_CODE ="101";
    public static final String MERCHANT_NAME ="MLTTest";
    public static final String LOGIN_ID ="MLT";
    public static final String LOGIN_PW ="nips@2019";
    public static final String APPLICATION_VERSION ="1.0.1";
    public static final String MAGSTRIPE_ENTRY_MODE ="021";
    public static final String CONTACTLESS_ENTRY_MODE ="071";
    public static final String CONTACTLESS_MGSTRIPE ="911";
    public static final String MAGSTRIPE_TECHNICAL_FALLBACK ="801";
    public static final String CONTACT_ENTRY_MODE ="051";
    public static final String SIGNATURE_FIELDS_CARD_PAYMENT = "RequestId,TimeStamp,TerminalId";
    public static final String SIGNATURE_FIELDS_DEVICE_SERIAL_NUMBER = "RequestId,TimeStamp,DeviceSerialNumber";
    public static final String SIGNATURE_CVM_TYPE ="01";
    public static String NOL_MODE="NolMode";
    public static String INTEGRATED_MODE="Integrated";
    public static String STAND_ALONE_MODE="StandAlone";
    public static String TICKET_MODE ="TicketMode";

    public static String DEVICE_SN = "E2C1000590";
    public static String MERCHANT_ID_ADIB = "1003967";
    public static String TERMINAL_ID_ADIB ="80040245";
    public static String DUMY_APPID = "ApplicationID";
    public static String POS_TYPE = "SmartScreen";
    public static String SECURE_HASH_FIELDS = "ServiceCode,TerminalID,TimeStamp";
    public static String PIN_CVM_TYPE = "02";
    public static String NO_CVM_TYPE = "00";
    public static String NO_CVM_PRF = "05";
    public static final String SIGNATURE_FIELDS_ISO_MESSAGE ="ServiceCode,TerminalID,TimeStamp";
    
    ////////////////////EmvTransactionName///////////////////////

    public static String VOID_PURCHASE_TRANSACTION = "VOID PURCHASE";
    public static String REFUND_TRANSACTION = "REFUND";
    public static String VOID_REFUND_TRANSACTION = "VOID REFUND";
    public static String PURCHASE_TRANSACTION = "PURCHASE";
    public static String RE_PRINT_RCPT = "RE PRINT RECIEPT";
    
    ////////////////////EmvErrors////////////////////////////////
    public static String CANCEL_TXN="Operation Cancelled...";
    public static String NULL = "";
    public static String CARD_ERR = "Device error, please retry.";
    public static String TXN_FLOW_ERR = "Transaction Flow error...";
    public static String MSR_ERR = "Error in magnetic stripe reading";
    public static String NOT_VER_ERR = "Error Transaction not verified...";
    public static String CRD_UNSPTD_ERR = "Card not supported or Operation cancelled...";
    public static String TIME_OUT_ERR = "Timed out.";
    public static String USE_CHIP_ERR = "Card not supported, please use chip.";
    public static String CARD_REMOVED_ERR = "Card removed.";
    public static String EMPTY_PIN_ERR = "Pin error.";
    public static String CRD_ERR = "Card error please try again.";
    public static String PIN_TIME_OUT_ERR = "Pin timed out.";
    public static String PIN_ERR = "Pin error.";
    
    ////////////////////TxnStates///////////////////////////////
    
    public static String ENTER_CARD = "Enter card displayed";
    public static String CARD_INSERTED_INT = "Card Inserted";
    public static String PIN_ENTERED = "PIN Entered";
    public static String PROC_ONLINE = "Online Processing";
    public static String GPRS_CONN = "GPRS connection";
    public static String RESP_FRM_HST = "Response received from Host";
    public static String RMV_CRD_DSP = "Remove card displayed";
    public static String PRINT_RCT = "Printing of receipt";
    public static String PRINT_RCT_FLD = "Printing of receipt failed";
    public static String CRD_RMVD = "Card removed";
    public static String CRD_NO_RMVD = "Card not removed after 20 seconds";
    
    ////////////////////SerialCommandCenter////////////////////////
    //region request
    public static String EID_READ_DATA = "EMIRATESIDREADERDATA";
    public static String INIT_CASH_DEVICE = "INITIALIZECASHDEVICE";
    public static String ACCEPT_CASH = "ACCEPTCASH";
    public static String GET_CASH_DEVICE_STATUS = "GETCASHDEVICESTATUS";
    public static String PRINT_STICKER = "STICKERPRINTERJOB";
    public static String GET_STICKER_PRINTER_STATUS = "GETSTICKERPRINTERSTATUS";
    public static String PRINT_CARD = "CARDPRINTERJOB";
    public static String GET_CARD_PRINTER_STATUS = "GETCARDPRINTERSTATUS";
    public static String CANCEL_ACCEPTANCE = "CANCELCASHACCEPTANCE";
    public static String GET_ALL_DEV_STATUS = "GETALLDEVICESTATUS";
    public static String UPDATE_TRANS_PARAMS = "TRANSACTIONUPDATEPARAMETERSSTRING";
    public static String EID_GET_STATUS = "GETEMIRATESIDREADERSTATUS";
    public static String PC_SHUTDOWN = "SHUTDOWN";
    public static String PC_RESTART = "RESTART";
    public static String GET_NFC_DEV_STATUS = "GETNFCDEVICESTATUS";
    public static String GET_FSE_CARD_DETAILS = "GETFSECARDDETAILS";
    public static String CANCEL_NFC_READER = "CANCELNFCREADING";
    public static String RESET_CASH = "CITRESET-CASH-COINS";
    public static String RESET_CARDS = "CITRESET-CARDS-VL";
    public static String GET_SUMMARY = "CITGETSUMMARY";
    public static String CIT_UPDATE_VLCARDS = "CITUPDATE-CARDS-VL";
    public static String CIT_UPDATE_CASH = "CITUPDATE-CASH";
    public static String PRINTRECEIPT = "PRINTRECIEPT";
    public static String UPDATE_SESSION = "UPDATESESSIONVARIABLES";
    public static String RESPONSE_ = "UPDATESESSIONVARIABLES";

    //endRegion


    //region response
    public static String RESPONSE_CASH_DEV_INIT = "cashdeviceinit";
    public static String RESPONSE_CASH_DEV_STATUS = "cashdevicestatus";
    public static String RESPONSE_CASH_DEV = "cashdevice";
    public static String RESPONSE_CANCELLED_ACCEPTANCE = "cancelledacceptance";
    public static String RESPONSE_STICKER_STATUS = "stickerprinterstatus";
    public static String RESPONSE_CANCEL_ACCEPTANCE_FAILED = "not cancelled acceptance";
    public static String RESPONSE_EVOLIS_READY = "evolisprinterstatus";
    public static String RESPONSE_EID_STATUS = "emiratesstatus";
    public static String RESPONSE_ACHIEVED = "billcountachieved";
    public static String RESPONSE_EID_DATA = "emiratesdata";
    public static String RESPONSE_EID_CANCEL = "emiratesidreadercancelreading";
    public static String RESPONSE_CASH_ACCEPTED = "billaccepted";
    public static String RESPONSE_CASH_DISPENSED = "billdispensed";
    public static String RESPONSE_EID_INSERTED = "emiratesid-inserted";
    public static String RESPONSE_EVOLIS_PRINT_JOB = "evolisprintjob";
    public static String RESPONSE_ACCEPTOR_LIGHT = "cashacceptorlight";
    public static String RESPONSE_ALL_DEV_STATUS = "getalldevicestatus";
    public static String RESPONSE_TRANS_LOG = "transactionlog";
    public static String RESPONSE_STICKER_PRINT_JOB = "stickerprintjob";
    public static String RESPONSE_ACK = "ack";
    public static String RESPONSE_NFC_DEV_STATUS = "getnfcdevicestatus";
    public static String RESPONSE_FSE_CARD_DETAILS = "getfsecarddetails";
    public static String RESPONSE_CANCELLED_NFC = "cancellednfc";
    public static String RESPONSE_CITRESETCASH = "citresetcash";
    public static String RESPONSE_CITRESETCARDS = "citresetcards";
    public static String RESPONSE_CITGETSUMMARY = "citgetsummary";
    public static String RESPONSE_CITUPDATE = "citupdate";
    public static String RESPONSE_UPDATESESSIONVARIABLES = "updatesessionvariables";
    public static String RESPONSE_START_TXN = "starttransaction";
    public static String RESPONSE_GET_POS_STATUS = "getposstatus";
    public static String RESPONSE_GET_TERM_DET = "getterminaldetails";

    public static String STATUS_NOT_READY = "notready";
    public static String STATUS_READY = "ready";
    public static String STATUS_TRUE = "true";

    //////////////////////////MSGFlags//////////////////////
    public static final String PORT_OPEN = "PORT_OPEN";
    public static final String CARD_INSERTED = "CARD_INSERTED";
    public static final String CARD_TAPPED = "CARD_TAPPED";
    public static final String CARD_SWIPPED = "CARD_SWIPPED";
    public static final String PROMPT_PHONE = "PROMPT_PHONE";
    public static final String CHIP_FALLBACK = "CHIP_FALLBACK";
    public static final String SWIPE_FALLBACK = "SWIPE_FALLBACK";
    public static final String SELECT_APP = "SELECT_APP";
    public static final String START_PINENTRY = "START_PINENTRY";
    public static final String TXN_PROCESSING = "TXN_PROCESSING";
    public static final String PINENTRY_FAIL = "PINENTRY_FAIL";
    public static final String TXN_SUCCESSFUL = "TXN_SUCCESSFUL";
    public static final String TXN_ERROR = "TXN_ERROR";
    public static final String TXN_REVERSAL = "TXN_REVERSAL";
    public static final String TXN_REVERSED = "TXN_REVERSED";
    public static final String START_INT_TXN = "START_INT_TXN";
    public static final String STOP_INT_TXN = "STOP_INT_TXN";
    public static final String CANCEL_INT_TXN = "CANCEL_INT_TXN";
    public static final String FALLBACKCHIPORTAP = "FALLBACKCHIPORTAP";
    public static final String SENDSTATE = "SENDSTATE";
    public static final String RMV_CARD_DISP = "RMV_CARD_DISP";

}
