package com.tgcyber.hotelmobile._activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tgcyber.hotelmobile._utils.LogCat;

/**
 * Created by Administrator on 2016/8/21.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    //重写onReceive方法
    @Override
    public void onReceive(Context context, Intent intent) {
        //后边的XXX.class就是要启动的服务
        try {
            LogCat.i("HotelBootReceiver", "开机自动服务自动启动....." + context.getPackageName());
            Intent service = new Intent(context, HotelUpGPSService.class);
            context.startService(service);
            //启动应用，参数为需要自动启动的应用的包名
            Intent tt = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());

            context.startActivity(tt);
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}