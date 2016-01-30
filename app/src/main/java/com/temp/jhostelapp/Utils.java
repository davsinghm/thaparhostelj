package com.temp.jhostelapp;

/**
 * Created by DSM_ on 1/30/16.
 */
public class Utils {

    public static String emptyToNull(String string) {
        if ("".equals(string)) {
            return null;
        }
        return string;
    }

    public static long parseLong(String string, long defVal) {
        try {
            return Long.valueOf(string);
        } catch (NumberFormatException e) {
            return defVal;
        }
    }
}
