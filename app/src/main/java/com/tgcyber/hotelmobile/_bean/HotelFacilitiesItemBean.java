package com.tgcyber.hotelmobile._bean;

import com.tgcyber.hotelmobile._utils.StringUtil;
import com.tgcyber.hotelmobile.bean.ContentBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/17.
 */
public class HotelFacilitiesItemBean extends ContentBean{

    public String title;
    public String address;
    public String location;
    public String text;
    public String button;
    public String type;
    public String url;
    public String tel;
    public String like;
    public String likeurl;
    public String qrcode_url;
    public String remark;
    public int btnmap;
    public List<ImgEntity> img;

    public static class ImgEntity implements Serializable {
        public String url;
    }



    public static HotelFacilitiesItemBean getBean(String jsonStr) {
        HotelFacilitiesItemBean bean = null;
        if (StringUtil.isBlank(jsonStr)) {
            return null;
        }

        try {
            bean = new HotelFacilitiesItemBean();
            JSONObject obj = new JSONObject(jsonStr);
            bean.title = obj.optString("title");
            bean.address = obj.optString("address");
            bean.location = obj.optString("location");
            bean.text = obj.optString("text");
            bean.qrcode_url = obj.optString("qrcode_url");
            bean.tel = obj.optString("tel");
            bean.like = obj.optString("like");
            bean.likeurl = obj.optString("likeurl");
            bean.button = obj.optString("button");
            bean.url = obj.optString("url");
            bean.type = obj.optString("type");
            bean.remark = obj.optString("remark");
            bean.btnmap = obj.optInt("btnmap");

            JSONArray imgArray = obj.optJSONArray("img");
            JSONObject itemObj;
            if(imgArray!=null) {
                bean.img = new ArrayList<ImgEntity>();
                ImgEntity imgEntity;
                for (int i = 0; i < imgArray.length(); i++) {
                    imgEntity = new ImgEntity();
                    itemObj = (JSONObject) imgArray.opt(i);
                    imgEntity.url = itemObj.optString("url");
                    bean.img.add(imgEntity);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bean;
    }

}
