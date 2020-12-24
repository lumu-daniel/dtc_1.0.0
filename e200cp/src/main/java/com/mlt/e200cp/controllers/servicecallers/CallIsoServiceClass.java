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
import com.mlt.e200cp.utilities.helper.util.Utility;

import org.json.JSONObject;

import java.security.spec.InvalidKeySpecException;

import static android.content.ContentValues.TAG;
import static com.mlt.e200cp.models.MessageFlags.TXN_ERROR;
import static com.mlt.e200cp.models.MessageFlags.TXN_REVERSED;
import static com.mlt.e200cp.models.MessageFlags.TXN_SUCCESSFUL;
import static com.mlt.e200cp.utilities.helper.util.ISOConstant.SUCCESSFLAG;
import static com.mlt.e200cp.utilities.helper.util.ISOConstant.calledService;
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

public class CallIsoServiceClass {

    private GetTransactionDetails getTransactionDetails;
    //    private CallRegoPrinter regoPrinter;
    private Boolean reversals = false;
    private PosDetails details;
    private PosSequenceInterface sequenceInterface;
    private static CallIsoServiceClass callIsoServiceClass = null;
    private ISOPaymentResponse response;
    private String getHashPassword;

    private CallIsoServiceClass(GetTransactionDetails getTransactionDetails1, Context context1, PosDetails posdetails, PosSequenceInterface posSequenceInterface) {
        this.getTransactionDetails = getTransactionDetails1;
        this.details = posdetails;
        this.sequenceInterface = posSequenceInterface;
        AppCompatActivity appCompatActivity = (AppCompatActivity) context1;
//        regoPrinter = CallRegoPrinter.getInstance(posSequenceInterface,getTransactionDetails1);
    }

    public static CallIsoServiceClass getInstance(GetTransactionDetails getTransactionDetails1, Context context1, PosDetails posdetails, PosSequenceInterface posSequenceInterface){
//        return callIsoServiceClass==null?new CallIsoServiceClass(getTransactionDetails1,context1,posdetails,posSequenceInterface):callIsoServiceClass;
        if(callIsoServiceClass!=null){
            callIsoServiceClass = null;
        }
        callIsoServiceClass = new CallIsoServiceClass(getTransactionDetails1,context1,posdetails,posSequenceInterface);
        return callIsoServiceClass;
    }

    public void initiateParams(){
        String plaintext = "";

        /*{"ReasonCode":"0000","PlainText":"mZwhjOli7degNW\/f3k1HkrzlE1FsTLdRXuimHJrptmo=","xmlns":"http:\/\/tempuri.org\/","Message":"Success","RequestId":"RequestId","SecureHash":"m1S\/ekjzyRHsBZJk+UetXqgrZ8yyecXmztnpXDzqt\/XHWK0PdElb\/UKLOEDk9YjIK1bBJZFMq7seuGXea4I6CR0e\/iAD6HPr","TerminalId":"61100009","TimeStamp":"29092019 00:09:13","SignatureField":"RequestId,TimeStamp,TerminalId"}*/
        plaintext = ConfigurationClass.plainText;
        if(!plaintext.equals("")){
            callService(plaintext, details);
        }
    }

