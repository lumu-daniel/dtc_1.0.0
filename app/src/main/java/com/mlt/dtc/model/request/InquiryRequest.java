package com.mlt.dtc.model.request;

import com.mlt.dtc.model.CKeyValuePair;

import java.io.Serializable;
import java.util.List;



/*
  Created by raheel on 3/6/2018.
 */

/**
 *  Inquiry Request Class Models
 */
public class InquiryRequest implements Serializable {

    /**
     *  Header Content Member
     */

    // Version Number
    public String Version;

    // Current Time Stamp
    public String TimeStamp;

    // Terminal Id of the device
    public String Terminalid;

    // Source Application either Mobile,Web,Desktop
    public String SourceApplication;

    // Signature Fields to create the hash
    public String SignatureFields;

    // Service Name
    public String ServiceName;

    // Service ID
    public String ServiceId;

    // Secure hash to be created for every request
    public String SecureHash;

    // Request Type i.e Inquiry or Payment
    public String RequestType;

    // Request ID
    public String RequestId;

    // Request Category
    public String RequestCategory;

    // Merchant Id to be configure
    public String MerchantId;

    // Login Credentials
    public Login Login;

    // Language for the request and response
    public String Language;

    // IP assigned from the Service has been called
    public String IpAssigned;

    // Bank Id
    public String BankId;

    // Login Class Objects
    public static class Login {

        public String Description;

        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
        }

        public String getLoginId() {
            return LoginId;
        }

        public void setLoginId(String loginId) {
            LoginId = loginId;
        }

        public String getPassword() {
            return Password;
        }

        public void setPassword(String password) {
            Password = password;
        }

        public String LoginId;

        public String Password;
    }

    /**
     *  Body Content Member
     */

    // Customer Unique value to be passed as a List
    public List<CKeyValuePair> CustomerUniqueNo;


    // Service atttribute list to be passed
    public List<CKeyValuePair> ServiceAttributesList;


    //*********************** GETTER SETTER FOR THE HEADERS ***************************** //////////

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getTerminalid() {
        return Terminalid;
    }

    public void setTerminalid(String terminalid) {
        Terminalid = terminalid;
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

    public String getServiceId() {
        return ServiceId;
    }

    public void setServiceId(String serviceId) {
        ServiceId = serviceId;
    }

    public String getSecureHash() {
        return SecureHash;
    }

    public void setSecureHash(String secureHash) {
        SecureHash = secureHash;
    }

    public String getRequestType() {
        return RequestType;
    }

    public void setRequestType(String requestType) {
        RequestType = requestType;
    }

    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String requestId) {
        RequestId = requestId;
    }

    public String getRequestCategory() {
        return RequestCategory;
    }

    public void setRequestCategory(String requestCategory) {
        RequestCategory = requestCategory;
    }

    public String getMerchantId() {
        return MerchantId;
    }

    public void setMerchantId(String merchantId) {
        MerchantId = merchantId;
    }

    public Login getLogin() {
        return Login;
    }

    public void setLogin(Login login) {
        Login = login;
    }

    public String getLanguage() {
        return Language;
    }

    public void setLanguage(String language) {
        Language = language;
    }

    public String getIpAssigned() {
        return IpAssigned;
    }

    public void setIpAssigned(String ipAssigned) {
        IpAssigned = ipAssigned;
    }

    public String getBankId() {
        return BankId;
    }

    public void setBankId(String bankId) {
        BankId = bankId;
    }


    // ************************ GETTER AND SETTER FOR THE BODY MEMBER ************************* //

    public List<CKeyValuePair> getCustomerUniqueNo() {
        return CustomerUniqueNo;
    }

    public void setCustomerUniqueNo(List<CKeyValuePair> customerUniqueNo) {
        CustomerUniqueNo = customerUniqueNo;
    }



    public List<CKeyValuePair> getServiceAttributesList() {
        return ServiceAttributesList;
    }

    public void setServiceAttributesList(List<CKeyValuePair> serviceAttributesList) {
        ServiceAttributesList = serviceAttributesList;
    }

}

