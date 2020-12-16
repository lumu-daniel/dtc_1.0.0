package com.mlt.e200cp.interfaces;

public interface ChipCardCallBack {
    void onResponseSuccess(String data);
    void onCardInserted(String data);
    void onResponseFailure(String t);
}
