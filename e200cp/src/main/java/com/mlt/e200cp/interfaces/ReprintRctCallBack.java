package com.mlt.e200cp.interfaces;

import com.mlt.e200cp.models.response.ISOPaymentResponse;

public interface ReprintRctCallBack {
    void onResponseSuccess(ISOPaymentResponse data);
    void onResponseFailure(String data);
}
