package com.example.opentalk.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.opentalk.Adapter.Adapter_Lobby_rooms;
import com.example.opentalk.AlertDialog_opentalk;
import com.example.opentalk.BitmapConverter;
import com.example.opentalk.Data.Lobby_Rooms_Data;
import com.example.opentalk.Data.Logindata;
import com.example.opentalk.Http.HttpConnection_Lobby_Rooms;
import com.example.opentalk.Http.HttpConnection_room_enter;
import com.example.opentalk.R;
import com.example.opentalk.Retrofit.ApiClient;
import com.example.opentalk.Retrofit.FcmToken.FcmToken;
import com.example.opentalk.Retrofit.FcmToken.FcmTokenData;
import com.example.opentalk.Retrofit.Lobby_Search_Room;
import com.example.opentalk.Retrofit.Lobby_Search_RoomData;
import com.example.opentalk.ServerIp;
import com.example.opentalk.SharedPreference.PreferenceManager_member;
import com.example.opentalk.Socket_my.Socket_Other_LoginCk_thread;
import com.example.opentalk.Socket_my.Socket_connect_offline_thread;
import com.example.opentalk.Service.TaskCloseNoticeService;
import com.example.opentalk.Socket_my.Socket_friend_chat_thread;
import com.example.opentalk.WebRTC.NPermission;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.opentalk.SharedPreference.PreferenceManager_member.getLoignDataPref;

/*
*
*  수정 날짜 2021/01/13
*  수정 내용 : 카메라 퍼미션 추가 (Npermission에 오디오 퍼미션도 있는데 이걸로 권한받아도 음성채팅 되는지 확인해보자)
*
*  클래스 설명 : 이 클래스는 채팅방 들어가기전에 대기방을 보여주는 클래스이다.
*
* */

public class Activity_Lobby extends AppCompatActivity implements NPermission.OnPermissionResult {
    Toolbar myToolbar;
    String TAG ="Activity_Lobby";
    private NPermission nPermission;
    private boolean isGranted;
    SwipeRefreshLayout refresh_layout;

    /*방뿌려주는,방입장하는 Http통신클래스 전역변수*/
    HttpConnection_Lobby_Rooms httpConnection_lobby_rooms;
    HttpConnection_room_enter httpConnection_room_enter;
    /*navigation drawer화면에 nickname과 email TextView*/
    TextView drawer_nickname;
    TextView drawer_emailid;
    CircleImageView drawer_profileIMG;
    DrawerLayout mDrawerLayout;
    NavigationView navigationView;

    /*로그인 데이터 정보 가져오기, 담아줄 변수*/
    Logindata logindata;
    public static String userid; //static 해준이유 어디서나 로그인한 아이디는 같아야하니까.
    public static String usernickname; //static 해준이유 어디서나 로그인한 아이디는 같아야하니까.
    public static String profileImgToString;

    /*방들을 lobby에 뿌려줄때 필요한 재료들 */
    RecyclerView lobby_room_RecyclerView;
    Adapter_Lobby_rooms adapter_lobby_rooms;
    LinearLayoutManager linearLayoutManager;
    ArrayList<Lobby_Rooms_Data> lobby_rooms_dataArrayList = new ArrayList<>();

    /*방만들러가는 버튼*/
//    Button lobby_roomCreate_btn;
    LinearLayout lobby_roomCreate_btn;
//    ConstraintLayout roomCreate_btn_palent_view;
    /*방들 결과값 담아주는 곳*/
    String mJsonString;

    /*방들 몇개까지 보여줄지 정하기*/
    //추후에 밑으로 내리기하면 변경해서 해주자
    //그럼 기존에 방정보들은 어떻게 되는거지?? shard에 저장하는건가?? 흠 고민해보자.
    int rooms_num=10;

    /*socket 연결*/
    public static Socket socket_friend_list = null ;
    public Socket socket_other_loginCk = null;

