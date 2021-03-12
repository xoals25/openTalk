package com.example.opentalk.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.example.opentalk.BitmapConverter;
import com.example.opentalk.R;

import java.io.InputStream;

/*
*  클래스 생성 날짜 : 2020/12/08
*  클래스 설명 : 회원가입을 할 때, 프로필 이미지를 받아오는 클래스
*  클래스 상세 설명 : 프로필 이미지를 받을 때, bitmap으로 받고 String으로 변경해서 Activity_Signup에 intent로 넘겨줄 것이다.
*                   넘겨주고 Activity_Signup에서 서버로 넘겨줌.
*
* */
public class Activity_Signup_Profileimg extends AppCompatActivity {
    Toolbar myToolbar;
    String TAG = "Activity_Signup_Profileimg";

    ConstraintLayout signup_profileimg_linearlayout; //레이아웃 클릭시 사진 찍기 or 갤러리에서 불러오기
    ImageView signup_profileimg_input; //사진이 담기는 곳
    String profile_img_bitmap_tostring; //프로필사진 bitmap을 string으로 변경해서 Activity_Signup으로 넘길것이다.
    byte[] profile_img_bitmap_tobyte=null;

    int PICK_IMAGE = 0;
    int CAPTURE_IMAGE = 1;

    Button signup_profileimg_next_btn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_profileimg);
        permission_check(); //권한
        init();

        signup_profileimg_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog();
            }
        });
        signup_profileimg_next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Signup_Profileimg.this,Activity_Signup.class);
                //넘겨주는 값이 스트링이 아니라 byte array인데 이유는 string으로 넘기면 오류가 발생한다...
                //그래서 Activity_Signup에서 byte로 받아String으로 변경해줘서 서버로 전송해준다.
                intent.putExtra("profile_img_bitmap_tostring",profile_img_bitmap_tobyte);
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
        signup_profileimg_linearlayout = (ConstraintLayout)findViewById(R.id.signup_profileimg_linearlayout);
        signup_profileimg_input =(ImageView)findViewById(R.id.signup_profileimg_input);
        signup_profileimg_next_btn =(Button)findViewById(R.id.signup_profileimg_next_btn);
    }
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //갤러리
        if(requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK){
            try {
                InputStream in = getContentResolver().openInputStream(data.getData());
                Bitmap bit_img = BitmapFactory.decodeStream(in);
                in.close();
                signup_profileimg_input.setImageBitmap(bit_img);
                profile_img_bitmap_tobyte = BitmapConverter.BitmapToByteArray(bit_img);

            }catch (Exception e){

            }
        }
        //사진 찍기
        else if(requestCode == CAPTURE_IMAGE && resultCode == Activity.RESULT_OK &&data.hasExtra("data")){
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            if(bitmap!=null){
                signup_profileimg_input.setImageBitmap(bitmap);
                profile_img_bitmap_tobyte = BitmapConverter.BitmapToByteArray(bitmap);
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
    /***권한 받는 메소드***/
    private void permission_check(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "권한 설정 완료");
            } else {
                Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }
}
