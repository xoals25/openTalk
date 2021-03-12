package com.example.opentalk.Retrofit.ChatPaging;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatPagingData {


    @Expose
    @SerializedName("content") private String content;
    @Expose
    @SerializedName("nickname") private String nickname;
    @Expose
    @SerializedName("readstate") private String readstate;
    @Expose
    @SerializedName("datetime") private String datetime;
    @Expose
    @SerializedName("msgtype") private String msgtype;
    @Expose
    @SerializedName("openchatroomid") private String openchatroomid;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public String getOpenchatroomid() {
        return openchatroomid;
    }

    public void setOpenchatroomid(String openchatroomid) {
        this.openchatroomid = openchatroomid;
    }
}
