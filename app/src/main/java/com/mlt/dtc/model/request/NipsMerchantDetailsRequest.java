package com.mlt.dtc.model.request;

import java.io.Serializable;


/*
  Created by talal on 3/6/2018.
 */

/**
 * Inquiry Request Class Models
 */


public class NipsMerchantDetailsRequest implements Serializable {

    //Request Id
    private String RequestId;

    //Device Serial Number
    private String DeviceSerialNumber;

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

    public String getDeviceSerialNumber() {
        return DeviceSerialNumber;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber) {
        DeviceSerialNumber = deviceSerialNumber;
    }
}
