package com.mlt.e200cp.utilities.helper.listener;


import com.mlt.e200cp.utilities.helper.protocol.EMV;

public interface EmvCallback {
    boolean run(EMV callback);
}
