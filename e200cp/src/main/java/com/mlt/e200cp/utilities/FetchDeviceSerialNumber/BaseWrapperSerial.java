package com.mlt.e200cp.utilities.FetchDeviceSerialNumber;

import android.content.Context;
import android.util.Log;

import com.mlt.e200cp.utilities.helper.ports.CommPort;
import com.mlt.e200cp.utilities.helper.protocol.VNG;
import com.mlt.e200cp.utilities.helper.util.DataSync;
import com.mlt.e200cp.utilities.helper.util.Utility;

import java.util.LinkedHashMap;


/**
 * Created by simon_chen on 2018/2/14.
 */

public class BaseWrapperSerial {


    protected Context ctx;



    //region CommPort R/W IO

    protected CommPort commPort;
    protected DataSync mDataSync = new DataSync();
    boolean logCmd = true;

    protected void initCommPort() {

        commPort = new CommPort(ctx);
        commPort.setOnEvent((err, data, len) -> {
            switch(err){
                case CONNECTED:
//                    vi.addLog("Connected");
                    break;
                case DISCONNECTED:
//                    vi.addLog("Disconnected !!!");
                    mDataSync.setErr("Device Disconnected");
                    break;
                case DATA_READY:
                    mDataSync.dataIn(data, len);
                    break;
                default:
                    break;
            }
        });
    }

    public byte [] waitData(int timeout) {

        byte [] data = mDataSync.waitData(timeout);

        if (logCmd && data != null) {
            String log = "RSP-> " + Utility.bytes2Hex(data);
            Log.v(this.getClass().getName(), log);
            //vi.addLog(log);
        }
        return data;
    }

    public byte [] waitData() {
        return waitData(commPort.READ_TIMEOUT);
    }

    public boolean sendData(byte [] data, int length) {

        if (logCmd) {
            String log = "CMD-> " + Utility.bytes2Hex(data, length);
            //vi.addLog(log);
            Log.v(this.getClass().getName(), log);
        }

        return commPort.sendData(data, length);
    }

    public boolean sendData(byte [] data) {
        return sendData(data, data.length);
    }

    public byte [] exchangeData(byte [] data) { return exchangeData(data, data.length, commPort.READ_TIMEOUT); }

    public byte [] exchangeData(byte [] data, int length) { return exchangeData(data, length, commPort.READ_TIMEOUT); }

    public byte [] exchangeData(byte [] data, int length, int timeout) {
        if (sendData(data, length)) {
            return waitData(timeout);
        }
        return null;
    }

    public VNG exchangeData(VNG req) { return exchangeData(req, commPort.READ_TIMEOUT); }

    public VNG exchangeData(VNG req, int timeout) {
        if (sendData(req.getCmdBuffer())) {
            byte [] rspData = waitData(timeout);
            if (rspData != null)
                return new VNG(rspData);
        }
        return null;
    }

    //endregion

    //region bypass to ViewInterface

    //endregion

    //region action table

    private LinkedHashMap<String, Runnable> actionTable = new LinkedHashMap<>();

    protected void beforeInvoke(String methodName) {}

    protected void afterInvoke(String methodName) {}

    protected void putAction(String action, Runnable runnable) {
        actionTable.put(action, runnable);
    }


    Thread procTaskThread = new Thread();

    protected void onAction(String action) {

        if (!procTaskThread.isAlive()) {
            procTaskThread = new Thread(() ->{
                beforeInvoke(action);
                actionTable.get(action).run();
                afterInvoke(action);
            });
            procTaskThread.start();
        }
        else{
//            addLog("Action [" + action + "] is in processing");
    }

    //endregion

    //region Activity Event Dispath

    //endregion
}
}
