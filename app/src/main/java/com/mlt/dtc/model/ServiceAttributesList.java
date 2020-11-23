package com.mlt.dtc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ServiceAttributesList {

@SerializedName("xmlns:a")
@Expose
private String xmlnsA;
@SerializedName("xmlns:i")
@Expose
private String xmlnsI;
@SerializedName("a:CKeyValuePair")
@Expose
private List<CKeyValuePair> aCKeyValuePair = null;

public String getXmlnsA() {
return xmlnsA;
}

public void setXmlnsA(String xmlnsA) {
this.xmlnsA = xmlnsA;
}

public String getXmlnsI() {
return xmlnsI;
}

public void setXmlnsI(String xmlnsI) {
this.xmlnsI = xmlnsI;
}

public List<CKeyValuePair> getACKeyValuePair() {
return aCKeyValuePair;
}

public void setACKeyValuePair(List<CKeyValuePair> aCKeyValuePair) {
this.aCKeyValuePair = aCKeyValuePair;
}

}