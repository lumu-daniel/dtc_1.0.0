package com.mlt.e200cp.utilities.FetchDeviceSerialNumber;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.mlt.e200cp.controllers.mainlogiccontrollers.HelperEMVClass;
import com.mlt.e200cp.interfaces.ActivityResultListener;
import com.mlt.e200cp.interfaces.PresenterInterface;
import com.mlt.e200cp.interfaces.ViewInterface;
import com.mlt.e200cp.utilities.helper.ports.PortManager;
import com.mlt.e200cp.utilities.helper.protocol.VNG;
import com.mlt.e200cp.utilities.helper.shell.BaseShell;

import java.util.LinkedHashMap;


public class BasePresenterViewWrapper implements PresenterInterface, ViewInterface {

    protected ViewInterface vi;
    protected Context ctx;
    protected ActivityResultListener onActivityResule;
    protected int errorCount;
    public PortManager portManager;


    public BasePresenterViewWrapper() {}

    @Override
    public void initPresenter(Context context, ViewInterface viewInterface) {
        ctx = context;
        portManager = (PortManager) ULTests.getInstance(ctx).portManager;
        vi = viewInterface;
        presenterImp();
        errorCount = 0;
        refreshActionTable();
    }

    @Override
    public void onDestroy() {
    }

    protected void presenterImp() {
        putAction("Empty", () -> {});
    }

    protected void initCommPort() {
        portManager.initCommPort(ctx);
        BaseShell.portManager = portManager;
    }

    public boolean connect() {
        return portManager.connect();
    }

    public boolean connect(int port) {
        return portManager.connect(port);
    }

    public void disconnect() {
        portManager.disconnect();
    }

    public byte [] waitData(int timeout) {
        byte [] data = portManager.waitData(timeout);

        return data;
    }

    public byte [] waitData() {
        return portManager.waitData(portManager.getCommPort().READ_TIMEOUT);
    }

    public boolean sendData(byte [] data, int length) {

        return portManager.sendData(data, length);
    }

    public boolean sendData(byte [] data) {
        return sendData(data, data.length);
    }

    public byte [] exchangeData(byte [] data) {
        if (sendData(data, data.length)) {
            return waitData();
        }
        return null;
    }

    public byte [] exchangeData(byte [] data, int length) {
        if (sendData(data, length)) {
            return waitData();
        }
        return null;
    }

    public byte [] exchangeData(byte [] data, int length, int timeout) {
        if (sendData(data, length)) {
            return waitData(timeout);
        }
        return null;
    }

    public VNG exchangeData(VNG req) {
        return exchangeData(req, portManager.getCommPort().READ_TIMEOUT);
    }

    public VNG exchangeData(VNG req, int timeout) {
        if (sendData(req.getCmdBuffer())) {
            byte [] rspData = waitData(timeout);
            if (rspData != null)
                return new VNG(rspData);
        }
        return null;
    }

    @Override
    public void setActionTable(String[] actionList) {
        vi.setActionTable(actionList);
    }

    @Override
    public void addLog(String msg) {
        vi.addLog(msg);
    }

    //endregion

    //region action table

    private LinkedHashMap<String, Runnable> actionTable = new LinkedHashMap<>();

    protected void onCancel() {}

    protected boolean beforeInvoke(String methodName) { return true; }

    // deviated class override this function to do pre-processing before call runnable
    // - return
    // --- false to flag this action is handled
    // --- true to let base class call stored function in actionTable
    protected boolean defaultActionHandler(String action) {
        return true;
    }

    protected void afterInvoke(String methodName) {}

    protected void putAction(String action) {
        actionTable.put(action, null);
    }

    protected void putAction(String action, Runnable runnable) {
        actionTable.put(action, runnable);
    }

    private void refreshActionTable() {
        setActionTable(actionTable.keySet().toArray(new String[actionTable.size()]));
    }

    public void refreshActionTable(String[] list) {
        setActionTable(list);
    }

    public Thread procTaskThread = new Thread();
    String lastAction = "idle";

    public void onAction(String action) {

        if (action.equals("CANCEL")) {
            onCancel();
            return;
        }

        if (!procTaskThread.isAlive()) {
            procTaskThread = new Thread(() ->{

                lastAction = action;

                if (beforeInvoke(action))
                {
                    if (defaultActionHandler(action)) {
                        if (actionTable.containsKey(action) && (actionTable.get(action) != null))
                            actionTable.get(action).run();
                    }
                }

                afterInvoke(action);

                lastAction = "idle";
            });
            procTaskThread.start();
        }
        else
            addLog("Previous Action [" + lastAction + "] is in processing");
    }

    static ProgressDialog processDialog = null;


    public void showProcessing(String msg, Runnable runnable) {

        showProcessing(msg);

        if (runnable != null) {
            runnable.run();
            endProcessing();
        }
    }

    public void showProcessing(String msg) {
        HelperEMVClass.appCompatActivity.runOnUiThread(() -> {
            endProcessing();
            processDialog = new ProgressDialog(HelperEMVClass.appCompatActivity);
            processDialog.setMessage(msg);
            processDialog.show();
        });

    }
    public void endProcessing() {
        HelperEMVClass.appCompatActivity.runOnUiThread(() -> {
            if (processDialog != null) {
                if (processDialog.isShowing())
                    processDialog.cancel();
                processDialog = null;
            }
        });

    }

    //endregion
}
