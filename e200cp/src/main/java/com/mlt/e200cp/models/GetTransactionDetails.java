package com.mlt.e200cp.models;

import android.widget.TextView;

public class GetTransactionDetails {

    private String Version;
    private String TerminalID;
    private String MerchantId;
    private String GrossAmount;
    private String CardNumber;
    private String DEVICE_SERIAL_NUMBER;
    private String CardType;
    private String KSN;
    private String CardExpiryDate;
    private String EPB;
    private String ICCDATA;
    private String TRACKDATA;
    private String POSENTRYTYPE;
    private String TransactionInitiationDateAndTime;
    private String ApplicationID;
    private String ApprovalCode;
    private String IssuerAuthorisationData;
    private String GenerateReceiptOnly;
    private String ReceiptCustomerCopy;
    private String ReceiptMerchantCopy;
    private String ReversalReceiptCustomerCopy;
    private String ReversalReceiptMerchantCopy;
    private String IssuerScriptingData71;
    private String IssuerScriptingData72;
    private String CardName;
    private String CVMType;
    private String IsReversal;
    private Boolean PINENTERED;
    private Boolean UNKNOWNCVM;
    private TextView pinText;
    private boolean isIntergrated;

    public String getReversalReceiptCustomerCopy() {
        return ReversalReceiptCustomerCopy;
    }

    public void setReversalReceiptCustomerCopy(String reversalReceiptCustomerCopy) {
        ReversalReceiptCustomerCopy = reversalReceiptCustomerCopy;
    }

    public String getReversalReceiptMerchantCopy() {
        return ReversalReceiptMerchantCopy;
    }

    public void setReversalReceiptMerchantCopy(String reversalReceiptMerchantCopy) {
        ReversalReceiptMerchantCopy = reversalReceiptMerchantCopy;
    }

    public boolean isIntergrated() {
        return isIntergrated;
    }

    public void setIntergrated(boolean intergrated) {
        isIntergrated = intergrated;
    }

    public TextView getPinText() {
        return pinText;
    }

    public void setPinText(TextView pinText) {
        this.pinText = pinText;
    }

    public String getIsReversal() {
        return IsReversal;
    }

    public void setIsReversal(String isReversal) {
        IsReversal = isReversal;
    }

    public String getReceiptCustomerCopy() {
        return ReceiptCustomerCopy;
    }

    public void setReceiptCustomerCopy(String receiptCustomerCopy) {
        ReceiptCustomerCopy = receiptCustomerCopy;
    }

    public String getReceiptMerchantCopy() {
        return ReceiptMerchantCopy;
    }

    public void setReceiptMerchantCopy(String receiptMerchantCopy) {
        ReceiptMerchantCopy = receiptMerchantCopy;
    }

    public Boolean getUNKNOWNCVM() {
        return UNKNOWNCVM;
    }

    public void setUNKNOWNCVM(Boolean UNKNOWNCVM) {
        this.UNKNOWNCVM = UNKNOWNCVM;
    }

    public Boolean getPINENTERED() {
        return PINENTERED;
    }

    public void setPINENTERED(Boolean PINENTERED) {
        this.PINENTERED = PINENTERED;
    }

    public String getCVMType() {
        return CVMType;
    }

    public void setCVMType(String CVMType) {
        this.CVMType = CVMType;
    }

    public String getCardName() {
        return CardName;
    }

    public void setCardName(String cardName) {
        CardName = cardName;
    }

    public String getIssuerScriptingData71() {
        return IssuerScriptingData71;
    }

    public void setIssuerScriptingData71(String issuerScriptingData71) {
        IssuerScriptingData71 = issuerScriptingData71;
    }

    public String getIssuerScriptingData72() {
        return IssuerScriptingData72;
    }

    public void setIssuerScriptingData72(String issuerScriptingData72) {
        IssuerScriptingData72 = issuerScriptingData72;
    }

    public String getGenerateReceiptOnly() {
        return GenerateReceiptOnly;
    }

    public void setGenerateReceiptOnly(String generateReceiptOnly) {
        GenerateReceiptOnly = generateReceiptOnly;
    }

    public String getIssuerAuthorisationData() {
        return IssuerAuthorisationData;
    }

    public void setIssuerAuthorisationData(String issuerAuthorisationData) {
        IssuerAuthorisationData = issuerAuthorisationData;
    }

    public String getApprovalCode() {
        return ApprovalCode;
    }

    public void setApprovalCode(String approvalCode) {
        ApprovalCode = approvalCode;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }

    public String getTerminalID() {
        return TerminalID;
    }

    public void setTerminalID(String terminalID) {
        TerminalID = terminalID;
    }

    public String getMerchantId() {
        return MerchantId;
    }

    public void setMerchantId(String merchantId) {
        MerchantId = merchantId;
    }

    public String getGrossAmount() {
        return GrossAmount;
    }

    public void setGrossAmount(String grossAmount) {
        GrossAmount = grossAmount;
    }

    public String getCardNumber() {
        return CardNumber;
    }

    public void setCardNumber(String cardNumber) {
        CardNumber = cardNumber;
    }

    public String getDEVICE_SERIAL_NUMBER() {
        return DEVICE_SERIAL_NUMBER;
    }

    public void setDEVICE_SERIAL_NUMBER(String DEVICE_SERIAL_NUMBER) {
        this.DEVICE_SERIAL_NUMBER = DEVICE_SERIAL_NUMBER;
    }

    public String getCardType() {
        return CardType;
    }

    public void setCardType(String cardType) {
        CardType = cardType;
    }

    public String getKSN() {
        return KSN;
    }

    public void setKSN(String KSN) {
        this.KSN = KSN;
    }

    public String getCardExpiryDate() {
        return CardExpiryDate;
    }

    public void setCardExpiryDate(String cardExpiryDate) {
        CardExpiryDate = cardExpiryDate;
    }

    public String getEPB() {
        return EPB;
    }

    public void setEPB(String EPB) {
        this.EPB = EPB;
    }

    public String getICCDATA() {
        return ICCDATA;
    }

    public void setICCDATA(String ICCDATA) {
        this.ICCDATA = ICCDATA;
    }

    public String getTRACKDATA() {
        return TRACKDATA;
    }

    public void setTRACKDATA(String TRACKDATA) {
        this.TRACKDATA = TRACKDATA;
    }

    public String getPOSENTRYTYPE() {
        return POSENTRYTYPE;
    }

    public void setPOSENTRYTYPE(String POSENTRYTYPE) {
        this.POSENTRYTYPE = POSENTRYTYPE;
    }

    public String getTransactionInitiationDateAndTime() {
        return TransactionInitiationDateAndTime;
    }

    public void setTransactionInitiationDateAndTime(String transactionInitiationDateAndTime) {
        TransactionInitiationDateAndTime = transactionInitiationDateAndTime;
    }

    public String getApplicationID() {
        return ApplicationID;
    }

    public void setApplicationID(String applicationID) {
        ApplicationID = applicationID;
    }
}
