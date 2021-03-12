package com.example.opentalk.Handler;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.opentalk.Adapter.Adapter_Friend_Wait;
import com.example.opentalk.Code.HandlerType_Code;
import com.example.opentalk.Data.FriendWaitData;

import java.util.ArrayList;

public class Friend_wait_Handler extends Handler {
    String TAG = "Friend_wait_Handler";
    ArrayList<FriendWaitData> request_email_arrayList;
    Adapter_Friend_Wait adapter_friend_wait;
    Context mContext;

    public Friend_wait_Handler(ArrayList<FriendWaitData> request_email_arrayList,Adapter_Friend_Wait adapter_friend_wait,Context mContext){
        this.request_email_arrayList = request_email_arrayList;
        this.adapter_friend_wait = adapter_friend_wait;
        this.mContext = mContext;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        if(msg.what == HandlerType_Code.HandlerType.FRIEND_WAIT_RECYCLERVIEW){
            adapter_friend_wait.notifyDataSetChanged();
        }
        else if(msg.what== HandlerType_Code.HandlerType.FRIEND_WAIT_REMOVE_RECYCLERVIEW){
            adapter_friend_wait.notifyItemRemoved(msg.arg1);
//            adapter_friend_wait.notifyDataSetChanged();
        }
        else if(msg.what == HandlerType_Code.HandlerType.FRIEND_ADD_REQUEST_MYSELF_FAILURE){
            Log.d(TAG, "handleMessage: FRIEND_ADD_REQUEST_MYSELF_FAILURE");
            Toast.makeText(mContext,"본인 아이디는 친구 요청할 수 없습니다.",Toast.LENGTH_SHORT).show();
        }
        else if(msg.what == HandlerType_Code.HandlerType.FRIEND_ADD_REQUEST_NOEXIST_FAILURE){
            Log.d(TAG, "handleMessage: FRIEND_ADD_REQUEST_NOEXIST_FAILURE");
            Toast.makeText(mContext,"가입되지 않은 아이디입니다.",Toast.LENGTH_SHORT).show();
        }
        else if(msg.what == HandlerType_Code.HandlerType.FRIEND_ADD_REQUEST_ALEADY_FRIEND){
            Toast.makeText(mContext,"이미 친구관계입니다.",Toast.LENGTH_SHORT).show();
        }
        else if(msg.what == HandlerType_Code.HandlerType.FRIEND_ADD_REQUEST_AWAITING){
            Toast.makeText(mContext,"이미 친구요청중입니다.",Toast.LENGTH_SHORT).show();
        }

        if(msg.what == HandlerType_Code.HandlerType.FRIEND_WAIT_RECYCLERVIEW_CHANGE){
            adapter_friend_wait.notifyDataSetChanged();
        }
    }
}
