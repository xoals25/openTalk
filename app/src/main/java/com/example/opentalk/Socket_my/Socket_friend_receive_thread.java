package com.example.opentalk.Socket_my;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.example.opentalk.Handler.Friend_wait_Handler;
import com.example.opentalk.Code.HandlerType_Code;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

/*
* 2021 / 01 / 21
* 클래스 설명  : 현재 사용하지 않는 클래스파일
*
* */

public class Socket_friend_receive_thread extends Thread {
    String TAG = "Socket_friend_receive_thread";
    Socket socket_friend_list;
    BufferedReader bufferedReader;
    Context mContext;
    Friend_wait_Handler friend_wait_handler;

    public Socket_friend_receive_thread(Socket socket, Context mContext,Friend_wait_Handler friend_wait_handler){
        this.socket_friend_list = socket;
        this.mContext = mContext;
        this.friend_wait_handler = friend_wait_handler;

        try{
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (Exception e){
            Log.d(TAG, "Socket_friend_receive_thread: error message : "+e.getMessage());
        }

    }

    @Override
    public void run() {
        super.run();
        String socket_data = null;
        try{
            Log.d(TAG, "run: start");
            while ((socket_data = bufferedReader.readLine())!=null){
                String type = null;
                String request_email_id = null;
                JSONObject jsonObject = new JSONObject(socket_data);
                //어떤 요청인지 알려주는 데이터 담기
                type = jsonObject.getString("type");
                Log.d(TAG, "run: type : "+type);
                if(type.equals("friend_request")){
                    //요청한 사람의 id 담기
                    request_email_id = jsonObject.getString("request_email_id");
                    //이제 Fragment_friend_wait의 리사이클러뷰에 뿌려주기위해 handler에게 정보 전달
                    Message message = new Message();
                    message.what = HandlerType_Code.HandlerType.FRIEND_WAIT_RECYCLERVIEW;
                    message.obj = request_email_id;
                    friend_wait_handler.sendMessage(message);
                }
                socket_data=null;
            }
        }
        catch (Exception e){
            Log.d(TAG, "run: error message : "+e.getMessage());
        }

    }
}
