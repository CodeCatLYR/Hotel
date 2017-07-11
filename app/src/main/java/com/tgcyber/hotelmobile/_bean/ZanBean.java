package com.tgcyber.hotelmobile._bean;

import com.tgcyber.hotelmobile._utils.StringUtil;
import com.tgcyber.hotelmobile.bean.ContentBean;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/9/5.
 */
public class ZanBean extends ContentBean{

    public int status;
    public String like;

    public static ZanBean getBean(String jsonStr) {
        ZanBean bean = null;
        if (StringUtil.isBlank(jsonStr)) {
            return null;
        }

        try {
            bean = new ZanBean();
            JSONObject obj = new JSONObject(jsonStr);
            bean.status = obj.optInt("status");
            bean.like = obj.optString("like");

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bean;
    }
}
