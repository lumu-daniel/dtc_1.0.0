package com.mlt.e200cp.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PosDetailsCopy implements Serializable {

    @SerializedName("transactionreferencenumber")
    private String transactionreferencenumber;
    @SerializedName("transactiontype")
    private String transactiontype;
    @SerializedName("merchantid")
    private String merchantid;
    @SerializedName("merchantname")
    private String merchantname;
    @SerializedName("transactioncustomername")
    private String transactioncustomername;
    @SerializedName("amount")
    private String amount;
    @SerializedName("serviceid")
    private String serviceid;
    @SerializedName("servicenameeng")
    private String servicenameeng;
    @SerializedName("servicenamearabic")
    private String servicenamearabic;
    @SerializedName("sourceapplication")
    private String sourceapplication;
    @SerializedName("username")
    private String username;
    @SerializedName("hostdevicename")
    private String hostdevicename;
    @SerializedName("executecommand")
    private String executecommand;
    @SerializedName("transactionchild")
    private String transactionchild;
    @SerializedName("postype")
    private String postype;
    @SerializedName("poscom")
    private String poscom;
    @SerializedName("receiptnumber")
    private Object receiptnumber;
    @SerializedName("marchineid")
    private String marchineid;
    @SerializedName("requestingapplicationid")
    private String requestingapplicationid;
    @SerializedName("isloggingpossible")
    private Boolean isloggingpossible;
    @SerializedName("customeraddress")
    private String customeraddress;
    @SerializedName("customercity")
    private String customercity;
    @SerializedName("customercontactnumber")
    private String customercontactnumber;
    @SerializedName("customercountry")
    private String customercountry;
    @SerializedName("customeremail")
    private String customeremail;
    @SerializedName("language")
    private String language;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @SerializedName("transactionreferencenumber")
    public String getTransactionreferencenumber() {
        return transactionreferencenumber.toUpperCase();
    }

    @SerializedName("transactionreferencenumber")
    public void setTransactionreferencenumber(String transactionreferencenumber) {
        this.transactionreferencenumber = transactionreferencenumber;
    }

    @SerializedName("transactiontype")
    public String getTransactiontype() {
        return transactiontype;
    }

    @SerializedName("transactiontype")
    public void setTransactiontype(String transactiontype) {
        this.transactiontype = transactiontype;
    }

    @SerializedName("merchantid")
    public String getMerchantid() {
        return merchantid;
    }

    @SerializedName("merchantid")
    public void setMerchantid(String merchantid) {
        this.merchantid = merchantid;
    }

    @SerializedName("merchantname")
    public String getMerchantname() {
        return merchantname;
    }

    @SerializedName("merchantname")
    public void setMerchantname(String merchantname) {
        this.merchantname = merchantname;
    }

    @SerializedName("transactioncustomername")
    public String getTransactioncustomername() {
        return transactioncustomername;
    }

    @SerializedName("transactioncustomername")
    public void setTransactioncustomername(String transactioncustomername) {
        this.transactioncustomername = transactioncustomername;
    }

    @SerializedName("amount")
    public String getAmount() {
        return amount;
    }

    @SerializedName("amount")
    public void setAmount(String amount) {
        this.amount = amount;
    }

    @SerializedName("serviceid")
    public String getServiceid() {
        return serviceid;
    }

    @SerializedName("serviceid")
    public void setServiceid(String serviceid) {
        this.serviceid = serviceid;
    }

    @SerializedName("servicenameeng")
    public String getServicenameeng() {
        return servicenameeng;
    }

    @SerializedName("servicenameeng")
    public void setServicenameeng(String servicenameeng) {
        this.servicenameeng = servicenameeng;
    }

    @SerializedName("servicenamearabic")
    public String getServicenamearabic() {
        return servicenamearabic;
    }

    @SerializedName("servicenamearabic")
    public void setServicenamearabic(String servicenamearabic) {
        this.servicenamearabic = servicenamearabic;
    }

    @SerializedName("sourceapplication")
    public String getSourceapplication() {
        return sourceapplication;
    }

    @SerializedName("sourceapplication")
    public void setSourceapplication(String sourceapplication) {
        this.sourceapplication = sourceapplication;
    }

    @SerializedName("username")
    public String getUsername() {
        return username;
    }

    @SerializedName("username")
    public void setUsername(String username) {
        this.username = username;
    }

    @SerializedName("hostdevicename")
    public String getHostdevicename() {
        return hostdevicename;
    }

    @SerializedName("hostdevicename")
    public void setHostdevicename(String hostdevicename) {
        this.hostdevicename = hostdevicename;
    }

    @SerializedName("executecommand")
    public String getExecutecommand() {
        return executecommand;
    }

    @SerializedName("executecommand")
    public void setExecutecommand(String executecommand) {
        this.executecommand = executecommand;
    }

    @SerializedName("transactionchild")
    public String getTransactionchild() {
        return transactionchild;
    }

    @SerializedName("transactionchild")
    public void setTransactionchild(String transactionchild) {
        this.transactionchild = transactionchild;
    }

    @SerializedName("postype")
    public String getPostype() {
        return postype;
    }

    @SerializedName("postype")
    public void setPostype(String postype) {
        this.postype = postype;
    }

    @SerializedName("poscom")
    public String getPoscom() {
        return poscom;
    }

    @SerializedName("poscom")
    public void setPoscom(String poscom) {
        this.poscom = poscom;
    }

    @SerializedName("receiptnumber")
    public Object getReceiptnumber() {
        return receiptnumber;
    }

    @SerializedName("receiptnumber")
    public void setReceiptnumber(Object receiptnumber) {
        this.receiptnumber = receiptnumber;
    }

    @SerializedName("marchineid")
    public String getMarchineid() {
        return marchineid;
    }

    @SerializedName("marchineid")
    public void setMarchineid(String marchineid) {
        this.marchineid = marchineid;
    }

    @SerializedName("requestingapplicationid")
    public String getRequestingapplicationid() {
        return requestingapplicationid;
    }

    @SerializedName("requestingapplicationid")
    public void setRequestingapplicationid(String requestingapplicationid) {
        this.requestingapplicationid = requestingapplicationid;
    }

    @SerializedName("isloggingpossible")
    public Boolean getIsloggingpossible() {
        return isloggingpossible;
    }

    @SerializedName("isloggingpossible")
    public void setIsloggingpossible(Boolean isloggingpossible) {
        this.isloggingpossible = isloggingpossible;
    }

    @SerializedName("customeraddress")
    public String getCustomeraddress() {
        return customeraddress;
    }

    @SerializedName("customeraddress")
    public void setCustomeraddress(String customeraddress) {
        this.customeraddress = customeraddress;
    }

    @SerializedName("customercity")
    public String getCustomercity() {
        return customercity;
    }

    @SerializedName("customercity")
    public void setCustomercity(String customercity) {
        this.customercity = customercity;
    }

    @SerializedName("customercontactnumber")
    public String getCustomercontactnumber() {
        return customercontactnumber;
    }

    @SerializedName("customercontactnumber")
    public void setCustomercontactnumber(String customercontactnumber) {
        this.customercontactnumber = customercontactnumber;
    }

    @SerializedName("customercountry")
    public String getCustomercountry() {
        return customercountry;
    }

    @SerializedName("customercountry")
    public void setCustomercountry(String customercountry) {
        this.customercountry = customercountry;
    }

    @SerializedName("customeremail")
    public String getCustomeremail() {
        return customeremail;
    }

    @SerializedName("customeremail")
    public void setCustomeremail(String customeremail) {
        this.customeremail = customeremail;
    }

    @SerializedName("language")
    public String getLanguage() {
        return language;
    }

    @SerializedName("language")
    public void setLanguage(String language) {
        this.language = language;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}