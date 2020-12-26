package com.mlt.e200cp.controllers.mainlogiccontrollers;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mlt.e200cp.controllers.backgroundcontrollers.SequencyHandler;
import com.mlt.e200cp.controllers.deviceconfigcontrollers.ConfigurationClass;
import com.mlt.e200cp.controllers.deviceconfigcontrollers.FeaturesByProduct;
import com.mlt.e200cp.controllers.presenters.PresenterClasses;
import com.mlt.e200cp.interfaces.PosSequenceInterface;
import com.mlt.e200cp.interfaces.ResponseReciever;
import com.mlt.e200cp.interfaces.ResultsCallback;
import com.mlt.e200cp.interfaces.ViewInterface;
import com.mlt.e200cp.models.EmvTransactionDetails;
import com.mlt.e200cp.models.PosDetails;
import com.mlt.e200cp.models.TestData;
import com.mlt.e200cp.models.repository.response.ISOPaymentResponse;
import com.mlt.e200cp.utilities.FetchDeviceSerialNumber.BasePresenterViewWrapper;
import com.mlt.e200cp.utilities.helper.protocol.EMV;
import com.mlt.e200cp.utilities.helper.protocol.TLV;
import com.mlt.e200cp.utilities.helper.protocol.VNG;
import com.mlt.e200cp.utilities.helper.shell.EmvL2;
import com.mlt.e200cp.utilities.helper.shell.Reader;
import com.mlt.e200cp.utilities.helper.util.Dump;
import com.mlt.e200cp.utilities.helper.util.ISOConstant;
import com.mlt.e200cp.utilities.helper.util.Utility;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import saioapi.util.Sys;
import static com.mlt.e200cp.models.EmvTransactionType.REFUND_TRANSACTION;
import static com.mlt.e200cp.models.StringConstants.CANCEL_TXN;
import static com.mlt.e200cp.models.StringConstants.CARD_ERR;
import static com.mlt.e200cp.models.StringConstants.CARD_INSERTED;
import static com.mlt.e200cp.models.StringConstants.CARD_REMOVED_ERR;
import static com.mlt.e200cp.models.StringConstants.CARD_SWIPPED;
import static com.mlt.e200cp.models.StringConstants.CARD_TAPPED;
import static com.mlt.e200cp.models.StringConstants.CHIP_FALLBACK;
import static com.mlt.e200cp.models.StringConstants.CONTACTLESS_ENTRY_MODE;
import static com.mlt.e200cp.models.StringConstants.CONTACTLESS_MGSTRIPE;
import static com.mlt.e200cp.models.StringConstants.CONTACT_ENTRY_MODE;
import static com.mlt.e200cp.models.StringConstants.CRD_ERR;
import static com.mlt.e200cp.models.StringConstants.CRD_NO_RMVD;
import static com.mlt.e200cp.models.StringConstants.CRD_RMVD;
import static com.mlt.e200cp.models.StringConstants.CRD_UNSPTD_ERR;
import static com.mlt.e200cp.models.StringConstants.EMPTY_PIN_ERR;
import static com.mlt.e200cp.models.StringConstants.FALLBACKCHIPORTAP;
import static com.mlt.e200cp.models.StringConstants.MAGSTRIPE_ENTRY_MODE;
import static com.mlt.e200cp.models.StringConstants.MSR_ERR;
import static com.mlt.e200cp.models.StringConstants.NOT_VER_ERR;
import static com.mlt.e200cp.models.StringConstants.NO_CVM_PRF;
import static com.mlt.e200cp.models.StringConstants.NO_CVM_TYPE;
import static com.mlt.e200cp.models.StringConstants.PROMPT_PHONE;
import static com.mlt.e200cp.models.StringConstants.SELECT_APP;
import static com.mlt.e200cp.models.StringConstants.SIGNATURE_CVM_TYPE;
import static com.mlt.e200cp.models.StringConstants.TIME_OUT_ERR;
import static com.mlt.e200cp.models.StringConstants.TXN_FLOW_ERR;
import static com.mlt.e200cp.models.StringConstants.TXN_PROCESSING;
import static com.mlt.e200cp.models.StringConstants.TXN_REVERSAL;
import static com.mlt.e200cp.models.StringConstants.USE_CHIP_ERR;
import static com.mlt.e200cp.utilities.helper.shell.Reader.STATE.OUT_WITH_DATA;
import static com.mlt.e200cp.utilities.helper.util.ISOConstant.SUCCESSFLAG;
import static com.mlt.e200cp.utilities.helper.util.Logger.LINE_OUT;
import static com.mlt.e200cp.utilities.helper.util.Logger.log;
import static com.mlt.e200cp.utilities.helper.util.Utility.CVMReqLimit;
import static com.mlt.e200cp.utilities.helper.util.Utility.appendLog;
import static com.mlt.e200cp.utilities.helper.util.Utility.getExpiryDateOnCard;
import static com.mlt.e200cp.utilities.helper.util.Utility.getProductName;
import static com.mlt.e200cp.utilities.helper.util.Utility.getTranasctionDateAndTime;
import static com.mlt.e200cp.utilities.helper.util.Utility.hex2string;
import static com.mlt.e200cp.utilities.helper.util.Utility.txn_type;

public class HelperEMVClass extends BasePresenterViewWrapper implements ResponseReciever {

