package com.example.opentalk.Retrofit.Login;

import com.example.opentalk.Retrofit.Login.LoginPrimiaryData;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginPrimiary {

    @FormUrlEncoded
    @POST("LoginPrimiary.php")
    Call<LoginPrimiaryData> postData(
            @FieldMap HashMap<String,Object> param
            );
}
