package com.tgcyber.hotelmobile._bean;

import android.os.Parcel;
import android.os.Parcelable;

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
public class SecondPageBean extends ContentBean{

    public String title;
    public String uploadurl;
    public String locimg;
    public String locurl;
    public List<BannerEntity> banner;
    public List<MenuEntity> class2;
    public List<MenuEntity> menu;
    public List<ItemsEntity> items;


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
            public String name;
            public String type;
            public String address;
            public String text;
            public String tel;
            public String url;
            public String timg;
            public String longg;
            public String remark;
        }
    }

    public static SecondPageBean getBean(String jsonStr) {
        SecondPageBean bean = null;
        if (StringUtil.isBlank(jsonStr)) {
            return null;
        }

        try {
            bean = new SecondPageBean();
            JSONObject obj = new JSONObject(jsonStr);
            bean.title = obj.optString("title");
            bean.locimg = obj.optString("locimg");
            bean.locurl = obj.optString("locurl");
            bean.uploadurl = obj.optString("uploadurl");

            JSONArray bannerArray = obj.optJSONArray("banner");
            BannerEntity bannerEntity;
            JSONObject itemObj;
            if(bannerArray!=null) {
                bean.banner = new ArrayList<BannerEntity>();
                for (int i = 0; i < bannerArray.length(); i++) {
                    bannerEntity = new BannerEntity();
                    itemObj = (JSONObject) bannerArray.opt(i);

                    bannerEntity.id = itemObj.optString("id");
                    bannerEntity.img = itemObj.optString("img");
                    bannerEntity.url = itemObj.optString("url");
                    bannerEntity.type = itemObj.optString("type");
                    bannerEntity.name = itemObj.optString("name");
                    bean.banner.add(bannerEntity);
                }
            }
            JSONArray class2Array = obj.optJSONArray("class2");
            MenuEntity classEntity;
            JSONObject classObj;
            if(class2Array!=null) {
                bean.class2 = new ArrayList<MenuEntity>();
                for (int i = 0; i < class2Array.length(); i++) {
                    classEntity = new MenuEntity();
                    classObj = (JSONObject) class2Array.opt(i);

                    classEntity.id = classObj.optInt("id");
                    classEntity.img = classObj.optString("img");
                    classEntity.url = classObj.optString("url");
                    classEntity.type = classObj.optString("type");
                    classEntity.name = classObj.optString("name");
                    bean.class2.add(classEntity);
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
            JSONObject itemObj1;
            if(itemsArray!=null) {
                bean.items = new ArrayList<ItemsEntity>();
                for (int i = 0; i < itemsArray.length(); i++) {
                    itemsEntity = new ItemsEntity();
                    itemObj1 = (JSONObject) itemsArray.opt(i);


                    JSONArray listArray = itemObj1.optJSONArray("list");
                    ItemsEntity.ListEntity listEntity;
                    JSONObject itemObj2;
                    if (listArray!=null){
                        itemsEntity.list = new ArrayList<ItemsEntity.ListEntity>();

                        bean.items.add(itemsEntity);

                        for (int j = 0; j < listArray.length(); j++) {
                            listEntity = new ItemsEntity.ListEntity();
                            itemObj2 = (JSONObject) listArray.opt(j);

                            listEntity.id = itemObj2.optInt("id");
                            listEntity.name = itemObj2.optString("name");
                            listEntity.type = itemObj2.optString("type");
                            listEntity.address = itemObj2.optString("address");
                            listEntity.text = itemObj2.optString("text");
                            listEntity.tel = itemObj2.optString("tel");
                            listEntity.url = itemObj2.optString("url");
                            listEntity.timg = itemObj2.optString("timg");
                            listEntity.longg = itemObj2.optString("long");
                            listEntity.remark = itemObj2.optString("remark");

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
