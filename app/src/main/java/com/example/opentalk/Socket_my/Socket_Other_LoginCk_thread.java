package com.example.opentalk.Socket_my;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.opentalk.Activity.Activity_Lobby;
import com.example.opentalk.Activity.Activity_Login_Main;
import com.example.opentalk.SharedPreference.PreferenceManager_member;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Socket_Other_LoginCk_thread extends Thread {
    String TAG = "Socket_Other_LoginCk_thread";

    String SOCKET_SERVER_IP = "3.36.188.116";
    int SOCKET_SERVER_PORT_LOGINCK = 8079;
    Socket socket;
    String userid;
    BufferedReader bufferedReader;
    public boolean onoff = true;
    Context context;
    JSONObject jsonObject;

    public Socket_Other_LoginCk_thread(Socket socket,String userid,Context context,boolean onoff,JSONObject jsonObject){
        this.socket =socket;
        this.userid =userid;
        this.context = context;
        this.onoff = onoff;
        this.jsonObject = jsonObject;
    }

    @Override
    public void run() {
        super.run();

        try {
            this.socket = new Socket(SOCKET_SERVER_IP, SOCKET_SERVER_PORT_LOGINCK);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            bufferedWriter.write(jsonObject+"\n");
            bufferedWriter.flush();
        }catch (IOException e){

        }

        String socket_data = null;
        try {
            Log.d(TAG, "run: while문 끝난거 확인하기0");
            if(onoff==true) {
                Log.d(TAG, "run: while문 끝난거 확인하기0-1");
                while ((socket_data = bufferedReader.readLine()) != null && onoff == true) {
                    Log.d(TAG, "run: while문 끝난거 확인하기1");
                    JSONObject jsonObject = new JSONObject(socket_data);
                    Log.d(TAG, "run: while문 끝난거 확인하기1-1");
                    String type = jsonObject.getString("type");
                    Log.d(TAG, "run: while문 끝난거 확인하기1-2");
                    if (type.equals("CompulsoryLogoutFromLogin")) {
                        Log.d(TAG, "run: 확인하기2");
                        //종료할 때 해줘야할 것들 다 처리해줘야한다..
                        //모든 activity가 onDestory가 뜰것이다.
                        //onDestory에서 처리해주지 못한 예를 들면
                        //Activity_Lobby에서 onDestory가 뜨지만 거기에서 Socket_connect_offline_thread 실행 방지를 위해

                    /*
                    if(compulsoryLogoutCk=false) {
                                Socket_connect_offline_thread socket_connect_offline_thread = new Socket_connect_offline_thread();
                                socket_connect_offline_thread.start();
                     }
                     */

                        //이런 처리를 해준것처럼 나머지들도 onDesoty가 나와서 대부분 처리가 되겠지만 혹시모르니 살펴보자.

                        //Activity_Lobby는 처리 완료
                        //Activity_MyPage 처리 완료
                        onoff = false;
                        Activity_Lobby lobby_context = ((Activity_Lobby) Activity_Lobby.mainContext);
                        if (lobby_context != null) {
                            lobby_context.compulsoryLogoutCk = true;
                        }
                        Intent intent = new Intent(context, Activity_Login_Main.class);
                        PreferenceManager_member.setInt(context, "autoLogin", -1);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("CompulsoryLogout", "yes");

                        context.startActivity(intent);
                    } else if (type.equals("CompulsoryLogoutFromPwdchagne")) {
                        onoff = false;
                        Activity_Lobby lobby_context = ((Activity_Lobby) Activity_Lobby.mainContext);
                        if (lobby_context != null) {
                            lobby_context.compulsoryLogoutCk = false;
                        }
                        Intent intent = new Intent(context, Activity_Login_Main.class);
                        PreferenceManager_member.setInt(context, "autoLogin", -1);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("CompulsoryLogout", "yes");
                        context.startActivity(intent);
                    }
                    else{
                        Log.d(TAG, "run: 확인하기 else로 들어옴");
                    }
                }
            }
            Log.d(TAG, "run: while문 끝난거 확인하기 끝");
        }catch (Exception e){
            Log.d(TAG, "Socket_Other_LoginCk_thread run while문 ERROR MESSAGE : "+e.toString());
        }

    }

}
