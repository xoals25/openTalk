package com.example.opentalk.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.opentalk.BitmapConverter;
import com.example.opentalk.Data.Logindata;
import com.example.opentalk.R;
import com.example.opentalk.Retrofit.ApiClient;
import com.example.opentalk.Retrofit.NicknameChange.NicknameChange;
import com.example.opentalk.Retrofit.NicknameChange.NicknameChangeData;
import com.example.opentalk.Retrofit.SignupCk;
import com.example.opentalk.Retrofit.SignupCkData;
import com.example.opentalk.SecurityUtil.SecurityUtilSHA;
import com.example.opentalk.ServerIp;
import com.example.opentalk.VolleyRequestQhelper;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

import static com.example.opentalk.SharedPreference.PreferenceManager_member.getLoignDataPref;
import static com.example.opentalk.SharedPreference.PreferenceManager_member.setLoignDataPref;

public class Activity_Profile_Change extends AppCompatActivity {
    Toolbar myToolbar;
    Button finishbtn;
    /*내정보 프로필 이미지,아이디,닉네임*/
    ImageView profile_change_img;
    TextView profile_change_id;
    TextView profile_change_nickname;
    ConstraintLayout nickname_layout;
    ConstraintLayout pwd_layout;
    /*로그인 데이터 정보 가져오기, 담아줄 변수*/
    Logindata logindata;
//    public String userid; //static 해준이유 어디서나 로그인한 아이디는 같아야하니까.
//    public String usernickname; //static 해준이유 어디서나 로그인한 아이디는 같아야하니까.
    public String profileImgToString;

    /*사진 찍는거 관련*/
    int PICK_IMAGE = 0;
    int CAPTURE_IMAGE = 1;
    String img_bitmap_tostring=null;
    byte[] profile_img_bitmap_tobyte;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_change);
        init();

        profile_change_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog();
            }
        });


        nickname_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputdialog();
            }
        });

        pwd_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Profile_Change.this,Activity_profile_change_pwdck.class);
                startActivity(intent);
            }
        });

        finishbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "onClick: 확인 확인 확인");

                finish();
            }
        });
    }

    private void init(){
        // 추가된 소스, Toolbar를 생성한다.
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기(툴바 맨왼쪽에 생김)
        logindata = getLoignDataPref(this,"login_inform");
//        userid = logindata.getUserid();
//        usernickname = logindata.getUsername();
        profileImgToString = logindata.getImgString_tobitmap();
        if(VolleyRequestQhelper.requestQueue==null){
            VolleyRequestQhelper.requestQueue = Volley.newRequestQueue(this);
        }
        profile_change_img = (ImageView)findViewById(R.id.profile_change_img);
        profile_change_id = (TextView)findViewById(R.id.profile_change_id);
        profile_change_nickname = (TextView)findViewById(R.id.profile_change_nickname);
        nickname_layout = (ConstraintLayout)findViewById(R.id.nickname_layout);
        pwd_layout = (ConstraintLayout)findViewById(R.id.pwd_layout);
        if(logindata.getUserpwd()==null){
            pwd_layout.setVisibility(View.GONE);
        }
        profile_change_id.setText(Activity_Lobby.userid);
        finishbtn =(Button)findViewById(R.id.finishbtn);


        if(!profileImgToString.equals("false")) {
            profile_change_img.setImageBitmap(BitmapConverter.StringToBitmap(profileImgToString));
        }
        profile_change_nickname.setText(Activity_Lobby.usernickname);
    }

    /*사진 가져오는거 선택*/
    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("사진업로드");
        myAlertDialog.setMessage("카메라 또는 갤러리를 선택해주세요.");

        myAlertDialog.setPositiveButton("갤러리", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Intent pictureActionIntent = null;
                pictureActionIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                pictureActionIntent.setType("image/*");
                startActivityForResult(pictureActionIntent, PICK_IMAGE);
            }
        });

        myAlertDialog.setNegativeButton("카메라", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                cameraIntent.setType("image/*");
                startActivityForResult(cameraIntent,CAPTURE_IMAGE);
            }
        });
        myAlertDialog.show();
    }

    private void inputdialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_dialog_profilenickname_change, null);
        builder.setView(view);
        EditText dialog_nickname_input = (EditText) view.findViewById(R.id.dialog_nickname_input);
        ConstraintLayout dialog_ok_layout = (ConstraintLayout)view.findViewById(R.id.dialog_ok_layout);
