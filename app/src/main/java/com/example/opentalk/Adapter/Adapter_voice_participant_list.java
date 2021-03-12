package com.example.opentalk.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.opentalk.Activity.Activity_Room_Voice;
import com.example.opentalk.BitmapConverter;
import com.example.opentalk.Data.VoiceChatRoom_Participant_List_Data;
import com.example.opentalk.R;


import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.Socket;

public class Adapter_voice_participant_list extends RecyclerView.Adapter<Adapter_voice_participant_list.CustomViewHolder> {

    String TAG = "Adapter_voice_participant_list";

    ArrayList<VoiceChatRoom_Participant_List_Data> participant_list_data_arraylist;
    String mynickname;
    Socket nodejs_socket;
    Context context;
    String kick_user;
    int room_num;

    public Adapter_voice_participant_list(ArrayList<VoiceChatRoom_Participant_List_Data> participant_list_data_arraylist,String mynickname,Socket nodejs_socket,Context context,int room_num){
        this.participant_list_data_arraylist = participant_list_data_arraylist;
        this.mynickname = mynickname;
        this.nodejs_socket = nodejs_socket;
        this.context = context;
        this.room_num = room_num;
    }

    @NonNull
    @Override
    public Adapter_voice_participant_list.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voicechatroom_participant_list,parent,false);
        Adapter_voice_participant_list.CustomViewHolder holder = new Adapter_voice_participant_list.CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_voice_participant_list.CustomViewHolder holder, int position) {

        //방장만 강퇴버튼 생기게 하기
        int participant_list_data_arraylist_size = participant_list_data_arraylist.size();
        for (int i= 0; i<participant_list_data_arraylist_size; i++){
            //만약 해당리스트들중에 내아이디가 방장이 아닌것을 확인하면 강퇴표시 다지워줘라
            if (participant_list_data_arraylist.get(i).getNickname().equals(mynickname)){
                if(!participant_list_data_arraylist.get(i).getOwner().equals("방장")){
                    holder.user_kict_btn.setVisibility(View.INVISIBLE);
                }
                else {
                    holder.user_kict_btn.setVisibility(View.VISIBLE);
                }
            }
        }

        /*닉네임 및 본인 확인*/  /*마이크,스피커,강퇴*/
        holder.nickname.setText(participant_list_data_arraylist.get(position).getNickname());
        //본인 아이디
        if(participant_list_data_arraylist.get(position).getNickname().equals(mynickname)){
            //(본인)표시 보이게
            holder.identity_chechk.setVisibility(View.VISIBLE);
            //마이크 on
            if(participant_list_data_arraylist.get(position).isMike_onoff()==true){
                holder.mike_btn.setBackground(ContextCompat.getDrawable(context,R.drawable.microphone));
            }
            //마이크 off
            else {
                holder.mike_btn.setBackground(ContextCompat.getDrawable(context,R.drawable.microphone_mute));
            }
            //스피커 on
            if(participant_list_data_arraylist.get(position).isSpeaker_onoff()==true){
                holder.speaker_btn.setBackground(ContextCompat.getDrawable(context,R.drawable.speaker_on));
            }
            //스피커 off
            else {
                holder.speaker_btn.setBackground(ContextCompat.getDrawable(context,R.drawable.speaker_off));
            }
        }
        //그외 아이디
        else{
            //(본인)표시 숨김
            holder.identity_chechk.setVisibility(View.INVISIBLE);
            //마이크 on
            if(participant_list_data_arraylist.get(position).isMike_onoff()==true){
                holder.mike_btn.setBackground(ContextCompat.getDrawable(context,R.drawable.microphonegray));
            }
            //마이크 off
            else {
                holder.mike_btn.setBackground(ContextCompat.getDrawable(context,R.drawable.microphonemutegray));
            }
            //스피커 on
            if(participant_list_data_arraylist.get(position).isSpeaker_onoff()==true){
                holder.speaker_btn.setBackground(ContextCompat.getDrawable(context,R.drawable.speaker_on_gray));
            }
            //스피커 off
            else {
                holder.speaker_btn.setBackground(ContextCompat.getDrawable(context,R.drawable.speaker_off_gray));
            }


        }

        /*프로필 이미지 및 왕관*/

        //프로필 이미지가 있다면
        if(!participant_list_data_arraylist.get(position).getProfile_img().equals("없음")){
            Bitmap profile_imgBitmap = BitmapConverter.StringToBitmap(participant_list_data_arraylist.get(position).getProfile_img());
            holder.profile_img.setImageBitmap(profile_imgBitmap);
        }
        //없다면 기본 프로필 이미지로 변경
        else {
            holder.profile_img.setImageResource(R.drawable.profile4);
        }

        //방장만 왕관 보이게하기
        if(participant_list_data_arraylist.get(position).getOwner().equals("방장")){
            holder.crwon_img.setVisibility(View.VISIBLE);
            //근데 방장이 나라면 내꺼만 강퇴 지워주세요.
            if(participant_list_data_arraylist.get(position).getNickname().equals(mynickname)){
                holder.user_kict_btn.setVisibility(View.INVISIBLE);
            }
        }
        else {
            holder.crwon_img.setVisibility(View.INVISIBLE);
            //방장이 아닌데 내 아이디라면 모두의 kick_btn을 지워주세요.
        }

        //해당 뷰에 포지션주기
        holder.rootview.setTag(position);
        holder.user_kict_btn.setTag(position);

    }

    @Override
    public int getItemCount() {
        return (null!=participant_list_data_arraylist ? participant_list_data_arraylist.size() : 0) ;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profile_img; //프로필 이미지
        ImageView crwon_img; //방장 표시
        TextView nickname; //닉네임
        TextView identity_chechk; //본인확인 ->(본인)

        ImageView mike_btn; //마이크 음소거
        ImageView speaker_btn;
        ImageView user_kict_btn;
        protected View rootview;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "CustomViewHolder: CustomViewHolderCustomViewHolderCustomViewHolderCustomViewHolderCustomViewHolder");
            profile_img = (CircleImageView)itemView.findViewById(R.id.profile_img);
            crwon_img = (ImageView)itemView.findViewById(R.id.crwon_img);
            nickname = (TextView)itemView.findViewById(R.id.nickname);
            identity_chechk = (TextView)itemView.findViewById(R.id.identity_chechk);
            mike_btn = (ImageView)itemView.findViewById(R.id.mike_btn);
            speaker_btn = (ImageView)itemView.findViewById(R.id.speaker_btn);
            user_kict_btn = (ImageView)itemView.findViewById(R.id.user_kict_btn);

            rootview = itemView;

            mike_btn.setClickable(true);
            speaker_btn.setClickable(true);
            user_kict_btn.setClickable(true);

