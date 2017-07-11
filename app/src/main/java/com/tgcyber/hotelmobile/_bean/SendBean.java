package com.tgcyber.hotelmobile._bean;

import com.tgcyber.hotelmobile._utils.StringUtil;
import com.tgcyber.hotelmobile.bean.ContentBean;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/8/25.
 */
public class SendBean extends ContentBean{
    public int status;
    public String info;
    public String imgurl;
    public String fileurl;


    public static SendBean getBean(String jsonStr) {
        SendBean bean = null;
        if (StringUtil.isBlank(jsonStr)) {
            return null;
        }

        try {
            bean = new SendBean();
            JSONObject obj = new JSONObject(jsonStr);
            bean.status = obj.optInt("status");
            bean.info = obj.optString("info");
            bean.imgurl = obj.optString("imgurl");
            bean.fileurl= obj.optString("fileurl");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bean;
    }
}
