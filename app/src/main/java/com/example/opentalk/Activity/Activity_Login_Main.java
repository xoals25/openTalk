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

    /*?????? ????????? ??????*/
    SignInButton google_sign_in_button;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 0;
    int back = 1;

    /*???????????? ??????*/
    VideoView videoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);

        init();

        /*?????? ??????????????? ??????*/
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
                informDialog("????????? \'" + logindata.getUserid() + "\' ??? ?????????????????? ??????????????? ???????????? ???????????????.");
            }
        }
        /**/
        if(Activity_Lobby.mainContext!=null){
            Activity_Lobby.mainContext=null;
        }
        /*?????? ????????? ??????*/
        google_login_btn_layout = (ConstraintLayout)findViewById(R.id.google_login_btn_layout);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        /*(??????????????? ?????? ?????? ??? ?????? ?????????)????????? ????????? ???????????? ?????? ????????????????????? ?????? ?????????????????????*/
        signOut();
        /*?????? ????????? ??????*/
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
    //?????? ?????? ?????? ??? NotificationAndFCM ????????? ?????? FcmService.class??? onNewToken???????????? ????????????.
    //?????? splash????????? ?????? ?????? ??????? ->?????? ????????? ?????? ?????? ?????????????????? ?????? ???????????? ?????? ????????? ???????????? Activity_Login_Main??? ????????? ??????????????? ????????????.
    //???????????? ???????????? ????????? ??????????????? ????????????.
    private void getFcmtoken(){
        Log.d(TAG, "init: ????????? ????????????????????");
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

    /*?????? ?????? ?????????*/
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    /*?????? ?????? ????????????*/
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
    /*??????????????? ???????????? ????????? ??????????????? ????????? ??????*/
    private void googleloginCk(String userid){
        LoginCk loginCk = ApiClient.getApiClient().create(LoginCk.class);
        Call<LoginCkData> call = loginCk.getUserId(userid);
        call.enqueue(new Callback<LoginCkData>() {
            @Override
            public void onResponse(Call<LoginCkData> call, Response<LoginCkData> response) {
                Log.d(TAG, "onResponse: response : : : "+response.body());
                boolean resultck = response.body().isResult();
                Log.d(TAG, "onResponse: resultck : "+resultck);
                /*userid??? ????????? ???????????? ?????? ??????*/
                if(resultck==true){
                    //???????????? ?????????,?????? uri?????? ?????? string?????? ????????????.
                    Intent intent = new Intent(Activity_Login_Main.this, Activity_sociallogin_detail_information.class);
                    intent.putExtra("userid",userid);
                    startActivityForResult(intent,back);
                }
                /*????????????*/
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

        Log.d(TAG, "onActivityResult: ??????0");
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "onActivityResult: ??????1");
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        else if(requestCode==back && resultCode==RESULT_CANCELED ){
            Log.d(TAG, "onActivityResult: ??????2");
            signOut();
        }
    }

    /*?????? ???????????? ???????????? ?????? ?????? ?????? ??????*/
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
                        //????????? ???????????? ????????? ????????? ???????????????
                        Logindata logindata = new Logindata(userid,null,usernickname,userimg);
                        //??????????????? ???????????? ??????(???????????????)
                        setLoignDataPref(Activity_Login_Main.this,"login_inform",logindata);
                        Intent intent = new Intent(Activity_Login_Main.this, Activity_Lobby.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    else{
                        Log.d(TAG, "onResponse: loginPrimiary ?????? : "+result);
                    }
                }
                else {
                    Log.d(TAG, "onResponse: loginPrimiary ?????? : "+response.toString());
                }
            }

            @Override
            public void onFailure(Call<LoginPrimiaryData> call, Throwable t) {
                Log.d(TAG, "onResponse: loginPrimiary ?????? t : "+t.toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        /*????????? ??? ??????*/
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
