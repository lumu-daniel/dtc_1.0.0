package com.mlt.e200cp.utilities.helper.util;

import android.os.Handler;
import android.os.Looper;

import saioapi.util.Sys;

import static com.mlt.e200cp.controllers.mainlogiccontrollers.HelperEMVClass.tv_pin;


public class PinpadManager {

    private Handler handler;
    private boolean virtualPinpad = false;
    private Object waitPopupDialog = new Object();
    private enum STATE { IDLE, WAIT_GUI, READY }
    private STATE state = STATE.IDLE;

    public PinpadManager(boolean isVirtualPinpad) {
        virtualPinpad = isVirtualPinpad;
        handler = new Handler(Looper.getMainLooper());
    }

    private void runOnUiThread(Runnable r) {
        handler.post(r);
    }

    public void updatePinLength(final int len) {
        runOnUiThread (() -> {
            try{
                if (len > 0)
                    tv_pin.setText(String.format("%0" + len + "d", 0).replace("0","*"));
                else
                    tv_pin.setText("");
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    // start PIN process until finish
    public boolean start() {

//        state = STATE.WAIT_GUI;

        runOnUiThread(() -> {

            try{
                tv_pin.setText("");    // clear

                // In PCI requirement, it requires to freeze screen during PIN INPUT
                if(virtualPinpad)
                    Sys.setPinEntryModeEnabled(true);
            } catch (Exception e){
            }

        });

        waitReady();

        return state == STATE.READY;
    }

    public void end() {
        runOnUiThread(() -> {

            // In PCI requirement, it requires to freeze screen during PIN INPUT
            if (virtualPinpad)
                Sys.setPinEntryModeEnabled(false);

//            pinDialog.cancel();
            state = STATE.IDLE;
        });
    }

    private void waitReady() {
        try {
            synchronized(waitPopupDialog) {
                waitPopupDialog.wait(2000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void unlock() {
        synchronized(waitPopupDialog) {
            waitPopupDialog.notify();
        }
    }

    public void ready() {
        state = STATE.READY;
        unlock();
    }

    public void cancel() {
        state = STATE.IDLE;
        unlock();
    }

}