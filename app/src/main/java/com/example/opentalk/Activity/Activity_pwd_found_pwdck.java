package com.example.opentalk.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.opentalk.R;
import com.example.opentalk.Retrofit.ApiClient;
import com.example.opentalk.Retrofit.Pwdfound.Pwdfoundck;
import com.example.opentalk.Retrofit.Pwdfound.PwdfoundckData;
import com.example.opentalk.Socket_my.Socket_Other_LoginCk_thread;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONObject;

import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_pwd_found_pwdck extends AppCompatActivity {
    String TAG = "Activity_pwd_found_pwdck";

    EditText pwdck_id_input;
    EditText pwdck_phone_input;
    EditText pwdck_authnum;

    Button phone_btn;
    Button pwdck_btn;
    
    /*인증 관련*/
    String verificationId;
    boolean verifiCk = false;
    String userid;
    String phonenum;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd_found_pwdck);
        init();
        phone_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userid = pwdck_id_input.getText().toString();
                phonenum = pwdck_phone_input.getText().toString();
                pwdfoundck(userid,phonenum);
            }
        });
        pwdck_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verifiCk==true) {
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                            verificationId,
                            pwdck_authnum.getText().toString()
                    );
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //다음 페이지로 이동
                                Log.d(TAG, "onComplete: 확인완료");
                                Intent intent = new Intent(Activity_pwd_found_pwdck.this, Activity_profile_change_pwdchange.class);
                                intent.putExtra("userid",userid);
                                intent.putExtra("logining","dont");
                                finish();
                                startActivity(intent);
                            } else {
                                Toast.makeText(Activity_pwd_found_pwdck.this, "인증번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    //인증번호 없이 테스트 할려면 밑에 주석을 없애주세요.
//                    userid = pwdck_id_input.getText().toString();
//                    Log.d(TAG, "onComplete: 확인완료");
//                    Intent intent = new Intent(Activity_pwd_found_pwdck.this, Activity_profile_change_pwdchange.class);
//                    intent.putExtra("userid",userid);
//                    intent.putExtra("logining","dont");
//                    finish();
//                    startActivity(intent);
                    informDialog("인증번호를 확인해주세요.");
                }

            }
        });
    }

    private void init(){
        pwdck_id_input = (EditText)findViewById(R.id.pwdck_id_input);
        pwdck_phone_input = (EditText)findViewById(R.id.pwdck_phone_input);
        pwdck_authnum = (EditText)findViewById(R.id.pwdck_authnum);

        phone_btn = (Button)findViewById(R.id.phone_btn);
        pwdck_btn = (Button)findViewById(R.id.pwdck_btn);
    }

    private void pwdfoundck(String userid,String phonenum){
        Pwdfoundck pwdfoundck = ApiClient.getApiClient().create(Pwdfoundck.class);
        HashMap<String,Object> param = new HashMap<>();
        param.put("userid",userid);
        param.put("phonenum",phonenum);
        Call<PwdfoundckData> call = pwdfoundck.postData(param);
        call.enqueue(new Callback<PwdfoundckData>() {
            @Override
            public void onResponse(Call<PwdfoundckData> call, Response<PwdfoundckData> response) {
                if(response.isSuccessful()){
                    boolean result = response.body().isResult();
                    if(result==true){
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                "+82"+phonenum,
                                60,
                                TimeUnit.SECONDS,
                                Activity_pwd_found_pwdck.this,
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
                                        pwdck_authnum.setVisibility(View.VISIBLE);
                                        phone_btn.setVisibility(View.GONE);

                                        pwdck_id_input.setEnabled(false);
                                        pwdck_phone_input.setEnabled(false);
                                        verifiCk=true;
                                    }
                                }
                        );
                    }
                    else{
                        informDialog("입력하신 정보와 일치하는 정보가 없습니다.");
                    }
                }
                else{
                    Log.d(TAG, "pwdfoundck onResponse: "+response.toString());
                }
            }

            @Override
            public void onFailure(Call<PwdfoundckData> call, Throwable t) {
                Log.d(TAG, "pwdfoundck onFailure: ERROR MESSAGE t : "+t.toString());
            }
        });
    }

    //프로필사진 확대 화면
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
}
