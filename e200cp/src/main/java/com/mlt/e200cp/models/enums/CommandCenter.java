package com.mlt.e200cp.models.enums;

public enum CommandCenter {

    //region request
    EID_READ_DATA("EMIRATESIDREADERDATA"),
    INIT_CASH_DEVICE("INITIALIZECASHDEVICE"),
    ACCEPT_CASH("ACCEPTCASH"),
    GET_CASH_DEVICE_STATUS("GETCASHDEVICESTATUS"),
    PRINT_STICKER("STICKERPRINTERJOB"),
    GET_STICKER_PRINTER_STATUS("GETSTICKERPRINTERSTATUS"),
    PRINT_CARD("CARDPRINTERJOB"),
    GET_CARD_PRINTER_STATUS("GETCARDPRINTERSTATUS"),
    CANCEL_ACCEPTANCE("CANCELCASHACCEPTANCE"),
    GET_ALL_DEV_STATUS("GETALLDEVICESTATUS"),
    UPDATE_TRANS_PARAMS("TRANSACTIONUPDATEPARAMETERSSTRING"),
    EID_GET_STATUS("GETEMIRATESIDREADERSTATUS"),
    PC_SHUTDOWN("SHUTDOWN"),
    PC_RESTART("RESTART"),
    GET_NFC_DEV_STATUS("GETNFCDEVICESTATUS"),
    GET_FSE_CARD_DETAILS("GETFSECARDDETAILS"),
    CANCEL_NFC_READER("CANCELNFCREADING"),
    RESET_CASH("CITRESET-CASH-COINS"),
    RESET_CARDS("CITRESET-CARDS-VL"),
    GET_SUMMARY("CITGETSUMMARY"),
    CIT_UPDATE_VLCARDS("CITUPDATE-CARDS-VL"),
    CIT_UPDATE_CASH("CITUPDATE-CASH"),
    PRINTRECEIPT("PRINTRECIEPT"),
    UPDATE_SESSION("UPDATESESSIONVARIABLES"),
    RESPONSE_("UPDATESESSIONVARIABLES"),

    //endRegion


    //region response
    RESPONSE_CASH_DEV_INIT("cashdeviceinit"),
    RESPONSE_CASH_DEV_STATUS("cashdevicestatus"),
    RESPONSE_CASH_DEV("cashdevice"),
    RESPONSE_CANCELLED_ACCEPTANCE("cancelledacceptance"),
    RESPONSE_STICKER_STATUS("stickerprinterstatus"),
    RESPONSE_CANCEL_ACCEPTANCE_FAILED("not cancelled acceptance"),
    RESPONSE_EVOLIS_READY("evolisprinterstatus"),
    RESPONSE_EID_STATUS("emiratesstatus"),
    RESPONSE_ACHIEVED("billcountachieved"),
    RESPONSE_EID_DATA("emiratesdata"),
    RESPONSE_EID_CANCEL("emiratesidreadercancelreading"),
    RESPONSE_CASH_ACCEPTED("billaccepted"),
    RESPONSE_CASH_DISPENSED("billdispensed"),
    RESPONSE_EID_INSERTED("emiratesid-inserted"),
    RESPONSE_EVOLIS_PRINT_JOB("evolisprintjob"),
    RESPONSE_ACCEPTOR_LIGHT("cashacceptorlight"),
    RESPONSE_ALL_DEV_STATUS("getalldevicestatus"),
    RESPONSE_TRANS_LOG("transactionlog"),
    RESPONSE_STICKER_PRINT_JOB("stickerprintjob"),
    RESPONSE_ACK("ack"),
    RESPONSE_NFC_DEV_STATUS("getnfcdevicestatus"),
    RESPONSE_FSE_CARD_DETAILS("getfsecarddetails"),
    RESPONSE_CANCELLED_NFC("cancellednfc"),
    RESPONSE_CITRESETCASH("citresetcash"),
    RESPONSE_CITRESETCARDS("citresetcards"),
    RESPONSE_CITGETSUMMARY("citgetsummary"),
    RESPONSE_CITUPDATE("citupdate"),
    RESPONSE_UPDATESESSIONVARIABLES("updatesessionvariables"),
    RESPONSE_START_TXN("starttransaction"),
    RESPONSE_GET_POS_STATUS("getposstatus"),
    RESPONSE_GET_TERM_DET("getterminaldetails"),

    STATUS_NOT_READY("notready"),
    STATUS_READY("ready"),
    STATUS_TRUE("true");

    public String label;
    private CommandCenter(String label) {
        this.label = label;
    }

}
