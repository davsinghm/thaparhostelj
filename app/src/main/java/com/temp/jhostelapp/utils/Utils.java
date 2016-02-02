package com.temp.jhostelapp.utils;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.temp.jhostelapp.R;

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

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager inputManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static int getAccentColor(Context context) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, value, true);
        return value.data;
    }
}
