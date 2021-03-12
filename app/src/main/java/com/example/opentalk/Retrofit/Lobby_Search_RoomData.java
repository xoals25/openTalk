package com.example.opentalk.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

public class Lobby_Search_RoomData {

    @Expose
    @SerializedName("rooms_id") private int rooms_id;
    @Expose
    @SerializedName("rooms_title") private String rooms_title;
    @Expose
    @SerializedName("rooms_type") private String rooms_type;
    @Expose
    @SerializedName("rooms_public") private String rooms_public;
    @Expose
    @SerializedName("rooms_person_numlimit") private int rooms_person_numlimit;
    @Expose
    @SerializedName("rooms_person_attend_num") private int rooms_person_attend_num;

    public int getRooms_id() {
        return rooms_id;
    }

    public void setRooms_id(int rooms_id) {
        this.rooms_id = rooms_id;
    }

    public String getRooms_title() {
        return rooms_title;
    }

    public void setRooms_title(String rooms_title) {
        this.rooms_title = rooms_title;
    }

    public String getRooms_type() {
        return rooms_type;
    }

    public void setRooms_type(String rooms_type) {
        this.rooms_type = rooms_type;
    }

    public String getRooms_public() {
        return rooms_public;
    }

    public void setRooms_public(String rooms_public) {
        this.rooms_public = rooms_public;
    }

    public int getRooms_person_numlimit() {
        return rooms_person_numlimit;
    }

    public void setRooms_person_numlimit(int rooms_person_numlimit) {
        this.rooms_person_numlimit = rooms_person_numlimit;
    }

    public int getRooms_person_attend_num() {
        return rooms_person_attend_num;
    }

    public void setRooms_person_attend_num(int rooms_person_attend_num) {
        this.rooms_person_attend_num = rooms_person_attend_num;
    }
}
