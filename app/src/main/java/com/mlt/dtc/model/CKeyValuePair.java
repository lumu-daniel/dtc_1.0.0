package com.mlt.dtc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public
class CKeyValuePair {
    @SerializedName("a:Key")
    @Expose
    private String key;
    @SerializedName("a:Value")
    @Expose
    private String value;

    public CKeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String aKey) {
        this.key = aKey;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String aValue) {
        this.value = aValue;
    }
}
