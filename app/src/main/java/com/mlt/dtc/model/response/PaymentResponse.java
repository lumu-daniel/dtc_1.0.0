package com.mlt.dtc.model.Response;


import com.google.android.material.internal.ParcelableSparseArray;

import java.io.Serializable;

public class PaymentResponse extends ParcelableSparseArray implements Serializable {

    // Payment Channel
    public String PaymentChannel;

    // Bank Id
    public String BankId;

    // Service ID
    public String ServiceId;

    // Service Name
    public String ServiceName;

    // Version
    public String Version;

    // Verified
    public String verified;

    // Terminal ID
    public String TerminalID;

    // Request Id
    public String RequestId;

    // Channel Reference Number
    public String ChannelReferenceNumber;

    // Authorization Code
    public String AuthorizationCode;

    // Transaction Date
    public String TransactionDate;

    // Transaction Token
    public String TransactionToken;

    // Transaction Status
    public String TransactionStatus;

    // Reason Code
    public String ReasonCode;

    // Merchant Id
    public String MerchantId;

    // Message
    public String Message;

    // Time Stamp
    public String TimeStamp;

    // Amount
    public String Amount;

    // Currency
    public String Currency;

    // Customer Address
    public String CustomerAddress;

    // Customer City
    public String CustomerCity;

    // Customer Contact Number
    public String CustomerContactNumber;

    // Customer Country
    public String CustomerCountry;

    // Customer Email
    public String CustomerEmail;

    // Customer Ip Address
    public String CustomerIPAddress;

    // Customer Postal Code
    public String CustomerPostalCode;

    // Customer State
    public String CustomerState;

    // Device Finger Print
    public String DeviceFingerPrint;

    // First Name
    public String FirstName;

    // Language
    public String Language;

    // Last Name
    public String LastName;

    // Reference Number
    public String ReferenceNumber;

    // Password
    public String Password;

    // User ID
    public String UserId;

    // Ip Assigned
    public String IpAssigned;

    // Source Application
    public String SourceApplication;

    // Sub Merchant ID
    public String SubMerchantId;

    // Call Back Url
    public String CallBackUrl;

    // Secure Hash
    public String SecureHash;

    // Signature Fields
    public String SignatureFields;

    public String getPaymentChannel() {
        return PaymentChannel;
    }

    public void setPaymentChannel(String paymentChannel) {
        PaymentChannel = paymentChannel;
    }

    public String getBankId() {
        return BankId;
    }

    public void setBankId(String bankId) {
        BankId = bankId;
    }

    public String getServiceId() {
        return ServiceId;
    }

    public void setServiceId(String serviceId) {
        ServiceId = serviceId;
    }

    public String getServiceName() {
        return ServiceName;
    }

    public void setServiceName(String serviceName) {
        ServiceName = serviceName;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getTerminalID() {
        return TerminalID;
    }

    public void setTerminalID(String terminalID) {
        TerminalID = terminalID;
    }

    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String requestId) {
        RequestId = requestId;
    }

    public String getChannelReferenceNumber() {
        return ChannelReferenceNumber;
    }

    public void setChannelReferenceNumber(String channelReferenceNumber) {
        ChannelReferenceNumber = channelReferenceNumber;
    }

    public String getAuthorizationCode() {
        return AuthorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        AuthorizationCode = authorizationCode;
    }

    public String getTransactionDate() {
        return TransactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        TransactionDate = transactionDate;
    }

    public String getTransactionToken() {
        return TransactionToken;
    }

    public void setTransactionToken(String transactionToken) {
        TransactionToken = transactionToken;
    }

    public String getTransactionStatus() {
        return TransactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        TransactionStatus = transactionStatus;
    }

    public String getReasonCode() {
        return ReasonCode;
    }

    public void setReasonCode(String reasonCode) {
        ReasonCode = reasonCode;
    }

    public String getMerchantId() {
        return MerchantId;
    }

    public void setMerchantId(String merchantId) {
        MerchantId = merchantId;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getCustomerAddress() {
        return CustomerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        CustomerAddress = customerAddress;
    }

    public String getCustomerCity() {
        return CustomerCity;
    }

    public void setCustomerCity(String customerCity) {
        CustomerCity = customerCity;
    }

    public String getCustomerContactNumber() {
        return CustomerContactNumber;
    }

    public void setCustomerContactNumber(String customerContactNumber) {
        CustomerContactNumber = customerContactNumber;
    }

    public String getCustomerCountry() {
        return CustomerCountry;
    }

    public void setCustomerCountry(String customerCountry) {
        CustomerCountry = customerCountry;
    }

    public String getCustomerEmail() {
        return CustomerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        CustomerEmail = customerEmail;
    }

    public String getCustomerIPAddress() {
        return CustomerIPAddress;
    }

    public void setCustomerIPAddress(String customerIPAddress) {
        CustomerIPAddress = customerIPAddress;
    }

    public String getCustomerPostalCode() {
        return CustomerPostalCode;
    }

    public void setCustomerPostalCode(String customerPostalCode) {
        CustomerPostalCode = customerPostalCode;
    }

    public String getCustomerState() {
        return CustomerState;
    }

    public void setCustomerState(String customerState) {
        CustomerState = customerState;
    }

    public String getDeviceFingerPrint() {
        return DeviceFingerPrint;
    }

    public void setDeviceFingerPrint(String deviceFingerPrint) {
        DeviceFingerPrint = deviceFingerPrint;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLanguage() {
        return Language;
    }

    public void setLanguage(String language) {
        Language = language;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getReferenceNumber() {
        return ReferenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        ReferenceNumber = referenceNumber;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getIpAssigned() {
        return IpAssigned;
    }

    public void setIpAssigned(String ipAssigned) {
        IpAssigned = ipAssigned;
    }

    public String getSourceApplication() {
        return SourceApplication;
    }

    public void setSourceApplication(String sourceApplication) {
        SourceApplication = sourceApplication;
    }

    public String getSubMerchantId() {
        return SubMerchantId;
    }

    public void setSubMerchantId(String subMerchantId) {
        SubMerchantId = subMerchantId;
    }

    public String getCallBackUrl() {
        return CallBackUrl;
    }

    public void setCallBackUrl(String callBackUrl) {
        CallBackUrl = callBackUrl;
    }

    public String getSecureHash() {
        return SecureHash;
    }

    public void setSecureHash(String secureHash) {
        SecureHash = secureHash;
    }

    public String getSignatureFields() {
        return SignatureFields;
    }

    public void setSignatureFields(String signatureFields) {
        SignatureFields = signatureFields;
    }
}
