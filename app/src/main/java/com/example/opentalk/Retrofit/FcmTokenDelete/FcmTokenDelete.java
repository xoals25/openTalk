package com.example.opentalk.Retrofit.FcmTokenDelete;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FcmTokenDelete {

    @FormUrlEncoded
    @POST("fcmtokendelete.php")
    Call<FcmTokenDeleteData> postData(
            @FieldMap HashMap<String, Object> param
    );
}
