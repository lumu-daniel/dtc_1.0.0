package com.mlt.e200cp.models;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.Serializable;

public class PaymentDetailsModel implements Serializable {

    private long PaymentDetailId;

    private String TokenReferenceNumber;

    private String ReferenceNumber;

    private String TransactionId;

    private int PaymentTypeId;

    private String NameEn;

    private String NameAr;

    private Double DueAmount = 0.0;

    private Double PaidAmount = 0.0;

    private Double RemainingAmount = 0.0;

    private String BankReferenceNumber;

    private Double ServiceAmount = 0.0;

    private Double ServiceCharges = 0.0;

    private Double CashAccepted = 0.0;

    private Double CoinAccepted = 0.0;

    private Double CashRetruned = 0.0;

    private Double CointRetruned = 0.0;

    private Double CashRejected = 0.0;

    private Double CoinRejected = 0.0;

    private Double MissingAmount = 0.0;

    private Double BalanceAmount = 0.0;

    private String PaymentStatus;

    private String PrintStatus;

    private long CreatedBy;

    private String TerminalId;

    private String SentReceiveDate = new DateTime(DateTimeZone.UTC).toString();;

    private String TRFNumber;

    private String ApprovalCode;

    private String PayeeFirstName;

    private String PayeeLastName;

    private String PayeeEmiratesId;

    private String PayeePassportNo;

    private String CardNo;

    private String ChequeType;

    private String ChequeNo;

    private String ChequeDate = new DateTime(DateTimeZone.UTC).toString();

    private String StrChequeDate = new DateTime(DateTimeZone.UTC).toString();

    private String ChequeSerial;

    private String AccountNo;

    private byte[] ChequeFrontScan;

    private byte[] ChequeBackScan;

    private String ChequeScanFileExtension;

    private String ChequeAmount;

    private String Iban;

    private String SwiftCode;

    private String BankName;

    private String AddressLine1;

    private String AddressLine2;

    private String City;

    private String State;

    private String ZipCode;

    private String PostalCode;

    private String Country;

    private String StatusCode;

    private String Status;

    private String StatusMessage;

    private String Merchant;

    private String SubMerchnat;

    private String ServiceID;

    private String SetviseCode;

    private String SessionId;

    private String TrafficNumber;

    private String CardNumber;

    private String AuthCode;

    private String CustomerEmail;

    private String ServiceName;

    private String PlateNo;

    public String getPlateNo() {
        return PlateNo;
    }

    public void setPlateNo(String plateNo) {
        PlateNo = plateNo;
    }

    public String getServiceName() {
        return ServiceName;
    }

    public void setServiceName(String serviceName) {
        ServiceName = serviceName;
    }

    public String getCustomerEmail() {
        return CustomerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        CustomerEmail = customerEmail;
    }

    public long getPaymentDetailId() {
        return PaymentDetailId;
    }

    public void setPaymentDetailId(long paymentDetailId) {
        PaymentDetailId = paymentDetailId;
    }

    public String getTokenReferenceNumber() {
        return TokenReferenceNumber;
    }

    public void setTokenReferenceNumber(String tokenReferenceNumber) {
        TokenReferenceNumber = tokenReferenceNumber;
    }

    public String getReferenceNumber() {
        return ReferenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        ReferenceNumber = referenceNumber;
    }

    public String getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(String transactionId) {
        TransactionId = transactionId;
    }

    public int getPaymentTypeId() {
        return PaymentTypeId;
    }

    public void setPaymentTypeId(int paymentTypeId) {
        PaymentTypeId = paymentTypeId;
    }

    public String getNameEn() {
        return NameEn;
    }

    public void setNameEn(String nameEn) {
        NameEn = nameEn;
    }

    public String getNameAr() {
        return NameAr;
    }

    public void setNameAr(String nameAr) {
        NameAr = nameAr;
    }

    public Double getDueAmount() {
        return DueAmount;
    }

    public void setDueAmount(Double dueAmount) {
        DueAmount = dueAmount;
    }

    public Double getPaidAmount() {
        return PaidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        PaidAmount = paidAmount;
    }

    public Double getRemainingAmount() {
        return RemainingAmount;
    }

