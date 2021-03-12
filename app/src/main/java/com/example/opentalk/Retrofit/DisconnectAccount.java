package com.example.opentalk.Retrofit;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface DisconnectAccount {

    @FormUrlEncoded
    @POST("disconnect_account.php")
    Call<DisconnectAccountData> postData(
            @FieldMap HashMap<String,Object> param
            );
}