    /*
     * For all classes that will use this utility,
     * Use a single global object and call the
     * onAction(function) method from the global object
     * It should be loaded at the beginning of the main Activity (Starter activity)*/
    public static EmvTransactionDetails emvTransactionDetails;
    public static boolean cancelFlag = false;
    public Reader.STATE state = null;
    private Handler mHandler = null;
    public static int appSelIndex = -1;
    public static boolean waitFlag = false;
    public static boolean checkFlag = false;
    public static boolean cancelledflag;
    public static boolean checkPinEmpty ; // Sets pin error incase person presses enter without pin.
    public static TextView tv_pin;
    private HashMap<String, Object> map;
    public static AppCompatActivity appCompatActivity;
    private TLV dataField = null;
    //    public boolean errorFlag;
    private int ChipProcessingRetryCounter;
    private boolean fallBackFlag; // Used to determine card with chip and ctls.
    private int reverseCount = 0;
    private String ServerIAD ="00000000000000000000";
    public static PosSequenceInterface callbackInterface;
    private int timeOut;
    public PosDetails posDetails;
    public static HelperEMVClass helperEMVClass;
    static ProgressDialog dialog;
    private ResultsCallback callback;
    private ISOPaymentResponse response;

    //set Constructor.
    private HelperEMVClass(AppCompatActivity context, String amount, PosSequenceInterface sequenceInterface, int timeOut1, ViewInterface viewInterface, PosDetails details, boolean isIntergrated){
        FeaturesByProduct.product = getProductName(context);
        FeaturesByProduct.initRelatedObjects(context);
        helperEMVClass=this;
        emvTransactionDetails = new EmvTransactionDetails();
        emvTransactionDetails.setIntergrated(isIntergrated);
        emvTransactionDetails.setGrossAmount(StringUtils.leftPad(amount,12,'0'));
        emvTransactionDetails.setTransactionInitiationDateAndTime(getTranasctionDateAndTime().replace("T"," ").replace("Z",""));
        this.timeOut = timeOut1;
        this.callbackInterface = sequenceInterface;
        this.appCompatActivity = context;
        this.posDetails = details;
        int errConter = 0;
        vi = viewInterface;
        initPresenter(appCompatActivity,vi);
        Sys.setPinEntryModeEnabled(false);
        putAction("Run Transaction", () -> runTransaction());
        putAction("Chip fallback", () -> chipFallback());
        initCommPort();
    }

    public static HelperEMVClass getInstance(AppCompatActivity context, String amount, PosSequenceInterface sequenceInterface, int timeOut1, ViewInterface viewInterface, PosDetails details, boolean isIntergrated){
        if(helperEMVClass!=null){
            helperEMVClass = null;
        }
        helperEMVClass=new HelperEMVClass(context,amount,sequenceInterface,timeOut1,viewInterface,details,isIntergrated);
        return helperEMVClass;
    }


    @Override
    protected boolean beforeInvoke(String methodName) {
        try {

            ChipProcessingRetryCounter = 0;
            fallBackFlag = false;
            checkPinEmpty = false;
            cancelledflag = false;
            cancelFlag = false;
            emvTransactionDetails.setPINENTERED(false);
            return connect();
        } catch (Exception e) {
            e.printStackTrace();
            appendLog(e.getLocalizedMessage());
            return false;
        }
    }

    @Override
    protected void afterInvoke(String methodName) {
        disconnect();
    }

    @Override
    public void addLog(String msg) {
        Log.e("MyLog",msg);
    }

