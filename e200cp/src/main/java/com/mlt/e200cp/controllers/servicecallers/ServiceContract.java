package com.mlt.e200cp.controllers.servicecallers;

import android.util.Log;

import com.mlt.e200cp.controllers.deviceconfigcontrollers.ConfigurationClass;
import com.mlt.e200cp.interfaces.GeneralServiceCallback;
import com.mlt.e200cp.interfaces.ServiceCallback;
import com.mlt.e200cp.models.EmvTransactionDetails;
import com.mlt.e200cp.models.EmvTransactionType;
import com.mlt.e200cp.models.PosDetails;
import com.mlt.e200cp.models.XMLRequestAndResponse;
import com.mlt.e200cp.models.repository.requests.ISOPaymentRequest;
import com.mlt.e200cp.models.repository.response.ISOPaymentResponse;
import com.mlt.e200cp.utilities.helper.util.EncryptDecrpt;

import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

import static android.content.ContentValues.TAG;
import static com.mlt.e200cp.controllers.deviceconfigcontrollers.ConfigurationClass.LOGIN_ID;
import static com.mlt.e200cp.models.StringConstants.APPLICATION_VERSION;
import static com.mlt.e200cp.models.StringConstants.LOGIN_PW;
import static com.mlt.e200cp.models.StringConstants.MERCHANT_NAME;
import static com.mlt.e200cp.models.StringConstants.SERVICE_CODE;
import static com.mlt.e200cp.models.StringConstants.TXN_CURRENCY;
import static com.mlt.e200cp.utilities.helper.util.ISOConstant.RemoveEscapeSequence;
import static com.mlt.e200cp.utilities.helper.util.ISOConstant.shortUUID;
import static com.mlt.e200cp.utilities.helper.util.ServiceUrls.ISO_PAYMENT_URLRetro;
import static com.mlt.e200cp.utilities.helper.util.ServiceUrls.ISO_PAYMENT_URLRoute;
import static com.mlt.e200cp.utilities.helper.util.ServiceUrls.ISO_SOAP_FUNCTION;
import static com.mlt.e200cp.utilities.helper.util.Utility.appendLog;
import static com.mlt.e200cp.utilities.helper.util.Utility.checkExpiry;
import static com.mlt.e200cp.utilities.helper.util.Utility.convertXMLtoJSON;
import static com.mlt.e200cp.utilities.helper.util.Utility.getTranasctionDateAndTime;
import static com.mlt.e200cp.utilities.helper.util.Utility.processDate;
import static com.mlt.e200cp.utilities.helper.util.Utility.setVariables;
import static com.mlt.e200cp.utilities.helper.util.Utility.txn_type;

public class ServiceContract {


    public static void callIsoService(PosDetails details, EmvTransactionDetails transactionDetails, ServiceCallback callback){

        String xmlReq = "";
        if(!transactionDetails.getIsReversal().equals("true")){
            xmlReq = new XMLRequestAndResponse().ISOPaymentRequest(setIsoServiceParameters(encryptParams(transactionDetails, ConfigurationClass.plainText,callback),details));
        }else{
            xmlReq = new XMLRequestAndResponse().ISOPaymentRequest(setIsoServiceParameters(transactionDetails,details));
        }

        GenericServiceCall.getInstance(ISO_PAYMENT_URLRetro,xmlReq,ISO_PAYMENT_URLRoute,ISO_SOAP_FUNCTION).callService(new GeneralServiceCallback() {
            @Override
            public String onResponseSuccess(String data) {
                if(data!=null){
                    try {
//                        if(ConfigurationClass.serviceFlag.equalsIgnoreCase("Integrated")){
//                            PresenterClasses.sendState(TxnState.RESP_FRM_HST.label);
//                        }

                        data = data.replace("i:nil=\"true\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\"","").replace("i:nil=\"true\"","");
                        HashMap<String, Object> hashMap = setVariables(convertXMLtoJSON(data).getJSONObject("s:Envelope").getJSONObject("s:Body").getJSONObject("InquiryResponse"),transactionDetails);
                        EmvTransactionDetails transactionDetails = (EmvTransactionDetails)hashMap.get("GetTransactionDetails");
                        ISOPaymentResponse response = (ISOPaymentResponse)hashMap.get("ISOPaymentResponse");
                        response = decryptCardValues(response);
                        if(response.getTransactionDetailsData().getResponseCode().equals("00")){
                            callback.onResponseSuccess(transactionDetails,response);
                        }else{
                            onResponseFailure("Transaction Failed.");
                        }

                        return null;
                    } catch (Exception e) {
                        onResponseFailure("Failed");
                        e.printStackTrace();
                        appendLog(e.getLocalizedMessage());
                    }
                }else{
                    Log.e(TAG, "onResponseSuccess: Server responds null" );
                    onResponseFailure("");
                }
                return null;
            }

            @Override
            public String onResponseFailure(String t) {
                Log.e(TAG, "onResponseFailure: "+t );
                callback.onResponseFailure("Transaction Failed");
                return null;
            }
        });

    }

