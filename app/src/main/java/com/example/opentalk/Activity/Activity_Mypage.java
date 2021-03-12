package com.example.opentalk.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.opentalk.R;
import com.example.opentalk.Retrofit.ApiClient;
import com.example.opentalk.Retrofit.DisconnectAccount;
import com.example.opentalk.Retrofit.DisconnectAccountData;
import com.example.opentalk.Retrofit.FcmTokenDelete.FcmTokenDelete;
import com.example.opentalk.Retrofit.FcmTokenDelete.FcmTokenDeleteData;
import com.example.opentalk.Retrofit.SignupCk;
import com.example.opentalk.Retrofit.SignupCkData;
import com.example.opentalk.SharedPreference.PreferenceManager_member;
import com.example.opentalk.Socket_my.Socket_Other_LoginCk_thread;
import com.example.opentalk.Socket_my.Socket_connect_offline_thread;
import com.example.opentalk.Socket_my.Socket_friend_choose_thread;
import com.example.opentalk.Socket_my.Socket_friend_disconnect_account_thread;

import org.json.JSONObject;

import java.net.Socket;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;

/*
*
* 2021 -01 -17 생성
*
* 자동 로그인,로그아웃,알림 설정, 정보변경, 회원탈퇴 기능이 있다.
*
* */

public class Activity_Mypage extends AppCompatActivity {

    String TAG = "Activity_Mypage";
    Toolbar myToolbar;

    /*자동 로그인 관련*/
//    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch autologin_onoff_btn;

    /*로그아웃 관련*/
    LinearLayout logout_btn_linear;
    TextView logout_btn;
    /*회원탈퇴 관련*/
    LinearLayout disconnect_account_linear;

    /*정보수정 관련*/
    LinearLayout mypage_profile_change_btn;

    /*안드로이드 고유번호,기기 고유번호*/
    String androidid;
    String deviceid;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        init();
        /*자동로그인 온오프*/
        autologin_onoff_btn.setOnClickListener(new AutoLoginSwitchListner());
        /*로그아웃*/
        logout_btn_linear.setOnClickListener(new LogoutListner());

