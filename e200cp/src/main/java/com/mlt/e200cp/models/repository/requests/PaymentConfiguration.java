package com.mlt.e200cp.models.repository.requests;

public class PaymentConfiguration {

    private String RequestId, TimeStamp,SecureHash,SignatureField,DeviceSerialNumber,ServiceId;

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

    public String getServiceId() {
        return ServiceId;
    }

    public void setServiceId(String serviceId) {
        ServiceId = serviceId;
    }
}