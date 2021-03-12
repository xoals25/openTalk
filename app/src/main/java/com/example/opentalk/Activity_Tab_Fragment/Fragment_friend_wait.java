package com.example.opentalk.Activity_Tab_Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.opentalk.Activity.Activity_Friend_List;
import com.example.opentalk.Activity.Activity_Lobby;
import com.example.opentalk.Adapter.Adapter_Friend_Wait;
import com.example.opentalk.Data.FriendWaitData;
import com.example.opentalk.Handler.Friend_wait_Handler;
import com.example.opentalk.Http.HttpConnection_Friend_Wait;
import com.example.opentalk.Http.HttpConnection_Lobby_Rooms;
import com.example.opentalk.R;
import com.example.opentalk.Socket_my.Socket_friend_receive_thread;

import java.util.ArrayList;

public class Fragment_friend_wait extends Fragment {
    public static Context context_Fragment_friend_wait;

    String TAG = "Fragment_friend_wait";
    public RecyclerView recyclerView;
    private Socket_friend_receive_thread socket_friend_receive_thread;
    public static Friend_wait_Handler friend_wait_handler_static;
    /****리사이클러뷰 관련****/
    RecyclerView friend_wait_RecyclerView;
    public static ArrayList<FriendWaitData> request_email_arrayList;
    LinearLayoutManager linearLayoutManager;
    Adapter_Friend_Wait adapter_friend_wait;

    //요청한 친구 list 요청할 서버 주소
    String IP_ADDRESS = "3.36.188.116/opentalk";

    //받은 친구 요청 리스트 요청할 http
    HttpConnection_Friend_Wait httpConnection_friend_wait;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_friend_wait,null);
        context_Fragment_friend_wait = inflater.getContext();
        friend_wait_RecyclerView = (RecyclerView)view.findViewById(R.id.friend_wait_RecyclerView);
        request_email_arrayList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(getContext());
        adapter_friend_wait = new Adapter_Friend_Wait(request_email_arrayList,getContext());

        friend_wait_RecyclerView.setAdapter(adapter_friend_wait);
        friend_wait_RecyclerView.setLayoutManager(linearLayoutManager);

        Log.d(TAG, "onCreateView: adapter_friend_wait : "+adapter_friend_wait);
        friend_wait_handler_static = new Friend_wait_Handler(request_email_arrayList,adapter_friend_wait,getContext());

        /*초기에 http통신해서 방들 뿌려주기*/
        httpConnection_friend_wait = new HttpConnection_Friend_Wait(getContext(),friend_wait_handler_static,request_email_arrayList);
        httpConnection_friend_wait.execute("http://"+IP_ADDRESS+"/friend_wait_list.php",Activity_Lobby.userid);

//        socket_friend_receive_thread = new Socket_friend_receive_thread(Activity_Lobby.socket_friend_list,getContext(),friend_wait_handler);
//        socket_friend_receive_thread.start();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TAG", "onResume1: ");
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d("TAG", "onPause1: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        friend_wait_handler_static = null;
        request_email_arrayList = null;
    }
}