    public static void callRefundService(PosDetails details, EmvTransactionDetails emvTransactionDetails, ServiceCallback callback){
        encryptParams(emvTransactionDetails,ConfigurationClass.plainText,callback);
        String xmlReq = new XMLRequestAndResponse().ISOPaymentRequest(setIsoServiceParameters(emvTransactionDetails,details));
        GenericServiceCall.getInstance(ISO_PAYMENT_URLRetro,xmlReq,"IsoMessageService.svc",ISO_SOAP_FUNCTION).callService(new GeneralServiceCallback() {
            @Override
            public String onResponseSuccess(String data) {
                if(data!=null){
                    try {
//                        if(ConfigurationClass.serviceFlag.equalsIgnoreCase("Integrated")){
//                            PresenterClasses.sendState(TxnState.RESP_FRM_HST.label);
//                        }
                        data = data.replace("i:nil=\"true\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\"","").replace("i:nil=\"true\"","");
                        HashMap<String, Object> hashMap = setVariables(convertXMLtoJSON(data).getJSONObject("s:Envelope").getJSONObject("s:Body").getJSONObject("InquiryResponse"), emvTransactionDetails);
                        EmvTransactionDetails emvTransactionDetails = (EmvTransactionDetails)hashMap.get("GetTransactionDetails");
                        ISOPaymentResponse response = (ISOPaymentResponse)hashMap.get("ISOPaymentResponse");
                        if(response.getTransactionDetailsData().getResponseCode().equals("00")){
                            callback.onResponseSuccess(emvTransactionDetails,response);
                        }else{
                            onResponseFailure(response.getErrorDescription());
                        }
                    } catch (Exception e) {
                        onResponseFailure("Failed");
                        e.printStackTrace();
                        appendLog(e.getLocalizedMessage());
                    }
                }else{
                    Log.e(TAG, "onResponseSuccess: Response is null." );
                    onResponseFailure("");
                }
                return null;
            }

            @Override
            public String onResponseFailure(String t) {
                callback.onResponseFailure("Transaction Failed");
                return null;
            }
        });
    }

    public static void callVoidService(PosDetails details, EmvTransactionDetails emvTransactionDetails, ServiceCallback callback){
        encryptParams(emvTransactionDetails,ConfigurationClass.plainText,callback);
        String xmlReq = new XMLRequestAndResponse().ISOPaymentRequest(setIsoServiceParameters(emvTransactionDetails,details));

        GenericServiceCall.getInstance(ISO_PAYMENT_URLRetro,xmlReq,"IsoMessageService.svc",ISO_SOAP_FUNCTION).callService(new GeneralServiceCallback() {
            @Override
            public String onResponseSuccess(String data) {
                if(data!=null){
                    try {
//                        if(ConfigurationClass.serviceFlag.equalsIgnoreCase("Integrated")){
//                            PresenterClasses.sendState(TxnState.RESP_FRM_HST.label);
//                        }
                        data = data.replace("i:nil=\"true\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\"","").replace("i:nil=\"true\"","");
                        HashMap<String, Object> hashMap = setVariables(convertXMLtoJSON(data).getJSONObject("s:Envelope").getJSONObject("s:Body").getJSONObject("InquiryResponse"), emvTransactionDetails);
                        EmvTransactionDetails emvTransactionDetails = (EmvTransactionDetails)hashMap.get("GetTransactionDetails");
                        ISOPaymentResponse response = (ISOPaymentResponse)hashMap.get("ISOPaymentResponse");
                        if(response.getTransactionDetailsData().getResponseCode().equals("00")){
                            callback.onResponseSuccess(emvTransactionDetails,response);
                        }else{
                            callback.onResponseFailure("Failed");
                        }


                    } catch (Exception e) {
                        onResponseFailure("Failed");
                        e.printStackTrace();
                        appendLog(e.getLocalizedMessage());
                    }
                }else {
                    Log.e(TAG, "onResponseSuccess: Null response." );
                    onResponseFailure("");
                }
                return null;
            }

            @Override
            public String onResponseFailure(String t) {
                callback.onResponseFailure("Transaction Failed");
                return null;
            }
        });

    }

