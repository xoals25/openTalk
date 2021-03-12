package com.example.opentalk.Data;

import android.graphics.Bitmap;

public class Friend_List_Data {

    int friend_table_id;
    String friend_email;
    String friend_nickname;
    String connect; //"offline"이면 오프라인, "online"이면 온라인
    String imgstring_tobitmap;
    String uniquename;
    int chatnum;
    String lastchatmsg;

    public Friend_List_Data(int friend_table_id, String friend_email, String friend_nickname, String connect, String imgstring_tobitmap, String uniquename, int chatnum, String lastchatmsg) {
        this.friend_table_id = friend_table_id;
        this.friend_email = friend_email;
        this.friend_nickname = friend_nickname;
        this.connect = connect;
        this.imgstring_tobitmap = imgstring_tobitmap;
        this.uniquename = uniquename;
        this.chatnum = chatnum;
        this.lastchatmsg = lastchatmsg;
    }

    public int getFriend_table_id() {
        return friend_table_id;
    }

    public void setFriend_table_id(int friend_table_id) {
        this.friend_table_id = friend_table_id;
    }

    public String getFriend_email() {
        return friend_email;
    }

    public void setFriend_email(String friend_email) {
        this.friend_email = friend_email;
    }

    public String getFriend_nickname() {
        return friend_nickname;
    }

    public void setFriend_nickname(String friend_nickname) {
        this.friend_nickname = friend_nickname;
    }

    public String getConnect() {
        return connect;
    }

    public void setConnect(String connect) {
        this.connect = connect;
    }

    public String getImgstring_tobitmap() {
        return imgstring_tobitmap;
    }

    public void setImgstring_tobitmap(String imgstring_tobitmap) {
        this.imgstring_tobitmap = imgstring_tobitmap;
    }

    public String getUniquename() {
        return uniquename;
    }

    public void setUniquename(String uniquename) {
        this.uniquename = uniquename;
    }

    public int getChatnum() {
        return chatnum;
    }

    public void setChatnum(int chatnum) {
        this.chatnum = chatnum;
    }

    public String getLastchatmsg() {
        return lastchatmsg;
    }

    public void setLastchatmsg(String lastchatmsg) {
        this.lastchatmsg = lastchatmsg;
    }
}

