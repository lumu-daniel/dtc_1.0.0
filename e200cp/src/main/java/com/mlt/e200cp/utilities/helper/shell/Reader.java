package com.mlt.e200cp.utilities.helper.shell;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mlt.e200cp.controllers.backgroundcontrollers.SequencyHandler;
import com.mlt.e200cp.controllers.mainlogiccontrollers.HelperEMVClass;
import com.mlt.e200cp.models.enums.Constants;
import com.mlt.e200cp.models.GetTransactionDetails;
import com.mlt.e200cp.utilities.helper.protocol.VNG;
import com.mlt.e200cp.utilities.helper.protocol.VngDef;

import java.util.HashMap;

import static com.mlt.e200cp.controllers.mainlogiccontrollers.HelperEMVClass.callbackInterface;
import static com.mlt.e200cp.models.MessageFlags.RMV_CARD_DISP;

public class Reader extends BaseShell {

    public enum STATE { ERROR, TIMEOUT_CANCEL, IN_ICC, IN_MSR, IN_CTLS, OUT_WITH_DATA, OUT_NO_DATA, UNKNOWN }

    public static String TRACK1 = "";
    public static String TRACK2 = "";
    public static String TRACK3 = "";

    public static VNG rsp = null;

    public static Boolean waiting = false;
//    public static PresentCardDialog presentCardDialog;


    public static HashMap<String, Object> autoReport(boolean icc, boolean msr, boolean ctls, int secTimeout, byte [] data, GetTransactionDetails details) {

//        presentCardDialog = new PresentCardDialog(ctx , true);
        HashMap<String, Object> hashMap = new HashMap<>();

        // AR<RS>{Len}{AR Data}
        // -- {AR Data} = {MSR Status}{ICC Status}{CTLS Status}{CTLS Info}{Timeout}
        VNG req = new VNG("AR");
        int arDataLen = 3 + (ctls ? data.length : 1);
        req.addRSLength(arDataLen);
        req.addData((msr ? (byte)0x01 : (byte)0x00));
        req.addData((icc ? (byte)0x01 : (byte)0x00));
        req.addData((ctls ? (byte)0x01 : (byte)0x00));

        if (!ctls) {
            req.addData((byte)secTimeout);
        } else {
            req.addData(data);
        }

        rsp = portManager.exchangeData(req);

        // AR<RS>{Len}{Status}
        if (!rsp.parseHeader("AR") || rsp.buffer[5] != 0x00){
            hashMap.put("Response",rsp);
            hashMap.put("State", STATE.ERROR);
            return hashMap;
        }

//        presentCardDialog.start();

        STATE state = null;

        while(true) {

            rsp = null;
            byte [] rspData = portManager.waitData(secTimeout * 1000);

            if (rspData == null) {
                state = STATE.ERROR;
                break;
            }

            rsp = new VNG(rspData);
            hashMap.put("Response",rsp);
            if (rsp.parseHeader("81.")) {
                state = STATE.IN_MSR;
            } else if (rsp.parseHeader("QSD")) {
                state = STATE.IN_ICC;
            } else if (rsp.parseHeader("QN")) {
                continue;
            } else if (rsp.parseHeader("QR")) {
                state = STATE.IN_CTLS;
            } else if (rsp.parseHeader("AR")) {
                /*
                    0x01 : Fail
                    0x02 : Timeout
                    0x03 : Cancel
                 */
                if (rsp.buffer[5] == 0x02 || rsp.buffer[5] == 0x03)
                    state = STATE.TIMEOUT_CANCEL;
                else
                    state = STATE.ERROR;
            } else
                state = STATE.UNKNOWN;

            break;
        }

//        presentCardDialog.end();
        hashMap.put("State",state);

        if(hashMap.size()>=1){
            return hashMap;
        }else{
            return null;
        }
    }

    private static STATE parseResponse(byte [] rspData) {

        if (rspData == null)
            return STATE.ERROR;

        VNG rsp = new VNG(rspData);
        String cmd = rsp.parseString(3);

        boolean hasTrackData = false;

        switch (cmd) {
            case "QM1":
                    /*
                        ‘0’: No Card (use notification here)
                        ‘1’: Card inserting
                        ‘2’: Magnetic Stripe Card inserted
                        ‘3’: Card pulling out
                        ‘4’: ICC Card inserted
                        ‘5’: Magnetic Stripe Card inserted (with Card Lock mechanism)
                        ‘6’: ICC Card inserted (with Card Lock mechanism)
                    */
                String status = rsp.parseString(1);
                switch (status) {
                    case "0":
                        return hasTrackData ? STATE.OUT_NO_DATA : STATE.OUT_WITH_DATA;
                    case "4":
                    case "6":
                        return STATE.IN_ICC;
                    case "2":
                    case "5":
                        return STATE.IN_MSR;
                    case "1":   // inserting
                    case "3":   // removing
                        return STATE.UNKNOWN;
                    default:
                        return STATE.UNKNOWN;
                }
            case "81.":
                // overwrite if has data
                String tmp = rsp.parseStringToSymbol(VngDef.FS);
                if(tmp.isEmpty())
                if (!tmp.isEmpty()) TRACK1 = tmp;
                tmp = rsp.parseStringToSymbol(VngDef.FS);
                if (!tmp.isEmpty()) TRACK2 = tmp;
                tmp = rsp.parseStringToSymbol(VngDef.ETX);
                if (!tmp.isEmpty()) TRACK3 = tmp;
                hasTrackData = true;
//
//                setRequestValues(TRACK2.replace("=","D"));
//                MainActivity.mainActivity.helperMethods._setFragment(new EnterPIN());
                // continue until card removed
                return STATE.UNKNOWN;
            default:
                return STATE.UNKNOWN;
        }
    }