    public static void callReceiptReprint(PosDetails details, EmvTransactionDetails emvTransactionDetails, ServiceCallback callback){
        encryptParams(emvTransactionDetails,ConfigurationClass.plainText,callback);
        String xmlReq = new XMLRequestAndResponse().ISOPaymentRequest(setIsoServiceParameters(emvTransactionDetails,details));

        GenericServiceCall.getInstance(ISO_PAYMENT_URLRetro,xmlReq,"IsoMessageService.svc",ISO_SOAP_FUNCTION).callService(new GeneralServiceCallback() {
            @Override
            public String onResponseSuccess(String data) {
                if(data!=null){
                    try {
//                        if(ConfigurationClass.serviceFlag.equalsIgnoreCase("Integrated")){
//                            PresenterClasses.sendState(TxnState.RESP_FRM_HST.label);
//                        }
                        data = data.replace("i:nil=\"true\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\"","").replace("i:nil=\"true\"","");
                        HashMap<String, Object> hashMap = setVariables(convertXMLtoJSON(data).getJSONObject("s:Envelope").getJSONObject("s:Body").getJSONObject("InquiryResponse"), emvTransactionDetails);
                        EmvTransactionDetails emvTransactionDetails = (EmvTransactionDetails)hashMap.get("GetTransactionDetails");
                        ISOPaymentResponse response = (ISOPaymentResponse)hashMap.get("ISOPaymentResponse");

                        if(response.getTransactionDetailsData().getResponseCode().equalsIgnoreCase("00")){
                            callback.onResponseSuccess(emvTransactionDetails,response);
                        }
                        else{
                            onResponseFailure(response.getErrorDescription());
                        }
                    } catch (Exception e) {
                        onResponseFailure("Failed");
                        e.printStackTrace();
                        appendLog(e.getLocalizedMessage());
                    }
                }else {
                    Log.e(TAG, "onResponseSuccess: Null response." );
                    onResponseFailure("");
                }
                return null;
            }

            @Override
            public String onResponseFailure(String t) {
                callback.onResponseFailure("Transaction Failed");
                return null;
            }
        });

    }

    private static EmvTransactionDetails encryptParams(EmvTransactionDetails emvTransactionDetails, String plaintext, ServiceCallback callback){
        try {
            if(txn_type.equals(EmvTransactionType.REFUND_TRANSACTION)||txn_type.equals(EmvTransactionType.PURCHASE_TRANSACTION)){
                if(checkExpiry(emvTransactionDetails.getCardExpiryDate())){
                    callback.onResponseFailure("Card is expired.");
                    return null;
                }else{
                    emvTransactionDetails.setCardExpiryDate(reFormatExpiry(emvTransactionDetails.getCardExpiryDate()));
                }
            }

            if(txn_type.equals(EmvTransactionType.REFUND_TRANSACTION)){
                emvTransactionDetails.setEPB("Optional");
                emvTransactionDetails.setKSN("Optional");
            }

            switch (txn_type){
                case REFUND_TRANSACTION:
                case PURCHASE_TRANSACTION:
                    emvTransactionDetails.setCardType(RemoveEscapeSequence(EncryptDecrpt.Encrypt(emvTransactionDetails.getCardType(), ConfigurationClass.plainText)));
                    emvTransactionDetails.setCardNumber(RemoveEscapeSequence(EncryptDecrpt.Encrypt(emvTransactionDetails.getCardNumber(), plaintext)));
                    emvTransactionDetails.setCardExpiryDate(RemoveEscapeSequence(EncryptDecrpt.Encrypt(emvTransactionDetails.getCardExpiryDate(), plaintext)));
                    emvTransactionDetails.setCardName(RemoveEscapeSequence(EncryptDecrpt.Encrypt(emvTransactionDetails.getCardName()==null?"": emvTransactionDetails.getCardName(), plaintext)));
                    emvTransactionDetails.setEPB(RemoveEscapeSequence(EncryptDecrpt.Encrypt(emvTransactionDetails.getEPB(), plaintext)));
                    emvTransactionDetails.setKSN(RemoveEscapeSequence(EncryptDecrpt.Encrypt(emvTransactionDetails.getKSN(), plaintext)));
                    emvTransactionDetails.setTRACKDATA(RemoveEscapeSequence(EncryptDecrpt.Encrypt(emvTransactionDetails.getTRACKDATA(), plaintext)));
                    emvTransactionDetails.setCVMType(RemoveEscapeSequence(EncryptDecrpt.Encrypt(emvTransactionDetails.getCVMType()==null?"02": emvTransactionDetails.getCVMType(), plaintext)));
                    emvTransactionDetails.setICCDATA(RemoveEscapeSequence(EncryptDecrpt.Encrypt(emvTransactionDetails.getICCDATA()!=null? emvTransactionDetails.getICCDATA():"null", plaintext)));
                    break;
                case REPRINT_RECEIPT:

                    break;
                case VOID_PURCHASE_TRANSACTION:
                case VOID_REFUND_TRANSACTION:
                    emvTransactionDetails.setRcptNo(EncryptDecrpt.Encrypt(emvTransactionDetails.getRcptNo(), plaintext));
                    break;

            }
            emvTransactionDetails.setDEVICE_SERIAL_NUMBER(ConfigurationClass.DEVICE_SERIAL_NUMBER);
            emvTransactionDetails.setMerchantId(ConfigurationClass.MERCHANT_ID);//uat
            emvTransactionDetails.setTerminalID(ConfigurationClass.TERMINAL_ID);//uat
        } catch (InvalidKeySpecException e) {
            if(!txn_type.equals(EmvTransactionType.REPRINT_RECEIPT)){
                callback.onResponseFailure(e.getLocalizedMessage());
            }
            e.printStackTrace();
            appendLog(e.getLocalizedMessage());
        }

        return emvTransactionDetails;
    }

