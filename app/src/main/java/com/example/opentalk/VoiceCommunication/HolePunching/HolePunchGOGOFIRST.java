package com.example.opentalk.VoiceCommunication.HolePunching;

import android.util.Log;

import com.example.opentalk.VoiceCommunication.Data.OtherUserIP_Data;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class HolePunchGOGOFIRST extends Thread {

    String TAG = "HolePunchGOGO";
    DatagramSocket hole_punching_socket;
    DatagramPacket hole_punching_packet;
    int i = 0;
    int j = 0;
    OtherUserIP_Data otherUserIPData;
    boolean onoff = true;
    String type_msg;

    DatagramSocket voice_socket;
    String inetAddress;

    String email_id;

    public HolePunchGOGOFIRST(DatagramSocket hole_punching_socket, OtherUserIP_Data otherUserIPData, String type_msg, DatagramSocket voice_socket, String inetAddress,String email_id){
        this.hole_punching_socket = hole_punching_socket;
        this.otherUserIPData = otherUserIPData;
        this.type_msg = type_msg;
        this.voice_socket = voice_socket;
        this.inetAddress = inetAddress;
        this.email_id =email_id;
    }
    @Override
    public void run() {
        super.run();
//        String hello ="요청-p2p";
//        byte[] hello_buf=hello.getBytes();
        JSONObject private_jsonObject = new JSONObject();
        JSONObject public_jsonObject = new JSONObject();
        try {
            private_jsonObject.put("type",type_msg+"-사설");
            public_jsonObject.put("type",type_msg+"-공인");

            if(type_msg.equals("홀펀칭첫번째")){
                //홀펀칭으로 들어왔으니 내가 통신할 socket ip와 port privateIP 보내주기
                String voice_socket_PRIVATE_IP = inetAddress+"-"+voice_socket.getLocalPort();
                //이건 if(type.contains("voiceSocketIP")) 이부분에서 voice_socket port를 voice_socket port를 사용하지않고 udp_socket port를 사용하니까 public port만 따로 보내줌
                int voice_socket_PUBLIC_PORT = voice_socket.getLocalPort();

                private_jsonObject.put("private_IP",voice_socket_PRIVATE_IP);
                private_jsonObject.put("public_PORT",voice_socket_PUBLIC_PORT);
                private_jsonObject.put("email_id",email_id);
                public_jsonObject.put("private_IP",voice_socket_PRIVATE_IP);
                public_jsonObject.put("public_PORT",voice_socket_PUBLIC_PORT);
                public_jsonObject.put("email_id",email_id);

            }

        }catch (Exception e){

        }
        byte[] private_json_buf = private_jsonObject.toString().getBytes();
        byte[] public_json_buf = public_jsonObject.toString().getBytes();

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
                    hole_punching_socket.send(hole_punching_packet);
                    Log.d(TAG, "run: hole_punching_socket.port를 확인해보자 "+type_msg+" 사설: "+hole_punching_socket.getLocalPort());
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
                hole_punching_socket.send(hole_punching_packet);
                Log.d(TAG, "run: hole_punching_socket.port를 확인해보자 "+type_msg+" 공인: "+hole_punching_socket.getLocalPort());
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
