package com.tgcyber.hotelmobile._bean;

import com.tgcyber.hotelmobile._utils.StringUtil;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/3/23.
 */
public class RespBean {
    /**
     * errcode : 0
     * errmsg : 已收藏
     */

    public int status;
    public String errmsg;
    public static RespBean getBean(String jsonStr) {
        RespBean bean = null;
        if (StringUtil.isBlank(jsonStr)) {
            return null;
        }

        try {
            bean = new RespBean();
            JSONObject obj = new JSONObject(jsonStr);
            bean.status = obj.optInt("status");
            bean.errmsg = obj.optString("errmsg");

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bean;
    }

}
