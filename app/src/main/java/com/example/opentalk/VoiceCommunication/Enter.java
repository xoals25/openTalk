package com.example.opentalk.VoiceCommunication;


import android.content.Context;
import android.util.Log;

import com.example.opentalk.VoiceCommunication.Data.OtherUserIP_Data;
import com.example.opentalk.VoiceCommunication.HolePunching.HolePunchGOGOFIRST;
import com.example.opentalk.VoiceCommunication.HolePunching.HolePunchGOGOTWO;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

/****
 *
 * 12 - 03 수정 내용
 *  ArrayList<String> first_holepunch_array = new ArrayList<>();
 *  first_holepunch_array에 보낸 패킷으로 값을 받아서
 *  String array_KEY = msg_ip+"-"+port+"-"; 로 값을 넣어줘서 홀펀칭으로 접근할 때 중복 방지 해줬는데
 *  이부분을 해당 유저의 String array_KEY = email_id로 값을 넣어줘서 중복 방지해주는 것으로 변경해줬다.
 *
 * ****/
public class Enter extends Thread {

    String TAG = "Enter";
    DatagramPacket udp_packet;
    DatagramSocket udp_socket;
    DatagramSocket voice_socket;
    DatagramPacket d_packet_receive;
    public int port = 8088;
    public String IP_ADDRESS = "3.36.188.116";
    String inetAddress;
//    ArrayList<OtherUserIP_Data> otherUserPublicIPArrayList;
//    ArrayList<OtherUserIP_Data> otherUserPrivateIPArrayList;
    ArrayList<String> first_holepunch_array = new ArrayList<>();

    String room_num;
    String email_id;

    boolean enterONOFF = true;


