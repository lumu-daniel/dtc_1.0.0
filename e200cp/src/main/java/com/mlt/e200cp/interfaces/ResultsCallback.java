package com.mlt.e200cp.interfaces;

public interface ResultsCallback {
    String onResponseSuccess(String data);
    String onResponseFailure(String t);
}
