package com.example.opentalk.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;

import com.example.opentalk.CustomPwdMethod;
import com.example.opentalk.Data.Logindata;
import com.example.opentalk.R;
import com.google.android.material.button.MaterialButtonToggleGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.opentalk.SharedPreference.PreferenceManager_member.getLoignDataPref;

public class Activity_Room_Create extends AppCompatActivity {
    String TAG ="Activity_Room_Create";
    Toolbar myToolbar;
    /*방제목*/
    EditText room_create_title_input;
    /*채팅방 종류선택*/
    MaterialButtonToggleGroup room_create_type_btnGroup;
    Button room_create_type_voicebtn;
    Button room_create_type_facebtn;
    String room_type="voice"; //초기에는 voice로 선택이 되어있어서 voice로 작성
    int puple ;
    int gray ;
    /*공개방 여부 선택*/
    MaterialButtonToggleGroup room_create_pwdroom_btnGroup;
    Button room_create_pwdroom_nobtn;
    Button room_create_pwdroom_yesbtn;
    String pwdroom="공개방"; //초기에는 공개방으로 선택이 되어있어서 공개방으로 작성
    /*비공개방 비밀번호 입력관련*/
    LinearLayout room_create_pwd_Linear; //비공개 선택시 visivility=visible 공개 선택시 visivility=gone으로 해주기
    EditText room_create_pwd_input;
    /*인원수 관련*/
    ImageButton numlimit_prev_btn;
    ImageButton numlimit_next_btn;
    TextView room_create_numlimit_value;
    int person_numlimit=2;
    /*방만들기 버튼*/
    Button room_create_finish;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_create);
        initFindViewById();

        /*보이스방 선택*/
        room_create_type_voicebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                room_type="voice";
                //계속 선택된 버튼을 한번더 선택하면 해제가 되어서 다시 체크하도록 해주기
                room_create_type_btnGroup.check(v.getId());

                room_create_type_voicebtn.setTextColor(puple);
                room_create_type_facebtn.setTextColor(gray);
            }
        });
        /*화상채팅방 선택*/
        room_create_type_facebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                room_type="face";
                //계속 선택된 버튼을 한번더 선택하면 해제가 되어서 다시 체크하도록 해주기
                room_create_type_btnGroup.check(v.getId());
                room_create_type_voicebtn.setTextColor(gray);
                room_create_type_facebtn.setTextColor(puple);
            }
        });

        /*공개방 선택*/
        room_create_pwdroom_nobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                //계속 선택된 버튼을 한번더 선택하면 해제가 되어서 다시 체크하도록 해주기
                room_create_pwdroom_btnGroup.check(v.getId());
                //공개방인지 비공개방인지 mysql에 넘길값
                pwdroom="공개방";
                //비밀번호 입력란 없애기
                room_create_pwd_Linear.setVisibility(View.GONE);
                //비번작성했다가 공개방 변경했을때 혹시나 mysql에 값넘어가는것 방지
                room_create_pwd_input.setText("");
                room_create_pwdroom_nobtn.setTextColor(puple);
                room_create_pwdroom_yesbtn.setTextColor(gray);
            }
        });
        /*비공개방 선택*/
        room_create_pwdroom_yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                room_create_pwdroom_btnGroup.check(v.getId());
                //공개방인지 비공개방인지 mysql에 넘길값
                pwdroom="비공개방";
                //비밀번호 입력란 보이게하기
                room_create_pwd_Linear.setVisibility(View.VISIBLE);
                room_create_pwdroom_nobtn.setTextColor(gray);
                room_create_pwdroom_yesbtn.setTextColor(puple);
            }
        });
        /*인원수 -1 버튼*/
        numlimit_prev_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //인원수 숫자 감소시키기
                person_numlimit-=1;
                //감소된 인원수 textView에 셋팅
                room_create_numlimit_value.setText(String.valueOf(person_numlimit));
                //인원수가 16->15로 되면 인원수 +1 버튼 보이게하기
                if(person_numlimit==15){
                    numlimit_next_btn.setVisibility(View.VISIBLE);
                }
                //인원수가 2가되면 인원수 -1 버튼 숨기기
                else if(person_numlimit==2){
                    numlimit_prev_btn.setVisibility(View.INVISIBLE);
                }
            }
        });
        /*인원수 +1 버튼*/
        numlimit_next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //인원수 숫자 증가시키기
                person_numlimit+=1;
                //감소된 인원수 textView에 셋팅
                room_create_numlimit_value.setText(String.valueOf(person_numlimit));
                //인원수가 16이되면 인원수 +1 버튼 숨기기
                if(person_numlimit==16){
                    numlimit_next_btn.setVisibility(View.INVISIBLE);
                }
                //인원수가 2->3로 되면 인원수 -1 버튼 보이게하기
                else if(person_numlimit==3){
                    numlimit_prev_btn.setVisibility(View.VISIBLE);
                }
            }
        });

        /*방만들기 버튼*/
        room_create_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String room_title = room_create_title_input.getText().toString();
                String room_pwd = room_create_pwd_input.getText().toString();
                Logindata logindata = getLoignDataPref(Activity_Room_Create.this,"login_inform");
                String user_email = logindata.getUserid();
                String IP_ADDRESS = "3.36.188.116/opentalk";
                if(pwdroom.equals("공개방")){
                    if(!room_title.equals("")){
                        Room_Create room_create = new Room_Create();
                        room_create.execute("http://"+IP_ADDRESS+"/room_create.php",room_title,room_type,pwdroom,room_pwd,String.valueOf(person_numlimit),user_email);
                    }
                }
                else if(pwdroom.equals("비공개방")){
                    if(!room_title.equals("") && !room_pwd.equals("")){
                        Room_Create room_create = new Room_Create();
                        room_create.execute("http://"+IP_ADDRESS+"/room_create.php",room_title,room_type,pwdroom,room_pwd,String.valueOf(person_numlimit),user_email);
                    }
                    else if(room_title.equals("")){
                        Toast.makeText(Activity_Room_Create.this,"방 제목을 입력해주세요.",Toast.LENGTH_SHORT).show();
                    }
                    else if(room_pwd.equals("")){
                        Toast.makeText(Activity_Room_Create.this,"비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void initFindViewById(){
        /*툴바 관련*/
        // 추가된 소스, Toolbar를 생성한다.
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기(툴바 맨왼쪽에 생김)
        puple = ContextCompat.getColor(getApplicationContext(), R.color.colordarkPuple);
        gray = ContextCompat.getColor(getApplicationContext(), R.color.colorlittledarkgray);
        /*방제목*/
        room_create_title_input =(EditText)findViewById(R.id.room_create_title_input);
        /*채팅방 종류선택*/
        room_create_type_btnGroup =(MaterialButtonToggleGroup)findViewById(R.id.room_create_type_btnGroup);
        room_create_type_voicebtn =(Button)findViewById(R.id.room_create_type_voicebtn);
        room_create_type_facebtn =(Button)findViewById(R.id.room_create_type_facebtn);
        /*공개방 여부 선택*/
        room_create_pwdroom_btnGroup=(MaterialButtonToggleGroup)findViewById(R.id.room_create_pwdroom_btnGroup);
        room_create_pwdroom_nobtn =(Button)findViewById(R.id.room_create_pwdroom_nobtn);
        room_create_pwdroom_yesbtn =(Button)findViewById(R.id.room_create_pwdroom_yesbtn);
        /*비공개방 비밀번호 입력관련*/
        room_create_pwd_Linear =(LinearLayout)findViewById(R.id.room_create_pwd_Linear);
        room_create_pwd_input =(EditText)findViewById(R.id.room_create_pwd_input);
        /*비밀번호 메소드 재설정(마지막 비밀번호 입력값도 숨겨주는 것으로 변경)*/
        room_create_pwd_input.setTransformationMethod(new CustomPwdMethod.CustomPasswordTransformationMethod());
        /*인원수 관련*/
        numlimit_prev_btn =(ImageButton)findViewById(R.id.numlimit_prev_btn);
        numlimit_next_btn =(ImageButton)findViewById(R.id.numlimit_next_btn);
        room_create_numlimit_value =(TextView) findViewById(R.id.room_create_numlimit_value);
        numlimit_prev_btn.setVisibility(View.INVISIBLE);//최대인원수 2일땐 이전버튼 숨기기
        /*방만들기 버튼*/
        room_create_finish=(Button)findViewById(R.id.room_create_finish);
    }

    class Room_Create extends AsyncTask<String,Void,String> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(Activity_Room_Create.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            String do_room_title = params[1];
            String do_room_type = params[2];
            String do_room_public = params[3];
            String do_room_pwd = params[4];
            int do_room_person_numlimit = Integer.valueOf(params[5]);
            String do_room_captain = params[6];

            String postparameters = "room_title="+do_room_title
                    +"&room_type="+do_room_type
                    +"&room_public="+do_room_public
                    +"&room_pwd="+do_room_pwd
                    +"&room_person_numlimit="+do_room_person_numlimit
                    +"&room_captain="+do_room_captain;
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
            Log.d(TAG, "onPostExecute: "+result);


            if(result.equals("중복")){
                Toast.makeText(Activity_Room_Create.this,"중복된 방제목입니다.",Toast.LENGTH_SHORT).show();
            }
            else if(result.contains("실패")){
                Toast.makeText(Activity_Room_Create.this,"방만들기 실패~",Toast.LENGTH_SHORT).show();
            }
            //만약 중복,실패가아니라 숫자로 들어온다면(성공했다는 표시이고 방번호를 넘겨주는것이다.)
            else{
                Log.d(TAG, "onPostExecute: 성공");
                int room_num = Integer.valueOf(result);
                Log.d(TAG, "onPostExecute: room_num : "+room_num);
                if(room_type.equals("voice")){
                    Intent intent = new Intent(Activity_Room_Create.this,Activity_Room_Voice.class);
                    intent.putExtra("room_num",room_num);
                    startActivity(intent);
                    finish();
                }
                else if(room_type.equals("face")){
                    Intent intent = new Intent(Activity_Room_Create.this,Activity_Room_Face.class);
                    intent.putExtra("room_num",room_num);
                    startActivity(intent);
                    finish();
                }

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
