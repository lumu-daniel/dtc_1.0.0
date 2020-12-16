package com.mlt.e200cp.controllers.servicecallers;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.mlt.e200cp.controllers.backgroundcontrollers.SequencyHandler;
import com.mlt.e200cp.controllers.deviceconfigcontrollers.ConfigurationClass;
import com.mlt.e200cp.controllers.mainlogiccontrollers.HelperEMVClass;
import com.mlt.e200cp.controllers.presenters.PresenterClasses;
import com.mlt.e200cp.interfaces.PosSequenceInterface;
import com.mlt.e200cp.interfaces.ResultsCallback;
import com.mlt.e200cp.models.GetTransactionDetails;
import com.mlt.e200cp.models.PosDetails;
import com.mlt.e200cp.models.XMLRequestAndResponse;
import com.mlt.e200cp.models.enums.Constants;
import com.mlt.e200cp.models.enums.EmvTransactionName;
import com.mlt.e200cp.models.enums.TxnState;
import com.mlt.e200cp.models.requests.ISOPaymentRequest;
import com.mlt.e200cp.models.response.ISOPaymentResponse;
import com.mlt.e200cp.utilities.helper.util.EncryptDecrpt;
import com.mlt.e200cp.utilities.helper.util.ISOConstant;

import org.json.JSONObject;

import java.security.spec.InvalidKeySpecException;

import static android.content.ContentValues.TAG;
import static com.mlt.e200cp.models.MessageFlags.TXN_ERROR;
import static com.mlt.e200cp.models.MessageFlags.TXN_SUCCESSFUL;
import static com.mlt.e200cp.models.enums.EmvTransactionType.REFUND_TRANSACTION;
import static com.mlt.e200cp.models.enums.EmvTransactionType.VOID_PURCHASE_TRANSACTION;
import static com.mlt.e200cp.models.enums.EmvTransactionType.VOID_REFUND_TRANSACTION;
import static com.mlt.e200cp.utilities.helper.util.ISOConstant.currentDateTime;
import static com.mlt.e200cp.utilities.helper.util.Logger.LINE_OUT;
import static com.mlt.e200cp.utilities.helper.util.Logger.log;
import static com.mlt.e200cp.utilities.helper.util.ServiceUrls.ISO_PAYMENT_URLRetro;
import static com.mlt.e200cp.utilities.helper.util.ServiceUrls.ISO_SOAP_FUNCTION;
import static com.mlt.e200cp.utilities.helper.util.Utility.appendLog;
import static com.mlt.e200cp.utilities.helper.util.Utility.convertXMLtoJSON;
import static com.mlt.e200cp.utilities.helper.util.Utility.getTranasctionDateAndTime;
import static com.mlt.e200cp.utilities.helper.util.Utility.processDate;
import static com.mlt.e200cp.utilities.helper.util.Utility.txn_type;

public class RefundAndVoidServices {

    private GetTransactionDetails getTransactionDetails;
    private PosDetails details;
    private PosSequenceInterface sequenceInterface;
    private static RefundAndVoidServices refundAndVoidServices;
    private String getHashPassword;
    private ISOPaymentResponse response;

    private RefundAndVoidServices(GetTransactionDetails getTransactionDetails1, Context context1, PosDetails posdetails, PosSequenceInterface posSequenceInterface) {
        this.getTransactionDetails = getTransactionDetails1;
        this.details = posdetails;
        this.sequenceInterface = posSequenceInterface;
        AppCompatActivity appCompatActivity = (AppCompatActivity) context1;
    }

    public static RefundAndVoidServices getInstance(GetTransactionDetails getTransactionDetails1, Context context1, PosDetails posdetails, PosSequenceInterface posSequenceInterface){
        if(refundAndVoidServices!=null){
            refundAndVoidServices = null;
        }
        refundAndVoidServices = new RefundAndVoidServices(getTransactionDetails1,context1,posdetails,posSequenceInterface);
        return refundAndVoidServices;
    }

    public void initiateParams(){
        if(!ConfigurationClass.plainText.equals("")){
            if(txn_type.equals(REFUND_TRANSACTION)){
                callRefundService();
            }else{
                callVoidService();
            }
        }
    }

