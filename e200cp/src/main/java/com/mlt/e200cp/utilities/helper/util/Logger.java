package com.mlt.e200cp.utilities.helper.util;

import android.util.Log;

/**
 * Created by simon_chen on 2018/2/14.
 */

public class Logger {

    public String className;
    public String methodName;
    public String lineNumber;

    public static Logger LINE_OUT() {

        Logger info = new Logger();

        int level = 3;

        StackTraceElement[] traces;
        traces = Thread.currentThread().getStackTrace();

        String fullClassName = traces[level].getClassName();
        info.className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        info.methodName = traces[level].getMethodName();
        info.lineNumber = "[" + traces[level] + "]";

        return info;
    }

    public static void log(String message, Logger lineOut) {

        String fullClassName = Thread.currentThread().getStackTrace()[2].getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        Log.e(lineOut.className + "." + lineOut.methodName , lineOut.lineNumber + " " + message);
    }

}
