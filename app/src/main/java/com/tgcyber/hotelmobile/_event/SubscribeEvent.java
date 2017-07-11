package com.tgcyber.hotelmobile._event;

/**
 * Created by Administrator on 2016/3/17.
 */
public class SubscribeEvent {

    public static final int ONE_TYPE = 1;
    public static final int TWO_TYPE = 2;
    public static final int LOGIN_TYPE = 3;

    public int type;

    public SubscribeEvent(int type){
        this.type = type;
    }
}
