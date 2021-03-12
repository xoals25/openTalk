package com.example.opentalk.Retrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FriendChat {

    @FormUrlEncoded
    @POST("friendchatlist.php")
    Call<List<FriendCahtData>> postRoomname(
            @Field("roomname") String roomname
    );
}
