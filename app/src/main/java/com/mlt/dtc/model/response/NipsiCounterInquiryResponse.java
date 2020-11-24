package com.mlt.dtc.model.Response;

import java.io.Serializable;


/*
  Created by talal on 3/6/2018.
 */

/**
 * Inquiry Request Class Models
 */


public class NipsiCounterInquiryResponse implements Serializable {

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

    //Reason Code
    private String ReasonCode;

    //Message
    private String Message;

    //PlainText to encrypt the card values
    private String PlainText;

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

    public String getReasonCode() {
        return ReasonCode;
    }

    public void setReasonCode(String reasonCode) {
        ReasonCode = reasonCode;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getPlainText() {
        return PlainText;
    }

    public void setPlainText(String plainText) {
        PlainText = plainText;
    }
}
