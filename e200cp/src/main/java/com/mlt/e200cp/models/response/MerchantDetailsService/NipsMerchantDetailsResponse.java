package com.mlt.e200cp.models.response.MerchantDetailsService;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NipsMerchantDetailsResponse {

    @SerializedName("DeviceSerialNumber")
    @Expose
    private String deviceSerialNumber;
    @SerializedName("ReasonCode")
    @Expose
    private String reasonCode;
    @SerializedName("xmlns")
    @Expose
    private String xmlns;
    @SerializedName("MerchantDetail")
    @Expose
    private MerchantDetail merchantDetail;
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("RequestId")
    @Expose
    private String requestId;
    @SerializedName("SecureHash")
    @Expose
    private String secureHash;
    @SerializedName("TimeStamp")
    @Expose
    private String timeStamp;
    @SerializedName("SignatureField")
    @Expose
    private String signatureField;

    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getXmlns() {
        return xmlns;
    }

    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }

    public MerchantDetail getMerchantDetail() {
        return merchantDetail;
    }

    public void setMerchantDetail(MerchantDetail merchantDetail) {
        this.merchantDetail = merchantDetail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getSecureHash() {
        return secureHash;
    }

    public void setSecureHash(String secureHash) {
        this.secureHash = secureHash;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSignatureField() {
        return signatureField;
    }

    public void setSignatureField(String signatureField) {
        this.signatureField = signatureField;
    }

}