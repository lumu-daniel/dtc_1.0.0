package com.mlt.e200cp.interfaces;

import com.mlt.e200cp.models.EmvTransactionDetails;

public interface ReversalCallBack {
    void onTxnReversed(EmvTransactionDetails details);
    void onReverseFailed(String error);
}
