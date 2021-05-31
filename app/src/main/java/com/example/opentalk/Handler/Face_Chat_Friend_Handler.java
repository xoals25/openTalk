package com.example.opentalk.Handler;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.opentalk.Activity.Activity_Friend_Chat;
import com.example.opentalk.Adapter.Adapter_Chat_Friend;
import com.example.opentalk.Adapter.Adapter_face_participant_list;
import com.example.opentalk.Code.HandlerType_Code;

public class Face_Chat_Friend_Handler extends Handler {

    String TAG = "Chat_Handler";
    Adapter_Chat_Friend adapter_chat_friend;
    RecyclerView friend_chat_recyclerview;
    public Face_Chat_Friend_Handler(Adapter_Chat_Friend adapter_chat_friend,RecyclerView friend_chat_recyclerview) {
      this.adapter_chat_friend = adapter_chat_friend;
      this.friend_chat_recyclerview =friend_chat_recyclerview;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
       if(msg.what == HandlerType_Code.HandlerType.ADAPTER_FRIEND_CHAT_LIST_NOTIFYDATASETCHANGED_ONE){
//           adapter_chat_friend.notifyDataSetChanged();
//           adapter_chat_friend.notifyItemChanged(((Activity_Friend_Chat)Activity_Friend_Chat.Activity_Friend_Chat_context).chat_msg_data_ArrayList.size()-1);

           adapter_chat_friend.notifyDataSetChanged();
           friend_chat_recyclerview.scrollToPosition(adapter_chat_friend.getItemCount() - 1);
        }
       else if(msg.what == HandlerType_Code.HandlerType.ADAPTER_FRIEND_CHAT_LIST_NOTIFYDATASETCHANGED_READ_SETTING){
           adapter_chat_friend.notifyItemChanged(msg.arg1);
       }
       else if(msg.what == HandlerType_Code.HandlerType.ADAPTER_FRIEND_CHAT_LIST_NOTIFYDATASETCHANGED_READ_ALL){
           adapter_chat_friend.notifyDataSetChanged();
       }
    }
}