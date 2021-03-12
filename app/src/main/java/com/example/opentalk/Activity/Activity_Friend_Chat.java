package com.example.opentalk.Activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.opentalk.Adapter.Adapter_Chat_Friend;
import com.example.opentalk.Code.HandlerType_Code;
import com.example.opentalk.Code.ViewType_Code;
import com.example.opentalk.Data.Chat_Msg_Data_Friend;
import com.example.opentalk.Data.Lobby_Rooms_Data;
import com.example.opentalk.Data.Logindata;
import com.example.opentalk.Handler.Face_Chat_Friend_Handler;
import com.example.opentalk.R;
import com.example.opentalk.Retrofit.ApiClient;
import com.example.opentalk.Retrofit.ChatPaging.ChatPaging;
import com.example.opentalk.Retrofit.ChatPaging.ChatPagingData;
import com.example.opentalk.Retrofit.FriendCahtData;
import com.example.opentalk.Retrofit.FriendChat;
import com.example.opentalk.Retrofit.Lobby_Search_Room;
import com.example.opentalk.Retrofit.Lobby_Search_RoomData;
import com.example.opentalk.Retrofit.SignupCk;
import com.example.opentalk.Retrofit.SignupCkData;
import com.example.opentalk.Socket_my.Socket_friend_chat_thread;
import com.example.opentalk.TimeProcess;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.opentalk.SharedPreference.PreferenceManager_member.getLoignDataPref;

/*
*
*  2021-01-26에 생성된 클래스
*  자신의 친구목록에서 원하는 친구를 클릭하면 현재 클래스로 들어온다.
*  이 클래스는 친구들과 채팅하는 클래스이다.
*
*  추후에 채팅방에서 친구 초대할 때, 채팅으로 초대 message가 오는 클래스이기도 하다.
*
* */

public class Activity_Friend_Chat extends AppCompatActivity {
    String TAG = "Activity_Friend_Chat";

    public static Context Activity_Friend_Chat_context;

    /*툴바 관련*/
    Toolbar friend_chat_toolbar;

    /**채팅방 정보 관련**/
    public String roomnum;
    public String friendid;
    public String mynickname; //닉네임 가져오기;
    public String todayyearmonthday="";
    public int roomPosition;
    Logindata logindata;
    public int pagingnum =1;
    /**페이징 관련 **/
    public int firstMysqlCahtnum = 0; //처음에 채팅 내용을 mysql에서 불러온 chat의 총개수
    int firstPosition;
    public int lastChatId =-1;
    boolean pagingCheck =true;
    int addchatnum = 0;
    String pagingDay ="";

    /**채팅이 올라오는 리사이클러뷰 관련**/
    RecyclerView friend_chat_recyclerview;
    public ArrayList<Chat_Msg_Data_Friend> chat_msg_data_ArrayList;
    public Adapter_Chat_Friend adapter_chat_friend;
    public Face_Chat_Friend_Handler face_chat_friend_handler;
    LinearLayoutManager linearLayoutManager;

    /**채팅 입력 관련**/
    EditText friend_chat_input;
    Button friend_chat_input_btn;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_chat);

        Log.d(TAG, "onCreate: 접속 확인 1");
        init();
        Log.d(TAG, "onCreate: 접속 확인 2");
        /*채팅 보내기*/
        friend_chat_input_btn.setOnClickListener(new ChatSendListener());
        friend_chat_recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "onScrolled: firstMysqlCahtnum : "+firstMysqlCahtnum);
                Log.d(TAG, "onScrolled: lastChatId : "+lastChatId);
                if(firstMysqlCahtnum>50) {
                    if(pagingCheck==true) {
                        firstPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                        Log.d(TAG, "onScrolled: firstPosition : " + firstPosition);
                        Log.d(TAG, "onScrolled: pagingCheck == true firstPosition  : " + firstPosition);
//                    int lastPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
//                    int itemTotlaCount = recyclerView.getAdapter().getItemCount();

                        if (firstPosition == 15 || firstPosition==0) {
                            pagingCheck = false;
                            int fromnum = 50 * pagingnum;
                            int count = 50;
                            chatPaging(fromnum, count);
                            pagingnum += 1;
                        } else {
                            Log.d(TAG, "onScrollStateChanged: 확인작업 firstPosition != 15");
                        }
                    }

                }
            }
        });
    }
    private void init(){
        /*툴바 관련*/
        friend_chat_toolbar = (Toolbar)findViewById(R.id.friend_chat_toolbar);
        setSupportActionBar(friend_chat_toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기(툴바 맨왼쪽에 생김)
        /*채팅방 정보 관련*/
        roomnum = getIntent().getExtras().getString("roomnum");
        friendid = getIntent().getExtras().getString("friendid");
        //만약 기존에 채팅방이 있는 상태에서 알림을 눌렀을 경우 기존 액티비티 종료 시켜주기
        if(getIntent().getExtras().getString("beforeroom","").equals("yes")){
            ((Activity_Friend_Chat)Activity_Friend_Chat.Activity_Friend_Chat_context).finish();
        }
        roomPosition = getIntent().getExtras().getInt("roomPosition");

        logindata = getLoignDataPref(this,"login_inform");
        mynickname = logindata.getUsername();
        /*채팅 올라오는 리사이클러뷰 관련*/
        friend_chat_recyclerview = (RecyclerView)findViewById(R.id.friend_chat_recyclerview);
        chat_msg_data_ArrayList = new ArrayList<>();
        adapter_chat_friend = new Adapter_Chat_Friend(this,chat_msg_data_ArrayList);
        face_chat_friend_handler = new Face_Chat_Friend_Handler(adapter_chat_friend,friend_chat_recyclerview);
        linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);

        friend_chat_recyclerview.setLayoutManager(linearLayoutManager);
        friend_chat_recyclerview.setAdapter(adapter_chat_friend);

        /*채팅 입력 관련*/
        friend_chat_input =(EditText)findViewById(R.id.friend_chat_input);
        friend_chat_input_btn = (Button)findViewById(R.id.friend_chat_input_btn);

        Activity_Friend_Chat_context = this;
        /*채팅방 입장 알려주기*/
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "friend_chat_enter");
            jsonObject.put("roomnum", roomnum);
            jsonObject.put("userid", Activity_Lobby.userid);
            jsonObject.put("roomfriendid", friendid);
            Socket_friend_chat_thread socket_friend_chat_thread = new Socket_friend_chat_thread(jsonObject);
            socket_friend_chat_thread.start();
            Log.d(TAG, "onResume: friend_chat_enter start");
        }
        catch (Exception e){
            Log.d(TAG, "onResume: friend_chat_enter error message : "+e.getMessage());
        }
        /*채팅방의 채팅들 올려주는 메소드*/
