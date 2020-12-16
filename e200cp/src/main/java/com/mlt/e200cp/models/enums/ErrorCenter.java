package com.mlt.e200cp.models.enums;

public enum ErrorCenter {
    CANCEL_TXN("Operation Cancelled..."),
    NULL(""),
    CARD_ERR("Device error, please retry."),
    TXN_FLOW_ERR("Transaction Flow error..."),
    MSR_ERR("Error in magnetic stripe reading"),
    NOT_VER_ERR("Error Transaction not verified..."),
    CRD_UNSPTD_ERR("Card not supported or Operation cancelled..."),
    TIME_OUT_ERR("Timed out."),
    USE_CHIP_ERR("Card not supported, please use chip."),
    CARD_REMOVED_ERR("Card removed."),
    EMPTY_PIN_ERR("Pin error."),
    CRD_ERR("Card error please try again."),
    PIN_TIME_OUT_ERR("Pin timed out."),
    PIN_ERR("Pin error.");
    public final String label;
    private ErrorCenter(String label) {
        this.label = label;
    }
}
