package com.tgcyber.hotelmobile._event;

import com.tgcyber.hotelmobile.bean.UserInfo;

/**
 * Created by lyr on 2017/7/7.
 */

public class HXEvent {
    public static final int LOGIN_SUCCESS = 7;
    public static final int LOGIN_FAIL = 17;
    public static final int MSG_COUNT = 27;

    public int type;
    public UserInfo user;
    public int unreadMsgCount;

    public HXEvent(int type, UserInfo user) {
        this.type = type;
        this.user = user;
    }

    public HXEvent(int type, int unreadCount) {
        this.type = type;
        unreadMsgCount = unreadCount;
    }
}