    public void setRemainingAmount(Double remainingAmount) {
        RemainingAmount = remainingAmount;
    }

    public String getBankReferenceNumber() {
        return BankReferenceNumber;
    }

    public void setBankReferenceNumber(String bankReferenceNumber) {
        BankReferenceNumber = bankReferenceNumber;
    }

    public Double getServiceAmount() {
        return ServiceAmount;
    }

    public void setServiceAmount(Double serviceAmount) {
        ServiceAmount = serviceAmount;
    }

    public Double getServiceCharges() {
        return ServiceCharges;
    }

    public void setServiceCharges(Double serviceCharges) {
        ServiceCharges = serviceCharges;
    }

    public Double getCashAccepted() {
        return CashAccepted;
    }

    public void setCashAccepted(Double cashAccepted) {
        CashAccepted = (cashAccepted==null)?0.0:cashAccepted;
    }

    public Double getCoinAccepted() {
        return CoinAccepted;
    }

    public void setCoinAccepted(Double coinAccepted) {
        CoinAccepted = (coinAccepted==null)?0.0:coinAccepted;
    }

    public Double getCashRetruned() {
        return CashRetruned;
    }

    public void setCashRetruned(Double cashRetruned) {
        CashRetruned = cashRetruned;
    }

    public Double getCointRetruned() {
        return CointRetruned;
    }

    public void setCointRetruned(Double cointRetruned) {
        CointRetruned = (cointRetruned==null)?0.0:cointRetruned;
    }

    public Double getCashRejected() {
        return CashRejected;
    }

    public void setCashRejected(Double cashRejected) {
        CashRejected = (cashRejected==null)?0.0:cashRejected;
    }

    public Double getCoinRejected() {
        return CoinRejected;
    }

    public void setCoinRejected(Double coinRejected) {
        CoinRejected = coinRejected;
    }

    public Double getMissingAmount() {
        return MissingAmount;
    }

    public void setMissingAmount(Double missingAmount) {
        MissingAmount = missingAmount;
    }

    public Double getBalanceAmount() {
        return BalanceAmount;
    }

    public void setBalanceAmount(Double balanceAmount) {
        BalanceAmount = balanceAmount;
    }

    public String getPaymentStatus() {
        return PaymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        PaymentStatus = paymentStatus;
    }

    public String getPrintStatus() {
        return PrintStatus;
    }

    public void setPrintStatus(String printStatus) {
        PrintStatus = printStatus;
    }

    public long getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(long createdBy) {
        CreatedBy = createdBy;
    }

    public String getCreatedDate() {
        return new DateTime(DateTimeZone.UTC).toString();
    }

    public void setCreatedDate(String createdDate) {
    }

    public String getTerminalId() {
        return TerminalId;
    }

    public void setTerminalId(String terminalId) {
        TerminalId = terminalId;
    }

    public String getSentReceiveDate() {
        return SentReceiveDate;
    }

    public void setSentReceiveDate(String sentReceiveDate) {
        SentReceiveDate = new DateTime(DateTimeZone.UTC).toString();
    }

    public String getTRFNumber() {
        return TRFNumber;
    }

    public void setTRFNumber(String TRFNumber) {
        this.TRFNumber = TRFNumber;
    }

    public String getApprovalCode() {
        return ApprovalCode;
    }

    public void setApprovalCode(String approvalCode) {
        ApprovalCode = approvalCode;
    }

    public String getPayeeFirstName() {
        return PayeeFirstName;
    }

    public void setPayeeFirstName(String payeeFirstName) {
        PayeeFirstName = payeeFirstName;
    }

    public String getPayeeLastName() {
        return PayeeLastName;
    }

    public void setPayeeLastName(String payeeLastName) {
        PayeeLastName = payeeLastName;
    }

    public String getPayeeEmiratesId() {
        return PayeeEmiratesId;
    }

    public void setPayeeEmiratesId(String payeeEmiratesId) {
        PayeeEmiratesId = payeeEmiratesId;
    }

    public String getPayeePassportNo() {
        return PayeePassportNo;
    }

    public void setPayeePassportNo(String payeePassportNo) {
        PayeePassportNo = payeePassportNo;
    }

