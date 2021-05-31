package com.example.opentalk.NotificationAndFCM;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.opentalk.Activity.Activity_Friend_Chat;
import com.example.opentalk.Activity.Activity_Friend_List;
import com.example.opentalk.Activity.Activity_Lobby;
import com.example.opentalk.BitmapConverter;
import com.example.opentalk.ServerIp;
import com.example.opentalk.SharedPreference.PreferenceManager_member;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FcmService extends FirebaseMessagingService {

    String TAG = "FcmService";
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, "onNewToken: token : "+s);
        //토큰 처음 생길 때 처음 저장 하고 계속 갖고있기
        PreferenceManager_member.setString(this,"fcmtoken",s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("TAG", "onMessageReceived: ");

        Activity_Friend_Chat context = ((Activity_Friend_Chat) Activity_Friend_Chat.Activity_Friend_Chat_context);
        String friendchat_roomnum = remoteMessage.getData().get("roomnum");
        String msg_send_id = remoteMessage.getData().get("fromid");
        String msg_send_nickname = remoteMessage.getData().get("fromnickname");
        String msg = remoteMessage.getData().get("msg");
        String imgpath = remoteMessage.getData().get("profileimg");
        Bitmap userimgToBitmap = null;
        Log.d(TAG, "onMessageReceived: imgpath:"+imgpath);
        /*url로 데이터 문자열 읽어오기...다음부터는 파일로하자 제발....... 혹은 다음엔 sql문 있을때 거기서 가져오자...*/
        if(!imgpath.equals("없음")){
            try {
                URL url = new URL(ServerIp.IP_ADDRESS_IMG_URL+imgpath);
                Log.d(TAG, "onMessageReceived: url : "+url.getPath());
                Log.d(TAG, "onMessageReceived: 확인작업업(notifi imgpath) imgpath : "+imgpath);
                InputStream in = null;
                in = url.openStream();
                Log.d(TAG, "onMessageReceived: 확인작업업(norifi) 2 ");
                byte[] buffer = new byte[128];
                int readCount = 0;
                StringBuffer result = new StringBuffer();

                while ((readCount = in.read(buffer)) != -1) {
                    String part = new String(buffer, 0, readCount);
                    result.append(part);
                }
                userimgToBitmap = BitmapConverter.StringToBitmap(result.toString());
            } catch (Exception e) {
                Log.d(TAG, "onMessageReceived: ERROR MESSAGE : "+e.getMessage());
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("aa hh:mm");

        Date date = new Date();
        String senddate = sdf.format(date);
        if (context != null) {
            if (!context.roomnum.equals(friendchat_roomnum)) {
                //알림 메세지
                NotificationCollect.showCustomLayoutNotification(this, senddate, friendchat_roomnum, msg_send_id, msg_send_nickname, msg, userimgToBitmap, true);
            }
        } else {
            Activity_Lobby activityLobbycontext = ((Activity_Lobby) Activity_Lobby.mainContext);
            if (activityLobbycontext != null) {
                //포그라운드 알림 메세지
                NotificationCollect.showCustomLayoutNotification(this, senddate, friendchat_roomnum, msg_send_id, msg_send_nickname, msg, userimgToBitmap, true);
            } else {
                //백그라운드 알림 메세지
                NotificationCollect.showCustomLayoutNotification(this, senddate, friendchat_roomnum, msg_send_id, msg_send_nickname, msg, userimgToBitmap, false);
            }
        }
    }
}
