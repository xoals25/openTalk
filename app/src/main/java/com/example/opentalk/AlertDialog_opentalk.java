package com.example.opentalk;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.example.opentalk.Activity.Activity_Lobby;
import com.example.opentalk.Http.HttpConnection_room_enter;

public class AlertDialog_opentalk {
    String TAG ="AlertDialog_opentalk";
    public Context context;
    public String pwd;
    public AlertDialog.Builder alertDialog;
    /*httpConnection관련*/
    HttpConnection_room_enter httpConnection_room_enter;
    String IP_ADDRESS; //서버와 통신할때 Activity_Lobby에서 어떤 서버와 통신할지 알려주는 받아오는 값
    int adapter_room_id; //마찬가지로 Activity_Lobby에서 받아오는 방 고유 번호
    String adapter_room_public; //마찬가지로 공개방인지,비공개방인지 받아오는 값

    /*Activity_Lobby에서 필요한 것들 담아오기*/
    //순서대로 1.연결할 클래스 2.통신할 클래스(연결할 클래스에서 선언한 것) 3.통신할 주소 4.클릭한 방 고유 번호 5.클릭한 방 공개방인지 비공개방인지.
    public AlertDialog_opentalk(Context context, HttpConnection_room_enter httpConnection_room_enter, String IP_ADDRESS, int adapter_room_id, String adapter_room_public) {
        this.context = context;
        this.httpConnection_room_enter = httpConnection_room_enter;
        this.IP_ADDRESS = IP_ADDRESS;
        this.adapter_room_id = adapter_room_id;
        this.adapter_room_public = adapter_room_public;
    }

    public void dialog(){
        //다이얼로그 띄어서 비번입력하고 입려값 담아오기
        alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle("비밀번호방");       // 제목 설정
        alertDialog.setMessage("비밀번호를 입력해주세요.");   // 내용 설정

        // EditText 삽입하기
        final EditText edittext = new EditText(context);
        /*비밀번호 메소드 재설정(마지막 비밀번호 입력값도 숨겨주는 것으로 변경)*/
        edittext.setTransformationMethod(new CustomPwdMethod.CustomPasswordTransformationMethod());
        edittext.setTextColor(Color.BLACK);
        alertDialog.setView(edittext);


        // 확인 버튼 설정
        alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v(TAG, "Yes Btn Click");
                // Text 값 받아서 로그 남기기
                pwd = edittext.getText().toString();
                Log.v(TAG, pwd);
                dialog.dismiss();     //닫기
                /*비번방에 들어갈때 서버와 통신하기*/
                httpConnection_room_enter.execute("http://"+IP_ADDRESS+"/room_enter.php",String.valueOf(adapter_room_id),adapter_room_public,pwd);
            }
        });
        // 취소 버튼 설정
        alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v(TAG,"No Btn Click");
                dialog.dismiss();     //닫기
                // Event
            }
        });
    }

}
