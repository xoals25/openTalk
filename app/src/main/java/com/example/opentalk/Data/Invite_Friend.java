package com.example.opentalk.Data;

public class Invite_Friend {
    String friendId;
    String friendNickName;
    String uniquename;

    public Invite_Friend(String friendId, String friendNickName, String uniquename) {
        this.friendId = friendId;
        this.friendNickName = friendNickName;
        this.uniquename = uniquename;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getFriendNickName() {
        return friendNickName;
    }

    public void setFriendNickName(String friendNickName) {
        this.friendNickName = friendNickName;
    }

    public String getUniquename() {
        return uniquename;
    }

    public void setUniquename(String uniquename) {
        this.uniquename = uniquename;
    }
}
