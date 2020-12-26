package com.mlt.e200cp.interfaces;

import com.mlt.e200cp.models.repository.response.ISOPaymentResponse;

public interface TransactionDoneCallback {
    void onSuccess(ISOPaymentResponse response);
    void onfailure(String error);
}