    //populate the data for printing and second generate.
    String iad = "";
    private void setVariables(){
        Log.e("Response command",response.getErrorDescription());
        try{
            iad = response.getIAD().equalsIgnoreCase("")?"":EncryptDecrpt.Decrypt(response.getIAD(),ConfigurationClass.plainText);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        getTransactionDetails.setIssuerAuthorisationData(iad);
        getTransactionDetails.setApprovalCode(response.getTransactionDetailsData().getApprovalCode());
        getTransactionDetails.setCardType(response.getTransactionDetailsData().getCardType());
        String merchantReciept = response.getReceiptMerchantCopy();
        String customerReciept = response.getReceiptCustomerCopy();
        String IssuerScriptingData71 = response.getIssuerScriptingData71().equalsIgnoreCase("")?"":EncryptDecrpt.Decrypt(response.getIssuerScriptingData71(),ConfigurationClass.plainText);
        String IssuerScriptingData72 = response.getIssuerScriptingData72().equalsIgnoreCase("")?"":EncryptDecrpt.Decrypt(response.getIssuerScriptingData72(),ConfigurationClass.plainText);
        getTransactionDetails.setReceiptMerchantCopy(merchantReciept);
        getTransactionDetails.setReceiptCustomerCopy(customerReciept);
        getTransactionDetails.setIssuerScriptingData71(IssuerScriptingData71);
        getTransactionDetails.setIssuerScriptingData72(IssuerScriptingData72);
    }

    //Encrypt values using using the plain text key aquired at the installation of the app.
    private void encryptValues(String plaintext){
        try {
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
            getTransactionDetails.setEPB(getEncryptedPinBlock);
            getTransactionDetails.setKSN(getEncryptedKSN);
            String getEncryptedTrackData = ISOConstant.RemoveEscapeSequence(EncryptDecrpt.Encrypt(getTransactionDetails.getTRACKDATA(), plaintext));
            String getEncryptedCVMType = ISOConstant.RemoveEscapeSequence(EncryptDecrpt.Encrypt(getTransactionDetails.getCVMType()==null?"02":getTransactionDetails.getCVMType(), plaintext));
            String getEncryptedICCData = ISOConstant.RemoveEscapeSequence(EncryptDecrpt.Encrypt(getTransactionDetails.getICCDATA()!=null?getTransactionDetails.getICCDATA():"null", plaintext));
            getTransactionDetails.setCardNumber(getEncryptedCardNumber);
            getTransactionDetails.setCardExpiryDate(getEncryptedCardExpiry);
            getTransactionDetails.setTRACKDATA(getEncryptedTrackData);
            getTransactionDetails.setICCDATA(getEncryptedICCData);
            getTransactionDetails.setDEVICE_SERIAL_NUMBER(ConfigurationClass.DEVICE_SERIAL_NUMBER);
            getTransactionDetails.setMerchantId(ConfigurationClass.MERCHANT_ID);//uat
            getTransactionDetails.setTerminalID(ConfigurationClass.TERMINAL_ID);//uat
            getTransactionDetails.setCardName(getEncryptedCardName);
            getTransactionDetails.setCVMType(getEncryptedCVMType);
        } catch (InvalidKeySpecException e) {
            SequencyHandler.getInstance(TXN_ERROR,sequenceInterface).execute(e.getLocalizedMessage(),response);
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

    private void callService(String plaintext,PosDetails details){
        try {
            getTransactionDetails.setCardType(ISOConstant.RemoveEscapeSequence(EncryptDecrpt.Encrypt(getTransactionDetails.getCardType(), plaintext)));
            getHashPassword = ISOConstant.RemoveEscapeSequence(EncryptDecrpt.Encrypt(ConfigurationClass.SECRET_KEY, plaintext));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            appendLog(e.getLocalizedMessage());
        }
        if(!ISOConstant.reversal){
            encryptValues(plaintext);
        }/*else{
            ISOConstant.reversal = false;
        }*/
        String xmlReq = new XMLRequestAndResponse().ISOPaymentRequest(setIsoServiceParameters(getTransactionDetails,details));
        GenericServiceCall.getInstance(ISO_PAYMENT_URLRetro,xmlReq,"IsoMessageService.svc",ISO_SOAP_FUNCTION).callService(new ResultsCallback() {
            @Override
            public String onResponseSuccess(String data) {
                log(data,LINE_OUT());

                if(data!=null){
                    try {
                        if(ConfigurationClass.serviceFlag.equalsIgnoreCase("Integrated")){
                            PresenterClasses.sendState(TxnState.RESP_FRM_HST.label);
                        }
                        log(currentDateTime(),LINE_OUT());
                        data = data.replace("i:nil=\"true\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\"","").replace("i:nil=\"true\"","");
                        response = new Gson().fromJson(convertXMLtoJSON(data).getJSONObject("s:Envelope").getJSONObject("s:Body").getJSONObject("InquiryResponse").toString(),ISOPaymentResponse.class);
                        decryptCardValues();
                        setVariables();
                        String reponseCode = response.getTransactionDetailsData().getResponseCode();
                        Utility.recieptNumber = response.getTransactionDetailsData().getReceiptNumber();
                        if (!getTransactionDetails.getIsReversal().equals("true")){
                            HelperEMVClass.helperEMVClass.onResponseRecieved(response);
                        }
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

                        }
                        else{
                            HelperEMVClass.helperEMVClass.onResponseRecieved(response);
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
                calledService = true;
                if(ISOConstant.reversal){
                    t = "Transaction reversal error.";
                    ISOConstant.reversal = false;
                }
                sequenceInterface.onTrasactionError(t);
//                regoPrinter.initiatePrinters(appCompatActivity,sequenceInterface.onBothRecieptsSelected());
                return null;
            }
        });
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
        request.setIsSendReceiptEmail("false");
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

        return request;
    }

}
