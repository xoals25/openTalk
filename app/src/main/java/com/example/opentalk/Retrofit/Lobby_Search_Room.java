package com.example.opentalk.Retrofit;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Lobby_Search_Room {

    @FormUrlEncoded
    @POST("search.php")
    Call<List<Lobby_Search_RoomData>> postData(
            @FieldMap HashMap<String,Object> param
            );
}
