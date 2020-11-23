package com.mlt.dtc.interfaces;

import org.json.JSONException;
import org.json.JSONObject;

public
interface ServiceCallback {
    void onSuccess(JSONObject obj) throws JSONException;
    void onFailure(String obj);
}
