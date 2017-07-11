package com.tgcyber.hotelmobile.bean;


import com.tgcyber.hotelmobile._utils.LogCat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LocBean implements Serializable {

    public List<LocItem> items;
    public int status;
    public static LocBean getBean(String json) {
        LocBean bean = null;
        try {
            JSONObject obj = new JSONObject(json);

            bean = new LocBean();
            bean.status = obj.optInt("state");


            JSONArray array = obj.optJSONArray("items");
            if (array != null) {
                int length = array.length();
                LogCat.i("LocBean","items:"+length);
                bean.items = new ArrayList<LocItem>();
                LocItem temp;
                JSONObject itemObj;
                for (int i = 0; i < length; i++) {
                    itemObj = (JSONObject) array.opt(i);
                    temp = LocItem.getItem(itemObj);
                    bean.items.add(temp);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            bean = null;
        }
        return bean;
    }
}