    private void callRefundService(){
        try {
            getTransactionDetails.setEPB("Optional");
            getTransactionDetails.setKSN("Optional");
            getTransactionDetails.setCardType(ISOConstant.RemoveEscapeSequence(EncryptDecrpt.Encrypt(getTransactionDetails.getCardType(), ConfigurationClass.plainText)));
            getHashPassword = ISOConstant.RemoveEscapeSequence(EncryptDecrpt.Encrypt(ConfigurationClass.SECRET_KEY, ConfigurationClass.plainText));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            appendLog(e.getLocalizedMessage());
        }
        encryptValues(ConfigurationClass.plainText);
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
                        setVariables(convertXMLtoJSON(data).getJSONObject("s:Envelope").getJSONObject("s:Body").getJSONObject("InquiryResponse"));
                        String reponseCode = response.getTransactionDetailsData().getResponseCode();
                        decryptCardValues();
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
                response = null;
                return null;
            }

            @Override
            public String onResponseFailure(String t) {
                SequencyHandler.getInstance(TXN_ERROR,sequenceInterface).execute("Transaction Failed.",response);
                response = null;
                return null;
            }
        });
    }

    private void callVoidService(){
        try {
            getHashPassword = ISOConstant.RemoveEscapeSequence(EncryptDecrpt.Encrypt(ConfigurationClass.SECRET_KEY, ConfigurationClass.plainText));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            appendLog(e.getLocalizedMessage());
        }
        encryptValues(ConfigurationClass.plainText);
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
                        setVariables(convertXMLtoJSON(data).getJSONObject("s:Envelope").getJSONObject("s:Body").getJSONObject("InquiryResponse"));
                        String reponseCode = response.getTransactionDetailsData().getResponseCode();
                        decryptCardValues();
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
                    response = null;
                }else {
                    Log.e(TAG, "onResponseSuccess: Null response." );
                    onResponseFailure("");
                }
                return null;
            }

            @Override
            public String onResponseFailure(String t) {
                SequencyHandler.getInstance(TXN_ERROR,sequenceInterface).execute("Transaction Failed.",response);
                response = null;
                return null;
            }
        });

    }

    //Encrypt values using using the plain text key aquired at the installation of the app.
    private void encryptValues(String plaintext){
        try {
            if(txn_type.equals(REFUND_TRANSACTION)){
                if(checkExpiry(getTransactionDetails.getCardExpiryDate())){
                    HelperEMVClass.helperEMVClass.endTransaction("Card is expired.");
                    return;
                }else{
                    getTransactionDetails.setCardExpiryDate(reFormatExpiry(getTransactionDetails.getCardExpiryDate()));
                }
                // 06-2026
                String getEncryptedCardNumber = ISOConstant.RemoveEscapeSequence(EncryptDecrpt.Encrypt(getTransactionDetails.getCardNumber(), plaintext));
                String getEncryptedCardExpiry = ISOConstant.RemoveEscapeSequence(EncryptDecrpt.Encrypt(getTransactionDetails.getCardExpiryDate(), plaintext));
                String getEncryptedCardName = ISOConstant.RemoveEscapeSequence(EncryptDecrpt.Encrypt(getTransactionDetails.getCardName()==null?"":getTransactionDetails.getCardName(), plaintext));
                String getEncryptedPinBlock = ISOConstant.RemoveEscapeSequence(EncryptDecrpt.Encrypt(getTransactionDetails.getEPB(), plaintext));//uat
                String getEncryptedKSN = ISOConstant.RemoveEscapeSequence(EncryptDecrpt.Encrypt(getTransactionDetails.getKSN(), plaintext));//uat
                String getEncryptedTrackData = ISOConstant.RemoveEscapeSequence(EncryptDecrpt.Encrypt(getTransactionDetails.getTRACKDATA(), plaintext));
                String getEncryptedCVMType = ISOConstant.RemoveEscapeSequence(EncryptDecrpt.Encrypt(getTransactionDetails.getCVMType()==null?"02":getTransactionDetails.getCVMType(), plaintext));
                String getEncryptedICCData = ISOConstant.RemoveEscapeSequence(EncryptDecrpt.Encrypt(getTransactionDetails.getICCDATA()!=null?getTransactionDetails.getICCDATA():"null", plaintext));
                getTransactionDetails.setCardNumber(getEncryptedCardNumber);
                getTransactionDetails.setCardExpiryDate(getEncryptedCardExpiry);
                getTransactionDetails.setTRACKDATA(getEncryptedTrackData);
                getTransactionDetails.setICCDATA(getEncryptedICCData);
                getTransactionDetails.setCardName(getEncryptedCardName);
                getTransactionDetails.setCVMType(getEncryptedCVMType);
                getTransactionDetails.setEPB(getEncryptedPinBlock);
                getTransactionDetails.setKSN(getEncryptedKSN);
            }else{
               details.setReceiptNumber(EncryptDecrpt.Encrypt(details.getReceiptNumber(), plaintext));
            }
            getTransactionDetails.setDEVICE_SERIAL_NUMBER(ConfigurationClass.DEVICE_SERIAL_NUMBER);
            getTransactionDetails.setMerchantId(ConfigurationClass.MERCHANT_ID);//uat
            getTransactionDetails.setTerminalID(ConfigurationClass.TERMINAL_ID);//uat

        } catch (Exception e) {
            SequencyHandler.getInstance(TXN_ERROR,sequenceInterface).execute(e.getLocalizedMessage());
            e.printStackTrace();
            appendLog(e.getLocalizedMessage());
        }
    }

    // 06-2026
    private String reFormatExpiry(String expiry) {
        StringBuilder sb = new StringBuilder(expiry);
        sb.replace(2,5 ,"");
        return sb.toString();

    }

    private boolean checkExpiry(String expiry){
        String time = getTranasctionDateAndTime().replace("T"," ").replace("Z"," ");
        time = time.split("-")[0]+"-"+time.split("-")[0];
        if(Integer.parseInt(time.split("-")[0])>Integer.parseInt(expiry.split("-")[0])){
            return false;
        }else if(Integer.parseInt(time.split("-")[0])==Integer.parseInt(expiry.split("-")[0])){
            if(Integer.parseInt(time.split("-")[1])>Integer.parseInt(expiry.split("-")[1])){
                return false;
            }else {
                return true;
            }
        }else{
            return true;
        }
    }

    public ISOPaymentRequest setIsoServiceParameters(GetTransactionDetails getTransactionDetails, PosDetails posDetails) {

        String time = getTranasctionDateAndTime().replace("T"," ").replace("Z"," ");
        /*==============MLT SERVICE=============================*/
        ISOPaymentRequest request = new ISOPaymentRequest();
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
        request.setIsSendReceiptEmail("false");
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
        if(txn_type.equals(VOID_PURCHASE_TRANSACTION)||txn_type.equals(VOID_REFUND_TRANSACTION)){
            request.setTransactionReceiptToBeVoidedORReversedOrReprinted(posDetails.getReceiptNumber());
        }
        setTransactionType(posDetails);
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
        request.setIsReveralTransaction("false");
        request.setTransactionCurreny(Constants.TXN_CURRENCY.label);
        /*==============MLT SERVICE=============================*/

        return request;
    }

    private void setTransactionType(PosDetails details){
        switch (txn_type){
            case VOID_PURCHASE_TRANSACTION:
                details.setTransactionType(EmvTransactionName.VOID_PURCHASE_TRANSACTION.label);
                break;
            case VOID_REFUND_TRANSACTION:
                details.setTransactionType(EmvTransactionName.VOID_REFUND_TRANSACTION.label);
                break;
            case PURCHASE_TRANSACTION:
                details.setTransactionType(EmvTransactionName.PURCHASE_TRANSACTION.label);
                break;
            case REFUND_TRANSACTION:
                details.setTransactionType(EmvTransactionName.REFUND_TRANSACTION.label);
                break;
        }
    }

    //populate the data for printing and second generate.
    private void setVariables(JSONObject obj){
        response = new Gson().fromJson(obj.toString(),ISOPaymentResponse.class);
        Log.e("Response command",response.getErrorDescription());
        getTransactionDetails.setIssuerAuthorisationData(response.getIAD());
        getTransactionDetails.setApprovalCode(response.getTransactionDetailsData().getApprovalCode());
        getTransactionDetails.setCardType(response.getTransactionDetailsData().getCardType());
        String merchantReciept = response.getReceiptMerchantCopy();
        String customerReciept = response.getReceiptCustomerCopy();
        String IssuerScriptingData71 = response.getIssuerScriptingData71();
        String IssuerScriptingData72 = response.getIssuerScriptingData72();
        getTransactionDetails.setReceiptMerchantCopy(merchantReciept);
        getTransactionDetails.setReceiptCustomerCopy(customerReciept);
        getTransactionDetails.setIssuerScriptingData71(IssuerScriptingData71);
        getTransactionDetails.setIssuerScriptingData72(IssuerScriptingData72);
    }




    private void decryptCardValues() {
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
    }
}
