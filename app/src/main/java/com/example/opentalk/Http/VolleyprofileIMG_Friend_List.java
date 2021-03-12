package com.example.opentalk.Http;

import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.opentalk.BitmapConverter;
import com.example.opentalk.Code.HandlerType_Code;
import com.example.opentalk.Data.Friend_List_Data;
import com.example.opentalk.Handler.Friend_list_Handler;
import com.example.opentalk.VolleyRequestQhelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VolleyprofileIMG_Friend_List {

    String TAG="VolleyprofileIMG";
    ArrayList<Friend_List_Data> friend_list_dataArrayList;
    String email_id;
    int position;
    Friend_list_Handler friend_list_handler;
    int num =0;
    public VolleyprofileIMG_Friend_List(ArrayList<Friend_List_Data> friend_list_dataArrayList, Friend_list_Handler friend_list_handler, String email_id, int position){
        this.friend_list_dataArrayList = friend_list_dataArrayList;
        this.friend_list_handler = friend_list_handler;
        this.email_id = email_id;
        this.position = position;
    }

//    public static RequestQueue requestQueue;

    //for문을 돌릴것 posi
    public void profile_upload(String serverurl){

        Log.d(TAG, "profile_upload: volley 메소드 접근");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, serverurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                num +=1;
                if(response.equals("NULL")){
                    Log.d(TAG, "onResponse: NULL");
                    friend_list_dataArrayList.get(position).setImgstring_tobitmap("없음");
                }
                else{
                    Log.d(TAG, "onResponse: NULL아님");
                    friend_list_dataArrayList.get(position).setImgstring_tobitmap(response);
                }

                    //리사이클러뷰 아이템 리셋 해주는 핸들러
                    friend_list_handler.sendEmptyMessage(HandlerType_Code.HandlerType.FRIEND_LIST_RECYCLERVIEW);
                    Log.d(TAG, "onResponse: 핸들러 작동~!~!!");

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
                params.put("email_id",email_id);
                return params;
            }
        };
        VolleyRequestQhelper.requestQueue.add(stringRequest);
    }


}
