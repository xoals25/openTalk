package com.example.opentalk.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.Volley;
import com.example.opentalk.Adapter.Adapter_Chat;
import com.example.opentalk.Adapter.Adapter_voice_participant_list;
import com.example.opentalk.Code.HandlerType_Code;
import com.example.opentalk.Data.Chat_Msg_Data;
import com.example.opentalk.Data.Logindata;
import com.example.opentalk.Data.VoiceChatRoom_Participant_List_Data;
import com.example.opentalk.Handler.Voice_Chat_Handler;
import com.example.opentalk.Http.HttpConnection_room_exit;
import com.example.opentalk.Http.VolleyprofileIMG_Participant_List;
import com.example.opentalk.R;
import com.example.opentalk.Code.ViewType_Code;
import com.example.opentalk.VoiceCommunication.AudioReceiveThread;
import com.example.opentalk.VoiceCommunication.AudioSendThread;
import com.example.opentalk.VoiceCommunication.Data.OtherUserIP_Data;
import com.example.opentalk.VoiceCommunication.Data.OtherUserIP_Voice_Data;
import com.example.opentalk.VoiceCommunication.Enter;
import com.example.opentalk.VoiceCommunication.Exit;
import com.example.opentalk.VoiceCommunication.Private_IPAddress;
import com.example.opentalk.VolleyRequestQhelper;


import org.json.JSONArray;
import org.json.JSONObject;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.example.opentalk.SharedPreference.PreferenceManager_member.getLoignDataPref;

public class Activity_Room_Voice extends AppCompatActivity {

    static public Context mContext;
    /*툴바*/
    Toolbar myToolbar;
    ActionBar actionBar;
    /*화면 애니메이션*/
    //슬라이드 왼쪽으로 이동(집어넣기) 애니메이션
    Animation translateLeftAnim;
    //왼쪽이동 after버전
    Animation translateLeftAnim_after;
    //슬라이드 오른쪽으로 이동(나오게 하기) 애니메이션
    Animation translateRightAnim;
    //화면 true면 채팅화면이 보여지는 상태 false면 채팅화면이 사라지고 참가자목록 레이아웃 보임
    boolean isPageOpen = true;
    //참가자 레이아웃
    LinearLayout room_participant_list_LinearLayout;
    //채팅창 레이아웃
    LinearLayout room_chat_LinearLayout;
    /*핸들러들*/
    //화면 애니메이션할때 버튼 비활성화,활성화 해주는 핸들러
    Voice_Chat_Handler voice_chat_handler;

    String TAG ="Activity_Room_Voice";
    Intent intent_room_id;
    int room_num;
    /*로그인한 아이디 담고있는 데이터*/
    Logindata logindata;
//    String myid ; //닉네임 가져오기
//    String email_id;
    /*채팅 입력하는곳,채팅보내는 버튼*/
    EditText room_voice_input;
    Button room_voice_input_btn; //버튼 누르면
    /***노드 js 소켓 통신관련 - 소켓 객체생성***/
    private Socket mSocket;
    boolean kick_check = true; // true면 내가 나간것, false면 강퇴당한것.
    String URL = "http://3.36.188.116:3000";
    {
        try {
            mSocket = IO.socket(URL);
            Log.d(TAG, "instance initializer: 연결");
        } catch (URISyntaxException e) {
            Log.d(TAG, "instance initializer: e.gemessage : "+e.getMessage());
        }
    }
    /*채팅 리사이클러뷰 관련*/
    RecyclerView room_voice_msg_recyclerview;
    Adapter_Chat adapter_chat;
    LinearLayoutManager linearLayoutManager;
    ArrayList<Chat_Msg_Data> chat_msg_dataArrayList;


    /****음성통신 관련****/
    AudioReceiveThread audioReceiveThread;
    AudioSendThread audioSendThread;
    Enter enter;
    Exit exit;
    public int voicebytelength = 512;
    DatagramSocket udp_socket; //음성통신전에 중개서버와 통신, p2p 홀펀칭할때 사용
    DatagramSocket voice_socket; //음성통신을 할때 사용하는 socket
    AudioRecord mAudioRecord;
    private int mAudioSource = MediaRecorder.AudioSource.VOICE_COMMUNICATION;
    private int mSampleRate = 44100;
    private int mChannelCount = AudioFormat.CHANNEL_IN_STEREO;
    private int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT; //PCM 데이터를 받기 때문에
    private int mBufferSize = AudioTrack.getMinBufferSize(mSampleRate, mChannelCount, mAudioFormat);
    //음성 채팅방에 참여한사람들의 목소리를 들을 audiotrack을 담고있는 arraylist
    ArrayList<OtherUserIP_Voice_Data> otherUserIP_voices = new ArrayList<>();

