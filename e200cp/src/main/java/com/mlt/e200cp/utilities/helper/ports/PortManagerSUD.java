package com.mlt.e200cp.utilities.helper.ports;

import android.content.Context;

import saioapi.comm.v2.ComManager;

public class PortManagerSUD extends PortManager {

    public static int [] PORTS_SUD = {
            ComManager.DEVICE_COM3,     // SUD
            ComManager.DEVICE_COM4,     // SPED
            ComManager.DEVICE_COM1,     // P95
            ComManager.DEVICE_COM2,     // C150S
    };

    public static int TARGET_SUD = 0;
    public static int TARGET_SPED = 1;
    public static int TARGET_P95 = 2;
    public static int TARGET_C150S = 3;

    public PortManagerSUD() {
        NUM_OF_PORTS = 3;
        targetName = new String[] { "SUD", "SPED", "P95", "C150S" };
        initListener();
    }

    @Override
    public void initCommPort(Context appCtx) {

        ports = new CommPort[NUM_OF_PORTS];
        ids = new int[NUM_OF_PORTS];

        for(int i = 0 ; i < NUM_OF_PORTS ; i++) {
            ports[i] = new CommPort(appCtx);
            ids[i] = PORTS_SUD[i];
        }
    }

    @Override
    public boolean connect() {
        boolean ret = true;
        for(int i = 0 ; i < NUM_OF_PORTS ; i++) {
            if (ports[i].connect(PORTS_SUD[i])) {
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

        for(int i = 0 ; i < NUM_OF_PORTS ; i++)
            if (!ports[i].isConnected())
                return false;

        return true;
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
            case "QR":
                sendTarget = TARGET_C150S;
                break;
            case "EP":
                sendTarget = TARGET_SPED;
                break;
            case "YM":
                sendTarget = TARGET_P95;
                break;
            case "CR":
            case "CM":
            case "RS":
            case "QZ":
                sendTarget = TARGET_SUD;
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
