package com.tgcyber.hotelmobile.bean;

/**
 * Created by lyr on 2017/6/30.
 */

public class UserInfo {

    private final String HX_PRE_SIGN = "triproaming";

    private String username;
    private String nickname;
    private String password;
    private String guid;
    private String hxId;
    private String hxPwd;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
        this.hxId = HX_PRE_SIGN + "_" + guid;
        this.hxPwd = guid;
    }

    public String getHxId() {
        return hxId;
    }


    public String getHxPwd() {
        return hxPwd;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "HX_PRE_SIGN='" + HX_PRE_SIGN + '\'' +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", guid='" + guid + '\'' +
                ", hxId='" + hxId + '\'' +
                ", hxPwd='" + hxPwd + '\'' +
                '}';
    }
}
