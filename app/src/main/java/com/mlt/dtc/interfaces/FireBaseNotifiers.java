package com.mlt.dtc.interfaces;

import java.util.LinkedHashMap;

public interface FireBaseNotifiers {
    void onMessage(LinkedHashMap<String, String> linkedHashMap);
}
