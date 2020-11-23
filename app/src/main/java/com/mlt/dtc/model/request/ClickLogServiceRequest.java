package com.mlt.dtc.model.request;



import com.mlt.dtc.model.CKeyValuePair;

import java.io.Serializable;
import java.util.List;




/**
 * Created by talal on 10/9/2017.
 */

public class ClickLogServiceRequest implements Serializable {

    private String ServiceId;
    private String RequestId;
    private String SourceApplication;
    private String RequestType;
    private String RequestCategory;
    private String TimeStamp;
    // Login Credentials
    public ClickLogServiceRequest.Login Login;
    // Customer Unique value to be passed as a List
    private List<CKeyValuePair> CustomerUniqueNo;
    // Services Attribute List to be passed as a List
    private List<CKeyValuePair> ServiceAttributeList;

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

    public ClickLogServiceRequest.Login getLogin() {
        return Login;
    }

    public void setLogin(ClickLogServiceRequest.Login login) {
        Login = login;
    }

    public List<CKeyValuePair> getServiceAttributeList() {
        return ServiceAttributeList;
    }

    public void setServiceAttributeList(List<CKeyValuePair> serviceAttributeList) {
        ServiceAttributeList = serviceAttributeList;
    }

    public static class Login implements Serializable {

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
