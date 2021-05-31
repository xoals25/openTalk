package com.example.opentalk.Http;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.opentalk.Code.HandlerType_Code;
import com.example.opentalk.Data.FaceChatRoom_Participant_List_Data;
import com.example.opentalk.Data.VoiceChatRoom_Participant_List_Data;
import com.example.opentalk.Handler.Face_Chat_Handler;
import com.example.opentalk.Handler.Voice_Chat_Handler;
import com.example.opentalk.VolleyRequestQhelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VolleyprofileIMG_Participant_List_Face {
    String TAG="VolleyprofileIMG_Participant_List";
    ArrayList<FaceChatRoom_Participant_List_Data> participant_list_arrayList;
    Face_Chat_Handler face_chat_handler;
    int position;
    //이건 원래 email로 하는게 맞는거 같다 해당 nickname으로 이미지를 받아오게 된다면 추후에 nickname을 변경할때 코드가 복잡해질거같다.
    //그래서 변경할 수 없는 email로 해당 이미지를 가져오는게 맞는거같다.
    String user_nickname;
    int adapter_changeItem_position = 0;
    public VolleyprofileIMG_Participant_List_Face(ArrayList<FaceChatRoom_Participant_List_Data> participant_list_arrayList, Face_Chat_Handler face_chat_handler, int position, String user_nickname){
        this.participant_list_arrayList = participant_list_arrayList;
        this.face_chat_handler = face_chat_handler;
        this.position =position;
        this.user_nickname = user_nickname;
    }

    //for문을 돌릴것 posi
    public void profile_upload(String serverurl){

        Log.d(TAG, "profile_upload: volley 메소드 접근");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, serverurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("NULL")){
                    Log.d(TAG, "onResponse: NULL");
                    participant_list_arrayList.get(position).setProfile_img("없음");
                }
                else{
                    Log.d(TAG, "onResponse: NULL아님");
                    participant_list_arrayList.get(position).setProfile_img(response);
                }
                Message message = new Message();
                message.what=HandlerType_Code.HandlerType.ADAPTER_FACE_PARICIPANT_LIST_NOTIFYDATASETCHANGED;
                message.arg1=adapter_changeItem_position;
                face_chat_handler.sendMessage(message);
                adapter_changeItem_position +=1;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: volleyError : "+error.toString());
            }
        }
        ){
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_nickname",user_nickname);
                return params;
            }
        };
        VolleyRequestQhelper.requestQueue.add(stringRequest);
    }

}
