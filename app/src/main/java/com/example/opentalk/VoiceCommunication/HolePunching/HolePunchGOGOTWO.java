package com.example.opentalk.VoiceCommunication.HolePunching;

import android.util.Log;

import com.example.opentalk.VoiceCommunication.Data.OtherUserIP_Data;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class HolePunchGOGOTWO extends Thread {

    String TAG = "HolePunchGOGO";
    DatagramSocket voice_socket;
    DatagramPacket hole_punching_packet;
    int i = 0;
    int j = 0;
    OtherUserIP_Data otherUserIPData;
    boolean onoff = true;
    String type_msg;

    String email_id;

    public HolePunchGOGOTWO(DatagramSocket voice_socket, OtherUserIP_Data otherUserIPData, String type_msg,String email_id){
        this.voice_socket = voice_socket;
        this.otherUserIPData = otherUserIPData;
        this.type_msg = type_msg;
        this.email_id = email_id;
    }
    @Override
    public void run() {
        super.run();
        String final_msg = type_msg;
        byte[] private_json_buf = final_msg.getBytes();
        byte[] public_json_buf = final_msg.getBytes();
        while (i<15){
            i++;

            /****설마 사설이랑 공인 둘다 오지는 않겠지??
             *
             * 더 확실하게 하기위해 type holepunching 하고 user_email로 아이디를 보내주자.
             * 그리고 Enter에서는 arraylist에 ip-port로 저장하지 말고 user_email로 저장하자.
             *
             * ****/
                try {
                    //사설 ip
                    InetAddress other_user_ip = otherUserIPData.getPrivate_inetAddress();
                    int other_user_port = otherUserIPData.getPrivate_port();
                    hole_punching_packet = new DatagramPacket(private_json_buf,private_json_buf.length,other_user_ip,other_user_port);
                    voice_socket.send(hole_punching_packet);
                    Log.d(TAG, "run: hole_punching_socket.port를 확인해보자 "+type_msg+" 사설: "+voice_socket.getLocalPort());
                    Log.d(TAG, "run: 누구한테 보내는가? "+type_msg+" 사설: "+other_user_ip+"/"+other_user_port);
                }catch (Exception e){
                    Log.d(TAG, "run: e.getMessage : "+e.getMessage());
                }
        }

        while (j<40){
            j++;
            try {
                //공인 ip
                InetAddress other_user_ip = otherUserIPData.getPublic_inetAddress();
                int other_user_port = otherUserIPData.getPublic_port();
                hole_punching_packet = new DatagramPacket(public_json_buf,public_json_buf.length,other_user_ip,other_user_port);
                voice_socket.send(hole_punching_packet);
                Log.d(TAG, "run: hole_punching_socket.port를 확인해보자 "+type_msg+" 공인: "+voice_socket.getLocalPort());
                Log.d(TAG, "run: 누구한테 보내는가? "+type_msg+" 공인: "+other_user_ip+"/"+other_user_port);
            }catch (Exception e){
                Log.d(TAG, "run: e.getMessage : "+e.getMessage());
            }
        }
        Log.d(TAG, "run: 보냄");
    }

    public void setONOFF(boolean onoff){
        this.onoff = onoff;
    }
}
