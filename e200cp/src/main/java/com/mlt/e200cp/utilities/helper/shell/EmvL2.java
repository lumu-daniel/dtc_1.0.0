package com.mlt.e200cp.utilities.helper.shell;

import android.content.Context;
import android.util.Log;

import com.mlt.e200cp.controllers.backgroundcontrollers.SequencyHandler;
import com.mlt.e200cp.controllers.mainlogiccontrollers.HelperEMVClass;
import com.mlt.e200cp.interfaces.PosSequenceInterface;
import com.mlt.e200cp.models.PosDetails;
import com.mlt.e200cp.utilities.helper.listener.EmvCallback;
import com.mlt.e200cp.utilities.helper.protocol.EMV;
import com.mlt.e200cp.utilities.helper.protocol.TLV;
import com.mlt.e200cp.utilities.helper.util.Utility;

import static com.mlt.e200cp.controllers.mainlogiccontrollers.HelperEMVClass.emvTransactionDetails;
import static com.mlt.e200cp.models.StringConstants.CANCEL_TXN;
import static com.mlt.e200cp.models.StringConstants.CONTACTLESS_ENTRY_MODE;
import static com.mlt.e200cp.models.StringConstants.EMPTY_PIN_ERR;
import static com.mlt.e200cp.models.StringConstants.PINENTRY_FAIL;
import static com.mlt.e200cp.models.StringConstants.PIN_CVM_TYPE;
import static com.mlt.e200cp.models.StringConstants.PIN_ERR;
import static com.mlt.e200cp.models.StringConstants.PIN_TIME_OUT_ERR;
import static com.mlt.e200cp.models.StringConstants.START_PINENTRY;
import static com.mlt.e200cp.models.StringConstants.TXN_PROCESSING;
import static com.mlt.e200cp.utilities.helper.protocol.EMV.CMD_ID._53_PayFirstGenAC;
import static com.mlt.e200cp.utilities.helper.util.Logger.LINE_OUT;
import static com.mlt.e200cp.utilities.helper.util.Logger.log;
import static com.mlt.e200cp.utilities.helper.util.Utility.appendLog;

public class EmvL2 extends BaseShell {

    public static final int MAX_FLOW_TIMEOUT = 6000;

    public static StringBuilder debugLog = new StringBuilder();

    public static EMV exchange(EMV req) {

        EMV rsp = new EMV();

        byte [] rspData = portManager.exchangeData(req.getCmdBuffer());

        if (rspData == null)
            return new EMV();

        rsp.setData(rspData);
        if (rsp.isEmv){
            if (rsp.cmdId == req.cmdId && rsp.cmdType == EMV.CMD_TYPE.RESPONSE) {
            } else {
                log("Unexpected EMV INS : " + rsp.cmdId, LINE_OUT());
                rsp.isSuccess = false;
            }
        }
        return rsp;
    }

