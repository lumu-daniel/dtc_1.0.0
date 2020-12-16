package com.mlt.e200cp.utilities.FetchDeviceSerialNumber;

import android.content.Context;

import com.mlt.e200cp.utilities.helper.ports.PortManager;
import com.mlt.e200cp.utilities.helper.shell.MsgEncrytHandlerE5;

import rego.printlib.export.Definiation_en.*;
import rego.printlib.export.regoPrinter;

import static com.mlt.e200cp.utilities.helper.util.Utility.appendLog;

public class ULTests {

    private static ULTests app =null;
    private static Context baseContext;

    private ULTests(Context context){
        this.baseContext = context;
        portManager = new PortManager();
        p95MsgEncrytHandler = new MsgEncrytHandlerE5();
    }

    public static ULTests getInstance(Context context) {
        try {
            if(app!=null){
                app = null;
            }
            app = new ULTests(context);
        } catch (Exception e) {
            e.printStackTrace();
            appendLog(e.getLocalizedMessage());
        }
        return app;
    }

    public String serialNumber = null;
    public String pan = "";
    public String product = "xCL_E200CP";

    // put into application data to keep the only
    public MsgEncrytHandlerE5 p95MsgEncrytHandler = null;
    public PortManager portManager = null;

    private regoPrinter printer;
    private int myState = 0;
    private String printName="RG-MTP58B";

    private TransferMode printmode = TransferMode.TM_NONE;
    private boolean labelmark = true;

    public regoPrinter getObject() {
        return printer;
    }

    public void setObject() {
        printer = new regoPrinter(baseContext);
    }

    public String getName() {
        return printName;
    }

    public void setName(String name) {
        printName = name;
    }
    public void setState(int state) {
        myState = state;
    }

    public int getState() {
        return myState;
    }

    public void setPrintway(int printway) {

        switch (printway) {
            case 0:
                printmode = TransferMode.TM_NONE;
                break;
            case 1:
                printmode = TransferMode.TM_DT_V1;
                break;
            default:
                printmode = TransferMode.TM_DT_V2;
                break;
        }

    }

    public int getPrintway() {
        return printmode.getValue();
    }

    public boolean getlabel() {
        return labelmark;
    }

    public void setlabel(boolean labelprint) {
        labelmark = labelprint;
    }
}
