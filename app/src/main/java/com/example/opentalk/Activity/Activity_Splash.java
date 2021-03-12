package com.example.opentalk.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import com.example.opentalk.Data.Logindata;
import com.example.opentalk.R;
import com.example.opentalk.Retrofit.ApiClient;
import com.example.opentalk.Retrofit.Login.Ck.LoginPrimiaryCk;
import com.example.opentalk.Retrofit.Login.Ck.LoginPrimiaryCkData;
import com.example.opentalk.Retrofit.Login.LoginPrimiary;
import com.example.opentalk.Retrofit.Login.LoginPrimiaryData;
import com.example.opentalk.SharedPreference.PreferenceManager_member;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.opentalk.SharedPreference.PreferenceManager_member.getLoignDataPref;

public class Activity_Splash extends AppCompatActivity {
    String TAG ="Activity_Splash";

    /*안드로이드 고유번호,기기 고유번호*/
    String androidid;
    String deviceid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__splash);
        init();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                /*
                 *자동 로그인 저장 하기
                 * -1 = 자동 로그인 설정이 안되어있다면 (mypage에서 설정 해제 아니면 모두 -1로 설정 ->로그인 하면 1로 설정하기 위함)
                 * 1 = 자동 로그인 설정 했을때
                 * 2 = 자동 로그인 해제 했을때 (로그인시 다시 1로 설정되어 자동로그인 설정 방지)
                 * */
                //자동 로그인 되어있으면 Lobby로 아니라면 현재 액티비티 유지
                if(PreferenceManager_member.getInt(Activity_Splash.this,"autoLogin")==1){
                    Logindata logindata = getLoignDataPref(Activity_Splash.this,"login_inform");
                    loginPrimiaryCk(logindata.getUserid(),androidid);
                }
                else{
                    Intent intent = new Intent(Activity_Splash.this,Activity_Login_Main.class);
                    finish();
                    startActivity(intent);
                }
            }
        },3000);

    }

    private void init(){
        androidid = Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);
    }

    /*해당 아이디를 로그인한 기기 정보 저장 통신*/
    private void loginPrimiaryCk(String userid,String deviceid){
        LoginPrimiaryCk loginPrimiaryCk = ApiClient.getApiClient().create(LoginPrimiaryCk.class);
        HashMap<String,Object> param = new HashMap<>();
        param.put("userid",userid);
        param.put("deviceid",deviceid);
        Call<LoginPrimiaryCkData> call = loginPrimiaryCk.postData(param);
        call.enqueue(new Callback<LoginPrimiaryCkData>() {
            @Override
            public void onResponse(Call<LoginPrimiaryCkData> call, Response<LoginPrimiaryCkData> response) {
                if(response.isSuccessful()){
                    boolean result = response.body().isResult();
                    if(result==true){
                        Intent intent = new Intent(Activity_Splash.this,Activity_Lobby.class);
                        finish();
                        startActivity(intent);
                    }
                    else{
                        Intent intent = new Intent(Activity_Splash.this,Activity_Login_Main.class);
                        PreferenceManager_member.setInt(Activity_Splash.this,"autoLogin",-1);
                        intent.putExtra("CompulsoryLogout","yes");
                        finish();
                        startActivity(intent);
                    }
                }
                else {
                    Log.d(TAG, "onResponse: loginPrimiary 실패 : "+response.toString());
                }
            }

            @Override
            public void onFailure(Call<LoginPrimiaryCkData> call, Throwable t) {
                Log.d(TAG, "onResponse: loginPrimiary 실패 t : "+t.toString());
            }
        });
    }
}