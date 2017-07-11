package com.tgcyber.hotelmobile.bean;


import org.json.JSONObject;

import java.io.Serializable;

public class LocItem implements Serializable {

    /*
     "items": [
        {
            "id": 1,
            "name": "商家1",
            "location": "113.580028,22.151665",
            "text": "商家简介",
            "url": "http://api.hotelmobile.top/apptest/content?id=1"
        },
        {
            "id": 2,
            "name": "商家2",
            "location": "113.579998,22.151465",
            "text": "商家简介",
            "url": "http://api.hotelmobile.top/apptest/content?id=2"
     */
    public String name;// 新闻标题
    public String location;// 当前ITEM类型
    public String img;// 图片地址，需和前面的ＢＡＳＥＵＲＬ相加
    public int id;//
    public String url;// 跳转后需要读取数据的接口地址
    public String text;//点击后的图片效果

    public static LocItem getItem(JSONObject obj) throws Exception {

        LocItem item = new LocItem();
        item.name = obj.optString("name");
        item.location = obj.optString("location");
        item.url = obj.optString("url");
        item.img = obj.optString("img");
        item.text = obj.optString("text");
        item.id = obj.optInt("id");
        return item;
    }
}
