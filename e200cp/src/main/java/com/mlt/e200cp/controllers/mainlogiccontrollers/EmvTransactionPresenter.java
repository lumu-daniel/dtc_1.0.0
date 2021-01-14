package com.mlt.e200cp.controllers.mainlogiccontrollers;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.mlt.e200cp.controllers.eidcontrollers.ReadChipCards;
import com.mlt.e200cp.interfaces.ChipCardCallBack;
import com.mlt.e200cp.interfaces.ResultsCallback;
import com.mlt.e200cp.interfaces.ViewInterface;
import com.mlt.e200cp.utilities.FetchDeviceSerialNumber.BasePresenterViewWrapper;
import com.mlt.e200cp.utilities.FetchDeviceSerialNumber.ULTests;
import com.mlt.e200cp.utilities.helper.protocol.VNG;
import com.mlt.e200cp.utilities.helper.util.Utility;

import static android.content.ContentValues.TAG;
import static com.mlt.e200cp.utilities.helper.util.Utility.appendLog;

public class EmvTransactionPresenter extends BasePresenterViewWrapper {

    private ResultsCallback callback;
    public static EmvTransactionPresenter emvTransactionPresenter;
    private ChipCardCallBack chipCardCallBack;
    private String timeOut;

    private EmvTransactionPresenter(ChipCardCallBack resultsCallback, Context context, ViewInterface viewInterface){
        this.chipCardCallBack = resultsCallback;
        initPresenter(context,viewInterface);
    }

    private EmvTransactionPresenter(ResultsCallback resultsCallback, Context context, ViewInterface viewInterface, String timeOut){
        this.callback = resultsCallback;
        this.timeOut = timeOut;
        initPresenter(context,viewInterface);
    }

    public static EmvTransactionPresenter getInstance(ResultsCallback callback, Context context, ViewInterface viewInterface,String timeOut) {
        if(emvTransactionPresenter!=null){
            emvTransactionPresenter = null;
        }
        emvTransactionPresenter = new EmvTransactionPresenter(callback,context,viewInterface,timeOut);
        return emvTransactionPresenter;
    }

    public static EmvTransactionPresenter getCHIPInstance(ChipCardCallBack callback, Context context, ViewInterface viewInterface) {
        if(emvTransactionPresenter!=null){
            emvTransactionPresenter = null;
        }
        emvTransactionPresenter = new EmvTransactionPresenter(callback,context,viewInterface);
        return emvTransactionPresenter;
    }

    public static VNG callExchangeData (VNG cmd){
        return emvTransactionPresenter.exchangeData(cmd);
    }
    @Override
    protected void presenterImp() {

        putAction("Run Transaction", () -> runTransaction(timeOut));
        putAction("Read Emirates ID", () -> readEID(chipCardCallBack));
        putAction("Close Port", () -> closePort());
//        putAction( "Kill EMVThread", () -> getProcThread());
        putAction( "Read Transaction Cards", () -> readTranactionCards(chipCardCallBack));
        putAction( "Read RFIDCards", () -> readRFID(callback));
        putAction( "CTLS Notification", () -> getCTLSNotification(timeOut));
        ULTests appData = ULTests.getInstance(ctx);

        initCommPort();

    }


    @Override
    public boolean beforeInvoke(String methodName) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return connect();
    }

    @Override
    public void afterInvoke(String methodName) {
        disconnect();
    }