    public void runTransaction(){
        try{

            cancelFlag = false;
            //Response from the EMV commands
            EMV rsp;

            //response from the VNG commands
            VNG response;
            if(txn_type.equals(REFUND_TRANSACTION)){
                map = Reader.autoReport(true, true, true, timeOut,
                        Utility.hex2Byte("00 9F 02 06 "+ StringUtils.leftPad(emvTransactionDetails.getGrossAmount(),
                                12,'0')+"9F 03 06 00 00 00 00 00 00 DF 15 01 03 DF 20 01 14"), emvTransactionDetails);
            }else{
                map = Reader.autoReport(true, true, true, timeOut,
                        Utility.hex2Byte("00 9F 02 06 "+ StringUtils.leftPad(emvTransactionDetails.getGrossAmount(),
                                12,'0')+"9F 03 06 00 00 00 00 00 00 DF 15 01 01 DF 20 01 14"), emvTransactionDetails);
            }

            //Initialise the global tag length Value object to set the tag length values.
            dataField = new TLV();
            dataField.clear();
            dataField.addData(0x9F02, StringUtils.leftPad(emvTransactionDetails.getGrossAmount(),12,'0'));          //Set the amount for both the Contact and the magnetic Stripe.
            if(txn_type.equals(REFUND_TRANSACTION)){
                dataField.addData(0x9C, "20");                     //transaction type refund
            }else{
                dataField.addData(0x9C, "00");                     //transaction type purchase
            }
            dataField.addData(0xDF46, "0001");                  // transaction type by XAC
            emvTransactionDetails.setGenerateReceiptOnly("false");
            emvTransactionDetails.setIsReversal("false");
            dataField.addData(0xDF41, "575A");                // required tags

            state = (Reader.STATE) map.get("State");    //State type of port transaction.
            log(state.name(),LINE_OUT()); //Shows the type of port transaction in the logs.
            response = (VNG) map.get("Response"); //Shows the port response (ICC Data, MSR Data)

            if (cancelFlag) {
                EmvL2.payStop();
                return;
            }

            switch(state){
                case IN_ICC:
                    emvTransactionDetails.setPOSENTRYTYPE(CONTACT_ENTRY_MODE);
                    parseICC(dataField);
                    break;
                case IN_CTLS:
                    emvTransactionDetails.setPOSENTRYTYPE(CONTACTLESS_ENTRY_MODE);
                    if(!processCTLS(response)){
                        return;
                    }else{
                        if(txn_type.equals(REFUND_TRANSACTION)){
                            SequencyHandler.getInstance(TXN_PROCESSING,callbackInterface).execute(appCompatActivity,posDetails);
                            return;
                        }else{
                            if(emvTransactionDetails.getCVMType().equals(NO_CVM_TYPE)){
                                SequencyHandler.getInstance(TXN_PROCESSING,callbackInterface).execute(appCompatActivity,posDetails);
                            }else if(emvTransactionDetails.getCVMType().equals(NO_CVM_PRF)){
                                if(Integer.parseInt(emvTransactionDetails.getGrossAmount()) > CVMReqLimit){
                                    EmvL2.pinInputProcess((byte) 1, null, TestData.DK4PIN.slot, callbackInterface,ctx,posDetails);
                                }else{
                                    SequencyHandler.getInstance(TXN_PROCESSING,callbackInterface).execute(appCompatActivity,posDetails);
                                }
                            }
                            else{
                                EmvL2.pinInputProcess((byte) 1, null, TestData.DK4PIN.slot, callbackInterface,ctx,posDetails);
                            }
                        }

                    }
                    break;
                case IN_MSR:
                    emvTransactionDetails.setPOSENTRYTYPE(MAGSTRIPE_ENTRY_MODE);
                    processMSR(response);
                    break;
                case TIMEOUT_CANCEL:
                    endTransaction(CANCEL_TXN);
                    log("Reader time out...",LINE_OUT());
                    break;
                case UNKNOWN:
                    /*if(ChipProcessingRetryCounter>0){
                        endTransaction(NULL.label);
                    }else{
                        endTransaction(NULL.label);
                        ChipProcessingRetryCounter++;
                    }*/
                    endTransaction(CARD_ERR);
                    log("",LINE_OUT());
                    break;
                case ERROR:
                    endTransaction(CARD_ERR);
                    log("Card error 1, Device restart required",LINE_OUT());
                    break;
            }
        }catch (Exception ex){
            ex.printStackTrace();
            appendLog(ex.getLocalizedMessage());
            log("Card not Supported..."+ex.getStackTrace()[0].getLineNumber()+" "+ex.getStackTrace()[0].getClassName(),LINE_OUT());
            endTransaction(TXN_FLOW_ERR);
        }

    }

