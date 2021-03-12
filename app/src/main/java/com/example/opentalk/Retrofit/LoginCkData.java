package com.example.opentalk.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginCkData {

    @Expose
    @SerializedName("result") private boolean result;
    @Expose
    @SerializedName("userid") private String userid;
    @Expose
    @SerializedName("usernickname") private String usernickname;
    @Expose
    @SerializedName("userimg") private String userimg;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsernickname() {
        return usernickname;
    }

    public void setUsernickname(String usernickname) {
        this.usernickname = usernickname;
    }

    public String getUserimg() {
        return userimg;
    }

    public void setUserimg(String userimg) {
        this.userimg = userimg;
    }
}
