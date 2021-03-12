package com.example.opentalk.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FriendCahtData {
    @Expose
    @SerializedName("roomnum") private String roomnum;
    @Expose
    @SerializedName("content") private String content;
    @Expose
    @SerializedName("to_userid") private String to_userid;
    @Expose
    @SerializedName("from_userid") private String from_userid;
    @Expose
    @SerializedName("from_nickname") private String from_nickname;
    @Expose
    @SerializedName("readstate") private String readstate;
    @Expose
    @SerializedName("datetime") private String datetime;
    @Expose
    @SerializedName("msgtype") private String msgtype;

    public String getRoomnum() {
        return roomnum;
    }

    public void setRoomnum(String roomnum) {
        this.roomnum = roomnum;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTo_userid() {
        return to_userid;
    }

    public void setTo_userid(String to_userid) {
        this.to_userid = to_userid;
    }

    public String getFrom_userid() {
        return from_userid;
    }

    public void setFrom_userid(String from_userid) {
        this.from_userid = from_userid;
    }

    public String getFrom_nickname() {
        return from_nickname;
    }

    public void setFrom_nickname(String from_nickname) {
        this.from_nickname = from_nickname;
    }

    public String getReadstate() {
        return readstate;
    }

    public void setReadstate(String readstate) {
        this.readstate = readstate;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }
}
