package com.mlt.dtc.model;

import java.io.Serializable;
import java.util.List;

public
class TaxiMeterInfoRequestDetails  implements Serializable {

    private String ServiceId;
    private String RequestId;
    private String SourceApplication;
    private String RequestType;
    private String RequestCategory;
    private String TimeStamp;
    // Login Credentials
    public TaxiMeterInfoRequestDetails.Login Login;
    // Customer Unique value to be passed as a List
    private List<CKeyValuePair> CustomerUniqueNo;

    /**
     * Header Content Member
     */
    public String getServiceId() {
        return ServiceId;
    }

    public void setServiceId(String serviceId) {
        ServiceId = serviceId;
    }

    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String requestId) {
        RequestId = requestId;
    }

    public String getSourceApplication() {
        return SourceApplication;
    }

    public void setSourceApplication(String sourceApplication) {
        SourceApplication = sourceApplication;
    }

    public String getRequestType() {
        return RequestType;
    }

    public void setRequestType(String requestType) {
        RequestType = requestType;
    }

    public String getRequestCategory() {
        return RequestCategory;
    }

    public void setRequestCategory(String requestCategory) {
        RequestCategory = requestCategory;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String imeStamp) {
        this.TimeStamp = imeStamp;
    }
    public TaxiMeterInfoRequestDetails.Login getLogin() {
        return Login;
    }

    public void setLogin(TaxiMeterInfoRequestDetails.Login login) {
        Login = login;
    }

    public static class Login implements  Serializable{

        public String getLoginID() {
            return LoginID;
        }

        public void setLoginID(String loginId) {
            LoginID = loginId;
        }

        public String getPassword() {
            return Password;
        }

        public void setPassword(String password) {
            Password = password;
        }

        public String LoginID;

        public String Password;
    }

    /**
     * Body Content Member
     */
    public List<CKeyValuePair> getCustomerUniqueNo() {
        return CustomerUniqueNo;
    }

    public void setCustomerUniqueNo(List<CKeyValuePair> customerUniqueNo) {
        CustomerUniqueNo = customerUniqueNo;
    }
}