    /*검색관련*/
    EditText search_content;
    ImageView search_btn;
    TextView lobby_room_noexist_text; //검색한방이 없을 경우에는 알림 글
    Spinner chat_spinner;

    int pagingnum =1;
    boolean pagingck = true;

    StringBuilder exceptnum = new StringBuilder();
//    String exceptnum = "-1";
    public static Context mainContext;
    public boolean compulsoryLogoutCk = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        startService(new Intent(this, TaskCloseNoticeService.class));
        exceptnum.append("-1");

        nPermission = new NPermission(true);
        mainContext =this;
//        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        //녹음 권한 받기
        permissionCheck();
        initfindViewById();
        /***방뿌려주는 리사이클러뷰 연결할 어답터(방을 클릭도 할 수 있다.)
         *
         * 방클릭할때 voice방넘어갈때 방 번호도 같이 넘김 ->음성통신 중개서버에 방번호 넘겨주기위함
         *
         *
         * */

        /*방 선택해서 입장하기*/
        adapter_lobby_rooms = new Adapter_Lobby_rooms(this, lobby_rooms_dataArrayList, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getTag()!=null) {
                    int position = (int)v.getTag();
                    int adapter_room_id = lobby_rooms_dataArrayList.get(position).getId();
                    String adapter_room_type = lobby_rooms_dataArrayList.get(position).getRoom_type();
                    String adapter_room_public = lobby_rooms_dataArrayList.get(position).getRoom_public();
                    String adapter_room_pwd ="";
                    Log.d(TAG, "onClick: 확인1"+adapter_room_public);
                    if(adapter_room_public.equals("공개방")){
                        Log.d(TAG, "onClick: 확인2");
                        httpConnection_room_enter = new HttpConnection_room_enter(Activity_Lobby.this,adapter_room_type,adapter_room_id,lobby_rooms_dataArrayList.get(position).getRoom_title());
                        httpConnection_room_enter.execute("http://"+ServerIp.IP_ADDRESS_ADD_FOLDER_NAME+"/room_enter.php",String.valueOf(adapter_room_id),adapter_room_public,adapter_room_pwd);
                    }
                    /****
                     * 현재 인원수가 꽉찬 비공개방을 클릭하게되면 비밀번호를 입력하는 창이뜨고 입력하고 확인을 누르면 인원수가 꽉찼다고 메시지가 뜨는데
                     * 추후에는 인원수가 꽉찬 비공개방을 클릭하면 비밀번호 입력하는 창이 뜨지않고 바로 인원수가 꽉찼다고 알려주도록하자
                     * ->인원수가 꽉차지 않았을때 httpConnection을 두번해야한다. 첫번째 방클릭할때 두번째는 alertdialog클래스에서 입력하고 확인눌렀을때 이렇게 두번하면된다.
                     * ****/
                    else if(adapter_room_public.equals("비공개방")){
                        httpConnection_room_enter = new HttpConnection_room_enter(Activity_Lobby.this,adapter_room_type,adapter_room_id,lobby_rooms_dataArrayList.get(position).getRoom_title());
                        AlertDialog_opentalk alertDialog_opentalk = new AlertDialog_opentalk(Activity_Lobby.this,httpConnection_room_enter,ServerIp.IP_ADDRESS_ADD_FOLDER_NAME,adapter_room_id,adapter_room_public);
                        /*다이얼로그 만들어주기*/
                        alertDialog_opentalk.dialog();
                        // 창 띄우기
                        alertDialog_opentalk.alertDialog.show();
                    }
                }
            }
        });
        /*리사이클러뷰 셋팅*/
        lobby_room_RecyclerView.setLayoutManager(linearLayoutManager);
        lobby_room_RecyclerView.setAdapter(adapter_lobby_rooms);

        /*초기에 http통신해서 방들 뿌려주기*/
