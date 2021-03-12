package com.example.opentalk.Handler;

import android.os.Handler;
import android.os.Message;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.opentalk.Adapter.Adapter_face_participant_list;
import com.example.opentalk.Adapter.Adapter_voice_participant_list;
import com.example.opentalk.Code.HandlerType_Code;

public class Face_Chat_Handler extends Handler {

    String TAG = "Chat_Handler";
    Adapter_face_participant_list adapter_face_participant_list;

    public Face_Chat_Handler(Adapter_face_participant_list adapter_face_participant_list ) {
      this.adapter_face_participant_list = adapter_face_participant_list;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        //버튼 비활성화
       if(msg.what == HandlerType_Code.HandlerType.ADAPTER_FACE_PARICIPANT_LIST_NOTIFYDATASETCHANGED){
           adapter_face_participant_list.notifyItemChanged(msg.arg1);
           adapter_face_participant_list.notifyDataSetChanged();
        }
    }
}