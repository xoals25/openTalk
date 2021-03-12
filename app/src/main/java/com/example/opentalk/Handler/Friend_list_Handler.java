package com.example.opentalk.Handler;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.opentalk.Adapter.Adapter_Friend_List;
import com.example.opentalk.Code.HandlerType_Code;

public class Friend_list_Handler extends Handler {

    String TAG = "Friend_list_Handler";
    Adapter_Friend_List adapter_friend_list;

    public Friend_list_Handler(Adapter_Friend_List adapter_friend_list){
        this.adapter_friend_list = adapter_friend_list;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        if (msg.what == HandlerType_Code.HandlerType.FRIEND_LIST_RECYCLERVIEW){
            Log.d(TAG, "handleMessage: friend_list handler들어옴");
            adapter_friend_list.notifyDataSetChanged();
        }
        else if(msg.what == HandlerType_Code.HandlerType.FRIEND_LIST_RECYCLERVIEW_CHANGE_ONE){
            adapter_friend_list.notifyItemChanged(msg.arg1);
        }
    }
}
