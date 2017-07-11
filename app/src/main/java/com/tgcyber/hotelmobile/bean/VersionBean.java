package com.tgcyber.hotelmobile.bean;

import com.tgcyber.hotelmobile._utils.StringUtil;

import org.json.JSONObject;

public class VersionBean {
    // {"message":"操作成功","code":0,"success":true,"data":{"android":{"version":"1.0.0","url":"http:\/\/baidu.com","versionInt":1,"desc":"nice
    // version!"},"ios":{"version":"1.0.0","url":"http:\/\/baidu.com","versionInt":1,"desc":"nice
    // version!"}}}
    public String status;
    public String desc;
    public int versionInt;
    public String version;
    public String url;

    public static VersionBean getBean(String jsonStr) {
        VersionBean bean = null;
        if (StringUtil.isBlank(jsonStr)) {
            return null;
        }
        try {
            bean = new VersionBean();
            JSONObject obj = new JSONObject(jsonStr);

            bean.status = obj.optString("status");
            bean.version = obj.optString("v");
            bean.desc = obj.optString("desc");
            bean.url = obj.optString("url");
            JSONObject dataObj = obj.optJSONObject("data");
            if (dataObj != null) {
                JSONObject androidObj = dataObj.optJSONObject("android");
                if (androidObj != null) {
                    bean.version = androidObj.optString("version");
                    bean.desc = androidObj.optString("desc");
                    bean.url = androidObj.optString("url");
                    bean.versionInt = androidObj.optInt("versionInt");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bean;
    }
}
