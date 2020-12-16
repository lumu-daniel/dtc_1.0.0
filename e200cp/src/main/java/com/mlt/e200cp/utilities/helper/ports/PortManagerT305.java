package com.mlt.e200cp.utilities.helper.ports;

import android.content.Context;

import saioapi.comm.v2.ComManager;

public class PortManagerT305 extends PortManager {

    // PortManagerT305 + EPP
    public static int [] PORTS_T305_P95 = { ComManager.DEVICE_COM3 , ComManager.DEVICE_COM2 };
    public static int TARGET_T305 = 0;
    public static int TARGET_P95 = 1;

    public PortManagerT305() {
        NUM_OF_PORTS = 2;
        targetName = new String[] { "T305", "P95" };
        initListener();
    }

    @Override
    public void initCommPort(Context appCtx) {

        ports = new CommPort[NUM_OF_PORTS];
        ids = new int[NUM_OF_PORTS];

        for(int i = 0 ; i < NUM_OF_PORTS ; i++) {
            ports[i] = new CommPort(appCtx);
            ids[i] = PORTS_T305_P95[i];
        }
    }

    @Override
    public boolean connect() {
        boolean ret = true;
        for(int i = 0 ; i < NUM_OF_PORTS ; i++) {
            if (ports[i].connect(PORTS_T305_P95[i])) {
                ports[i].setOnEvent(portListener[i]);
            } else {
                ret = false;
                break;
            }
        }
        if (!ret) disconnect();
        return ret;
    }

    @Override
    public boolean isConnected() {
        return (ports[0].isConnected() && ports[1].isConnected());
    }

    @Override
    public void disconnect() {
        for(int i = 0 ; i < NUM_OF_PORTS ; i++) {
            ports[i].disconnect();
        }
    }

    private void autoDispatchCommand(String cmdIns) {

        if (cmdIns.startsWith("Q")) {   // QM, QS, Q1, Q4-6
            sendTarget = TARGET_P95;
        }

        switch (cmdIns) {
            case "CR":
            case "CM":
            case "RS":
            case "QZ":
            case "QR":
            case "EP":
                sendTarget = TARGET_T305;
                break;
            case "YM":
                sendTarget = TARGET_P95;
                break;
        }

    }

    @Override
    public boolean sendData(byte [] data, int length) {

        if (length >= 2) {
            autoDispatchCommand("" + (char)data[0] + (char)data[1]);
        }

        return super.sendData(data, length);
    }

}