    public static EMV runTransactionFlow(EMV req, EmvCallback callback) {

        try{
            EMV rsp = new EMV();

            if(!portManager.sendData(req.getCmdBuffer()))
                return rsp;

            while(true) {

                byte [] rspData = portManager.waitData(MAX_FLOW_TIMEOUT);
                if(rspData==null){
                    return rsp;
                }
                else{
                    String response = Utility.bytes2Hex(rspData);
//                    Log.e("RESPONSE",response);
                }

                rsp.setData(rspData);
                String rspstr = Utility.bytes2Hex(rsp.buffer);
//                Log.e("RESPONSE",rspstr);

                boolean invokeCallback = false;

                if (rsp.isEmv){

                    if (rsp.cmdId == req.cmdId && rsp.cmdType == EMV.CMD_TYPE.RESPONSE) {
                        return rsp;
                    } else if (rsp.cmdType == EMV.CMD_TYPE.REQUEST || rsp.cmdType == EMV.CMD_TYPE.NOTIFICATION ) {
                        invokeCallback = true;
                    } else {
                        log("Unexpected EMV INS : " + rsp.cmdId, LINE_OUT());
                        return new EMV();
                    }

                } else {
                    // non-EMV YM format response
                    invokeCallback = true;
                }

                if (invokeCallback && callback != null) {
                    if (!callback.run(rsp)) {
                        return new EMV();
                    }
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
            appendLog(ex.getLocalizedMessage());
            return null;
        }

    }

    // YM<RS>{Len}{0x51}C{P1:Reader Flag}{P2:0x00}{Len}{BER-TLV}
    public static EMV payStart(boolean debugMode, TLV data, EmvCallback callback) {

        //        P1:Reader Flag
        //        bit1 : Force Online
        //        bit2 : Force Accept
        //        bit8 : Enable Debug Mode
        byte P1 = 0;
        byte P2 = 0;

        if (debugMode) {
            P1 |= 0x80;
            debugLog.setLength(0);
        }

        EMV rsp = runTransactionFlow(
                new EMV((byte)EMV.CMD_ID._51_PayStart, (byte)EMV.CMD_TYPE.REQUEST, P1, P2, data),
                callback);
        if(rsp==null){
            return null;
        }

        if (rsp.isSuccess) {
            byte [] panNumeric = rsp.dataField.retrieveTag(0x5A, true);
            if(panNumeric!=null){
                String pan = Utility.numeric2string(panNumeric).replace("F","");
//                ULTests.getInstance().pan = pan;
                emvTransactionDetails.setCardNumber(pan);
            }else {
                return null;
            }
        }else{
            HelperEMVClass.cancelFlag = true;
        }

        return rsp;
    }

    // YM<RS>{Len}{0x52}C{P1:0x00}{P2:0x00}{Len}{BER-TLV}
    public static EMV payAuth(TLV data, EmvCallback callback) {

        EMV rsp = runTransactionFlow(new EMV((byte)EMV.CMD_ID._52_PayAuth, (byte)EMV.CMD_TYPE.REQUEST, (byte)0, (byte)0, data), callback);

//        String response = Utility.bytes2Hex(rsp.buffer);
        //byte [] cardName = rsp.dataField.retrieveTag(0x5F20,true);
//        log(Utility.byte2String(cardName),LINE_OUT());
//        Log.e("RESPONSE",response);

        return rsp;
    }

    // YM<RS>{Len}{0x53}C{P1:ForceAAC}{P2:0x00}{Len}{BER-TLV}
    public static EMV payFirstGenAC(boolean forceAAC, TLV data, EmvCallback callback) {

        byte P1 = (byte)(forceAAC ? 1 : 0);

        EMV holder = new EMV((byte) _53_PayFirstGenAC, (byte)EMV.CMD_TYPE.REQUEST, P1, (byte)0, data);
        String request = Utility.bytes2Hex(data.data);

        EMV rsp = runTransactionFlow(new EMV((byte) _53_PayFirstGenAC, (byte)EMV.CMD_TYPE.REQUEST, P1, (byte)0, data), callback);

        if(rsp==null){
            HelperEMVClass.helperEMVClass.endTransaction(CANCEL_TXN);
        }
        String response = Utility.bytes2Hex(rsp.dataField.data);
//        Log.e("TAG",response);
        String iccData = response.substring(0,response.indexOf("0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"));
        processName(iccData);

        Log.e("ICC",iccData);
        if(iccData.substring(iccData.indexOf("9505")+4,iccData.indexOf("9505")+14).substring(4,5).equals("4")){
            emvTransactionDetails.setUNKNOWNCVM(true);
        }else{
            emvTransactionDetails.setUNKNOWNCVM(false);
        }

        return rsp;
    }

    private static void processName(String data){
        String length = data.substring(4,6);
        int num = (Integer.parseInt(length.trim(), 16 ))*2;
        String name = data.substring(6,num+6);
        String nameData = getName(name);
        emvTransactionDetails.setCardName(nameData);
        String ICC = data.replace(data.substring(0,num+6),"");
        emvTransactionDetails.setICCDATA(ICC);
    }

    private static String getName(String hex){
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hex.length(); i+=2) {
            String str = hex.substring(i, i+2);
            output.append((char)Integer.parseInt(str, 16));
        }
        return output.toString();
    }



    // YM<RS>{Len}{0x54}C{P1:OnlineStatus}{P2:ForceAAC}{Len}{BER-TLV}
    public static EMV paySecGenAC(boolean onlineStatus, boolean forceAAC, TLV data, EmvCallback callback) {

        byte P1 = (byte)(onlineStatus ? 1 : 0);
        byte P2 = (byte)(forceAAC ? 1 : 0);

        EMV rsp = runTransactionFlow(
                new EMV((byte)EMV.CMD_ID._54_PaySecGenAC, (byte)EMV.CMD_TYPE.REQUEST, P1, P2, data),
                callback);

        return rsp;
    }

    // YM<RS>{Len}{0x55}C{P1:0x00}{P2:0x00}{Len:0x0000}
    public static void payStop() {
        exchange(new EMV((byte)EMV.CMD_ID._55_PayStop));
    }

    static int count = 0;
    public static EMV pinInputProcess(byte pinType, TLV pinInfo, char pinKey, PosSequenceInterface sequenceInterface, Context ctx, PosDetails posDetails) {

        SequencyHandler.getInstance(START_PINENTRY,sequenceInterface).execute();
        EMV emvCallbackRsp = new EMV((byte)EMV.CMD_ID._18_CB_PINENTRY, (byte)EMV.CMD_TYPE.RESPONSE, (byte)0, (byte)0);

        Pinpad.Params params;


        if (pinType == EMV.CVM_PIN_TYPE.OnlinePin)
            params = new Pinpad.Params(Pinpad.TYPE.ONLINE, pinKey, '0', emvTransactionDetails.getCardNumber());
        else
            params = new Pinpad.Params(Pinpad.TYPE.OFFLINE, pinInfo.retrieveTag(0xC2, true));

        Pinpad.Result pinResult = new Pinpad().input(params);

            // EP1{PEK#}{PBF}{Min}{Max}{Timeout}{Account}

        if (pinResult.status == Pinpad.Status.OK) {

        }
        switch (pinResult.status) {
            case OK:
                if (pinType == EMV.CVM_PIN_TYPE.OnlinePin) {
                    byte[] epb_ksn = Utility.hex2Byte(pinResult.data.substring(0, 16));
                    emvCallbackRsp.dataField = new TLV();
                    emvCallbackRsp.dataField.addData(0xDF43, epb_ksn);
                    if(pinResult.data.substring(0, 16).equalsIgnoreCase("0000000000000000")){
                        HelperEMVClass.checkPinEmpty = true;
                    }else{
                        String epb_ksnString = pinResult.data.substring(0,36);
                        Log.e("PINEPB",epb_ksnString);
                        emvTransactionDetails.setEPB(epb_ksnString.substring(0,16));
                        emvTransactionDetails.setKSN(epb_ksnString.substring(16));
                        emvTransactionDetails.setCVMType(PIN_CVM_TYPE);
                    }

                } else {
                    byte[] rpdu = Utility.hex2Byte(pinResult.data);
                    emvCallbackRsp.dataField = new TLV();
                    emvCallbackRsp.dataField.addData(0xDF45, rpdu);
                    String rpduString = Utility.bytes2Hex(rpdu);
                    Log.e("PINEPB",rpduString);
                    if(rpduString.equals("9000")){
                        emvTransactionDetails.setEPB("offline");
                        emvTransactionDetails.setKSN("offline");
                    }else {
                        if(count>1){
                            HelperEMVClass.helperEMVClass.endTransaction("Wrong pin entered multiple times.");
                            HelperEMVClass.cancelledflag = true;
                            count = 0;
                        }else{
                            SequencyHandler.getInstance(PINENTRY_FAIL,sequenceInterface).execute();
                            count++;
                        }

                    }
                    emvTransactionDetails.setCVMType(PIN_CVM_TYPE);
                }
                emvTransactionDetails.setPINENTERED(true);
                if(emvTransactionDetails.getPOSENTRYTYPE().equals(CONTACTLESS_ENTRY_MODE)){
                    if(HelperEMVClass.checkPinEmpty){
                        HelperEMVClass.helperEMVClass.endTransaction(EMPTY_PIN_ERR);
                        HelperEMVClass.checkPinEmpty = false;
                    }else{
                        SequencyHandler.getInstance(TXN_PROCESSING,sequenceInterface).execute(ctx,posDetails);
                    }
                }
                break;
            case CANCEL:
                if(!emvTransactionDetails.getPOSENTRYTYPE().equals(CONTACTLESS_ENTRY_MODE)){
                    HelperEMVClass.cancelledflag = true;
                }
                HelperEMVClass.helperEMVClass.endTransaction(CANCEL_TXN);
                break;
            case TIMEOUT:
                if(!emvTransactionDetails.getPOSENTRYTYPE().equals(CONTACTLESS_ENTRY_MODE)){
                    HelperEMVClass.cancelledflag = true;
                }
                emvCallbackRsp.parameter[1] = 2;
                HelperEMVClass.helperEMVClass.endTransaction(PIN_TIME_OUT_ERR);
                break;
            case BYPASS:
                emvCallbackRsp.parameter[1] = 3;
                break;
            case ERR:
                if(!emvTransactionDetails.getPOSENTRYTYPE().equals(CONTACTLESS_ENTRY_MODE)){
                    HelperEMVClass.cancelledflag = true;
                }
                HelperEMVClass.helperEMVClass.endTransaction(PIN_ERR);
                break;
            default:
                emvCallbackRsp.parameter[1] = 1;
                    break;
        }

        return emvCallbackRsp;
    }
}
