package com.example.opentalk.Socket_my;

import android.util.Log;

import com.example.opentalk.Activity.Activity_Lobby;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class Socket_friend_chat_thread extends Thread{

    String TAG = "Socket_friend_chat_thread";
    JSONObject jsonObject;
    public Socket_friend_chat_thread(JSONObject jsonObject){
        this.jsonObject = jsonObject;
    }

    @Override
    public void run() {
        super.run();
        try {
            Log.d(TAG, "run: 채팅 보내는거 확인");
            PrintWriter printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(Activity_Lobby.socket_friend_list.getOutputStream())));
            printWriter.write(jsonObject.toString()+"\n");
            printWriter.flush();
        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "run: 채팅 보낼 때 error message : "+e.getMessage());
        }
    }
}
