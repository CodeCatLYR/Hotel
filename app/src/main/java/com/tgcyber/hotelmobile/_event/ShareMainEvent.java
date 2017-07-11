package com.tgcyber.hotelmobile._event;

/**
 * Created by Administrator on 2016/9/28.
 */
public class ShareMainEvent {

    public static final int ONE_TYPE = 1;
    public static final int TWO_TYPE = 2;

    public int type;

    public ShareMainEvent(int type){
        this.type = type;
    }
}
