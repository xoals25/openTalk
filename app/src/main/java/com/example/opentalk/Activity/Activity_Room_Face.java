package com.example.opentalk.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.Volley;
import com.example.opentalk.Adapter.Adapter_Chat;
import com.example.opentalk.Adapter.Adapter_Invite_Friend;
import com.example.opentalk.Adapter.Adapter_face_participant_list;
import com.example.opentalk.BitmapConverter;
import com.example.opentalk.Code.ViewType_Code;
import com.example.opentalk.Data.Chat_Msg_Data;
import com.example.opentalk.Data.FaceChatRoom_Participant_List_Data;
import com.example.opentalk.Data.Invite_Friend;
import com.example.opentalk.Data.Logindata;
import com.example.opentalk.Handler.Face_Chat_Handler;
import com.example.opentalk.Http.HttpConnection_room_exit;
import com.example.opentalk.Http.VolleyprofileIMG_Participant_List_Face;
import com.example.opentalk.R;
import com.example.opentalk.Retrofit.ApiClient;
import com.example.opentalk.Retrofit.InviteFriendList.InviteFriendList;
import com.example.opentalk.Retrofit.InviteFriendList.InviteFriendListData;
import com.example.opentalk.WebRTC.SignalingClient_WebRTC;
import com.example.opentalk.VolleyRequestQhelper;
import com.example.opentalk.WebRTC.Adapter.AdapterVideo;
import com.example.opentalk.WebRTC.Adapter.PeerConnectionAdapter;
import com.example.opentalk.WebRTC.Adapter.SdpAdapter;
import com.example.opentalk.WebRTC.Data.DataVideoTrack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.opentalk.SharedPreference.PreferenceManager_member.getLoignDataPref;

/*
*  2021 / 01 / 13 에 만든 Class
*  화상채팅,텍스트 채팅을 할 수 있는 클래스이다.
*
*  2021 01 29
*  수정내용 초대하기기능 추가
*
* */

public class Activity_Room_Face extends AppCompatActivity implements SignalingClient_WebRTC.Callback {
    ConstraintLayout test;
    String TAG ="Activity_Room_Face";
    Context context;
    InputMethodManager imm;//키보드 입력창
    /*방관련 정보*/
    int room_name;
    String room_title;
    /*디바이스 화면 크기 가져오기 및 x,y값*/
    DisplayMetrics dm;
    int devicewidth;
    int deviceheight;
    DisplayMetrics displayMetrics = new DisplayMetrics();	// 디바이스 사이즈 받기

    /*로그인한 아이디 담고있는 데이터*/
    Logindata logindata;
//    String myNickname;

    /*툴바 관련*/
    Toolbar myToolbar;
    ActionBar actionBar;

    /*핸들러*/
    Face_Chat_Handler face_chat_handler;
    /****툴바 클릭시 생기는 화면 및 애니메이션 관련*****/
    boolean isPageOpen = true;
    LinearLayout room_participant_list_LinearLayout;//참가자 레이아웃
    FrameLayout face_main_view;//위치 이동 할 때 사용 얘도 색상 변경해줘야한다.
    LinearLayout recyclerview_parentview;//색상 변경 할 때 사용
    Animation translateLeftAnim; //슬라이드 왼쪽으로 이동(집어넣기) 애니메이션
    Animation translateLeftAnim_after; //왼쪽이동 after버전
    Animation translateRightAnim; //슬라이드 오른쪽으로 이동(나오게 하기) 애니메이션

    /****사용자 목록 관련****/
    Adapter_face_participant_list adapter_face_participant_list;
    ArrayList<FaceChatRoom_Participant_List_Data> participant_list_arrayList;
    LinearLayoutManager participant_layoutManger;
    RecyclerView participant_list_recyclerView;
    boolean kick_check = true; // true면 내가 나간것, false면 강퇴당한것.
    /*초대 관련*/
    LinearLayout room_invite_btn;//초대 버튼
    ArrayList<Invite_Friend> friend_list;

    /****채팅 온오프 버튼 및 애니메이션 관련 및 텍스트 채팅 관련****/
    Button chat_openBtn;
    LinearLayout chat_parentview; //채팅이 올라오는 뷰, 채팅 온오프버튼 및 애니메이션 관련에 있음
    FrameLayout chat_framView; //
    boolean chatonoffCheck=true;
    Animation translateBottomMiddleAnim; //채팅 화면(chat_parentview)을 밑에서 가운데로
    Animation translateMiddleBottomAnim; //채팅 화면(chat_parentview)을 가운데에서 밑으로
    Button room_face_input_btn; //채팅입력버튼
    EditText room_face_input; //채팅입력창
    /*채팅 뿌려주는 화면 관련*/
    RecyclerView room_face_msg_recyclerview;
    Adapter_Chat adapter_chat;
    LinearLayoutManager linearLayoutManager;
    ArrayList<Chat_Msg_Data> chat_msg_dataArrayList;
    ImageView room_face_camera_btn;
    int PICK_IMAGE = 0;
    int CAPTURE_IMAGE = 1;
    String profile_img_bitmap_toString; //이미지 byte변환
    byte[] profile_img_bitmap_toByte; //이미지 byte변환

