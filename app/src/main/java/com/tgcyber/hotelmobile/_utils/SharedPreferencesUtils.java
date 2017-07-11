package com.tgcyber.hotelmobile._utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.tgcyber.hotelmobile.constants.Constants;

public class SharedPreferencesUtils {

    public static void saveInt(Context context, String key, int value) {
        if (context == null) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
        if (sp != null) {
            Editor editor = sp.edit();
            editor.putInt(key, value);
            editor.commit();
        }
    }

    public static boolean saveString(Context context, String key, String value) {
        if (context == null) {
            return false;
        }
        SharedPreferences sp = context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
        if (sp != null) {
            Editor editor = sp.edit();
            editor.putString(key, value);
            return editor.commit();
        }
        return false;
    }

    public static void saveLong(Context context, String key, Long value) {
        if (context == null) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
        if (sp != null) {
            Editor editor = sp.edit();
            editor.putLong(key, value);
            editor.commit();
        }
    }

    public static void saveBoolean(Context context, String key, Boolean value) {
        if (context == null) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
        if (sp != null) {
            Editor editor = sp.edit();
            editor.putBoolean(key, value);
            editor.commit();
        }
    }

    public static Boolean getBoolean(Context context, String key, Boolean defaultValue) {
        Boolean result = defaultValue;
        if (context == null) {
            return result;
        }
        SharedPreferences sp = context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
        if (sp != null) {
            result = sp.getBoolean(key, defaultValue);
        }
        return result;
    }

    public static String getString(Context context, String key, String defaultValue) {
        String result = defaultValue;
        if (context == null) {
            return result;
        }
        SharedPreferences sp = context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
        if (sp != null) {
            result = sp.getString(key, defaultValue);
        }
        return result;
    }

    public static int getInt(Context context, String key, int defaultValue) {
        int result = defaultValue;
        if (context == null) {
            return result;
        }
        SharedPreferences sp = context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
        if (sp != null) {
            result = sp.getInt(key, defaultValue);
        }
        return result;
    }

    public static long getLong(Context context, String key, long defaultValue) {
        long result = defaultValue;
        if (context == null) {
            return result;
        }
        SharedPreferences sp = context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
        if (sp != null) {
            result = sp.getLong(key, defaultValue);
        }
        return result;
    }

    public static long getLong(Context context, String key) {
        return getLong(context, key, 0);
    }

    public static String getString(Context context, String key) {
        return getString(context, key, null);
    }

    public static int getInt(Context context, String key) {
        return getInt(context, key, 0);
    }

    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }
}