//        httpConnection_lobby_rooms = new HttpConnection_Lobby_Rooms(Activity_Lobby.this,adapter_lobby_rooms,lobby_rooms_dataArrayList);
//        httpConnection_lobby_rooms.execute("http://"+IP_ADDRESS+"/lobby_rooms.php",String.valueOf(rooms_num));
        search_rooms("","전체",pagingnum,false,exceptnum.toString());

        /*방만들기 버튼*/
        lobby_roomCreate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Lobby.this,Activity_Room_Create.class);
                startActivity(intent);
            }
        });
        /*화면 당기기*/
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //방 새로고침
                refreshbtn();
                refresh_layout.setRefreshing(false);
            }
        });
        /*검색 기능*/
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chat_spinner_text = chat_spinner.getSelectedItem().toString();
                pagingnum = 1;
                exceptnum.delete(0,exceptnum.length()); //검색버튼을 눌러 새로 받아와야한다. 그래서 제외숫자를 다 지워준다.
                exceptnum.append("-1");
                search_rooms(search_content.getText().toString(),chat_spinner_text,pagingnum,true,exceptnum.toString());
            }
        });
        /*필터 기능*/
        chat_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String chat_spinner_text = chat_spinner.getSelectedItem().toString();
                pagingnum = 1;
                exceptnum.delete(0,exceptnum.length()); //검색버튼을 눌러 새로 받아와야한다. 그래서 제외숫자를 다 지워준다.
                exceptnum.append("-1");
                search_rooms(search_content.getText().toString(),chat_spinner_text,pagingnum,true,exceptnum.toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "onNothingSelected: 2");
            }
        });

        /*RecyclerView 이용시 페이징 처리 현재화면에 출력된 리스트중 마지막 View의 position을 리턴*/
        lobby_room_RecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int lastPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotlaCount = recyclerView.getAdapter().getItemCount();
                if(lastPosition+1 == itemTotlaCount && pagingck==true){
                    pagingck=false;
                    String chat_spinner_text = chat_spinner.getSelectedItem().toString();
                    pagingnum +=1;
                    search_rooms(search_content.getText().toString(),chat_spinner_text,pagingnum,false,exceptnum.toString());
                }
                else{
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Toast.makeText(this,"asdasd",Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT < 23 || isGranted) {

        } else {
            nPermission.requestPermission(this, Manifest.permission.CAMERA);
        }

        if(!profileImgToString.equals("false")) {
            drawer_profileIMG.setImageBitmap(BitmapConverter.StringToBitmap(profileImgToString));
        }
        drawer_nickname.setText(usernickname); //header의 정보 변경될수있으니 리줌에다가 유저닉네임 set해주기
    }

    /*findViewById 및 간단한 설정값*/
    private void initfindViewById(){
        /*
        *자동 로그인 저장 하기
        * -1 = 자동 로그인 설정이 안되어있다면
        * 1 = 자동 로그인 설정 했을때
        * 2 = 자동 로그인 해제 했을때
        * */
        if(PreferenceManager_member.getInt(this,"autoLogin")==-1) {
            PreferenceManager_member.setInt(this, "autoLogin", 1);
        }


        refresh_layout = (SwipeRefreshLayout)findViewById(R.id.refresh_layout);
//        lobby_roomCreate_btn = (Button)findViewById(R.id.lobby_roomCreate_btn);
        lobby_roomCreate_btn = (LinearLayout) findViewById(R.id.lobby_roomCreate_layout_btn);

        logindata = getLoignDataPref(this,"login_inform");
        userid = logindata.getUserid();
        usernickname = logindata.getUsername();
        profileImgToString = logindata.getImgString_tobitmap();
        /*방들을 lobby에 뿌려줄때 필요한 재료들 */
        lobby_room_RecyclerView = (RecyclerView)findViewById(R.id.lobby_room_RecyclerView);
        linearLayoutManager = new LinearLayoutManager(this);


//        Lobby_Rooms lobby_rooms = new Lobby_Rooms();
//        lobby_rooms.execute("http://"+IP_ADDRESS+"/lobby_rooms.php",String.valueOf(rooms_num));


        // 추가된 소스, Toolbar를 생성한다.
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기(툴바 맨왼쪽에 생김)
        actionBar.setHomeAsUpIndicator(R.drawable.hamburger); //뒤로가기 버튼을 햄버거 버튼 이미지로 변경해주기

        /*navigation drawer*/
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        /*navigation drawer header부분 가져오기*/
        View headerView = navigationView.getHeaderView(0);
        drawer_nickname = (TextView)headerView.findViewById(R.id.drawer_nickname);
        drawer_emailid = (TextView)headerView.findViewById(R.id.drawer_emailid);
        drawer_profileIMG =headerView.findViewById(R.id.drawer_profileIMG);

        drawer_profileIMG.setOnClickListener(new ProfileImgClickListner());
//        drawer_nickname.setText(usernickname); //header의 nickname구간 로그인한 nickname으로 변경
        Log.d(TAG, "initfindViewById: 확인작업입니다. "+usernickname);
        drawer_emailid.setText("("+userid+")"); //header의 email구간 로그인한 email로 변경
        //navigation drawer 아이템 선택하는곳 (R.id.mypage=마이페이지 화면으로 이동,friend는 친구화면으로 이동)
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(false); //체크되어있음
                mDrawerLayout.closeDrawers();
                int id = item.getItemId();
                if(id == R.id.mypage){
                    Intent intent = new Intent(Activity_Lobby.this,Activity_Mypage.class);
                    startActivity(intent);
                }
                else if(id == R.id.friend){
                    Intent intent = new Intent(Activity_Lobby.this,Activity_Friend_List.class);
                    startActivity(intent);
                }
                return false;
            }

        });

        /*검색 관련*/
        search_content = (EditText)findViewById(R.id.search_content);
        search_btn = (ImageView) findViewById(R.id.search_btn);
        lobby_room_noexist_text=(TextView)findViewById(R.id.lobby_room_noexist_text);
        chat_spinner =(Spinner)findViewById(R.id.chat_spinner);

        //소켓통신 시작
        ClientThread clientThread = new ClientThread();
        clientThread.start();

        /*
        * 중복 로그인 방지하기위한 코드
        * Socket통신
        * */
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "login");
            jsonObject.put("userid", userid);
            Socket_Other_LoginCk_thread socket_other_loginCk_thread = new Socket_Other_LoginCk_thread(socket_other_loginCk,userid,this,true,jsonObject);
            socket_other_loginCk_thread.start();
        }
        catch (Exception e){
            Log.d(TAG, "Activity_Lobby Socket_Other_LoginCk_thread:  error message : "+e.getMessage());
        }


        //가져온 토큰 mysql에 저장
        if(PreferenceManager_member.getString(this,"fcmtoken")!=null) {
            Log.d(TAG, "initfindViewById: 저장되어있는 토큰 있음");
            token_store();
        }
        else{
            Log.d(TAG, "initfindViewById: 저장되어있는 토큰 없음");
        }

        /*백그라운드에서 알림눌러서 들어왔을 경우에*/
        Intent getintent = getIntent();
        if (getintent.getExtras() != null) {
            if (getintent.getExtras().getString("notifi") != null) {
                if(getintent.getExtras().getString("notifi").equals("notifiEnter")) {
                    Intent startintent = new Intent(this, Activity_Friend_List.class);
                    startintent.putExtra("notifi", "notifiEnter");
                    startintent.putExtra("roomnum", getintent.getExtras().getString("roomnum"));
                    startintent.putExtra("friendid", getintent.getExtras().getString("friendid"));
                    startActivity(startintent);
                }
            }
        }
    }

    /*방 검색 http통신 (레트로핏)*/
    private void search_rooms(String search_content,String search_type,int pagingnum,boolean clear,String exceptnumToString){
        Lobby_Search_Room lobby_search_room = ApiClient.getApiClient().create(Lobby_Search_Room.class);
        HashMap<String,Object> input = new HashMap<>();
        input.put("searchcontent",search_content);
        input.put("searchtype",search_type);
        input.put("pagingnum",pagingnum);
        input.put("exceptnum",exceptnumToString);
        Call<List<Lobby_Search_RoomData>> call = lobby_search_room.postData(input);
        call.enqueue(new Callback<List<Lobby_Search_RoomData>>() {
            @Override
            public void onResponse(Call<List<Lobby_Search_RoomData>> call, Response<List<Lobby_Search_RoomData>> response) {
                if (response.isSuccessful()){
                    Log.d(TAG, "onResponse: 확인작업입니다.");
                    List<Lobby_Search_RoomData> result = response.body();
                    int resultsize = result.size();
                    if(resultsize!=0){
                        pagingck=true;
                        lobby_room_noexist_text.setVisibility(View.GONE);
                        lobby_room_RecyclerView.setVisibility(View.VISIBLE);
                        if(clear==true) {
                            lobby_rooms_dataArrayList.clear();
                        }
                        for (int i=0; i<resultsize; i++){
                            int room_id = result.get(i).getRooms_id();
                            String room_title = result.get(i).getRooms_title();
                            String room_type = result.get(i).getRooms_type();
                            String room_public = result.get(i).getRooms_public();
                            int room_people_numlimit = result.get(i).getRooms_person_numlimit();
                            int room_people_attend_num = result.get(i).getRooms_person_attend_num();

//                           //랜덤으로 뿌려준 방들의 id를 추후에 제외 숫자로 서버로 넘겨주기위해  1,2,3,4,5 ... 이런식으로 저장해준다.
                            Activity_Lobby.this.exceptnum.append(","+room_id);


                            Lobby_Rooms_Data lobby_rooms_data = new Lobby_Rooms_Data(room_id,room_title,room_type,room_public,room_people_numlimit,room_people_attend_num);
                            lobby_rooms_dataArrayList.add(lobby_rooms_data);
                        }
                        adapter_lobby_rooms.notifyDataSetChanged();
                    }
                    else{
                        if(clear==true) {
                            Log.d(TAG, "onResponse: ");
                            lobby_room_RecyclerView.setVisibility(View.GONE);
                            lobby_room_noexist_text.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Lobby_Search_RoomData>> call, Throwable t) {
                Log.d(TAG, "onFailure: t : "+t);
            }
        });
    }

    /*토큰 저장 (레트로핏)*/
    private void token_store(){
        FcmToken fcmToken = ApiClient.getApiClient().create(FcmToken.class);
        HashMap<String,Object> input = new HashMap<>();
        input.put("userid",userid);
        input.put("usertoken",PreferenceManager_member.getString(this,"fcmtoken"));
        Call<FcmTokenData> call = fcmToken.postData(input);
        call.enqueue(new Callback<FcmTokenData>() {
            @Override
            public void onResponse(Call<FcmTokenData> call, Response<FcmTokenData> response) {
                if (response.isSuccessful()){
                    boolean isresult = response.body().isResult();
                    Log.d(TAG, "onResponse: 토큰 저장 성공 여부 확인 :"+isresult );
                    if(isresult==true) {
                        Log.d(TAG, "onResponse: 토큰 저장 성공");
                    }
                }
            }
            @Override
            public void onFailure(Call<FcmTokenData> call, Throwable t) {
                Log.d(TAG, "onFailure: t : "+t);
            }
        });
    }

    /*방 새로고침*/
    private void refreshbtn(){
//        /*초기에 방들 뿌려주기*/
        pagingnum = 1;
        String chat_spinner_text = chat_spinner.getSelectedItem().toString();
        exceptnum.delete(0,exceptnum.length()); //검색버튼을 눌러 새로 받아와야한다. 그래서 제외숫자를 다 지워준다.
        exceptnum.append("-1");
        search_rooms(search_content.getText().toString(),chat_spinner_text,pagingnum,true,exceptnum.toString());
    }
    /***
     *
     * 12-03
     * 친구관련,접속여부를 위한 소켓 통신
     * 이것은 원래는 클래스파일을 따로 만들어줘야하는데 추후에 옮겨주기
     *
     * ***/


    class ClientThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                Log.d(TAG, "run: ClientThread");
                //socket 객체생성(IP,PORT)
                socket_friend_list = new Socket(ServerIp.SERVER_IP, ServerIp.SOCKET_SERVER_PORT_CONNECT);
                Log.d(TAG, "onCreateView: socket : "+socket_friend_list.getPort());
                //PrintWriter는 File(String), OutputStream, Writer 등의 객체를 인수로 받아 더 간편하게 스트림을 연결할 수 있다.
//                PrintWriter printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket_friend_list.getOutputStream())));

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket_friend_list.getOutputStream()));
                bufferedWriter.write(userid+"\n");
                bufferedWriter.flush();
//                bufferedWriter.close();
                Log.d(TAG, "socket : "+socket_friend_list);
            }
            catch (IOException e) {

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.lobby_toolbar, menu);

        return true;
    }
    /*툴바메뉴 선택할때 navigation drawer,새로고침 버튼이 들어가있다.*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.refresh: { // 오른쪽 상단 버튼 눌렀을 때
                refreshbtn();
                return false;
            }
            case android.R.id.home:{
                mDrawerLayout.openDrawer(GravityCompat.START);
                return false;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Toast.makeText(this,"lobby_onPause",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Toast.makeText(this,"lobby_destory",Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onDestroy: lobby 종료1");

        //여기에 종료한다는것을 알려주자.
        //단 누군가 다른기기에 접속해서 강제종료가 아닐경우 false=자기가 종료함 true = 강제 종료
        //밑에 스레드가 시작하면 다른기기에서 로그인했지만, 로그아웃처리가되어 친구들에게 로그아웃처럼 보인다.
        if(!compulsoryLogoutCk) {
            Log.d(TAG, "onDestroy: lobby 종료2 ");
            Socket_connect_offline_thread socket_connect_offline_thread = new Socket_connect_offline_thread();
            socket_connect_offline_thread.start();
        }

//        moveTaskToBack(true);						// 태스크를 백그라운드로 이동
//        finishAndRemoveTask();						// 액티비티 종료 + 태스크 리스트에서 지우기
//        android.os.Process.killProcess(android.os.Process.myPid());	// 앱 프로세스 종료
        mainContext=null;
    }

    public void permissionCheck(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        nPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionResult(String permission, boolean isGranted) {
        switch (permission) {
            case Manifest.permission.CAMERA:
                this.isGranted = isGranted;
                if (!isGranted) {
                    nPermission.requestPermission(this, Manifest.permission.CAMERA);
                } else {
                    nPermission.requestPermission(this, Manifest.permission.RECORD_AUDIO);
                }
                break;
            default:
                break;
        }
    }

    class ProfileImgClickListner implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            profileexpandDialog();
        }
    }

    //프로필사진 확대 화면
    private void profileexpandDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_dialog_profileimg, null);
        builder.setView(view);
        ImageView profile_img_expand = (ImageView)view.findViewById(R.id.profile_img_expand);
        profile_img_expand.setImageBitmap(BitmapConverter.StringToBitmap(profileImgToString));
//        Button profile_img_btn = (Button)view.findViewById(R.id.profile_img_btn);
        final AlertDialog dialog = builder.create();
//        profile_img_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Activity_Lobby.this,Activity_Profile_Change.class);
//                startActivityForResult(intent,100);
//                dialog.dismiss();
//            }
//        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
