package com.tgcyber.hotelmobile._event;

/**
 * Created by Administrator on 2016/5/9.
 */
public class NewsPagerEvent {

    public int type;
    public int newspager_type;

    public static int LOGIN_TYPE = 10000;
    public static int REFRESH_TYPE = 10001;

    public NewsPagerEvent(int type, int newspager_type){
        this.type = type;
        this.newspager_type = newspager_type;
    }
}
