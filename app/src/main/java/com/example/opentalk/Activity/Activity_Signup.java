package com.example.opentalk.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.opentalk.BitmapConverter;
import com.example.opentalk.CustomPwdMethod;
import com.example.opentalk.R;
import com.example.opentalk.Retrofit.ApiClient;
import com.example.opentalk.Retrofit.SignupCk;
import com.example.opentalk.Retrofit.SignupCkData;
import com.example.opentalk.SecurityUtil.SecurityUtilSHA;
import com.example.opentalk.ServerIp;
import com.example.opentalk.VolleyRequestQhelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;

/*
*  수정 진행중 : 2020/12/08
*  수정하는 부분 : Activity_Signup_Profileimg에서 profile_imgpath를 가져와서 회원가입 php 통신할때 같이 추가해서 통신
*  ->작업완료 (서버에 전달 했습니다.)
*
*  수정 진행중 2021/01/17
*  아이디 이메일형식 제거
*  아이디,닉네임 중복 체크
*
* */


public class Activity_Signup extends AppCompatActivity {
    Toolbar myToolbar;
    String TAG = "Activity_Signup";

    /*아이디 관련*/
    EditText signup_Id_input; //아이디 입력창
    TextView signup_Idck_text; //입력 중복 됬는지 알려주는 글
    boolean Idck = false; //중복이면 false 아니면 true
    ImageView Idck_mark; //중복인지아닌지 체크 표시
    String signup_Id;

    /*닉네임 관련*/
    EditText signup_nickname_input; //닉네임 입력창
    TextView signup_nicknameck_text; //닉네임 중복 알림 글
    boolean nicknameck = false; //중복이면 false 아니면 true
    ImageView Nicknameck_mark; //중복인지아닌지 체크 표시
    String signup_nickname;

    /*비밀번호 관련*/
    EditText signup_pwd_input; //비밀번호 입력창
    TextView signup_pwd_text; //비밀번호 정규식 알림글
    ImageView pwd_mark;
    boolean pwd_ck = false; //중복이면 false 아니면 true
    int pwd_ck_Num=0; //비밀번호 입력할 때마다 값들을 셋팅해주면 비효율인거같아서 0일때만 셋팅해주기
    String pwd;

    /*비밀번호체크 관련*/
    EditText signup_pwdck_input; //비밀번호확인 입력창
    TextView signup_pwdck_text; //비밀번호를 위와 맞게 작성했는지 알림글
    ImageView pwdck_mark;
    boolean pwdck_ck =false; //잘못 입력했으면 false 아니면 true
    int pwdck_ck_Num=0; //비밀번호 입력할 때마다 값들을 셋팅해주면 비효율인거같아서 0일때만 셋팅해주기
    String signup_pwd;

    /*이미지 관련*/
    String img_bitmap_tostring;
    byte[] img_bitmap_tobytearray;


    /*회원가입 완료*/
    Button signup_signup_btn;

    /*회원가입 결과값*/
    private String mJsonString;
    
    /*인증*/
    EditText signup_phone;
    EditText signup_authnum;
    Button phone_btn;
    Button authnum_btn;
    String verificationId;
    boolean authnumck =false;
    ImageView phoneck_img;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initfindViewById();

