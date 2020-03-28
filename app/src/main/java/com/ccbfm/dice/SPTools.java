package com.ccbfm.dice;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences
 */
public final class SPTools {
    private static final String TAG = "SPTools";
    private static final boolean DEBUG = false;

    private static Application sContext;
    private static SharedPreferences sPreferences;

    private static final String PREFERENCES_DICE_NUMBER = "preferences_dice_number";
    public static final String KEY_DICE_NUMBER_RECORD = "key_dice_number_record";
    public static final String KEY_DICE_NUMBER = "key_dice_number";

    public static void init(Application context) {
        sContext = context;
    }


    public static int getIntValue(String key) {
        return getIntValue(key, 0);
    }

    public static int getIntValue(String key, int defValue) {
        checkPreferences();
        int value = sPreferences.getInt(key, defValue);
        if (DEBUG) {
            LogTools.w(TAG, "getIntValue", key + " = " + value);
        }
        return value;
    }

    public static void putIntValue(String key, int value) {
        checkPreferences();
        if (DEBUG) {
            LogTools.w(TAG, "putIntValue", key + " = " + value);
        }
        sPreferences.edit().putInt(key, value).apply();
    }

    public static String getStringValue(String key) {
        return getStringValue(key, "");
    }

    public static String getStringValue(String key, String defValue) {
        checkPreferences();
        return sPreferences.getString(key, defValue);
    }

    public static void putStringValue(String key, String value) {
        checkPreferences();
        sPreferences.edit().putString(key, value).apply();
    }

    private static void checkPreferences() {
        if (sPreferences == null) {
            synchronized (SPTools.class) {
                if (sContext == null) {
                    throw new NullPointerException("Context == null");
                }
                if (sPreferences == null) {
                    sPreferences = sContext.getSharedPreferences(PREFERENCES_DICE_NUMBER, Context.MODE_PRIVATE);
                }
            }
        }
    }
}
