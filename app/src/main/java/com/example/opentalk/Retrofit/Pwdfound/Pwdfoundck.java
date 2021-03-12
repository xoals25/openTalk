package com.example.opentalk.Retrofit.Pwdfound;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Pwdfoundck {

    @FormUrlEncoded
    @POST("pwdfoundck.php")
    Call<PwdfoundckData> postData(
            @FieldMap HashMap<String,Object> param
            );
}
