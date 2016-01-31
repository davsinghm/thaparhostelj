package com.temp.jhostelapp;

import android.content.Context;
import android.preference.PreferenceManager;

import com.temp.jhostelapp.utils.Utils;

/**
 * Created by DSM_ on 1/30/16.
 */

public class PreferenceHelper {

    //preference names
    final public static String TIME_LASTEST_NOTIFICATIONS = "time_lastest_notifications";
    final public static String TIME_LASTEST_COMPLAINTS = "time_lastest_complaints";

    //caching frequently used strings
    private static String rollNo;
    private static String token;

    public static String getRollNo(Context context) {
        if (rollNo != null)
            return rollNo;
        return (rollNo = getString(context, "rollNo", null));
    }

    public static String getToken(Context context) {
        if (rollNo != null) //checking if rollNo & token ever set? cause token can be null;
            return token;
        return (token = getString(context, "token", null));
    }

    public static void putRollNo(Context context, String rollNo) {
        rollNo = Utils.emptyToNull(rollNo);
        PreferenceHelper.rollNo = rollNo;
        putString(context, "rollNo", rollNo);
    }

    public static void putToken(Context context, String token) {
        token = Utils.emptyToNull(token);
        PreferenceHelper.token = token;
        putString(context, "token", token);
    }

    public static long getLong(Context context, String name, long defVal) {
        return  PreferenceManager.getDefaultSharedPreferences(context).getLong(name, defVal);
    }

    public static String getString(Context context, String name, String defVal) {
        return Utils.emptyToNull(PreferenceManager.getDefaultSharedPreferences(context).getString(name, defVal));
    }

    public static void putLong(Context context, String name, long value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(name, value).commit();
    }

    public static void putString(Context context, String name, String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(name, value).commit();
    }
}
