package com.example.opentalk.Activity;


import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.opentalk.Data.Logindata;
import com.example.opentalk.R;
import com.example.opentalk.Retrofit.ApiClient;
import com.example.opentalk.Retrofit.Login.LoginPrimiary;
import com.example.opentalk.Retrofit.Login.LoginPrimiaryData;
import com.example.opentalk.Retrofit.LoginCk;
import com.example.opentalk.Retrofit.LoginCkData;
import com.example.opentalk.SharedPreference.PreferenceManager_member;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.opentalk.SharedPreference.PreferenceManager_member.getLoignDataPref;
import static com.example.opentalk.SharedPreference.PreferenceManager_member.setLoignDataPref;

public class Activity_Login_Main extends AppCompatActivity {
    String TAG ="Activity_login_main";

    ConstraintLayout google_login_btn_layout;
    ConstraintLayout other_login_btn_layout;

    /*구글 로그인 관련*/
    SignInButton google_sign_in_button;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 0;
    int back = 1;

    /*비디오뷰 관련*/
    VideoView videoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);

        init();

        /*구글 소셜로그인 버튼*/
        google_login_btn_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        other_login_btn_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Login_Main.this,Activity_Login.class);
                startActivity(intent);
            }
        });

    }

    private void init(){

        if(!TextUtils.isEmpty(getIntent().getStringExtra("CompulsoryLogout"))) {
            if (getIntent().getExtras().getString("CompulsoryLogout", "").equals("yes")) {
                Logindata logindata = getLoignDataPref(this, "login_inform");
                informDialog("아이디 \'" + logindata.getUserid() + "\' 가 다른기기에서 로그인되어 로그아웃 되었습니다.");
            }
        }
        /**/
        if(Activity_Lobby.mainContext!=null){
            Activity_Lobby.mainContext=null;
        }
        /*구글 로그인 관련*/
        google_login_btn_layout = (ConstraintLayout)findViewById(R.id.google_login_btn_layout);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        /*(로그아웃을 해서 혹은 그 외의 이유로)처음에 로그인 화면으로 가면 로그인되어있는 구글 로그아웃해주기*/
        signOut();
        /*다른 로그인 관련*/
        other_login_btn_layout = (ConstraintLayout)findViewById(R.id.other_login_btn_layout);
        /*FCM Token GET*/
        getFcmtoken();

    }

    private void informDialog(String informtext){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_dialog_inform, null);
        builder.setView(view);
        TextView alert_text = (TextView) view.findViewById(R.id.alert_text);
        final AlertDialog dialog = builder.create();
            alert_text.setText(informtext);
            Button alert_btn = (Button) view.findViewById(R.id.alert_btn);
            alert_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    /*FCM Token GET AND Shared save*/
    //토큰 처음 생길 때 NotificationAndFCM 폴더에 있는 FcmService.class의 onNewToken메소드가 작동한다.
    //이걸 splash화면에 옮겨 줘야 할까? ->근데 어차피 처음 한번 만들어주기만 하면 되는거라 처음 깔렸을 경우에는 Activity_Login_Main이 무조건 실행되니까 괜찮을듯.
    //로그아웃 하더라도 여기를 거쳐가니까 괜찮을듯.
    private void getFcmtoken(){
        Log.d(TAG, "init: 여기가 시작되는건가??");
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        Log.d("TAG", token);
                    }});
    }

    /*구글 소셜 로그인*/
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    /*구글 소셜 로그아웃*/
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                String personEmail = acct.getEmail();
                googleloginCk(personEmail);
            }

//            Intent intent = new Intent(this,Activity_Lobby.class);
//            startActivity(intent);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }
    /*소셜로그인 아이디로 가입이 되어있는지 아닌지 확인*/
    private void googleloginCk(String userid){
        LoginCk loginCk = ApiClient.getApiClient().create(LoginCk.class);
        Call<LoginCkData> call = loginCk.getUserId(userid);
        call.enqueue(new Callback<LoginCkData>() {
            @Override
            public void onResponse(Call<LoginCkData> call, Response<LoginCkData> response) {
                Log.d(TAG, "onResponse: response : : : "+response.body());
                boolean resultck = response.body().isResult();
                Log.d(TAG, "onResponse: resultck : "+resultck);
                /*userid로 가입된 아이디가 없을 경우*/
                if(resultck==true){
                    //인텐트로 이메일,사진 uri혹은 사진 string값을 넘겨주기.
                    Intent intent = new Intent(Activity_Login_Main.this, Activity_sociallogin_detail_information.class);
                    intent.putExtra("userid",userid);
                    startActivityForResult(intent,back);
                }
                /*있을경우*/
                else{
                    String userid = response.body().getUserid();
                    String usernickname = response.body().getUsernickname();
                    String userimg = response.body().getUserimg();
                    String androidid = Settings.Secure.getString(Activity_Login_Main.this.getContentResolver(),Settings.Secure.ANDROID_ID);
                    loginPrimiary(userid,usernickname,userimg,androidid);
                }
            }

            @Override
            public void onFailure(Call<LoginCkData> call, Throwable t) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: 확인0");
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "onActivityResult: 확인1");
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        else if(requestCode==back && resultCode==RESULT_CANCELED ){
            Log.d(TAG, "onActivityResult: 확인2");
            signOut();
        }
    }

    /*해당 아이디를 로그인한 기기 정보 저장 통신*/
    private void loginPrimiary(String userid,String usernickname,String userimg,String deviceid){
        LoginPrimiary loginPrimiary = ApiClient.getApiClient().create(LoginPrimiary.class);
        HashMap<String,Object> param = new HashMap<>();
        param.put("userid",userid);
        param.put("deviceid",deviceid);
        Call<LoginPrimiaryData> call = loginPrimiary.postData(param);
        call.enqueue(new Callback<LoginPrimiaryData>() {
            @Override
            public void onResponse(Call<LoginPrimiaryData> call, Response<LoginPrimiaryData> response) {
                if(response.isSuccessful()){
                    boolean result = response.body().isResult();
                    if(result==true){
                        //로그인 데이터에 알맞게 아이템 장착해주기
                        Logindata logindata = new Logindata(userid,null,usernickname,userimg);
                        //로그인정보 쉐어드에 저장(어레이버전)
                        setLoignDataPref(Activity_Login_Main.this,"login_inform",logindata);
                        Intent intent = new Intent(Activity_Login_Main.this, Activity_Lobby.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    else{
                        Log.d(TAG, "onResponse: loginPrimiary 실패 : "+result);
                    }
                }
                else {
                    Log.d(TAG, "onResponse: loginPrimiary 실패 : "+response.toString());
                }
            }

            @Override
            public void onFailure(Call<LoginPrimiaryData> call, Throwable t) {
                Log.d(TAG, "onResponse: loginPrimiary 실패 t : "+t.toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        /*비디오 뷰 관련*/
        videoView = (VideoView)findViewById(R.id.video_view);
        videoView.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.loginmainvideoscreen));
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        NotificationManager nm =(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(1234);
    }
}