    public Enter(DatagramSocket udp_socket, DatagramSocket voice_socket,
                 String inetAddress,/* ArrayList<OtherUserIP_Data> otherUserPrivateIPArrayList,
                 ArrayList<OtherUserIP_Data> otherUserPublicIPArrayList,*/
                 String room_num, String email_id){
        this.udp_socket = udp_socket;
        this.voice_socket = voice_socket;
        this.inetAddress = inetAddress;
//        this.otherUserPrivateIPArrayList  = otherUserPrivateIPArrayList;
//        this.otherUserPublicIPArrayList  = otherUserPublicIPArrayList;

        this.room_num = room_num;
        this.email_id = email_id;
    }
    @Override
    public void run() {
        super.run();

        byte[] receive_buffer =  new byte[1024];
        byte[] json_buf;
        String my_ip;
        try {
            /**
             * 사설 ip 보내는거는 잠시 주석처리 ->우선 공인 ip처리먼저해보기 (사설 ip와 포트를 모르겠다...)
             * */



            int my_port = udp_socket.getLocalPort();
            my_ip = inetAddress+"-"+my_port;
            Log.d(TAG, "run: udp_socket.getlocalport : "+udp_socket.getLocalPort());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type","입장");
            jsonObject.put("room_num",room_num);
            jsonObject.put("email_id",email_id);
            jsonObject.put("privateIp",my_ip);
            String json_string = jsonObject.toString();
            json_buf = json_string.getBytes();
            udp_packet = new DatagramPacket(json_buf,json_buf.length,InetAddress.getByName(IP_ADDRESS),port);
            udp_socket.send(udp_packet);

//            ((MainActivity)MainActivity.mContext).server_udp_socket = new DatagramSocket(8888);

            Log.d(TAG, "run: udp_socket.getlocalport : "+udp_socket.getLocalPort());

            while (enterONOFF){

                d_packet_receive = new DatagramPacket(receive_buffer, receive_buffer.length);
                udp_socket.receive(d_packet_receive);
                String msg = new String(d_packet_receive.getData(),0,d_packet_receive.getLength());
                Log.d(TAG, "run: msg : "+msg);
                if(d_packet_receive.getPort()==8088){
                    JSONObject jsonObject1 = new JSONObject(msg);
                    String type = jsonObject1.getString("type");
                    if(type.equals("응답")){
                        String publicIP = jsonObject1.getString("public_IP");
                        String privateIP = jsonObject1.getString("private_IP");
                        Log.d(TAG, "publicIP : "+publicIP);
                        Log.d(TAG, "privateIP : "+privateIP);
                        //public ip 가공해서 담아주기
                        String[] split_PRIVATE_IP = privateIP.split("-");
                        InetAddress private_InetAddress = InetAddress.getByName(split_PRIVATE_IP[0]);
                        int private_port = Integer.valueOf(split_PRIVATE_IP[1]);

                        String[] split_PUBLIC_IP = publicIP.split("-");
                        InetAddress public_InetAddress = InetAddress.getByName(split_PUBLIC_IP[0]);
                        int public_port = Integer.valueOf(split_PUBLIC_IP[1]);

                        /*어레이리스트에 넣어주지말고 스레드에 인자값 otherUserIP(privateIp,privatePort,publicIp,publicPort)를 넣어줘서 들어온것만 돌리게해주자.*/
                        OtherUserIP_Data otherUserIPData = new OtherUserIP_Data(private_InetAddress,private_port,public_InetAddress,public_port);
                        HolePunchGOGOFIRST holePunchGOGOFIRST = new HolePunchGOGOFIRST(udp_socket, otherUserIPData,"홀펀칭첫번째",voice_socket,inetAddress,email_id);
                        holePunchGOGOFIRST.start();
                        Log.d(TAG, "홀펀칭 성공 확인 - 첫번째 홀펀칭 시작");
                    }
                    else if(type.equals("퇴장한유저")){
                        String exit_user_email_id = jsonObject1.getString("email_id");
                        Log.d(TAG, "run: exit_user_email_id : "+exit_user_email_id);
                        first_holepunch_array.remove(exit_user_email_id);
                    }
                }
                else{

                    //자신이 음성통신할 socket을 알려주어라.(그냥 음성통신에 이용할 소켓으로 데이터를 온곳으로 보내주면된다.)
                    //->어 그러면 상대방이 내가 보낸 소켓 데이터를 모르기때문에 문제가 생긴다.
                    JSONObject receive_data_json = new JSONObject(msg);
                    String type = receive_data_json.getString("type");

                    if(type.contains("홀펀칭첫번째")){
                        Log.d(TAG, "홀펀칭 테스트중입니다. 첫번째 홀펀칭");
                        //첫 홀펀칭 성공할경우 여기로 들어온다.
                        InetAddress msg_ip = d_packet_receive.getAddress();
                        int port = d_packet_receive.getPort();

                        String email_id_hole_first = receive_data_json.getString("email_id");

                        /***홀펀칭 성공할때 응답을 한번만해주기위해
                         *
                         * 1.홀펀칭으로 들어온 ip,port를 합쳐서 밑에 msg_ip+"-"+port+"-"; 형식으로 arraylist에 저장
                         * 2.ip,port합친게 arraylist에 없다면 다음 2차 홀펀칭 시도해주기
                         * 3.합친게 있다면 그냥 무시하고 패스
                         *
                         * 그리고 2차 홀펀칭 마지막 단계에 첫번째 소통했던 소켓의 private_ip,port와 public_ip,port를 담아서 보내주기
                         * 그럼 상대방이 자신의 arraylist에서 완료한것은 삭제해주기
                         * ->추후에 오류 방지
                         *
                         * 수정!!!!!!!!
                         * ip+"-"+port로 하지말고 추후에는 email_id로 arraylist에 저장하자.
                         * 그럼 나중에도 지우기 쉽잖앙~~~ 해결스!
                         *
                         * ***/
                        String array_KEY = email_id_hole_first;
                        Log.d(TAG, "홀펀칭 성공 확인 - 첫번째 성공");

                        // 첫번째 B에게 받은 B의 voice소켓에 A의 voice소켓으로 통신 시도(라우터에 저장하기)
                        // 첫번째 A->B에게 A의 voice소켓 정보를 넘겨주기
                        // 이야기로 쉽게 홀펀칭 첫번째 도착했어
                        // 너에게 온 아이디는 없으니까 한번만 받아줄게
                        // 내 voice_socket에게도 문열어줘 문앞에 있어 voice_socket의 정보 보내줄테니까 얘랑 통신해 다른애들은 신경쓰지말고
                        if (!first_holepunch_array.contains(array_KEY)){
                            first_holepunch_array.add(array_KEY);
                            byte[] buf;
                            /*이부분은 굳이 json으로 안하는이유는
                            * A의 voice_socket -> B의 voice_socket으로 정보를 넘기는 것이기 때문에
                            * 이 클래스에서는 udp_socket으로 받는곳이다..
                            * */
                            String aproach_router ="홀펀칭두번째";
                            buf = aproach_router.getBytes();

                            String other_user_privateIP = receive_data_json.getString("private_IP");
                            String[] split_IP = other_user_privateIP.split("-");
                            //udp_socket을 통해 받은 voice_socket private ip와 port
                            InetAddress private_inetAddress = InetAddress.getByName(split_IP[0]);
                            int private_port = Integer.valueOf(split_IP[1]);
                            udp_packet = new DatagramPacket(buf,buf.length,private_inetAddress,private_port);
                            voice_socket.send(udp_packet);

                            //udp_socket으로 받았으니 ip는 udp_socket을 사용하면 voice_socket과 ip는 같고 port만 json으로 전달받은 voice_socket port만 받으면된다.
                            InetAddress public_inetAddress = d_packet_receive.getAddress();
                            int public_port = receive_data_json.getInt("public_PORT");
                            udp_packet = new DatagramPacket(buf,buf.length,public_inetAddress,public_port);
                            voice_socket.send(udp_packet);


                            //홀펀칭으로 들어왔으니 내가 통신할 socket ip와 port privateIP 보내주기
                            String voice_socket_PRIVATE_IP = inetAddress+"-"+voice_socket.getLocalPort();
                            //이건 if(type.contains("voiceSocketIP")) 이부분에서 voice_socket port를 voice_socket port를 사용하지않고 udp_socket port를 사용하니까 public port만 따로 보내줌
                            int voice_socket_PUBLIC_PORT = voice_socket.getLocalPort();
                            JSONObject voice_socket_ip_json = new JSONObject();
                            voice_socket_ip_json.put("type","voiceSocketIP"+my_ip);
                            voice_socket_ip_json.put("private_IP",voice_socket_PRIVATE_IP);
                            voice_socket_ip_json.put("public_PORT",voice_socket_PUBLIC_PORT);
                            //buf에다 json 정보 다시 담아주기기
                            buf = voice_socket_ip_json.toString().getBytes();
                            //다시 홀펀칭에 성공한아이로 voice_socket 데이터를 보내주기
                            udp_packet = new DatagramPacket(buf,buf.length,msg_ip,port);
                            udp_socket.send(udp_packet);
                            Log.d(TAG, "홀펀칭 성공 확인 : 상대에게 voice_socket정보 전달 : voice_and_udp_socket_port : "+voice_socket.getLocalPort()+" and "+udp_socket.getLocalPort());


                        }
                    }
                    //첫번째에서 A가 건내준 정보를 받은 B
                    //B->A의 voice_socket을 뚫기 위함.

                    //voice_socket정보 받았어
                    //너의 voice_socket과 통신할 수 있도록 내 아이디 email_id에 넣어서 메세지 보낼게 받아줘 voice_socket한테 저장하라 알려줘
                    //->audioReceiveThread로 넘어간다.
                     if(type.contains("voiceSocketIP")){
                        Log.d(TAG, "홀펀칭 성공 확인 - voiceSocketIP 1 type : "+type);
                        String other_user_privateIP = receive_data_json.getString("private_IP");
                        String[] split_IP = other_user_privateIP.split("-");
                        //udp_socket을 통해 받은 voice_socket private ip와 port
                        InetAddress private_inetAddress = InetAddress.getByName(split_IP[0]);
                        int private_port = Integer.valueOf(split_IP[1]);

                        //udp_socket으로 받았으니 ip는 udp_socket을 사용하면 voice_socket과 ip는 같고 port만 json으로 전달받은 voice_socket port만 받으면된다.
                        InetAddress public_inetAddress = d_packet_receive.getAddress();
                        int public_port = receive_data_json.getInt("public_PORT");
                        //받은 ip public_ip,private ip 데이터에 담아주기 ->이건 arraylist에 담아주는게 좋겠지?왜냐면 여러개가 올 수 있으니까....
                        //아니면 hashmap에다가 넣어주든가..
                        OtherUserIP_Data otherUserIPData = new OtherUserIP_Data(private_inetAddress,private_port,public_inetAddress,public_port);
                        //voice전용 socket을 전달해주고 holepunching시도
                        HolePunchGOGOTWO holePunchGOGOTWO = new HolePunchGOGOTWO(voice_socket, otherUserIPData,"홀펀칭두번째",email_id);
                        holePunchGOGOTWO.start();
                        Log.d(TAG, "홀펀칭 성공 확인 - 두번째 홀펀칭 시작");
                    }
                }
            }
        }catch (Exception e){
            Log.d(TAG, "run: e.message : "+e.getMessage());
        }
    }

    //음성 채팅방 나갈때 off해줄것
    public void setEnterONOFF(boolean enterONOFF){
        this.enterONOFF = enterONOFF;
    }
}