        disconnect_account_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnClickHandler();
            }
        });

        /*정보수정 관련*/
        mypage_profile_change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Mypage.this,Activity_Profile_Change.class);
                startActivity(intent);
            }
        });
    }

    private void init(){
        // 추가된 소스, Toolbar를 생성한다.
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기(툴바 맨왼쪽에 생김)
        /*자동 로그인 switch btn 1이라면 체크되어 있게*/
        autologin_onoff_btn = (Switch) findViewById(R.id.autologin_onoff_btn);
        if(PreferenceManager_member.getInt(this,"autoLogin")==1){
            autologin_onoff_btn.setChecked(true);
        }
        /*로그아웃 관련*/
        logout_btn_linear = (LinearLayout) findViewById(R.id.logout_btn_linear);
        logout_btn = (TextView) findViewById(R.id.logout_btn);
        /*회원탈퇴 관련*/
        disconnect_account_linear = (LinearLayout)findViewById(R.id.disconnect_account_linear);
        /*정보수정 관련*/
        mypage_profile_change_btn = (LinearLayout)findViewById(R.id.mypage_profile_change_btn);

        androidid = Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);
    }

    /*회원탈퇴시 뜨는 다이얼로그*/
    public void OnClickHandler()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("회원탈퇴 확인").setMessage("정말 회원탈퇴 하시겠습니까?");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                //자바 소켓통신으로 friend_list,request도 제거해주기
                try{
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type","disconnect");
                    jsonObject.put("userid", Activity_Lobby.userid);
                    Socket_friend_disconnect_account_thread disconnect_account_thread = new Socket_friend_disconnect_account_thread(jsonObject);
                    disconnect_account_thread.start();
                }
                catch (Exception e){
                    Log.d(TAG, "onClick: "+e.getMessage());
                }
                /*레트로핏 통신으로 회원정보 관련 다 없애기*/
                disconnect_account(Activity_Lobby.userid,androidid);


            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /*회원탈퇴를 위한 통신 (레트로핏)*/
    public void disconnect_account(String userid,String deviceid){
        DisconnectAccount disconnectAccount = ApiClient.getApiClient().create(DisconnectAccount.class);
        HashMap<String,Object> param = new HashMap<>();
        param.put("userid",userid);
        param.put("deviceid",deviceid);
        Call<DisconnectAccountData> call = disconnectAccount.postData(param);
        call.enqueue(new Callback<DisconnectAccountData>() {
            @Override
            public void onResponse(Call<DisconnectAccountData> call, retrofit2.Response<DisconnectAccountData> response) {
                if(response.isSuccessful() && response.body() !=null){
                    boolean resultck = response.body().isResult();
                    Log.d(TAG, "onResponse: resultck : "+resultck);
                    //회원탈퇴 완료
                    if(resultck==true){
                        Intent intent = new Intent(Activity_Mypage.this, Activity_Login_Main.class);
                        PreferenceManager_member.setInt(Activity_Mypage.this,"autoLogin",-1);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        Socket_connect_offline_thread socket_connect_offline_thread = new Socket_connect_offline_thread();
                        socket_connect_offline_thread.start();
                        startActivity(intent);
                    }
                    //회원탈퇴 실패
                    else {

                    }
                }
            }
            @Override
            public void onFailure(Call<DisconnectAccountData> call, Throwable t) {

            }
        });
    }

    /*로그아웃할 아이디에 해당하는 토큰을 제거 위한 통신 (레트로핏)*/
    /*토큰 제거 및 현 deviceid에 해당하는 userid 삭제*/
    public void deleteToken(String userid,String deviceid){
        FcmTokenDelete fcmTokenDelete = ApiClient.getApiClient().create(FcmTokenDelete.class);
        HashMap<String,Object> param = new HashMap<>();
        param.put("userid",userid);
        param.put("deviceid",deviceid);
        Call<FcmTokenDeleteData> call = fcmTokenDelete.postData(param);
        call.enqueue(new Callback<FcmTokenDeleteData>() {
            @Override
            public void onResponse(Call<FcmTokenDeleteData> call, retrofit2.Response<FcmTokenDeleteData> response) {
                if(response.isSuccessful() && response.body() !=null){
                    boolean resultck = response.body().isResult();
                    Log.d(TAG, "onResponse: resultck : "+resultck);

                    if(resultck==true){
                        Intent intent = new Intent(Activity_Mypage.this, Activity_Login_Main.class);
                        PreferenceManager_member.setInt(Activity_Mypage.this,"autoLogin",-1);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        Socket_connect_offline_thread socket_connect_offline_thread = new Socket_connect_offline_thread();
                        socket_connect_offline_thread.start();
                        try {
                            //여기서만 해주는 이유는 위에껄로 하면 자신도 로그아웃 되기때문이다.
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("type", "logout");
                            jsonObject.put("userid", userid);
                            Socket socket_other_loginCk = null;
                            Socket_Other_LoginCk_thread socket_other_loginCk_thread =
                                    new Socket_Other_LoginCk_thread(socket_other_loginCk,userid,Activity_Mypage.this,false,jsonObject);
                            socket_other_loginCk_thread.start();
                            //여기에 http통신으로 상대 유저아이디에 등록된 기기 삭제
                        }
                        catch (Exception e){
                            Log.d(TAG, "Activity_pwd_found_pwdck Socket_Other_LoginCk_thread:  error message : "+e.getMessage());
                        }
                        startActivity(intent);
                    }

                    else {

                    }
                }
            }
            @Override
            public void onFailure(Call<FcmTokenDeleteData> call, Throwable t) {

            }
        });
    }

    /*자동 로그인 온오프*/
    class AutoLoginSwitchListner implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            /*
             *자동 로그인 저장 하기
             * -1 = 자동 로그인 설정이 안되어있다면
             * 1 = 자동 로그인 설정 했을때
             * 2 = 자동 로그인 해제 했을때
             * */
            Log.d(TAG, "autologin_onoff_btn.isChecked() : "+autologin_onoff_btn.isChecked());
            if(autologin_onoff_btn.isChecked()==false){
                Log.d(TAG, "autologin_onoff_btn: 여기로 오네1");
                PreferenceManager_member.setInt(Activity_Mypage.this,"autoLogin",2);
            }
            else{
                Log.d(TAG, "autologin_onoff_btn: 여기로 오네2");
                PreferenceManager_member.setInt(Activity_Mypage.this,"autoLogin",1);
            }
        }
    }
    /*로그아웃*/
    class LogoutListner implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            deleteToken(Activity_Lobby.userid,androidid);
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                return false;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
