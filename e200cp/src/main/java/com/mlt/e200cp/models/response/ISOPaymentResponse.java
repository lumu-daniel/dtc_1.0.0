package com.mlt.e200cp.models.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ISOPaymentResponse implements Serializable {

    @SerializedName("ErrorDescription")
    private String errorDescription;
    @SerializedName("IAD")
    private String iAD;
    @SerializedName("IssuerScriptingData71")
    private String issuerScriptingData71;
    @SerializedName("IssuerScriptingData72")
    private String issuerScriptingData72;
    @SerializedName("ReceiptCustomerCopy")
    private String receiptCustomerCopy;
    @SerializedName("ReceiptCustomerCopyHtml")
    private String receiptCustomerCopyHtml;
    @SerializedName("ReceiptMerchantCopy")
    private String receiptMerchantCopy;
    @SerializedName("ReceiptMerchantCopyHtml")
    private String receiptMerchantCopyHtml;
    @SerializedName("ResponseMessage")
    private String responseMessage;
    @SerializedName("ReversalCauseReceiptCustomerCopy")
    private String reversalCauseReceiptCustomerCopy;
    @SerializedName("ReversalCauseReceiptCustomerCopyHtml")
    private String reversalCauseReceiptCustomerCopyHtml;
    @SerializedName("ReversalCauseReceiptMerchantCopy")
    private String reversalCauseReceiptMerchantCopy;
    @SerializedName("ReversalCauseReceiptMerchantCopyHtml")
    private String reversalCauseReceiptMerchantCopyHtml;
    @SerializedName("TransactionDetailsData")
    private TransactionDetailsData transactionDetailsData;

    @SerializedName("ErrorDescription")
    public String getErrorDescription() {
        return errorDescription;
    }

    @SerializedName("ErrorDescription")
    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    @SerializedName("IAD")
    public String getIAD() {
        return iAD;
    }

    @SerializedName("IAD")
    public void setIAD(String iAD) {
        this.iAD = iAD;
    }

    @SerializedName("IssuerScriptingData71")
    public String getIssuerScriptingData71() {
        return issuerScriptingData71;
    }

    @SerializedName("IssuerScriptingData71")
    public void setIssuerScriptingData71(String issuerScriptingData71) {
        this.issuerScriptingData71 = issuerScriptingData71;
    }

    @SerializedName("IssuerScriptingData72")
    public String getIssuerScriptingData72() {
        return issuerScriptingData72;
    }

    @SerializedName("IssuerScriptingData72")
    public void setIssuerScriptingData72(String issuerScriptingData72) {
        this.issuerScriptingData72 = issuerScriptingData72;
    }

    @SerializedName("ReceiptCustomerCopy")
    public String getReceiptCustomerCopy() {
        return receiptCustomerCopy;
    }

    @SerializedName("ReceiptCustomerCopy")
    public void setReceiptCustomerCopy(String receiptCustomerCopy) {
        this.receiptCustomerCopy = receiptCustomerCopy;
    }

    @SerializedName("ReceiptCustomerCopyHtml")
    public String getReceiptCustomerCopyHtml() {
        return receiptCustomerCopyHtml;
    }

    @SerializedName("ReceiptCustomerCopyHtml")
    public void setReceiptCustomerCopyHtml(String receiptCustomerCopyHtml) {
        this.receiptCustomerCopyHtml = receiptCustomerCopyHtml;
    }

    @SerializedName("ReceiptMerchantCopy")
    public String getReceiptMerchantCopy() {
        return receiptMerchantCopy;
    }

    @SerializedName("ReceiptMerchantCopy")
    public void setReceiptMerchantCopy(String receiptMerchantCopy) {
        this.receiptMerchantCopy = receiptMerchantCopy;
    }

    @SerializedName("ReceiptMerchantCopyHtml")
    public String getReceiptMerchantCopyHtml() {
        return receiptMerchantCopyHtml;
    }

    @SerializedName("ReceiptMerchantCopyHtml")
    public void setReceiptMerchantCopyHtml(String receiptMerchantCopyHtml) {
        this.receiptMerchantCopyHtml = receiptMerchantCopyHtml;
    }

    @SerializedName("ResponseMessage")
    public String getResponseMessage() {
        return responseMessage;
    }

    @SerializedName("ResponseMessage")
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    @SerializedName("ReversalCauseReceiptCustomerCopy")
    public String getReversalCauseReceiptCustomerCopy() {
        return reversalCauseReceiptCustomerCopy;
    }

    @SerializedName("ReversalCauseReceiptCustomerCopy")
    public void setReversalCauseReceiptCustomerCopy(String reversalCauseReceiptCustomerCopy) {
        this.reversalCauseReceiptCustomerCopy = reversalCauseReceiptCustomerCopy;
    }

    @SerializedName("ReversalCauseReceiptCustomerCopyHtml")
    public String getReversalCauseReceiptCustomerCopyHtml() {
        return reversalCauseReceiptCustomerCopyHtml;
    }

    @SerializedName("ReversalCauseReceiptCustomerCopyHtml")
    public void setReversalCauseReceiptCustomerCopyHtml(String reversalCauseReceiptCustomerCopyHtml) {
        this.reversalCauseReceiptCustomerCopyHtml = reversalCauseReceiptCustomerCopyHtml;
    }

    @SerializedName("ReversalCauseReceiptMerchantCopy")
    public String getReversalCauseReceiptMerchantCopy() {
        return reversalCauseReceiptMerchantCopy;
    }

    @SerializedName("ReversalCauseReceiptMerchantCopy")
    public void setReversalCauseReceiptMerchantCopy(String reversalCauseReceiptMerchantCopy) {
        this.reversalCauseReceiptMerchantCopy = reversalCauseReceiptMerchantCopy;
    }

    @SerializedName("ReversalCauseReceiptMerchantCopyHtml")
    public String getReversalCauseReceiptMerchantCopyHtml() {
        return reversalCauseReceiptMerchantCopyHtml;
    }

    @SerializedName("ReversalCauseReceiptMerchantCopyHtml")
    public void setReversalCauseReceiptMerchantCopyHtml(String reversalCauseReceiptMerchantCopyHtml) {
        this.reversalCauseReceiptMerchantCopyHtml = reversalCauseReceiptMerchantCopyHtml;
    }

    @SerializedName("TransactionDetailsData")
    public TransactionDetailsData getTransactionDetailsData() {
        return transactionDetailsData;
    }

    @SerializedName("TransactionDetailsData")
    public void setTransactionDetailsData(TransactionDetailsData transactionDetailsData) {
        this.transactionDetailsData = transactionDetailsData;
    }


    public static class TransactionDetailsData implements Serializable {

        @SerializedName("a:Amount")
        private String Amount;

        @SerializedName("a:ApprovalCode")
        private String ApprovalCode;

        @SerializedName("a:CardExpiryDate")
        private String CardExpiryDate;

        @SerializedName("a:CardNumber")
        private String CardNumber;

        @SerializedName("a:CardType")
        private String CardType;

        @SerializedName("a:CustomerName")
        private String CustomerName;

        @SerializedName("a:DateTime")
        private String DateTime;

        @SerializedName("a:ErrorDescription")
        private String ErrorDescription;

        @SerializedName("a:IsReversed")
        private Boolean IsReversed = false;

        @SerializedName("a:MachineID")
        private String MachineID;

        @SerializedName("a:MerchantID")
        private String MerchantID;

        @SerializedName("a:MerchantName")
        private String MerchantName;

        @SerializedName("a:POSEntryMode")
        private String POSEntryMode;

        @SerializedName("a:PaySlipNumber")
        private String PaySlipNumber;

        @SerializedName("a:ReceiptNumber")
        private String ReceiptNumber;

        @SerializedName("a:RequestingApplicationID")
        private String RequestingApplicationID;

        @SerializedName("a:ResponseCode")
        private String ResponseCode;

        @SerializedName("a:TransactionCurrency")
        private String TransactionCurrency;

        @SerializedName("a:TransactionReferenceNumber")
        private String TransactionReferenceNumber;

        @SerializedName("a:TransactionType")
        private String TransactionType;

        public Boolean getIsReversed() {
            return IsReversed;
        }

        public void setIsReversed(Boolean isReversed) {
            IsReversed = isReversed;
        }

        public String getAmount() {
            return Amount;
        }

        @SerializedName("a:Amount")
        public void setAmount(String amount) {
            this.Amount = amount;
        }

        @SerializedName("a:ApprovalCode")
        public String getApprovalCode() {
            return ApprovalCode;
        }

        @SerializedName("a:ApprovalCode")
        public void setApprovalCode(String approvalCode) {
            this.ApprovalCode = approvalCode;
        }

        @SerializedName("a:CardExpiryDate")
        public String getCardExpiryDate() {
            return CardExpiryDate;
        }

        @SerializedName("a:CardExpiryDate")
        public void setCardExpiryDate(String cardExpiryDate) {
            this.CardExpiryDate = cardExpiryDate;
        }

        @SerializedName("a:CardNumber")
        public String getCardNumber() {
            return CardNumber;
        }

        @SerializedName("a:CardNumber")
        public void setCardNumber(String cardNumber) {
            this.CardNumber = cardNumber;
        }

        @SerializedName("a:CardType")
        public String getCardType() {
            return CardType;
        }

        @SerializedName("a:CardType")
        public void setCardType(String cardType) {
            this.CardType = cardType;
        }

        @SerializedName("a:CustomerName")
        public String getCustomerName() {
            return CustomerName;
        }

        @SerializedName("a:CustomerName")
        public void setCustomerName(String customerName) {
            this.CustomerName = customerName;
        }

        @SerializedName("a:DateTime")
        public String getDateTime() {
            return DateTime;
        }

        @SerializedName("a:DateTime")
        public void setDateTime(String dateTime) {
            this.DateTime = dateTime;
        }

        @SerializedName("a:ErrorDescription")
        public String getErrorDescription() {
            return ErrorDescription;
        }

        @SerializedName("a:ErrorDescription")
        public void setErrorDescription(String errorDescription) {
            this.ErrorDescription = errorDescription;
        }

        @SerializedName("a:MachineID")
        public String getMachineID() {
            return MachineID;
        }

        @SerializedName("a:MachineID")
        public void setMachineID(String machineID) {
            this.MachineID = machineID;
        }

        @SerializedName("a:MerchantID")
        public String getMerchantID() {
            return MerchantID;
        }

        @SerializedName("a:MerchantID")
        public void setMerchantID(String merchantID) {
            this.MerchantID = merchantID;
        }

        @SerializedName("a:MerchantName")
        public String getMerchantName() {
            return MerchantName;
        }

        @SerializedName("a:MerchantName")
        public void setMerchantName(String merchantName) {
            this.MerchantName = merchantName;
        }

        @SerializedName("a:POSEntryMode")
        public String getPOSEntryMode() {
            return POSEntryMode;
        }

        @SerializedName("a:POSEntryMode")
        public void setPOSEntryMode(String pOSEntryMode) {
            this.POSEntryMode = pOSEntryMode;
        }

        @SerializedName("a:PaySlipNumber")
        public String getPaySlipNumber() {
            return PaySlipNumber;
        }

        @SerializedName("a:PaySlipNumber")
        public void setPaySlipNumber(String paySlipNumber) {
            this.PaySlipNumber = paySlipNumber;
        }

        @SerializedName("a:ReceiptNumber")
        public String getReceiptNumber() {
            return ReceiptNumber;
        }

        @SerializedName("a:ReceiptNumber")
        public void setReceiptNumber(String receiptNumber) {
            this.ReceiptNumber = receiptNumber;
        }

        @SerializedName("a:RequestingApplicationID")
        public String getRequestingApplicationID() {
            return RequestingApplicationID;
        }

        @SerializedName("a:RequestingApplicationID")
        public void setRequestingApplicationID(String requestingApplicationID) {
            this.RequestingApplicationID = requestingApplicationID;
        }

        @SerializedName("a:ResponseCode")
        public String getResponseCode() {
            return ResponseCode;
        }

        @SerializedName("a:ResponseCode")
        public void setResponseCode(String responseCode) {
            this.ResponseCode = responseCode;
        }

        @SerializedName("a:TransactionCurrency")
        public String getTransactionCurrency() {
            return TransactionCurrency;
        }

        @SerializedName("a:TransactionCurrency")
        public void setTransactionCurrency(String transactionCurrency) {
            this.TransactionCurrency = transactionCurrency;
        }

        @SerializedName("a:TransactionReferenceNumber")
        public String getTransactionReferenceNumber() {
            return TransactionReferenceNumber;
        }

        @SerializedName("a:TransactionReferenceNumber")
        public void setTransactionReferenceNumber(String transactionReferenceNumber) {
            this.TransactionReferenceNumber = transactionReferenceNumber;
        }

        @SerializedName("a:TransactionType")
        public String getTransactionType() {
            return TransactionType;
        }

        @SerializedName("a:TransactionType")
        public void setTransactionType(String transactionType) {
            this.TransactionType = transactionType;
        }

    }

}
