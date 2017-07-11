package com.tgcyber.hotelmobile.bean;


import org.json.JSONObject;

import java.io.Serializable;

public class ConfigItem implements Serializable {

    /*
 "config": {
        "url": "http://api.hotelmobile.top",
        "title": "T",
        "room": "T",
        "hotelurl": "http://www.hotel.com",
        "help": "http://api.hotelmobile.top/apptest/help",
        "upapitimes": 300000,
        "bgimg": "/static/apptest/body1.jpg",
        "openimg": "/static/apptest/adv2.jpg",
        "devinfo": "",
        "wifi_openduration": 900,
        "wifi_bandwidth": 20,
        "wifi_ssid": "quicklink-",
        "wifi_password": "12345678@",
        "bgadv_url": "http://api.hotelmobile.top/apptest/bgadv"
    },
     */
    public String title;// 头部标题
    public String room;// 左上角房间号
    public String token;//
    public long upapitimes;
    public long openimgtimes;
    public String url;// 修改时间
    public String urltype;// 修改时间
    public int isshowlogview;//打印ＬＯＧ，１为显示
    public String hotelurl;
    public String bgimg;
    public String openimg;
    public String help;
    public int wifi_openduration;
    public int wifi_bandwidth;
    public String wifi_ssid;
    public String wifi_password;
    public String bgadv_url;
    public String rest_url;
    public String update_apk_url;
    public static ConfigItem getItem(JSONObject obj) throws Exception {

        ConfigItem item = new ConfigItem();
        item.title = obj.optString("title");
        item.room = obj.optString("room");
        item.token = obj.optString("token");
        item.upapitimes = obj.optLong("upapitimes");
        item.openimgtimes = obj.optLong("openimgtimes");
        item.url = obj.optString("url");
        item.urltype = obj.optString("urltype");
        item.help = obj.optString("help");
        item.bgimg = obj.optString("bgimg");
        item.openimg = obj.optString("openimg");
        item.hotelurl= obj.optString("hotelurl");
        item.wifi_openduration = obj.optInt("wifi_openduration");
        item.wifi_bandwidth = obj.optInt("wifi_bandwidth");
        item.isshowlogview = obj.optInt("isshowlogview");
        item.wifi_ssid = obj.optString("wifi_ssid");
        item.wifi_password= obj.optString("wifi_password");
        item.bgadv_url= obj.optString("bgadv_url");
        item.rest_url= obj.optString("rest_url");
        item.update_apk_url= obj.optString("update_apk_url");
        return item;
    }
}