//            //방장만 강퇴버튼 생기게 하기
//            int participant_list_data_arraylist_size = participant_list_data_arraylist.size();
//            for (int i= 0; i<participant_list_data_arraylist_size; i++){
//                //만약 해당리스트들중에 내아이디가 방장이 아닌것을 확인하면 강퇴표시 다지워줘라
//                if (participant_list_data_arraylist.get(i).getNickname().equals(mynickname)){
//                    if(!participant_list_data_arraylist.get(i).getOwner().equals("방장")){
//                        user_kict_btn.setVisibility(View.INVISIBLE);
//                    }
//                    else {
//                        user_kict_btn.setVisibility(View.VISIBLE);
//                    }
//                }
//            }

            user_kict_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: ");
                    int position = (int) user_kict_btn.getTag();
                    kick_user = participant_list_data_arraylist.get(position).getNickname();
                    startDialog();
                }
            });


        }
    }
    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(context);
        myAlertDialog.setTitle("강퇴");
        myAlertDialog.setMessage(kick_user+"님을 정말 강퇴 하시겠습니까?");


        myAlertDialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //nodejs로 보내기.
                user_kick_emit(kick_user);
            }
        });

        myAlertDialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });
        myAlertDialog.show();
    }

    private void user_kick_emit(String user){

        try {
            JSONObject data = new JSONObject();
            data.put("num",room_num);
            data.put("user_nickname",user);
            nodejs_socket.emit("user_kick",data);
            Log.d(TAG, "user_kick_emit: 강퇴 : 유저 => "+user);
        }catch (Exception e){
            Log.d(TAG, "user_kick_emit: ERROR.message : "+e.getMessage());
        }


    }
}
