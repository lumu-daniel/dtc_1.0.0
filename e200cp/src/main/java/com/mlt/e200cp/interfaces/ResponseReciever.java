package com.mlt.e200cp.interfaces;

import com.mlt.e200cp.models.repository.response.ISOPaymentResponse;

public interface ResponseReciever {
    void onResponseRecieved(ISOPaymentResponse isoPaymentResponse);
}