//    private Thread getProcThread(){
//        return procTaskThread;
//    }

    private void runTransaction(String timeOut) {

        VNG req = new VNG();
        VNG rsp = new VNG();

        // Auto Report Command
        // AR<RS>{Len}{AR Data}
        // AR Data = {MSR Status}{ICC Status}{CTLS Status}{CTLS Info}{Timeout}
            /*
                { MSR Status}:1 byte, 0x00 = Disabled, 0x01 = Enabled
                { ICC Status} : 1 byte, 0x00 = Disabled, 0x01 = Enabled
                { CTLS Status} : 1 byte, 0x00 = Disabled, 0x01 = Enabled
                { CTLS Info} : Present when { CTLS Status}= 0x01.
                Please follow command 55 of CTLS command Set to set {RFID mode} and corresponding tags.
                { Timeout} : 1 byte.Present when { CTLS Status} = 0x00.
                Range 0x00(no timeout) ~0xFF(255 seconds)
            */

        req.clear();
        req.addData("AR");
        // enable all readers
        // CLTS Data = 9F 02 06 00 00 00 00 00 20 DF 15 01 01 DF 20 01 14
        // Timeout == DF20 - 0x14 : 20 second
        // Timeout == DF20 - 0x3C : 60 second
        byte[] arData = Utility.hex2Byte("01 00 00 "+timeOut);
        req.addRSLenData(arData);

        rsp = exchangeData(req);
        if (rsp == null || !rsp.parseString(2).equals("AR")) {
            addLog("Unrecognized Respond of AR");
            return;
        }

        if (rsp.parseRSLenData()[0] != 0x00) {
            addLog("Fail to Enable Auto Report");
            return;
        }

        addLog("Present Card...");

        while (true) {
            // Wait for Present Card - 20 sec
            byte[] rspData = waitData(60000);

            if (rspData == null) {
                addLog("Auto Report No Response");
                return;
            }

            rsp.clear();
            ;
            rsp.addData(rspData);

            // Magnestripe
            if (rsp.tryToParse("81.")) {
                String track1 = rsp.parseStringToSymbol(VNG.FS);
                String track2 = rsp.parseStringToSymbol(VNG.FS);
                String track3 = rsp.parseStringToSymbol(VNG.ETX);

                addLog("Track 1 : " + track1);
                addLog("Track 2 : " + track2);
                addLog("Track 3 : " + track3);

                if(track1!=null&&track2!=null&&track3!=null){
                    JsonObject object = new JsonObject();
                    if(track1.equals("")){
                        track1 = "M^Card";
                    }
                    object.addProperty("track1",track1);
                    object.addProperty("track2",track2);
                    object.addProperty("track3",track3);
                    callback.onResponseSuccess(object.toString());
                }else{
                    callback.onResponseFailure("Error1: Card not read");
                    runTransaction(timeOut);
                }

                Log.i(TAG,track1);
                Log.i(TAG,track2);
                Log.i(TAG,track3);

                return;
            } else if (rsp.tryToParse("AR")) {
                byte status = rsp.parseRSLenData()[0];
                if (status == 2){
                    addLog("Auto Report : Timeout");
                    callback.onResponseFailure("Error2: "+"Auto Report : Timeout");
                }
                else if (status == 3){
                    addLog("Auto Report : Cancel");
                    callback.onResponseFailure("Error3: Auto Report : Cancel");
                }
                else{
                    addLog("Auto Report : " + status);
                    callback.onResponseFailure("Error4: Auto Report : " + status);
                }
                return;
            } else if (rsp.tryToParse("QN")) {
                // ignore QN
                continue;
            } else {
                addLog("Unrecognized Response of AR v2");
                return;
            }
        }
    }

    private void readTranactionCards(ChipCardCallBack callback){
        VNG req = new VNG();
        VNG rsp = new VNG();
        req.clear();
        req.addData("AR");
        byte[] arData = Utility.hex2Byte("00 01 00 14");
        req.addRSLenData(arData);

        rsp = exchangeData(req);
        if (rsp == null || !rsp.parseString(2).equals("AR")) {
            addLog("Unrecognized Respond of AR");
            return;
        }

        if (rsp.parseRSLenData()[0] != 0x00) {
            addLog("Fail to Enable Auto Report");
            return;
        }

        addLog("Present Card...");

        while (true) {
            // Wait for Present Card - 20 sec
            byte[] rspData = waitData(60000);

            if (rspData == null) {
                addLog("Auto Report No Response");
                return;
            }

            rsp.clear();
            ;
            rsp.addData(rspData);

            // ICC
            if (rsp.tryToParse("QSD")) {
                String status = rsp.parseString(1);
                if (status.equals("0"))  // power on ok
                {
                    byte[] ATR = rsp.parseRSLenData();
                    callback.onCardInserted("Inserted");
                    String n = Utility.bytes2Hex(ATR, ATR.length);
                    ReadChipCards readChipCards = new ReadChipCards();
                    readChipCards.onChipCardInserted(n,callback);

                } else {
                    addLog("Power On Fail : " + status);
                    return;
                }
                return;
            } else if (rsp.tryToParse("AR")) {
                byte status = rsp.parseRSLenData()[0];
                if (status == 2){
                    addLog("Auto Report : Timeout");
                    callback.onResponseFailure("Error2: "+"Auto Report : Timeout");
                }
                else if (status == 3){
                    addLog("Auto Report : Cancel");
                    callback.onResponseFailure("Error3: Auto Report : Cancel");
                }
                else{
                    addLog("Auto Report : " + status);
                    callback.onResponseFailure("Error4: Auto Report : " + status);
                }
                return;
            } else if (rsp.tryToParse("QN")) {
                // ignore QN
                continue;
            } else {
                addLog("Unrecognized Response of AR v2");
                return;
            }
        }
    }

    private void readEID(ChipCardCallBack callback){
        VNG req = new VNG();
        VNG rsp = new VNG();

        req.clear();
        req.addData("AR");
        byte[] arData = Utility.hex2Byte("00 01 00 14");
        req.addRSLenData(arData);

        rsp = exchangeData(req);
        if (rsp == null || !rsp.parseString(2).equals("AR")) {
            addLog("Unrecognized Respond of AR");
            return;
        }

        if (rsp.parseRSLenData()[0] != 0x00) {
            addLog("Fail to Enable Auto Report");
            return;
        }

        addLog("Present Card...");

        while (true) {
            // Wait for Present Card - 20 sec
            byte[] rspData = waitData(60000);

            if (rspData == null) {
                addLog("Auto Report No Response");
                return;
            }

            rsp.clear();
            ;
            rsp.addData(rspData);

            if (rsp.tryToParse("QSD")) {
                String status = rsp.parseString(1);
                if (status.equals("0"))  // power on ok
                {
                    callback.onCardInserted("Inserted");
                    byte[] ATR = rsp.parseRSLenData();
                    String n = Utility.bytes2Hex(ATR, ATR.length);
                    ReadChipCards readChipCards = new ReadChipCards();
                    readChipCards.onChipCardInserted(n,callback);

                } else {
                    addLog("Power On Fail : " + status);
                    return;
                }
            } else if (rsp.tryToParse("AR")) {
                byte status = rsp.parseRSLenData()[0];
                if (status == 2)
                    addLog("Auto Report : Timeout");
                else if (status == 3)
                    addLog("Auto Report : Cancel");
                else
                    addLog("Auto Report : " + status);

                return;
            } else if (rsp.tryToParse("QN")) {
                // ignore QN
                continue;
            } else {
                addLog("Unrecognized Response of AR v2");
                return;
            }
            break;
        }
    }

    private void closePort(){
        exchangeData(new VNG("72"));
    }

    private void readRFID(ResultsCallback callback) {
        VNG rsp = exchangeData(new VNG("QR1E000D0000A4000C02E101"));
        String ff = Utility.byte2String(rsp.parseBytes(rsp.size));
        Log.e(TAG, "readRFID: "+ff );

    }

    private void getCTLSNotification(String hexTimeOut){

        try{
            VNG rsp = new VNG();

            rsp = exchangeData(new VNG(Utility.hex2Byte("51 52 1E 00 15 55 00 12 00 9F 02 06 00 00 00 00 10 00 DF 15 01 01 DF 20 01"+hexTimeOut)));
//            rsp = exchangeData(new VNG(Utility.hex2Byte("9F 02 06 00 00 00 00 00 20 DF 15 01 01 DF 20 01 14")));

//        if(Utility.hex2Byte("51521E00145700113A1C1C1E000BE009C20106C30105C90100").equals(rsp)) {
//            callback.onResponseFailure("timed out");
//        }else if(Utility.hex2Byte("51521E00145700113A1C1C1E000BE009C20106C30100C90102").equals(rsp.buffer)){
//            callback.onResponseSuccess("passed");
//        }

//        rsp.clear();

            boolean check =false;
            while(true){

                rsp = new VNG(waitData(40000));


                if(rsp!=null){
                    String data = Utility.bytes2Hex(rsp.parseBytes(rsp.size));
                    if(data.contains("51521E000456000100")){
                        check = true;
//                        procTaskThread.interrupt();
                        break;
                    }else if(data.contains("C30105")){
                        callback.onResponseSuccess("time out");
                        return;
                    }else if(data.contains("C30100")){
                        callback.onResponseSuccess("passed");
                        return;
                    }
                }else{
                    callback.onResponseFailure("failed");
                    return;
                }
            }
//            if(procTaskThread.isInterrupted()){
//                callback.onResponseSuccess("cancelled");
//            }
            if(check){
                callback.onResponseSuccess("cancelled");
            }
            Log.e(TAG, "getCTLSNotification: " );
        }catch (Exception ex){
            ex.printStackTrace();
            appendLog(ex.getLocalizedMessage());
        }
    }
}
