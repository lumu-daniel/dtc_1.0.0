package com.mlt.e200cp.interfaces;

import com.mlt.e200cp.models.EmvTransactionDetails;
import com.mlt.e200cp.models.repository.response.ISOPaymentResponse;

import org.json.JSONException;
import org.json.JSONObject;

public interface ServiceCallback {
    String onResponseSuccess(EmvTransactionDetails data, ISOPaymentResponse isoPaymentResponse);
    String onResponseFailure(String t);
}
