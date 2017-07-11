package com.tgcyber.hotelmobile._utils;

import android.util.Log;

import com.tgcyber.hotelmobile.constants.Constants;

public class LogCat {

    /*
     * public static void i(String msg){ if(Constants.logged){ Log.i("gary",
     * msg); }
     *
     * }
     */
    public static void i(String tag, String msg) {
        if (Constants.logged) {
            if (msg == null){
                Log.i(tag, tag + "为空");
            }else {
                Log.i(tag, msg);
            }
        }
    }

    public static void e(String tag, String msg) {
        // TODO Auto-generated method stub
        if (Constants.logged) {
            if (msg == null){
                Log.e(tag, tag + "为空");
            } else {
                Log.e(tag, msg);
            }
        }
    }
}
