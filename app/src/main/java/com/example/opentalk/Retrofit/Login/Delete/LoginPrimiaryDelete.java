package com.example.opentalk.Retrofit.Login.Delete;

import com.example.opentalk.Retrofit.Login.LoginPrimiaryData;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginPrimiaryDelete {

    @FormUrlEncoded
    @POST("LoginPrimiaryDelete.php")
    Call<LoginPrimiaryDeleteData> postData(
            @FieldMap HashMap<String, Object> param
    );
}
