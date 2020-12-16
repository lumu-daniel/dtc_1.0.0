package com.mlt.e200cp.models.enums;

public enum TxnState {

    ENTER_CARD("Enter card displayed"),
    CARD_INSERTED("Card Inserted"),
    PIN_ENTERED("PIN Entered"),
    PROC_ONLINE("Online Processing"),
    GPRS_CONN("GPRS connection"),
    RESP_FRM_HST("Response received from Host"),
    RMV_CRD_DSP("Remove card displayed"),
    PRINT_RCT("Printing of receipt"),
    PRINT_RCT_FLD("Printing of receipt failed"),
    CRD_RMVD("Card removed"),
    CRD_NO_RMVD("Card not removed after 20 seconds");
    public final String label;
    private TxnState(String label) {
        this.label = label;
    }
}
