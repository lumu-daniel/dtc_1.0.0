package com.mlt.e200cp.models.enums;

//Service constants
public enum Constants {
        DEVICE_SN("E2C1000590"),
        MERCHANT_ID("1003967"),
        TERMINAL_ID("80040245"),
        DUMY_APPID("ApplicationID"),
        SERVICE_CODE("101"),
        MERCHANT_NAME("MLTTest"),
        POS_TYPE("SmartScreen"),
        SECURE_HASH_FIELDS("ServiceCode,TerminalID,TimeStamp"),
        LOGIN_ID("MLT"),
        LOGIN_PW("nips@2019"),
        APPLICATION_VERSION("1.0.1"),
        CONTACTLESS_ENTRY_MODE("071"),
        CONTACTLESS_MGSTRIPE("911"),
        MAGSTRIPE_ENTRY_MODE("021"),
        MAGSTRIPE_TECHNICAL_FALLBACK("801"),
        CONTACT_ENTRY_MODE("051"),
        PIN_CVM_TYPE("02"),
        SIGNATURE_CVM_TYPE("01"),
        NO_CVM_TYPE("00"),
        NO_CVM_PRF("05"),
        TXN_CURRENCY("784");
        public final String label;
        private Constants(String label) {
            this.label = label;
        }
    }