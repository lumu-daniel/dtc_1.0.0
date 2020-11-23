package com.mlt.dtc.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mlt.dtc.model.CustomerUniqueNo;
import com.mlt.dtc.model.ServiceAttributesList;
import java.io.Serializable;

public
class UserLoginDetailsResponse implements Serializable {

    @SerializedName("xmlns")
    @Expose
    private String xmlns;
    @SerializedName("CustomerUniqueNo")
    @Expose
    private CustomerUniqueNo customerUniqueNo;
    @SerializedName("ServiceAttributesList")
    @Expose
    private ServiceAttributesList serviceAttributesList;

    public String getXmlns() {
        return xmlns;
    }

    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }

    public CustomerUniqueNo getCustomerUniqueNo() {
        return customerUniqueNo;
    }

    public void setCustomerUniqueNo(CustomerUniqueNo customerUniqueNo) {
        this.customerUniqueNo = customerUniqueNo;
    }

    public ServiceAttributesList getServiceAttributesList() {
        return serviceAttributesList;
    }

    public void setServiceAttributesList(ServiceAttributesList serviceAttributesList) {
        this.serviceAttributesList = serviceAttributesList;
    }
}
