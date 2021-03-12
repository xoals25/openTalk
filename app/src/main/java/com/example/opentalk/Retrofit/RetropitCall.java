package com.example.opentalk.Retrofit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetropitCall {

    public static void signupIdCk(String userid){
        SignupCk signupCk = ApiClient.getApiClient().create(SignupCk.class);
        Call<SignupCkData> call = signupCk.getUserId(userid);
        call.enqueue(new Callback<SignupCkData>() {
            @Override
            public void onResponse(Call<SignupCkData> call, Response<SignupCkData> response) {
                if(response.isSuccessful() && response.body() !=null){
                    boolean idck = response.body().isResult();
                    if(idck==true){

                    }
                    else {

                    }
                }
            }
            @Override
            public void onFailure(Call<SignupCkData> call, Throwable t) {

            }
        });
    }

    public static void signupNicknameCk(String userNickname){
        SignupCk signupCk = ApiClient.getApiClient().create(SignupCk.class);
        Call<SignupCkData> call = signupCk.getNickname(userNickname);
        call.enqueue(new Callback<SignupCkData>() {
            @Override
            public void onResponse(Call<SignupCkData> call, Response<SignupCkData> response) {
                boolean idck = response.body().isResult();
                if(idck==true){

                }
                else {

                }
            }

            @Override
            public void onFailure(Call<SignupCkData> call, Throwable t) {

            }
        });
    }

}
