package com.tgcyber.hotelmobile.bean;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeItem implements Serializable {

    /*
     "id": 1,
                    "name": "酒店设施",
                    "type": "list1",
                    "img": "/static/apptest/cont.png"
     */
    public String name;// 新闻标题
    public String type;// 当前ITEM类型
    public String img;// 图片地址，需和前面的ＢＡＳＥＵＲＬ相加
    public int id;//
    public String url;// 跳转后需要读取数据的接口地址
    public String clickimg;//点击后的图片效果
    public String value;// 值，适用于type=phone之类的类型
    public List<HomeItem> homeItemList;

    public static HomeItem getItem(JSONObject obj) throws Exception {

        HomeItem item = new HomeItem();
        item.name = obj.optString("name");
        item.type = obj.optString("type");
        item.url = obj.optString("url");
        item.img = obj.optString("img");
        item.value = obj.optString("value");
        item.clickimg = obj.optString("clickimg");
        item.id = obj.optInt("id");

        JSONArray jsonArray = obj.optJSONArray("sublist");

        if (jsonArray != null){
            item.homeItemList = new ArrayList<HomeItem>();
            JSONObject jsonObject;
            HomeItem homeItem;

            for (int i = 0; i<jsonArray.length(); i++){
                jsonObject = (JSONObject) jsonArray.opt(i);
                homeItem = new HomeItem();

                homeItem.id = jsonObject.optInt("id");
                homeItem.name = jsonObject.optString("name");
                homeItem.type = jsonObject.optString("type");
                homeItem.value = jsonObject.optString("value");
                homeItem.img = jsonObject.optString("img");

                item.homeItemList.add(homeItem);
            }
        }

        return item;
    }
}
