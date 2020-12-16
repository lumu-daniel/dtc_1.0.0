package com.mlt.e200cp.utilities.helper.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceConnector {

    public static final String PREF_NAME = "USER_PREFERENCES";
    public static final int MODE = Context.MODE_PRIVATE;

    public static void writeString(Context context, String key, String value) {
        getEditor(context).putString(key, value).commit();

    }

    public static SharedPreferences.Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }


    public static String readString(Context context, String key, String defValue) {
        return getPreferences(context).getString(key, defValue);
    }



    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, MODE);
    }

}
