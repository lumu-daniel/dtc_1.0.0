package com.mlt.dtc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClickLogServiceResponseObject {
    @SerializedName("xmlns")
    @Expose
    private String xmlns;
    @SerializedName("CustomerUniqueNo")
    @Expose
    private CustomerUniqueNoObject customerUniqueNo;
    @SerializedName("ServiceAttributesList")
    @Expose
    private ServiceAttributesList serviceAttributesList;

    public String getXmlns() {
        return xmlns;
    }

    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }

    public CustomerUniqueNoObject getCustomerUniqueNo() {
        return customerUniqueNo;
    }

    public void setCustomerUniqueNo(CustomerUniqueNoObject customerUniqueNo) {
        this.customerUniqueNo = customerUniqueNo;
    }

    public ServiceAttributesList getServiceAttributesList() {
        return serviceAttributesList;
    }

    public void setServiceAttributesList(ServiceAttributesList serviceAttributesList) {
        this.serviceAttributesList = serviceAttributesList;
    }

}

