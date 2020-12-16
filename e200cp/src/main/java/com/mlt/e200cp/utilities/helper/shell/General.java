package com.mlt.e200cp.utilities.helper.shell;

import com.mlt.e200cp.utilities.helper.protocol.VNG;

public class General extends BaseShell {

    public static class FirmwareInfo {
        public String model = "FFFFFFFFFFFFFFFF";
        public String version = "00000000";
        public String build = "00000000";
        public String serialNumber = "0000000000000000";
        public String cid = "0000";

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("{")
                    .append("\n\tModel   : " + model)
                    .append("\n\tVersion : " + version)
                    .append("\n\tBuild   : " + build)
                    .append("\n\tSN      : " + serialNumber)
                    .append("\n\tCID     : " + cid)
                    .append("\n}");
            return sb.toString();
        }
    }

    public FirmwareInfo getFirmwareInfo(int target) {

        FirmwareInfo firmwareInfo = new FirmwareInfo();

        VNG rsp = new VNG(portManager.exchangeData(target, "R0".getBytes()));

        // R0{Model}{FWVer}{Build#}{SN}{CID}
        /*
            Model Name: 16 bytes ASCII hexadecimal data.
            FW Version: 8 bytes ASCII hexadecimal data
            Build #: 8 bytes ASCII hexadecimal data
            Serial Number = 16 bytes of ASCII data
            CID: 4 bytes ASCII hexadecimal data
        */
        if (rsp.parseHeader("R0"))
        {
            firmwareInfo.model = rsp.parseString(16);
            firmwareInfo.version = rsp.parseString(8);
            firmwareInfo.build = rsp.parseString(8);
            firmwareInfo.serialNumber = rsp.parseString(16);
            firmwareInfo.cid = rsp.parseString(4);
        }

        return firmwareInfo;
    }

    public FirmwareInfo getFirmwareInfo() {
        return getFirmwareInfo(0);
    }
}
