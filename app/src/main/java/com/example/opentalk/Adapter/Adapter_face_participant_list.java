package com.example.opentalk.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
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

import com.example.opentalk.BitmapConverter;
import com.example.opentalk.Data.FaceChatRoom_Participant_List_Data;
import com.example.opentalk.Data.VoiceChatRoom_Participant_List_Data;
import com.example.opentalk.R;

import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.Socket;

public class Adapter_face_participant_list extends RecyclerView.Adapter<Adapter_face_participant_list.CustomViewHolder> {

    String TAG = "Adapter_voice_participant_list";

    ArrayList<FaceChatRoom_Participant_List_Data> participant_list_data_arraylist;
    String mynickname;
    Context context;
    String kick_user;
    int room_num;
    Socket nodejs_socket;
    public Adapter_face_participant_list(ArrayList<FaceChatRoom_Participant_List_Data> participant_list_data_arraylist, String mynickname,Context context, int room_num,Socket nodejs_socket){
        this.participant_list_data_arraylist = participant_list_data_arraylist;
        this.mynickname = mynickname;
        this.context = context;
        this.room_num = room_num;
        this.nodejs_socket = nodejs_socket;
    }

    @NonNull
    @Override
    public Adapter_face_participant_list.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_facechat_participant_list,parent,false);
        Adapter_face_participant_list.CustomViewHolder holder = new Adapter_face_participant_list.CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_face_participant_list.CustomViewHolder holder, int position) {

        //????????? ???????????? ????????? ??????
        int participant_list_data_arraylist_size = participant_list_data_arraylist.size();
        for (int i= 0; i<participant_list_data_arraylist_size; i++){
            //?????? ???????????????????????? ??????????????? ????????? ???????????? ???????????? ???????????? ???????????????
            if (participant_list_data_arraylist.get(i).getNickname().equals(mynickname)){
                if(!participant_list_data_arraylist.get(i).getOwner().equals("??????")){
                    holder.user_kict_btn.setVisibility(View.INVISIBLE);
                }
                else {
                    holder.user_kict_btn.setVisibility(View.VISIBLE);
                }
            }
        }
        /*????????? ??? ?????? ??????*/  /*?????????,?????????,??????*/
        holder.nickname.setText(participant_list_data_arraylist.get(position).getNickname());

        /*????????? ????????? ??? ??????*/
        //????????? ???????????? ?????????
        if(!participant_list_data_arraylist.get(position).getProfile_img().equals("??????")){
            Bitmap profile_imgBitmap = BitmapConverter.StringToBitmap(participant_list_data_arraylist.get(position).getProfile_img());
            holder.profile_img.setImageBitmap(profile_imgBitmap);
        }
        //????????? ?????? ????????? ???????????? ??????
        else {
            holder.profile_img.setImageResource(R.drawable.profile4);
        }

        //????????? ?????? ???????????????
        if(participant_list_data_arraylist.get(position).getOwner().equals("??????")){
            holder.crwon_img.setVisibility(View.VISIBLE);
            //?????? ????????? ????????? ????????? ?????? ???????????????.
            if(participant_list_data_arraylist.get(position).getNickname().equals(mynickname)){
                holder.user_kict_btn.setVisibility(View.INVISIBLE);
            }
        }
        else {
            holder.crwon_img.setVisibility(View.INVISIBLE);
            //????????? ????????? ??? ??????????????? ????????? kick_btn??? ???????????????.
        }

        //?????? ?????? ???????????????
        holder.rootview.setTag(position);
        holder.user_kict_btn.setTag(position);

    }

    @Override
    public int getItemCount() {
        return (null!=participant_list_data_arraylist ? participant_list_data_arraylist.size() : 0) ;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profile_img; //????????? ?????????
        ImageView crwon_img; //?????? ??????
        TextView nickname; //?????????
        TextView identity_chechk; //???????????? ->(??????)
        ImageView user_kict_btn;
        protected View rootview;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "CustomViewHolder: CustomViewHolderCustomViewHolderCustomViewHolderCustomViewHolderCustomViewHolder");
            profile_img = (CircleImageView)itemView.findViewById(R.id.profile_img);
            crwon_img = (ImageView)itemView.findViewById(R.id.crwon_img);
            nickname = (TextView)itemView.findViewById(R.id.nickname);
            identity_chechk = (TextView)itemView.findViewById(R.id.identity_chechk);
            user_kict_btn = (ImageView)itemView.findViewById(R.id.user_kict_btn);

            rootview = itemView;

            user_kict_btn.setClickable(true);

            //????????? ???????????? ????????? ??????
            int participant_list_data_arraylist_size = participant_list_data_arraylist.size();
            for (int i= 0; i<participant_list_data_arraylist_size; i++){
                //?????? ???????????????????????? ??????????????? ????????? ???????????? ???????????? ???????????? ???????????????
                if (participant_list_data_arraylist.get(i).getNickname().equals(mynickname)){
                    if(!participant_list_data_arraylist.get(i).getOwner().equals("??????")){
                        user_kict_btn.setVisibility(View.INVISIBLE);
                    }
                    else {
                        user_kict_btn.setVisibility(View.VISIBLE);
                    }
                }
            }

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
        myAlertDialog.setTitle("??????");
        myAlertDialog.setMessage(kick_user+"?????? ?????? ?????? ???????????????????");


        myAlertDialog.setPositiveButton("???", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //nodejs??? ?????????.
                user_kick_emit(kick_user);
            }
        });

        myAlertDialog.setNegativeButton("?????????", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });
        myAlertDialog.show();
    }

    private void user_kick_emit(String user){

        try {
            JSONObject data = new JSONObject();
            data.put("room",room_num);
            data.put("username",user);
            nodejs_socket.emit("user_kick",data);
            Log.d(TAG, "user_kick_emit: ?????? : ?????? => "+user);
        }catch (Exception e){
            Log.d(TAG, "user_kick_emit: ERROR.message : "+e.getMessage());
        }


    }
}
