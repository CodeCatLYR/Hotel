package com.tgcyber.hotelmobile._utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class WebViewUtils {

    public static boolean handlerUrl(Activity activity, String url) {
        if (StringUtil.isBlank(url)) {
            return true;
        } else if (url.startsWith("mailto:")) {
            startEmailActivity(activity, url);
            return true;
        } else if (url.startsWith("tel:")) {
            startTelActivity(activity, url);
            return true;
        } else if (url.toLowerCase().endsWith(".apk")) {
            downloadAPK(activity, url);
            return true;
        }

        return false;
    }

    public static void startEmailActivity(Activity activity, String url) {

        try {
            Uri uri = Uri.parse(url);

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);

            activity.startActivity(emailIntent);
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public static void startTelActivity(Activity activity, String url) {

        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
            activity.startActivity(intent);
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public static void downloadAPK(Activity activity, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(intent);
    }
}
