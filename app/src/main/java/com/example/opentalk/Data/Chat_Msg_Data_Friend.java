package com.example.opentalk.Data;

import android.graphics.Bitmap;

public class Chat_Msg_Data_Friend {


    String usernickname;
    String message;
    int viewtype; //viewtype 2일경우 userid가 null이 나오니 null일경우 처리해줘야한다.;
    String readstate;
    String senddate;
    Bitmap imgbitmap;
    String msgtype;
    String openchatroomid;
    String roomtitle;

    public Chat_Msg_Data_Friend(String usernickname, String message, int viewtype, String readstate, String senddate, Bitmap imgbitmap, String msgtype, String openchatroomid, String roomtitle) {
        this.usernickname = usernickname;
        this.message = message;
        this.viewtype = viewtype;
        this.readstate = readstate;
        this.senddate = senddate;
        this.imgbitmap = imgbitmap;
        this.msgtype = msgtype;
        this.openchatroomid = openchatroomid;
        this.roomtitle = roomtitle;
    }

    public String getUsernickname() {
        return usernickname;
    }

    public void setUsernickname(String usernickname) {
        this.usernickname = usernickname;
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

    public String getReadstate() {
        return readstate;
    }

    public void setReadstate(String readstate) {
        this.readstate = readstate;
    }

    public String getSenddate() {
        return senddate;
    }

    public void setSenddate(String senddate) {
        this.senddate = senddate;
    }

    public Bitmap getImgbitmap() {
        return imgbitmap;
    }

    public void setImgbitmap(Bitmap imgbitmap) {
        this.imgbitmap = imgbitmap;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public String getOpenchatroomid() {
        return openchatroomid;
    }

    public void setOpenchatroomid(String openchatroomid) {
        this.openchatroomid = openchatroomid;
    }

    public String getRoomtitle() {
        return roomtitle;
    }

    public void setRoomtitle(String roomtitle) {
        this.roomtitle = roomtitle;
    }
}
