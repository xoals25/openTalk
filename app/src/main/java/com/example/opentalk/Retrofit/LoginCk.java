package com.example.opentalk.Retrofit;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginCk {

    @FormUrlEncoded
    @POST("loginck.php")
    Call<LoginCkData> getUserId(
            @Field("userid") String userid
    );
}
