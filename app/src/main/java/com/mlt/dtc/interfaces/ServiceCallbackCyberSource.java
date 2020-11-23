package com.mlt.dtc.interfaces;

import com.mlt.dtc.model.response.PaymentResponse;

import org.json.JSONException;



/**
 * Created by raheel on 3/26/2018.
 */

public interface ServiceCallbackCyberSource {
    void onSuccess(PaymentResponse obj) throws JSONException;
    void onFailure(String obj);
}
