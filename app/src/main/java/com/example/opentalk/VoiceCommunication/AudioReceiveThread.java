package com.example.opentalk.VoiceCommunication;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import com.example.opentalk.Activity.Activity_Room_Voice;
import com.example.opentalk.VoiceCommunication.Data.OtherUserIP_Voice_Data;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AudioReceiveThread extends Thread {

    static public Context mContext;
    String email_id;

    String TAG = "AudioReceiveThread";
    DatagramSocket voice_socket;
    DatagramPacket voice_receive_packet;
    DatagramPacket voice_send_packet;
    AudioRecord audioRecord;

    boolean voice_communication = true;
    boolean voice_listen =true;
    int bytelength =((Activity_Room_Voice)Activity_Room_Voice.mContext).voicebytelength;


    Map<String,AudioTrack> user_list_hashmap = new HashMap<>();
    ArrayList<OtherUserIP_Voice_Data> otherUserIP_voices;
    private int mAudioSource = MediaRecorder.AudioSource.VOICE_COMMUNICATION;
    private int mSampleRate = 44100;
    private int mChannelCount = AudioFormat.CHANNEL_IN_STEREO;
    private int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT; //PCM 데이터를 받기 때문에
    private int mBufferSize = AudioTrack.getMinBufferSize(mSampleRate, mChannelCount, mAudioFormat);

    //socket과 audioRecord는 담아오기
    // 1번 방법.socket은 스레드에서 작동하기전에 홀펀칭을 먼저 해야하기때문에 먼저 생성되야 해서 생성하고 받아오는 식으로 진행
    // 2번 방법.아니면 스레드에서 애초에 socket생성후에 여기서 홀펀칭 한번만 진행하고 홀펀칭이 되면 voice_talk를 true시켜주는 걸로 할까?
    public AudioReceiveThread(DatagramSocket voice_socket, ArrayList<OtherUserIP_Voice_Data> otherUserIP_voices,String email_id){
        this.voice_socket = voice_socket;
        this.otherUserIP_voices = otherUserIP_voices;
        this.email_id = email_id;
    }

    @Override
    public void run() {
        super.run();
        byte[] receive_buffer =  new byte[bytelength];
        byte[] buffer = new byte[bytelength];

        //오디오트랙 여기서 객체 만들어주장~~

        while (voice_communication){


                try {
                    voice_receive_packet = new DatagramPacket(receive_buffer, receive_buffer.length);
                    voice_socket.receive(voice_receive_packet);
                    Log.d(TAG, "run: voice_receive_packet의 주소 : "+voice_receive_packet.getSocketAddress());

                    String msg = new String(voice_receive_packet.getData(),0,voice_receive_packet.getLength());
                    String msg_socket_address = String.valueOf(voice_receive_packet.getSocketAddress());

                    //내 voice_socket으로 메세지 잘받았어 너의 email_id도 같이 받아서 저장 해놨어
                    //내 voice_socket으로 내 email_id도 보낼테니 저장해줘
                    //문제점 발생 : map의 key를 email_id로 할려했는데 듣기 할때 속도를 줄이기위해 바로 해당 packet의 주소를 key로 잡은것이다.
                    //그냥 나갈때 voice_socket으로 퇴장이라고 보내야겠다......
                    if(msg.equals("홀펀칭두번째")){

                        if(!user_list_hashmap.containsKey(msg_socket_address)){
                            Log.d(TAG, "run: 홀펀칭두번째 받음");
                            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, mSampleRate, mChannelCount, mAudioFormat, mBufferSize, AudioTrack.MODE_STREAM); // AudioTrack 생성
                            audioTrack.play();
                            user_list_hashmap.put(msg_socket_address,audioTrack);

//                            //보낸 msg에서 이메일만 때오기
//                            String[] msg_split = msg.split("/@#");
//                            String msg_email_id = msg_split[1];
//                            Log.d(TAG, "run: 홀펀칭시도한 사람이 보낸 이메일입니다. : "+msg_email_id);

                            OtherUserIP_Voice_Data otherUserIP_voice = new OtherUserIP_Voice_Data(voice_receive_packet.getAddress(),voice_receive_packet.getPort());
                            otherUserIP_voices.add(otherUserIP_voice);

                            //여기선 voice_socket에 자신의 email_id 보내는게 맞다. 그럼 밑에서 받은 유저가 이 아이디를 저장하면된다.
                            msg = "홀펀칭-두번째-확인";
                            buffer = msg.getBytes();
                            voice_receive_packet = new DatagramPacket(buffer, buffer.length,voice_receive_packet.getAddress(),voice_receive_packet.getPort());
                            voice_socket.send(voice_receive_packet);
                            Log.d(TAG, "run: 홀펀칭두번째확인 보내기");
                            Log.d(TAG, "run: hsahmap size : "+user_list_hashmap.size());
                        }

                    }
                    //B에게 온 A의 답장
                    //너의 voice_socket에게 답장 받았어 너의 email_id도 저장할게 ~
                    else if(msg.equals("홀펀칭-두번째-확인")){
                        if(!user_list_hashmap.containsKey(msg_socket_address)){
                            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, mSampleRate, mChannelCount, mAudioFormat, mBufferSize, AudioTrack.MODE_STREAM); // AudioTrack 생성
                            audioTrack.play();
                            user_list_hashmap.put(msg_socket_address,audioTrack);
                            Log.d(TAG, "run: 홀펀칭두번째확인답장 받음");
                            Log.d(TAG, "run: hsahmap size : "+user_list_hashmap.size());

                            //email_id : 추후에 삭제해주기위해서 address,port : 음성 보내기위함
                            //voice_socket으로 보내준 email_id를 데이터에 넣어야한다
                            //실수로 자신의 email_id 넣지마라
                            OtherUserIP_Voice_Data otherUserIP_voice = new OtherUserIP_Voice_Data(voice_receive_packet.getAddress(),voice_receive_packet.getPort());
                            otherUserIP_voices.add(otherUserIP_voice);
                        }
                    }
                    else if(msg.equals("퇴장")){
                        user_list_hashmap.remove(msg_socket_address);

                        int otherUserIP_voices_size = otherUserIP_voices.size();
                        for (int i=0; i<otherUserIP_voices_size; i++){
                            //해당 아이디 발견하면 해당하는 인덱스 삭제해주고 break해주기
                            if(otherUserIP_voices.get(i).getInetAddress()==voice_receive_packet.getAddress()&& otherUserIP_voices.get(i).getPort()==voice_receive_packet.getPort()){
                                otherUserIP_voices.remove(i);
                                break;
                            }
                        }

                    }
                    else{
                        //목소리 on/off
                        if(voice_listen){
                            user_list_hashmap.get(msg_socket_address).write(receive_buffer,0,receive_buffer.length);
                        }
                    }
                }catch (Exception e){
                    Log.d(TAG, "run: error.getmessage : "+e.getMessage());
                }

        }
    }
    //방을 나갈경우 스레드 종료할 때
    public void setVoice_communication(boolean voice_communication_ONOFF){
        this.voice_communication = voice_communication_ONOFF;
    }

    //상대방 목소리 ON/OFF
    public void setVoice_listen(boolean voice_listenONOFF){
        this.voice_listen = voice_listenONOFF;
    }
}
