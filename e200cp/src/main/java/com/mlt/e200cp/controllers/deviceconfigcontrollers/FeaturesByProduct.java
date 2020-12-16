package com.mlt.e200cp.controllers.deviceconfigcontrollers;
import android.content.Context;

import com.mlt.e200cp.utilities.FetchDeviceSerialNumber.ULTests;
import com.mlt.e200cp.utilities.helper.ports.PortManager;
import com.mlt.e200cp.utilities.helper.ports.PortManagerKLD;
import com.mlt.e200cp.utilities.helper.ports.PortManagerSUD;
import com.mlt.e200cp.utilities.helper.ports.PortManagerT305;
import com.mlt.e200cp.utilities.helper.shell.Pinpad;


public class FeaturesByProduct {

    public static String product = "";

    public static void initRelatedObjects(Context context) {

        if (product.equals("T305")) {
            ULTests.getInstance(context).portManager = new PortManagerT305();
        } else if (product.equals("SUD12"))
            ULTests.getInstance(context).portManager = new PortManagerSUD();
        else if (product.contains("KLD"))
            ULTests.getInstance(context).portManager = new PortManagerKLD();
        else
            ULTests.getInstance(context).portManager = new PortManager();

        if (product.contains("AT-150") ||
                product.contains("AT-170") ||
                product.contains("xCL_E200CP"))
            Pinpad.isVirtualPinpad = true;
        else
            Pinpad.isVirtualPinpad = false;
    }
}
