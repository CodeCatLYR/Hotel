package com.tgcyber.hotelmobile._activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.map2d.demo.util.ToastUtil;
import com.google.gson.Gson;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._bean.RespBean;
import com.tgcyber.hotelmobile._event.HXEvent;
import com.tgcyber.hotelmobile._fragment.HomeFragment;
import com.tgcyber.hotelmobile._fragment.ProfileFragment;
import com.tgcyber.hotelmobile._fragment.ShareFragment;
import com.tgcyber.hotelmobile._utils.LogCat;
import com.tgcyber.hotelmobile._widget.FixedSpeedScroller;
import com.tgcyber.hotelmobile.bean.BackAdItem;
import com.tgcyber.hotelmobile.bean.HomeItem;
import com.tgcyber.hotelmobile.bean.HomePageBean;
import com.tgcyber.hotelmobile.bean.HttpResult;
import com.tgcyber.hotelmobile.bean.UserInfo;
import com.tgcyber.hotelmobile.bean.VersionBean;
import com.tgcyber.hotelmobile.constants.Constants;
import com.tgcyber.hotelmobile.constants.KeyConstant;
import com.tgcyber.hotelmobile.constants.UrlConstant;
import com.tgcyber.hotelmobile.utils.HotelClient;
import com.tgcyber.hotelmobile.utils.HotelResponseListener;
import com.tgcyber.hotelmobile.utils.IOUtil;
import com.tgcyber.hotelmobile.utils.ImagetUtils;
import com.tgcyber.hotelmobile.utils.SharedPreferencesUtils;
import com.tgcyber.hotelmobile.utils.ToastUtils;
import com.tgcyber.hotelmobile.widget.NewVersionDialog;
import com.umeng.message.IUmengCallback;
import com.umeng.message.MessageSharedPrefs;
import com.umeng.message.PushAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends BaseActionBarActivity implements View.OnClickListener {

    private MyMessageListener mMessageListener;

    private ViewPager mMainVp;
    private LinearLayout mMainHome;
    public static HomePageBean homeBean;
    private View mMainWebview;
    private LinearLayout mMainShare;
    private LinearLayout mMainTele;

    private ImageView mIv_home;
    private TextView mTv_home;
    private ImageView mIv_webview;
    private TextView mTv_webview;
    private ImageView mIv_share;
    private TextView mTv_share;
    private ImageView mIv_tele;
    private TextView mTv_tele;

    private NDFragmentPagerAdapter ndFragmentPagerAdapter;
    private ViewPagerOnPagerListener viewPagerOnPagerListener;
    private List<Fragment> fragments;

    private Fragment fragment = null;
    private int keyBackDownTimes = 0;
    private int RESET_TIMES = 5;

    private PopupWindow popupWindow = null, popupPhone = null;
    public static boolean isStart = false;
    protected View list_error_status;      //刷新失败时可点击的部分
    private final int delayTime = 200;                   //延时
    private TextView mMsgBadgeTv;

    /**
     * 设置ViewPager的滑动速度
     */
    private void setViewPagerScrollSpeed() {
        try {
            Field mScroller = null;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(mMainVp.getContext());
            mScroller.set(mMainVp, scroller);
        } catch (Exception e) {
        }
    }


    private void assignViews() {
        LogCat.i("MainActivity", "assignViews()");
        mMainVp = (ViewPager) findViewById(R.id.main_vp);
        // setViewPagerScrollSpeed( );
        mMainHome = (LinearLayout) findViewById(R.id.main_home);
        mMainWebview = findViewById(R.id.main_webview);
        mMainShare = (LinearLayout) findViewById(R.id.main_share);
        mMainTele = (LinearLayout) findViewById(R.id.main_tele);

        mIv_home = (ImageView) mMainHome.findViewById(R.id.iv_home);
        mTv_home = (TextView) mMainHome.findViewById(R.id.tv_home);
        mIv_webview = (ImageView) mMainWebview.findViewById(R.id.iv_webview);
        mTv_webview = (TextView) mMainWebview.findViewById(R.id.tv_webview);
        mIv_share = (ImageView) mMainShare.findViewById(R.id.iv_share);
        mTv_share = (TextView) mMainShare.findViewById(R.id.tv_share);
        mIv_tele = (ImageView) mMainTele.findViewById(R.id.iv_tele);
        mTv_tele = (TextView) mMainTele.findViewById(R.id.tv_tele);
        list_error_status = findViewById(R.id.ll_error);
        list_error_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadHomeBeanInfo();
                    }
                }, delayTime);
            }
        });
        mMainHome.setOnClickListener(this);
        //  mMainCommunity.setOnClickListener(this);
        mMainWebview.setOnClickListener(this);
        mMainShare.setOnClickListener(this);
        mMainTele.setOnClickListener(this);

        mIv_home.setSelected(true);
        mTv_home.setTextColor(getResources().getColor(R.color.nd_homepage_bottom_text_blue));

        mMsgBadgeTv = (TextView) findViewById(R.id.profile_msg_badger);
    }

    @Override
    int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        super.initView();
        EventBus.getDefault().register(this);
        assignViews();
        initViewPagerData();
        autoLogin();
        registerHxListener();
    }

    private void registerHxListener() {
        mMessageListener = new MyMessageListener();
        EMClient.getInstance().chatManager().addMessageListener(mMessageListener);
    }



    private void autoLogin() {
        String userJson = SharedPreferencesUtils.getString(this, KeyConstant.USER_DATA);
        if(!TextUtils.isEmpty(userJson)){
            Gson gson = new Gson();
            UserInfo userData = gson.fromJson(userJson, UserInfo.class);

            login(userData);
        }
    }

    private void login(final UserInfo userData) {
        if(userData == null){
            return;
        }

        RequestParams params = new RequestParams();
        params.add("username", userData.getUsername());
        params.add("password", userData.getPassword());

        String url = Constants.HOTEL_BASE_URL + UrlConstant.LOGIN_URL;
        HotelClient.get(url, params, new HotelResponseListener() {
            @Override
            public void onSuccess(HttpResult result) {

                HotelApplication.getInstance().saveUser(userData);
            }

            @Override
            public void onFailed(int status, HttpResult result) {
                showToast(result.info);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    private PushAgent mPushAgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            LogCat.i("MainActivity", "Oncreat()");

            //推送
            mPushAgent = PushAgent.getInstance(this);
            mPushAgent.onAppStart();
            // mPushAgent.setPushIntentServiceClass(MyPushIntentService.class);
            startPushService();
            boolean isFirstRunPermission = SharedPreferencesUtils.getBoolean(this, Constants.SP_KEY_FIRST_RUNPERMISSION, true);
            checkAppVersion();

            // setActionbarVisible(View.GONE);

            //          如想让你的app在android 6.0系统上也能运行的话，需要动态获取权限，没有权限的话分享sdk会出错，参考一下代码做动态获取权限,适配安卓6.0系统
            //          你需要最新的android.support.v4包，或者v13的包可也以//,Manifest.permission.CALL_PHONE Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_LOGS,
            if (Build.VERSION.SDK_INT >= 23 && isFirstRunPermission) {
                //Manifest.permission.READ_SMS,　String[] mPermissionList = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_LOGS,Manifest.permission.READ_PHONE_STATE, Manifest.permission.SET_DEBUG_APP,Manifest.permission.SYSTEM_ALERT_WINDOW,Manifest.permission.GET_ACCOUNTS,Manifest.permission.WRITE_APN_SETTINGS};
                String[] mPermissionList1 = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WAKE_LOCK, Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.GET_ACCOUNTS, Manifest.permission.SET_DEBUG_APP, Manifest.permission.CALL_PHONE, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_SMS, Manifest.permission.CAMERA, Manifest.permission.WRITE_APN_SETTINGS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CHANGE_NETWORK_STATE, Manifest.permission.WRITE_SETTINGS, Manifest.permission.READ_LOGS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS};
                {
                    ActivityCompat.requestPermissions(this, mPermissionList1, 151);
                }
                SharedPreferencesUtils.saveBoolean(this, Constants.SP_KEY_FIRST_RUNPERMISSION, false);
            }
            IOUtil.initAppDirectory(this, Constants.SP_NAME);
            initUpGPSService();
            isStart = true;
            downloadThreadBackAdv();
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (!hasPermission()) {
                    startActivityForResult(
                            new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                            MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
                }
            }*/
         /*   broadcastReceiver = new ApkInstallReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            registerReceiver(broadcastReceiver, filter);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean hasPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private class ClearCacheThread extends Thread {

        public ClearCacheThread() {
        }

        @Override
        public void run() {

            if (!IOUtil.cleanHistoryFile(Constants.APP_PATH) | !IOUtil.cleanWebViewCacheFile() | !IOUtil.cleanHistoryFile(Constants.APP_PATH_OLD)) {
                //to do
            }
            try {
                context.deleteDatabase("webview.db");
                context.deleteDatabase("webviewCache.db");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void clearCache() {
        new ClearCacheThread().start();
    }

    private void initFirstRun() {
        if (((HotelApplication) getApplication()).isFirstRunApp()) {
            ((HotelApplication) getApplication()).setIsFirstRunApp(true);
        }
    }

    private void initUpGPSService() {
        try {
            if (isServiceRunning()) {
                LogCat.i("MainActivity", "initUpGPSService isRunning!");
                return;
            }
            LogCat.i("MainActivity", "initUpGPSService Start!");
            Intent startIntent = new Intent(this, HotelUpGPSService.class);
            startService(startIntent);//启动服务
        } catch (Exception e) {
            ToastUtils.showMsg(this, e.getMessage());
        }
    }

    private boolean isServiceRunning() {
        LogCat.i("MainActivity", "isServiceRunning");
        try {
            ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if ("com.tgcyber.hotelmobile._activity.HotelUpGPSService".equals(service.service.getClassName())) {
                    LogCat.i("MainActivity", "isServiceRunning true");
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    protected void onInitViewFinish() {
    }

    /*    public class ApkInstallReceiver extends BroadcastReceiver {

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("MainActivity", "ACTION_DOWNLOAD_COMPLETE");
                if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    Log.d("MainActivity", "ACTION_DOWNLOAD_COMPLETE");
                    long downloadApkId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    long id = SpUtils.getInstance(context).getLong("downloadId", -1L);
                    if (downloadApkId == id) {
                        installApk(context, downloadApkId);
                    }
                }
            }


        }*/
    private void installApk(Context context, long downloadApkId) {

        Intent install = new Intent(Intent.ACTION_VIEW);
        DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadFileUri = dManager.getUriForDownloadedFile(downloadApkId);
        if (downloadFileUri != null) {
            LogCat.i("DownloadManager", downloadFileUri.toString());
            install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(install);
        } else {
            LogCat.i("DownloadManager", "下载失败");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onResume() {
        super.onResume();
        long nextReq = 0;
        nextReq = SharedPreferencesUtils.getLong(this,
                Constants.SP_KEY_DATA_NEXTREQ);

        long now = System.currentTimeMillis();

        if ((nextReq != 0 && (now - nextReq) > 1000 * 60 * 5) || homeBean == null || homeBean.configItem == null || homeBean.configItem.url == null) {
            LogCat.i("MainActivity", "onResume:");
            loadHomeBeanInfo();
        }

        sendRefreshMsgCountEvent();
    }

    private final String HotelSys = "com.tgcyber.hotelsys";

    private boolean checkSysteIsRun(boolean isShowMsg) {
        LogCat.i("MainActivity", "checkSysteIsRun:");
        boolean isAppRunning = false;
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);

            for (ActivityManager.RunningTaskInfo info : list) {
                if (info.topActivity.getPackageName().equals(HotelSys) || info.baseActivity.getPackageName().equals(HotelSys)) {
                    isAppRunning = true;
                    LogCat.i("MainActivity", "PASS:" + info.topActivity.getPackageName() + " info.baseActivity.getPackageName()=" + info.baseActivity.getPackageName());
                    break;
                }
                //else
                //    LogCat.i("MainActivity", info.topActivity.getPackageName() + " info.baseActivity.getPackageName()=" + info.baseActivity.getPackageName());
            }
            if (!isAppRunning) {
                if (isHaveApp(HotelSys)) {
                    PackageManager packageManager = getPackageManager();
                    Intent intent = packageManager.getLaunchIntentForPackage(HotelSys);
                    // startActivity(intent);
                    isAppRunning = true;
                } else {
                    if (isShowMsg)
                        ToastUtils.showMsg(context, R.string.no_hotelsys);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isAppRunning;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private int initCurrent = 0;
    private boolean isPass = true;//测试需要

    private void initViewPagerData() {
        try {
            if (mMainVp != null) {
                initCurrent = mMainVp.getCurrentItem();
            }
            if (!isPass) {
                homeBean = null;
            }

            setActionbarRightDrawable(R.drawable.menu, "home");
            if (homeBean != null && homeBean.configItem != null && homeBean.configItem.title != null) {
                if (fragments == null || ndFragmentPagerAdapter == null) {
                    fragments = new ArrayList<Fragment>();
                    fragments.add(HomeFragment.newInstance(null, null));
                    fragments.add(ShareFragment.newInstance(Constants.HOTEL_SHARE_DATA, -1));
                    fragments.add(ProfileFragment.newInstance());

                    ndFragmentPagerAdapter = new NDFragmentPagerAdapter(getSupportFragmentManager(), fragments);
                    viewPagerOnPagerListener = new ViewPagerOnPagerListener();

                    mMainVp.setAdapter(ndFragmentPagerAdapter);
                    mMainVp.setOffscreenPageLimit(1);
                    mMainVp.addOnPageChangeListener(viewPagerOnPagerListener);
                }

                //fragment = fragments.get(0);
                mMainVp.setCurrentItem(initCurrent);
                // autorefreshFragmentData(0);
                initActionBar();
                mMainVp.setVisibility(View.VISIBLE);
                list_error_status.setVisibility(View.GONE);
            } else {
                LogCat.i("MainActivity", "initViewPagerData:");
                try2loadDataFromLocal(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initActionBar() {
        setActionbarTitle(homeBean.configItem.title);
        setActionbarLeftText(homeBean.configItem.room, "room");
    }

    private boolean isRun = false;
    private long waitLoadDataTimeSmall = 1000 * 3;
    private final long MaxWaitTime = 1000 * 60 * 10;

    private void resetData() {
        errCount = 0;
    }

    private void loadHomeBeanInfo() {
        LogCat.i("MainActivity", "loadHomeBeanInfo:");
        if (isRun)
            return;
        isRun = true;
        if (!isNetworkAvailable()) {
            LogCat.i("MainActivity", "loadHomeBeanInfo:!isNetworkAvailable()");

            if (homeBean == null || homeBean.menus == null || homeBean.configItem == null) {
                isRun = false;
                mMainVp.setVisibility(View.GONE);
                list_error_status.setVisibility(View.VISIBLE);
            }
            showToast(R.string.network_error);

            return;
        }
        if (Constants.TOKEN == null) {
            Constants.TOKEN = SharedPreferencesUtils.getString(this, Constants.SP_KEY_PUSH_TOKEN);
        }

        RequestParams params = new RequestParams();
        if (((HotelApplication) getApplication()).isFirstRunApp())
            params.put("firstopen", "1");
        if (params != null) {
            LogCat.i("MainActivity", "loadInfo =" + Constants.HOTEL_HOME_DATA + "?" + params.toString());
        }
        HotelClient.post(Constants.HOTEL_HOME_DATA, params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onFailure(int arg0, Header[] arg1,
                                          byte[] arg2, Throwable arg3) {
                        // TODO Auto-generated method stub
                        isRun = false;
                        if (isFinishing()) {
                            return;
                        }
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                isRun = false;
                                loadHomeBeanInfo();
                            }
                        }, waitLoadDataTimeSmall);
                        waitLoadDataTimeSmall = waitLoadDataTimeSmall * 2;
                        if (waitLoadDataTimeSmall > MaxWaitTime / 2)
                            waitLoadDataTimeSmall = MaxWaitTime / 2;
                        LogCat.i("MainActivity", "onFailure");
                    }

                    @Override
                    public void onSuccess(int arg0, Header[] arg1,
                                          byte[] arg2) {
                        LogCat.i("MainActivity", "onSuccess");
                        if (isFinishing()) {
                            return;
                        }
                        String response = null;
                        try {
                            response = new String(arg2, "utf-8");
                            HomePageBean bean = HomePageBean.getBean(response);
                            if (bean != null
                                    && bean.configItem != null
                                    && bean.menus != null && bean.menus.size() > 0) {
                                if (bean.configItem.isshowlogview == 1) {
                                    Constants.logged = true;
                                    SharedPreferencesUtils.saveBoolean(getApplicationContext(), Constants.SP_KEY_SHOW_LOG, true);
                                } else {
                                    SharedPreferencesUtils.saveBoolean(getApplicationContext(), Constants.SP_KEY_SHOW_LOG, false);
                                    Constants.logged = false;
                                }
                                SharedPreferencesUtils.saveString(getApplicationContext(), Constants.SP_KEY_BASEURL, bean.configItem.url);
                                Constants.logged = true;
                                resetData();
                                ((HotelApplication) getApplication()).setIsFirstRunApp(false);
                                LogCat.i("MainActivity", response);
                                SharedPreferencesUtils.saveLong(MainActivity.this, Constants.SP_KEY_DATA_NEXTREQ, System.currentTimeMillis());
                                IOUtil.saveObject2Cache(bean, getKey());
                                homeBean = bean;
                                Message msg = new Message();
                                msg.what = 999;
                                mHandler.sendMessage(msg);
                                return;

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (homeBean == null || homeBean.menus == null || homeBean.configItem == null) {
                            isRun = false;
                            mMainVp.setVisibility(View.GONE);
                            list_error_status.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    protected String getKey() {
        return "com_tgcyber_hotelmobile_mainactivity";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_home: {
                mMainVp.setCurrentItem(0);
                break;
            }
            case R.id.main_share: {
                mMainVp.setCurrentItem(1);
                break;
            }

            case R.id.main_tele: {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                startActivity(intent);
                break;
            }

            case R.id.main_webview: {
                mMainVp.setCurrentItem(2);

//                if (homeBean == null || homeBean.menus == null || homeBean.menus.size() < 2)
//                    return;
//                Intent intent = new Intent(this, WebViewActivity.class);
//                //intent.putExtra("type", data.type);
//                intent.putExtra("url", homeBean.menus.get(2).value);
//                intent.putExtra("name", homeBean.menus.get(2).name);
//                startActivity(intent);
                break;
            }
        }
    }

    class NDFragmentPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> list;

        public NDFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.list = list;
        }

        @Override
        public Fragment getItem(int position) {
            return list == null ? null : list.get(position);
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }
    }

    class ViewPagerOnPagerListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            LogCat.i("MainActivity", "onPageScrolled:" + position);
            fragment = fragments.get(position);
            autorefreshFragmentData(position);
        }

        @Override
        public void onPageSelected(int position) {
            LogCat.i("MainActivity", "onPageSelected:" + position);
            setActionBarImage(position);
            selectBottomBarItem(position);
            fragment = fragments.get(position);
            autorefreshFragmentData(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /*

    这一步为了实现Fragment自动刷新，autoLoadData方法只会在第一次进入Fragment时才起作用（目前只有首页、发现页需要自动刷新）

    autoLoadData 方法在BaseListFragment中实现*/
    private void autorefreshFragmentData(int position) {
        int i = mMainVp.getCurrentItem();
        if (position == 1)         //分享
        {
            //   ((ShareFragment) fragment).initData();
        }
    }

    public void initMenuItem() {
        if (homeBean != null && homeBean.menus.size() > 3) {
            setActionbarLeftText(homeBean.configItem.room, "room");
            setActionbarTitle(homeBean.configItem.title);
            imageLoader.displayImage(homeBean.menus.get(0).clickimg, mIv_home, menuImgOptions);
            imageLoader.displayImage(homeBean.menus.get(1).img, mIv_share, menuImgOptions);
            imageLoader.displayImage(homeBean.menus.get(2).img, mIv_webview, menuImgOptions);
            imageLoader.displayImage(homeBean.menus.get(3).img, mIv_tele, menuImgOptions);
            mTv_home.setTextColor(getResources().getColor(R.color.nd_homepage_bottom_text_blue));
            mTv_webview.setTextColor(getResources().getColor(R.color.nd_homepage_bottom_text_gray));
            mTv_share.setTextColor(getResources().getColor(R.color.nd_homepage_bottom_text_gray));
            mTv_tele.setTextColor(getResources().getColor(R.color.nd_homepage_bottom_text_gray));
            mTv_home.setText(homeBean.menus.get(0).name);
            mTv_share.setText(homeBean.menus.get(1).name);
            mTv_webview.setText(homeBean.menus.get(2).name);
            mTv_tele.setText(homeBean.menus.get(3).name);
        }
    }

    public void checkIfLogin() {
         /*  LogCat.i("MainActivity", "checkIfLogin");

     boolean isLogined = getHotelApplication().isLogined();
        if (!isLogined) {
            login();
        }else*/
        //   ((HomeFragment) fragment).autoLoadData();

    }

    public static final int REQUEST_ACTIVITY_LOGIN = 9001;

    private void setActionBarImage(int position) {
        if (homeBean == null || homeBean.menus == null || homeBean.menus.size() < 2)
            return;
        switch (position) {
            case 1: {
                setActionbarLeftDrawable(-1);
                setActionbarTitle(homeBean.menus.get(1).name);
                setActionbarRightDrawable(R.drawable.camera, "camera");
                break;
            }
            case 0: {
                setActionbarLeftText(homeBean.configItem.room, "room");
                setActionbarTitle(homeBean.configItem.title);
                setActionbarRightDrawable(R.drawable.menu, "home");
                break;
            }

            case 2:{
                setActionbarLeftDrawable(-1);
                setActionbarRightDrawable(-1);
                setActionbarTitle("我的");
            }
        }
    }

    private boolean isFirstRunning = true;

    /*
        * 从本地读数据，如果本地可以读，就从本地拿数据（isRefresh：是否点击刷新，刷新后需要归位）
        */
    protected void try2loadDataFromLocal(boolean isRefresh) {
        if (isFirstRunning || !isRefresh)
            try {
                LogCat.i("MainActivity", "try2loadDataFromLocal从sd卡读");
                homeBean = (HomePageBean) IOUtil.readObjectFromCache(getKey());
                isFirstRunning = false;
            } catch (Exception e) {
                homeBean = null;
            }
        if (homeBean != null && !isRefresh) {
            LogCat.i("MainActivity", "try2loadDataFromLocal从sd卡读bean homeBean != null");
            Constants.HOTEL_BASE_URL = homeBean.configItem.url;
            Message msg = new Message();
            msg.what = 999;
            mHandler.sendMessage(msg);
            //loadMenuInfo(isRefresh);
        } else {
            LogCat.i("MainActivity", "try2loadDataFromLocal从sd卡读bean但是bean为空");
            loadHomeBeanInfo();
        }

    }

    private void selectBottomBarItem(int position) {
        clearBottomBarAllStatus();
        LogCat.i("MainActivity", "selectBottomBarItem:" + position);
        switch (position) {
            case 0: {
                mIv_home.setImageResource(R.drawable.home_hover);
                mTv_home.setTextColor(getResources().getColor(R.color.nd_homepage_bottom_text_blue));
                if (homeBean != null && homeBean.menus.size() > 3) {
                    imageLoader.displayImage(homeBean.menus.get(0).clickimg, mIv_home, menuImgOptions);
                }
                break;
            }
            case 2: {
                mIv_webview.setImageResource(R.drawable.browser_hover);
                mTv_webview.setTextColor(getResources().getColor(R.color.nd_homepage_bottom_text_blue));
                if (homeBean != null && homeBean.menus.size() > 3) {
                    imageLoader.displayImage(homeBean.menus.get(2).clickimg, mIv_webview, menuImgOptions);
                }
                break;
            }
            case 1: {
                mIv_share.setImageResource(R.drawable.dynamic_hover);
                mTv_share.setTextColor(getResources().getColor(R.color.nd_homepage_bottom_text_blue));
                if (homeBean != null && homeBean.menus.size() > 3) {
                    imageLoader.displayImage(homeBean.menus.get(1).clickimg, mIv_share, menuImgOptions);
                }
                break;
            }
            case 3: {
                mIv_tele.setImageResource(R.drawable.phone_hover);
                mTv_tele.setTextColor(getResources().getColor(R.color.nd_homepage_bottom_text_blue));
                if (homeBean != null && homeBean.menus.size() > 3) {
                    imageLoader.displayImage(homeBean.menus.get(3).clickimg, mIv_tele, menuImgOptions);
                }
                break;
            }
        }
    }

    private void clearBottomBarAllStatus() {
        mIv_home.setImageResource(R.drawable.home);
        // mIv_community.setImageResource(R.drawable.icon50_concern);
        mIv_webview.setImageResource(R.drawable.browse);
        mIv_share.setImageResource(R.drawable.dynamic);
        mIv_tele.setImageResource(R.drawable.phone);

        mTv_home.setTextColor(getResources().getColor(R.color.nd_homepage_bottom_text_gray));
        //  mTv_community.setTextColor(getResources().getColor(R.color.nd_homepage_bottom_text_gray));
        mTv_webview.setTextColor(getResources().getColor(R.color.nd_homepage_bottom_text_gray));
        mTv_share.setTextColor(getResources().getColor(R.color.nd_homepage_bottom_text_gray));
        mTv_tele.setTextColor(getResources().getColor(R.color.nd_homepage_bottom_text_gray));
        if (homeBean != null && homeBean.menus != null && homeBean.menus.size() > 3) {
            imageLoader.displayImage(homeBean.menus.get(0).img, mIv_home, menuImgOptions);
            imageLoader.displayImage(homeBean.menus.get(1).img, mIv_share, menuImgOptions);
            imageLoader.displayImage(homeBean.menus.get(2).img, mIv_webview, menuImgOptions);
            imageLoader.displayImage(homeBean.menus.get(3).img, mIv_tele, menuImgOptions);
        }
    }

    /**
     * 开启推送服务
     */

    private void startPushService() {
        //		mPushAgent.setLocationInterval(600);  //若不设置,默认频率为600秒/次
        boolean acceptArticlePush = getHotelApplication().getArticlePushStatus();
        LogCat.i("MainActivity", "acceptArticlePush = " + acceptArticlePush + ":" + Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        if (true) {
           /* mPushAgent.enable(new IUmengRegisterCallback() {
                @Override
                public void onRegistered(String s) {
                    if(s!=null&&s.length()>8)
                    {
                        Constants.TOKEN=s;
                        SharedPreferencesUtils.saveString(MainActivity.this,Constants.SP_KEY_PUSH_TOKEN, s);

                    }
                   // Toast.makeText(MainActivity.this,"A推送TOKEN="+Constants.TOKEN, Toast.LENGTH_LONG).show();
                    LogCat.i("MainActivity", "acceptArticlePush device_token s= " + s);
                }
            });*/
            mPushAgent.enable(new IUmengCallback() {
                @Override
                public void onSuccess() {
                    MessageSharedPrefs.getInstance(MainActivity.this).setIsEnabled(true);
                    ((HotelApplication) MainActivity.this.getApplication()).setArticlePushStatus(true);
                }

                @Override
                public void onFailure(String s, String s1) {
                    LogCat.i("MainActivity", "startPushService()--enable--onFailure:" + "s=" + s + ", s1=" + s1);
                }
            });
            String device_token = mPushAgent.getRegistrationId();
            if (device_token != null && device_token.length() > 5) {
                Constants.TOKEN = device_token;
                SharedPreferencesUtils.saveString(MainActivity.this, Constants.SP_KEY_PUSH_TOKEN, device_token);
                LogCat.i("MainActivity", "acceptArticlePush device_token= " + device_token);
            }
            //  Toast.makeText(MainActivity.this,"B推送TOKEN="+Constants.TOKEN, Toast.LENGTH_LONG).show();

        } else {
            mPushAgent.disable(new IUmengCallback() {
                @Override
                public void onSuccess() {
                    ((HotelApplication) MainActivity.this.getApplication()).setArticlePushStatus(false);
                }

                @Override
                public void onFailure(String s, String s1) {
                    LogCat.i("MainActivity", "startPushService()--disable--onFailure:" + "s=" + s + ", s1=" + s1);
                }
            });
        }
    }

    /**
     * 更新用户信息
     */
    //private boolean isLogined = false;
    @Override
    public void finish() {

        super.finish();
    }

    //private ApkInstallReceiver broadcastReceiver = null;
    @Override
    protected void onDestroy() {
        isStart = false;
        if (timer != null) {
            timer.cancel();
        }
       /* if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }*/
        mHandler.removeCallbacksAndMessages(null);

        EMClient.getInstance().chatManager().removeMessageListener(mMessageListener);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private int errCount = 0;
    private Handler mHandler = new Handler() {

        public void dispatchMessage(android.os.Message msg) {
            if (msg.what == RESET_TIMES) {
                keyBackDownTimes = 0;
            } else if (msg.what == 12) {
                cleanCallLog();
            } else if (msg.what == 99) {
                loadBackAdv();
            } else if (msg.what == 100) {
                if (bgadvs == null || errCount >= 10)
                    return;
                new Thread() {
                    @Override
                    public void run() {
                        // List<File> a=new ArrayList<File>();
                        ArrayList<String> a = new ArrayList<String>();
                        String url, imgType, fileName;
                        File file;
                        for (int i = 0; i < bgadvs.size(); i++) {
                            url = bgadvs.get(i).img;
                            imgType = url.substring(url.lastIndexOf("."));
                            fileName = String.valueOf(url.hashCode()) + imgType;
                            file = new File(Constants.DOWNLOAD_PATH + fileName);
                            if (file.exists()) {
                                LogCat.i("MainActivity", "file.length():" + file.length() + " bgadvs.get(i).size:" + bgadvs.get(i).size);
                                ;
                                if (file.length() == bgadvs.get(i).size) {
                                    a.add(Constants.DOWNLOAD_PATH + fileName);
                                } else {
                                    file.delete();
                                    errCount++;
                                    mHandler.sendEmptyMessageDelayed(100, 1000 * 30);
                                    break;
                                }
                            }

                        }
                        if (a != null && a.size() == bgadvs.size() && checkSysteIsRun(false)) {
                            LogCat.i("MainActivity", " Support.setLockScreenBackground((File[]) a.toArray()");
                            ; //showToast(R.string.str_file_exist);
                            try {

                                //发送广播     private static final String ACTION = "com.tgcyber.hotelsys.broadcast";
                                // String broadcastIntent = "com.tgcyber.hotelsys.broadcast";//自己自定义
                                errCount = 0;
                                Intent intent = new Intent(ACTION);
                                intent.putExtra("type", "BACKGROUND");
                                intent.putStringArrayListExtra("filelist", a);
                                MainActivity.this.sendBroadcast(intent);
                                //Support.setLockScreenBackground( (File[]) a.toArray(new File[a.size()]));
                            } catch (Exception e) {
                                Message msg = new Message();
                                msg.what = 101;
                                msg.obj = getResources().getString(R.string.str_wifi_err);//+"err:"+e.getMessage()+" msg:"+a.toString();
                                mHandler.sendMessage(msg);

                                //showToast(getResources().getString(R.string.str_wifi_err)+"err:"+e.getMessage()+" msg:"+a.toString());
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();

            } else if (msg.what == 101) {
                String m = (String) msg.obj;
                Toast.makeText(MainActivity.this, m, Toast.LENGTH_LONG).show();
            } else if (msg.what == 999) {
                isPass = true;
                initViewPagerData();
                if (fragments != null && fragments.size() > 0)
                    ((HomeFragment) fragments.get(0)).setViewData(homeBean);
                isRun = false;
                LogCat.i("MainActivity", "重新初始化界面");
                // Toast.makeText(MainActivity.this, "重新初始化界面", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        // LogCat.i("MainActivity", "onKeyDown()1");
        if (keyCode == KeyEvent.KEYCODE_BACK) {//||keyCode==KeyEvent.KEYCODE_HOME
            {
                LogCat.i("MainActivity", "onKeyDown()2");
              /*  keyBackDownTimes++;
                if (keyBackDownTimes == 1) {
                    LogCat.i("MainActivity", "onKeyDown()3");
                    Toast.makeText(getApplicationContext(),
                            R.string.str_finish_if_try_again, Toast.LENGTH_SHORT).show();
                    mHandler.sendEmptyMessageDelayed(RESET_TIMES, 5000);

                } else {
                    LogCat.i("MainActivity", "onKeyDown()4");
                    keyBackDownTimes = 0;
                    finish();
                }*/
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /*   @Override
       public void onAttachedToWindow() {
           System.out.println("Page01 -->onAttachedToWindow");
           try {
               getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
               super.onAttachedToWindow();
           }catch(Exception e)
           {
               e.printStackTrace();
           }
       }*/
    @Override
    protected void onActionbarLeftClick(View v) {
        if (v.getTag() != null && v.getTag().equals("back")) {

            LogCat.i("MainActivity", "onActionbarLeftClick:searchsearch_home");
        } else if (v.getTag() != null && v.getTag().equals("room")) {
            if (homeBean == null || homeBean.configItem == null || homeBean.configItem.hotelurl == null)
                return;
            if (homeBean.configItem.urltype.equals("web")) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(homeBean.configItem.hotelurl);
                intent.setData(content_url);
                startActivity(intent);
            } else if (homeBean.configItem.urltype.equals("webview")) {
                Intent intent = new Intent(this, WebViewActivity.class);
                //intent.putExtra("type", data.type);
                intent.putExtra("url", homeBean.configItem.hotelurl);
                intent.putExtra("name", homeBean.configItem.room);
                startActivity(intent);
            }

            LogCat.i("MainActivity", "onActionbarLeftClick:searchsearch_home");
        } else {
            //  showToast("R.string.activity_main_actionbar_left");
        }
    }

    @Override
    protected void onActionbarRightClick(View v) {
        super.onActionbarRightClick(v);
        LogCat.i("MainActivity", "onActionbarRightClick:" + mMainVp.getCurrentItem());
        if (v.getTag().equals("search_follow")) {
            LogCat.i("MainActivity", "onActionbarRightClick:searchsearch_follow");
            //startSearchActivity("search_follow");
        } else if (v.getTag().equals("pager_share")) {
            LogCat.i("MainActivity", "onActionbarRightClick:searchpager_share");
            //startShare();
        } else if (v.getTag().equals("home")) {
            initPopupWindow(v);
        } else if (v.getTag().equals("mail")) {
            setActionbarRightDrawable(R.drawable.icon50_notice);
        } else if (v.getTag().equals("camera")) {
            ShareFragment shareFragment = (ShareFragment) fragments.get(1);
            if (shareFragment != null) {
                shareFragment.initCurrentShareUrl();
                startCamera();
            }

        }
    }

    private void goAppDetailSettingIntent() {
        Toast.makeText(this, R.string.str_camera_msg, Toast.LENGTH_LONG).show();

        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        try {
            context.startActivity(localIntent);
        } catch (Exception e) {
            Toast.makeText(this, R.string.str_camera_can_not_go, Toast.LENGTH_LONG).show();
        }
    }

    private void startCamera() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int state = checkSelfPermission(Manifest.permission.CAMERA);
            LogCat.i("MainActivity", "CAMERA权限 state=" + state);
            if (state != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                LogCat.i("MainActivity", "申请CAMERA权限");
                if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    showMessageOKCancel(getResources().getString(R.string.str_camera_msg),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    LogCat.i("MainActivity", "申请CAMERA权限B");
                                                           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                                requestPermissions(new String[] {Manifest.permission.CAMERA},
                                                                        CAMERA_REQUEST_CODE);
                                                            }*/
                                    Toast.makeText(MainActivity.this, R.string.str_camera_can_not_use, Toast.LENGTH_LONG).show();
                                    // Toast.makeText(MainActivity.this,R.string.str_camera_msg, Toast.LENGTH_LONG).show();
                                    goAppDetailSettingIntent();
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        CAMERA_REQUEST_CODE);
            } else {
                photograph();
            }
        } else
            photograph();
    }

    //相片路径
    private static final String CAMERA_FILE_PATH = Constants.CAMERA_PATH + "myPhoto.jpg";
    private File uploadFile = new File(Constants.CAMERA_PATH, "myPhoto.upload");
    private static final int REQUEST_PORTRAIT_CAMERA = 3;

    /**
     * 开启照相
     */
    private final int CAMERA_REQUEST_CODE = 130;

    private void photograph() {

        LogCat.i("MainActivity", "photograph");
        if (haveSdCard() && IOUtil.createCameraPath()) {
            File file = new File(CAMERA_FILE_PATH);
            Uri fileUri = Uri.fromFile(file);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, REQUEST_PORTRAIT_CAMERA);
        } else {
            showToast(R.string.str_sdcard_can_not_use);
        }
    }

    public static boolean haveSdCard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(new ContextThemeWrapper(this,
                R.style.AlertDialogCustom))
                .setMessage(message)
                .setPositiveButton(R.string.string_bt_setting, okListener)
                .setNegativeButton(R.string.str_bt_cancel, null)
                .create()
                .show();
    }

    private void showMessageOK(int title, String message, int okStr, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(new ContextThemeWrapper(this,
                R.style.AlertDialogCustom))
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(okStr, okListener)
                .create()
                .show();
    }

    private void showMessageOKCancel(int title, String message, int okStr, DialogInterface.OnClickListener okListener, int cancelStr, DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(new ContextThemeWrapper(this,
                R.style.AlertDialogCustom))
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(okStr, okListener)
                .setNegativeButton(cancelStr, null)
                .create()
                .show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }

    ;

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    public boolean isHaveApp(String app) {
        PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            String pn;
            for (int i = 0; i < pinfo.size(); i++) {
                pn = pinfo.get(i).packageName;
                if (pn.equals(app)) {
                    return true;
                }
            }
        } else
            return true;

        return false;
    }

    private void startHelp() {
        if (homeBean == null || homeBean.configItem == null || homeBean.configItem.help == null)
            return;
        Intent intent = new Intent(this, WebViewActivity.class);
        //intent.putExtra("type", data.type);
        intent.putExtra("src", "push");
        intent.putExtra("url", homeBean.configItem.help);
        intent.putExtra("name", getResources().getString(R.string.string_title_help));
        startActivity(intent);
    }

    private void startBrowser() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri url = Uri.parse("about:blank");
        intent.setData(url);
        startActivity(intent);
    }

    public void initPopupWindow(View v) {

        if (popupWindow == null) {
            View popupWindow_view = LayoutInflater.from(this).inflate(R.layout.popup_open_menu, null);
            View tv_popup_browser = (View) popupWindow_view.findViewById(R.id.tv_popup_browser);
            View tv_popup_open_wifi = (View) popupWindow_view.findViewById(R.id.tv_popup_open_wifi);
            View tv_popup_help = (View) popupWindow_view.findViewById(R.id.tv_popup_help);
            // View tv_popup_weixin = (View) popupWindow_view.findViewById(R.id.tv_popup_weixin);
            //  View tv_popup_ar = (View) popupWindow_view.findViewById(R.id.tv_popup_ar);
            //  View tv_popup_copylink = (View) popupWindow_view.findViewById(R.id.tv_popup_copylink);
            //    View tv_popup_open_with_facebook = (View) popupWindow_view.findViewById(R.id.tv_popup_open_with_facebook);
            //     View tv_popup_open_with_instagram = (View) popupWindow_view.findViewById(R.id.tv_popup_open_with_instagram);
            View tv_popup_open_with_reduction = (View) popupWindow_view.findViewById(R.id.tv_popup_open_with_reduction);

            tv_popup_browser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startBrowser();
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                }
            });
            tv_popup_open_wifi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                    if (checkSysteIsRun(true))
                        openWifi();
                }
            });

            tv_popup_help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startHelp();
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                }
            });

         /*   tv_popup_weixin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String weixin = "com.tencent.mm";
                    if (isHaveApp(weixin)){
                        PackageManager packageManager = getPackageManager();
                        Intent intent = new Intent();
                        intent = packageManager.getLaunchIntentForPackage(weixin);
                        startActivity(intent);
                    } else {
                        ToastUtils.showMsg(context, R.string.no_weixin);
                    }


                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                }
            });
            tv_popup_ar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String ar = "com.Test.dasanbademo";
                    if (isHaveApp(ar)){
                        PackageManager packageManager = getPackageManager();
                        Intent intent = new Intent();
                        intent = packageManager.getLaunchIntentForPackage(ar);
                        startActivity(intent);
                    } else {
                        ToastUtils.showMsg(context, R.string.no_ar);
                    }


                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                }
            });
            tv_popup_copylink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String sina = "com.sina.weibo";
                    if (isHaveApp(sina)){
                        PackageManager packageManager = getPackageManager();
                        Intent intent = new Intent();
                        intent = packageManager.getLaunchIntentForPackage(sina);
                        startActivity(intent);
                    } else {
                        ToastUtils.showMsg(context, R.string.no_sina);
                    }

                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                }
            });

            tv_popup_open_with_facebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String facebook = "com.facebook.katana";
                    if (isHaveApp(facebook)){
                        PackageManager packageManager = getPackageManager();
                        Intent intent = packageManager.getLaunchIntentForPackage(facebook);
                        startActivity(intent);
                    } else {
                        ToastUtils.showMsg(context, R.string.no_facebook);
                    }

                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                }
            });

            tv_popup_open_with_instagram.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String instagram = "com.instagram.android";
                    if (isHaveApp(instagram)){
                        PackageManager packageManager = getPackageManager();
                        Intent intent = packageManager.getLaunchIntentForPackage(instagram);
                        startActivity(intent);
                    } else {
                        ToastUtils.showMsg(context, R.string.no_instagram);
                    }

                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                }
            });*/

            tv_popup_open_with_reduction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                    //if(checkSysteIsRun(true))
                    resetSystem();
                }
            });


            popupWindow = new PopupWindow(popupWindow_view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setOutsideTouchable(true);
        }

        if (popupWindow != null)
            popupWindow.showAsDropDown(v, 0, 0);
    }

    private void cleanCallLog() {
        ContentResolver resolver = getContentResolver();
        resolver.delete(CallLog.Calls.CONTENT_URI, null, null);
    }

    private void resetSystem() {
        showMessageOKCancel(R.string.str_alert_title2, getResources().getString(R.string.str_reset_msg), R.string.str_bt_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                postReset();
                LogCat.i("MainActivity", "resetSystem");
            }
        }, R.string.str_bt_cancel, null);

    }

    public static final String ACTION = "com.tgcyber.hotelsys.broadcast";

    private void openWifi() {
        if (homeBean == null || homeBean.configItem == null || homeBean.configItem.wifi_ssid == null)
            return;

        new Thread() {
            @Override
            public void run() {
                int res = 0;
                Object s[] = new String[]{homeBean.configItem.wifi_ssid, homeBean.configItem.wifi_password, "" + homeBean.configItem.wifi_openduration / 60};
                try {
                    //发送广播
                    // String broadcastIntent = "com.tgcyber.hotelsys.broadcast";//自己自定义
                    Intent intent = new Intent(ACTION);
                    intent.putExtra("type", "WIFI");
                    intent.putExtra("wifi_openduration", homeBean.configItem.wifi_openduration);
                    intent.putExtra("wifi_bandwidth", homeBean.configItem.wifi_bandwidth);
                    intent.putExtra("wifi_password", homeBean.configItem.wifi_password);
                    intent.putExtra("wifi_ssid", homeBean.configItem.wifi_ssid);
                    intent.putExtra("wifi_stay", s);
                    MainActivity.this.sendBroadcast(intent);
                    //res = Support.openWifiHotspot(homeBean.configItem.wifi_openduration, homeBean.configItem.wifi_bandwidth, homeBean.configItem.wifi_password, homeBean.configItem.wifi_ssid);
                    // if(res==1)showMessageOK(R.string.str_alert_title,getResources().getString(R.string.str_wifi_msg,s),R.string.string_bt_see,null);
                    // else   showToast(R.string.str_wifi_err1);
                } catch (Exception e) {
                    Message msg = new Message();
                    msg.what = 101;
                    msg.obj = getResources().getString(R.string.str_wifi_err);//+"err:"+e.getMessage()+" msg:"+getResources().getString(R.string.str_wifi_msg,s);
                    mHandler.sendMessage(msg);
                    //  showMessageOK(getResources().getString(R.string.str_wifi_msg,s),null);
                    showToast(getResources().getString(R.string.str_wifi_err) + "err:" + e.getMessage() + " msg:" + getResources().getString(R.string.str_wifi_msg, s));
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private View popupPhoneView;

    public void initPopupView(final List<HomeItem> homeItemList) {
        if (homeItemList == null || homeItemList.size() == 0) {
            LogCat.i("MainActivity", "initPopupView  data is null!");
            return;
        }
        popupPhoneView = LayoutInflater.from(this).inflate(R.layout.popup_open_phone, null);
        TextView polic = (TextView) popupPhoneView.findViewById(R.id.tv_popup_polic);
        TextView hospital = (TextView) popupPhoneView.findViewById(R.id.tv_popup_hospital);
        TextView cs = (TextView) popupPhoneView.findViewById(R.id.tv_popup_cs);
        ImageView iv_popup_polic = (ImageView) popupPhoneView.findViewById(R.id.iv_popup_polic);
        ImageView iv_popup_hospital = (ImageView) popupPhoneView.findViewById(R.id.iv_popup_hospital);
        ImageView iv_popup_cs = (ImageView) popupPhoneView.findViewById(R.id.iv_popup_cs);
        LinearLayout ll_popup_polic = (LinearLayout) popupPhoneView.findViewById(R.id.ll_popup_polic);
        LinearLayout ll_popup_hospital = (LinearLayout) popupPhoneView.findViewById(R.id.ll_popup_hospital);
        LinearLayout ll_popup_cs = (LinearLayout) popupPhoneView.findViewById(R.id.ll_popup_cs);


        if (homeItemList.get(0) != null && homeItemList.get(0).name != null) {
            polic.setText(homeItemList.get(0).name);
        }
        if (homeItemList.get(1) != null && homeItemList.get(1).name != null) {
            hospital.setText(homeItemList.get(1).name);
        }
        if (homeItemList.get(2) != null && homeItemList.get(2).name != null) {
            cs.setText(homeItemList.get(2).name);
        }

        if (homeItemList.get(0) != null && homeItemList.get(0).img != null) {
            imageLoader.displayImage(Constants.HOTEL_BASE_URL + homeItemList.get(0).img, iv_popup_polic, headImgOptions);
        }
        if (homeItemList.get(1) != null && homeItemList.get(1).img != null) {
            imageLoader.displayImage(Constants.HOTEL_BASE_URL + homeItemList.get(1).img, iv_popup_hospital, headImgOptions);
        }
        if (homeItemList.get(2) != null && homeItemList.get(2).img != null) {
            imageLoader.displayImage(Constants.HOTEL_BASE_URL + homeItemList.get(2).img, iv_popup_cs, headImgOptions);
        }

        ll_popup_polic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCallPhone(homeItemList.get(0).value);
                if (popupPhone != null && popupPhone.isShowing()) {
                    popupPhone.dismiss();
                }
            }
        });

        ll_popup_hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCallPhone(homeItemList.get(1).value);
                if (popupPhone != null && popupPhone.isShowing()) {
                    popupPhone.dismiss();
                }
            }
        });

        ll_popup_cs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCallPhone(homeItemList.get(2).value);
                if (popupPhone != null && popupPhone.isShowing()) {
                    popupPhone.dismiss();
                }
            }
        });

        popupPhone = new PopupWindow(popupPhoneView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupPhone.setBackgroundDrawable(new BitmapDrawable());
        popupPhone.setOutsideTouchable(true);

    }

    public void initPopupPhone(View v) {
        LogCat.i("MainActivity", "initPopupPhone:");
        if (popupPhone == null) {
            List<HomeItem> homeItemList = ((HomeItem) v.getTag()).homeItemList;
            initPopupView(homeItemList);
        }
        if (popupPhone == null)
            return;
        popupPhoneView.measure(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        requiredHeight = popupPhoneView.getMeasuredHeight() - 10;
        requiredWidth = popupPhoneView.getMeasuredHeight() - 10;
        int[] location = new int[2];
        v.getLocationOnScreen(location);


        popupPhone.showAtLocation(v, Gravity.NO_GRAVITY, location[0] - requiredWidth, location[1] - requiredHeight);
        LogCat.i("MainActivity", "showAtLocation  location[0]=" + location[0] + " location[1]=" + location[1] + " popupPhone.getHeight()=" + popupPhone.getHeight());
        //popupPhone.showAtLocation(v, Gravity.TOP, 0, 0);
        LogCat.i("MainActivity", "initPopupPhone:END");
    }

    int requiredHeight, requiredWidth;

    private void startCallPhone(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phone);
        intent.setData(data);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }


    private void onPushCountUpdate() {
        /*
         * NewPushImageView iv = (NewPushImageView)ivActionbarRight; int count =
		 * SharedPreferencesUtils.getInt(getApplicationContext(),
		 * Constants.SP_KEY_PUSH_COUNT); int count2 =
		 * SharedPreferencesUtils.getInt(getApplicationContext(),
		 * Constants.SP_KEY_MSG_COUNT); if(count == 0 && count2 == 0){
		 * iv.setPushFlag(false); }else{ iv.setPushFlag(true); }
		 */
       /* if (leftFragment != null) {
            leftFragment.updatePushInfoIfNeed();
        }*/
    }

    //    private int newspager_type = MainEvent.NO_LOGIN_MODEL;


    public static final int REQUEST_ACTIVITY = 539;
    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1101;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        LogCat.i("MainActivity", "onActivityrequestCode:" + requestCode + "------onActivityresultCode:" + resultCode);
        try {
            super.onActivityResult(requestCode, resultCode, data);


            if (requestCode == 1012) {
                try {
                    // LogCat.i("MainActivity",
                    // "pos="+data.getExtras().getInt("position")+" state="+data.getExtras().getString("isorder")+" requestCode:"+requestCode+"resultCode"+resultCode);
                    fragment.onActivityResult(requestCode, resultCode, data);
                    // super.onActivityResult(requestCode, resultCode, data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            } else if (REQUEST_PORTRAIT_CAMERA == requestCode
                    && resultCode == RESULT_OK) {
                onPortraitCamera();
            } else if (REQUEST_PORTRAIT_CROP == requestCode
                    && resultCode == RESULT_OK && data != null) {
                onPortraitCrop(data);
            } else if (REQUEST_ACTIVITY == requestCode) {

                return;
            } else if (requestCode == MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS) {
                if (!hasPermission()) {
                    //若用户未开启权限，则引导用户开启“Apps with usage access”权限
                    LogCat.i("MainActivity", "Settings.ACTION_USAGE_ACCESS_SETTINGS");
                    startActivityForResult(
                            new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                            MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
                }
            }


            LogCat.i("MainActivity", "***" + requestCode + " resultCode:"
                    + resultCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 剪切图片返回
     *
     * @param intent
     */
    private boolean isUploadingPortrait = false;

    @SuppressWarnings("unchecked")
    private void onPortraitCrop(Intent intent) {

        if (intent == null) {
            return;
        }
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }
        final Bitmap photo = bundle.getParcelable("data");
        if (photo == null) {
            return;
        }
        isUploadingPortrait = true;
        showProgressDialog(R.string.str_dialog_reseting_portrait);
        //
        new AsyncTask() {

            protected void onPostExecute(Object result) {
                if (isFinishing()) {
                    return;
                }
                uploadPortrait();
            }

            @Override
            protected Object doInBackground(Object... arg0) {
                // TODO Auto-generated method stub
                saveMyBitmap(photo);
                return null;
            }
        }.execute();
    }

    /**
     * 上传头像
     */
    private void uploadPortrait() {
        closeProgressDialog();
        ToastUtil.show(context, getString(R.string.photo));
    }

    /**
     * 保存最后生成的图片到本地，用来上传
     *
     * @param mBitmap
     */
    public void saveMyBitmap(Bitmap mBitmap) {

        FileOutputStream fOut = null;
        try {
            if (uploadFile.exists()) {
                uploadFile.delete();
            }
            uploadFile.createNewFile();
            fOut = new FileOutputStream(uploadFile);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError oom) {

        } finally {
            if (fOut != null) {
                try {
                    fOut.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }


    /**
     * 拍图片返回
     */
    private void onPortraitCamera() {
        try {
            File file = new File(CAMERA_FILE_PATH);
            if (file == null || file.length() <= 0) {
                LogCat.i("MainActivity", "file == null");
                return;
            }
            zoomImage(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void zoomImage(final File file) {
        if (!haveSdCard()) {
            showToast(R.string.str_sdcard_can_not_use);
            return;
        }
        File cacheFile = new File(Environment.getExternalStorageDirectory(),
                Constants.CAMERA_PATH);
        if (cacheFile == null || file == null) {
            return;
        }
        final String cachePath = cacheFile.getAbsolutePath();
        new AsyncTask() {

            @Override
            protected Object doInBackground(Object... arg0) {
                // TODO Auto-generated method stub
                return ImagetUtils.bitmapCompress(cachePath,
                        file.getAbsolutePath());
            }

            protected void onPostExecute(Object result) {
                try {
                    String path = (String) result;
                    //裁剪
                    //                if (!isFinishing() && !isBlank(path)) {
                    //                    startPhotoZoom(path, 150);
                    //                }

                    //不裁剪
                    Intent intent = new Intent(MainActivity.this, ShareFriendActivity.class);
                    intent.putExtra("path", path);
                    ShareFragment shareFragment = (ShareFragment) fragments.get(1);
                    LogCat.i("MainActivity", "shareFragment.getCurrentShareUrl():" + shareFragment.getCurrentShareUrl());
                    if (shareFragment.getCurrentShareUrl() != null) {
                        intent.putExtra("sharelistname", shareFragment.getShareListName());
                        intent.putExtra("sharelistid", shareFragment.getShareListId());
                        intent.putExtra("shareid", shareFragment.getCurrentShareId());
                        intent.putExtra("sharename", shareFragment.getCurrentShareName());
                        intent.putExtra("uploadurl", shareFragment.getCurrentShareUrl());
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    ToastUtil.show(MainActivity.this, "Error:" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private static final int REQUEST_PORTRAIT_CROP = 5;

    private void startPhotoZoom(String newPath, int size) {
        // TODO Auto-generated method stub
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(Uri.fromFile(new File(newPath)), "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, REQUEST_PORTRAIT_CROP);
    }


    /**
     * 检查app最新版本
     */
    private void checkAppVersion() {
        LogCat.i("MainActivity", "checkAppVersion");
        /* if (VERSION.SDK_INT <= 13) return; */
        long lastCheckVersionTime = SharedPreferencesUtils
                .getLong(getApplicationContext(),
                        getString(R.string.check_version_time));
        long nowTime = System.currentTimeMillis();
        if (lastCheckVersionTime == 0) {
            // SharedPreferencesUtils.saveLong(getApplicationContext(),
            // getString(R.string.check_version_time), nowTime);
            loadVersionInfo();
        } else {
            if ((nowTime - lastCheckVersionTime) > Constants.CHECK_VERSION_UPDATE_INTERVAL) {
                loadVersionInfo();
            }
        }
    }

    private void loadVersionInfo() {
        LogCat.i("MainActivity", "loadVersionInfo");
        RequestParams params = new RequestParams();
        //if(homeBean==null||homeBean.configItem==null)
        //      return;
        //||homeBean.configItem.update_apk_url==null
        String url = "http://api.hotelmobile.top/apptest/appversion?v=118";
        HotelClient.post(url, params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                          Throwable arg3) {
                        // TODO Auto-generated method stub
                        LogCat.i("MainActivity", "checkAppVersion onFailure");
                    }

                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                        // TODO Auto-generated method stub
                        if (isFinishing()) {
                            return;
                        }
                        String response = null;
                        try {
                            response = new String(arg2, "utf-8");
                            LogCat.i("MainActivity", "checkAppVersion response=" + response);
                        } catch (Exception e) {

                        }
                        if (null != response && response.length() > 0) {
                            showVersionInfo(response);
                        }
                    }
                });
    }

    private void postReset() {
        LogCat.i("MainActivity", "postReset");
        if (homeBean == null || homeBean.configItem == null || homeBean.configItem.rest_url == null)
            return;
        LogCat.i("MainActivity", "postReset" + homeBean.configItem.rest_url);
        RequestParams params = new RequestParams();
        HotelClient.post(homeBean.configItem.rest_url, params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                          Throwable arg3) {
                        // TODO Auto-generated method stub
                        showToast(R.string.network_error);
                    }

                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                        // TODO Auto-generated method stub
                        if (isFinishing()) {
                            return;
                        }
                        String response = null;
                        try {
                            response = new String(arg2, "utf-8");
                            RespBean bean = RespBean.getBean(response);
                            LogCat.i("MainActivity", "postReset response=" + response);
                            if (null != bean && bean.status == 1) {
                                {
                                    cleanCallLog();
                                    clearCache();
                                    //调用系统内部ＲＥＳＥＴ数据
                                    /*Intent intent = new Intent(ACTION);
                                    intent.putExtra("type","RESET");
                                    MainActivity.this.sendBroadcast(intent);*/
                                    // Support.back2factory();
                                }
                            } else
                                showToast(R.string.reset_service_error);
                        } catch (Exception e) {
                            e.printStackTrace();
                            showToast(getResources().getString(R.string.reset_service_error) + "err:" + e.getMessage() + " msg:" + response);
                        }

                    }
                });
    }

    private void showVersionInfo(String versionJson) {
        LogCat.i("MainActivity", "versionJson = " + versionJson);
        long nowTime = System.currentTimeMillis();
        VersionBean bean = VersionBean.getBean(versionJson);
        if (bean != null) {
            try {
                /*int oldVersionCode = getPackageManager().getPackageInfo(
                        this.getPackageName(), 0).versionCode;
                int newVersionCode = bean.versionInt;
                if (newVersionCode > oldVersionCode)*/
                if (bean.status != null && bean.status.equals("1")) {
                    // 发现新版本
                    String versionDesc = bean.desc;
                    versionDesc = versionDesc.replace("|", "\n");
                    String apkDownloadUrl = bean.url;
                    SharedPreferencesUtils.saveString(getApplicationContext(),
                            getString(R.string.version_desc), versionDesc);
                    SharedPreferencesUtils.saveString(getApplicationContext(),
                            getString(R.string.apk_download_url),
                            apkDownloadUrl);
                    showNewVersionInfo(versionDesc, apkDownloadUrl);
                    SharedPreferencesUtils.saveLong(getApplicationContext(),
                            getString(R.string.check_version_time), nowTime);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showNewVersionInfo(String versionDescription,
                                    final String downloadUrl) {
        showNewVersionDialog(versionDescription, downloadUrl);
    }

    private void showNewVersionDialog(String versionDescription,
                                      final String downloadUrl) {
        final NewVersionDialog versionDialog = new NewVersionDialog(
                MainActivity.this, R.style.FullScreenDialog,
                R.layout.show_version_window);

        View.OnClickListener clickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                if (view.getId() == R.id.show_version_btn_confirm) {
                    downloadAPK(downloadUrl);
                }
                versionDialog.dismiss();
            }
        };
        versionDialog.setSubViewOnClickListener(clickListener);

        versionDialog.setTitle(getString(R.string.found_the_new_version));
        versionDialog.setMessage(versionDescription);
        versionDialog.show();
    }

    private void downloadAPK(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        this.startActivity(intent);
    }

    private Timer timer;

    protected void downloadThreadBackAdv() {
        LogCat.i("MainActivity", "downloadThreadBackAdv");
        timer = new Timer();
        MyTimerTask task = new MyTimerTask();
        timer.schedule(task, 30000, 60 * 1000 * 60);
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            LogCat.i("MainActivity", "MyTimerTask");
            mHandler.sendEmptyMessage(99);
        }
    }

    ;

    private void loadBackAdv() {

        LogCat.i("MainActivity", "loadBackAdv:");
        if (homeBean == null || homeBean.configItem == null || homeBean.configItem.bgadv_url == null)
            return;
        try {
            RequestParams params = new RequestParams();
            if (params != null) {
                LogCat.i("MainActivity", "loadInfo =" + homeBean.configItem.bgadv_url + "?" + params.toString());
            }
            HotelClient.post(homeBean.configItem.bgadv_url, params,
                    new AsyncHttpResponseHandler() {

                        @Override
                        public void onFailure(int arg0, Header[] arg1,
                                              byte[] arg2, Throwable arg3) {
                            if (isFinishing()) {
                                return;
                            }
                            LogCat.i("MainActivity", "loadBackAdv onFailure");
                        }

                        @Override
                        public void onSuccess(int arg0, Header[] arg1,
                                              byte[] arg2) {
                            LogCat.i("MainActivity", "loadBackAdv onSuccess");
                            if (isFinishing()) {
                                return;
                            }

                            String response = null;
                            try {
                                response = new String(arg2, "utf-8");
                                bgadvs = BackAdItem.getItem(response);
                                if (bgadvs != null && bgadvs.size() > 0) {
                                    for (int i = 0; i < bgadvs.size(); i++)
                                        download(bgadvs.get(i).img);
                                    mHandler.sendEmptyMessageDelayed(100, 1000 * 60 * 2);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<BackAdItem> bgadvs;

    private void download(String url) {
        LogCat.i("MainActivity", "download:" + url);

        try {
            String imgType = url.substring(url.lastIndexOf("."));
            final String fileName = String.valueOf(url.hashCode()) + imgType;
            if (IOUtil.fileISExists(Constants.DOWNLOAD_PATH + fileName)) {
                LogCat.i("MainActivity", "Alerady download:" + url + " path:" + Constants.DOWNLOAD_PATH + fileName);
                ; //showToast(R.string.str_file_exist);
            } else {
                String[] allowedContentTypes = new String[]{"image/png",
                        "image/jpeg", "image/gif"};
                HotelClient.get(url, null,
                        new BinaryHttpResponseHandler(allowedContentTypes) {

                            @Override
                            public void onStart() {
                                super.onStart();
                            }

                            @Override
                            public void onFailure(int arg0, Header[] arg1,
                                                  byte[] arg2, Throwable arg3) {
                                showToast(R.string.network_error);
                            }

                            @Override
                            public void onSuccess(int arg0, Header[] arg1,
                                                  final byte[] fileData) {
                                if (isFinishing()) {
                                    return;
                                }
                                if (null != fileData) {
                                    savePhoto(fileData, fileName);
                                }
                            }
                        });


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void savePhoto(final byte[] fileData,
                           final String fileName) {
        if (!IOUtil.checkSDCardAvailable()) {
            showToast(R.string.str_sdcard_can_not_use);
            return;
        }
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object... arg0) {
                IOUtil.saveImg2Download(fileData, Constants.DOWNLOAD_PATH
                        + fileName);
                return null;
            }

            @Override
            protected void onPostExecute(Object result) {
                super.onPostExecute(result);
                if (isFinishing()) {
                    return;
                }
                //showToast(getString(R.string.str_download_and_save, (Constants.DOWNLOAD_PATH + fileName)));
            }
        };
        task.execute();
    }


    private class MyMessageListener implements EMMessageListener{

        @Override
        public void onMessageReceived(List<EMMessage> list) {
            sendRefreshMsgCountEvent();
        }


        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {
        }

        @Override
        public void onMessageRead(List<EMMessage> list) {
        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {
        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {
        }
    }

    private void sendRefreshMsgCountEvent() {
        int unreadCount = EMClient.getInstance().chatManager().getUnreadMessageCount();
        EventBus.getDefault().post(new HXEvent(HXEvent.MSG_COUNT, unreadCount));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshMsgCount(HXEvent event){
        if(event.type == HXEvent.MSG_COUNT){
            if(event.unreadMsgCount > 0){
                mMsgBadgeTv.setVisibility(View.VISIBLE);
                mMsgBadgeTv.setText(String.valueOf(event.unreadMsgCount));
            }else{
                mMsgBadgeTv.setVisibility(View.GONE);
            }
        }
    }

}
