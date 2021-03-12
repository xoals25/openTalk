package com.example.opentalk.Data;

public class FaceChatRoom_Participant_List_Data {

    String nickname;
    String owner;
    String profile_img;

    public FaceChatRoom_Participant_List_Data(String nickname, String owner, String profile_img) {
        this.nickname = nickname;
        this.owner = owner;
        this.profile_img = profile_img;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }
}
