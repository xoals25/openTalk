package com.example.opentalk.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.opentalk.CustomPwdMethod;
import com.example.opentalk.Data.Logindata;
import com.example.opentalk.R;
import com.example.opentalk.Retrofit.ApiClient;
import com.example.opentalk.Retrofit.Login.LoginPrimiary;
import com.example.opentalk.Retrofit.Login.LoginPrimiaryData;
import com.example.opentalk.SecurityUtil.SecurityUtilSHA;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.opentalk.SharedPreference.PreferenceManager_member.setLoignDataPref;

/*
*
* 2021 - 01 -17 수정함
* 수정 내용 : 구글 로그인인*
* */

public class Activity_Login extends AppCompatActivity {
    Toolbar myToolbar;
    String TAG ="Activity_Login";
    /*로그인 이메일,패스워드 입력란*/
    LinearLayout id_linear;
    LinearLayout pwd_linear;
    EditText login_email_input;
    EditText login_pwd_input;
    /*로그인,회원가입,비번찾기 버튼*/
    Button login_login_btn;
    Button login_signup_btn;
    Button pwd_forgot_btn;
    /*로그인 이메일 형식에 맞게 입력했는지 확인*/
    boolean emailck = false;
    /*로그인한 정보 쉐어드에 저장하기 위한 어레이리스트(추후에 로그인정보 http통신 하지 않고 바로 가져와 사용할 수 있게)*/
//    ArrayList<Logindata> logindataArrayList = new ArrayList<>();
    //json 결과값 담아주는 곳
    String mJsonString;
    //쉐어드 클래스파일

    /*안드로이드 고유번호,기기 고유번호*/
    String androidid;
    String deviceid;
    private JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        initfindViewById();

        /*추후에 아이디중복,비밀번호확인 일치체크 기능 넣으면 없애줄 것들*/
        emailck=true;
        login_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login_email = login_email_input.getText().toString();
                String login_pwd = login_pwd_input.getText().toString();
                login_pwd = SecurityUtilSHA.encryptSHA256(login_pwd);

