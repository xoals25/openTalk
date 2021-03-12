package com.example.opentalk.Data;

public class Logindata {
    String userid;
    String userpwd;
    String username;
    String imgString_tobitmap;

    public Logindata(String userid, String userpwd, String username, String imgString_tobitmap) {
        this.userid = userid;
        this.userpwd = userpwd;
        this.username = username;
        this.imgString_tobitmap = imgString_tobitmap;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserpwd() {
        return userpwd;
    }

    public void setUserpwd(String userpwd) {
        this.userpwd = userpwd;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImgString_tobitmap() {
        return imgString_tobitmap;
    }

    public void setImgString_tobitmap(String imgString_tobitmap) {
        this.imgString_tobitmap = imgString_tobitmap;
    }
}
