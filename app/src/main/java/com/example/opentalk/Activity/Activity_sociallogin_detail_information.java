package com.example.opentalk.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.example.opentalk.BitmapConverter;
import com.example.opentalk.Data.Logindata;
import com.example.opentalk.R;
import com.example.opentalk.Retrofit.ApiClient;
import com.example.opentalk.Retrofit.Login.LoginPrimiary;
import com.example.opentalk.Retrofit.Login.LoginPrimiaryData;
import com.example.opentalk.Retrofit.SignupCk;
import com.example.opentalk.Retrofit.SignupCkData;
import com.example.opentalk.SecurityUtil.SecurityUtilSHA;
import com.example.opentalk.VolleyRequestQhelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;

import static com.example.opentalk.SharedPreference.PreferenceManager_member.setLoignDataPref;

public class Activity_sociallogin_detail_information extends AppCompatActivity {
    Toolbar myToolbar;
    String TAG ="Activity_sociallogin_detail_information";

    /*닉네임 관련*/
    EditText social_nickname_input;
    TextView social_nicknameck_text;
    ImageView Nicknameck_mark;
    boolean nicknameck = false; //중복이면 false 아니면 true

    /*이미지 관련*/
    String profile_img_bitmap_toString;
    Button social_signup_btn;
    ImageView imgtest;

    String personEmail;
    String usernickname;
    Uri personPhoto;