//        dialog_ok_layout.setVisibility(View.GONE);
        Button dialog_ok_btn = (Button)view.findViewById(R.id.dialog_ok_btn);
        Button dialog_cancel_btn = (Button)view.findViewById(R.id.dialog_cancel_btn);
        final AlertDialog dialog = builder.create();

        dialog_ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog_nickname_input.getText().toString().length()>=1){
                    nicknameChange(Activity_Lobby.usernickname,dialog_nickname_input.getText().toString(),dialog);
                }
                else{
                    Toast.makeText(Activity_Profile_Change.this,"변경하실 닉네임을 입력해주세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    /*닉네임 변경 서버 통신 (레트로핏)*/
    public void nicknameChange(String usernickname,String changenickname,AlertDialog dialog){
        NicknameChange nicknameChange = ApiClient.getApiClient().create(NicknameChange.class);
        HashMap<String,Object> param = new HashMap<>();
        param.put("usernickname",usernickname);
        param.put("changenickname",changenickname);
        Call<NicknameChangeData> call = nicknameChange.postData(param);
        call.enqueue(new Callback<NicknameChangeData>() {
            @Override
            public void onResponse(Call<NicknameChangeData> call, retrofit2.Response<NicknameChangeData> response) {
                boolean resultck = response.body().isResult();
                if(resultck==true){
                    Toast.makeText(Activity_Profile_Change.this,"닉네임 변경 완료",Toast.LENGTH_SHORT).show();
                    profile_change_nickname.setText(changenickname);
                    Activity_Lobby.usernickname = changenickname;
                    Logindata logindata_after = new Logindata(logindata.getUserid(),logindata.getUserpwd(),changenickname,logindata.getImgString_tobitmap());
                    setLoignDataPref(Activity_Profile_Change.this,"login_inform",logindata_after);
                    Log.d("TAG", "onResponse: 확인작업입니다."+changenickname);
                    dialog.dismiss();
                }
                else{
                    Toast.makeText(Activity_Profile_Change.this,"닉네임 변경 실패",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<NicknameChangeData> call, Throwable t) {
                Log.d("TAG", "onResponse: 확인작업입니다. : t : "+t.toString());
            }
        });
    }

    /*프로필 변경 서버통신(볼리)*/
    public void signupUpload(String img_String) {
        String serverUrl="http://"+ ServerIp.IP_ADDRESS_ADD_FOLDER_NAME+"/imgchange.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("성공")){
                    //로그인 데이터에 알맞게 아이템 장착해주기
                    Logindata logindata_after = new Logindata(logindata.getUserid(),logindata.getUserpwd(),logindata.getUsername(),img_String);
                    setLoignDataPref(Activity_Profile_Change.this,"login_inform",logindata_after);
                    profileImgToString= img_String;
                    Activity_Lobby.profileImgToString = img_String;

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }
        ){
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("useid", Activity_Lobby.userid);
                if(img_bitmap_tostring!=null) {
                    params.put("changeimg", img_bitmap_tostring);
                }
                return params;
            }
        };
        VolleyRequestQhelper.requestQueue.add(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //갤러리
        if(requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK){
            try {
                InputStream in = getContentResolver().openInputStream(data.getData());
                Bitmap bit_img = BitmapFactory.decodeStream(in);
                in.close();
                profile_change_img.setImageBitmap(bit_img);
                img_bitmap_tostring = BitmapConverter.BitmapToString(bit_img);
                signupUpload(img_bitmap_tostring);
            }catch (Exception e){
            }
        }
        //사진 찍기
        else if(requestCode == CAPTURE_IMAGE && resultCode == Activity.RESULT_OK &&data.hasExtra("data")){
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            if(bitmap!=null){
                profile_change_img.setImageBitmap(bitmap);
                img_bitmap_tostring = BitmapConverter.BitmapToString(bitmap);
                signupUpload(img_bitmap_tostring);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

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
