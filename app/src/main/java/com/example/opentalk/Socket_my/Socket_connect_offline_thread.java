package com.example.opentalk.Socket_my;

import android.util.Log;

import com.example.opentalk.Activity.Activity_Lobby;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Socket_connect_offline_thread extends Thread {

    @Override
    public void run() {
        super.run();
        try{
            Log.d("TAG", "run: lobby 종료 확인");
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(Activity_Lobby.socket_friend_list.getOutputStream()));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type","friend_disconnect");
            jsonObject.put("my_email_id",Activity_Lobby.userid);
            bufferedWriter.write(jsonObject.toString()+"\n");
            bufferedWriter.flush();
            bufferedWriter.close();
        }
        catch (IOException | JSONException e){

        }

    }
}
