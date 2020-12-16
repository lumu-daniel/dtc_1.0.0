package com.mlt.e200cp.interfaces;

public interface SerialEIDReadCallback {
    void success(String details);
    void failure(String details);
    void EIDInserted(String details);
}
