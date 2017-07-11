package com.tgcyber.hotelmobile._bean;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/24.
 */
public  class BannerEntity implements Serializable {
    public String id;
    public String img;
    public String url;
    public String type;
    public String name;
    public static BannerEntity getData(JSONObject obj) throws Exception{
        BannerEntity item = new BannerEntity();
        item.id = obj.optString("id");
        item.img = obj.optString("img");
        item.type=obj.optString("type");
        item.name=obj.optString("name");
        item.url=obj.optString("url");

        return item;
    }
}