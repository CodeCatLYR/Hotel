package com.tgcyber.hotelmobile.bean;

import com.tgcyber.hotelmobile._utils.StringUtil;

import org.json.JSONObject;

public class MenuData {

    public String name;
    public String type;
    public String url;

    public static MenuData getOrderItem(JSONObject obj) throws Exception {
        MenuData item = new MenuData();
        item.name = obj.optString("name");
        item.url = obj.optString("url");
        item.type = obj.optString("type");

        if (StringUtil.isBlank(item.name)) {
            return null;
        }
        if (StringUtil.isBlank(item.url)) {
            return null;
        }

        return item;
    }
}
