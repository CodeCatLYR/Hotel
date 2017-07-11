package com.tgcyber.hotelmobile._activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;

import com.tgcyber.hotelmobile.R;

/**
 * Created by lyr on 2017/7/7.
 */

public class AppCenterActivity extends BaseActionBarActivity implements View.OnClickListener {

    private final String WX_PACKAGE_NAME = "com.tencent.mm";
    private final String SINA_PACKAGE_NAME = "com.sina.weibo";
    private final String FB_PACKAGE_NAME = "com.facebook.katana";
    private final String INSTAGRAM_PACKAGE_NAME = "com.instagram.android";
    private final String ZHUHAI_PACKAGE_NAME = "com.jdar.game";
    private final String MACAU_PACKAGE_NAME = "com.macau.ar";

//    07-07 17:28:41.211 29581-31176/com.tgcyber.hotelmobile I/lyr: name ----   微信    apk path -------     com.tencent.mm  download url   ---  http://dl.hotelmobile.top/static/app/weixin_1042.apk
//            07-07 17:29:36.302 29581-31176/com.tgcyber.hotelmobile I/lyr: name ----   微博    apk path -------     com.sina.weibo  download url   ---  http://dl.hotelmobile.top/static/app/weibo_3350.apk
//            07-07 17:30:40.562 29581-29581/com.tgcyber.hotelmobile I/lyr: message unread count ----   0
//            07-07 17:30:53.020 29581-31176/com.tgcyber.hotelmobile I/lyr: name ----   Facebook    apk path -------     com.facebook.katana  download url   ---  http://dl.hotelmobile.top/static/app/Facebook_52431682.apk
//            07-07 17:31:17.987 29581-31176/com.tgcyber.hotelmobile I/lyr: name ----   Instagram    apk path -------     com.instagram.android  download url   ---  http://dl.hotelmobile.top/static/app/instagram-10-15-0..apk


//     name ----   珠海AR景区    apk path -------     com.jdar.game  download url   ---  http://dl.hotelmobile.top/static/ar.apk
//    name ----   澳门AR景区    apk path -------     com.macau.ar  download url   ---  http://dl.hotelmobile.top/static/app/ar0401.apk


    @Override
    int getLayoutId() {
        return R.layout.activity_app_center;
    }

    @Override
    protected void initView() {
        super.initView();
        setActionbarLeftDrawable(R.drawable.back);
        setActionbarTitle("应用中心");
        findViewById(R.id.appcenter_wx_ll).setOnClickListener(this);
        findViewById(R.id.appcenter_sina_ll).setOnClickListener(this);
        findViewById(R.id.appcenter_fb_ll).setOnClickListener(this);
        findViewById(R.id.appcenter_instagram_ll).setOnClickListener(this);
        findViewById(R.id.appcenter_zhuhai_ll).setOnClickListener(this);
        findViewById(R.id.appcenter_macau_ll).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.appcenter_wx_ll:
                openApp(WX_PACKAGE_NAME);
                break;

            case R.id.appcenter_sina_ll:
                openApp(SINA_PACKAGE_NAME);
                break;

            case R.id.appcenter_fb_ll:
                openApp(FB_PACKAGE_NAME);
                break;

            case R.id.appcenter_instagram_ll:
                openApp(INSTAGRAM_PACKAGE_NAME);
                break;

            case R.id.appcenter_zhuhai_ll:
                openApp(ZHUHAI_PACKAGE_NAME);
                break;

            case R.id.appcenter_macau_ll:
                openApp(MACAU_PACKAGE_NAME);
                break;
        }
    }



    private void openApp(String packName){
        PackageManager packageManager = getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packName);
        if(intent != null){
            startActivity(intent);
        }else{
            showToast("还没安装应用，请先下载应用");
        }
    }
}
