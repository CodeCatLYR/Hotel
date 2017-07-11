package com.tgcyber.hotelmobile.constants;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class Constants {
    public static final String NULL = "NULL";
    public static final String HOME = "Home";
    public static final String THEME = "Theme";
    public static  boolean logged =true;
    public static String versionName;
    public static String TOKEN=null;//推送的ＴＯＫＥＮ，当做应用ＴＯＫＥＮ发送至服务器
    public static String DEVICE_TOKEN=null;//推送的ＴＯＫＥＮ，当做应用ＴＯＫＥＮ发送至服务器
    public static String DeviceInfo;//当前设备信息
    public static String LANGUARE;//当前语言
    //经纬度
    public static   double latitude;
    public static   double longitude;
    public static String address="当前地点";
    public static int versionCode;
   // public static List<String> docids;//浏览历史记录
  //  public static List<String> like;//收藏记录
    public static int screenWidth;
    public static int MenuWidth = 180;
    public static int AD_WAIT = 3000;
    public static Context APP_CONTEXT;
 public static final String SP_KEY_BASEURL = "SP_KEY_BASEURL";
    public static final String SP_KEY_SHOW_LOG = "SP_KEY_SHOW_LOG";
    public static final String SP_KEY_PUSH_TOKEN = "SP_KEY_PUSH_TOKEN";

    //地点定位服务间隔更新
    public static final String SP_KEY_GPS_NEXTREQ = " ";
    public static final long SP_GPS_NEXTREQ = 180*1000;

    //图片HOST
    public static String HOTEL_BASE_URL;
    public static final String HOTEL_HOME_DATA="http://api.hotelmobile.top/apptest/index";
    public static final String HOTEL_SHARE_DATA="http://api.hotelmobile.top/apptest/share";
    public static final String HOTEL_SHARE_ITEM_DATA="http://api.hotelmobile.top/apptest/share_items?id=";
    public static final String HOTEL_UP_GPS="http://api.hotelmobile.top/apptest/upapi";

    //上传聊天语音
    public static final String HOTEL_UP_VOICE="http://api.hotelmobile.top/apptest/voice_u";
    public static final String HOTEL_UP_IMG="http://api.hotelmobile.top/apptest/img_u";
    // 屏幕高度
    public static int screenHeight;
    /**
     * 是否存在SD卡
     */
    public static final boolean SDCARD_IS_EXIST = Environment
            .getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED);

    /**
     * APP的根目录
     **/
    public static  String APP_PATH = Environment
            .getExternalStorageDirectory()
            + File.separator
            + "com.tgcyber.hotelmobile"
            + File.separator;
    public static  String APP_PATH_OLD = Environment
            .getExternalStorageDirectory()
            + File.separator
            + "hotel"
            + File.separator;
    public static final String PORTRAIT_PATH = Environment
            .getExternalStorageDirectory()
            + File.separator
            + "com.tgcyber.hotelmobile"
            + File.separator + "cache";

    /**
     * 拍照保存目录
     */
    public static final String CAMERA_PATH = APP_PATH + "camera"
            + File.separator;

    /**
     * 语音保存目录
     */
    public static final String VOICE_PATH = APP_PATH + "voice"
            + File.separator;
    /**
     * 保持图片的目录
     */
    public static  String DOWNLOAD_PATH = APP_PATH + "download"
            + File.separator;

    /**
     * 文件缓存目录
     */
    public static  String CACHE_PATH = APP_PATH + "cache" + File.separator;

    /**
     * 数据缓存目录
     */
    public static final String DATA_PATH = APP_PATH + "data" + File.separator;



    public static final String SP_NAME = "com.tgcyber.hotelmobile";
    public static final String SP_KEY_USER_TOKEN = "sp_key_user_token";
    public static final String SP_KEY_USER_UID = "sp_key_user_uid";
    public static final String SP_KEY_USER_NAME = "sp_key_user_name";
    public static final String SP_KEY_USER_LOGIN_STATE = "sp_key_user_login_state";
    public static final String SP_KEY_USER_MOBILE = "sp_key_user_mobile";
    public static final String SP_KEY_USER_AVATAR = "sp_key_user_avatar";
    public static final String SP_KEY_THIRD_USER_AVATAR = "sp_key_third_user_avatar";
    public static final String SP_KEY_PUSH_ENABLED = "sp_key_push_enabled";
    public static final String SP_KEY_FIRST_RUNPERMISSION = "sp_key_first_run_permission";
    public static final String SP_KEY_PUSH_COUNT = "sp_key_push_count";
    public static final String SP_KEY_MSG_COUNT = "sp_key_pmsg_count";

    public static final String SP_KEY_AD_SLEEP = "sp_key_ad_sleep";
    public static final String SP_KEY_DATA_NEXTREQ = "sp_key_data_nextreq";
    public static final String SP_KEY_MENU_ID = "sp_key_menu_id";
    public static final String SP_KEY_MENU_NEXTREQ = "sp_key_menu_nextreq";
    public static final String SP_KEY_LAT = "sp_key_lat";
    public static final String SP_KEY_LNG = "sp_key_lng";
    public static final String SP_KEY_CITY = "sp_key_city";
    public static final String SP_KEY_ADDR = "sp_key_addr";

    public static final String IMG_CACHE_DIR = "tgcyber.hotelmobile/cache";

    public static final String API_HOST = "http://api.hotelmobile.top";

    public static final String API_VERSION = API_HOST + "/api/home/version";

    //登录成功的广播
    public static final String ACTION_BROAD_CAST_LOGIN_SUCCESS = "action_broad_cast_login_success";

    //退出注销的广播
    public static final String ACTION_BROAD_CAST_LOGIN_LOGOUT = "action_broad_cast_logout";


    //更新的广播
    public static final String ACTION_PUSH_COUNT_UPDATE = "action_push_count_update";

    /**
     * 检查软件更新的时间间隔（2天一次）
     */
    public static final long CHECK_VERSION_UPDATE_INTERVAL = 1000* 3600 * 24 * 1;


}
