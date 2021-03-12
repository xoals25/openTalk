package com.example.opentalk.Data;

public class Lobby_Rooms_Data {
    int id;//mysql의 room table 고유번호
    String room_title;
    String room_type;
    String room_public;
    int room_people_numlimit;
    int room_people_num;

    public Lobby_Rooms_Data(int id, String room_title, String room_type, String room_public, int room_people_numlimit, int room_people_num) {
        this.id = id;
        this.room_title = room_title;
        this.room_type = room_type;
        this.room_public = room_public;
        this.room_people_numlimit = room_people_numlimit;
        this.room_people_num = room_people_num;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoom_title() {
        return room_title;
    }

    public void setRoom_title(String room_title) {
        this.room_title = room_title;
    }

    public String getRoom_type() {
        return room_type;
    }

    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }

    public String getRoom_public() {
        return room_public;
    }

    public void setRoom_public(String room_public) {
        this.room_public = room_public;
    }

    public int getRoom_people_numlimit() {
        return room_people_numlimit;
    }

    public void setRoom_people_numlimit(int room_people_numlimit) {
        this.room_people_numlimit = room_people_numlimit;
    }

    public int getRoom_people_num() {
        return room_people_num;
    }

    public void setRoom_people_num(int room_people_num) {
        this.room_people_num = room_people_num;
    }
}
