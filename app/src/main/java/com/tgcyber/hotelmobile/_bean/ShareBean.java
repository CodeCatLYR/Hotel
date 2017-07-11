package com.tgcyber.hotelmobile._bean;

import com.tgcyber.hotelmobile._utils.StringUtil;
import com.tgcyber.hotelmobile.bean.ContentBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/20.
 */
public class ShareBean extends ContentBean{

    public String title;
    public List<BannerEntity> banner;
    public List<MenuEntity> menu;
    public List<ItemsEntity> items;
    public String uploadurl;


    public static class MenuEntity implements Serializable {
        public int id;
        public String name;
        public String type;
        public String img;
        public String url;
    }

    public static class ItemsEntity implements Serializable {
        public List<ListEntity> list;

        public static class ListEntity implements Serializable {
            public int id;
            public String avatar;
            public String location;
            public String address;
            public String text;
            public String tokenid;
            public String createtime;
            public String img;
            public int like;
            public String qrcode_url;
        }
    }

    public static ShareBean getBean(String jsonStr) {
        ShareBean bean = null;
        if (StringUtil.isBlank(jsonStr)) {
            return null;
        }

        try {
            bean = new ShareBean();
            JSONObject obj = new JSONObject(jsonStr);
            bean.title = obj.optString("title");
            bean.uploadurl = obj.optString("uploadurl");

            JSONArray bannerArray = obj.optJSONArray("banner");
            BannerEntity bannerEntity;
            JSONObject bannerObj;
            if(bannerArray!=null) {
                bean.banner = new ArrayList<BannerEntity>();
                for (int i = 0; i < bannerArray.length(); i++) {
                    bannerEntity = new BannerEntity();
                    bannerObj = (JSONObject) bannerArray.opt(i);

                    bannerEntity.id = bannerObj.optString("id");
                    bannerEntity.img = bannerObj.optString("img");
                    bannerEntity.url = bannerObj.optString("url");
                    bannerEntity.type = bannerObj.optString("type");
                    bannerEntity.name = bannerObj.optString("name");
                    bean.banner.add(bannerEntity);
                }
            }

            JSONArray menuArray = obj.optJSONArray("menu");
            MenuEntity menuEntity;
            JSONObject menuObj;
            if(menuArray!=null) {
                bean.menu = new ArrayList<MenuEntity>();
                for (int i = 0; i < menuArray.length(); i++) {
                    menuEntity = new MenuEntity();
                    menuObj = (JSONObject) menuArray.opt(i);

                    menuEntity.id = menuObj.optInt("id");
                    menuEntity.name = menuObj.optString("name");
                    menuEntity.type = menuObj.optString("type");
                    menuEntity.img = menuObj.optString("img");
                    menuEntity.url = menuObj.optString("url");
                    bean.menu.add(menuEntity);
                }
            }

            JSONArray itemsArray = obj.optJSONArray("items");
            ItemsEntity itemsEntity;
            JSONObject itemObj;
            if(itemsArray!=null) {
                bean.items = new ArrayList<ItemsEntity>();
                for (int i = 0; i < itemsArray.length(); i++) {
                    itemsEntity = new ItemsEntity();
                    itemObj = (JSONObject) itemsArray.opt(i);


                    JSONArray listArray = itemObj.optJSONArray("list");
                    ItemsEntity.ListEntity listEntity;
                    JSONObject itemObj2;
                    if (listArray!=null){
                        itemsEntity.list = new ArrayList<ItemsEntity.ListEntity>();

                        bean.items.add(itemsEntity);

                        for (int j = 0; j < listArray.length(); j++) {
                            listEntity = new ItemsEntity.ListEntity();
                            itemObj2 = (JSONObject) listArray.opt(j);

                            listEntity.id = itemObj2.optInt("id");
                            listEntity.like = itemObj2.optInt("like");
                            listEntity.avatar = itemObj2.optString("avatar");
                            listEntity.location = itemObj2.optString("location");
                            listEntity.address = itemObj2.optString("address");
                            listEntity.text = itemObj2.optString("text");
                            listEntity.tokenid = itemObj2.optString("tokenid");
                            listEntity.createtime = itemObj2.optString("createtime");
                            listEntity.img = itemObj2.optString("img");
                            listEntity.qrcode_url = itemObj2.optString("qrcode_url");
                            bean.items.get(i).list.add(listEntity);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bean;
    }
}