    private void processMSR(VNG rsp){
        SequencyHandler.getInstance(CARD_SWIPPED,callbackInterface).execute();
        emvTransactionDetails.setCVMType(/*ctlsData.substring(ctlsData.indexOf("C701")+4,ctlsData.indexOf("C701")+6)*/"01");
        String response = Utility.bytes2Hex(rsp.parseBytes(rsp.size));

        //incase of magstripe failure
        if(response.contains("1C1C")){
            log("Error in magnetic stripe reading",LINE_OUT());
            endTransaction(MSR_ERR);
            return;
            //ToDo;
        }
        String track2Data = response.replace("1C","2D").split("2D")[1];
        String CardName = hex2string(response).split("\\^")[1].replace("/"," ");
        emvTransactionDetails.setCardName(CardName);
        Log.e("TRACK2",CardName);
        setRequestValues(hex2string(track2Data).replace("=","D"));
        Log.e("TRACK2",track2Data);
        if(/*getTransactionDetails.getCardType().equals("Master")&&*/fallBackFlag){
            String[] MC_OPTIONS = {"Do you want to continue with Stripe?","Do you want to use chip?"};
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(appCompatActivity)
                            .setCancelable(false)
                            .setMessage("Please Insert Or Tap Card.")
                            .setPositiveButton("Ok",(dialog, which) -> {
                                SequencyHandler.getInstance(FALLBACKCHIPORTAP,callbackInterface).execute( HelperEMVClass.this);
                                dialog.dismiss();
                            })
                            /*.setItems(MC_OPTIONS, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case 0:
                                            dialog.dismiss();
                                            getTransactionDetails.setPOSENTRYTYPE(Constants.MAGSTRIPE_TECHNICAL_FALLBACK.label);
                                            SequencyHandler.getInstance(callbackInterface).execute(TXN_PROCESSING,ctx,posDetails,callbackInterface);
                                            break;
                                        case 1:
                                            dialog.dismiss();
                                            SequencyHandler.getInstance(callbackInterface).execute(CHIP_FALLBACK,HelperEMVClass.this);
                                            break;
                                    }
                                }
                            })*/
                            .create().show();
                }
            });
        }
        else{
            emvTransactionDetails.setPOSENTRYTYPE(MAGSTRIPE_ENTRY_MODE);
            SequencyHandler.getInstance(TXN_PROCESSING,callbackInterface).execute(ctx,posDetails);
        }
    }

    private Boolean processCTLS(VNG rsp){
        SequencyHandler.getInstance(CARD_TAPPED,callbackInterface).execute();
        String ctlsData = Utility.bytes2Hex(rsp.parseBytes(rsp.size));
        if(ctlsData.contains("C701")){
            emvTransactionDetails.setCVMType(ctlsData.substring(ctlsData.indexOf("C701")+4,ctlsData.indexOf("C701")+6));
        }
        if(ctlsData.contains("5F 20")){
            processCardName(ctlsData.replace(" ",""));
        }
        if(ctlsData.contains("C3018D")){
            waitFlag = true;
            SequencyHandler.getInstance(PROMPT_PHONE,callbackInterface).execute();

            while(waitFlag) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    appendLog(e.getLocalizedMessage());
                }
            }

            if(checkFlag){
                ctlsAfterPhone();
            }else{
                endTransaction(NOT_VER_ERR);
            }
            return false;
        }
        else if(ctlsData.contains("C30129")){
            endTransaction(CRD_UNSPTD_ERR);
            return false;
        }
        else if(ctlsData.contains("C30102")){
            SequencyHandler.getInstance(CHIP_FALLBACK,callbackInterface).execute(this);
            return false;
        }else{
            if(ctlsData.contains("C00101")) {
                if(ctlsData.substring(ctlsData.indexOf("C101")+4,ctlsData.indexOf("C101")+6).equals(CTLSTypes.MC_MagStripe.label)){
                    setRequestValues(getTrack2DataCTLSMSR(ctlsData));
                    emvTransactionDetails.setICCDATA(ctlsData);
                }else if(ctlsData.substring(ctlsData.indexOf("C101")+4,ctlsData.indexOf("C101")+6).equals(CTLSTypes.MC_Mchip.label)){
                    String trkData = getTrack2DataCTLSICC(ctlsData);
                    setRequestValues(trkData);
                    emvTransactionDetails.setICCDATA(ctlsData);
                }
                return true;

            }
            else if(ctlsData.contains("C00102")){
                //MSR ctls processing
                if(ctlsData.substring(ctlsData.indexOf("C101")+4,ctlsData.indexOf("C101")+6).equals(CTLSTypes.VISA_MSD_Legacy.label)){
                    setRequestValues(getTrack2DataCTLSMSR(ctlsData));
                    emvTransactionDetails.setICCDATA(ctlsData);
                }else if(ctlsData.substring(ctlsData.indexOf("C101")+4,ctlsData.indexOf("C101")+6).equals(CTLSTypes.VISA_qVSDC.label)){
                    setRequestValues(getTrack2DataCTLSICC(ctlsData));
                    emvTransactionDetails.setICCDATA(ctlsData);
                }
                return true;
            }
            else{
                if(ctlsData.contains("C30105")){
                    endTransaction(TIME_OUT_ERR);
                }
                else{
                    exchangeData(new VNG(Utility.hex2Byte("51 52 1E 00 03 56 00 00")));
                    endTransaction(USE_CHIP_ERR);
                }
                Log.e("CTLS","CTLS error");

                return false;
            }
        }
    }

    public void parseICC( TLV dataField) {

        // Card Inserted
        SequencyHandler.getInstance(CARD_INSERTED,callbackInterface).execute( );
        EMV rsp = EmvL2.payStart(false, dataField, (callback) -> handlingEmvCallback(callback));
        if (!rsp.isSuccess) {
            if(cancelFlag){
                endTransaction(CANCEL_TXN);
            }else{
                endTransaction(CRD_UNSPTD_ERR);
            }
            log("Chip card not read. Please swipe card...",LINE_OUT());

            cancelFlag = false;
            return;
        }


        Dump.tlv(rsp.dataField);

//        addLog("PAN : " + ULTests.getInstance().pan);
        String track2Data = Utility.bytes2Hex(rsp.dataField.data);
        setRequestValuesICC(track2Data);

        // ====================================== PAY AUTH =======================================

        if(!txn_type.equals(REFUND_TRANSACTION)){
            dataField.clear();
            dataField.addData(0xDF41, "95"); // required tags

            rsp = EmvL2.payAuth(dataField, (callback) -> handlingEmvCallback(callback));
//        Log.e("Error",Utility.byte2String(rsp.parseBytes(rsp.size)));

            if (!rsp.isSuccess) {

                if(!cancelledflag){
                    endTransaction(CARD_REMOVED_ERR);
                }

                cancelledflag = false;
                return;
            }

            if(checkPinEmpty){
                endTransaction(EMPTY_PIN_ERR);
                checkPinEmpty = false;
                return;
            }
        }

        Dump.tlv(rsp.dataField);

        dataField.clear();
        dataField.addData(0xDF41, "5F205F245F2A5F348284959A9B9C9F029F039F099F109F1A9F269F279F339F349F399F359F369F379F089F1E");            // required tags

        if(!emvTransactionDetails.getPINENTERED()){
            emvTransactionDetails.setEPB("Optional");
            emvTransactionDetails.setKSN("Optional");
        }
        // ================================== PAY First Gen AC ===================================
        rsp = EmvL2.payFirstGenAC(false, dataField, (callback) -> handlingEmvCallback(callback));

        if(!txn_type.equals(REFUND_TRANSACTION)){
            Log.e("Error",Utility.byte2String(rsp.parseBytes(rsp.size)));
            if (!rsp.isSuccess) {
//            log("Chip card not read. Please swipe card... FirstGen",LINE_OUT());
//            cancelExcecutution();
//            SequencyHandler.getInstance(callbackInterface).execute(SWIPE_FALLBACK,this);
                if(cancelFlag){
                    endTransaction(CANCEL_TXN);
                }else{
                    endTransaction(CARD_REMOVED_ERR);
                }
                return;
            }

            Dump.tlv(rsp.dataField);

            addLog("First Gen AC : " + rsp.result.toString());

            if(!emvTransactionDetails.getPINENTERED()){
                if(emvTransactionDetails.getUNKNOWNCVM())
                    emvTransactionDetails.setCVMType(SIGNATURE_CVM_TYPE);
                else
                    emvTransactionDetails.setCVMType(NO_CVM_TYPE);
                emvTransactionDetails.setEPB("optional");
                emvTransactionDetails.setKSN("optional");
            }
        }

        SequencyHandler.getInstance(TXN_PROCESSING,callbackInterface).execute(ctx,posDetails);

        int counter = 300;
        while( !ISOConstant.calledService) {
            if(counter!=0){
                try {
                    Thread.sleep(1000);
                    counter--;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    appendLog(e.getLocalizedMessage());
                }
            }else{
                break ;
            }
        }
        ISOConstant.calledService = false;

//        if(!SUCCESSFLAG){
//            log("Remove Card ...",LINE_OUT());
//            endTransaction("Remove Card ...");
//        }

        if(!txn_type.equals(REFUND_TRANSACTION)){
            try{
                //=================================== Ist Issuer Scripth Command =========================
                if(emvTransactionDetails.getIssuerAuthorisationData()!=null&&!emvTransactionDetails.getIssuerAuthorisationData().equals("")){
                    ServerIAD = emvTransactionDetails.getIssuerAuthorisationData();
                }

                String ISD71,ISD72 = "";
                if(emvTransactionDetails.getIssuerScriptingData71()!=null&& emvTransactionDetails.getIssuerScriptingData72()!=null){
                    if(!emvTransactionDetails.getIssuerScriptingData71().equals("")&&!emvTransactionDetails.getIssuerScriptingData72().equals("")){
                        ISD71 = emvTransactionDetails.getIssuerScriptingData71();
                        ISD72 = emvTransactionDetails.getIssuerScriptingData72();

                    }else{
                        ISD71 = "00";
                        ISD72 = "00";
                    }
                }else {
                    ISD71 = "00";
                    ISD72 = "00";
                }

                // ================================== PAY Second Gen AC ==================================
                if(SUCCESSFLAG){
                    dataField.clear();
                    dataField.addData(0x8A, Utility.hex2Byte("3030"));
                    String HexapprovalCode = Utility.bytes2Hex(emvTransactionDetails.getApprovalCode()!=null? emvTransactionDetails.getApprovalCode().getBytes():"000000".getBytes());
                    dataField.addData(0x89, Utility.hex2Byte(HexapprovalCode));
                    dataField.addData(0x9F01, Utility.hex2Byte("414243444546"));
                    dataField.addData(0x91, Utility.hex2Byte(ServerIAD));
                    dataField.addData(0x71,Utility.hex2Byte(ISD71));
                    dataField.addData(0x72,Utility.hex2Byte(ISD72));
//            dataField.addData(0x9F4C, Utility.hex2Byte("0000000000000000"));
                    dataField.addData(0xDF41    , "95");    // required tags

                    rsp = EmvL2.paySecGenAC(true, false, dataField, (callback) -> handlingEmvCallback(callback));

                    Log.e("Second Generate",Utility.bytes2Hex(rsp.parseBytes(rsp.size)));

                    if (!rsp.isSuccess) {
                        log("failed",LINE_OUT());
//                endTransaction("Reversal call required...");
                        emvTransactionDetails.setIsReversal("true");
                        posDetails.setReversalReason("Card Removed.");
                        SequencyHandler.getInstance(TXN_REVERSAL,callbackInterface).execute(appCompatActivity,posDetails,callbackInterface);

                    }else{
                        //prod
                        /*if(ServerIAD.equals("00000000000000000000")){
                            getTransactionDetails.setIsReversal("true");
                            posDetails.setReversalReason("Transaction Rejected by Card.");
                            SequencyHandler.getInstance(TXN_REVERSAL,callbackInterface).execute(appCompatActivity,posDetails,callbackInterface);
                        }
                        else{
                            SequencyHandler.getInstance(TXN_SUCCESSFUL,callbackInterface).execute(response);
                        }*/

                        //UAT
                        endTransaction("");
                    }
                    SUCCESSFLAG = false;
                }
                else{
                    endTransaction("Failed case");
                }
            }catch (Exception ex){
                endTransaction("Error in transaction results");
                ex.printStackTrace();
                appendLog(ex.getLocalizedMessage());
            }
        }
        else{
            endTransaction("");
            if(SUCCESSFLAG){
                callbackInterface.onTransactionEnded("Successfull",response);
                SUCCESSFLAG = false;
            }
        }
        response = null;

    }

    @Override
    protected void onCancel() {
        if (portManager.isConnected()) {
            addLog("Cancel...");
            cancelFlag = true;
            Reader.waiting = false;
            portManager.sendData("72".getBytes());
        }
    }

    public void endTransaction(String msg) {
        EmvL2.payStop();
        showProcessing("Remove Card ...", () -> {
            try {
                Reader.STATE name = Reader.waitForCard(20000, false);
                if(ConfigurationClass.serviceFlag.equalsIgnoreCase("Integrated")){
                    if (name == OUT_WITH_DATA) {
                        PresenterClasses.sendState( CRD_RMVD);
                    } else {
                        PresenterClasses.sendState(CRD_NO_RMVD);
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                appendLog(e.getLocalizedMessage());
            }
        } );
        if(!SUCCESSFLAG){
            if(!msg.equalsIgnoreCase("")){
                callbackInterface.onTransactionEnded("Failed to read card.",response);
            }
        }else{
            SUCCESSFLAG=false;
            callbackInterface.onTransactionEnded("Successful",response);
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
//            e.printStackTrace();
            appendLog(e.getLocalizedMessage());
        }
        portManager.sendData("72".getBytes());
        procTaskThread.interrupt();

    }

    public boolean handlingEmvCallback(EMV callback) {

        if (callback.isEmv) {

            EMV rsp = null;

            switch (callback.cmdId) {
                case EMV.CMD_ID._12_NF_DEBUGINFO:
                    String info = new String(callback.dataField.toByteArray());
                    EmvL2.debugLog.append(info);
                    log(info, LINE_OUT());
                    if(info.contains("5F 20")){
                        processCardName(info.replace(" ",""));
                    }/*else if(info.contains("EMVERR_NOSUPPORT_CARDAID")||info.contains("EMVERR_STOP_TRANSACTION")||info.contains("EMVERR_ICC_COMM_ERROR")||info.contains("UNKNOWN ERROR CODE")){
                        if(info.contains("EMVERR_ICC_COMM_ERROR")){
                            errorFlag = true;
                            Log.e(TAG, "handlingEmvCallback: "+errConter );
                        }else if(info.contains("EMVERR_NOSUPPORT_CARDAID")||info.contains("EMVERR_STOP_TRANSACTION")){
                            errorFlag = true;
                        }
                        errConter++;

                    }*/
                    break;
                case EMV.CMD_ID._13_NF_DISPLAYMSG:
                    handleDisplayMessage(callback.parameter);
                    break;
                case EMV.CMD_ID._14_CB_SELECTAPP:
                    rsp = startSelectionProcess(callback.dataField);
                    break;
                case EMV.CMD_ID._15_CB_TRANAMOUNT:
                    rsp = new EMV(callback.cmdId, (byte)EMV.CMD_TYPE.RESPONSE, (byte)0, (byte)1);
                    break;
                case EMV.CMD_ID._16_CB_EXCEPTION:
                    rsp = new EMV(callback.cmdId, (byte)EMV.CMD_TYPE.RESPONSE, (byte)0, (byte)1);
//                    SequencyHandler.getInstance(callbackInterface).execute(TXN_ERROR,Utility.bytes2Hex(rsp.parseBytes(rsp.size)));
                    break;
                case EMV.CMD_ID._17_CB_VOICEREFERAL:
                    rsp = new EMV(callback.cmdId, (byte)EMV.CMD_TYPE.RESPONSE, (byte)0, (byte)1);
                    break;
                case EMV.CMD_ID._18_CB_PINENTRY:
                    Dump.pin(callback.parameter[0], callback.parameter[1]);
                    if(!cancelledflag){
                        rsp = EmvL2.pinInputProcess(callback.parameter[0], callback.dataField, TestData.DK4PIN.slot, callbackInterface,ctx,posDetails);
                    }

                    break;
                case EMV.CMD_ID._19_CB_UNKNOWN_TAG:
                    rsp = new EMV(callback.cmdId, (byte)0, (byte)0, (byte)0);
                    endTransaction(Utility.bytes2Hex(rsp.parseBytes(rsp.size)));
                    break;
                default:
                    endTransaction(String.valueOf(callback.cmdId));
                    addLog("Unrecognized EMV Callback ID : " + callback.cmdId);
                    return false;
            }

            if (rsp != null) {
                if (!sendData(rsp.getCmdBuffer()))
                    return false;
            }
        }
        return true;
    }

    private void handleDisplayMessage(byte [] parameter) {

        String msg = EMV.EMV_MAP.MSG_TABLE.get(parameter[0] & 0xFF);
        addLog(msg);

        if (parameter[0] == (byte)0x09 ||            // PIN ENTR
                parameter[0] == (byte)0xA4) {            // LAST PIN TRY

            // check P2 for more information
            // -- only presented if Application Configuration Table - PIN Entry Process is 0x00 or 0x02
            /*
                P2- LBYTE : PIN Type
                    x1h : Online PIN
                    x2h : Offline Plaintext PIN
                    x3h : Offline Encrypted PIN
                P2- HBYTE:PIN Try Counter (9F17)
                    2xh ~ Fxh : Remainder PIN try ChipProcessingRetryCounter.
                            If value is larger than 15, set to Fxh,
                    0xh : In case of online PIN
            */
            byte hb = (byte)((parameter[1] & 0xF0) >> 4);
            byte lb = (byte)(parameter[1] & 0x0F);
            Dump.pin(lb, hb);

        } else if (parameter[0] == (byte)0x0A ||        // INCORRECT PIN
                parameter[0] == (byte)0x0D ||        // PIN OK
                parameter[0] == (byte)0xA5 ||        // ONLINE PIN OK
                parameter[0] == (byte)0xA6 ) {       // CANCEL PIN
            // already shown msg above. further handling here
//            Log.e("Error",EMV.EMV_MAP.MSG_TABLE.get(parameter[0]));
        }

    }

    private EMV startSelectionProcess(TLV candidatesData) {

        EMV rsp = new EMV((byte)EMV.CMD_ID._14_CB_SELECTAPP, (byte)EMV.CMD_TYPE.RESPONSE, (byte)0x00, (byte)0x00);
        Log.e("Selection",Utility.bytes2Hex(rsp.parseBytes(rsp.size)));

        List<EMV.CANDIDATE> candidateList = new ArrayList<EMV.CANDIDATE>();
        int offset = 0;

        while(offset < candidatesData.index) {

            TLV.ENTRY candidateRaw = candidatesData.parseTLV(offset);
            offset += candidateRaw.itemLen;

            EMV.CANDIDATE candidate = new EMV.CANDIDATE(candidateRaw.value);
            candidateList.add(candidate);
            log(candidate.toString(), LINE_OUT());
        }

        List<String> candidates = new ArrayList<String>();

        for (EMV.CANDIDATE candidate : candidateList) {
            candidates.add(candidate.szAppLabel);
        }

        appSelIndex = -1;
        selectionDialog(candidates.toArray(new CharSequence[candidates.size()]));
        log("Selected AppEventBus : " + appSelIndex, LINE_OUT());

        if (appSelIndex == -1) {
            rsp.parameter[0] = (byte)0xFF;
            rsp.parameter[1] = (byte)0xFF;
        } else
            rsp.parameter[1] = candidateList.get(appSelIndex).byIndex;

        return rsp;
    }

    private void selectionDialog(final CharSequence[] candidates) {

        waitFlag = true;
        SequencyHandler.getInstance(SELECT_APP,callbackInterface).execute(candidates,10000,waitFlag);

        while(waitFlag) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                appendLog(e.getLocalizedMessage());
            }
        }
    }

    private void setRequestValues(String track2Data){
        if(track2Data.length()>55){
            track2Data = hex2string(track2Data).replace("=","D");
        }
        if(track2Data!=null){
            String cardNumber= track2Data.split("D")[0];
            emvTransactionDetails.setCardNumber(cardNumber);
            Log.e("Track2",track2Data);
            emvTransactionDetails.setTRACKDATA(track2Data);
            emvTransactionDetails.setCardExpiryDate(getExpiryDateOnCard(track2Data.split("D")[1].substring(0,4)));
            if(emvTransactionDetails.getPOSENTRYTYPE().equalsIgnoreCase(MAGSTRIPE_ENTRY_MODE)){
                if(track2Data.split("D")[1].substring(4,5).equals("2")||track2Data.split("D")[1].substring(4,5).equals("6")){
                    fallBackFlag = true;
                }
            }

            emvTransactionDetails.setCardType(cardNumber.startsWith("4")?"Visa":"Master");
            emvTransactionDetails.setKSN("optional");
            emvTransactionDetails.setEPB("optional");
        }else{
            return;
        }
    }

    private String getTrack2DataCTLSICC(String CTLS){
        if(CTLS.substring(CTLS.indexOf("C001")+4,CTLS.indexOf("C001")+6).equals("02")){
            CTLS = CTLS.substring(CTLS.indexOf("E281")+6);
            if(CTLS.startsWith("57")){
                return (CTLS.substring(4,(Integer.parseInt(CTLS.substring(2,4),16)*2)+4));
            }else return "";
        }else if(CTLS.substring(CTLS.indexOf("C001")+4,CTLS.indexOf("C001")+6).equals("01")){
            CTLS = CTLS.substring(CTLS.indexOf("E281")+6);
            if(CTLS.startsWith("57")){
                return (CTLS.substring(4,(Integer.parseInt(CTLS.substring(2,4),16)*2)+4));
            }else return CTLS.substring(CTLS.indexOf("5A")+4,CTLS.indexOf("5A")+20)+"D"+CTLS.substring(CTLS.indexOf("5F24")+6,CTLS.indexOf("5F24")+10);
        }else return "";
    }

    private String getTrack2DataCTLSMSR(String CTLS){
        emvTransactionDetails.setPOSENTRYTYPE(CONTACTLESS_MGSTRIPE);
        return CTLS.substring(CTLS.indexOf("1C")+2,CTLS.indexOf("1C1E"));
    }

    private void setRequestValuesICC(String track2Data){
        String cardNumber= emvTransactionDetails.getCardNumber();
        emvTransactionDetails.setCardNumber(cardNumber);
        String cardTrack2Data = track2Data.substring(4,(Integer.parseInt(track2Data.substring(2,4),16)*2)+4);
        emvTransactionDetails.setTRACKDATA(cardTrack2Data);
        emvTransactionDetails.setCardExpiryDate(getExpiryDateOnCard(cardTrack2Data.split("D")[1].substring(0,4)));
        emvTransactionDetails.setCardType(cardNumber.startsWith("4")?"Visa":"Master");
    }

    private void processCardName(String response){
        String namePart = response.substring(response.indexOf("5F20")+4);
        String nameLength = namePart.substring( 0, 2 );
        int length = Integer.parseInt(nameLength, 16)*2;
        if(length<100){
            String cardName = namePart.substring(2, length+2);
            cardName = hex2string(cardName);
            emvTransactionDetails.setCardName(cardName);
        }

    }

    @Override
    public void onResponseRecieved(ISOPaymentResponse isoPaymentResponse) {
        this.response = isoPaymentResponse;
    }

    private enum CTLSTypes{
        MC_MagStripe("20"),
        MC_Mchip("21"),
        VISA_MSD_Legacy("19"),
        VISA_qVSDC("17");
        public final String label;
        private CTLSTypes(String label) {
            this.label = label;
        }
    }

    private void chipFallback() {


        //Response from the EMV commands
        EMV rsp;

        //response from the VNG commands
        VNG response;

        //set the command to open ports
        map = Reader.autoReport(true, false, false, 20000,
                Utility.hex2Byte("00 9F 02 06 "+ StringUtils.leftPad(emvTransactionDetails.getGrossAmount(),
                        12,'0')+"9F 03 06 00 00 00 00 00 00 DF 15 01 01 DF 20 01 14"), emvTransactionDetails);

        //Initialise the global tag length Value object to set the tag length values.
        dataField = new TLV();
        dataField.clear();
        dataField.addData(0x9F02, StringUtils.leftPad(emvTransactionDetails.getGrossAmount(),12,'0'));          //Set the amount for both the Contact and the magnetic Stripe.
        dataField.addData(0x9C, "00");                     //transaction type
        dataField.addData(0xDF46, "0001");                  // transaction type by XAC
        emvTransactionDetails.setGenerateReceiptOnly("false");
        dataField.addData(0xDF41, "575A");                // required tags

        state = (Reader.STATE) map.get("State");    //State type of port transaction.
        addLog(state.name()); //Shows the type of port transaction in the logs.
        switch (state){
            case IN_ICC:
                emvTransactionDetails.setPOSENTRYTYPE(CONTACT_ENTRY_MODE);
                parseICC(dataField);
                break;
            case TIMEOUT_CANCEL:
                endTransaction(CANCEL_TXN);
                break;
            case ERROR:
//                SequencyHandler.getInstance(callbackInterface).execute(TXN_ERROR,"Transaction timed out.");
                break;
            default:
                endTransaction(CRD_ERR);
                break;
        }
    }

    private void msrFallback() {


        //Response from the EMV commands
        EMV rsp;

        //response from the VNG commands
        VNG response;


        //set the command to open ports
        map = Reader.autoReport(false, true, false, 20000,
                Utility.hex2Byte("00 9F 02 06 "+ StringUtils.leftPad(emvTransactionDetails.getGrossAmount(),
                        12,'0')+"9F 03 06 00 00 00 00 00 00 DF 15 01 01 DF 20 01 14"), emvTransactionDetails);

        state = (Reader.STATE) map.get("State");    //State type of port transaction.
        addLog(state.name()); //Shows the type of port transaction in the logs.
        response = (VNG) map.get("Response");
        switch (state){
            case IN_MSR:
                SequencyHandler.getInstance( CARD_SWIPPED,callbackInterface).execute();
                emvTransactionDetails.setCVMType(/*ctlsData.substring(ctlsData.indexOf("C701")+4,ctlsData.indexOf("C701")+6)*/"01");
                String resp = Utility.bytes2Hex(response.parseBytes(response.size));

                //incase of magstripe failure
                if(resp.contains("1C1C")){
                    log("Error in magnetic stripe reading",LINE_OUT());
                    endTransaction(MSR_ERR);
                    return;
                    //ToDo;
                }
                String track2Data = resp.replace("1C","^").split("2D")[1];
                callback.onResponseSuccess(track2Data);
                break;
            case ERROR:
                callback.onResponseFailure(TIME_OUT_ERR);
                break;
            case TIMEOUT_CANCEL:
                log("time out",LINE_OUT());
                callback.onResponseFailure(TIME_OUT_ERR);
                break;
            default:
                callback.onResponseFailure(CRD_ERR);
                break;
        }
        //Shows the port response (ICC Data, MSR Data)

    }

    private void ctlsAfterPhone() {


        //Response from the EMV commands
        EMV rsp;

        //response from the VNG commands
        VNG response;


        //set the command to open ports
        map = Reader.autoReport(false, false, true, 20000,
                Utility.hex2Byte("00 9F 02 06 "+ StringUtils.leftPad(emvTransactionDetails.getGrossAmount(),
                        12,'0')+"9F 03 06 00 00 00 00 00 00 DF 15 01 01 DF 20 01 14"), emvTransactionDetails);

        state = (Reader.STATE) map.get("State");    //State type of port transaction.
        addLog(state.name()); //Shows the type of port transaction in the logs.
        response = (VNG) map.get("Response");
        switch (state){
            case IN_CTLS:
                emvTransactionDetails.setPOSENTRYTYPE(CONTACTLESS_ENTRY_MODE);
                if(!processCTLS(response)){
                    return;
                }else{
                    if(txn_type.equals(REFUND_TRANSACTION)){
                        SequencyHandler.getInstance(TXN_PROCESSING,callbackInterface).execute(ctx,posDetails);
                    }else{
                        EmvL2.pinInputProcess((byte) 1, null, TestData.DK4PIN.slot, callbackInterface,ctx,posDetails);
                    }
                }
                break;
            case ERROR:
                endTransaction(TIME_OUT_ERR);
                break;
            case TIMEOUT_CANCEL:
                log("time out",LINE_OUT());
                endTransaction(TIME_OUT_ERR);
                break;
            default:
                endTransaction(CRD_ERR);
                break;
        }
        //Shows the port response (ICC Data, MSR Data)

    }


}
