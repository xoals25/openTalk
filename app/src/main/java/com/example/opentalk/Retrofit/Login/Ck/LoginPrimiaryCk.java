package com.example.opentalk.Retrofit.Login.Ck;

import com.example.opentalk.Retrofit.Login.LoginPrimiaryData;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginPrimiaryCk {

    @FormUrlEncoded
    @POST("LoginPrimiaryCk.php")
    Call<LoginPrimiaryCkData> postData(
            @FieldMap HashMap<String, Object> param
    );
}
