package com.example.opentalk.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.opentalk.Data.Lobby_Rooms_Data;
import com.example.opentalk.R;

import java.util.ArrayList;

public class Adapter_Lobby_rooms extends RecyclerView.Adapter<Adapter_Lobby_rooms.CustomViewHolder> {
    String TAG ="Adapter_Lobby_rooms";
    private ArrayList<Lobby_Rooms_Data> lobby_rooms_dataArrayList;
    private View.OnClickListener onClickListener;
    Context context;

    public Adapter_Lobby_rooms(Context context,ArrayList<Lobby_Rooms_Data> lobby_rooms_dataArrayList, View.OnClickListener onClickListener) {
        this.lobby_rooms_dataArrayList = lobby_rooms_dataArrayList;
        this.onClickListener = onClickListener;
        this.context = context;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        /*채팅방 종류*/
        protected TextView item_rooms_type;
        /*인원수*/
        protected LinearLayout item_rooms_people; //인원수 전체를 담고있는 레이아웃 ->꽉차면 글색상 변경
        protected TextView item_rooms_people_num; //현재인원수
        protected TextView item_rooms_people_numlimit; //최대인원수
        /*방제목*/
        protected TextView item_rooms_title;
        /*잠금방 표시 이미지*/
        protected ImageView item_rooms_lock;
        protected View rootview;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            item_rooms_type = (TextView)itemView.findViewById(R.id.item_rooms_type);
            item_rooms_people = (LinearLayout)itemView.findViewById(R.id.item_rooms_people);
            item_rooms_people_num = (TextView)itemView.findViewById(R.id.item_rooms_people_num);
            item_rooms_people_numlimit = (TextView)itemView.findViewById(R.id.item_rooms_people_numlimit);
            item_rooms_title = (TextView)itemView.findViewById(R.id.item_rooms_title);
            item_rooms_lock = (ImageView)itemView.findViewById(R.id.item_rooms_lock);

            rootview=itemView;

            rootview.setClickable(true);
            rootview.setEnabled(true);
            rootview.setOnClickListener(onClickListener);
        }
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lobby_rooms,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        /*방제목 입력주기*/
        holder.item_rooms_title.setText(lobby_rooms_dataArrayList.get(position).getRoom_title());
        /*채팅방 종류에따라 다르게 입력*/
        if (lobby_rooms_dataArrayList.get(position).getRoom_type().equals("voice")){
            holder.item_rooms_type.setText("음성채팅방");
        }
        else if(lobby_rooms_dataArrayList.get(position).getRoom_type().equals("face")){
            holder.item_rooms_type.setText("화상채팅방");
        }
        /*공개방,비공개방에 따라 잠금이미지 보이게,보이지않게하기*/
        if(lobby_rooms_dataArrayList.get(position).getRoom_public().equals("비공개방")){
            holder.item_rooms_lock.setVisibility(View.VISIBLE);
        }
        else if(lobby_rooms_dataArrayList.get(position).getRoom_public().equals("공개방")){
            holder.item_rooms_lock.setVisibility(View.INVISIBLE);
        }
        /*현재인원수*/
        holder.item_rooms_people_num.setText(String.valueOf(lobby_rooms_dataArrayList.get(position).getRoom_people_num()));
        /*최대인원수*/
        holder.item_rooms_people_numlimit.setText(String.valueOf(lobby_rooms_dataArrayList.get(position).getRoom_people_numlimit()));
        holder.rootview.setTag(position);
    }

    @Override
    public int getItemCount() {
        return (null != lobby_rooms_dataArrayList ? lobby_rooms_dataArrayList.size() : 0);
    }


}