    public String getCardNo() {
        return CardNo;
    }

    public void setCardNo(String cardNo) {
        CardNo = cardNo;
    }

    public String getChequeType() {
        return ChequeType;
    }

    public void setChequeType(String chequeType) {
        ChequeType = chequeType;
    }

    public String getChequeNo() {
        return ChequeNo;
    }

    public void setChequeNo(String chequeNo) {
        ChequeNo = chequeNo;
    }

    public String getChequeDate() {
        return ChequeDate;
    }

    public void setChequeDate(String chequeDate) {
        ChequeDate = new DateTime(DateTimeZone.UTC).toString();
    }

    public String getStrChequeDate() {
        return StrChequeDate;
    }

    public void setStrChequeDate(String strChequeDate) {
        StrChequeDate = new DateTime(DateTimeZone.UTC).toString();
    }

    public String getChequeSerial() {
        return ChequeSerial;
    }

    public void setChequeSerial(String chequeSerial) {
        ChequeSerial = chequeSerial;
    }

    public String getAccountNo() {
        return AccountNo;
    }

    public void setAccountNo(String accountNo) {
        AccountNo = accountNo;
    }

    public byte[] getChequeFrontScan() {
        return ChequeFrontScan;
    }

    public void setChequeFrontScan(byte[] chequeFrontScan) {
        ChequeFrontScan = chequeFrontScan;
    }

    public byte[] getChequeBackScan() {
        return ChequeBackScan;
    }

    public void setChequeBackScan(byte[] chequeBackScan) {
        ChequeBackScan = chequeBackScan;
    }

    public String getChequeScanFileExtension() {
        return ChequeScanFileExtension;
    }

    public void setChequeScanFileExtension(String chequeScanFileExtension) {
        ChequeScanFileExtension = chequeScanFileExtension;
    }

    public String getChequeAmount() {
        return ChequeAmount;
    }

    public void setChequeAmount(String chequeAmount) {
        ChequeAmount = chequeAmount;
    }

    public String getIban() {
        return Iban;
    }

    public void setIban(String iban) {
        Iban = iban;
    }

    public String getSwiftCode() {
        return SwiftCode;
    }

    public void setSwiftCode(String swiftCode) {
        SwiftCode = swiftCode;
    }

    public String getBankName() {
        return BankName;
    }

    public void setBankName(String bankName) {
        BankName = bankName;
    }

    public String getAddressLine1() {
        return AddressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        AddressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return AddressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        AddressLine2 = addressLine2;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getZipCode() {
        return ZipCode;
    }

    public void setZipCode(String zipCode) {
        ZipCode = zipCode;
    }

    public String getPostalCode() {
        return PostalCode;
    }

    public void setPostalCode(String postalCode) {
        PostalCode = postalCode;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getStatusCode() {
        return StatusCode;
    }

    public void setStatusCode(String statusCode) {
        StatusCode = statusCode;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getStatusMessage() {
        return StatusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        StatusMessage = statusMessage;
    }

    public String getMerchant() {
        return Merchant;
    }

    public void setMerchant(String merchant) {
        Merchant = merchant;
    }

    public String getSubMerchnat() {
        return SubMerchnat;
    }

    public void setSubMerchnat(String subMerchnat) {
        SubMerchnat = subMerchnat;
    }

    public String getServiceID() {
        return ServiceID;
    }

    public void setServiceID(String serviceID) {
        ServiceID = serviceID;
    }

    public String getSetviseCode() {
        return SetviseCode;
    }

    public void setSetviseCode(String setviseCode) {
        SetviseCode = setviseCode;
    }

    public String getSessionId() {
        return SessionId;
    }

    public void setSessionId(String sessionId) {
        SessionId = sessionId;
    }

    public String getTrafficNumber() {
        return TrafficNumber;
    }

    public void setTrafficNumber(String trafficNumber) {
        TrafficNumber = trafficNumber;
    }

    public String getCardNumber() {
        return CardNumber;
    }

    public void setCardNumber(String cardNumber) {
        CardNumber = cardNumber;
    }

    public String getAuthCode() {
        return AuthCode;
    }

    public void setAuthCode(String authCode) {
        AuthCode = authCode;
    }
}