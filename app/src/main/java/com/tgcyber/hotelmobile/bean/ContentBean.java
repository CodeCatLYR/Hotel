package com.tgcyber.hotelmobile.bean;

import java.io.Serializable;

public class ContentBean implements Serializable {

    public long initTime;

    /*
     * 初始化刷新的时间，用于显示上次更新的时间，等
     */
    public void updateInitTime() {
        initTime = System.currentTimeMillis();
    }
}
