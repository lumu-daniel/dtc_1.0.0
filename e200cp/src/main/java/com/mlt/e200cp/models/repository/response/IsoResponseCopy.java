package com.mlt.e200cp.models.repository.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class IsoResponseCopy implements Serializable {

    @SerializedName("Body")
    private Body body;

    @SerializedName("Body")
    public Body getBody() {
        return body;
    }

    @SerializedName("Body")
    public void setBody(Body body) {
        this.body = body;
    }

    public static class Body {

        @SerializedName("InquiryResponse")
        private InquiryResponse inquiryResponse;
        
        @SerializedName("InquiryResponse")
        public InquiryResponse getInquiryResponse() {
            return inquiryResponse;
        }

        @SerializedName("InquiryResponse")
        public void setInquiryResponse(InquiryResponse inquiryResponse) {
            this.inquiryResponse = inquiryResponse;
        }

    }

    public static class InquiryResponse {

        @SerializedName("ErrorDescription")
        private String errorDescription;
        @SerializedName("IAD")
        private IAD iAD;
        @SerializedName("IssuerScriptingData71")
        private IssuerScriptingData71 issuerScriptingData71;
        @SerializedName("IssuerScriptingData72")
        private IssuerScriptingData72 issuerScriptingData72;
        @SerializedName("ReceiptCustomerCopy")
        private String receiptCustomerCopy;
        @SerializedName("ReceiptCustomerCopyHtml")
        private ReceiptCustomerCopyHtml receiptCustomerCopyHtml;
        @SerializedName("ReceiptMerchantCopy")
        private String receiptMerchantCopy;
        @SerializedName("ReceiptMerchantCopyHtml")
        private ReceiptMerchantCopyHtml receiptMerchantCopyHtml;
        @SerializedName("ResponseMessage")
        private String responseMessage;
        @SerializedName("ReversalCauseReceiptCustomerCopy")
        private ReversalCauseReceiptCustomerCopy reversalCauseReceiptCustomerCopy;
        @SerializedName("ReversalCauseReceiptCustomerCopyHtml")
        private ReversalCauseReceiptCustomerCopyHtml reversalCauseReceiptCustomerCopyHtml;
        @SerializedName("ReversalCauseReceiptMerchantCopy")
        private ReversalCauseReceiptMerchantCopy reversalCauseReceiptMerchantCopy;
        @SerializedName("ReversalCauseReceiptMerchantCopyHtml")
        private ReversalCauseReceiptMerchantCopyHtml reversalCauseReceiptMerchantCopyHtml;
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
        public IAD getIAD() {
            return iAD;
        }

        @SerializedName("IAD")
        public void setIAD(IAD iAD) {
            this.iAD = iAD;
        }

        @SerializedName("IssuerScriptingData71")
        public IssuerScriptingData71 getIssuerScriptingData71() {
            return issuerScriptingData71;
        }

        @SerializedName("IssuerScriptingData71")
        public void setIssuerScriptingData71(IssuerScriptingData71 issuerScriptingData71) {
            this.issuerScriptingData71 = issuerScriptingData71;
        }

        @SerializedName("IssuerScriptingData72")
        public IssuerScriptingData72 getIssuerScriptingData72() {
            return issuerScriptingData72;
        }

        @SerializedName("IssuerScriptingData72")
        public void setIssuerScriptingData72(IssuerScriptingData72 issuerScriptingData72) {
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
        public ReceiptCustomerCopyHtml getReceiptCustomerCopyHtml() {
            return receiptCustomerCopyHtml;
        }

        @SerializedName("ReceiptCustomerCopyHtml")
        public void setReceiptCustomerCopyHtml(ReceiptCustomerCopyHtml receiptCustomerCopyHtml) {
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
        public ReceiptMerchantCopyHtml getReceiptMerchantCopyHtml() {
            return receiptMerchantCopyHtml;
        }

        @SerializedName("ReceiptMerchantCopyHtml")
        public void setReceiptMerchantCopyHtml(ReceiptMerchantCopyHtml receiptMerchantCopyHtml) {
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
        public ReversalCauseReceiptCustomerCopy getReversalCauseReceiptCustomerCopy() {
            return reversalCauseReceiptCustomerCopy;
        }

        @SerializedName("ReversalCauseReceiptCustomerCopy")
        public void setReversalCauseReceiptCustomerCopy(ReversalCauseReceiptCustomerCopy reversalCauseReceiptCustomerCopy) {
            this.reversalCauseReceiptCustomerCopy = reversalCauseReceiptCustomerCopy;
        }

        @SerializedName("ReversalCauseReceiptCustomerCopyHtml")
        public ReversalCauseReceiptCustomerCopyHtml getReversalCauseReceiptCustomerCopyHtml() {
            return reversalCauseReceiptCustomerCopyHtml;
        }

        @SerializedName("ReversalCauseReceiptCustomerCopyHtml")
        public void setReversalCauseReceiptCustomerCopyHtml(ReversalCauseReceiptCustomerCopyHtml reversalCauseReceiptCustomerCopyHtml) {
            this.reversalCauseReceiptCustomerCopyHtml = reversalCauseReceiptCustomerCopyHtml;
        }

        @SerializedName("ReversalCauseReceiptMerchantCopy")
        public ReversalCauseReceiptMerchantCopy getReversalCauseReceiptMerchantCopy() {
            return reversalCauseReceiptMerchantCopy;
        }

        @SerializedName("ReversalCauseReceiptMerchantCopy")
        public void setReversalCauseReceiptMerchantCopy(ReversalCauseReceiptMerchantCopy reversalCauseReceiptMerchantCopy) {
            this.reversalCauseReceiptMerchantCopy = reversalCauseReceiptMerchantCopy;
        }

        @SerializedName("ReversalCauseReceiptMerchantCopyHtml")
        public ReversalCauseReceiptMerchantCopyHtml getReversalCauseReceiptMerchantCopyHtml() {
            return reversalCauseReceiptMerchantCopyHtml;
        }

        @SerializedName("ReversalCauseReceiptMerchantCopyHtml")
        public void setReversalCauseReceiptMerchantCopyHtml(ReversalCauseReceiptMerchantCopyHtml reversalCauseReceiptMerchantCopyHtml) {
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

        public static class IssuerScriptingData72 {

            @SerializedName("@nil")
            private String nil;

            @SerializedName("@nil")
            public String getNil() {
                return nil;
            }

            @SerializedName("@nil")
            public void setNil(String nil) {
                this.nil = nil;
            }

        }
        public static class IssuerScriptingData71 {

            @SerializedName("@nil")
            private String nil;

            @SerializedName("@nil")
            public String getNil() {
                return nil;
            }

            @SerializedName("@nil")
            public void setNil(String nil) {
                this.nil = nil;
            }

        }

        public static class ReceiptCustomerCopyHtml {

            @SerializedName("@nil")
            private String nil;

            @SerializedName("@nil")
            public String getNil() {
                return nil;
            }

            @SerializedName("@nil")
            public void setNil(String nil) {
                this.nil = nil;
            }

        }

        public static class ReceiptMerchantCopyHtml {

            @SerializedName("@nil")
            private String nil;

            @SerializedName("@nil")
            public String getNil() {
                return nil;
            }

            @SerializedName("@nil")
            public void setNil(String nil) {
                this.nil = nil;
            }

        }

        public static class ReversalCauseReceiptCustomerCopy {

            @SerializedName("@nil")
            private String nil;

            @SerializedName("@nil")
            public String getNil() {
                return nil;
            }

            @SerializedName("@nil")
            public void setNil(String nil) {
                this.nil = nil;
            }

        }

        public static class ReversalCauseReceiptCustomerCopyHtml {

            @SerializedName("@nil")
            private String nil;

            @SerializedName("@nil")
            public String getNil() {
                return nil;
            }

            @SerializedName("@nil")
            public void setNil(String nil) {
                this.nil = nil;
            }


        }

        public static class ReversalCauseReceiptMerchantCopy {

            @SerializedName("@nil")
            private String nil;

            @SerializedName("@nil")
            public String getNil() {
                return nil;
            }

            @SerializedName("@nil")
            public void setNil(String nil) {
                this.nil = nil;
            }

        }

        public static class ReversalCauseReceiptMerchantCopyHtml {

            @SerializedName("@nil")
            private String nil;

            @SerializedName("@nil")
            public String getNil() {
                return nil;
            }

            @SerializedName("@nil")
            public void setNil(String nil) {
                this.nil = nil;
            }

        }

        public static class TransactionDetailsData {

            @SerializedName("Amount")
            private String amount;
            @SerializedName("ApprovalCode")
            private String approvalCode;
            @SerializedName("CardExpiryDate")
            private String cardExpiryDate;
            @SerializedName("CardNumber")
            private String cardNumber;
            @SerializedName("CardType")
            private String cardType;
            @SerializedName("CustomerName")
            private Object customerName;
            @SerializedName("DateTime")
            private String dateTime;
            @SerializedName("ErrorDescription")
            private Object errorDescription;
            @SerializedName("MachineID")
            private String machineID;
            @SerializedName("MerchantID")
            private String merchantID;
            @SerializedName("MerchantName")
            private String merchantName;
            @SerializedName("POSEntryMode")
            private String pOSEntryMode;
            @SerializedName("PaySlipNumber")
            private String paySlipNumber;
            @SerializedName("ReceiptNumber")
            private String receiptNumber;
            @SerializedName("RequestingApplicationID")
            private String requestingApplicationID;
            @SerializedName("ResponseCode")
            private String responseCode;
            @SerializedName("TransactionCurrency")
            private String transactionCurrency;
            @SerializedName("TransactionReferenceNumber")
            private String transactionReferenceNumber;
            @SerializedName("TransactionType")
            private String transactionType;

            @SerializedName("Amount")
            public String getAmount() {
                return amount;
            }

            @SerializedName("Amount")
            public void setAmount(String amount) {
                this.amount = amount;
            }

            @SerializedName("ApprovalCode")
            public String getApprovalCode() {
                return approvalCode;
            }

            @SerializedName("ApprovalCode")
            public void setApprovalCode(String approvalCode) {
                this.approvalCode = approvalCode;
            }

            @SerializedName("CardExpiryDate")
            public String getCardExpiryDate() {
                return cardExpiryDate;
            }

            @SerializedName("CardExpiryDate")
            public void setCardExpiryDate(String cardExpiryDate) {
                this.cardExpiryDate = cardExpiryDate;
            }

            @SerializedName("CardNumber")
            public String getCardNumber() {
                return cardNumber;
            }

            @SerializedName("CardNumber")
            public void setCardNumber(String cardNumber) {
                this.cardNumber = cardNumber;
            }

            @SerializedName("CardType")
            public String getCardType() {
                return cardType;
            }

            @SerializedName("CardType")
            public void setCardType(String cardType) {
                this.cardType = cardType;
            }

            @SerializedName("CustomerName")
            public Object getCustomerName() {
                return customerName;
            }

            @SerializedName("CustomerName")
            public void setCustomerName(Object customerName) {
                this.customerName = customerName;
            }

            @SerializedName("DateTime")
            public String getDateTime() {
                return dateTime;
            }

            @SerializedName("DateTime")
            public void setDateTime(String dateTime) {
                this.dateTime = dateTime;
            }

            @SerializedName("ErrorDescription")
            public Object getErrorDescription() {
                return errorDescription;
            }

            @SerializedName("ErrorDescription")
            public void setErrorDescription(Object errorDescription) {
                this.errorDescription = errorDescription;
            }

            @SerializedName("MachineID")
            public String getMachineID() {
                return machineID;
            }

            @SerializedName("MachineID")
            public void setMachineID(String machineID) {
                this.machineID = machineID;
            }

            @SerializedName("MerchantID")
            public String getMerchantID() {
                return merchantID;
            }

            @SerializedName("MerchantID")
            public void setMerchantID(String merchantID) {
                this.merchantID = merchantID;
            }

            @SerializedName("MerchantName")
            public String getMerchantName() {
                return merchantName;
            }

            @SerializedName("MerchantName")
            public void setMerchantName(String merchantName) {
                this.merchantName = merchantName;
            }

            @SerializedName("POSEntryMode")
            public String getPOSEntryMode() {
                return pOSEntryMode;
            }

            @SerializedName("POSEntryMode")
            public void setPOSEntryMode(String pOSEntryMode) {
                this.pOSEntryMode = pOSEntryMode;
            }

            @SerializedName("PaySlipNumber")
            public String getPaySlipNumber() {
                return paySlipNumber;
            }

            @SerializedName("PaySlipNumber")
            public void setPaySlipNumber(String paySlipNumber) {
                this.paySlipNumber = paySlipNumber;
            }

            @SerializedName("ReceiptNumber")
            public String getReceiptNumber() {
                return receiptNumber;
            }

            @SerializedName("ReceiptNumber")
            public void setReceiptNumber(String receiptNumber) {
                this.receiptNumber = receiptNumber;
            }

            @SerializedName("RequestingApplicationID")
            public String getRequestingApplicationID() {
                return requestingApplicationID;
            }

            @SerializedName("RequestingApplicationID")
            public void setRequestingApplicationID(String requestingApplicationID) {
                this.requestingApplicationID = requestingApplicationID;
            }

            @SerializedName("ResponseCode")
            public String getResponseCode() {
                return responseCode;
            }

            @SerializedName("ResponseCode")
            public void setResponseCode(String responseCode) {
                this.responseCode = responseCode;
            }

            @SerializedName("TransactionCurrency")
            public String getTransactionCurrency() {
                return transactionCurrency;
            }

            @SerializedName("TransactionCurrency")
            public void setTransactionCurrency(String transactionCurrency) {
                this.transactionCurrency = transactionCurrency;
            }

            @SerializedName("TransactionReferenceNumber")
            public String getTransactionReferenceNumber() {
                return transactionReferenceNumber;
            }

            @SerializedName("TransactionReferenceNumber")
            public void setTransactionReferenceNumber(String transactionReferenceNumber) {
                this.transactionReferenceNumber = transactionReferenceNumber;
            }

            @SerializedName("TransactionType")
            public String getTransactionType() {
                return transactionType;
            }

            @SerializedName("TransactionType")
            public void setTransactionType(String transactionType) {
                this.transactionType = transactionType;
            }

        }

        public static class IAD {

            @SerializedName("@nil")
            private String nil;

            @SerializedName("@nil")
            public String getNil() {
                return nil;
            }

            @SerializedName("@nil")
            public void setNil(String nil) {
                this.nil = nil;
            }

        }
    }
}