    public static STATE waitForCard(int timeout, boolean insertion) throws InterruptedException {
        return waitForCard(timeout, insertion, false);
    }

    public static STATE waitForCard(int timeout, boolean insertion, boolean polling) throws InterruptedException {

        if (!insertion) {
            TRACK1 = "";
            TRACK2 = "";
            TRACK3 = "";
        }

        // check init state
        STATE state = parseResponse(portManager.exchangeData("QM1".getBytes()));

        if (state == STATE.ERROR)
            return state;

        if (insertion) {
            if (state == STATE.IN_ICC || state == STATE.IN_MSR)
                return state;
        } else {
            if (state == STATE.OUT_NO_DATA || state == STATE.OUT_WITH_DATA)
                return state;
        }

        // enable card position notification
        if (!polling)
            if (!portManager.sendData("QM01".getBytes()))
                return STATE.ERROR;

        long st = System.currentTimeMillis();
        int leftTime = timeout;

        waiting = true;

        byte[] rspData = null;

        if (polling)
            if (!portManager.sendData("QM1".getBytes()))
                return STATE.ERROR;

        while(waiting) {

            rspData = portManager.waitData(leftTime);

            if (waiting && rspData != null) {
                state = parseResponse(rspData);

                if (insertion) {
                    if (state == STATE.IN_ICC || state == STATE.IN_MSR)
                        SequencyHandler.getInstance(RMV_CARD_DISP,callbackInterface).execute();
                        break;
                } else {
                    if (state == STATE.OUT_NO_DATA || state == STATE.OUT_WITH_DATA)
                        break;
                }
            }

            leftTime = timeout - (int) (System.currentTimeMillis() - st);

            // cancel or timeout
            if (!waiting || rspData == null || leftTime < 0) {
                state = STATE.TIMEOUT_CANCEL;
                break;
            }

            // polling and already get QM1 response (avoid conflict with 81. and other notifications)
            if (polling && rspData[0] == 'Q' && rspData[1] == 'M') {
                Thread.sleep(500);
                if (!portManager.sendData("QM1".getBytes()))
                    return STATE.ERROR;
            }
        }

        if (!insertion) {
            parseResponse(portManager.waitData(500));
            if (!TRACK2.isEmpty())
                state = STATE.OUT_WITH_DATA;
        }

        // disable card position notification
        if (!polling)
            portManager.sendData("QM00".getBytes());

        // during card insert / remove, there might be unhandled notification data in buffer
        // clear here to avoid impact following actions
        Log.i("TEST", "purge");
        portManager.purge();
        HelperEMVClass.helperEMVClass.procTaskThread.interrupt();
        return state;
    }

    public enum MODE { MSR_ONLY, COMBO_LOCK_CARD, COMBO_NO_LOCK }

    // use polling method here
    public static boolean setReaderMode(MODE mode) throws InterruptedException {

        String cmd = "QM7";
        switch (mode) {
            case MSR_ONLY: cmd += "0"; break;
            case COMBO_LOCK_CARD: cmd += "1"; break;
            case COMBO_NO_LOCK: cmd += "2"; break;
        }

        byte [] rspData = portManager.exchangeData(cmd.getBytes());

        if (rspData == null)
            return false;

        return new VNG(rspData).parseString(4).equals("QM70");
    }


    // use polling method here
    public static byte [] powerOn(boolean warmReset) {

        // QSC{volt: 2 (1.8), 3, 5 V}{flag: 0-cold, 1-warm}
        String cmd = "QSC3" + (warmReset ? "1" : "0");

        VNG rsp = portManager.exchangeData(new VNG(cmd));

        // QSD{Status}<RS>{Len}{ATR Data}
        if (rsp.parseHeader("QSD0")) {
            return rsp.parseRSLenData();
        }

        return new byte [] {};
    }

    // use polling method here
    public static byte [] sendApdu(byte [] apdu) {

        // QSE<RS>{Len}{APDU}
        VNG req = new VNG("QSE");
        req.addRSLenData(apdu);

        VNG rsp = portManager.exchangeData(req);

        // QSF{Status}<RS>{Len}{R-APDU}
        if (rsp.parseHeader("QSF0")) {
            return rsp.parseRSLenData();
        }

        return new byte [] {};
    }



}
