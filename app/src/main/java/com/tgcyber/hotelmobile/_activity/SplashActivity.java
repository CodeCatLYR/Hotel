package com.tgcyber.hotelmobile._activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile.constants.Constants;
import com.tgcyber.hotelmobile._utils.LogCat;
import com.tgcyber.hotelmobile.bean.HomePageBean;
import com.tgcyber.hotelmobile.utils.HotelClient;
import com.tgcyber.hotelmobile.utils.SharedPreferencesUtils;

import cz.msebera.android.httpclient.Header;

public class SplashActivity extends com.tgcyber.hotelmobile._activity.BaseActivity   {

    private final String mPageName = "SplashActivity";

    @Override
    int getLayoutId() {
        // TODO Auto-generated method stub
        return R.layout.activity_splash;
    }
    @Override
    public void onResume() {
        super.onResume();
        if((beginTime>0&&System.currentTimeMillis()-beginTime>sleep))
            startMain();

    }
    private long beginTime=0;
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    void initView() {
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }

    private Bitmap bg = null;
    private ImageView ivBg;
    private FrameLayout lo;
    //private String isShowAd = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        beginTime=System.currentTimeMillis();
        lo = (FrameLayout) findViewById(R.id.iv_activity_splash_layout);
      ivBg = findImageViewById(R.id.iv_activity_splash_bg);
        itemBigImgOptions = new  DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheOnDisk(true).build();

        LogCat.i("SplashActivity", "onCreate:Constants.screenWidth=" + Constants.screenWidth  + " Constants.screenHeigh=" + Constants.screenHeight);
        loadHomeBeanInfo();
        LogCat.i("SplashActivity", "show time:" + sleep);
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                LogCat.i("SplashActivity", "startMain!!!!!!!");
                startMain();
            }
        }, sleep);
        LogCat.i("SplashActivity", "END!!!!!!!!show time:" + sleep);
    }


    private void startMain() {
        if(isStartMain)
            return;
        isStartMain=true;
        LogCat.i("SplashActivity", "显示广告skipskip YYYY");
        Intent intent = new Intent(SplashActivity.this, com.tgcyber.hotelmobile._activity.MainActivity.class);
        startActivity(intent);
        finish();
    }
    private boolean isStartMain=false;
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // isClickAd = false;
        try {
            ivBg.setImageBitmap(null);
            if (bg != null) {
                bg.recycle();
                bg = null;

            }
            System.gc();
            mHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //  isStartMain=false;
    }

    private HomePageBean bean;
    private long sleep =3000;
    private Handler mHandler = new Handler();
    private String AD_HOME = "adhome.jason";

    private boolean isRun=false;
    private void loadHomeBeanInfo() {

        if(isRun)
            return;
        sleep = SharedPreferencesUtils.getLong(this,
                Constants.SP_KEY_AD_SLEEP);
        if(sleep<100)
            sleep=2000;
        isRun=true;
        if(Constants.TOKEN==null)
        {
            Constants.TOKEN=SharedPreferencesUtils.getString(this,  Constants.SP_KEY_PUSH_TOKEN);
        }

        LogCat.i("SplashActivity", "loadHomeBeanInfo:" );
            RequestParams params = new RequestParams();
            //  String device_token = UmengRegistrar.getRegistrationId(this);
            if (((HotelApplication) getApplication()).isFirstRunApp())
            params.put("firstopen", "1");
            if (params != null) {
                LogCat.i("SplashActivity", "loadInfo =" + Constants.HOTEL_HOME_DATA + "?" + params.toString());
            }
            HotelClient.post(Constants.HOTEL_HOME_DATA, params,
                    new AsyncHttpResponseHandler() {

                        @Override
                        public void onFailure(int arg0, Header[] arg1,
                                              byte[] arg2, Throwable arg3) {
                            // TODO Auto-generated method stub
                            isRun=false;
                            if (isFinishing()) {
                                return;
                            }
                            LogCat.i("SplashActivity", "onFailure");
                        }

                        @Override
                        public void onSuccess(int arg0, Header[] arg1,
                                              byte[] arg2) {
                            LogCat.i("SplashActivity", "onSuccess");
                            if (isFinishing()) {
                                return;
                            }

                            String response = null;
                            try {
                                response = new String(arg2, "utf-8");
                                bean = HomePageBean.getBean(response);
                                MainActivity.homeBean=bean;
                               // if (((HotelApplication) getApplication()).isFirstRunApp())

                                if (bean != null && bean.configItem != null
                                        && bean.configItem.openimg!=null) {
                                   //boolean res=IOUtil.saveImg2Ad(arg2, Constants.AD_PATH  + AD_HOME);
                                  // if(res) SharedPreferencesUtils.saveLong(  SplashActivity.this,  Constants.SP_KEY_DATA_NEXTREQ,  System.currentTimeMillis() + 60* 1000*10);
                                    initAd(bean.configItem.openimg,bean.configItem.openimgtimes);
                                }
                                if (null != response && response.length() > 0) {
                                    ((HotelApplication) getApplication()).setIsFirstRunApp(false);
                                    LogCat.i("SplashActivity", response);
                                }else
                                    LogCat.i("SplashActivity", "MainActivity.homeBean=null");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            isRun=false;
                        }
                    });
    }
    private void initAd(String adSrc,long sleep) {
        if (isFinishing()) {
            return;
        }
        String imgType = adSrc.substring(adSrc.lastIndexOf("."));
        final String fileName = String.valueOf(adSrc.hashCode()) + imgType;
        LogCat.i("SplashActivity", "fileName = " + fileName+ " sleep=" + sleep);
        if(sleep<1)
            sleep=2000;
        SharedPreferencesUtils.saveLong(SplashActivity.this,
                Constants.SP_KEY_AD_SLEEP, sleep);
        // ivBg.setImageResource(R.drawable.splash);
        imageLoader.displayImage(adSrc,ivBg, itemBigImgOptions);
        // imageLoader.displayImage(adSrc, ivBg);
        LogCat.i("SplashActivity", "downloadPhoto displayImage");
    }


    protected DisplayImageOptions itemBigImgOptions;
}
