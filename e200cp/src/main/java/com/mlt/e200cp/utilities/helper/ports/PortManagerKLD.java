package com.mlt.e200cp.utilities.helper.ports;

import android.content.Context;

import saioapi.comm.v2.ComManager;

public class PortManagerKLD extends PortManager  {

    public static int [] PORTS_KLD_TARGET = { ComManager.DEVICE_COM3 , ComManager.DEVICE_COM2 };
    public static int TARGET_KLD = 0;
    public static int TARGET_TARGET = 1;


    public PortManagerKLD() {
        NUM_OF_PORTS = 2;
        targetName = new String[] { "KLD", "TARGET" };
        initListener();
    }

    @Override
    public void initCommPort(Context appCtx) {

        ports = new CommPort[NUM_OF_PORTS];
        ids = new int[NUM_OF_PORTS];

        for(int i = 0 ; i < NUM_OF_PORTS ; i++) {
            ports[i] = new CommPort(appCtx);
            ids[i] = PORTS_KLD_TARGET[i];
        }
    }

    @Override
    public boolean connect() {
        boolean ret = true;
        for(int i = 0 ; i < NUM_OF_PORTS ; i++) {
            if (ports[i].connect(PORTS_KLD_TARGET[i])) {
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
}
