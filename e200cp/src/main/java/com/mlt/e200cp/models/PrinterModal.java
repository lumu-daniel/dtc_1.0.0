package com.mlt.e200cp.models;

public class PrinterModal {

    String ReceiptType, FSECardNumber, SessionId, KioskId, KioskLocation,UserId,denomination,numNotes,barcode;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public String getNumNotes() {
        return numNotes;
    }

    public void setNumNotes(String numNotes) {
        this.numNotes = numNotes;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getReceiptType() {
        return ReceiptType;
    }

    public void setReceiptType(String receiptType) {
        ReceiptType = receiptType;
    }

    public String getFSECardNumber() {
        return FSECardNumber;
    }

    public void setFSECardNumber(String FSECardNumber) {
        this.FSECardNumber = FSECardNumber;
    }

    public String getSessionId() {
        return SessionId;
    }

    public void setSessionId(String sessionId) {
        SessionId = sessionId;
    }

    public String getKioskId() {
        return KioskId;
    }

    public void setKioskId(String kioskId) {
        KioskId = kioskId;
    }

    public String getKioskLocation() {
        return KioskLocation;
    }

    public void setKioskLocation(String kioskLocation) {
        KioskLocation = kioskLocation;
    }
}