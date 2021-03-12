package com.example.opentalk.Socket_my;

import android.util.Log;

import com.example.opentalk.Activity.Activity_Friend_List;
import com.example.opentalk.Activity.Activity_Lobby;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class Socket_friend_choose_thread extends Thread {
    String TAG = "Socket_friend_choose_thread";
    JSONObject jsonObject;
    public Socket_friend_choose_thread(JSONObject jsonObject){
        this.jsonObject = jsonObject;
    }

    @Override
    public void run() {
        super.run();
        try {
            PrintWriter printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(Activity_Lobby.socket_friend_list.getOutputStream())));
            printWriter.write(jsonObject.toString()+"\n");
            printWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "onClick: 수락,거절버튼 클릭시 error message : "+e.getMessage()+e.getLocalizedMessage());
        }
    }
}
