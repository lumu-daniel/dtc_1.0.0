package com.mlt.e200cp.utilities.FetchDeviceSerialNumber;


import com.mlt.e200cp.utilities.helper.protocol.VNG;

public class FetchSerialNumber extends BaseWrapperSerial {

    public String getSerialNumber() {

        super.initCommPort();

        String serialNo = "";

        commPort.connect();
        VNG rsp = exchangeData(new VNG("R0"));
        if (rsp != null) {
            if (rsp.parseString(2).equals("R0")) {

                String serial = rsp.parseString(48);
                serialNo = serial.substring(serial.indexOf("E"));
            }
        }
        commPort.disconnect();

        return serialNo;
    }
}
