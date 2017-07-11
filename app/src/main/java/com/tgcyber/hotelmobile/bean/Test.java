package com.tgcyber.hotelmobile.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/2/17.
 */
public class Test {


    public List<ListEntity> list;

    public static class ListEntity {
        public int docid;
        public String title;
        public int imgstyle;
        public String nickname;
        public int uid;
        public String headimgurl;
        public int issubscribe;
        public int islike;
        public String digest;
        public int ptime;
        public int likenum;
        public List<String> imglist;
    }
}
