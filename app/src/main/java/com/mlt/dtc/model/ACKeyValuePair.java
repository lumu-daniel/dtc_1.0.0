package com.mlt.dtc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ACKeyValuePair {
    @SerializedName("a:Key")
    @Expose
    private String aKey;
    @SerializedName("a:Value")
    @Expose
    private Boolean aValue;



    public String getAKey() {
        return aKey;
    }

    public void setAKey(String aKey) {
        this.aKey = aKey;
    }

    public Boolean getAValue() {
        return aValue;
    }

    public void setAValue(Boolean aValue) {
        this.aValue = aValue;
    }

}
