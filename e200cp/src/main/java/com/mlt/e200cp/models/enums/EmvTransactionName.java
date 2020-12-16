package com.mlt.e200cp.models.enums;

public enum EmvTransactionName {
    VOID_PURCHASE_TRANSACTION("VOID PURCHASE"),
    REFUND_TRANSACTION("REFUND"),
    VOID_REFUND_TRANSACTION("VOID REFUND"),
    PURCHASE_TRANSACTION("PURCHASE"),
    RE_PRINT_RCPT("RE PRINT RECIEPT");

    public String label;
    private EmvTransactionName(String labels){
        this.label = labels;
    }
}
