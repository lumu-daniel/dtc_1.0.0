package com.mlt.dtc.model;

import com.google.gson.annotations.SerializedName;

public
class ClickObject {
    @SerializedName("TripID")
    private String tripID;
    @SerializedName("buttonName")
    private String buttonName;
    @SerializedName("dateTIme")
    private String dateTIme;
    @SerializedName("driverID")
    private String driverID;
    @SerializedName("eventServiceCode")
    private String eventServiceCode;

    public ClickObject(String tripID, String buttonName, String dateTIme, String driverID, String eventServiceCode) {
        this.tripID = tripID;
        this.buttonName = buttonName;
        this.dateTIme = dateTIme;
        this.driverID = driverID;
        this.eventServiceCode = eventServiceCode;
    }

    @SerializedName("TripID")
    public String getTripID() {
        return tripID;
    }

    @SerializedName("buttonName")
    public String getButtonName() {
        return buttonName;
    }

    @SerializedName("dateTIme")
    public String getDateTIme() {
        return dateTIme;
    }

    @SerializedName("driverID")
    public String getDriverID() {
        return driverID;
    }

    @SerializedName("eventServiceCode")
    public String getEventServiceCode() {
        return eventServiceCode;
    }
}