    /****WebRTC 관련,화상채팅 관련****/
    EglBase.Context eglBaseContext;
    PeerConnectionFactory peerConnectionFactory;
    MediaStream mediaStream;
    List<PeerConnection.IceServer> iceServers;
    HashMap<String, PeerConnection> peerConnectionMap; //(key : socketId(나말고 다른참가자), value : PeerConnection)
    HashMap<String, ArrayList<IceCandidate>> icecandidateMap; //(key : socketId(나말고 다른참가자), value : PeerConnection) ->상대방이 나가면 PeerConnection의 IceCandidate제거하기 위함
    ArrayList<IceCandidate> iceCandidateList; //icecandidateMap value에 넣어줄 어레이리스트
    PeerConnectionFactory.Options options;
    VideoTrack videoTrack;
    VideoCapturer videoCapturer;
    VideoSource videoSource;
    AudioSource audioSource;
    AudioTrack audioTrack;
    SignalingClient_WebRTC signalingClient_webRTC; //socket.io 객체 담겨있는 클래스
    /*webrtc 화면 뿌려주는 것들*/
    RecyclerView recyclerview_FaceView;
    ArrayList<DataVideoTrack> dataVideoTrackArrayList = new ArrayList<>();
    AdapterVideo adapterVideo;
    GridLayoutManager gridLayoutManager;
    boolean frontANDback =true; //현재 true이면 front인상태 //false이면 back인상태
    int faceScreen_x= 2;//스크린 크기 결정하는 x방향 배수 (클수록 화면 가로가 작아진다.)
    float faceScreen_y= (float)2.7; //스크린 크기 결정하는 y방향 배수 (클수록 화면 세로가 작아진다.)

    int peopleNum = 0;
    int nowScreenSetNum=1;//1번일때는 기본 x=2. y=3; / 2번일때는 2,3.4 / 3번일때는 3,4 / 4번일때는 3,4.5


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_face);
        init();
        /*채팅창 온오프*/
        chat_openBtn.setOnClickListener(new DrawButtonClickListener());
        /*메인 화면 클릭 isPageOpen가 true일때는 아무반응 없고 false일때 클릭시 다시 왼쪽으로 들어간 화면 오른쪽으로 꺼내오기*/
        face_main_view.setOnClickListener(new MainScreenOpenClickListener());
        recyclerview_parentview.setOnClickListener(new MainScreenOpenClickListener());
        recyclerview_FaceView.setOnClickListener(new MainScreenOpenClickListener());

        /*send message method*/
        room_face_input_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signalingClient_webRTC.sendMesageChat(room_face_input.getText().toString());
                room_face_input.setText("");
            }
        });
        /*send picture method*/
        room_face_camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pictureActionIntent = null;
                pictureActionIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                pictureActionIntent.setType("image/*");
                startActivityForResult(pictureActionIntent, PICK_IMAGE);
            }
        });
        /*초대하기 버튼*/
        room_invite_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteShowAlertDialog();
            }
        });
    }

    /*초기설정*/
    private void init(){
        test = (ConstraintLayout)findViewById(R.id.test);
        context=getApplicationContext();
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE); //키보드 입력창
        /*방관련 정보*/
        Intent intent = getIntent();
        room_name = intent.getExtras().getInt("room_num");
        room_title = intent.getExtras().getString("room_title");
        Log.d(TAG, "init: room_name 확인작업 : "+room_name);
        Log.d(TAG, "init: room_title 확인작업 : "+room_title);
        /*볼리 큐 초기화*/
        if(VolleyRequestQhelper.requestQueue==null){
            VolleyRequestQhelper.requestQueue = Volley.newRequestQueue(this);
        }

        /*디바이스 화면 크기 가져오기 및 x,y값*/
        dm = getApplicationContext().getResources().getDisplayMetrics();
        devicewidth = dm.widthPixels;
        deviceheight = dm.heightPixels;
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);// 디바이스 사이즈 받기

        /*로그인한 정보-닉네임*/
        logindata = getLoignDataPref(this,"login_inform");
