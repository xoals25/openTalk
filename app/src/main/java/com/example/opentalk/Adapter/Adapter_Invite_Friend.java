package com.example.opentalk.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.opentalk.Activity.Activity_Lobby;
import com.example.opentalk.Data.Invite_Friend;
import com.example.opentalk.R;
import com.example.opentalk.Socket_my.Socket_friend_chat_thread;

import org.json.JSONObject;

import java.util.ArrayList;

public class Adapter_Invite_Friend extends RecyclerView.Adapter<Adapter_Invite_Friend.CustomViewHolder> {

    String TAG="Adapter_Invite_Friend";
    ArrayList<Invite_Friend> friend_list;
    String roomType;
    String roomTitle;
    int openchatroomid;
//    private View.OnClickListener onClickListener;

    public Adapter_Invite_Friend(ArrayList<Invite_Friend> friend_list,String roomType,String roomTitle,int openchatroomid/*, View.OnClickListener onClickListener*/) {
        this.friend_list = friend_list;
        this.roomType = roomType;
        this.roomTitle = roomTitle;
        this.openchatroomid = openchatroomid;
//        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_dialog_item,parent,false);
        Adapter_Invite_Friend.CustomViewHolder holder = new Adapter_Invite_Friend.CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.friend_invite_btn.setTag(position);
        holder.friend_nickname.setText(friend_list.get(position).getFriendNickName());
    }

    @Override
    public int getItemCount() {
        return (null!=friend_list ? friend_list.size() : 0) ;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView friend_nickname;
        Button friend_invite_btn;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            friend_nickname = (TextView)itemView.findViewById(R.id.friend_nickname);
            friend_invite_btn = (Button)itemView.findViewById(R.id.friend_invite_btn);
            friend_invite_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int)v.getTag();
                    try{
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("type","friend_chat");
                        jsonObject.put("roomnum", friend_list.get(position).getUniquename());
                        jsonObject.put("openchatroomid", String.valueOf(openchatroomid));
                        jsonObject.put("roomtitle", roomTitle);
                        jsonObject.put("roomType", roomType);
                        jsonObject.put("msg",Activity_Lobby.usernickname+"님께서 "+roomTitle+"("+roomType+") 방에 초대하셨습니다. 승낙을 원하시면 버튼을 눌러주세요.");
                        jsonObject.put("to",friend_list.get(position).getFriendId());
                        jsonObject.put("from", Activity_Lobby.userid);
                        jsonObject.put("from_nickname",Activity_Lobby.usernickname);
                        jsonObject.put("msgtype","invitemsg");

                        Socket_friend_chat_thread socket_friend_chat_thread = new Socket_friend_chat_thread(jsonObject);
                        socket_friend_chat_thread.start();
                        friend_invite_btn.setText("초대완료");
                        friend_invite_btn.setEnabled(false);
                    }catch (Exception e){

                    }
                }
            });
        }
    }
}
