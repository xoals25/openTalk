package com.example.opentalk.Data;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.opentalk.BitmapConverter;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FriendWaitData {

    String imgpath;
    String userid;
//    String usernickname;
    Bitmap bitmap;

    public FriendWaitData(String imgpath, String userid, Bitmap bitmap) {
        this.imgpath = imgpath;
        this.userid = userid;
//        this.usernickname = usernickname;
        this.bitmap = bitmap;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

//    public String getUsernickname() {
//        return usernickname;
//    }
//
//    public void setUsernickname(String usernickname) {
//        this.usernickname = usernickname;
//    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
