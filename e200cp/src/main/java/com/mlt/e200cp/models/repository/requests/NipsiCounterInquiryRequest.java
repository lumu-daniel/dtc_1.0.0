package com.mlt.e200cp.models.repository.requests;

import java.io.Serializable;


/**
 * Inquiry Request Class Models
 */


public class NipsiCounterInquiryRequest implements Serializable {

    //Request Id
    private String RequestId;

    //Terminal ID
    private String TerminalId;

    //Curent TimeStamp
    private String TimeStamp;

    //Secure hash to be created for every request
    private String SecureHash;

    //Signature Fields to create the hash
    private String SignatureField;


    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String requestId) {
        RequestId = requestId;
    }

    public String getTerminalId() {
        return TerminalId;
    }

    public void setTerminalId(String terminalId) {
        TerminalId = terminalId;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getSecureHash() {
        return SecureHash;
    }

    public void setSecureHash(String secureHash) {
        SecureHash = secureHash;
    }

    public String getSignatureField() {
        return SignatureField;
    }

    public void setSignatureField(String signatureField) {
        SignatureField = signatureField;
    }

}
