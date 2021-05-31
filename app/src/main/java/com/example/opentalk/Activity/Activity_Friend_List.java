package com.example.opentalk.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.Volley;
import com.example.opentalk.Adapter.Adapter_Friend_List;
import com.example.opentalk.Data.Friend_List_Data;
import com.example.opentalk.Data.Logindata;
import com.example.opentalk.Handler.Friend_list_Handler;
import com.example.opentalk.Http.HttpConnection_Friend_List;
import com.example.opentalk.Http.VolleyprofileIMG_Friend_List;
import com.example.opentalk.R;
import com.example.opentalk.ServerIp;
import com.example.opentalk.Socket_my.Socket_friend_list_receive_thread;
import com.example.opentalk.VolleyRequestQhelper;

import java.util.ArrayList;

import static com.example.opentalk.SharedPreference.PreferenceManager_member.getLoignDataPref;

public class Activity_Friend_List extends AppCompatActivity {

    public static Context activity_Friend_Listcontext;
    Toolbar myToolbar;
    String TAG ="Activity_Friend_List";

    /*로그인 데이터 정보 가져오기, 담아줄 변수*/
    Logindata logindata;
//    String userid;
//    String usernickname;

    //http로 요청한 친구 list 요청할 서버 주소
    String IP_ADDRESS = ServerIp.IP_ADDRESS_ADD_FOLDER_NAME;

    /*친구목록 뿌려주는 리사이클러뷰 관련*/
    RecyclerView friend_list_recyclerview;
    Adapter_Friend_List adapter_friend_list;
    LinearLayoutManager linearLayoutManager;
    public ArrayList<Friend_List_Data> friend_list_dataArrayList;
    public ArrayList<String> img_path_ArrayList;
    public Friend_list_Handler friend_list_handler;

    Socket_friend_list_receive_thread socket_friend_list_receive_thread;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        activity_Friend_Listcontext = this;
        initfindViewById();
    }

    private void initfindViewById(){
        // 추가된 소스, Toolbar를 생성한다.
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기(툴바 맨왼쪽에 생김)
        /*로그인정보 입력*/
        logindata = getLoignDataPref(Activity_Friend_List.this, "login_inform");
//        userid = logindata.getUserid();
//        usernickname = logindata.getUsername();

        //리사이클러뷰 관련
        friend_list_recyclerview = (RecyclerView)findViewById(R.id.friend_list_recyclerview);
        linearLayoutManager = new LinearLayoutManager(this);
        friend_list_dataArrayList = new ArrayList<>();
        img_path_ArrayList = new ArrayList<>();

        adapter_friend_list = new Adapter_Friend_List(friend_list_dataArrayList, img_path_ArrayList, this, new FriendAdapterClickListener());

        friend_list_recyclerview.setAdapter(adapter_friend_list);
        friend_list_recyclerview.setLayoutManager(linearLayoutManager);
        friend_list_handler = new Friend_list_Handler(adapter_friend_list);

        if(VolleyRequestQhelper.requestQueue==null){
            VolleyRequestQhelper.requestQueue = Volley.newRequestQueue(this);
        }

        HttpConnection_Friend_List httpConnection_friend_list = new HttpConnection_Friend_List(this,friend_list_handler,friend_list_dataArrayList);
        httpConnection_friend_list.execute("http://"+IP_ADDRESS+"/friend_list.php",Activity_Lobby.userid);

        //친구목록 누군가 수락을 눌렀을경우 소켓통신으로 넘어오는 친구 데이터 받아오고,화면갱신해주는 스레드
        socket_friend_list_receive_thread = new Socket_friend_list_receive_thread(friend_list_handler,friend_list_dataArrayList,Activity_Lobby.socket_friend_list);
        socket_friend_list_receive_thread.start();

//        notifiPassActivity();
    }


    public void notifiPassActivity(){
        /*백그라운드에서 알림눌러서 들어왔을 경우에*/
        Intent getintent = getIntent();
        if (getintent.getExtras() != null) {
            if (getintent.getExtras().getString("notifi") != null) {
                if(getintent.getExtras().getString("notifi").equals("notifiEnter")) {
                    Log.d(TAG, "initfindViewById: 여기로지나감");
                    Intent startintent = new Intent(this, Activity_Friend_Chat.class);
                    startintent.putExtra("roomnum", getintent.getExtras().getString("roomnum"));
                    startintent.putExtra("friendid", getintent.getExtras().getString("friendid"));
                    for (int i=0; i<friend_list_dataArrayList.size();i++){
                        if(friend_list_dataArrayList.get(i).getUniquename().equals(getintent.getExtras().getString("roomnum"))){
                            startintent.putExtra("roomPosition",i);
                            break;
                        }
                    }
                    startActivity(startintent);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.friend_list_toolbar, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.friend_add_btn:{
                Intent intent = new Intent(this,Activity_Friend_Add.class);
//                if(socket_friend_list_receive_thread.onoff==false){
//                    socket_friend_list_receive_thread.onoff=true;
//                }
                startActivity(intent);
            }
            return false;
            case android.R.id.home:{
                finish();
                return false;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /*친구 클릭할시 채팅방 들어가는 리스너*/
    class FriendAdapterClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(v.getTag()!=null) {
                int position = (int) v.getTag();
                Intent intent = new Intent(Activity_Friend_List.this,Activity_Friend_Chat.class);
                String roomnum = friend_list_dataArrayList.get(position).getUniquename();
                String friendid = friend_list_dataArrayList.get(position).getFriend_email();
                /*소켓 중지 시키기*/
//                socket_friend_list_receive_thread.onoff=false;
                intent.putExtra("roomnum",roomnum);
                intent.putExtra("friendid",friendid);
                intent.putExtra("roomPosition",position);

                friend_list_dataArrayList.get(position).setChatnum(0);
                adapter_friend_list.notifyItemChanged(position);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: socket_friend_list_receive_thread.onoff : "+socket_friend_list_receive_thread.onoff);


    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");

    }

        @Override
    protected void onDestroy() {
        super.onDestroy();
            activity_Friend_Listcontext=null;
//        Toast.makeText(this,"Friend_list_destroy",Toast.LENGTH_SHORT).show();
    }


}