//        myNickname = logindata.getUsername();

        /*툴바 관련*/
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        /*툴바 클릭시생기는 화면 및 애니메이션 관련*/
        room_participant_list_LinearLayout = (LinearLayout)findViewById(R.id.room_participant_list_LinearLayout);
        recyclerview_parentview = (LinearLayout)findViewById(R.id.recyclerview_parentview); //색상변경할때 사용
        face_main_view = (FrameLayout)findViewById(R.id.face_main_view); //위치이동할때 사용
        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateLeftAnim_after = AnimationUtils.loadAnimation(this, R.anim.translate_left_after);
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);

        /*WebRTC관련*/
        recyclerview_FaceView = (RecyclerView)findViewById(R.id.recyclerview_FaceView);
        webrtcInit();

        /*사용자 목록 및 사용자 리사이클러뷰 관련*/
        participant_list_recyclerView = (RecyclerView)findViewById(R.id.room_participant_list_recyclerView);
        room_invite_btn = (LinearLayout)findViewById(R.id.room_invite_btn);
        participant_list_arrayList = new ArrayList<>();
        participant_layoutManger = new LinearLayoutManager(this);
        adapter_face_participant_list = new Adapter_face_participant_list(participant_list_arrayList,Activity_Lobby.usernickname,this,room_name,signalingClient_webRTC.socket);
        participant_list_recyclerView.setLayoutManager(participant_layoutManger);
        participant_list_recyclerView.setAdapter(adapter_face_participant_list);


        /*채팅 온오프 버튼 및 애니메이션 관련 및 텍스트 채팅 관련*/
        chat_openBtn = (Button)findViewById(R.id.chat_openBtn);
        chat_parentview = (LinearLayout)findViewById(R.id.chat_parentview);
        translateBottomMiddleAnim = AnimationUtils.loadAnimation(this,R.anim.translate_frombottom_tomiddle);
        translateMiddleBottomAnim = AnimationUtils.loadAnimation(this,R.anim.translate_frommiddle_tobottom);
        chat_framView=(FrameLayout)findViewById(R.id.chat_framView);
        room_face_input = (EditText)findViewById(R.id.room_face_input);
        room_face_input_btn=(Button)findViewById(R.id.room_face_input_btn);
        room_face_camera_btn=(ImageView)findViewById(R.id.room_face_camera_btn);

        /*채팅 리사이클러뷰 관련*/
        room_face_msg_recyclerview = (RecyclerView)findViewById(R.id.room_face_msg_recyclerview);
        linearLayoutManager = new LinearLayoutManager(this);
        chat_msg_dataArrayList = new ArrayList<>();
        adapter_chat = new Adapter_Chat(this,chat_msg_dataArrayList);
        room_face_msg_recyclerview.setLayoutManager(linearLayoutManager);
        room_face_msg_recyclerview.setAdapter(adapter_chat);
        face_chat_handler= new Face_Chat_Handler(adapter_face_participant_list);

    }

    //초대 다이얼로그 뜨는 화면
    private void inviteShowAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_dialog, null);
        builder.setView(view);
        friend_list= new ArrayList<Invite_Friend>();
        final RecyclerView recyclerView_friend_list = (RecyclerView)view.findViewById(R.id.friend_list);
        Adapter_Invite_Friend adapter_invite_friend = new Adapter_Invite_Friend(friend_list,"화상채팅",room_title,room_name);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView_friend_list.setAdapter(adapter_invite_friend);
        recyclerView_friend_list.setLayoutManager(linearLayoutManager);
        /*레트로핏 통신으로 친구 아이디 닉네임 가져오기*/
        InviteFriendList inviteFriendList = ApiClient.getApiClient().create(InviteFriendList.class);
        Call<List<InviteFriendListData>> call = inviteFriendList.postMyid(Activity_Lobby.userid);
        call.enqueue(new Callback<List<InviteFriendListData>>() {
            @Override
            public void onResponse(Call<List<InviteFriendListData>> call, Response<List<InviteFriendListData>> response) {
                if (response.isSuccessful()){
                    List<InviteFriendListData> result = response.body();
                    int resultsize = result.size();
                    if(resultsize!=0){
                        for (int i=0; i<resultsize; i++){
                            String connectCheck = result.get(i).getFriend_connect_check();
                            String friendId = result.get(i).getFriendId();
                            String friendNickname = result.get(i).getFriendNickname();
                            String uniquename = result.get(i).getUniquename();
                            Invite_Friend invite_friend = new Invite_Friend(friendId,friendNickname,uniquename);
                            friend_list.add(invite_friend);
                        }
                        adapter_invite_friend.notifyDataSetChanged();
                        final AlertDialog dialog = builder.create();
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                    }
                }
            }
            @Override
            public void onFailure(Call<List<InviteFriendListData>> call, Throwable t) {
                Log.d(TAG, "onFailure: t : "+t);
            }
        });
    }

    /*WebRTC 초기설정*/
    private void webrtcInit(){
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        peerConnectionMap = new HashMap<>();
        icecandidateMap = new HashMap<>();
        iceServers = new ArrayList<>();
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer());
//        iceServers.add(PeerConnection.IceServer.builder("stun:stun.ourcodeworld.com:5349").createIceServer());
//        iceServers.add(PeerConnection.IceServer.builder("stun:stun.ourcodeworld.com:3478").createIceServer());
//        iceServers.add(PeerConnection.IceServer.builder("turn:turn.ourcodeworld.com:3478").setUsername("xoals").setPassword("xoals").createIceServer());
//        iceServers.add(PeerConnection.IceServer.builder("turn:turn.ourcodeworld.com:5349").setUsername("xoals").setPassword("xoals").createIceServer());
        eglBaseContext = EglBase.create().getEglBaseContext();
        //socket 통신하는 클래스 (데이터 : 방이름,참가자 이름)
        signalingClient_webRTC = new SignalingClient_WebRTC(room_name,Activity_Lobby.usernickname);

        /*리사이클러뷰 관련련*/
        recyclerview_FaceView = (RecyclerView)findViewById(R.id.recyclerview_FaceView);
        gridLayoutManager = new GridLayoutManager(this,2);
        adapterVideo = new AdapterVideo(dataVideoTrackArrayList,peerConnectionMap,eglBaseContext,devicewidth,deviceheight,faceScreen_x,faceScreen_y);
        recyclerview_FaceView.setLayoutManager(gridLayoutManager);
        recyclerview_FaceView.setAdapter(adapterVideo);

        // create PeerConnectionFactory
        PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions
                .builder(this)
                .createInitializationOptions());
        options = new PeerConnectionFactory.Options();
        DefaultVideoEncoderFactory defaultVideoEncoderFactory =
                new DefaultVideoEncoderFactory(eglBaseContext, true, true);
        DefaultVideoDecoderFactory defaultVideoDecoderFactory =
                new DefaultVideoDecoderFactory(eglBaseContext);
        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoEncoderFactory(defaultVideoEncoderFactory)
                .setVideoDecoderFactory(defaultVideoDecoderFactory)
                .createPeerConnectionFactory();

        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBaseContext);
        // create VideoCapturer
        videoCapturer = createCameraCapturer();
        videoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast());
        videoCapturer.initialize(surfaceTextureHelper, getApplicationContext(), videoSource.getCapturerObserver());
        videoCapturer.startCapture(480, 640, 30);
        // create VideoTrack
        videoTrack = peerConnectionFactory.createVideoTrack("100", videoSource);

        /*리사이클러뷰에 사용할 videoTrack 추가 (내 화면)*/
        DataVideoTrack dataVideoTrack = new DataVideoTrack(videoTrack,"me",null);
        dataVideoTrackArrayList.add(dataVideoTrack);
        adapterVideo.notifyDataSetChanged();

        audioSource = peerConnectionFactory.createAudioSource(new MediaConstraints());
        audioTrack = peerConnectionFactory.createAudioTrack("101", audioSource);
        mediaStream = peerConnectionFactory.createLocalMediaStream("mediaStream");
        mediaStream.addTrack(videoTrack);
