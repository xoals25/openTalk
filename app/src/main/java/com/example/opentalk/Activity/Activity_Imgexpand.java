package com.example.opentalk.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.opentalk.BitmapConverter;
import com.example.opentalk.R;

/*
*  2021-01-14 생성
*  음성채팅,화상채팅에서 올라온 사진을 클릭하게 될경우 현재 클래스가 생성된다.
*
* */

public class Activity_Imgexpand extends AppCompatActivity {
    Toolbar myToolbar;
    /*채팅할 때 이미지 클릭시 크게 보여주느 뷰*/
    ImageView imgExpanidng;
    byte[] imgByte;
    Bitmap imgBitmap;
    String imgString;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imgexpand);
        init();

    }

    private void init(){
        // 추가된 소스, Toolbar를 생성한다.
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기(툴바 맨왼쪽에 생김)
        imgExpanidng = (ImageView)findViewById(R.id.imgExpanidng);
        Intent intent = getIntent();
        imgByte = intent.getExtras().getByteArray("imgString");
        imgString = BitmapConverter.ByteArrayToString(imgByte);
        imgBitmap = BitmapConverter.StringToBitmap(imgString);
//        Bitmap resizeBitmap =Bitmap.createScaledBitmap(imgBitmap, 4096, 4096, true);
        imgExpanidng.setImageBitmap(imgBitmap);
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
