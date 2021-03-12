package com.example.opentalk.Retrofit.NicknameChange;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface NicknameChange {

    @FormUrlEncoded
    @POST("nickname_change.php")
    Call<NicknameChangeData> postData(
            @FieldMap HashMap<String,Object> param
            );
}