//        friendchat(roomnum);

        /*알림클릭해서 들어오면 친구 리스트의 안읽은 메세지가 안없어지는데 이렇게해서 없애주기*/
        /*추후에는 친구리스트를 hashmap으로 처리를 할까?? 그러면 리사이클러뷰가 안되나...*/
        /*그러면 hashmap에 친구이름을 키로하고 해당 포지션을 값으로 갖고있어서 한번에 찾도록 해주자.*/
        if((Activity_Friend_List)Activity_Friend_List.activity_Friend_Listcontext !=null){
            Log.d(TAG, "init: activity_Friend_Listcontext 눌아님");
            Activity_Friend_List mContext = ((Activity_Friend_List)Activity_Friend_List.activity_Friend_Listcontext);
            int size = mContext.friend_list_dataArrayList.size();
            Log.d(TAG, "init: 눌아님  size : "+size);
            for (int i=0; i<size; i++){
                if(mContext.friend_list_dataArrayList.get(i).getUniquename().equals(roomnum)){
                    Log.d(TAG, "init: activity_Friend_Listcontext 눌아님 .equals(roomnum)");
                    if(mContext.friend_list_dataArrayList.get(i).getChatnum()!=0){
                        Log.d(TAG, "init: activity_Friend_Listcontext 눌아님 !=0");
                        mContext.friend_list_dataArrayList.get(i).setChatnum(0);
                        mContext.adapter_friend_list.notifyItemChanged(i);
                        break;
                    }
                    else{
                        break;
                    }
                }
            }
        }
    }


    /*페이징 (레트로핏)*/
    private void chatPaging(int fromnum,int count){
        ChatPaging chatPaging = ApiClient.getApiClient().create(ChatPaging.class);
        HashMap<String,Object> input = new HashMap<>();
        input.put("roomnum",roomnum);
        input.put("fromnum",fromnum);
        input.put("count",count);
        input.put("lastChatId",lastChatId);
        Call<List<ChatPagingData>> call = chatPaging.postData(input);
        call.enqueue(new Callback<List<ChatPagingData>>() {
            @Override
            public void onResponse(Call<List<ChatPagingData>> call, Response<List<ChatPagingData>> response) {
                if (response.isSuccessful()){
                    addchatnum = 0;
                    Log.d(TAG, "onResponse: 확인작업 1");
                    List<ChatPagingData> result = response.body();
                    int resultsize = result.size();
                    if(resultsize!=0){
                        Log.d(TAG, "onResponse: 확인작업2");
                        for (int i=0; i<resultsize; i++){

                            String content = result.get(i).getContent();
                            String fromnickname = result.get(i).getNickname();
                            String readstate = result.get(i).getReadstate();
                            String senddatebefore = result.get(i).getDatetime();
                            String senddate = TimeProcess.hourminProcess(senddatebefore);
                            String msgtype = result.get(i).getMsgtype();
                            String roomtitle = "";
                            String openchatroomid = "-1";

                            if (msgtype.equals("textmsg")) {

                            } else if (msgtype.equals("invitemsg")) {
                                openchatroomid = result.get(i).getOpenchatroomid();
                            }

                            if (fromnickname.equals(Activity_Lobby.usernickname)) {
                                Chat_Msg_Data_Friend chat_msg_data_friend = new Chat_Msg_Data_Friend(fromnickname, content, ViewType_Code.ViewType.RIGHT_CONTENT, readstate, senddate, null, msgtype, openchatroomid, roomtitle);
                                chat_msg_data_ArrayList.add(0,chat_msg_data_friend);
                                addchatnum+=1;
                            } else {
                                Chat_Msg_Data_Friend chat_msg_data_friend = new Chat_Msg_Data_Friend(fromnickname, content, ViewType_Code.ViewType.LEFT_CONTENT, readstate, senddate, null, msgtype, openchatroomid, roomtitle);
                                chat_msg_data_ArrayList.add(0,chat_msg_data_friend);
                                addchatnum+=1;
                            }
                            //요일 셋팅해주기
                            yearmonthdatSet(senddatebefore, readstate, senddate, "yearmonthday");
                        }
                        adapter_chat_friend.notifyItemRangeInserted(0, addchatnum);
//                        adapter_chat_friend.notifyDataSetChanged();
                        pagingCheck = true;
                    }
                    else{
                        Log.d(TAG, "onResponse: ");
                    }
                }
            }
            @Override
            public void onFailure(Call<List<ChatPagingData>> call, Throwable t) {
                Log.d(TAG, "onFailure: t : "+t);
            }
        });
    }

    //페이징 할 경우, 가운데 날짜 셋팅 해주는것.
    private void yearmonthdatSet(String senddatebefore, String readstate, String senddate, String msgtype) {
        String yearmonthday = TimeProcess.yearmonthday(senddatebefore);//년월일 요일

        //처음 채팅에 들어오면 가장 마지막으로 받아오는 채팅전에는 리사이클러뷰에 day를 알려주는 글이 무조건 생긴다.
        if(chat_msg_data_ArrayList.get(1).getMsgtype().equals("day")) {
            if (chat_msg_data_ArrayList.get(1).getMessage().equals(yearmonthday)) {
                Chat_Msg_Data_Friend chat_msg_data_friend = new Chat_Msg_Data_Friend("nonickname", yearmonthday, ViewType_Code.ViewType.CENTER_CONTENT, readstate, senddate, null, "day", "-1", "");
                chat_msg_data_ArrayList.remove(1);
                chat_msg_data_ArrayList.add(0, chat_msg_data_friend);
                pagingDay = yearmonthday;
            } else {
                if(pagingDay!=yearmonthday){
                    Chat_Msg_Data_Friend chat_msg_data_friend = new Chat_Msg_Data_Friend("nonickname", yearmonthday, ViewType_Code.ViewType.CENTER_CONTENT, readstate, senddate, null, "day", "-1", "");
                    chat_msg_data_ArrayList.add(0, chat_msg_data_friend);
                }
                pagingDay = yearmonthday;
            }
        }
        else{
            if(pagingDay!=yearmonthday){
                Chat_Msg_Data_Friend chat_msg_data_friend = new Chat_Msg_Data_Friend("nonickname", yearmonthday, ViewType_Code.ViewType.CENTER_CONTENT, readstate, senddate, null, "day", "-1", "");
                chat_msg_data_ArrayList.add(0, chat_msg_data_friend);
                pagingDay = yearmonthday;
                addchatnum+=1;
            }
            else{
            }
        }
    }

    /*채팅 보내는 리스너*/
    class ChatSendListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            try {
                String msg = friend_chat_input.getText().toString();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type","friend_chat");
                jsonObject.put("roomnum", roomnum);
                jsonObject.put("msg",msg);
                jsonObject.put("to",friendid);
                jsonObject.put("from",Activity_Lobby.userid);
                jsonObject.put("from_nickname",mynickname);
                jsonObject.put("msgtype","textmsg");
                Socket_friend_chat_thread socket_friend_chat_thread = new Socket_friend_chat_thread(jsonObject);
                socket_friend_chat_thread.start();

//                Chat_Msg_Data_Friend chat_msg_data_friend = new Chat_Msg_Data_Friend(mynickname,msg, ViewType_Code.ViewType.RIGHT_CONTENT,null,null,null);
//                chat_msg_data_ArrayList.add(chat_msg_data_friend);
//                adapter_chat_friend.notifyItemChanged(chat_msg_data_ArrayList.size()-1);
                friend_chat_input.setText("");

            }catch (Exception e){
                Log.d(TAG, "onResume: friend_chat_input error message : "+e.getMessage());
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

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        /*채팅방에서 나간거 알려주기*/
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "friend_chat_exit");
            jsonObject.put("roomnum", roomnum);
            jsonObject.put("userid", Activity_Lobby.userid);
            Socket_friend_chat_thread socket_friend_chat_thread = new Socket_friend_chat_thread(jsonObject);
            socket_friend_chat_thread.start();
        }
        catch (Exception e){
            Log.d(TAG, "onResume: friend_chat_exit error message : "+e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Activity_Friend_Chat_context = null;
    }
}
