package com.mlt.e200cp.controllers.servicecallers;

import android.util.Log;

import com.mlt.e200cp.controllers.backgroundcontrollers.SequencyHandler;
import com.mlt.e200cp.controllers.deviceconfigcontrollers.ConfigurationClass;
import com.mlt.e200cp.controllers.mainlogiccontrollers.HelperEMVClass;
import com.mlt.e200cp.controllers.presenters.PresenterClasses;
import com.mlt.e200cp.interfaces.PosSequenceInterface;
import com.mlt.e200cp.interfaces.ReprintRctCallBack;
import com.mlt.e200cp.interfaces.ResultsCallback;
import com.mlt.e200cp.models.GetTransactionDetails;
import com.mlt.e200cp.models.PosDetails;
import com.mlt.e200cp.models.XMLRequestAndResponse;
import com.mlt.e200cp.models.enums.Constants;
import com.mlt.e200cp.models.enums.EmvTransactionName;
import com.mlt.e200cp.models.enums.EmvTransactionType;
import com.mlt.e200cp.models.enums.TxnState;
import com.mlt.e200cp.models.requests.ISOPaymentRequest;
import com.mlt.e200cp.models.response.ISOPaymentResponse;
import com.mlt.e200cp.utilities.helper.util.EncryptDecrpt;
import com.mlt.e200cp.utilities.helper.util.ISOConstant;
import com.mlt.e200cp.utilities.helper.util.Utility;

import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

import static android.content.ContentValues.TAG;
import static com.mlt.e200cp.models.MessageFlags.TXN_ERROR;
import static com.mlt.e200cp.models.MessageFlags.TXN_REVERSED;
import static com.mlt.e200cp.models.MessageFlags.TXN_SUCCESSFUL;
import static com.mlt.e200cp.utilities.helper.util.ISOConstant.currentDateTime;
import static com.mlt.e200cp.utilities.helper.util.ISOConstant.printReversal;
import static com.mlt.e200cp.utilities.helper.util.Logger.LINE_OUT;
import static com.mlt.e200cp.utilities.helper.util.Logger.log;
import static com.mlt.e200cp.utilities.helper.util.ServiceUrls.ISO_PAYMENT_URLRetro;
import static com.mlt.e200cp.utilities.helper.util.ServiceUrls.ISO_SOAP_FUNCTION;
import static com.mlt.e200cp.utilities.helper.util.Utility.appendLog;
import static com.mlt.e200cp.utilities.helper.util.Utility.convertXMLtoJSON;
import static com.mlt.e200cp.utilities.helper.util.Utility.getTranasctionDateAndTime;
import static com.mlt.e200cp.utilities.helper.util.Utility.processDate;
import static com.mlt.e200cp.utilities.helper.util.Utility.setVariables;
import static com.mlt.e200cp.utilities.helper.util.Utility.txn_type;

public class ServiceContract {

    private PosSequenceInterface sequenceInterface;
    private GetTransactionDetails getTransactionDetails;
    private ISOPaymentResponse response;

    private ServiceContract(GetTransactionDetails getTransactionDetails1,PosSequenceInterface sequenceInterface1){
        this.sequenceInterface = sequenceInterface1;
        this.getTransactionDetails = getTransactionDetails1;
    }

    public ServiceContract(GetTransactionDetails getTransactionDetails1) {
        this.getTransactionDetails = getTransactionDetails1;
    }

