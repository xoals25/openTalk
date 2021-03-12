package com.example.opentalk.Retrofit.InviteFriendList;

import com.example.opentalk.Retrofit.FriendCahtData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface InviteFriendList {
    @FormUrlEncoded
    @POST("friend_invite_list.php")
    Call<List<InviteFriendListData>> postMyid(
            @Field("myid") String myid
    );

}
