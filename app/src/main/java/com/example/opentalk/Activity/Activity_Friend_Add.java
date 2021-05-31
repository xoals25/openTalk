package com.example.opentalk.Activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.opentalk.Adapter.TabPagerAdapter_frined_add;
import com.example.opentalk.Data.Logindata;
import com.example.opentalk.R;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import static com.example.opentalk.SharedPreference.PreferenceManager_member.getLoignDataPref;

public class Activity_Friend_Add extends AppCompatActivity {
    public static Context context_Activity_Friend_Add;
    String TAG = "Activity_Friend_Add";
    Toolbar myToolbar;
    public TabLayout tabLayout;
    public ViewPager viewPager;

    /*로그인 데이터 정보 가져오기, 담아줄 변수*/
    Logindata logindata;
//    String userid;
//    String usernickname;

//    /*socket 연결*/

    public TabPagerAdapter_frined_add tabPagerAdapter_frined_add;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_add);
        context_Activity_Friend_Add = this;
        initfindViewById();
//        Activity_Lobby activity_lobby = new Activity_Lobby();

        Log.d(TAG, "onCreate: Activity_Friend_Add에서 MainActivity에있는 socket이 null인지 확인하기 ");

        /*탭선택할경우에..*/
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
//                Toast.makeText(Activity_Friend_Add.this," "+tab.getPosition(),Toast.LENGTH_SHORT).show();
                if(tab.getPosition()==0){
                    //친구추가하는 화면이라면
                    Log.d("TAG", "onTabSelected: 친추화면 : ");
                }
                else if(tab.getPosition()==1){

                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Log.d("TAG", "onCreate: ");

    }

    /*findViewById 및 간단한 설정값*/
    private void initfindViewById() {
        // 추가된 소스, Toolbar를 생성한다.
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기(툴바 맨왼쪽에 생김)
        /*Initializing the TabLayout*/
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);

        tabLayout.addTab(tabLayout.newTab().setText("친구추가하기"));
        tabLayout.addTab(tabLayout.newTab().setText("받은 친구요청"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        /*Initializing ViewPager*/
        viewPager = (ViewPager)findViewById(R.id.viewPager);

        tabPagerAdapter_frined_add = new TabPagerAdapter_frined_add(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(tabPagerAdapter_frined_add);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        /*로그인정보 입력*/
        logindata = getLoignDataPref(this, "login_inform");
//        userid = logindata.getUserid();
//        usernickname = logindata.getUsername();

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
        context_Activity_Friend_Add = null;
    }
}