    /*인증*/
    EditText signup_phone_social;
    EditText signup_authnum_social;
    Button phone_btn_social;
    Button authnum_btn_social;
    String verificationId;
    boolean authnumck =false;
    ImageView phoneck_img_social;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sociallogin_detail_information);
        init();
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(Activity_sociallogin_detail_information.this);
        if (acct != null) {
            personEmail = acct.getEmail();

            Log.d(TAG, "onCreate: personEmail : "+personEmail);
            personPhoto = acct.getPhotoUrl();
            if(personPhoto!=null) {
                //지우면 안됨 이걸로 bitmap 뽑아오는것이에요.
                Glide.with(Activity_sociallogin_detail_information.this).load(personPhoto).into(imgtest);
            }
        }

        /*핸드폰 중복검사*/
        phone_btn_social.setOnClickListener(new phone_btn());
        authnum_btn_social.setOnClickListener(new authnum_btn());
        /*닉네임 중복 검사*/
        social_nickname_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(social_nickname_input.getText().toString().length()>0){
                    signupNicknameCk(social_nickname_input.getText().toString());
                }
                else {
                    nicknameck=false;
                    Nicknameck_mark.setVisibility(View.GONE);
                    social_nicknameck_text.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        social_signup_btn.setOnClickListener(new signup());
    }

    private void init(){
        // 추가된 소스, Toolbar를 생성한다.
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기(툴바 맨왼쪽에 생김)
        /*닉네임 관련*/
        social_nickname_input = (EditText)findViewById(R.id.social_nickname_input);
        social_nicknameck_text = (TextView) findViewById(R.id.social_nicknameck_text);
        Nicknameck_mark = (ImageView) findViewById(R.id.Nicknameck_mark);

        social_signup_btn = (Button) findViewById(R.id.social_signup_btn);
        imgtest = findViewById(R.id.imgtest);

        if(VolleyRequestQhelper.requestQueue==null){
            VolleyRequestQhelper.requestQueue = Volley.newRequestQueue(this);
        }
        /*인증*/
        signup_phone_social = (EditText)findViewById(R.id.signup_phone_social);
        phone_btn_social = (Button)findViewById(R.id.phone_btn_social);
        signup_authnum_social = (EditText)findViewById(R.id.signup_authnum_social);
        authnum_btn_social =(Button)findViewById(R.id.authnum_btn_social);
        phoneck_img_social =(ImageView)findViewById(R.id.phoneck_img_social);
    }

    /*회원가입 서버통신(볼리)*/
    public void signupUpload(String userid,String phone,String pwdSHA) {
        String serverUrl="http://3.36.188.116/opentalk/signup.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG", "onResponse: "+response);
                Intent getintent = getIntent();
                String userid = getintent.getExtras().getString("userid");
                String androidid = Settings.Secure.getString(Activity_sociallogin_detail_information.this.getContentResolver(),Settings.Secure.ANDROID_ID);
                loginPrimiary(userid,usernickname,profile_img_bitmap_toString,androidid);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: error : "+error.toString());
            }
        }
        ){
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("signup_Id", userid);
                Log.d(TAG, "getParams: personEmail"+personEmail);
                params.put("signup_nickname", usernickname);
                params.put("signup_pwd", pwdSHA);
                if(personPhoto!=null) {
                    params.put("signup_img", profile_img_bitmap_toString);
                }
                params.put("phonenum",phone);
                return params;
            }
        };
        VolleyRequestQhelper.requestQueue.add(stringRequest);

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
            public void onResponse(Call<LoginPrimiaryData> call, retrofit2.Response<LoginPrimiaryData> response) {
                if(response.isSuccessful()){
                    boolean result = response.body().isResult();
                    if(result==true){
                        //로그인 데이터에 알맞게 아이템 장착해주기
                        Logindata logindata = new Logindata(userid,null,usernickname,userimg);
                        //로그인정보 쉐어드에 저장(어레이버전)
                        setLoignDataPref(Activity_sociallogin_detail_information.this,"login_inform",logindata);
                        Intent intent = new Intent(Activity_sociallogin_detail_information.this, Activity_Lobby.class);
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

    /*닉네임 중복 체크 서버 통신 (레트로핏)*/
    public void signupNicknameCk(String userNickname){
        SignupCk signupCk = ApiClient.getApiClient().create(SignupCk.class);
        Call<SignupCkData> call = signupCk.getNickname(userNickname);
        call.enqueue(new Callback<SignupCkData>() {
            @Override
            public void onResponse(Call<SignupCkData> call, retrofit2.Response<SignupCkData> response) {
                boolean resultck = response.body().isResult();
                String userid = response.body().getUserid();
                Log.d(TAG, "onResponse: userid : "+userid);
                Log.d(TAG, "onResponse: resultck : "+resultck);
                if(resultck==true){
                    nicknameck=true;
                    Nicknameck_mark.setBackground(ContextCompat.getDrawable(Activity_sociallogin_detail_information.this,R.drawable.check_purple));
                    Nicknameck_mark.setVisibility(View.VISIBLE);
                    social_nicknameck_text.setVisibility(View.GONE);
                    social_nicknameck_text.setText("사용가능한 닉네임입니다.");
                }
                else {
                    nicknameck=false;
                    Nicknameck_mark.setBackground(ContextCompat.getDrawable(Activity_sociallogin_detail_information.this,R.drawable.redxmark));
                    Nicknameck_mark.setVisibility(View.VISIBLE);
                    social_nicknameck_text.setVisibility(View.VISIBLE);
                    social_nicknameck_text.setText("중복된 닉네임입니다.");
                }
            }

            @Override
            public void onFailure(Call<SignupCkData> call, Throwable t) {

            }
        });
    }

    /*회원가입 완료 메소드*/
    class signup implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(nicknameck==true && authnumck==true ){
                //볼리 통신으로 회원가입
                if(personPhoto!=null) {
                    BitmapDrawable drawable = (BitmapDrawable) imgtest.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    profile_img_bitmap_toString = BitmapConverter.BitmapToString(bitmap);
                }
                else{
                    profile_img_bitmap_toString="false";
                }
                usernickname = social_nickname_input.getText().toString();
                signupUpload(personEmail,signup_phone_social.getText().toString(), SecurityUtilSHA.encryptSHA256(""));
            }
            else if(nicknameck==false){
                if(social_nickname_input.getText().toString().length()==0){
                    Toast.makeText(Activity_sociallogin_detail_information.this,"닉네임을 입력 해주세요.",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Activity_sociallogin_detail_information.this,"닉네임을 중복검사 해주세요.",Toast.LENGTH_SHORT).show();
                }
            }
            else if(authnumck==false){
                Toast.makeText(Activity_sociallogin_detail_information.this,"인증번호를 확인해주세요.",Toast.LENGTH_SHORT).show();
            }
        }
    }
    /*핸드폰 인증발송 메소드*/
    class phone_btn implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+82"+signup_phone_social.getText().toString(),
                    60,
                    TimeUnit.SECONDS,
                    Activity_sociallogin_detail_information.this,
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            Log.d(TAG, "onVerificationCompleted: ");
                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
//                            Toast.makeText(Activity_sociallogin_detail_information.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onVerificationFailed: e : "+e.getMessage());
                        }

                        @Override
                        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            Log.d(TAG, "onCodeSent: s : "+s);
                            verificationId=s;
                            signup_authnum_social.setVisibility(View.VISIBLE);
                            authnum_btn_social.setVisibility(View.VISIBLE);
                        }
                    }
            );
        }
    }
    /*인증 확인*/
    class authnum_btn implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                    verificationId,
                    signup_authnum_social.getText().toString()
            );
            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        authnumck =true;
                        Log.d(TAG, "onComplete: ");
                        signup_authnum_social.setVisibility(View.GONE);
                        authnum_btn_social.setVisibility(View.GONE);
                        phone_btn_social.setVisibility(View.GONE);
                        phoneck_img_social.setVisibility(View.VISIBLE);
                        signup_phone_social.setEnabled(false);
                    }
                    else{
                        Toast.makeText(Activity_sociallogin_detail_information.this,"인증번호를 확인해주세요.",Toast.LENGTH_SHORT).show();
                    }
                }
            });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
