package com.tgcyber.hotelmobile.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeTeam implements Serializable {

    public String title;
    public String img;
    public List<HomeItem> items;


    public static HomeTeam getItem(JSONObject obj,String baseUrl) throws Exception {

        HomeTeam team = new HomeTeam();
        team.title = obj.optString("title");
        JSONArray imgArray = obj.optJSONArray("list");
        if (imgArray != null && imgArray.length() > 0) {
            team.items=new ArrayList<HomeItem>();
            JSONObject itemObj;
            HomeItem item=null;
            for (int i = 0; i <  imgArray.length(); i++) {
                itemObj = (JSONObject) imgArray.opt(i);
                item = HomeItem.getItem(itemObj);
                if(baseUrl!=null&&item.img!=null)
                    item.img=baseUrl+item.img;
                team.items.add(item);
            }
        }

        team.img = obj.optString("img");
        if(baseUrl!=null)
            team.img=baseUrl+team.img;
        return team;
    }
}
