package com.example.opentalk.Data;

public class VoiceChatRoom_Participant_List_Data {

    String nickname;
    String owner;
    String profile_img;
    boolean mike_onoff;
    boolean speaker_onoff;

    public VoiceChatRoom_Participant_List_Data(String nickname, String owner, String profile_img, boolean mike_onoff, boolean speaker_onoff) {
        this.nickname = nickname;
        this.owner = owner;
        this.profile_img = profile_img;
        this.mike_onoff = mike_onoff;
        this.speaker_onoff = speaker_onoff;
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

    public boolean isMike_onoff() {
        return mike_onoff;
    }

    public void setMike_onoff(boolean mike_onoff) {
        this.mike_onoff = mike_onoff;
    }

    public boolean isSpeaker_onoff() {
        return speaker_onoff;
    }

    public void setSpeaker_onoff(boolean speaker_onoff) {
        this.speaker_onoff = speaker_onoff;
    }
}
