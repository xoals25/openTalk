package com.example.opentalk.Retrofit.InviteFriendList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InviteFriendListData {

    @Expose
    @SerializedName("friendId") private String friendId;
    @Expose
    @SerializedName("friendNickname") private String friendNickname;
    @Expose
    @SerializedName("friend_connect_check") private String friend_connect_check;
    @Expose
    @SerializedName("uniquename") private String uniquename;

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getFriendNickname() {
        return friendNickname;
    }

    public void setFriendNickname(String friendNickname) {
        this.friendNickname = friendNickname;
    }

    public String getFriend_connect_check() {
        return friend_connect_check;
    }

    public void setFriend_connect_check(String friend_connect_check) {
        this.friend_connect_check = friend_connect_check;
    }

    public String getUniquename() {
        return uniquename;
    }

    public void setUniquename(String uniquename) {
        this.uniquename = uniquename;
    }
}
