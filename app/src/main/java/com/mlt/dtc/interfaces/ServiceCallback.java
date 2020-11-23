package com.mlt.dtc.interfaces;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by talal on 3/26/2018.
 */

public interface ServiceCallback {
    void onSuccess(JSONObject obj) throws JSONException;
    void onFailure(String obj);
}