    private static ISOPaymentRequest setIsoServiceParameters(EmvTransactionDetails emvTransactionDetails, PosDetails posDetails) {
        ISOPaymentRequest request = new ISOPaymentRequest();

        try{

            String time = getTranasctionDateAndTime().replace("T"," ").replace("Z"," ");
            String getHashPassword = RemoveEscapeSequence(EncryptDecrpt.Encrypt(ConfigurationClass.SECRET_KEY, ConfigurationClass.plainText));
            /*==============MLT SERVICE=============================*/

            request.setRequestId(shortUUID());
            request.setCardNumber(emvTransactionDetails.getCardNumber());
            request.setDeviceSerialNumber(emvTransactionDetails.getDEVICE_SERIAL_NUMBER());
            request.setCardType(emvTransactionDetails.getCardType());
            request.setMerchantID(emvTransactionDetails.getMerchantId());
            request.setkSN(emvTransactionDetails.getKSN());
            request.setExpiryDate(emvTransactionDetails.getCardExpiryDate());

            request.setpINBlock(emvTransactionDetails.getEPB());
            request.setTimestamp(time);
            request.setFirstName(emvTransactionDetails.getCardName());//getTransactionDetail.getCustomerFirstName()
            request.setLastName("");//getTransactionDetail.getCustomerLastName()
            request.setSecureHash(EncryptDecrpt.GetSecureHashCardPaymentISO(SERVICE_CODE, emvTransactionDetails.getTerminalID(),time));
            request.setServiceCode(SERVICE_CODE);
            request.setMerchantName(MERCHANT_NAME);
            request.setiCC(emvTransactionDetails.getICCDATA());
            request.setTerminalID(emvTransactionDetails.getTerminalID());
            request.setAmount(emvTransactionDetails.getGrossAmount());
            request.setTrack2Data(emvTransactionDetails.getTRACKDATA());
            request.setLoginID(LOGIN_ID);
            request.setPassword(LOGIN_PW);
            request.setGenerateReceiptOnly("false");
            request.setTransactionInitiationDate(processDate(emvTransactionDetails.getTransactionInitiationDateAndTime(),"date").substring(0,4));
            request.setTransactionInitiationTime(processDate(emvTransactionDetails.getTransactionInitiationDateAndTime(),"time"));
            request.setcVM(emvTransactionDetails.getCVMType());
            request.setpOSEntryMode(emvTransactionDetails.getPOSENTRYTYPE());
            request.setApplicationVersion(APPLICATION_VERSION);
            request.setHashPassword(getHashPassword);
            if(emvTransactionDetails.getIsReversal().equals("true")){
                try {
                    request.setTransactionReceiptToBeVoidedORReversedOrReprinted(EncryptDecrpt.Encrypt(posDetails.getReceiptNumber(), ConfigurationClass.plainText));
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                    appendLog(e.getLocalizedMessage());
                }
            }/*else if(txn_type.equals(VOID_PURCHASE_TRANSACTION)||txn_type.equals(VOID_REFUND_TRANSACTION)){
            request.setTransactionReceiptToBeVoidedORReversed(posDetails.getReceiptNumber());
        }*/
            if(txn_type.equals(EmvTransactionType.REPRINT_RECEIPT)){
                request.setIsSendReceiptEmail("false");
                request.setDateOfTransactionForReceiptReprint(posDetails.getDateOfTransactionForReceiptReprint());
                request.setOriginalTransactionTypeForReceiptReprint(posDetails.getOriginalTransactionTypeForReceiptReprint());
                request.setTransactionReceiptToBeVoidedORReversedOrReprinted(EncryptDecrpt.Encrypt(posDetails.getReceiptNumber(), ConfigurationClass.plainText));
            }else if (txn_type.equals(EmvTransactionType.VOID_REFUND_TRANSACTION) || txn_type.equals(EmvTransactionType.VOID_PURCHASE_TRANSACTION) ){
                request.setOriginalTransactionTypeForReceiptReprint(posDetails.getOriginalTransactionTypeForReceiptReprint());
                request.setTransactionReceiptToBeVoidedORReversedOrReprinted(emvTransactionDetails.getRcptNo());
            }

            if(txn_type.equals(EmvTransactionType.LST_TXN_REPRINT_RECEIPT)){
                request.setIsSendReceiptEmail("false");
            }
            request.setCustomerAddress(posDetails.getCustomerAddress());
            request.setCustomerEmail(posDetails.getCustomerEmail());
            request.setCustomerCity(posDetails.getCustomerCity());
            request.setCustomerContactNumber(posDetails.getCustomerContactNumber());
            request.setCustomerCountry(posDetails.getCustomerCountry());
            request.setHostDeviceName(posDetails.getHostDeviceName());
            request.setLanguage(posDetails.getLanguage());
            request.setRequestingApplicationID(posDetails.getRequestingApplicationID());
            request.setReversalReason(posDetails.getReversalReason());
            request.setServiceID(posDetails.getServiceID());
            request.setServiceName(posDetails.getServiceName());
            request.setServiceNameAR(posDetails.getServiceNameAR());
            request.setSignatureFields(posDetails.getSignatureFields());
            request.setTransactionCurreny(posDetails.getTransactionCurreny());
            request.setSourceApplication(posDetails.getSourceApplication());
            request.setUserName(posDetails.getUserName());
            request.setTransactionReferenceNumber(posDetails.getTransactionReferenceNumber());
            request.setTransactionType(posDetails.getTransactionType());
            request.setpOSDeviceName(posDetails.getpOSDeviceName());
            request.setIsSendReceiptEmail("false");
            request.setIsReveralTransaction(emvTransactionDetails.getIsReversal());
            request.setTransactionCurreny(TXN_CURRENCY);
            /*==============MLT SERVICE=============================*/
        } catch (Exception ex) {
            ex.printStackTrace();
            appendLog(ex.getLocalizedMessage());
        }

        return request;
    }


