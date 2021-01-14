package com.mlt.e200cp.utilities.helper.ports;

import android.content.Context;
import android.os.AsyncTask;

import com.mlt.e200cp.controllers.backgroundcontrollers.SequencyHandler;
import com.mlt.e200cp.controllers.mainlogiccontrollers.HelperEMVClass;
import com.mlt.e200cp.utilities.helper.protocol.VNG;
import com.mlt.e200cp.utilities.helper.protocol.VngDef;
import com.mlt.e200cp.utilities.helper.util.DataSync;
import com.mlt.e200cp.utilities.helper.util.Utility;

import static com.mlt.e200cp.models.StringConstants.TXN_ERROR;
import static com.mlt.e200cp.utilities.helper.util.Logger.LINE_OUT;
import static com.mlt.e200cp.utilities.helper.util.Logger.log;

public class PortManager {

    public static int TARGET_EPP = 0;

    public static boolean debug = true;

    public int NUM_OF_PORTS = 2;

    public CommPort ports[] = null;
    public int ids[] = null;
    public int sendTarget = 0;
    public String targetName[] = new String[] { "", "" };

    public int lastReply = 0;

    public DataSync mDataSync = new DataSync();

    public interface UILog {
        void onMsg(String name, String msg);
    }
    public UILog uiLog = null;

    public PortManager() {
        NUM_OF_PORTS = 1;
        targetName = new String[] { "EPP" };
        initListener();
    }

    public boolean connect() {
        if(ports[0].isConnected()){
            log("connected already.",LINE_OUT());
            return true;
        }else{
            boolean ret = ports[0].connect();
            if (ret)
                ports[0].setOnEvent(portListener[0]);
            else
                HelperEMVClass.helperEMVClass.endTransaction("Restart Activity");
            log(String.valueOf(ret),LINE_OUT());
            return ret;
        }
    }

    public boolean connect(int port) {
        boolean ret = ports[0].connect(port);
        if (ret)
            ports[0].setOnEvent(portListener[0]);
        return ret;
    }

    public boolean isConnected() {
        return ports[0].isConnected();
    }

    public void disconnect() {
        ports[0].setOnEvent(null);
        ports[0].disconnect();
    }

    public void initCommPort(Context appCtx) {

        NUM_OF_PORTS = 1;

        ports = new CommPort[NUM_OF_PORTS];
        ports[0] = new CommPort(appCtx);
    }

    public CommPort getCommPort() {
        return ports[sendTarget];
    }

    public void purge() {
        mDataSync.reset();
    }

    public interface Event {
        void onEvent(CommPort.ERROR err, int replyTarget, byte[] data, int len);
    }

    private Event appListener = (err, replyTarget, data, len) -> {
        lastReply = replyTarget;
        switch (err) {
            case CONNECTED:
                mDataSync.setErr("Device : " + targetName[replyTarget] + " Connected");
                break;
            case DISCONNECTED:
                mDataSync.setErr("Device : " + targetName[replyTarget] + " Disconnected");
                break;
            case DATA_READY:

                if (debug)
                    log("<- " + targetName[replyTarget] + " : " + Utility.bytes2Hex(data, len), LINE_OUT());

                if (uiLog != null)
                    uiLog.onMsg(targetName[replyTarget] + " -> ", Utility.bytes2Hex(data, len));

                // ignore ST change notification in flow
                if (len >= 4 && data[0] == (byte)'S' && data[1] == (byte)'T') {
                    log("From State [" + VngDef.ST(data[2]) + "] to [" + VngDef.ST(data[3]) + "]", LINE_OUT());
                } else {
                    mDataSync.dataIn(data, len);
                }

                break;
            case OTHERS:
            default:
                mDataSync.setErr("Unknown Error");
        }

    };

    public void setListener(Event onEvent) {
        appListener = onEvent;
    }

    public void initListener() {

        portListener = new CommPort.Event[NUM_OF_PORTS];

        for(int i = 0 ; i < NUM_OF_PORTS ; i++) {
            final int target = i;
            portListener[i] = (err, data, len) -> {
                appListener.onEvent(err, target, data, len);
            };
        }
    }

    public CommPort.Event [] portListener = null;

    public VNG waitVngData(int timeout) {
        byte [] rsp = mDataSync.waitData(timeout);
        return new VNG(rsp);
    }
    public byte [] waitData(int timeout) {
        return mDataSync.waitData(timeout);
    }

    public byte [] waitData() {
        return waitData(ports[sendTarget].READ_TIMEOUT);
    }

    public boolean sendData(byte [] data, int length) {
        return sendData(sendTarget, data, length);
    }

    public boolean sendData(byte [] data) {
        return sendData(data, data.length);
    }

    public boolean sendData(int target, byte [] data) { return sendData(target, data, data.length); }

    public boolean sendData(int target, byte [] data, int length) {

        sendTarget = target;

        if (debug)
            log("-> " + targetName[target] + " : " + Utility.bytes2Hex(data, length), LINE_OUT());

        if (uiLog != null)
            uiLog.onMsg(targetName[target] + " <- ", Utility.bytes2Hex(data, length));

        boolean ret = ports[target].sendData(data, length);

//        if (length >= 2 && data[0] == (byte)'7' && data[1] == (byte)'2')    // 72 cancel also unblock waiting
//            mDataSync.setErr("Cancel");

        return ret;
    }

    public byte [] exchangeData(byte [] data) { return exchangeData(data, data.length, ports[sendTarget].READ_TIMEOUT); }

    public byte [] exchangeData(byte [] data, int length) { return exchangeData(data, length, ports[sendTarget].READ_TIMEOUT); }

    public byte [] exchangeData(byte [] data, int length, int timeout) {

        if (sendData(data, length)) {
            return waitData(timeout);
        }
        return null;
    }

    public byte [] exchangeData(int target, byte [] data) {
        return exchangeData(target, data, data.length, ports[target].READ_TIMEOUT);
    }

    public byte [] exchangeData(int target, byte [] data, int length) {
        return exchangeData(target, data, length, ports[target].READ_TIMEOUT);
    }

    public byte [] exchangeData(int target, byte [] data, int length, int timeout) {
        sendTarget = target;
        if (sendData(target, data, length)) {
            return waitData(timeout);
        }
        return null;
    }

    public VNG exchangeData(VNG req) { return exchangeData(req, ports[sendTarget].READ_TIMEOUT); }

    public VNG exchangeData(VNG req, int timeout) {
        byte [] buf = req.getCmdBuffer();
        byte [] rsp = exchangeData(buf, buf.length, timeout);
        return new VNG(rsp);
    }

    public VNG exchangeData(int target, VNG req, int timeout) {
        byte [] buf = req.getCmdBuffer();
        byte [] rspData = exchangeData(target, buf, buf.length, timeout);

        if (rspData != null)
            return new VNG(rspData);

        return new VNG();
    }
}
