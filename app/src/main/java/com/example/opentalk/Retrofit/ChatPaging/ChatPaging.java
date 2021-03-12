package com.example.opentalk.Retrofit.ChatPaging;

import com.example.opentalk.Retrofit.Lobby_Search_RoomData;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ChatPaging {

    @FormUrlEncoded
    @POST("chatpaging.php")
    Call<List<ChatPagingData>> postData(
            @FieldMap HashMap<String,Object> param
    );
}
