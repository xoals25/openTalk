package com.example.opentalk.Handler;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.opentalk.Adapter.Adapter_Friend_List;
import com.example.opentalk.Adapter.Adapter_voice_participant_list;
import com.example.opentalk.Code.HandlerType_Code;

public class Voice_Chat_Handler extends Handler {

    String TAG = "Chat_Handler";
    Button room_voice_input_btn;
    Adapter_voice_participant_list adapter_voice_participant_list;

    public Voice_Chat_Handler(Button room_voice_input_btn,Adapter_voice_participant_list adapter_voice_participant_list ) {
        this.room_voice_input_btn = room_voice_input_btn;
        this.adapter_voice_participant_list = adapter_voice_participant_list;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        //버튼 비활성화
        if (msg.what == HandlerType_Code.HandlerType.CHAT_BTN_CLOSE) {
            room_voice_input_btn.setEnabled(false);
        }
        else if (msg.what == HandlerType_Code.HandlerType.CHAT_BTN_OPEN) {
            room_voice_input_btn.setEnabled(true);
        }
        else if(msg.what == HandlerType_Code.HandlerType.ADAPTER_VOICE_PARICIPANT_LIST_NOTIFYDATASETCHANGED){
            adapter_voice_participant_list.notifyDataSetChanged();
        }
    }
}