//        mediaStream.addTrack(audioTrack);
        signalingClient_webRTC.init(this);
    }

    /*화면 변경 해주는 메소드(adatper notify관련은 따로 상황에 맞게 직접해줘야함)*/
    private void screenSetting(int x, float y){
        //1번일때는 기본 x=2. y=3; / 2번일때는 2,3.4 / 3번일때는 3,4 / 4번일때는 3,4.5
        gridLayoutManager = new GridLayoutManager(Activity_Room_Face.this,x);
        faceScreen_x= x;
        faceScreen_y = y;
        adapterVideo = new AdapterVideo(dataVideoTrackArrayList,peerConnectionMap,eglBaseContext,devicewidth,deviceheight,faceScreen_x,faceScreen_y);
        recyclerview_FaceView.setLayoutManager(gridLayoutManager);
        recyclerview_FaceView.setAdapter(adapterVideo);
//        adapterVideo.notifyDataSetChanged();
    }

    /*인원수 늘어 날 때마다 자동으로 화면 변경됨 -> onOfferReceived,onAnswerReceived 사용됨*/
    private void autoScreenSettingPlus(){
        peopleNum+=1;
        runOnUiThread(() -> {
            if(peopleNum==5){
                nowScreenSetNum=2;
                screenSetting(2,(float) 3.7);
                adapterVideo.notifyDataSetChanged();
            }else if(peopleNum==7){
                nowScreenSetNum=3;
                screenSetting(3,(float) 4);
                adapterVideo.notifyDataSetChanged();
            }else if(peopleNum==10){
                nowScreenSetNum=4;
                screenSetting(3,(float) 4.5);
                adapterVideo.notifyDataSetChanged();
            }
            else {
                adapterVideo.notifyItemInserted(dataVideoTrackArrayList.size());
            }
        });
    }

    /*인원수 줄어 들 때마다 자동으로 화면 변경 -> onPeerLeave에서 사용됨*/
    private void autoScreenSettingMinus(){
        peopleNum-=1;
        if(peopleNum==4){
            nowScreenSetNum=1;
            screenSetting(2,(float)3);
            adapterVideo.notifyDataSetChanged();
        }
        else if(peopleNum==6){
            nowScreenSetNum=2;
            screenSetting(2,(float) 3.7);
            adapterVideo.notifyDataSetChanged();
        }else if(peopleNum==9){
            nowScreenSetNum=3;
            screenSetting(3,(float) 4);
            adapterVideo.notifyDataSetChanged();
        }else if(peopleNum==12){
            nowScreenSetNum=4;
            screenSetting(3,(float) 4.5);
            adapterVideo.notifyDataSetChanged();
        }
    }

    /*PeerConnection 가져오기 PeerConnection없으면 초기화해주기*/
    private synchronized PeerConnection getOrCreatePeerConnection(String socketId) {
        PeerConnection peerConnection = peerConnectionMap.get(socketId);
        if(peerConnection != null) {
            return peerConnection;
        }
        peerConnection = peerConnectionFactory.createPeerConnection(iceServers, new PeerConnectionAdapter("PC:" + socketId) {
            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);
                signalingClient_webRTC.sendIceCandidate(iceCandidate,socketId);
            }
            @Override
            public void onAddStream(MediaStream mediaStream) {
                super.onAddStream(mediaStream);
                VideoTrack remoteVideoTrack = mediaStream.videoTracks.get(0);
                /*리사이클러뷰에 사용할 videoTrack 추가 (상대방 화면)*/
                DataVideoTrack dataVideoTrack = new DataVideoTrack(remoteVideoTrack,socketId,null);
                dataVideoTrackArrayList.add(dataVideoTrack);
            }
        });
        Log.d(TAG, "getOrCreatePeerConnection3: 순서확인");
        peerConnection.addStream(mediaStream);
        Log.d(TAG, "getOrCreatePeerConnection4: 순서확인");
        peerConnectionMap.put(socketId, peerConnection);
        Log.d(TAG, "getOrCreatePeerConnection5: 순서확인");
        return peerConnection;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //갤러리
        if(requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK){
            try {
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(data.getData(), filePath, null, null, null);
                cursor.moveToFirst();
                String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
                profile_img_bitmap_toString = BitmapConverter.BitmapToString(
                        BitmapConverter.resize(
                                Activity_Room_Face.this,data.getData(),
                                256,imagePath));
                signalingClient_webRTC.sendImg(profile_img_bitmap_toString);

            }catch (Exception e){
            }
        }
    }

    /*이미지 받을때*/
    @Override
    public void onImgreceive(String username, Bitmap bitmap) {
        if(username.equals(Activity_Lobby.usernickname)){
            Chat_Msg_Data chat_msg_data = new Chat_Msg_Data(Activity_Lobby.usernickname,null, ViewType_Code.ViewType.RIGHT_CONTENT,bitmap);
            chat_msg_dataArrayList.add(chat_msg_data);
            synchronized (chat_msg_dataArrayList){
                runOnUiThread(()->{
                    adapter_chat.notifyItemChanged(chat_msg_dataArrayList.size()-1);
                    room_face_msg_recyclerview.scrollToPosition(adapter_chat.getItemCount() - 1);
                });
            }
        }
        else{
            Chat_Msg_Data chat_msg_data = new Chat_Msg_Data(Activity_Lobby.usernickname,null, ViewType_Code.ViewType.LEFT_CONTENT,bitmap);
            chat_msg_dataArrayList.add(chat_msg_data);
            synchronized (chat_msg_dataArrayList){
                runOnUiThread(()->{
                    adapter_chat.notifyItemChanged(chat_msg_dataArrayList.size()-1);
                    room_face_msg_recyclerview.scrollToPosition(adapter_chat.getItemCount() - 1);
                });
            }
        }
    }

    /*아무도 없는 처음 방에 들어올 때*/
    @Override
    public void onCreateRoom(String username) {
        peopleNum+=1;
        Chat_Msg_Data chat_msg_data = new Chat_Msg_Data(null,username+" 님 환영합니다.", ViewType_Code.ViewType.CENTER_CONTENT,null);
        chat_msg_dataArrayList.add(chat_msg_data);
        synchronized (chat_msg_dataArrayList){
            adapter_chat.notifyItemChanged(chat_msg_dataArrayList.size()-1);
        }
    }

    /*방에 누군가 참여할 때 방 사람들에게 발생하는 메소드*/
    @Override
    public void onPeerJoined(String username,String socketId) {
        PeerConnection peerConnection = getOrCreatePeerConnection(socketId);
        peerConnection.createOffer(new SdpAdapter("createOfferSdp:" + socketId) {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                super.onCreateSuccess(sessionDescription);
                Log.d(TAG, "onPeerJoined onCreateSuccess: 순서확인");
                peerConnection.setLocalDescription(new SdpAdapter("setLocalSdp:" + socketId), sessionDescription);
                signalingClient_webRTC.sendSessionDescription(sessionDescription, socketId);
            }
        }, new MediaConstraints());
        Log.d(TAG, "onPeerJoined3: 순서확인");

        runOnUiThread(() -> {
            /*이걸 PeerConnection peerConnection = getOrCreatePeerConnection(socketId); 보다 앞에 하니까 기존에 있던 사람에게 상대방 데이터가 안온다.*/
            Chat_Msg_Data chat_msg_data = new Chat_Msg_Data(null, username + " 님 환영합니다.", ViewType_Code.ViewType.CENTER_CONTENT,null);
            chat_msg_dataArrayList.add(chat_msg_data);
            synchronized (chat_msg_dataArrayList){
                adapter_chat.notifyItemChanged(chat_msg_dataArrayList.size()-1);
            }
        });
    }

    /*누군가 들어있는 방에 들어갈 때 들어온사람에게 발생하는 메소드*/
    @Override
    public void onSelfJoined(String username) {
        peopleNum+=1;
        Chat_Msg_Data chat_msg_data = new Chat_Msg_Data(null,username+" 님 환영합니다.", ViewType_Code.ViewType.CENTER_CONTENT,null);
        chat_msg_dataArrayList.add(chat_msg_data);
        synchronized (chat_msg_dataArrayList){
            adapter_chat.notifyItemChanged(chat_msg_dataArrayList.size()-1);
        }
    }

    /*누군가 나갈 때 방에 있는 사람들에게 발생하는 메소드*/
    @Override
    public void onPeerLeave(JSONObject data) {
        String socketId = data.optString("socketId");
        String room = data.optString("room");
        String username = data.optString("username");
        String after_owner_nickname = data.optString("owner_nickname");
        runOnUiThread(() -> {
            Chat_Msg_Data chat_msg_data = new Chat_Msg_Data(null,username+" 님이 나가셨습니다.", ViewType_Code.ViewType.CENTER_CONTENT,null);
            //데이터 어레이리스트에 추가
            chat_msg_dataArrayList.add(chat_msg_data);
            synchronized (chat_msg_dataArrayList) {
                adapter_chat.notifyItemChanged(chat_msg_dataArrayList.size() - 1);
            }

            int user_list_arraylist_size = participant_list_arrayList.size();
            for (int i=0; i<user_list_arraylist_size; i++){
                //해당 유저 찾고
                if(participant_list_arrayList.get(i).getNickname().equals(username)){
                    participant_list_arrayList.remove(i);
                    break;
                }
            }
            //방장 변경
            for (int i=0; i<user_list_arraylist_size-1; i++){
                //변경된 방장의 이름 찾기
                if(participant_list_arrayList.get(i).getNickname().equals(after_owner_nickname)){
                    participant_list_arrayList.get(i).setOwner("방장");
                    break;
                }
            }
            adapter_face_participant_list.notifyDataSetChanged();

            //peerConnection 연결 끊기
            Log.d(TAG, "onPeerLeave: 연결끊기 확인1");
            int datavideoTrackArrayListSize = dataVideoTrackArrayList.size();
            Log.d(TAG, "onPeerLeave: 연결끊기 확인2");
            for (int i = 0; i < datavideoTrackArrayListSize; i++) {
                Log.d(TAG, "onPeerLeave: 연결끊기 확인3");
                if (dataVideoTrackArrayList.get(i).getSocketId().equals(socketId)) {
                    Log.d(TAG, "onPeerLeave: 연결끊기 확인4");
                    //저장한 peerconnection 지우기

                    peerConnectionMap.get(socketId).removeStream(mediaStream);
                    peerConnectionMap.get(socketId).removeIceCandidates(icecandidateMap.get(socketId).toArray(new IceCandidate[0]));
                    icecandidateMap.remove(socketId);
                    peerConnectionMap.get(socketId).close();
                    peerConnectionMap.remove(socketId);
                    videoTrack.removeSink(dataVideoTrackArrayList.get(i).getSurfaceViewRenderer());
                    dataVideoTrackArrayList.get(i).getVideoTrack().removeSink(dataVideoTrackArrayList.get(i).getSurfaceViewRenderer());
                    dataVideoTrackArrayList.get(i).getSurfaceViewRenderer().release();
                    dataVideoTrackArrayList.get(i).setVideoTrack(null);
                    dataVideoTrackArrayList.get(i).setSurfaceViewRenderer(null);
                    dataVideoTrackArrayList.get(i).setSocketId(null);
//
                    dataVideoTrackArrayList.remove(i);

                    adapterVideo.notifyItemRemoved(i);
//                    adapterVideo.notifyItemRangeChanged(i,datavideoTrackArrayListSize-1);
//                    adapterVideo.notifyDataSetChanged();
                    Log.d(TAG, "onPeerLeave: 연결끊기 확인5");
                    break;
                }
            }
            autoScreenSettingMinus(); //누가 나갈 때 화면 자동 조정하기
        });

    }

    /*누군가 방에 입장할 때, sdp를 주고 받는데 단계.*/
    @Override
    public void onOfferReceived(JSONObject data) {

            final String socketId = data.optString("from");
            PeerConnection peerConnection = getOrCreatePeerConnection(socketId);
            peerConnection.setRemoteDescription(new SdpAdapter("setRemoteSdp:" + socketId),
                    new SessionDescription(SessionDescription.Type.OFFER, data.optString("sdp")));
            peerConnection.createAnswer(new SdpAdapter("localAnswerSdp") {
                @Override
                public void onCreateSuccess(SessionDescription sdp) {
                    super.onCreateSuccess(sdp);
                    peerConnection.setLocalDescription(new SdpAdapter("setLocalSdp:" + socketId), sdp);
                    signalingClient_webRTC.sendSessionDescription(sdp, socketId);
                    autoScreenSettingPlus();
                }
            }, new MediaConstraints());
    }

    /*누군가 방에 입장할 때, sdp를 주고 받는데 단계*/
    @Override
    public void onAnswerReceived(JSONObject data) {
        String socketId = data.optString("from");
        PeerConnection peerConnection = getOrCreatePeerConnection(socketId);
        peerConnection.setRemoteDescription(new SdpAdapter("setRemoteSdp:" + socketId),
                new SessionDescription(SessionDescription.Type.ANSWER, data.optString("sdp")));
        //여기서 Adapter notification해주기
        autoScreenSettingPlus();
    }

    /*입장한 사람의 IceCandidate 저장*/
    @Override
    public void onIceCandidateReceived(JSONObject data) {
        String socketId = data.optString("from");
        PeerConnection peerConnection = getOrCreatePeerConnection(socketId);
        IceCandidate iceCandidate = new IceCandidate(data.optString("id"), data.optInt("label"), data.optString("candidate"));
        peerConnection.addIceCandidate(iceCandidate);
        if(icecandidateMap.get(socketId)==null){
            iceCandidateList = new ArrayList<>();
            iceCandidateList.add(iceCandidate);
            icecandidateMap.put(socketId,iceCandidateList);
        }
        else {
            icecandidateMap.get(socketId).add(iceCandidate);
        }
    }

    /*채팅 주고 받는 메소드*/
    @Override
    public void onChat(JSONObject data) {
        String chat_message = data.optString("message"); //누군가 작성한 채팅 내용
        String user_name = data.optString("user_name"); //채팅을 작성한 사람
        Chat_Msg_Data chat_msg_data;
        if(user_name.equals(Activity_Lobby.usernickname)){
            //서버에서 받은 데이터 객체에 담기(userid->null,message->누가 입장한지,어떤 뷰타입인지)
            chat_msg_data = new Chat_Msg_Data(user_name,chat_message, ViewType_Code.ViewType.RIGHT_CONTENT,null);
        }
        else {
            //서버에서 받은 데이터 객체에 담기(userid->null,message->누가 입장한지,어떤 뷰타입인지)
            chat_msg_data = new Chat_Msg_Data(user_name,chat_message,ViewType_Code.ViewType.LEFT_CONTENT,null);
        }
        //데이터 어레이리스트에 추가
        chat_msg_dataArrayList.add(chat_msg_data);
        runOnUiThread(() -> {
            synchronized (chat_msg_dataArrayList){
                adapter_chat.notifyItemChanged(chat_msg_dataArrayList.size()-1);
                room_face_msg_recyclerview.scrollToPosition(adapter_chat.getItemCount() - 1);
            }
        });

    }

    /*참여할때 참가자 목록 추가하는 메소드 -JSONObject로 받은경우(보통 방을 생성할 때 방생성한 사람이 받거나, 누군가 참여할 때 방에 있는 사람들에게 오는 메소드)-*/
    @Override
    public void onParticipantObject(JSONObject data) {
        String userNickname = data.optString("user_name");
        String owner = data.optString("owner");
        //여기에 참여자 리사이클러뷰에 적용 시킬 arraylist add해주기
        FaceChatRoom_Participant_List_Data participant_list_data = new FaceChatRoom_Participant_List_Data(userNickname,owner,"없음");
        participant_list_arrayList.add(participant_list_data);
        int position;
        synchronized (participant_list_arrayList){
            runOnUiThread(() -> {
                adapter_face_participant_list.notifyItemChanged(participant_list_arrayList.size() - 1);
            });
            position = participant_list_arrayList.size()-1;
        }
        String serverurl = "http://3.36.188.116/opentalk/profile_img_load_nickname.php";
        VolleyprofileIMG_Participant_List_Face volleyprofileIMG_participant_list = new VolleyprofileIMG_Participant_List_Face(participant_list_arrayList,face_chat_handler,position,userNickname);
        volleyprofileIMG_participant_list.profile_upload(serverurl);
    }

    /*참여할때 참가자 목록 추가하는 메소드 -JSONArray로 받은경우(방에 입장할때 방 사람들의 모든 정보를 받기위해해)-*/
   @Override
    public void onParticipantArray(JSONArray data_array) {
       int jsonArray_length = data_array.length();
       try {
           for (int i=0; i<jsonArray_length; i++){
               JSONObject user_data = data_array.getJSONObject(i);
               String userNickname = user_data.getString("user_name");
               String owner = user_data.getString("owner");
               //여기에 참여자 리사이클러뷰에 적용 시킬 arraylist add해주기
               FaceChatRoom_Participant_List_Data participant_list_data = new FaceChatRoom_Participant_List_Data(userNickname,owner,"없음");
               participant_list_arrayList.add(participant_list_data);
           }
           runOnUiThread(()->{
                   adapter_face_participant_list.notifyDataSetChanged();
           });
           String serverurl = "http://3.36.188.116/opentalk/profile_img_load_nickname.php";
           int array_size = participant_list_arrayList.size();

           for (int i=0; i<array_size; i++){
               String nickname_participant_tome = participant_list_arrayList.get(i).getNickname();
               VolleyprofileIMG_Participant_List_Face volleyprofileIMG_participant_list = new VolleyprofileIMG_Participant_List_Face(participant_list_arrayList,face_chat_handler,i,nickname_participant_tome);
               volleyprofileIMG_participant_list.profile_upload(serverurl);
           }

       } catch (JSONException e) {
           e.printStackTrace();
       }
    }

    /*강퇴 당하거나 강퇴된 유저가 있을경우*/
    @Override
    public void onUserKick(JSONObject data) {
        String userNickName = data.optString("username");
        if(userNickName.equals(Activity_Lobby.usernickname)){
            //강퇴된 유저의 소켓으로 다시 한번 서버통신해서 sockets.leave(data.num)해줘야한다.
            signalingClient_webRTC.sendUserKickLeave(room_name);
            kick_check = false;
            runOnUiThread(()-> {
                Toast.makeText(Activity_Room_Face.this, "방에서 강제로 퇴장당하셨습니다.", Toast.LENGTH_LONG).show();
                finish();
            });

        }
        else {
            //처리해줄것 chat arraylist에 강퇴됬다는
            //서버에서 받은 데이터 객체에 담기(userid->null,message->누가 입장한지,어떤 뷰타입인지)
            Chat_Msg_Data chat_msg_data = new Chat_Msg_Data(null,userNickName+" 님이 방에서 쫓겨났습니다.", ViewType_Code.ViewType.CENTER_CONTENT,null);
            //채팅 데이터 어레이리스트에 추가
            chat_msg_dataArrayList.add(chat_msg_data);
            synchronized (chat_msg_dataArrayList) {
                runOnUiThread(()-> {
                    adapter_chat.notifyItemChanged(chat_msg_dataArrayList.size() - 1);
                });
            }
            //채팅방 사용자 목록에서 제거
            int user_list_arraylist_size = participant_list_arrayList.size();
            for (int i=0; i<user_list_arraylist_size; i++){
                //해당 유저 찾고
                if(participant_list_arrayList.get(i).getNickname().equals(userNickName)){
                    participant_list_arrayList.remove(i);
                    runOnUiThread(()->{
                        adapter_face_participant_list.notifyDataSetChanged();
                    });
                    break;
                }
            }
        }
    }

    private VideoCapturer createCameraCapturer() {
        Camera1Enumerator enumerator = new Camera1Enumerator(false);
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        return null;
    }

    /*채팅 온오프 클릭할 경우 리스너*/
    class DrawButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            if(chatonoffCheck==true) {
                chat_parentview.setVisibility(View.VISIBLE);
                room_face_msg_recyclerview.setVisibility(View.VISIBLE);
                test.startAnimation(translateBottomMiddleAnim);
                chat_openBtn.startAnimation(translateBottomMiddleAnim);
                chat_parentview.startAnimation(translateBottomMiddleAnim);
                chat_framView.startAnimation(translateBottomMiddleAnim);
            }
            else {
                imm.hideSoftInputFromWindow(room_face_input.getWindowToken(),0);
                chat_parentview.setVisibility(View.GONE);
                room_face_msg_recyclerview.setVisibility(View.GONE);
            }
            chatonoffCheck = !chatonoffCheck;
        }
    }

    /*메인화면 오른쪽으로 꺼내는 리스너*/
    class MainScreenOpenClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            if(!isPageOpen){
                //레이아웃 위치 되돌리고 애니메이션 적용 시키기 ->그래야 자연스러워 보인다.
                face_main_view.setX(0);
                face_main_view.startAnimation(translateRightAnim);
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
                                recyclerview_parentview.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                room_face_input_btn.setBackgroundColor(Color.parseColor("#dbddfc"));
                                chat_framView.setBackgroundColor(Color.parseColor("#e9ebf3"));
                                chat_parentview.setBackgroundColor(Color.parseColor("#e9ebf3"));
                                //액션바 색상 원래대로
                                actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#dbddfc")));
                                //채팅입력버튼 활성화
                                room_face_input_btn.setEnabled(true);
                            }
                        });
                    }
                },500);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        String IP_ADDRESS = "3.36.188.116/opentalk";
        HttpConnection_room_exit httpConnection_room_exit;
        httpConnection_room_exit = new HttpConnection_room_exit(this);
        httpConnection_room_exit.execute("http://"+IP_ADDRESS+"/room_exit.php",String.valueOf(room_name));

        options=null;
        signalingClient_webRTC.destroy();

        //내가 나가기전의 유저는 removeSink가 안된다 그렇다고 유저가 나갈때 해주면 오류가 생긴다..
        //removeSink를 꼭 해줘야하는데 이럴 땐 어떻게 해야하나 생각좀해보자..
        for (int i = 0; i<dataVideoTrackArrayList.size(); i++){
            videoTrack.removeSink(dataVideoTrackArrayList.get(i).getSurfaceViewRenderer());
            dataVideoTrackArrayList.get(i).getSurfaceViewRenderer().release();
        }
        dataVideoTrackArrayList.clear();
        for (Object key : peerConnectionMap.keySet()){
            peerConnectionMap.get(key).removeStream(mediaStream);
            peerConnectionMap.get(key).dispose();
            icecandidateMap.remove(key);
        }
        this.peerConnectionFactory.stopAecDump();
        this.videoCapturer.dispose();
        this.videoCapturer = null;
        this.videoSource.dispose();
        this.videoSource = null;
