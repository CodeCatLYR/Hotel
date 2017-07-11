package com.tgcyber.hotelmobile._utils;

import android.content.Context;
import android.provider.Settings.Secure;

import java.util.UUID;

public class DeviceUtils {

    private static String deviceid = null;
    private static String DEVICE_ID = "DEVICE_ID";

    public static String getDeviceID(Context context) {
        if (deviceid != null && !deviceid.startsWith("HOTEL"))
            return deviceid;

        if (context == null) {

            return deviceid == null ? "" : deviceid;
        }

        String temp = null;
        try {
            temp = Secure.getString(context.getContentResolver(),
                    Secure.ANDROID_ID);
        } catch (Exception e) {
        }
        if (temp == null || temp.length() < 9) {
            if (deviceid == null) {
                temp = com.tgcyber.hotelmobile.utils.SharedPreferencesUtils.getString(context, DEVICE_ID);

                if (temp == null) {
                    temp = newRandomUUID();
                    com.tgcyber.hotelmobile.utils.SharedPreferencesUtils.saveString(context, DEVICE_ID, temp);
                }
                deviceid = temp;
            }
        } else
            deviceid = temp;

        return deviceid == null ? "" : deviceid;
    }

    private static String newRandomUUID() {
        String uuidRaw = UUID.randomUUID().toString();
        return "HOTEL" + uuidRaw.replaceAll("-", "");
    }
}
