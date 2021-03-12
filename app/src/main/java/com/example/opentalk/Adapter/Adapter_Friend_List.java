package com.example.opentalk.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.opentalk.BitmapConverter;
import com.example.opentalk.Code.Connect_Code;
import com.example.opentalk.Data.Friend_List_Data;
import com.example.opentalk.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_Friend_List extends RecyclerView.Adapter<Adapter_Friend_List.CustomViewHolder> {

    String TAG ="Adapter_Friend_List";

    private View.OnClickListener onClickListener;
    ArrayList<Friend_List_Data> friend_list_dataArrayList;
    ArrayList<String> img_path_ArrayList;
    Context context;

    public Adapter_Friend_List(ArrayList<Friend_List_Data> friend_list_dataArrayList, ArrayList<String> img_path_ArrayList, Context context,View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.friend_list_dataArrayList = friend_list_dataArrayList;
        this.img_path_ArrayList = img_path_ArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_list,parent,false);
        Adapter_Friend_List.CustomViewHolder holder = new Adapter_Friend_List.CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.rootView.setTag(position);
        holder.friend_nickname.setText(friend_list_dataArrayList.get(position).getFriend_nickname());
        //만약 사용자가 오프라인이라면
        if(friend_list_dataArrayList.get(position).getConnect().equals(Connect_Code.Connect_Type.CONNECT_OFFLINE)){
            holder.connect_marker.setBackground(ContextCompat.getDrawable(context,R.drawable.circle_offline));
            holder.connect_text.setText("오프라인");
        }
        //만약 사용자가 온라인이라면
        else if(friend_list_dataArrayList.get(position).getConnect().equals(Connect_Code.Connect_Type.CONNECT_ONLINE)){
            holder.connect_marker.setBackground(ContextCompat.getDrawable(context,R.drawable.circle_online));
            holder.connect_text.setText("온라인");
        }
        //이미지는 나중에 하자
        if(!friend_list_dataArrayList.get(position).getImgstring_tobitmap().equals("없음")){
            Bitmap profileimg_bitmap = BitmapConverter.StringToBitmap(friend_list_dataArrayList.get(position).getImgstring_tobitmap());
            holder.friend_profile_img.setImageBitmap(profileimg_bitmap);
            Log.d(TAG, "onBindViewHolder: position : "+position+"은 null이 아니다.");
            Log.d(TAG, "onBindViewHolder: position : "+position+"은 "+friend_list_dataArrayList.get(position).getImgstring_tobitmap());
        }
        //없다면 기본 이미지로 변경
        else {
            holder.friend_profile_img.setImageResource(R.drawable.profile4);
            Log.d(TAG, "onBindViewHolder: position : "+position+"은 null 이다.");
            Log.d(TAG, "onBindViewHolder: position : "+position+"은 "+friend_list_dataArrayList.get(position).getImgstring_tobitmap());
        }
        //마지막 채팅 글이 없다면
        if(friend_list_dataArrayList.get(position).getLastchatmsg().equals("")){
            holder.item_friend_list_chatmsg.setText("");
            holder.item_friend_list_chatmsg.setVisibility(View.GONE);
            holder.last_chat_notification.setVisibility(View.GONE);
        }
        //있다면
        else{
            holder.item_friend_list_chatmsg.setText(friend_list_dataArrayList.get(position).getLastchatmsg());
            holder.item_friend_list_chatmsg.setVisibility(View.VISIBLE);
            holder.last_chat_notification.setVisibility(View.VISIBLE);
        }

        //안읽은 메세지가 없다면
        if(friend_list_dataArrayList.get(position).getChatnum()==0){
            holder.item_friend_list_chatnum.setVisibility(View.INVISIBLE);
            holder.item_friend_list_chatnum.setText("");
        }
        //안읽은 메세지가 있다면
        else{
            holder.item_friend_list_chatnum.setText(String.valueOf(friend_list_dataArrayList.get(position).getChatnum()));
            holder.item_friend_list_chatnum.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return (null != friend_list_dataArrayList ? friend_list_dataArrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        CircleImageView friend_profile_img;
        ImageView connect_marker;
        TextView friend_nickname;
        TextView connect_text;
        TextView item_friend_list_chatmsg;
        TextView item_friend_list_chatnum;
        TextView last_chat_notification;
        View rootView;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            friend_profile_img = (CircleImageView)itemView.findViewById(R.id.item_friend_list_img);
            connect_marker = (ImageView)itemView.findViewById(R.id.item_friend_list_connect_marker);
            friend_nickname = (TextView)itemView.findViewById(R.id.item_friend_list_nickname);
            connect_text = (TextView)itemView.findViewById(R.id.item_friend_list_connect_text);
            item_friend_list_chatmsg = (TextView)itemView.findViewById(R.id.item_friend_list_chatmsg);
            item_friend_list_chatnum = (TextView)itemView.findViewById(R.id.item_friend_list_chatnum);
            last_chat_notification = (TextView)itemView.findViewById(R.id.last_chat_notification);
            rootView=itemView;

            rootView.setClickable(true);
            rootView.setEnabled(true);
            rootView.setOnClickListener(onClickListener);

        }

    }
}
