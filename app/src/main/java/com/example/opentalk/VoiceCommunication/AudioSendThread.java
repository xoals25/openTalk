package com.example.opentalk.VoiceCommunication;

import android.media.AudioRecord;
import android.util.Log;

import com.example.opentalk.Activity.Activity_Room_Voice;
import com.example.opentalk.VoiceCommunication.Data.OtherUserIP_Voice_Data;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

public class AudioSendThread extends Thread{

    String TAG = "AudioSendThread";
    DatagramSocket voice_socket;
    DatagramPacket voice_packet;

    //스레드 onoff 관리
    boolean voice_communication = true;
    //말하는거 onoff
    boolean voice_talk =true;

    int bytelength =((Activity_Room_Voice)Activity_Room_Voice.mContext).voicebytelength;
    AudioRecord mAudioRecord;

    public ArrayList<OtherUserIP_Voice_Data> otherUserIP_voices;
    int otherUserIP_voices_Size;

    public AudioSendThread(DatagramSocket voice_socket, AudioRecord mAudioRecord, ArrayList<OtherUserIP_Voice_Data> otherUserIP_voices){
        this.voice_socket =voice_socket;
        this.mAudioRecord = mAudioRecord;
        this.otherUserIP_voices = otherUserIP_voices;
    }

    @Override
    public void run() {
        super.run();
        byte[] voice_buffer = new byte[bytelength];
        while (voice_communication){

            if(voice_talk) {
                //사이즈를 따로 넣어준이유는 반복문에서 속도가 조금이라도 더 빨라지기 때문에
                //->추후에 while문에도 빼줄수 있는지 실험해보자.
                if (otherUserIP_voices.size() != 0) {
                    otherUserIP_voices_Size = otherUserIP_voices.size();
                }
                int bufReadResult = mAudioRecord.read(voice_buffer, 0, voice_buffer.length);
//            Log.d(TAG, "run: bufReadResult : "+bufReadResult);

                /****기기 바꿀때마다 여기 첫번째로 주석 처리해줄곳
                 * 8082를 음성데이터 보내면 udp_packet,socket
                 * 8083으로 음성데이터 보내면 udp_packet2,socket2
                 * 8084로 음성데이터 보내면 udp_packet3,socket3으로 변경해줘야한다
                 *
                 * 그리고 MainActivity에서 전송할 port 변경해주기
                 * ****/

                for (int i = 0; i < otherUserIP_voices_Size; i++) {
                    try {
                        voice_packet = new DatagramPacket(voice_buffer, bufReadResult, otherUserIP_voices.get(i).getInetAddress(), otherUserIP_voices.get(i).getPort());
                        voice_socket.send(voice_packet);
                    } catch (Exception e) {
                        Log.d(TAG, "run: ERROR.message : " + e.getMessage());
                    }
                }
            }
        }
    }

    //방을 나갈경우 스레드 종료할 때
    public void setVoice_communication(boolean voice_communication_ONOFF){
        this.voice_communication = voice_communication_ONOFF;
    }

    //자신의 말을 음소거 할 경우
    public void setVoice_talk(boolean voice_talk_ONOFF){
        this.voice_talk = voice_talk_ONOFF;
    }

}
