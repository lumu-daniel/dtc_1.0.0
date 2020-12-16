package com.mlt.e200cp.utilities.helper.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class DataSync {

    private static final String TAG = "DataSync";

    Queue<byte []> dataQueue = new LinkedList<byte []>();
    public String err = null;

    protected long lastData = 0;

    public void reset() {
        dataQueue.clear();
        err = null;
    }

    protected void cleanExpired() {
        int EXPIRED_TIME = 1000;
        if (!dataQueue.isEmpty() && (System.currentTimeMillis() - lastData > EXPIRED_TIME))
            dataQueue.clear();
    }

    public synchronized byte [] waitData(long millis)
    {
        cleanExpired();

        if (!dataQueue.isEmpty()) {
            byte [] ret = dataQueue.poll();
            this.reset();
            return ret;
        }

        try {
            this.wait(millis);
        } catch (InterruptedException e) {
            return null;
        }
        byte [] ret = dataQueue.poll();
        this.reset();
        return ret;
    }

    public synchronized void dataIn(byte [] data) {
        err = null;
        if (data != null) {
            cleanExpired();
            dataQueue.offer(data);
            lastData = System.currentTimeMillis();
        }
        notify();
        return;
    }

    public synchronized void dataIn(byte [] data, int length) {
        if (data != null && data.length >= length) {
            dataIn(Arrays.copyOf(data, length));
        } else {
            notify();
        }
        return;
    }

    public synchronized void setErr(String errMsg) {
        dataQueue.clear();
        err = errMsg;
        notify();
        return;
    }
}