    ArrayList<OtherUserIP_Data> otherUserPrivateIPArrayList = new ArrayList<>();
    ArrayList<OtherUserIP_Data> otherUserPublicIPArrayList = new ArrayList<>();

    //사설 ip주소 가져오기
    Private_IPAddress private_ipAddress;

    //음소거 및 마이크 끄지
    ImageView voice_microphone;
    boolean voice_microphone_on_off = true;
    ImageView vocie_speaker;
    boolean vocie_speaker_on_off = true;

    /****사용자 목록 보여줄때 사용할 리사이클러뷰 관련****/
    Adapter_voice_participant_list adapter_voice_participant_list;
    ArrayList<VoiceChatRoom_Participant_List_Data> participant_list_arrayList;
    //LinearlayoutManager는 위에꺼 사용
    //오답노트 한번 사용한 linearlayoutmanager는 사용 못함
    LinearLayoutManager participant_layoutManger;
    RecyclerView participant_list_recyclerView;

    /****디바이스 사이즈 관련련***/
    DisplayMetrics displayMetrics = new DisplayMetrics();	// 디바이스 사이즈 받기


    //test할 때
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        /*툴바 조작*/
//        ActionBar ab = getSupportActionBar();
//        ab.setTitle("음성채팅방");
//        ab.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_room_voice);
        mContext=this;
        init_socket();
        initFindViewById();

        //우선 들어오면 바로 1.socket연결 2.해당하는 room번호(방 고유번호) 연결
        //연결할때 connection 해주고 바로 joinroom메소드에 num,와 user_nickname 보내주기
        //챗할때는 message_chat 메소드에 방번호,user_nickname,message 보내주기
        Log.d(TAG, "onCreate: 방입장 room_num : "+room_num);
        room_voice_input_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*msg가 빈값이 아닐때 메소드 작동*/
                if(!room_voice_input.getText().toString().equals("")){
                    message_chat_emit();
                    room_voice_input.setText("");
                }
            }
        });
        /*채팅 리사이클러뷰 관련*/
        adapter_chat = new Adapter_Chat(this,chat_msg_dataArrayList);
        room_voice_msg_recyclerview.setLayoutManager(linearLayoutManager);
        room_voice_msg_recyclerview.setAdapter(adapter_chat);
        //init_socket();
        joinroom_socket_emit();

        /*마이크 on/off*/
        voice_microphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(voice_microphone_on_off==true){
                    voice_microphone.setBackground(ContextCompat.getDrawable(Activity_Room_Voice.this,R.drawable.microphone_mute));
                    voice_microphone_on_off = false;
                    mAudioRecord.stop();
                    audioSendThread.setVoice_talk(false);
                    //마이크 끌때 서버에게 껐다고 알리기
                    mike_or_speaker_onoff_emit("OFF","mike");
                }
                else if(voice_microphone_on_off==false){
                    voice_microphone.setBackground(ContextCompat.getDrawable(Activity_Room_Voice.this,R.drawable.microphone));
                    voice_microphone_on_off=true;
                    mAudioRecord.startRecording();
                    audioSendThread.setVoice_talk(true);
                    //마이크 끌때 서버에게 켰다고 알리기
                    mike_or_speaker_onoff_emit("ON","mike");
                }
            }
        });

        /*소리듣기 on/off*/
        vocie_speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vocie_speaker_on_off==true){
                    audioReceiveThread.setVoice_listen(false);
                    vocie_speaker.setBackground(ContextCompat.getDrawable(Activity_Room_Voice.this,R.drawable.speaker_off));
                    vocie_speaker_on_off=false;
                    //스피커 끌때 서버에게 껐다고 알리기
                    mike_or_speaker_onoff_emit("OFF","speaker");
                }
                else if(vocie_speaker_on_off==false){
                    audioReceiveThread.setVoice_listen(true);
                    vocie_speaker.setBackground(ContextCompat.getDrawable(Activity_Room_Voice.this,R.drawable.speaker_on));
                    vocie_speaker_on_off=true;
                    //스피커 끌때 서버에게 켰다고 알리기
                    mike_or_speaker_onoff_emit("ON","speaker");
                }
            }
        });

        /*채팅창 화면 클릭 isPageOpen가 true일때는 아무반응 없고 false일때 클릭시 다시 원상복귀*/
        //false일때는 툴바 이미지 버튼이 안먹힌다 그래서 화면클릭 할 때로 받아온 것이다.
        room_chat_LinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isPageOpen){
                    //레이아웃 위치 되돌리고 애니메이션 적용 시키기 ->그래야 자연스러워 보인다.
                    int Dwidth = displayMetrics.widthPixels;
                    room_chat_LinearLayout.setX(0);
                    room_chat_LinearLayout.startAnimation(translateRightAnim);
                    isPageOpen = true;
                    //채팅 보내기 버튼 색돌아오게하기
                    Timer timer;
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    room_chat_LinearLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                    room_voice_input_btn.setBackgroundColor(Color.parseColor("#dbddfc"));
                                    //액션바 색상 원래대로
                                    actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#dbddfc")));
                                    //버튼 활성화
                                    voice_chat_handler.sendEmptyMessage(HandlerType_Code.HandlerType.CHAT_BTN_OPEN);
                                }
                            });
                        }
                    },500);
                }
            }
        });
    }




    private void initFindViewById(){
        /*볼리 큐 초기화*/
        if(VolleyRequestQhelper.requestQueue==null){
            VolleyRequestQhelper.requestQueue = Volley.newRequestQueue(this);
        }
        /*디바이스 사이즈 관련*/
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);// 디바이스 사이즈 받기
        /*애니메이션*/
        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateLeftAnim_after = AnimationUtils.loadAnimation(this, R.anim.translate_left_after);
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);
        /*툴바*/
        // 추가된 소스, Toolbar를 생성한다.
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기(툴바 맨왼쪽에 생김)

        //참가자 레이아웃
        room_participant_list_LinearLayout = (LinearLayout)findViewById(R.id.room_participant_list_LinearLayout);
        //채팅창 레이아웃
        room_chat_LinearLayout = (LinearLayout)findViewById(R.id.room_chat_LinearLayout);
        /*로그인한 아이디 갖고있는 객체화 해주기*/
        logindata = getLoignDataPref(this,"login_inform");
