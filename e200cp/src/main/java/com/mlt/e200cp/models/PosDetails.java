package com.mlt.e200cp.models;

public class PosDetails {
    
    private String ServiceID;
    private String CustomerAddress;
    private String CustomerCity;
    private String Language;
    private String CustomerContactNumber;
    private String CustomerEmail;
    private String CustomerCountry;
    private String UserName;
    private String TransactionType;
    private String TransactionReferenceNumber;
    private String TransactionCurreny;
    private String SourceApplication;
    private String SignatureFields;
    private String ServiceName;
    private String ServiceNameAR;
    private String ReversalReason;
    private String RequestingApplicationID;
    private String pOSDeviceName;
    private String HostDeviceName;
    private String receiptNumber;
    private String OriginalTransactionTypeForReceiptReprint;
    private String dateOfTransactionForReceiptReprint;
    private String txnAmt;

    public String getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(String txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getOriginalTransactionTypeForReceiptReprint() {
        return OriginalTransactionTypeForReceiptReprint;
    }

    public void setOriginalTransactionTypeForReceiptReprint(String originalTransactionTypeForReceiptReprint) {
        OriginalTransactionTypeForReceiptReprint = originalTransactionTypeForReceiptReprint;
    }

    public String getDateOfTransactionForReceiptReprint() {
        return dateOfTransactionForReceiptReprint;
    }

    public void setDateOfTransactionForReceiptReprint(String dateOfTransactionForReceiptReprint) {
        this.dateOfTransactionForReceiptReprint = dateOfTransactionForReceiptReprint;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getServiceID() {
        return ServiceID;
    }

    public void setServiceID(String serviceID) {
        ServiceID = serviceID;
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

    public String getLanguage() {
        return Language;
    }

    public void setLanguage(String language) {
        Language = language;
    }

    public String getCustomerContactNumber() {
        return CustomerContactNumber;
    }

    public void setCustomerContactNumber(String customerContactNumber) {
        CustomerContactNumber = customerContactNumber;
    }

    public String getCustomerEmail() {
        return CustomerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        CustomerEmail = customerEmail;
    }

    public String getCustomerCountry() {
        return CustomerCountry;
    }

    public void setCustomerCountry(String customerCountry) {
        CustomerCountry = customerCountry;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getTransactionType() {
        return TransactionType;
    }

    public void setTransactionType(String transactionType) {
        TransactionType = transactionType;
    }

    public String getTransactionReferenceNumber() {
        return TransactionReferenceNumber;
    }

    public void setTransactionReferenceNumber(String transactionReferenceNumber) {
        TransactionReferenceNumber = transactionReferenceNumber;
    }

    public String getTransactionCurreny() {
        return TransactionCurreny;
    }

    public void setTransactionCurreny(String transactionCurreny) {
        TransactionCurreny = transactionCurreny;
    }

    public String getSourceApplication() {
        return SourceApplication;
    }

    public void setSourceApplication(String sourceApplication) {
        SourceApplication = sourceApplication;
    }

    public String getSignatureFields() {
        return SignatureFields;
    }

    public void setSignatureFields(String signatureFields) {
        SignatureFields = signatureFields;
    }

    public String getServiceName() {
        return ServiceName;
    }

    public void setServiceName(String serviceName) {
        ServiceName = serviceName;
    }

    public String getServiceNameAR() {
        return ServiceNameAR;
    }

    public void setServiceNameAR(String serviceNameAR) {
        ServiceNameAR = serviceNameAR;
    }

    public String getReversalReason() {
        return ReversalReason;
    }

    public void setReversalReason(String reversalReason) {
        ReversalReason = reversalReason;
    }

    public String getRequestingApplicationID() {
        return RequestingApplicationID;
    }

    public void setRequestingApplicationID(String requestingApplicationID) {
        RequestingApplicationID = requestingApplicationID;
    }

    public String getpOSDeviceName() {
        return pOSDeviceName;
    }

    public void setpOSDeviceName(String pOSDeviceName) {
        this.pOSDeviceName = pOSDeviceName;
    }

    public String getHostDeviceName() {
        return HostDeviceName;
    }

    public void setHostDeviceName(String hostDeviceName) {
        HostDeviceName = hostDeviceName;
    }
}
