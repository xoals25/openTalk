package com.example.opentalk.Retrofit.PwdCk;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PwdCk {

    @FormUrlEncoded
    @POST("pwdck.php")
    Call<PwdCkData> postData(
            @FieldMap HashMap<String,Object> param
    );
}
