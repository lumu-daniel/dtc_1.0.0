package com.mlt.e200cp.controllers.mainlogiccontrollers;

import android.app.Application;
import android.content.Context;

import rego.printlib.export.Definiation_en.TransferMode;
import rego.printlib.export.regoPrinter;

public class PrinterController extends Application {
	private regoPrinter printer;
	private int myState = 0;
	private Context context;
	private String printName="RG-MTP58B";
	public PrinterController(Context con){
		this.context = con;
	}

	private TransferMode printmode = TransferMode.TM_NONE;
	private boolean labelmark = true;

	public regoPrinter getObject() {
		return printer;
	}

	public void setObject() {
		printer = new regoPrinter(context);
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
