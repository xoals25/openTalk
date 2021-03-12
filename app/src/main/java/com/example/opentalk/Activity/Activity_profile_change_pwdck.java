package com.example.opentalk.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.opentalk.R;
import com.example.opentalk.Retrofit.ApiClient;
import com.example.opentalk.Retrofit.PwdCk.PwdCk;
import com.example.opentalk.Retrofit.PwdCk.PwdCkData;
import com.example.opentalk.SecurityUtil.SecurityUtilSHA;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_profile_change_pwdck extends AppCompatActivity {
    String TAG = "Activity_profile_change_pwdck";
    /*비밀번호 확인 입력란*/
    EditText profile_chagnge_pwdck_input;
    Button signup_signup_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_change_pwdck);
        init();

        signup_signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profile_chagnge_pwdck_input.getText().length()>0) {
                    pwdCk(SecurityUtilSHA.encryptSHA256(profile_chagnge_pwdck_input.getText().toString()));
                }
            }
        });
    }

    private void init(){
        profile_chagnge_pwdck_input = (EditText)findViewById(R.id.profile_chagnge_pwdck_input);
        signup_signup_btn = (Button)findViewById(R.id.signup_signup_btn);
    }

    private void pwdCk(String pwdSHA){
        PwdCk pwdCk = ApiClient.getApiClient().create(PwdCk.class);
        HashMap<String,Object> param = new HashMap<>();
        param.put("userid",Activity_Lobby.userid);
        param.put("pwd",pwdSHA);
        Call<PwdCkData> call = pwdCk.postData(param);
        call.enqueue(new Callback<PwdCkData>() {
            @Override
            public void onResponse(Call<PwdCkData> call, Response<PwdCkData> response) {
                if (response.isSuccessful()){
                    boolean resultck = response.body().isResult();
                    if(resultck==true){
                        Intent intent = new Intent(Activity_profile_change_pwdck.this, Activity_profile_change_pwdchange.class);
                        intent.putExtra("userid",Activity_Lobby.userid);
                        intent.putExtra("logining","ing");
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(Activity_profile_change_pwdck.this,"입력하신 비밀번호가 올바르지 않습니다. 다시 확인해주세요.",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Log.d(TAG, "onResponse: 실패");
                }
            }

            @Override
            public void onFailure(Call<PwdCkData> call, Throwable t) {
                Log.d(TAG, "onFailure: 실패 t : "+t.toString());
            }
        });
    }
}