//        myid = logindata.getUsername(); //닉네임 가져오기
//        email_id = logindata.getUserid(); //이메일 가져오기
        /*방번호 입력하기*/
        intent_room_id = getIntent();
        room_num = intent_room_id.getExtras().getInt("room_num",0);
        /*채팅 입력하는곳,채팅보내는 버튼*/
        room_voice_input = (EditText)findViewById(R.id.room_voice_input);
        room_voice_input_btn = (Button)findViewById(R.id.room_voice_input_btn);
        /*채팅 리사이클러뷰 관련*/
        room_voice_msg_recyclerview = (RecyclerView)findViewById(R.id.room_voice_msg_recyclerview);
        linearLayoutManager = new LinearLayoutManager(this);
        chat_msg_dataArrayList = new ArrayList<>();

        /*음성통신 관련*/
        //음소거 및 마이크 끄지
        voice_microphone = (ImageView)findViewById(R.id.voice_microphone);
        vocie_speaker = (ImageView)findViewById(R.id.vocie_speaker);

        //사설 ip가져오는 클래스
        private_ipAddress = new Private_IPAddress(this);

        //녹음
        mAudioRecord = new AudioRecord(mAudioSource, mSampleRate, mChannelCount, mAudioFormat, mBufferSize);
        mAudioRecord.startRecording();

        try {
            udp_socket = new DatagramSocket();
            voice_socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        audioReceiveThread = new AudioReceiveThread(voice_socket,otherUserIP_voices,Activity_Lobby.userid);
        audioReceiveThread.start();
        audioSendThread = new AudioSendThread(voice_socket,mAudioRecord,otherUserIP_voices);
        audioSendThread.start();
        enter = new Enter(udp_socket,voice_socket,private_ipAddress.checkAvailableConnection(),/*otherUserPrivateIPArrayList,otherUserPublicIPArrayList,*/String.valueOf(room_num),Activity_Lobby.userid);
        enter.start();

        /*사용자 목록 리사이클러뷰 관련*/
        participant_list_recyclerView = (RecyclerView)findViewById(R.id.room_participant_list_recyclerView);
        participant_list_arrayList = new ArrayList<>();
        participant_layoutManger = new LinearLayoutManager(this);
        adapter_voice_participant_list = new Adapter_voice_participant_list(participant_list_arrayList,Activity_Lobby.usernickname,mSocket,this,room_num);
        participant_list_recyclerView.setLayoutManager(participant_layoutManger);
        participant_list_recyclerView.setAdapter(adapter_voice_participant_list);

        /*핸들러*/
        voice_chat_handler = new Voice_Chat_Handler(room_voice_input_btn,adapter_voice_participant_list);
        //test할곳


    }

    /****우선 destroy할때 방나가는것으로 해주고 추후에 방나가기 버튼을 눌러야 방에서 나가지는 것으로 하고 뒤로가기를 누르면 백그라운드로 실행되도록하자****/
    /*destory는 추후에 없애줄것이다.*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        String IP_ADDRESS = "3.36.188.116/opentalk";
        HttpConnection_room_exit httpConnection_room_exit;
        httpConnection_room_exit = new HttpConnection_room_exit(this);
        httpConnection_room_exit.execute("http://"+IP_ADDRESS+"/room_exit.php",String.valueOf(room_num));
        //강퇴당한게 아니라 내가 나간걸때 동작
        if(kick_check){
            leave_room_emit();
        }
        mSocket.disconnect();

        /*음성 통신 관련 이부분도 추후에 백그라운드로 실행될수 있게 해야한다.*/
        //말하는 스레드 종료 시키기
        audioSendThread.setVoice_talk(false);
        audioSendThread.setVoice_communication(false);
        //듣는 스레드 종료 시키기
        audioReceiveThread.setVoice_listen(false);
        audioReceiveThread.setVoice_communication(false);
        //방에 입장한 사람들의 정보를 받는 스레드 종료 시키기
        enter.setEnterONOFF(false);
        //녹음기 종료
        mAudioRecord.stop();
        //서버에 종료했다고 알려주는 스레드 스타트트
        exit = new Exit(udp_socket,voice_socket,String.valueOf(room_num),Activity_Lobby.userid,otherUserIP_voices);
        exit.start();
    }

    /****수업 끝나고 클래스화 시켜주기****/
    /*초기에 socket 클라이언트와 서버 연결해주기*/
    private void init_socket(){
        /*소켓 연결시켜주기*/
        mSocket.connect(); //이게 on('connect' 에 연결해준다.)
        Log.d(TAG, "init_socket: 연결"+mSocket);
    }
    /*채팅방 들어갈때 클라이언트->서버*/
    private void joinroom_socket_emit(){
        if(mSocket!=null) {
            Log.d(TAG, "joinroom_socket_emit:  연결");
            //서버에 보내줄 data(서버에 보낼 데이터) json객체형태로 보내주기
            JSONObject data = new JSONObject();
            try {
                /*그리고 서버에 보내줄 data 담아주기*/
                data.put("num", room_num); // value : room_num(방고유번호)
                data.put("user_nickname", Activity_Lobby.usernickname); // value : myid(닉네임)
                data.put("mike_onoff",voice_microphone_on_off); //현재 마이크 상태 보내기
                data.put("speaker_onoff",vocie_speaker_on_off); //현재 스피커 상태 보내기
                Log.d(TAG, "emit_nickname: "+Activity_Lobby.usernickname);
                /*방에 입장하면(나 혹은 다른사람들) 입장했다는 알림 받을 메소드,채팅 받을 메소드(중요 joinroom emit하기전에 해줘야함)*/
                joinroom_user_notice(); //입장한 유저 정보 받기
                room_participant_tome(); //채팅방 유저리스트 내가 접속 할때 서버에서 데이터들 받기
                room_participant_toother(); //다른 유저가 접속 할 떄 서버에서 데이터들 받기
                message_chat_on(); //채팅 받기
                leave_room_on(); //나간사람 알림 받기
                mike_or_speaker_onoff_on(); //마이크 or 스피커 onoff 알림 받기
                user_kick_on(); //유저 강퇴당할때 받는 메소드
                /*메소드 joinroom에 data를 담아서 서버에 메소드 보내주기*/
                mSocket.emit("joinroom", data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void joinroom_user_notice(){
       mSocket.on("joinroom_user_notice", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //서버에서 [{}]형식으로 오니까 우선 JSONArray로 파싱해주고 JSONObject로 파싱해주면된당.
                        Log.d(TAG, "run: joinroom_notice_me 여기로 입장하네?");
                        try {
                            //서버에서 받은 값 jsonobject 형태로 받아오기
                            JSONObject data = (JSONObject)args[0];
                            /*채팅 리사이클러뷰 데이터 뿌려주기*/
                            //서버에서 받은 데이터 객체에 담기(userid->null,message->누가 입장한지,어떤 뷰타입인지)
                            Chat_Msg_Data chat_msg_data = new Chat_Msg_Data(null,data.getString("user_nickname")+" 님이 입장했습니다.", ViewType_Code.ViewType.CENTER_CONTENT,null);
                            Log.d(TAG, "on_userid: "+data.getString("user_nickname"));
                            //데이터 어레이리스트에 추가
                            chat_msg_dataArrayList.add(chat_msg_data);
                            adapter_chat.notifyDataSetChanged();
                        } catch(Exception e) {
                            Log.d(TAG, "joinroom_notice_me: error");
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /*
     * 채팅방에 들어갈때(알림 받기) 서버->클라이언트
     * !입장한 사람이 받는 정보! ->방에 있는 사람들의 데이터를 가져온다.(나의 데이터를 포함해서.)
     *
     * 이 때, 데이터를 다 받아오고 volley로 서버와 통신해서 이미지 받아오기.
     * */
    private void room_participant_tome(){
        mSocket.on("room_participant_tome", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //서버에서 [{}]의 json형태로 보내온다
                            JSONArray jsonArray = (JSONArray)args[0];
                            int jsonArray_length = jsonArray.length();
                            for (int i=0; i<jsonArray_length; i++){
                                JSONObject jsonObject_me = jsonArray.getJSONObject(i);
                                String nickname = jsonObject_me.getString("user_nickname");
                                String owner = jsonObject_me.getString("owner");
                                boolean mike_onoff = jsonObject_me.getBoolean("mike_onoff");
                                boolean speaker_onoff = jsonObject_me.getBoolean("speaker_onoff");
                                Log.d(TAG, "room_participant_tome 데이터 확인 : nickname : "+nickname);
                                Log.d(TAG, "room_participant_tome 데이터 확인: owner : "+owner);
                                //여기에 참여자 리사이클러뷰에 적용 시킬 arraylist add해주기
                                VoiceChatRoom_Participant_List_Data participant_list_data = new VoiceChatRoom_Participant_List_Data(nickname,owner,"없음",mike_onoff,speaker_onoff);
                                participant_list_arrayList.add(participant_list_data);
                            }
                            adapter_voice_participant_list.notifyDataSetChanged();
                            //add 다해주고나서 notifyDataSetChanged해주고 이미지 받아오는 volley통신으로 이미지 받아오기
                            //해당 닉네임들은 어떻게 한번에 서버에 보낼까? 아니면 한번씩 보낼까? arraylist의 size만큼
                            String serverurl = "http://3.36.188.116/opentalk/profile_img_load_nickname.php";
                            int array_size = participant_list_arrayList.size();
                            for (int i=0; i<array_size; i++){
                                String nickname_participant_tome = participant_list_arrayList.get(i).getNickname();
                                VolleyprofileIMG_Participant_List volleyprofileIMG_participant_list = new VolleyprofileIMG_Participant_List(participant_list_arrayList,voice_chat_handler,i,nickname_participant_tome);
                                volleyprofileIMG_participant_list.profile_upload(serverurl);
                            }

//                            //리사이클러뷰 아이템 리셋 해주는 핸들러
//                            voice_chat_handler.sendEmptyMessage(HandlerType_Code.HandlerType.ADAPTER_VOICE_PARICIPANT_LIST_NOTIFYDATASETCHANGED);
//                            Log.d(TAG, "onResponse: 핸들러 작동~!~!!");

                        } catch(Exception e) {
                            Log.d(TAG, "room_participant_tome: error.message : "+e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    /*
     * 채팅방에 들어가 있을때 (알림 받기) 서버->클라이언트
     * !방에 있는 사람들이 받는 정보!  ->  입장한 사람의 정보를 받는다.(nickname,owner)
     *
     * 여기서는 중요한게 서버에서 정보를 받을 때, nickname이 나의 nickname이 아닐때만 받게 예외처리해줘야 한다.
     * 마지막으로 나의 닉네임이 아니라면, 해당 유저의 nickname과 owner정보를 다 받고 arraylist에 배치를 해주고 volley통신을 이용해 해당 유저의 img를 받아오자.
     * */
    private void room_participant_toother(){
        mSocket.on("room_participant_toother", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: 데이터 어디로 들어오는지 확인");
                        try {
                            JSONObject jsonObject = (JSONObject)args[0];
                            String nickname = jsonObject.getString("user_nickname");
                            String owner = jsonObject.getString("owner");
                            boolean mike_onoff = jsonObject.getBoolean("mike_onoff");
                            boolean speaker_onoff = jsonObject.getBoolean("speaker_onoff");
                            Log.d(TAG, "room_participant_toother 데이터 확인 : nickname : "+nickname);
                            Log.d(TAG, "room_participant_toother 데이터 확인: owner : "+owner);
                            //여기에 참여자 리사이클러뷰에 적용 시킬 arraylist add해주기
                            VoiceChatRoom_Participant_List_Data participant_list_data = new VoiceChatRoom_Participant_List_Data(nickname,owner,"없음",mike_onoff,speaker_onoff);
                            int position = participant_list_arrayList.size(); //인덱스는 0부터 시작인데 -1안해주는 이유는 밑에 바로 add가되서 값이 같아진다.
                            participant_list_arrayList.add(participant_list_data);
                            adapter_voice_participant_list.notifyDataSetChanged();

                            //이미지 받아오는 volley 통신해주기 (해당 nickname으로) ->한번만 하면된다.
                            String serverurl = "http://3.36.188.116/opentalk/profile_img_load_nickname.php";
                            VolleyprofileIMG_Participant_List volleyprofileIMG_participant_list = new VolleyprofileIMG_Participant_List(participant_list_arrayList,voice_chat_handler,position,nickname);
                            volleyprofileIMG_participant_list.profile_upload(serverurl);

                        } catch(Exception e) {
                            Log.d(TAG, "room_participant_toother: error.message : "+e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void leave_room_emit(){
        if(mSocket!=null) {
            //서버에 보내줄 data(서버에 보낼 데이터) json객체형태로 보내주기
            JSONObject data = new JSONObject();
            try {
                /*그리고 서버에 보내줄 data 담아주기*/
                data.put("num", room_num); // value : room_num(방고유번호)
                data.put("user_nickname", Activity_Lobby.usernickname); // value : userid(이메일) ->추후에 닉네임으로 변경할 수 있다.
                mSocket.emit("leaveroom", data);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void leave_room_on(){
        mSocket.on("leaveroom_notice", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //서버에서 받은 값 json객체 형태로 받아오기
                            JSONObject data = (JSONObject)args[0];
                            String userNickName = data.getString("user_nickname");
                            String after_owner_nickname = data.getString("owner_nickname");
                            /*채팅 리사이클러뷰 데이터 뿌려주기*/
                            //서버에서 받은 데이터 객체에 담기(userid->null,message->누가 입장한지,어떤 뷰타입인지)
                            Chat_Msg_Data chat_msg_data = new Chat_Msg_Data(null,userNickName+" 님이 나가셨습니다.", ViewType_Code.ViewType.CENTER_CONTENT,null);
                            int user_list_arraylist_size = participant_list_arrayList.size();


                            //데이터 어레이리스트에 추가
                            chat_msg_dataArrayList.add(chat_msg_data);
                            adapter_chat.notifyDataSetChanged();
                            //채팅방 사용자 목록에서 제거

                            for (int i=0; i<user_list_arraylist_size; i++){
                                //해당 유저 찾고
                                if(participant_list_arrayList.get(i).getNickname().equals(userNickName)){
                                    participant_list_arrayList.remove(i);
                                    break;
                                }
                            }
                            //방장 변경
                            for (int i=0; i<user_list_arraylist_size; i++){
                                //변경된 방장의 이름 찾기
                                if(participant_list_arrayList.get(i).getNickname().equals(after_owner_nickname)){
                                    participant_list_arrayList.get(i).setOwner("방장");
                                    break;
                                }
                            }
                            adapter_voice_participant_list.notifyDataSetChanged();

                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /*채팅 보낼때(입장한 방에) 클라이언트->서버*/
    private void message_chat_emit(){
        if(mSocket!=null) {
            //서버에 보내줄 data(서버에 보낼 데이터) json객체형태로 보내주기
            JSONObject data = new JSONObject();
            try {
//                /*들어온 방,생성한 방번호를 갖고있는 key가져오기*/
//                Intent intent = getIntent();
//                int room_num = intent.getExtras().getInt("room_num",0);

                /*그리고 서버에 보내줄 data 담아주기*/
                String message = room_voice_input.getText().toString(); // 보낼 채팅 담기
                data.put("num", room_num); //채팅 보낼 방번호
                data.put("user_nickname", Activity_Lobby.usernickname); //채팅 작성한 유저
                data.put("message", message); //보낼 채팅 내용
                /*서버에 방번호,유저,채팅내용 보내기*/
                mSocket.emit("message_chat_client_emit",data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /*채팅 받을때(입장한 방에서) 서버->클라이언트*/
    private void message_chat_on(){
        mSocket.on("message_chat_client_on", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                Log.d(TAG, "message_chat_on 0");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d(TAG, "message_chat_on 1");
                            /*서버에서 보낸 데이터 json 객체형태로 받기*/
                            JSONObject data = (JSONObject)args[0];
                            String on_msg = data.getString("message"); //누군가 작성한 채팅 내용
                            String on_userid = data.getString("user_nickname"); //채팅을 작성한 사람
                            int num = data.getInt("num"); //채팅 받은 방번호
                            Chat_Msg_Data chat_msg_data;
                            if(on_userid.equals(Activity_Lobby.usernickname)){
                                //서버에서 받은 데이터 객체에 담기(userid->null,message->누가 입장한지,어떤 뷰타입인지)
                                chat_msg_data = new Chat_Msg_Data(on_userid,on_msg, ViewType_Code.ViewType.RIGHT_CONTENT,null);
                            }
                            else {
                                //서버에서 받은 데이터 객체에 담기(userid->null,message->누가 입장한지,어떤 뷰타입인지)
                                chat_msg_data = new Chat_Msg_Data(on_userid,on_msg,ViewType_Code.ViewType.LEFT_CONTENT,null);
                            }
                            //데이터 어레이리스트에 추가
                            chat_msg_dataArrayList.add(chat_msg_data);
                            adapter_chat.notifyDataSetChanged();
                            room_voice_msg_recyclerview.scrollToPosition(adapter_chat.getItemCount() - 1);

//                            Chat_Msg_Data chat_msg_data = new Chat_Msg_Data(on_msg,)
//                            text.setText(on_msg);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /*mikeonoff할 때 서버에게 알리기 ->서버에서는 브로드캐스트 전송해야함함 ->아니구나 퍼블릭 전송해야하네? 내꺼도 변경해줘야하잖아.*/
   private void mike_or_speaker_onoff_emit(String mike_onoff,String mike_or_speaker){
        if(mSocket!=null) {
            //서버에 보내줄 data(서버에 보낼 데이터) json객체형태로 보내주기
            JSONObject data = new JSONObject();
            try {
                /*그리고 서버에 보내줄 data 담아주기*/
                data.put("num", room_num); // value : room_num(방고유번호)
                data.put("mike_or_speaker",mike_or_speaker);
                data.put("nickname", Activity_Lobby.usernickname); // value : myid(닉네임)
                data.put("onoff", mike_onoff); // value : Activity_Lobby.usernickname(닉네임)
                data.put("mike_onoff",voice_microphone_on_off); //현재 마이크 상태 보내기
                data.put("speaker_onoff",vocie_speaker_on_off); //현재 스피커 상태 보내기

                /*메소드 joinroom에 data를 담아서 서버에 메소드 보내주기*/
                mSocket.emit("mike_or_speaker_onoff", data);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast
                        .LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private void mike_or_speaker_onoff_on(){
        mSocket.on("mike_or_speaker_onoff", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    //서버에서 받은 값 json객체 형태로 받아오기
                    JSONObject data = (JSONObject)args[0];
                    String userNickName = data.getString("nickname");
                    String onoff = data.getString("onoff");
                    String mike_or_speaker = data.getString("mike_or_speaker");
                    int array_size = participant_list_arrayList.size();
                    for (int i=0; i<array_size; i++){
                        //마이크 끈 사람의 닉네임의 포지션 찾기
                        if(participant_list_arrayList.get(i).getNickname().equals(userNickName)){
                            //쓸대없이 onoff에다가 겁나서 boolean값을 안넣고 string으로 처리하니까 if문을 하나더만들어서 괜히 성능 안좋게 했다.
                            //추후에는 onoff를 그냥 true,false로 받고 종류만 mike인지 speaker인지 알아놔서
                            //if문은 mike,speaker하나만 해주고 바로 그냥 setMike_onoff(onoff)만 해줄 수 있게 해주자.
                            if(mike_or_speaker.equals("mike")){
                                //마이크 ON 알림
                                if(onoff.equals("ON")){
                                    Log.d(TAG, userNickName+"님 께서 마이크 ON : ");
                                    participant_list_arrayList.get(i).setMike_onoff(true);
                                    voice_chat_handler.sendEmptyMessage(HandlerType_Code.HandlerType.ADAPTER_VOICE_PARICIPANT_LIST_NOTIFYDATASETCHANGED);
                                }
                                //마이크 OFF 알림
                                else {
                                    Log.d(TAG, userNickName+"님 께서 마이크OFF : ");
                                    participant_list_arrayList.get(i).setMike_onoff(false);
                                    voice_chat_handler.sendEmptyMessage(HandlerType_Code.HandlerType.ADAPTER_VOICE_PARICIPANT_LIST_NOTIFYDATASETCHANGED);
                                }
                            }

                            else if(mike_or_speaker.equals("speaker")){
                                //스피커 ON 알림
                                if(onoff.equals("ON")){
                                    Log.d(TAG, userNickName+"님 께서 마이크 ON : ");
                                    participant_list_arrayList.get(i).setSpeaker_onoff(true);
                                    voice_chat_handler.sendEmptyMessage(HandlerType_Code.HandlerType.ADAPTER_VOICE_PARICIPANT_LIST_NOTIFYDATASETCHANGED);
                                }
                                //스피커 OFF 알림
                                else {
                                    Log.d(TAG, userNickName+"님 께서 마이크OFF : ");
                                    participant_list_arrayList.get(i).setSpeaker_onoff(false);
                                    voice_chat_handler.sendEmptyMessage(HandlerType_Code.HandlerType.ADAPTER_VOICE_PARICIPANT_LIST_NOTIFYDATASETCHANGED);
                                }
                            }
                            //알맞게 했으니 종료
                            break;
                        }
                    }

                } catch(Exception e) {
                    Log.d(TAG, "call: 마이크 ERROR-MESSAGE : "+e.getMessage());
                }
            }
        });
    }

    private void user_kick_on(){
        mSocket.on("user_kick", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: user_kick");
                            try {
                                //서버에서 받은 값 json객체 형태로 받아오기
                                JSONObject data = (JSONObject)args[0];
                                String userNickName = data.getString("user_nickname");
                                //만약 강퇴당한게 자신이라면
                                if (userNickName.equals(Activity_Lobby.usernickname)){
                                    //강퇴된 유저의 소켓으로 다시 한번 서버통신해서 sockets.leave(data.num)해줘야한다.
                                    JSONObject data_kick_user = new JSONObject();
                                    data_kick_user.put("num",room_num);
                                    mSocket.emit("user_kick_leave",data_kick_user);
                                    kick_check = false;
                                    Toast.makeText(Activity_Room_Voice.this,"방에서 강제로 퇴장당하셨습니다.",Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                else {
                                    //처리해줄것 chat arraylist에 강퇴됬다는
                                    //서버에서 받은 데이터 객체에 담기(userid->null,message->누가 입장한지,어떤 뷰타입인지)
                                    Chat_Msg_Data chat_msg_data = new Chat_Msg_Data(null,userNickName+" 님이 방에서 쫓겨났습니다.", ViewType_Code.ViewType.CENTER_CONTENT,null);
                                    //채팅 데이터 어레이리스트에 추가
                                    chat_msg_dataArrayList.add(chat_msg_data);
                                    adapter_chat.notifyDataSetChanged();
                                    //채팅방 사용자 목록에서 제거
                                    int user_list_arraylist_size = participant_list_arrayList.size();
                                    for (int i=0; i<user_list_arraylist_size; i++){
                                        //해당 유저 찾고
                                        if(participant_list_arrayList.get(i).getNickname().equals(userNickName)){
                                            participant_list_arrayList.remove(i);
                                            adapter_voice_participant_list.notifyDataSetChanged();
                                            break;
                                        }
                                    }
                                }
                            } catch(Exception e) {
                                Log.d(TAG, "강퇴 socket_on ERROR-MESSAGE : "+e.getMessage());
                            }
                        }
                    });
            }
        });
    }


    /*액션툴바*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_room_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.participant_btn:
                //클릭시 채팅레이아웃 왼쪽으로 집어넣어서 70p남기게 하기
                if(isPageOpen){
                    //처음엔 애니메이션으로 왼쪽으로 이동하는걸 보여주고
                    room_chat_LinearLayout.startAnimation(translateLeftAnim);

                    Timer timer;
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    room_chat_LinearLayout.setBackgroundColor(Color.parseColor("#4a4b4f"));
                                    room_voice_input_btn.setBackgroundColor(Color.parseColor("#818285"));
                                    //액션바 색상 변경
                                    actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#363739")));
                                    //버튼 비활성화
                                    voice_chat_handler.sendEmptyMessage(HandlerType_Code.HandlerType.CHAT_BTN_CLOSE);

                                    //레이아웃 실제로 위치 변경해줘야한다 -> 안그러면 실제 터치할 경우에는 적용이 되지 않는다.
                                    int Dwidth = displayMetrics.widthPixels;
                                    room_chat_LinearLayout.setX((float) -(Dwidth/1.13));
                                    //실제위치 맞춰주면 빼꼼화면이 보여지지 않고 다 사라지기 때문에 위의 setX위치맞춰서 다시 보이게하기
                                    room_chat_LinearLayout.startAnimation(translateLeftAnim_after);
                                }
                            });
                        }
                    },500);


                    isPageOpen = false;
                    Log.d(TAG, "onOptionsItemSelected: ture다 아러 ㅏ");
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
