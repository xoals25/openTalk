package com.example.opentalk.Activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.opentalk.BitmapConverter;
import com.example.opentalk.R;
import com.example.opentalk.SharedPreference.PreferenceManager_member;

public class ImgTestVersion extends AppCompatActivity {

    ImageView img;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img_testversion);
        img = (ImageView)findViewById(R.id.img);

        String profileImg_String_tobitmap = PreferenceManager_member.getLoignDataPref(this,"login_inform").getImgString_tobitmap();
        Bitmap profileImg_bitmap = BitmapConverter.StringToBitmap(profileImg_String_tobitmap);
        img.setImageBitmap(profileImg_bitmap);
    }
}
