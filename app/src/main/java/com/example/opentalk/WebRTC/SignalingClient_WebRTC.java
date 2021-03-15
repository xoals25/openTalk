package com.example.opentalk.WebRTC;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.opentalk.BitmapConverter;
import com.example.opentalk.Socket_my.SocketSSL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by Taemin on 2021/1/13.
 *
 * Face Group Communication socket
 *
 */

public class SignalingClient_WebRTC {
    private String TAG ="SignalingClient";
    private static final String STREAM_HOST = "https://3.36.188.116:8080";
//    private SignalingClient instance;
    private IO.Options options = new IO.Options();
    public Socket socket;
    public int room;
    public String user_name;
    public Callback callback;

    public Context context;

    public SignalingClient_WebRTC(int room, String user_name){
        this.room = room;
        this.user_name = user_name;
    }
//    public SignalingClient get() {
////        if(instance == null) {
////            synchronized (SignalingClient.class) {
////                if(instance == null) {
////                    instance = new SignalingClient();
////                }
////            }
////        }
//        return instance;
//    }



    private final TrustManager[] trustAll = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }
    };

    public void init(Callback callback) {
        this.callback = callback;
        try {
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//            sslContext.init(null, trustAll, null);
//            IO.setDefaultHostnameVerifier((hostname, session) -> true);
//            IO.setDefaultSSLContext(sslContext);
            SocketSSL.set(options);
            socket = IO.socket(STREAM_HOST);
            socket.connect();

            socket.emit("create or join", room,user_name);

            socket.on("created", args -> {
                //args[0] : 방번호, [1] : 유저이름 ,[2] : 방에입장한 사람의 socketId(여기선 내자신의 socketId)
                Log.e("chao", "room created:" + socket.id());
                String username = String.valueOf(args[1]);
                callback.onCreateRoom(username);
            });
            socket.on("full", args -> {
                Log.e("chao", "room full");
            });
            socket.on("join", args -> {
                //args[0] : 방번호, [1] : 유저이름 ,[2] : 방에입장한 사람의 socketId(여기선 내자신의 socketId)
                Log.e("chao", "peer joined " + Arrays.toString(args));
                callback.onPeerJoined(String.valueOf(args[1]),String.valueOf(args[2]));
            });
            socket.on("joined", args -> {
                //args[0] : 방번호, [1] : 유저이름 ,[2] : 방에입장한 사람의 socketId(여기선 내자신의 socketId)
                Log.e("chao", "self joined:" + socket.id());
                String username = String.valueOf(args[1]);
                callback.onSelfJoined(username);
            });
            socket.on("log", args -> {
                Log.e("chao", "log call " + Arrays.toString(args));
            });
            socket.on("bye", args -> {
                Log.e("chao", "bye " + Arrays.toString(args));
                Object arg = args[0];
                if(arg instanceof String) {
                } else if(arg instanceof JSONObject) {
                    JSONObject data = (JSONObject) arg;
                    String type = data.optString("type");
                    callback.onPeerLeave(data);
                }

            });
            socket.on("message", args -> {
                Log.e("chao", "message " + Arrays.toString(args));
                Object arg = args[0];
                if(arg instanceof String) {

                } else if(arg instanceof JSONObject) {
                    JSONObject data = (JSONObject) arg;
                    String type = data.optString("type");
                    if("offer".equals(type)) {
                        callback.onOfferReceived(data);
                    } else if("answer".equals(type)) {
                        callback.onAnswerReceived(data);
                    } else if("candidate".equals(type)) {
                        callback.onIceCandidateReceived(data);
                    }
                }
            });
            socket.on("chat",args ->{
                //args[0] : 채팅한사람이 보낸 모든 정보들이 있다.(room,user_name,message)
                Object arg = args[0];
                JSONObject data = (JSONObject) arg;
                callback.onChat(data);
            });
            socket.on("participant",args ->{
                Object arg = args[0];
                if(arg instanceof JSONObject) {
                    JSONObject data_jsonObject = (JSONObject) arg;
                    callback.onParticipantObject(data_jsonObject);
                } else if(arg instanceof JSONArray) {
                    JSONArray data_jsonArray = (JSONArray) arg;
                    callback.onParticipantArray(data_jsonArray);
                }
            });
            socket.on("user_kick",args -> {
                Object arg = args[0];
                JSONObject data = (JSONObject) arg;
                callback.onUserKick(data);
            });
            socket.on("img",args->{
                //args[0] : 보낸 유저이름, [1] : img (string)
                String username = String.valueOf(args[0]);
                Bitmap bitmap = BitmapConverter.StringToBitmap(String.valueOf(args[1]));
                callback.onImgreceive(username,bitmap);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("room", room);
            jo.put("username", user_name);
            jo.put("socketId",socket.id());
        }catch (Exception e){
        }
        socket.emit("bye",jo);
        socket.disconnect();
        socket.close();
//        instance = null;
    }

    public void sendIceCandidate(IceCandidate iceCandidate, String to) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("type", "candidate");
            jo.put("label", iceCandidate.sdpMLineIndex);
            jo.put("id", iceCandidate.sdpMid);
            jo.put("candidate", iceCandidate.sdp);
            jo.put("from", socket.id());
            jo.put("to", to);
            Log.d(TAG, "sendIceCandidate: 순서확인");
            socket.emit("message", jo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendSessionDescription(SessionDescription sdp, String to) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("type", sdp.type.canonicalForm());
            jo.put("sdp", sdp.description);
            jo.put("from", socket.id());
            jo.put("to", to);
            Log.d(TAG, "sendSessionDescription: 순서확인 sdp.type.canonicalForm():"+sdp.type.canonicalForm());
            socket.emit("message", jo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMesageChat(String message){
        JSONObject data = new JSONObject();
        try {
            data.put("room", room); //채팅 보낼 방번호
            data.put("user_name", user_name); //채팅 작성한 유저
            data.put("message", message); //보낼 채팅 내용
            socket.emit("chat", data);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void sendImg(String imgfrombitmap){
        socket.emit("img",room,user_name,imgfrombitmap);
    }
    public void sendUserKickLeave(int room){
        socket.emit("user_kick_leave",room);
    }

    public interface Callback {
        void onCreateRoom(String username);
        void onPeerJoined(String username,String socketId);
        void onSelfJoined(String username);
        void onPeerLeave(JSONObject data);
        void onOfferReceived(JSONObject data);
        void onAnswerReceived(JSONObject data);
        void onIceCandidateReceived(JSONObject data);
        void onChat(JSONObject data);
        void onParticipantObject(JSONObject data);
        void onParticipantArray(JSONArray data_array);
        void onUserKick(JSONObject data);
        void onImgreceive(String username,Bitmap bitmap);
    }

}