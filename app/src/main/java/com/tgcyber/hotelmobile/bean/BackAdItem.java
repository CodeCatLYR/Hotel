package com.tgcyber.hotelmobile.bean;


import com.tgcyber.hotelmobile.constants.Constants;
import com.tgcyber.hotelmobile._utils.LogCat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//后台黑屏时播放的广告图
public class BackAdItem implements Serializable {

    /*
    "bgadv": [
        {
            "id": 1,
            "img": "/static/apptest/adv1.jpg",
            "sdate": "2016-08-01",
            "edate": "2016-10-01"
        }
    ],
     */
    public String sdate;// 新闻标题
    public String edate;// 新闻摘要
    public String img;// 修改时间
    public String id;
    public long size;
    public String md5;
  //  public List<BackAdItem> bgadvs;
    public static List<BackAdItem>  getItem(String json) throws Exception {
        List<BackAdItem> bgadvs=null;
        JSONObject obj = new JSONObject(json);
        JSONArray badv = obj.optJSONArray("list");
        if (badv != null) {

            int length = badv.length();
            LogCat.i("BackAdItem","bgadv:"+length);
            bgadvs = new ArrayList<BackAdItem>();
            BackAdItem item;
            JSONObject itemObj;
            for (int i = 0; i < length; i++) {
                itemObj = (JSONObject) badv.opt(i);
                item = new BackAdItem();
                item.sdate = itemObj.optString("sdate");
                item.edate = itemObj.optString("edate");
                item.img = itemObj.optString("img");
                item.md5 = itemObj.optString("md5");
                item.size = itemObj.optLong("size");
                item.id = itemObj.optString("id");
                if(Constants.HOTEL_BASE_URL!=null&&item.img!=null)
                {
                    item.img= Constants.HOTEL_BASE_URL+item.img;
                    LogCat.i("BackAdItem","bgadv:"+item.img);
                }
                bgadvs.add(item);
            }
        }

        return bgadvs;
    }
}
