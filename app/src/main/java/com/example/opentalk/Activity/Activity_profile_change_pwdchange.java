package com.example.opentalk.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.opentalk.Data.Logindata;
import com.example.opentalk.R;
import com.example.opentalk.Retrofit.ApiClient;
import com.example.opentalk.Retrofit.Login.Delete.LoginPrimiaryDelete;
import com.example.opentalk.Retrofit.Login.Delete.LoginPrimiaryDeleteData;
import com.example.opentalk.Retrofit.pwdchange.PwdChange;
import com.example.opentalk.Retrofit.pwdchange.PwdChangeData;
import com.example.opentalk.SecurityUtil.SecurityUtilSHA;
import com.example.opentalk.Socket_my.Socket_Other_LoginCk_thread;

import org.json.JSONObject;

import java.net.Socket;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.opentalk.SharedPreference.PreferenceManager_member.getLoignDataPref;
import static com.example.opentalk.SharedPreference.PreferenceManager_member.setLoignDataPref;

public class Activity_profile_change_pwdchange extends AppCompatActivity {
    String TAG ="Activity_profile_change_pwdchange";


    /*비밀번호 입력란*/
    EditText pwd_input;
    EditText pwdck_input;
    Button finish_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_change_pwdchange);
        init();

        finish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = pwd_input.getText().toString();
                String pwdck = pwdck_input.getText().toString();

                if(pwd.length()>=8) {
                    if (pwd.equals(pwdck)) {
                        if (check_validation(pwd)) {
                            Intent intent = getIntent();
                            String userid = intent.getStringExtra("userid");
                            pwdchange(userid,SecurityUtilSHA.encryptSHA256(pwd));
                        } else {
                            informDialog("비밀번호를 8~32 자의 영문 대소문자, 숫자, 특수문자를 조합하여 입력해주세요.");
                        }
                    } else {
                        informDialog("비밀번호가 일치하지 않습니다.");
                    }
                }
                else{
                    informDialog("비밀번호를 8자리 이상 입력해주세요.");
                }
            }
        });
    }

    private void init(){
        pwd_input = (EditText)findViewById(R.id.pwd_input);
        pwdck_input = (EditText)findViewById(R.id.pwdck_input);
        finish_btn =(Button)findViewById(R.id.pwdchange_pwd_finish_btn);

    }

    /*비밀번호 정규식 유효성 검사*/
    private boolean check_validation(String password) {
        //숫자, 문자, 특수문자 모두 포함 (8~15자)
        String val_symbol = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&]).{8,32}.$";
        // 정규표현식 컴파일
        Pattern pattern_symbol = Pattern.compile(val_symbol);
        Matcher matcher_symbol = pattern_symbol.matcher(password);
        if (matcher_symbol.find()) {

            return true;
        }else {

            return false;
        }
    }


    private void pwdchange(String userid,String pwdSHA){
        PwdChange pwdChange = ApiClient.getApiClient().create(PwdChange.class);
        HashMap<String,Object> param = new HashMap<>();
        param.put("userid",userid);
        param.put("changepwd", pwdSHA);
        Call<PwdChangeData> call = pwdChange.postData(param);
        call.enqueue(new Callback<PwdChangeData>() {
            @Override
            public void onResponse(Call<PwdChangeData> call, Response<PwdChangeData> response) {
                if(response.isSuccessful() && response.body() !=null) {
                    boolean resultck = response.body().isResult();
                    if (resultck == true) {
                        //로그인 한 상태에서 profile변경에서 들어왔을 경우
                        if (getIntent().getExtras().getString("logining", "").equals("ing")) {
                            Logindata logindata = getLoignDataPref(Activity_profile_change_pwdchange.this, "login_inform");
                            Logindata logindata_after = new Logindata(logindata.getUserid(), pwdSHA, logindata.getUsername(), logindata.getImgString_tobitmap());
                            setLoignDataPref(Activity_profile_change_pwdchange.this, "login_inform", logindata_after);
                        }
                        //Activity_pwd_found_pwdck에서 즉 로그인 화면에서 비밀번호 찾기로 들어왔을 경우
                        else if (getIntent().getExtras().getString("logining", "").equals("dont")){
                            try {
                                //여기서만 해주는 이유는 위에껄로 하면 자신도 로그아웃 되기때문이다.
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("type", "pwdchange");
                                jsonObject.put("userid", userid);
                                Socket socket_other_loginCk = null;
                                Socket_Other_LoginCk_thread socket_other_loginCk_thread = new Socket_Other_LoginCk_thread(socket_other_loginCk,userid,Activity_profile_change_pwdchange.this,false,jsonObject);
                                socket_other_loginCk_thread.start();
                                //여기에 http통신으로 상대 유저아이디에 등록된 기기 삭제
                            }
                            catch (Exception e){
                                Log.d(TAG, "Activity_pwd_found_pwdck Socket_Other_LoginCk_thread:  error message : "+e.getMessage());
                            }
                        }
                        String androidid = Settings.Secure.getString(Activity_profile_change_pwdchange.this.getContentResolver(),Settings.Secure.ANDROID_ID);
                        loginPrimiaryDelete(getIntent().getExtras().getString("userid",""),androidid);

                    } else {
                        informDialog("기존의 비밀번호로 변경하실 수 없습니다.");
                    }
                }
            }
            @Override
            public void onFailure(Call<PwdChangeData> call, Throwable t) {

            }
        });
    }

    private void loginPrimiaryDelete(String userid,String deviceid){
        LoginPrimiaryDelete loginPrimiaryDelete = ApiClient.getApiClient().create(LoginPrimiaryDelete.class);
        HashMap<String,Object> param = new HashMap<>();
        param.put("userid",userid);
        param.put("deviceid",deviceid);
        Call<LoginPrimiaryDeleteData> call = loginPrimiaryDelete.postData(param);
        call.enqueue(new Callback<LoginPrimiaryDeleteData>() {
            @Override
            public void onResponse(Call<LoginPrimiaryDeleteData> call, Response<LoginPrimiaryDeleteData> response) {
                if(response.isSuccessful()){
                    boolean result = response.body().isResult();
                    if(result==true){
                        informDialog("성공");
                    }
                    else{

                    }
                }
                else{

                }
            }

            @Override
            public void onFailure(Call<LoginPrimiaryDeleteData> call, Throwable t) {

            }
        });
    }


    private void informDialog(String informtext){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_dialog_inform, null);
        builder.setView(view);
        TextView alert_text = (TextView) view.findViewById(R.id.alert_text);
        final AlertDialog dialog = builder.create();
        if(!informtext.equals("성공")) {
            alert_text.setText(informtext);
            Button alert_btn = (Button) view.findViewById(R.id.alert_btn);
            alert_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        else{
            alert_text.setText("비밀번호 변경 완료");
            Button alert_btn = (Button) view.findViewById(R.id.alert_btn);
            alert_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    dialog.dismiss();
                }
            });
        }
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


}