    public void callIsoService(PosDetails details){

        String xmlReq = "";
        if(!ISOConstant.reversal){
            xmlReq = new XMLRequestAndResponse().ISOPaymentRequest(setIsoServiceParameters(encryptParams(getTransactionDetails,ConfigurationClass.plainText),details));
        }else{
            xmlReq = new XMLRequestAndResponse().ISOPaymentRequest(setIsoServiceParameters(getTransactionDetails,details));
        }

        GenericServiceCall.getInstance(ISO_PAYMENT_URLRetro,xmlReq,"IsoMessageService.svc",ISO_SOAP_FUNCTION).callService(new ResultsCallback() {
            @Override
            public String onResponseSuccess(String data) {
                if(data!=null){
                    try {
                        if(ConfigurationClass.serviceFlag.equalsIgnoreCase("Integrated")){
                            PresenterClasses.sendState(TxnState.RESP_FRM_HST.label);
                        }
                        log(currentDateTime(),LINE_OUT());
                        data = data.replace("i:nil=\"true\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\"","").replace("i:nil=\"true\"","");
                        HashMap<String, Object> hashMap = setVariables(convertXMLtoJSON(data).getJSONObject("s:Envelope").getJSONObject("s:Body").getJSONObject("InquiryResponse"),getTransactionDetails);
                        getTransactionDetails = (GetTransactionDetails)hashMap.get("GetTransactionDetails");
                        response = (ISOPaymentResponse)hashMap.get("ISOPaymentResponse");

                        String reponseCode = response.getTransactionDetailsData().getResponseCode();
                        Utility.recieptNumber = response.getTransactionDetailsData().getReceiptNumber();
                        HelperEMVClass.helperEMVClass.onResponseRecieved(response);
                        if(!response.getReceiptMerchantCopy().equalsIgnoreCase("")){
                            ISOConstant.calledService = true;
                        }
                        if(!response.getReversalCauseReceiptMerchantCopy().equalsIgnoreCase("")){
                            ISOConstant.calledService = true;
                        }
                        if(reponseCode.equalsIgnoreCase("00")){
                            ISOConstant.SUCCESSDIALOGFLAG = true;
                            if(ISOConstant.reversal){
                                printReversal = true;
                                SequencyHandler.getInstance(TXN_REVERSED,sequenceInterface).execute(response);
                                ISOConstant.reversal = false;
                            }else{
                                if(!getTransactionDetails.getPOSENTRYTYPE().equals(Constants.CONTACT_ENTRY_MODE.label)){
                                    SequencyHandler.getInstance(TXN_SUCCESSFUL,sequenceInterface).execute(response);
                                }else{
                                    ISOConstant.SUCCESSFLAG = true;
                                }
                            }

                        } else if(reponseCode.equalsIgnoreCase("101")||reponseCode.equalsIgnoreCase("104")||reponseCode.equalsIgnoreCase("91")){//before reaching adib
                            SequencyHandler.getInstance(TXN_ERROR,sequenceInterface).execute("Transaction Failed",response);
                        }
                        else{
                            onResponseFailure(response.getErrorDescription());
                        }
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
                if(ISOConstant.reversal){
                    t = "Transaction reversal error.";
                    ISOConstant.reversal = false;
                }
                SequencyHandler.getInstance(TXN_ERROR,sequenceInterface).execute("Transaction Failed.",response);
//                regoPrinter.initiatePrinters(appCompatActivity,sequenceInterface.onBothRecieptsSelected());
                return null;
            }
        });

    }

    public void callRefundService(PosDetails details){
        encryptParams(getTransactionDetails,ConfigurationClass.plainText);
        String xmlReq = new XMLRequestAndResponse().ISOPaymentRequest(setIsoServiceParameters(getTransactionDetails,details));
        GenericServiceCall.getInstance(ISO_PAYMENT_URLRetro,xmlReq,"IsoMessageService.svc",ISO_SOAP_FUNCTION).callService(new ResultsCallback() {
            @Override
            public String onResponseSuccess(String data) {
                if(data!=null){
                    try {
                        if(ConfigurationClass.serviceFlag.equalsIgnoreCase("Integrated")){
                            PresenterClasses.sendState(TxnState.RESP_FRM_HST.label);
                        }
                        log(currentDateTime(),LINE_OUT());
                        data = data.replace("i:nil=\"true\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\"","").replace("i:nil=\"true\"","");
                        HashMap<String, Object> hashMap = setVariables(convertXMLtoJSON(data).getJSONObject("s:Envelope").getJSONObject("s:Body").getJSONObject("InquiryResponse"),getTransactionDetails);
                        getTransactionDetails = (GetTransactionDetails)hashMap.get("GetTransactionDetails");
                        response = (ISOPaymentResponse)hashMap.get("ISOPaymentResponse");
                        String reponseCode = response.getTransactionDetailsData().getResponseCode();
                        HelperEMVClass.helperEMVClass.onResponseRecieved(response);
                        if(!response.getReceiptMerchantCopy().equalsIgnoreCase("")){
                            ISOConstant.calledService = true;
                        }else if(!response.getReversalCauseReceiptMerchantCopy().equalsIgnoreCase("")){
                            ISOConstant.calledService = true;
                        }
                        if(reponseCode.equalsIgnoreCase("00")){
                            ISOConstant.SUCCESSDIALOGFLAG = true;
                            if(!getTransactionDetails.getPOSENTRYTYPE().equals(Constants.CONTACT_ENTRY_MODE.label)){
                                SequencyHandler.getInstance(TXN_SUCCESSFUL,sequenceInterface).execute(response);
                            }else{
                                ISOConstant.SUCCESSFLAG = true;
                            }

                        } else if(reponseCode.equalsIgnoreCase("101")||reponseCode.equalsIgnoreCase("104")||reponseCode.equalsIgnoreCase("91")){//before reaching adib

                            SequencyHandler.getInstance(TXN_ERROR,sequenceInterface).execute("Transaction Failed",response);
                        }
                        else{
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
                SequencyHandler.getInstance(TXN_ERROR,sequenceInterface).execute("Transaction Failed.");
                return null;
            }
        });
    }

    public void callVoidService(PosDetails details){
        encryptParams(getTransactionDetails,ConfigurationClass.plainText);
        String xmlReq = new XMLRequestAndResponse().ISOPaymentRequest(setIsoServiceParameters(getTransactionDetails,details));

        GenericServiceCall.getInstance(ISO_PAYMENT_URLRetro,xmlReq,"IsoMessageService.svc",ISO_SOAP_FUNCTION).callService(new ResultsCallback() {
            @Override
            public String onResponseSuccess(String data) {
                if(data!=null){
                    try {
                        if(ConfigurationClass.serviceFlag.equalsIgnoreCase("Integrated")){
                            PresenterClasses.sendState(TxnState.RESP_FRM_HST.label);
                        }
                        log(currentDateTime(),LINE_OUT());
                        data = data.replace("i:nil=\"true\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\"","").replace("i:nil=\"true\"","");
                        HashMap<String, Object> hashMap = setVariables(convertXMLtoJSON(data).getJSONObject("s:Envelope").getJSONObject("s:Body").getJSONObject("InquiryResponse"),getTransactionDetails);
                        getTransactionDetails = (GetTransactionDetails)hashMap.get("GetTransactionDetails");
                        response = (ISOPaymentResponse)hashMap.get("ISOPaymentResponse");
                        String reponseCode = response.getTransactionDetailsData().getResponseCode();
                        if(!response.getReceiptMerchantCopy().equalsIgnoreCase("")){
                            ISOConstant.calledService = true;
                        }else if(!response.getReversalCauseReceiptMerchantCopy().equalsIgnoreCase("")){
                            ISOConstant.calledService = true;
                        }
                        if(reponseCode.equalsIgnoreCase("00")){
                            ISOConstant.SUCCESSDIALOGFLAG = true;
                            SequencyHandler.getInstance(TXN_SUCCESSFUL,sequenceInterface).execute(response);

                        } else if(reponseCode.equalsIgnoreCase("101")||reponseCode.equalsIgnoreCase("104")||reponseCode.equalsIgnoreCase("91")){//before reaching adib

                            SequencyHandler.getInstance(TXN_ERROR,sequenceInterface).execute("Transaction Failed",response);

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
                SequencyHandler.getInstance(TXN_ERROR,sequenceInterface).execute("Transaction Failed.",response);
                return null;
            }
        });

    }

    public void callReceiptReprint(PosDetails details, ReprintRctCallBack callback){
        encryptParams(getTransactionDetails,ConfigurationClass.plainText);
        String xmlReq = new XMLRequestAndResponse().ISOPaymentRequest(setIsoServiceParameters(getTransactionDetails,details));

        GenericServiceCall.getInstance(ISO_PAYMENT_URLRetro,xmlReq,"IsoMessageService.svc",ISO_SOAP_FUNCTION).callService(new ResultsCallback() {
            @Override
            public String onResponseSuccess(String data) {
                if(data!=null){
                    try {
                        if(ConfigurationClass.serviceFlag.equalsIgnoreCase("Integrated")){
                            PresenterClasses.sendState(TxnState.RESP_FRM_HST.label);
                        }
                        log(currentDateTime(),LINE_OUT());
                        data = data.replace("i:nil=\"true\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\"","").replace("i:nil=\"true\"","");
                        HashMap<String, Object> hashMap = setVariables(convertXMLtoJSON(data).getJSONObject("s:Envelope").getJSONObject("s:Body").getJSONObject("InquiryResponse"),getTransactionDetails);
                        getTransactionDetails = (GetTransactionDetails)hashMap.get("GetTransactionDetails");
                        response = (ISOPaymentResponse)hashMap.get("ISOPaymentResponse");
                        log(currentDateTime()+response.getResponseMessage(),LINE_OUT());
                        String reponseCode = response.getTransactionDetailsData().getResponseCode();
                        if(!response.getReceiptMerchantCopy().equalsIgnoreCase("")){
                            ISOConstant.calledService = true;
                        }else if(!response.getReversalCauseReceiptMerchantCopy().equalsIgnoreCase("")){
                            ISOConstant.calledService = true;
                        }
                        if(reponseCode.equalsIgnoreCase("00")){
                            callback.onResponseSuccess(response);
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
                callback.onResponseFailure(t);
                return null;
            }
        });

    }

    private GetTransactionDetails encryptParams(GetTransactionDetails getTransactionDetails, String plaintext){
        try {
            if(txn_type.equals(EmvTransactionType.REFUND_TRANSACTION)||txn_type.equals(EmvTransactionType.PURCHASE_TRANSACTION)){
                if(Utility.checkExpiry(getTransactionDetails.getCardExpiryDate())){
                    HelperEMVClass.helperEMVClass.endTransaction("Card is expired.");
                    return null;
                }else{
                    getTransactionDetails.setCardExpiryDate(Utility.reFormatExpiry(getTransactionDetails.getCardExpiryDate()));
                }
            }

            if(txn_type.equals(EmvTransactionType.REFUND_TRANSACTION)){
                getTransactionDetails.setEPB("Optional");
                getTransactionDetails.setKSN("Optional");
            }

            switch (txn_type){
                case REFUND_TRANSACTION:
                case PURCHASE_TRANSACTION:
                    getTransactionDetails.setCardType(ISOConstant.RemoveEscapeSequence(EncryptDecrpt.Encrypt(getTransactionDetails.getCardType(), ConfigurationClass.plainText)));
                    getTransactionDetails.setCardNumber(ISOConstant.RemoveEscapeSequence(EncryptDecrpt.Encrypt(getTransactionDetails.getCardNumber(), plaintext)));
                    getTransactionDetails.setCardExpiryDate(ISOConstant.RemoveEscapeSequence(EncryptDecrpt.Encrypt(getTransactionDetails.getCardExpiryDate(), plaintext)));
                    getTransactionDetails.setCardName(ISOConstant.RemoveEscapeSequence(EncryptDecrpt.Encrypt(getTransactionDetails.getCardName()==null?"":getTransactionDetails.getCardName(), plaintext)));
                    getTransactionDetails.setEPB(ISOConstant.RemoveEscapeSequence(EncryptDecrpt.Encrypt(getTransactionDetails.getEPB(), plaintext)));
                    getTransactionDetails.setKSN(ISOConstant.RemoveEscapeSequence(EncryptDecrpt.Encrypt(getTransactionDetails.getKSN(), plaintext)));
                    getTransactionDetails.setTRACKDATA(ISOConstant.RemoveEscapeSequence(EncryptDecrpt.Encrypt(getTransactionDetails.getTRACKDATA(), plaintext)));
                    getTransactionDetails.setCVMType(ISOConstant.RemoveEscapeSequence(EncryptDecrpt.Encrypt(getTransactionDetails.getCVMType()==null?"02":getTransactionDetails.getCVMType(), plaintext)));
                    getTransactionDetails.setICCDATA(ISOConstant.RemoveEscapeSequence(EncryptDecrpt.Encrypt(getTransactionDetails.getICCDATA()!=null?getTransactionDetails.getICCDATA():"null", plaintext)));
                    break;
                case REPRINT_RECEIPT:

                    break;
                case VOID_PURCHASE_TRANSACTION:
                case VOID_REFUND_TRANSACTION:

                    break;

            }
            getTransactionDetails.setDEVICE_SERIAL_NUMBER(ConfigurationClass.DEVICE_SERIAL_NUMBER);
            getTransactionDetails.setMerchantId(ConfigurationClass.MERCHANT_ID);//uat
            getTransactionDetails.setTerminalID(ConfigurationClass.TERMINAL_ID);//uat
        } catch (InvalidKeySpecException e) {
            if(!txn_type.equals(EmvTransactionType.REPRINT_RECEIPT)){
                SequencyHandler.getInstance(TXN_ERROR,sequenceInterface).execute(e.getLocalizedMessage(),null);
            }
            e.printStackTrace();
            appendLog(e.getLocalizedMessage());
        }

        return getTransactionDetails;
    }

    public ISOPaymentRequest setIsoServiceParameters(GetTransactionDetails getTransactionDetails, PosDetails posDetails) {
        ISOPaymentRequest request = new ISOPaymentRequest();

        try{

            String time = getTranasctionDateAndTime().replace("T"," ").replace("Z"," ");
            String getHashPassword = ISOConstant.RemoveEscapeSequence(EncryptDecrpt.Encrypt(ConfigurationClass.SECRET_KEY, ConfigurationClass.plainText));
            /*==============MLT SERVICE=============================*/

            request.setRequestId(ISOConstant.shortUUID());
            request.setCardNumber(getTransactionDetails.getCardNumber());
            request.setDeviceSerialNumber(getTransactionDetails.getDEVICE_SERIAL_NUMBER());
            request.setCardType(getTransactionDetails.getCardType());
            request.setMerchantID(getTransactionDetails.getMerchantId());
            request.setkSN(getTransactionDetails.getKSN());
            request.setExpiryDate(getTransactionDetails.getCardExpiryDate());

            request.setpINBlock(getTransactionDetails.getEPB());
            request.setTimestamp(time);
            request.setFirstName(getTransactionDetails.getCardName());//getTransactionDetail.getCustomerFirstName()
            request.setLastName("");//getTransactionDetail.getCustomerLastName()
            request.setSecureHash(EncryptDecrpt.GetSecureHashCardPaymentISO(Constants.SERVICE_CODE.label,getTransactionDetails.getTerminalID(),time));
            request.setServiceCode(Constants.SERVICE_CODE.label);
            request.setMerchantName(Constants.MERCHANT_NAME.label);
            request.setiCC(getTransactionDetails.getICCDATA());
            request.setTerminalID(getTransactionDetails.getTerminalID());
            request.setAmount(getTransactionDetails.getGrossAmount());
            request.setTrack2Data(getTransactionDetails.getTRACKDATA());
            request.setLoginID(Constants.LOGIN_ID.label);
            request.setPassword(Constants.LOGIN_PW.label);
            request.setGenerateReceiptOnly("false");
            request.setTransactionInitiationDate(processDate(getTransactionDetails.getTransactionInitiationDateAndTime(),"date").substring(0,4));
            request.setTransactionInitiationTime(processDate(getTransactionDetails.getTransactionInitiationDateAndTime(),"time"));
            request.setcVM(getTransactionDetails.getCVMType());
            request.setpOSEntryMode(getTransactionDetails.getPOSENTRYTYPE());
            request.setApplicationVersion(Constants.APPLICATION_VERSION.label);
            request.setHashPassword(getHashPassword);
            if(ISOConstant.reversal){
                try {
                    request.setTransactionReceiptToBeVoidedORReversedOrReprinted(EncryptDecrpt.Encrypt(Utility.recieptNumber, ConfigurationClass.plainText));
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
            }

            if(txn_type.equals(EmvTransactionType.LST_TXN_REPRINT_RECEIPT)){
                request.setIsSendReceiptEmail("false");
            }
            request.setTransactionType(EmvTransactionName.PURCHASE_TRANSACTION.label);
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
            request.setIsReveralTransaction(getTransactionDetails.getIsReversal());
            request.setTransactionCurreny(Constants.TXN_CURRENCY.label);
            /*==============MLT SERVICE=============================*/
        }catch (Exception ex){
            ex.printStackTrace();
            appendLog(ex.getLocalizedMessage());
        }

        return request;
    }

}
