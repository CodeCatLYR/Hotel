package com.tgcyber.hotelmobile.utils;

import android.content.Context;
import android.widget.Toast;

import com.tgcyber.hotelmobile._utils.StringUtil;

public class ToastUtils {
    public static void showMsg(Context context, String msg) {
        if (context == null || StringUtil.isBlank(msg)) {
            return;
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showMsg(Context context, int res) {
        if (context == null) {
            return;
        }
        // System.out.println("res:"+res);
        Toast.makeText(context, res, Toast.LENGTH_SHORT).show();
    }
}
