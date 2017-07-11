package com.tgcyber.hotelmobile.bean;

import com.tgcyber.hotelmobile._bean.BannerEntity;
import com.tgcyber.hotelmobile.constants.Constants;
import com.tgcyber.hotelmobile._utils.LogCat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomePageBean extends ContentBean {

    public List<HomeTeam> teams;
    public ConfigItem configItem;
  //  public List<BackAdItem> bgadvs;
    public List<HomeItem> menus;
    public int status;
    public int state;
    public List<BannerEntity> banner;
    public static HomePageBean getBean(String json) {
        HomePageBean bean = null;
        try {
            JSONObject obj = new JSONObject(json);

            bean = new HomePageBean();
            bean.state = obj.optInt("state");

            JSONObject config = obj.optJSONObject("config");
            if(config!=null)
            {
                bean.configItem=ConfigItem.getItem(config);
                if( bean.configItem.url!=null)
                {
                    Constants.HOTEL_BASE_URL= bean.configItem.url;
                    bean.configItem.bgimg= Constants.HOTEL_BASE_URL+ bean.configItem.bgimg;
                    bean.configItem.openimg= Constants.HOTEL_BASE_URL+ bean.configItem.openimg;
                    LogCat.i("HomePageBean","config:A"+Constants.HOTEL_BASE_URL);
                }
                LogCat.i("HomePageBean","config:B");
            }
            JSONArray hotItems = obj.optJSONArray("banner");
            if (hotItems != null) {
                int length = hotItems.length();
                LogCat.i("HomePageBean","banner:A"+length);
                bean.banner = new ArrayList<BannerEntity>();
                BannerEntity temp;
                JSONObject itemObj;
                for (int i = 0; i < length; i++) {
                     itemObj = (JSONObject) hotItems.opt(i);
                    temp = BannerEntity.getData(itemObj);
                    if(Constants.HOTEL_BASE_URL!=null&&temp.img!=null)
                    {
                        temp.img= Constants.HOTEL_BASE_URL+temp.img;
                        LogCat.i("HomePageBean","banner:"+temp.img);
                    }
                    bean.banner.add(temp);
                }
            }
            JSONArray arrayMenu = obj.optJSONArray("menu");
            if (arrayMenu != null) {
                int length = arrayMenu.length();
                LogCat.i("HomePageBean","arrayMenu:"+length);
                bean.menus = new ArrayList<HomeItem>();
                JSONObject itemObj;
                HomeItem item=null;
                for (int i = 0; i <  arrayMenu.length(); i++) {
                    itemObj = (JSONObject) arrayMenu.opt(i);
                    item = HomeItem.getItem(itemObj);
                    if(Constants.HOTEL_BASE_URL!=null&&item.img!=null)
                        item.img=Constants.HOTEL_BASE_URL+item.img;
                    if(Constants.HOTEL_BASE_URL!=null&&item.clickimg!=null)
                        item.clickimg=Constants.HOTEL_BASE_URL+item.clickimg;
                    bean.menus.add(item);
                }
            }

            JSONArray array = obj.optJSONArray("items");
            if (array != null) {
                int length = array.length();
                LogCat.i("HomePageBean","items:"+length);
                bean.teams = new ArrayList<HomeTeam>();
                HomeTeam temp;
                JSONObject itemObj;
                for (int i = 0; i < length; i++) {
                     itemObj = (JSONObject) array.opt(i);
                    temp = HomeTeam.getItem(itemObj,Constants.HOTEL_BASE_URL);
                    bean.teams.add(temp);
                }
            }

           /* JSONArray badv = obj.optJSONArray("bgadv");
            if (badv != null) {
                int length = badv.length();
                LogCat.i("HomePageBean","bgadv:"+length);
                bean.bgadvs = new ArrayList<BackAdItem>();
                BackAdItem temp;
                JSONObject itemObj;
                for (int i = 0; i < length; i++) {
                     itemObj = (JSONObject) badv.opt(i);
                    temp = BackAdItem.getItem(itemObj);
                    if(Constants.HOTEL_BASE_URL!=null&&temp.img!=null)
                    {
                        temp.img= Constants.HOTEL_BASE_URL+temp.img;
                        LogCat.i("HomePageBean","bgadv:"+temp.img);
                    }
                    bean.bgadvs.add(temp);
                }
            }*/



        } catch (Exception e) {
            e.printStackTrace();
            bean = null;
        }
        return bean;
    }
}