        phone_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+82"+signup_phone.getText().toString(),
                        60,
                        TimeUnit.SECONDS,
                        Activity_Signup.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                Log.d(TAG, "onVerificationCompleted: ");
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Log.d(TAG, "onVerificationFailed: e : "+e.getMessage());
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                Log.d(TAG, "onCodeSent: s : "+s);
                                verificationId=s;
                                signup_authnum.setVisibility(View.VISIBLE);
                                authnum_btn.setVisibility(View.VISIBLE);
                            }
                        }
                );
            }
        });

        authnum_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                        verificationId,
                        signup_authnum.getText().toString()
                );
                FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            authnumck =true;
                            Log.d(TAG, "onComplete: ");
                            signup_authnum.setVisibility(View.GONE);
                            authnum_btn.setVisibility(View.GONE);
                            phone_btn.setVisibility(View.GONE);
                            phoneck_img.setVisibility(View.VISIBLE);
                            signup_phone.setEnabled(false);
                        }
                        else{
                            Toast.makeText(Activity_Signup.this,"인증번호를 확인해주세요.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        /*회원가입 완료 버튼*/
        signup_signup_btn.setOnClickListener(new Signup());

        /*아이디 중복검사 */
        signup_Id_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    Log.d(TAG, "onFocusChange: hasFocus");
                }
                else{
                    if(signup_Id_input.getText().toString().length()>=1) {
                        signupIdCk(signup_Id_input.getText().toString());
                    }
                }

            }
        });
        signup_Id_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Idck=false;
                Idck_mark.setVisibility(View.GONE);
                signup_Idck_text.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        /*닉네임 중복검사 */
        signup_nickname_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    Log.d(TAG, "onFocusChange: hasFocus");
                }
                else{
                    if(signup_nickname_input.getText().toString().length()>=1) {
                        signupNicknameCk(signup_nickname_input.getText().toString());
                    }
                }
            }
        });
        signup_nickname_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nicknameck=false;
                Nicknameck_mark.setVisibility(View.GONE);
                signup_nicknameck_text.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /*비밀번호 정규식 확인*/
        signup_pwd_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    Log.d(TAG, "onFocusChange: hasFocus");
                }
                else{
                    if(signup_pwd_input.getText().toString().length()==0) {
                        pwd_ck=false;
                        pwd_ck_Num=0;
                        pwd_mark.setVisibility(View.GONE);
                        signup_pwd_text.setVisibility(View.GONE);
                    }
                }
            }
        });
        signup_pwd_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                check_validation(signup_pwd_input.getText().toString());
                //비밀번호를 변경할때 비밀번호 확인란 다시 검사해주기 (비밀번호확인란이 입력이 되있다면

                if(signup_pwdck_input.getText().toString().length()!=0){
                    //만약 바꾼 비밀번호가 위아래 같다면
                    if(signup_pwdck_input.getText().toString().equals(signup_pwd_input.getText().toString())){
                        pwdck_ck=true;
                        pwdck_ck_Num=0;
                        pwdck_mark.setVisibility(View.VISIBLE);
                        pwdck_mark.setBackground(ContextCompat.getDrawable(Activity_Signup.this,R.drawable.check_purple));
                        signup_pwdck_text.setVisibility(View.GONE);
                    }
                    //만약 바꾼 비밀번호가 위아래 다르다면
                    else{
                        //단,이미 비밀번호가 일치하지 않는다고 입력이 되있지않다면.
                        if(pwdck_ck_Num==0) {
                            pwdck_ck = false;
                            pwdck_ck_Num += 1;
                            pwdck_mark.setVisibility(View.VISIBLE);
                            pwdck_mark.setBackground(ContextCompat.getDrawable(Activity_Signup.this, R.drawable.redxmark));
                            signup_pwdck_text.setVisibility(View.VISIBLE);
                            signup_pwdck_text.setText("비밀번호가 일치하지 않습니다.");
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /*비밀번호 확인란*/
        signup_pwdck_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(signup_pwdck_input.getText().toString().length()==0) {
                    pwdck_ck=false;
                    pwdck_ck_Num=0;
                    pwdck_mark.setVisibility(View.GONE);
                    signup_pwdck_text.setVisibility(View.GONE);
                }
            }
        });
        signup_pwdck_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(signup_pwdck_input.getText().toString().equals(signup_pwd_input.getText().toString())){
                    pwdck_ck=true;
                    pwdck_ck_Num=0;
                    pwdck_mark.setVisibility(View.VISIBLE);
                    pwdck_mark.setBackground(ContextCompat.getDrawable(Activity_Signup.this,R.drawable.check_purple));
                    signup_pwdck_text.setVisibility(View.GONE);
                }
                else{
                    if(pwdck_ck_Num==0) {
                        pwdck_ck=false;
                        pwdck_ck_Num+=1;
                        pwdck_mark.setVisibility(View.VISIBLE);
                        pwdck_mark.setBackground(ContextCompat.getDrawable(Activity_Signup.this, R.drawable.redxmark));
                        signup_pwdck_text.setVisibility(View.VISIBLE);
                        signup_pwdck_text.setText("비밀번호가 일치하지 않습니다.");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /*비밀번호 정규식 유효성 검사*/
    void check_validation(String password) {
        //숫자, 문자, 특수문자 모두 포함 (8~15자)
        String val_symbol = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&]).{8,32}.$";
        // 정규표현식 컴파일
        Pattern pattern_symbol = Pattern.compile(val_symbol);
        Matcher matcher_symbol = pattern_symbol.matcher(password);
        if (matcher_symbol.find()) {
            pwd_ck=true;
            pwd_ck_Num=0;
            pwd_mark.setVisibility(View.VISIBLE);
            pwd_mark.setBackground(ContextCompat.getDrawable(Activity_Signup.this,R.drawable.check_purple));
            signup_pwd_text.setVisibility(View.GONE);
        }else {
            if(pwd_ck_Num==0) {
                pwd_ck=false;
                pwd_ck_Num+=1;
                pwd_mark.setVisibility(View.VISIBLE);
                pwd_mark.setBackground(ContextCompat.getDrawable(Activity_Signup.this, R.drawable.redxmark));
                signup_pwd_text.setVisibility(View.VISIBLE);
                signup_pwd_text.setText("숫자,문자,특수문자 모두 포함 8~32자리 입력해주세요.");
            }
        }
    }

    /*findViewById 및 간단한 설정값*/
    private void initfindViewById(){
        // 추가된 소스, Toolbar를 생성한다.
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기(툴바 맨왼쪽에 생김)
        /*회원가입 입력값들*/
        signup_Id_input = (EditText)findViewById(R.id.signup_Id_input);
        signup_nickname_input = (EditText)findViewById(R.id.signup_nickname_input);
        signup_pwd_input = (EditText)findViewById(R.id.signup_pwd_input);
        signup_pwdck_input = (EditText)findViewById(R.id.signup_pwdck_input);
        /*비밀번호 메소드 재설정(마지막 비밀번호 입력값도 숨겨주는 것으로 변경)*/
        signup_pwd_input.setTransformationMethod(new CustomPwdMethod.CustomPasswordTransformationMethod());
        signup_pwdck_input.setTransformationMethod(new CustomPwdMethod.CustomPasswordTransformationMethod());
        /*회원가입 이메일 중복체크 text,비밀번호 일치하는지 text*/
        signup_Idck_text = (TextView)findViewById(R.id.signup_Idck_text);
        signup_pwd_text=(TextView)findViewById(R.id.signup_pwd_text);
        signup_pwdck_text = (TextView)findViewById(R.id.signup_pwdck_text);
        signup_nicknameck_text = (TextView)findViewById(R.id.signup_nicknameck_text);
        /*회원가입 완료 버튼*/
        signup_signup_btn = (Button)findViewById(R.id.signup_signup_btn);
        /*체크 마크*/
        Idck_mark = (ImageView)findViewById(R.id.Idck_mark);
        Nicknameck_mark = (ImageView)findViewById(R.id.Nicknameck_mark);
        pwdck_mark = (ImageView)findViewById(R.id.pwdck_mark);
        pwd_mark = (ImageView)findViewById(R.id.pwd_mark);
        if(VolleyRequestQhelper.requestQueue==null){
            VolleyRequestQhelper.requestQueue = Volley.newRequestQueue(this);
        }

        /*이미지 경로 가져오기*/
        Intent intent = getIntent();
        img_bitmap_tobytearray = intent.getExtras().getByteArray("profile_img_bitmap_tostring");
        if(img_bitmap_tobytearray==null){
            Log.d(TAG, "initfindViewById: null이다 null이야");
        }
        else if(img_bitmap_tobytearray!=null){
            img_bitmap_tostring = BitmapConverter.ByteArrayToString(img_bitmap_tobytearray);
        }

        /*인증*/
        signup_phone = (EditText)findViewById(R.id.signup_phone);
        phone_btn = (Button)findViewById(R.id.phone_btn);
        signup_authnum = (EditText)findViewById(R.id.signup_authnum);
        authnum_btn =(Button)findViewById(R.id.authnum_btn);
        phoneck_img =(ImageView)findViewById(R.id.phoneck_img);
    }

    /*회원가입 서버통신(볼리)*/
    public void signupUpload(String phonenum) {
        signup_Id = signup_Id_input.getText().toString();
        signup_nickname = signup_nickname_input.getText().toString();
        signup_pwd = signup_pwd_input.getText().toString();
        //SHA-256 암호화
        signup_pwd = SecurityUtilSHA.encryptSHA256(signup_pwd);
        String serverUrl="http://"+ ServerIp.IP_ADDRESS_ADD_FOLDER_NAME+"/signup.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG", "onResponse: "+response);
                Intent intent = new Intent(Activity_Signup.this,Activity_Login.class);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }
        ){
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("signup_Id", signup_Id);
                params.put("signup_nickname", signup_nickname);
                params.put("signup_pwd", signup_pwd);
                if(img_bitmap_tostring!=null) {
                    params.put("signup_img", img_bitmap_tostring);
                }
                params.put("phonenum",phonenum);
                return params;
            }
        };
        VolleyRequestQhelper.requestQueue.add(stringRequest);

    }

    /*아이디 중복 체크 서버 통신 (레트로핏)*/
    public void signupIdCk(String userid){
        SignupCk signupCk = ApiClient.getApiClient().create(SignupCk.class);
        Call<SignupCkData> call = signupCk.getUserId(userid);
        call.enqueue(new Callback<SignupCkData>() {
            @Override
            public void onResponse(Call<SignupCkData> call, retrofit2.Response<SignupCkData> response) {
                if(response.isSuccessful() && response.body() !=null){
                    boolean resultck = response.body().isResult();
                    Log.d(TAG, "onResponse: resultck : "+resultck);
                    if(resultck==true){
                        Idck=true;
                        Idck_mark.setBackground(ContextCompat.getDrawable(Activity_Signup.this,R.drawable.check_purple));
                        signup_Idck_text.setVisibility(View.GONE);
                        Idck_mark.setVisibility(View.VISIBLE);
                        signup_Idck_text.setText("사용가능한 아이디입니다.");
                    }
                    else {
                        Idck=false;
                        Idck_mark.setBackground(ContextCompat.getDrawable(Activity_Signup.this,R.drawable.redxmark));
                        Idck_mark.setVisibility(View.VISIBLE);
                        signup_Idck_text.setVisibility(View.VISIBLE);
                        signup_Idck_text.setText("중복된 아이디입니다.");
                    }
                }
            }
            @Override
            public void onFailure(Call<SignupCkData> call, Throwable t) {

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
                    Nicknameck_mark.setBackground(ContextCompat.getDrawable(Activity_Signup.this,R.drawable.check_purple));
                    Nicknameck_mark.setVisibility(View.VISIBLE);
                    signup_nicknameck_text.setVisibility(View.GONE);
                    signup_nicknameck_text.setText("사용가능한 닉네임입니다.");
                }
                else {
                    nicknameck=false;
                    Nicknameck_mark.setBackground(ContextCompat.getDrawable(Activity_Signup.this,R.drawable.redxmark));
                    Nicknameck_mark.setVisibility(View.VISIBLE);
                    signup_nicknameck_text.setVisibility(View.VISIBLE);
                    signup_nicknameck_text.setText("중복된 닉네임입니다.");
                }
            }

            @Override
            public void onFailure(Call<SignupCkData> call, Throwable t) {

            }
        });
    }

    /*회원가입 완료 이벤트*/
    class Signup implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            /*아이디 중복 확인,비밀번호 일치 확인,닉네임작성 확인*/
            if(Idck==true && pwd_ck==true &&pwdck_ck==true && nicknameck==true && authnumck==true){
                signupUpload(signup_phone.getText().toString());
            }
            else if(Idck==false){
                if(signup_Id_input.getText().toString().length()==0){
                    Toast.makeText(Activity_Signup.this,"아이디를 입력 해주세요.",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Activity_Signup.this,"아이디를 중복검사 해주세요.",Toast.LENGTH_SHORT).show();
                }

            }
            else if(nicknameck==false){
                if(signup_nickname_input.getText().toString().length()==0){
                    Toast.makeText(Activity_Signup.this,"닉네임을 입력 해주세요.",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Activity_Signup.this,"닉네임을 중복검사 해주세요.",Toast.LENGTH_SHORT).show();
                }
            }
            else if(pwd_ck==false){
                if(signup_pwd_input.getText().toString().length()==0){
                    Toast.makeText(Activity_Signup.this,"비밀번호를 입력 해주세요.",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Activity_Signup.this,"비밀번호를 형식에 맞게 입력해주세요.",Toast.LENGTH_SHORT).show();
                }
            }
            else if(pwdck_ck==false){
                if(signup_pwdck_input.getText().toString().length()==0){
                    Toast.makeText(Activity_Signup.this,"비밀번호 확인란을 입력해주세요.",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Activity_Signup.this,"비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show();
                }
            }
            else if(authnumck==false){
                Toast.makeText(Activity_Signup.this,"인증번호를 확인해주세요.",Toast.LENGTH_SHORT).show();
            }
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
