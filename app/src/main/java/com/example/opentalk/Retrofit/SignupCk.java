package com.example.opentalk.Retrofit;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SignupCk {

    @FormUrlEncoded
    @POST("signIdCk.php")
    Call<SignupCkData> getUserId(
            @Field("userid") String userid
    );
    @FormUrlEncoded
    @POST("signNickNameCk.php")
    Call<SignupCkData> getNickname(
            @Field("usernickname") String usernickname
    );
}
