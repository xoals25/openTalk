package com.example.opentalk.VoiceCommunication;

import android.util.Log;

import com.example.opentalk.VoiceCommunication.Data.OtherUserIP_Voice_Data;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

/*
* 12-03 작성된 클래스
* 클래스 설명 : 음성 채팅방 나갈 때 서버에게 나간다고 알려주고, p2p통신하는 유저들에게도 나간다고 알려주는 클래스
*
* */

public class Exit extends Thread {

    String TAG ="Exit";
    //port와 IP_ADDRESS는 Exit 클래스와 같아야한다.
    public int port = 8088;
    public String IP_ADDRESS = "3.36.188.116";
    DatagramSocket udp_socket; //서버에게 보내줄것 ->서버에서 퇴장한 유저들 따로 또 알려주기(enter에서 이메일들을 모아두는 어레이리스트에서 값 지워주기 위해서)
    DatagramSocket voice_socket; //사용자들에게 보내줄 것

    DatagramPacket datagramPacket;

    //유저들의 주소가 저장되어있는 것들
    ArrayList<OtherUserIP_Voice_Data> otherUserIP_voices;

    String room_num;
    String email_id;

    public Exit(DatagramSocket udp_socket,DatagramSocket voice_socket,String room_num,String email_id, ArrayList<OtherUserIP_Voice_Data> otherUserIP_voices){
        this.udp_socket = udp_socket;
        this.voice_socket = voice_socket;
        this.room_num = room_num;
        this.email_id = email_id;
        this.otherUserIP_voices = otherUserIP_voices;
    }

    byte[] udp_socket_buf; //이건 json type : "퇴장" room_num : "~~" email_id : "~~"로 보내줘야한다.
    byte[] voice_socket_buf; //이건 그냥 "퇴장" 이라하고 보내주기

    @Override
    public void run() {
        super.run();
        JSONObject jsonObject;
        String user_send_msg;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("type","퇴장");
            jsonObject.put("room_num",room_num);
            jsonObject.put("email_id",email_id);

            udp_socket_buf = jsonObject.toString().getBytes();

            datagramPacket = new DatagramPacket(udp_socket_buf,udp_socket_buf.length,InetAddress.getByName(IP_ADDRESS),port);
            udp_socket.send(datagramPacket);

            user_send_msg ="퇴장";
            voice_socket_buf = user_send_msg.getBytes();
            //아 hashmap가져와야하는구나 hashmap에 유저들 주소가 있으니까...
            int otherUserIP_voices_size = otherUserIP_voices.size();
            for (int i=0; i<otherUserIP_voices_size; i++){
                datagramPacket = new DatagramPacket(voice_socket_buf,voice_socket_buf.length,otherUserIP_voices.get(i).getInetAddress(),otherUserIP_voices.get(i).getPort());
                voice_socket.send(datagramPacket);
            }

        }catch (Exception e){
            Log.d(TAG, "run: 에러 메세지 : "+e.getMessage());
        }
    }
}
