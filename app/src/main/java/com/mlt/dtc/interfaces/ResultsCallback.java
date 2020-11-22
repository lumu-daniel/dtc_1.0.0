package com.mlt.dtc.interfaces;

public
interface ResultsCallback {
    String onResponseSuccess(String data);
    String onResponseFailure(String t);
}