//        this.audioSource.dispose();
//        this.audioSource=null;
        this.peerConnectionFactory.dispose();
        eglBaseContext=null;
        PeerConnectionFactory.stopInternalTracingCapture();
        PeerConnectionFactory.shutdownInternalTracer();
        peerConnectionFactory=null;
        mediaStream=null;
        iceServers=null;
        peerConnectionMap=null;
        icecandidateMap=null;
        iceCandidateList=null;
        options=null;
        videoTrack=null;

        finish();
    }

    /*액션툴바*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.facechat_room_actionbar, menu);
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
                    face_main_view.startAnimation(translateLeftAnim);
                    Timer timer;
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerview_parentview.setBackgroundColor(Color.parseColor("#4a4b4f"));
                                    room_face_input_btn.setBackgroundColor(Color.parseColor("#818285"));
                                    chat_framView.setBackgroundColor(Color.parseColor("#7b7d86"));
                                    chat_parentview.setBackgroundColor(Color.parseColor("#7b7d86"));

                                    actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#363739")));
                                    room_face_input_btn.setEnabled(false);
                                    //레이아웃 실제로 위치 변경해줘야한다 -> 안그러면 실제 터치할 경우에는 적용이 되지 않는다.
                                    face_main_view.setX((float) -(devicewidth/1.13));
                                    //실제위치 맞춰주면 빼꼼화면이 보여지지 않고 다 사라지기 때문에 위의 setX위치맞춰서 다시 보이게하기
                                    face_main_view.startAnimation(translateLeftAnim_after);
                                }
                            });
                        }
                    },500);
                    isPageOpen = false;
                    Log.d(TAG, "onOptionsItemSelected: ture다 아러 ㅏ");
                }
                else {
                    //레이아웃 위치 되돌리고 애니메이션 적용 시키기 ->그래야 자연스러워 보인다.
                    face_main_view.setX(0);
                    face_main_view.startAnimation(translateRightAnim);
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
                                    recyclerview_parentview.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                    room_face_input_btn.setBackgroundColor(Color.parseColor("#dbddfc"));
                                    chat_framView.setBackgroundColor(Color.parseColor("#e9ebf3"));
                                    chat_parentview.setBackgroundColor(Color.parseColor("#e9ebf3"));
                                    //액션바 색상 원래대로
                                    actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#dbddfc")));
                                    //채팅입력버튼 활성화
                                    room_face_input_btn.setEnabled(true);
                                }
                            });
                        }
                    },500);
                }
                return true;
            case R.id.switchCamera_btn :
                /*카메라 전환 frontANDback==true(정면 카메라일 때), frontANDback==false(후면 카메라일 때)*/
                if(frontANDback==true) {
                    CameraVideoCapturer cameraVideoCapturer = (CameraVideoCapturer) videoCapturer;
                    cameraVideoCapturer.switchCamera(null);
                    dataVideoTrackArrayList.get(0).getSurfaceViewRenderer().setMirror(false);
                    frontANDback=false;
                }
                else {
                    CameraVideoCapturer cameraVideoCapturer = (CameraVideoCapturer) videoCapturer;
                    cameraVideoCapturer.switchCamera(null);
                    dataVideoTrackArrayList.get(0).getSurfaceViewRenderer().setMirror(true);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