                if(emailck==true && !login_pwd.equals("")){
                    loginPrimiary(login_email,login_pwd,androidid);
                }
                else if(emailck==false){
                    Toast.makeText(Activity_Login.this,"이메일을 확인해주세요.",Toast.LENGTH_SHORT).show();
                }
                else if(login_pwd.equals("")){
                    Toast.makeText(Activity_Login.this,"비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*회원가입 버튼*/
        login_signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Login.this, Activity_Signup_Profileimg.class);
                startActivity(intent);
            }
        });

        /*비밀번호 찾기 버튼*/
        pwd_forgot_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Login.this, Activity_pwd_found_pwdck.class);
                startActivity(intent);
            }
        });

        /*아이디,비밀번호 포커스 색상변경*/
        login_email_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    id_linear.setBackground(ContextCompat.getDrawable(Activity_Login.this,R.drawable.shadow_login_linear_purple));
                }
                else{
                    id_linear.setBackground(ContextCompat.getDrawable(Activity_Login.this,R.drawable.shadow_room_create));
                }
            }
        });
        login_pwd_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    pwd_linear.setBackground(ContextCompat.getDrawable(Activity_Login.this,R.drawable.shadow_login_linear_purple));
                }
                else{
                    pwd_linear.setBackground(ContextCompat.getDrawable(Activity_Login.this,R.drawable.shadow_room_create));
                }
            }
        });


    }

    /*findViewById 및 간단한 설정값*/
    private void initfindViewById(){
        // 추가된 소스, Toolbar를 생성한다.
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기(툴바 맨왼쪽에 생김)
        /*로그인 이메일,패스워드 입력란*/
        id_linear = (LinearLayout)findViewById(R.id.id_linear);
        pwd_linear = (LinearLayout)findViewById(R.id.pwd_linear);
        login_pwd_input = (EditText)findViewById(R.id.login_pwd_input);
        login_email_input = (EditText)findViewById(R.id.login_email_input);
        /*비밀번호 메소드 재설정(마지막 비밀번호 입력값도 숨겨주는 것으로 변경)*/
        login_pwd_input.setTransformationMethod(new CustomPwdMethod.CustomPasswordTransformationMethod());
        /*로그인,회원가입,비번찾기 버튼*/
        login_signup_btn = (Button)findViewById(R.id.login_signup_btn);
        pwd_forgot_btn = (Button)findViewById(R.id.pwd_forgot_btn);
        login_login_btn = (Button)findViewById(R.id.login_login_btn);
        /*기기 고유번호*/
        androidid = Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);

    }

    /*해당 아이디를 로그인한 기기 정보 저장 통신*/
    private void loginPrimiary(String userid,String login_pwd,String deviceid){
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
                        String IP_ADDRESS = "3.36.188.116/opentalk";
                        LoginSelect loginSelect = new LoginSelect();
                        loginSelect.execute("http://"+IP_ADDRESS+"/login.php",userid,login_pwd);
                        Log.d(TAG, "onResponse: userid : "+userid);
                        Log.d(TAG, "onResponse: login_pwd : "+login_pwd);
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

    class LoginSelect extends AsyncTask<String,Void,String> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(Activity_Login.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            String login_email = params[1];
            String login_pwd = params[2];

            String postparameters = "login_email="+login_email+"&login_pwd="+login_pwd;
            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                //입력을 실시할 수 있게 해주는 메소드
                //setDoInput(boolean) : Server 통신에서 입력 가능한 상태로 만듬
                //어플리케이션으로 URL 접속에 write를 실시하는 경우는 doOutput플래그를 true로 설정한다.
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                //출력해주기
                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postparameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                //출력을 해줬으니 서버에서 데이터를 전송받기
                int responseStatusCode = httpURLConnection.getResponseCode();

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK){
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;
                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();
                return sb.toString();

            }catch (Exception e){
                Log.d(TAG, "Exception: "+e.getMessage());
                return new String("Error: " + e.getMessage());
            }

        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d(TAG, "httpresult1: "+result);
            if(!result.equals("실패")){
                Log.d(TAG, "httpresult2: ");
                mJsonString = result;
                showResult();
            }
            else {
                Toast.makeText(Activity_Login.this,"아이디나 비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showResult(){
        String TAG_JSON ="login_inform";
        String TAG_EMAIL = "login_email";
        String TAG_PWD = "login_pwd";
        String TAG_NICKNAME = "login_nickname";
        String TAG_IMG = "login_img";
        try {
            Log.d(TAG, "httpresult3: ");
            JSONObject jsonObject =  new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            Log.d(TAG, "httpresult4: ");

            for (int i=0; i<jsonArray.length(); i++){
                JSONObject item = jsonArray.getJSONObject(i);
                String login_email = item.getString(TAG_EMAIL);
                String login_pwd = item.getString(TAG_PWD);
                String login_nickname = item.getString(TAG_NICKNAME);
                String login_img = item.getString(TAG_IMG);
                Log.d(TAG, "showResult: login_img:"+login_img);
                Log.d(TAG, "showResult:");
                //로그인 데이터에 알맞게 아이템 장착해주기
                    Logindata logindata = new Logindata(login_email,login_pwd,login_nickname,login_img);
//                logindataArrayList.add(logindata);
                //로그인정보 쉐어드에 저장(어레이버전)
//                shared.setLoignArrayPref(Activity_Login.this,"login_inform",logindataArrayList);
                setLoignDataPref(Activity_Login.this,"login_inform",logindata);
            }
            Intent intent = new Intent(Activity_Login.this, Activity_Lobby.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }catch (Exception e){
            Log.d(TAG, "showResult : ", e);
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
        Log.d(TAG, "onDestroy: 파괴");
    }
}