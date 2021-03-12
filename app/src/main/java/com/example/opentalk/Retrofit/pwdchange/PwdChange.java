package com.example.opentalk.Retrofit.pwdchange;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PwdChange {

    @FormUrlEncoded
    @POST("pwdchange.php")
    Call<PwdChangeData> postData(
            @FieldMap HashMap<String,Object> param
            );
}