    // 06-2026
    private static String reFormatExpiry(String expiry) {
        StringBuilder sb = new StringBuilder(expiry);
        sb.replace(2, 5, "");
        return sb.toString();

    }

    private static ISOPaymentResponse decryptCardValues(ISOPaymentResponse response) {
        if(!response.getTransactionDetailsData().getCardType().equals("")){
            response.getTransactionDetailsData().setCardType(EncryptDecrpt.Decrypt(response.getTransactionDetailsData().getCardType(),ConfigurationClass.plainText));
        }
        if(!response.getTransactionDetailsData().getApprovalCode().equals("")){
            response.getTransactionDetailsData().setApprovalCode(EncryptDecrpt.Decrypt(response.getTransactionDetailsData().getApprovalCode(),ConfigurationClass.plainText));
        }
        if(!response.getTransactionDetailsData().getCardExpiryDate().equals("")){
            response.getTransactionDetailsData().setCardExpiryDate(EncryptDecrpt.Decrypt(response.getTransactionDetailsData().getCardExpiryDate(),ConfigurationClass.plainText));
        }
        if(!response.getTransactionDetailsData().getCardNumber().equals("")){
            response.getTransactionDetailsData().setCardNumber(EncryptDecrpt.Decrypt(response.getTransactionDetailsData().getCardNumber(),ConfigurationClass.plainText));
        }
        if(!response.getTransactionDetailsData().getPaySlipNumber().equals("")){
            response.getTransactionDetailsData().setPaySlipNumber(EncryptDecrpt.Decrypt(response.getTransactionDetailsData().getPaySlipNumber(),ConfigurationClass.plainText));
        }
        if(!response.getTransactionDetailsData().getCustomerName().equals("")){
            response.getTransactionDetailsData().setCustomerName(EncryptDecrpt.Decrypt(response.getTransactionDetailsData().getCustomerName(),ConfigurationClass.plainText));
        }
        return response;
    }
}

