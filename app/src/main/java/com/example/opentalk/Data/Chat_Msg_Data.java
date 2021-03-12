package com.example.opentalk.Data;

import android.graphics.Bitmap;

public class Chat_Msg_Data {


    String userid;
    String message;
    int viewtype; //viewtype 2일경우 userid가 null이 나오니 null일경우 처리해줘야한다.;
    Bitmap imgbitmap;

    public Chat_Msg_Data(String userid, String message, int viewtype, Bitmap imgbitmap) {
        this.userid = userid;
        this.message = message;
        this.viewtype = viewtype;
        this.imgbitmap = imgbitmap;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getViewtype() {
        return viewtype;
    }

    public void setViewtype(int viewtype) {
        this.viewtype = viewtype;
    }

    public Bitmap getImgbitmap() {
        return imgbitmap;
    }

    public void setImgbitmap(Bitmap imgbitmap) {
        this.imgbitmap = imgbitmap;
    }
}
