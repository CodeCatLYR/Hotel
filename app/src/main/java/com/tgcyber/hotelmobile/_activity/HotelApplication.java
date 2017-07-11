package com.tgcyber.hotelmobile._activity;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.tencent.smtt.sdk.QbSdk;
import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._utils.DeviceUtils;
import com.tgcyber.hotelmobile._utils.LogCat;
import com.tgcyber.hotelmobile._utils.StringUtil;
import com.tgcyber.hotelmobile.bean.UserInfo;
import com.tgcyber.hotelmobile.constants.Constants;
import com.tgcyber.hotelmobile.constants.KeyConstant;
import com.tgcyber.hotelmobile.push.MyPushIntentService;
import com.tgcyber.hotelmobile.utils.DemoHelper;
import com.tgcyber.hotelmobile.utils.SharedPreferencesUtils;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class HotelApplication extends Application {

    private static  String DISPLAY_WELCOME_KEY = "hotel_welcome_";
    private boolean isRightApplication = false;
    private static final String ARTICLE_PUSH_KEY = "article_push";
    private static final String ARTICLE_PUSH_SETTED = "article_push_setted";
    private PushAgent mPushAgent;
    public static HotelApplication application;
    private UserInfo user;
    private Gson mGson;


    public static HotelApplication getInstance(){
        return application;
    }

    @Override
    public void onCreate() {
        application=this;
        super.onCreate();

        mGson = new Gson();

        try {
            Constants.DEVICE_TOKEN=DeviceUtils.getDeviceID(this);
            Constants.APP_CONTEXT = getApplicationContext();
            Constants.versionName=getPackageManager().getPackageInfo(
                    this.getPackageName(), 0).versionName;
            Constants.versionCode=getPackageManager().getPackageInfo(
                    this.getPackageName(), 0).versionCode;
            DISPLAY_WELCOME_KEY=DISPLAY_WELCOME_KEY+Constants.versionName;
            initShare();
            Constants.LANGUARE= getResources().getConfiguration().locale.getCountry();
            Constants.DeviceInfo = Constants.versionName + ","
                    + android.os.Build.MODEL + ","
                    + android.os.Build.VERSION.SDK + ","
                    + android.os.Build.VERSION.RELEASE + ","
                    + DeviceUtils.getDeviceID(getApplicationContext());
            //Support.init(this);
            try{
            QbSdk.initX5Environment(getApplicationContext(),  new QbSdk.PreInitCallback() {//QbSdk.WebviewInitType.FIRSTUSE_AND_PRELOAD,
                @Override
                public void onCoreInitFinished() {
                    LogCat.i("HotelApplication","onCoreInitFinished()");
                }

                @Override
                public void onViewInitFinished(boolean b) {
                    LogCat.i("HotelApplication","onViewInitFinished()  b="+b);
                }
            });
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
                //ToastUtil.show(this,e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        initPusht();
        initImageLoader(getApplicationContext());

        //初始化环信
        DemoHelper.getInstance().init(this);
    }
    private void initShare()
    {

    }
    private void initPush()
    {
        mPushAgent = PushAgent.getInstance(this);
        UmengMessageHandler messageHandler = new UmengMessageHandler(){
            /**
             * 参考集成文档的1.6.3
             * http://dev.umeng.com/push/android/integration#1_6_3
             * */
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                new Handler().post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        // 对自定义消息的处理方式，点击或者忽略
                        LogCat.i("HotelApplication", "dealWithCustomMessage");
                        boolean isClickOrDismissed = true;
                        if(isClickOrDismissed) {
                            //自定义消息的点击统计
                            UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
                        } else {
                            //自定义消息的忽略统计
                            UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
                        }
                        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
                    }
                });
            }


            /**
             * 参考集成文档的1.6.4
             * http://dev.umeng.com/push/android/integration#1_6_4
             * */
            @Override
            public Notification getNotification(Context context,
                                                UMessage msg) {
                LogCat.i("HotelApplication", "getNotification");
                switch (msg.builder_id) {
                    case 1:
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
                        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
                        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
                        myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
                        myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
                        builder.setContent(myNotificationView)
                                .setSmallIcon(getSmallIconId(context, msg))
                                .setTicker(msg.ticker)
                                .setAutoCancel(true);

                        return builder.build();

                    default:
                        //默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }
        };

        mPushAgent.setMessageHandler(messageHandler);

        /**
         * 该Handler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * 参考集成文档的1.6.2
         * http://dev.umeng.com/push/android/integration#1_6_2
         * */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler(){
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                LogCat.i("HotelApplication", "openActivity");
                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
            }
            @Override
            public void openActivity(Context context, UMessage msg){
                LogCat.i("HotelApplication", "openActivity");
                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();

            }
        };
        //使用自定义的NotificationHandler，来结合友盟统计处理消息通知
        //参考http://bbs.umeng.com/thread-11112-1-1.html
        //CustomNotificationHandler notificationClickHandler = new CustomNotificationHandler();
        mPushAgent.setNotificationClickHandler(notificationClickHandler);


    }
    private void initPusht()
    {
        mPushAgent = PushAgent.getInstance(this);

        // mPushAgent.setDebugMode(true);
        UmengMessageHandler messageHandler = new UmengMessageHandler(){
            /**
             * 参考集成文档的1.6.3
             * http://dev.umeng.com/push/android/integration#1_6_3
             * */
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                new Handler().post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        // 对自定义消息的处理方式，点击或者忽略
                        LogCat.i("HotelApplication", "dealWithCustomMessage");
                     /*   boolean isClickOrDismissed = true;
                        if(isClickOrDismissed) {
                            //自定义消息的点击统计
                            LogCat.i("HotelApplication", "自定义消息的点击统计");
                            UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
                        } else {
                            //自定义消息的忽略统计
                            LogCat.i("HotelApplication", "自定义消息的忽略统计");
                            UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
                        }
                        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();*/
                    }
                });
            }


            /**
             * 参考集成文档的1.6.4
             * http://dev.umeng.com/push/android/integration#1_6_4
             * */
            @Override
            public Notification getNotification(Context context,
                                                UMessage msg) {
                LogCat.i("HotelApplication", "getNotification");
                switch (msg.builder_id) {
                    case 1:
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
                        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
                        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
                        myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
                        myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
                        builder.setContent(myNotificationView)
                                .setSmallIcon(getSmallIconId(context, msg))
                                .setTicker(msg.ticker)
                                .setAutoCancel(true);

                        return builder.build();

                    default:
                        //默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }
        };

        mPushAgent.setMessageHandler(messageHandler);

        /**
         * 该Handler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * 参考集成文档的1.6.2
         * http://dev.umeng.com/push/android/integration#1_6_2
         * */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler(){
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                LogCat.i("HotelApplication", "openActivity");
                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
            }
            @Override
            public void openActivity(Context context, UMessage msg){
                LogCat.i("HotelApplication", "openActivity");
                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();

            }
        };
        //使用自定义的NotificationHandler，来结合友盟统计处理消息通知
        //参考http://bbs.umeng.com/thread-11112-1-1.html
        //CustomNotificationHandler notificationClickHandler = new CustomNotificationHandler();
        mPushAgent.setNotificationClickHandler(notificationClickHandler);

        //注册推送服务，每次调用register方法都会回调该接口
       /* new Thread(new Runnable() {
            public void run() {

            }
        }).start();*/
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                sendBroadcast(new Intent(UPDATE_STATUS_ACTION));
                if(deviceToken!=null&&deviceToken.length()>5)
                {
                    Constants.TOKEN=deviceToken;
                    SharedPreferencesUtils.saveString(HotelApplication.this,Constants.SP_KEY_PUSH_TOKEN, deviceToken);

                }
                // Toast.makeText(MainActivity.this,"A推送TOKEN="+Constants.TOKEN, Toast.LENGTH_LONG).show();
                LogCat.i("HotelApplication", "acceptArticlePush device_token= " + deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                LogCat.i("HotelApplication", "mPushAgent.register     onFailure");
            }
        });

        mPushAgent.setPushIntentServiceClass(MyPushIntentService.class);
    }
    public static final String UPDATE_STATUS_ACTION = "com.umeng.message.example.action.UPDATE_STATUS";
    private void initImageLoader(Context applicationContext) {
        // TODO Auto-generated method stub
        isRightApplication = "com.tgcyber.hotelmobile"
                .equals(getCurProcessName(getApplicationContext()));
        LogCat.i("HotelApplication", "hasInit = " + isRightApplication);
        if (isRightApplication) {
            File cacheDir = StorageUtils.getOwnCacheDirectory(
                    applicationContext, Constants.IMG_CACHE_DIR);
            LogCat.i("HotelApplication", "cacheDir = " + cacheDir.toString());
            // DisplayImageOptions defaultOptions = new
            // DisplayImageOptions.Builder()
            // .cacheInMemory(true)
            // .cacheOnDisc(true)
            // .build();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                    applicationContext)
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                            // .discCacheFileNameGenerator(new Md5FileNameGenerator())
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
                            // .writeDebugLogs() // Remove for release app
                    .diskCache(new UnlimitedDiskCache(cacheDir)) // default
                            // .discCache(new UnlimitedDiskCache(cacheDir)) // default
                            // .defaultDisplayImageOptions(defaultOptions)
                    .diskCacheSize(100 * 1024 * 1024)
                            // .discCacheSize(10 * 1024 * 1024)
                    .build();

            // Initialize ImageLoader with configuration.
            ImageLoader.getInstance().init(config);
        }

    }

    String getCurProcessName(Context context) {

        try {
            int pid = android.os.Process.myPid();
            ActivityManager mActivityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);

            for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                    .getRunningAppProcesses()) {
                if (appProcess.pid == pid) {

                    return appProcess.processName;
                }
            }
        } catch (Exception e) {

        }

        return null;
    }

    public boolean isFirstRunApp() {
        return SharedPreferencesUtils.getBoolean(getApplicationContext(),
                DISPLAY_WELCOME_KEY, true);
    }

    public void setIsFirstRunApp(boolean isDisplayer) {
        SharedPreferencesUtils.saveBoolean(getApplicationContext(),
                DISPLAY_WELCOME_KEY, isDisplayer);
    }


    public String getTemplateHtml(String fileName) {
        String result = null;
        try {
            InputStream in = getResources().getAssets().open(fileName); // 从Assets中的文件获取输入流
            int length = in.available(); // 获取文件的字节数
            byte[] buffer = new byte[length]; // 创建byte数组
            in.read(buffer); // 将文件中的数据读取到byte数组中
            result = EncodingUtils.getString(buffer, "utf-8"); // 将byte数组转换成指定格式的字符串
            if (in!=null){
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean getArticlePushStatus() {
        if (!SharedPreferencesUtils.getBoolean(getApplicationContext(),
                ARTICLE_PUSH_SETTED)) {
            return true;
        } else {
            return SharedPreferencesUtils.getBoolean(getApplicationContext(),
                    ARTICLE_PUSH_KEY);
        }
    }

    public void setArticlePushStatus(boolean status) {
        SharedPreferencesUtils.saveBoolean(getApplicationContext(),
                ARTICLE_PUSH_KEY, status);
        SharedPreferencesUtils.saveBoolean(getApplicationContext(),
                ARTICLE_PUSH_SETTED, true);
    }

    public void saveLoginData(String token, String uid) {
        SharedPreferencesUtils.saveString(getApplicationContext(),
                Constants.SP_KEY_USER_TOKEN, token);
        SharedPreferencesUtils.saveString(getApplicationContext(),
                Constants.SP_KEY_USER_UID, uid);
    }

    public void saveUserData(String name, String mobile, String avatar) {
        saveUserName(name);
        saveUserAvatar(avatar);
        saveUserMobile(mobile);
    }

    public void saveUserLoginState(int type) {
        SharedPreferencesUtils.saveInt(getApplicationContext(),
                Constants.SP_KEY_USER_LOGIN_STATE, type);
    }

    public void saveUserMobile(String mobile) {
        SharedPreferencesUtils.saveString(getApplicationContext(),
                Constants.SP_KEY_USER_MOBILE, mobile);
    }

    public void saveUserName(String name) {
        SharedPreferencesUtils.saveString(getApplicationContext(),
                Constants.SP_KEY_USER_NAME, name);
        LogCat.i("Application", "saveUserName = " + name);
    }

    public void saveUserAvatar(String avatar) {
        SharedPreferencesUtils.saveString(getApplicationContext(),
                Constants.SP_KEY_USER_AVATAR, avatar);
        LogCat.i("Application", "saveUserAvatar = " + avatar);
    }

    public String getLoginToken() {
        return SharedPreferencesUtils.getString(getApplicationContext(),
                Constants.SP_KEY_USER_TOKEN);
    }

    public String getLoginName() {
        return SharedPreferencesUtils.getString(getApplicationContext(),
                Constants.SP_KEY_USER_NAME);
    }

    public String getUserAvatar() {
        return SharedPreferencesUtils.getString(getApplicationContext(),
                Constants.SP_KEY_USER_AVATAR);
    }

    public String getThirdUserAvatar() {
        return SharedPreferencesUtils.getString(getApplicationContext(),
                Constants.SP_KEY_THIRD_USER_AVATAR);
    }

    public int getUserLoginState() {
        return SharedPreferencesUtils.getInt(getApplicationContext(),
                Constants.SP_KEY_USER_LOGIN_STATE);
    }

    public String getUserToken() {
        return SharedPreferencesUtils.getString(getApplicationContext(),
                Constants.SP_KEY_USER_TOKEN);
    }

    public String getUserMobile() {
        return SharedPreferencesUtils.getString(getApplicationContext(),
                Constants.SP_KEY_USER_MOBILE);
    }

    public String getLoginUid() {
        return SharedPreferencesUtils.getString(getApplicationContext(),
                Constants.SP_KEY_USER_UID);
    }

    public void logout2() {
        SharedPreferencesUtils.saveString(getApplicationContext(),
                Constants.SP_KEY_USER_TOKEN, "");
        SharedPreferencesUtils.saveString(getApplicationContext(),
                Constants.SP_KEY_USER_UID, "");
        SharedPreferencesUtils.saveString(getApplicationContext(),
                Constants.SP_KEY_USER_NAME, "");
        SharedPreferencesUtils.saveString(getApplicationContext(),
                Constants.SP_KEY_USER_MOBILE, "");
        SharedPreferencesUtils.saveString(getApplicationContext(),
                Constants.SP_KEY_USER_AVATAR, "");
        SharedPreferencesUtils.saveString(getApplicationContext(),
                Constants.SP_KEY_THIRD_USER_AVATAR, "");
        SharedPreferencesUtils.saveInt(getApplicationContext(),
                Constants.SP_KEY_USER_LOGIN_STATE, -1);
        LogCat.i("Application", "logout ");
    }

    public boolean isLogined() {
        String token = SharedPreferencesUtils.getString(
                getApplicationContext(), Constants.SP_KEY_USER_TOKEN);
        String uid = SharedPreferencesUtils.getString(getApplicationContext(),
                Constants.SP_KEY_USER_UID);
        if (StringUtil.isBlank(token) || StringUtil.isBlank(uid)) {
            return false;
        }
        return true;
    }


    public void saveThirdUserData(String thirdAvatar) {
        saveThirdUserAvatar(thirdAvatar);
    }

    private void saveThirdUserAvatar(String thirdAvatar) {
        SharedPreferencesUtils.saveString(getApplicationContext(),
                Constants.SP_KEY_THIRD_USER_AVATAR, thirdAvatar);
        LogCat.i("Application", "saveUserAvatar = " + thirdAvatar);
    }

    public UserInfo getUser() {
        return user;
    }

    public void saveUser(String username, String pwdMd5, String guid) {
        UserInfo user = new UserInfo();
        user.setUsername(username);
        user.setPassword(pwdMd5);
        user.setGuid(guid);

        saveUser(user);
    }

    public void saveUser(UserInfo user) {
        this.user = user;
        EaseUI.getInstance().setCurrentUser(new EaseUser(user.getUsername()));
        SharedPreferencesUtils.saveString(this, KeyConstant.USER_DATA, mGson.toJson(user));
    }


    public void logout(){
        showToast("成功退出登陆");
        SharedPreferencesUtils.saveString(getApplicationContext(), KeyConstant.USER_DATA, "");
        user = null;
        EMClient.getInstance().logout(true, null);
        EaseUI.getInstance().setCurrentUser(null);
    }


    public boolean hasLogin(){
        return user != null;
    }

    public boolean checkLogin(Context context){
        if(hasLogin()){
            return true;
        }

        showToast("请登录后操作");
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        return false;
    }

    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
