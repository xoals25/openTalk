package com.example.opentalk;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.opentalk.Code.HandlerType_Code;
import com.example.opentalk.Data.FriendWaitData;
import com.example.opentalk.Handler.Friend_wait_Handler;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class UrlToBitmapThread extends Thread{

    ArrayList<FriendWaitData> request_email_arrayList;
    Friend_wait_Handler friend_wait_handler;

    public UrlToBitmapThread(ArrayList<FriendWaitData> request_email_arrayList,Friend_wait_Handler friend_wait_handler){
        this.request_email_arrayList =request_email_arrayList;
        this.friend_wait_handler = friend_wait_handler;
    }

    @Override
    public void run() {
        super.run();
            for (int i=0; i<request_email_arrayList.size(); i++){
                try {
                    Bitmap bitmap=null;
                    URL url = new URL(ServerIp.IP_ADDRESS_IMG_URL+request_email_arrayList.get(i).getImgpath());
                    InputStream in = null;
                    in = url.openStream();
                    byte[] buffer = new byte[128];
                    int readCount = 0;
                    StringBuffer result = new StringBuffer();

                    while ((readCount = in.read(buffer)) != -1) {
                        String part = new String(buffer, 0, readCount);
                        result.append(part);
                    }
                    bitmap = BitmapConverter.StringToBitmap(result.toString());
                    request_email_arrayList.get(i).setBitmap(bitmap);
                } catch (Exception e) {
                }
            }
        friend_wait_handler.sendEmptyMessage(HandlerType_Code.HandlerType.FRIEND_WAIT_RECYCLERVIEW_CHANGE);
    }
}
