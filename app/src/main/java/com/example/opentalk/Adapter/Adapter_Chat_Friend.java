package com.example.opentalk.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.opentalk.Activity.Activity_Friend_Chat;
import com.example.opentalk.Activity.Activity_Imgexpand;
import com.example.opentalk.Activity.Activity_Lobby;
import com.example.opentalk.Activity.Activity_Room_Face;
import com.example.opentalk.Activity.Activity_Room_Voice;
import com.example.opentalk.BitmapConverter;
import com.example.opentalk.Code.ViewType_Code;
import com.example.opentalk.Data.Chat_Msg_Data;
import com.example.opentalk.Data.Chat_Msg_Data_Friend;
import com.example.opentalk.Http.HttpConnection_room_enter;
import com.example.opentalk.R;

import java.util.ArrayList;

/*
*
* 2021.01.26 생성
* 친구와 채팅할 때 사용되는 리사이클러뷰에 셋팅되는 어답터
*
* 2021.01.27 수정
* RightViewHolder(내가 작성한 채팅)을 GONE으로 만들었음
*
* */

public class Adapter_Chat_Friend extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    String TAG ="Adapter_Chat_Friend";
    Context context;
    ArrayList<Chat_Msg_Data_Friend> chat_msg_dataArrayList;
    String myid = Activity_Lobby.userid;
    String usernickname = Activity_Lobby.usernickname;
    TextView leftview;
    TextView rightview;
    public Adapter_Chat_Friend(Context context, ArrayList<Chat_Msg_Data_Friend> chat_msg_dataArrayList) {
        this.context = context;
        this.chat_msg_dataArrayList = chat_msg_dataArrayList;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        if(viewType == ViewType_Code.ViewType.CENTER_CONTENT){
            view = inflater.inflate(R.layout.item_chat_friend_notice_centerview,parent,false);
            return new CenterViewHolder(view);
        }
        else if(viewType == ViewType_Code.ViewType.LEFT_CONTENT){
            view = inflater.inflate(R.layout.item_chat_friend_otherid_leftview, parent, false);
            return new LeftViewHolder(view);
        }
        /*만약 마지막을 esle if로 하게되면 return을 밖에도 해줘야한다.*/
        //이유는 esle if로 끝나면 내가 정의하지않은 조건문을 벗어나면 어떤 것을 return을 해야하나 컴퓨터는 모르기때문에
        //else 즉 위의 조건 제외한 모든것은 여기서 return한다고 컴퓨터에게 알려주는 것이다.
        else{
            view = inflater.inflate(R.layout.item_chat_friend_myid_rightview, parent, false);
            return new RightViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //참조변수가 참조하고 있는 인스턴스의 실제 타입을 알아보기 위해 instanceof 연산자를 사용합니다.
        //주로 조건문에 사용되며, instanceof의 왼쪽에는 참조변수를 오른쪽에는 타입(클래스명)이 피연산자로 위치합니다.
        //그리고 연산의 결과로 boolean값인 true, false 중의 하나를 반환 합니다.
        //왼쪽 참조변수가 오른쪽 인스턴스타입이라면 true 아니면 false
        if(holder instanceof CenterViewHolder){
            ((CenterViewHolder) holder).item_chat_friend_notice_text.setText(chat_msg_dataArrayList.get(position).getMessage());
    }
        else if(holder instanceof LeftViewHolder){
            if (chat_msg_dataArrayList.get(position).getImgbitmap()==null) {
                if(chat_msg_dataArrayList.get(position).getMsgtype().equals("textmsg")) {
                    ((LeftViewHolder) holder).item_chat_friend_otherid_msg.setVisibility(View.VISIBLE);
                    ((LeftViewHolder) holder).item_chat_friend_otherid_img.setVisibility(View.GONE);
                    ((LeftViewHolder) holder).invite_layout.setVisibility(View.GONE);
                    ((LeftViewHolder) holder).item_chat_friend_otherid_msg.setText(chat_msg_dataArrayList.get(position).getMessage());
                    ((LeftViewHolder) holder).item_chat_friend_otherid_id.setText(chat_msg_dataArrayList.get(position).getUsernickname());
                }
                else if(chat_msg_dataArrayList.get(position).getMsgtype().equals("invitemsg")){
                    ((LeftViewHolder) holder).item_chat_friend_otherid_msg.setVisibility(View.GONE);
                    ((LeftViewHolder) holder).invite_layout.setVisibility(View.VISIBLE);
                    ((LeftViewHolder) holder).invite_msg.setText(chat_msg_dataArrayList.get(position).getMessage());
                    ((LeftViewHolder) holder).item_chat_friend_otherid_id.setText(chat_msg_dataArrayList.get(position).getUsernickname());
                    ((LeftViewHolder) holder).invite_btn.setTag(position);
                }
            }
            else{
                ((LeftViewHolder) holder).item_chat_friend_otherid_msg.setVisibility(View.GONE);
                ((LeftViewHolder) holder).item_chat_friend_otherid_img.setVisibility(View.VISIBLE);
                ((LeftViewHolder) holder).item_chat_friend_otherid_img.setImageBitmap(chat_msg_dataArrayList.get(position).getImgbitmap());
                ((LeftViewHolder) holder).item_chat_friend_otherid_id.setText(chat_msg_dataArrayList.get(position).getUsernickname());
                ((LeftViewHolder)holder).item_chat_friend_otherid_img.setTag(position);
            }
            if (chat_msg_dataArrayList.get(position).getReadstate().equals("read")){
                ((LeftViewHolder)holder).item_chat_friend_other_readstate.setText("");
                ((LeftViewHolder)holder).item_chat_friend_other_readstate.setVisibility(View.INVISIBLE);
            }
            else{
                ((LeftViewHolder)holder).item_chat_friend_other_readstate.setText("1");
                ((LeftViewHolder)holder).item_chat_friend_other_readstate.setVisibility(View.VISIBLE);
            }
            if((position+1)<chat_msg_dataArrayList.size()){
                if(!chat_msg_dataArrayList.get(position+1).getUsernickname().equals(usernickname)) {
                    if (chat_msg_dataArrayList.get(position + 1).getSenddate().equals(chat_msg_dataArrayList.get(position).getSenddate())) {
                        ((LeftViewHolder) holder).item_chat_friend_other_senddate.setVisibility(View.GONE);
                    } else {
                        ((LeftViewHolder) holder).item_chat_friend_other_senddate.setVisibility(View.VISIBLE);
                        ((LeftViewHolder)holder).item_chat_friend_other_senddate.setText(chat_msg_dataArrayList.get(position).getSenddate());
                    }
                }
                else{
                    ((LeftViewHolder) holder).item_chat_friend_other_senddate.setVisibility(View.VISIBLE);
                    ((LeftViewHolder)holder).item_chat_friend_other_senddate.setText(chat_msg_dataArrayList.get(position).getSenddate());
                }
            }
            else{
                ((LeftViewHolder) holder).item_chat_friend_other_senddate.setVisibility(View.VISIBLE);
                ((LeftViewHolder)holder).item_chat_friend_other_senddate.setText(chat_msg_dataArrayList.get(position).getSenddate());
            }

            if((position-1)>=0){
                if(!chat_msg_dataArrayList.get(position-1).getUsernickname().equals(usernickname)) {
                    if (chat_msg_dataArrayList.get(position-1).getSenddate().equals(chat_msg_dataArrayList.get(position).getSenddate())) {
                        if(chat_msg_dataArrayList.get(position-1).getUsernickname().equals("nonickname")) {
                            ((LeftViewHolder) holder).item_chat_friend_otherid_id.setVisibility(View.VISIBLE);
                        }
                        else{
                            ((LeftViewHolder) holder).item_chat_friend_otherid_id.setVisibility(View.GONE);
                        }
                    }
                    else {
                        ((LeftViewHolder) holder).item_chat_friend_otherid_id.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    ((LeftViewHolder) holder).item_chat_friend_otherid_id.setVisibility(View.VISIBLE);
                }
            }


        }
        else if(holder instanceof RightViewHolder){
            if (chat_msg_dataArrayList.get(position).getImgbitmap()==null) {
                if(chat_msg_dataArrayList.get(position).getMsgtype().equals("textmsg")) {
                    ((RightViewHolder) holder).item_chat_friend_myid_msg.setVisibility(View.VISIBLE);
                    ((RightViewHolder) holder).item_chat_friend_myid_img.setVisibility(View.GONE);
                    ((RightViewHolder) holder).item_chat_friend_myid_msg.setText(chat_msg_dataArrayList.get(position).getMessage());
                    ((RightViewHolder) holder).item_chat_friend_myid_id.setVisibility(View.GONE);
//                ((RightViewHolder) holder).item_chat_friend_myid_id.setText(chat_msg_dataArrayList.get(position).getUserid());
                }
                else if(chat_msg_dataArrayList.get(position).getMsgtype().equals("invitemsg")){
                    ((RightViewHolder) holder).item_chat_friend_myid_msg.setText("초대 메세지입니다.");
                }
            }
            else {
                ((RightViewHolder) holder).item_chat_friend_myid_msg.setVisibility(View.GONE);
                ((RightViewHolder) holder).item_chat_friend_myid_img.setVisibility(View.VISIBLE);
                ((RightViewHolder) holder).item_chat_friend_myid_img.setImageBitmap(chat_msg_dataArrayList.get(position).getImgbitmap());
                ((RightViewHolder) holder).item_chat_friend_myid_id.setVisibility(View.GONE);
//                ((RightViewHolder) holder).item_chat_friend_myid_id.setText(chat_msg_dataArrayList.get(position).getUserid());
                ((RightViewHolder)holder).item_chat_friend_myid_img.setTag(position);
            }

            if (chat_msg_dataArrayList.get(position).getReadstate().equals("read")){
                ((RightViewHolder)holder).item_chat_friend_my_readstate.setText("");
                ((RightViewHolder)holder).item_chat_friend_my_readstate.setVisibility(View.INVISIBLE);
            }
            else{
                ((RightViewHolder)holder).item_chat_friend_my_readstate.setText("1");
                ((RightViewHolder)holder).item_chat_friend_my_readstate.setVisibility(View.VISIBLE);
            }
            if((position+1)<chat_msg_dataArrayList.size()){
                if(chat_msg_dataArrayList.get(position+1).getUsernickname().equals(usernickname)) {
                    if (chat_msg_dataArrayList.get(position + 1).getSenddate().equals(chat_msg_dataArrayList.get(position).getSenddate())) {
                        ((RightViewHolder) holder).item_chat_friend_my_senddate.setVisibility(View.GONE);
                    } else {
                        ((RightViewHolder) holder).item_chat_friend_my_senddate.setVisibility(View.VISIBLE);
                        ((RightViewHolder)holder).item_chat_friend_my_senddate.setText(chat_msg_dataArrayList.get(position).getSenddate());
                    }
                }
                else{
                    ((RightViewHolder) holder).item_chat_friend_my_senddate.setVisibility(View.VISIBLE);
                    ((RightViewHolder)holder).item_chat_friend_my_senddate.setText(chat_msg_dataArrayList.get(position).getSenddate());
                }
            }
            else{
                ((RightViewHolder) holder).item_chat_friend_my_senddate.setVisibility(View.VISIBLE);
                ((RightViewHolder)holder).item_chat_friend_my_senddate.setText(chat_msg_dataArrayList.get(position).getSenddate());
            }
        }
    }

    @Override
    public int getItemCount() {
        return (null != chat_msg_dataArrayList ? chat_msg_dataArrayList.size() : 0);
    }

    /*여기서 데이터에 viewtype을 가져와서 위의 viewtype에 자동으로 적용시켜주는듯하다.*/
    @Override
    public int getItemViewType(int position) {
        return chat_msg_dataArrayList.get(position).getViewtype();
    }

    public class CenterViewHolder extends RecyclerView.ViewHolder{
        TextView item_chat_friend_notice_text;

        CenterViewHolder(View itemView) {
            super(itemView);
            item_chat_friend_notice_text = itemView.findViewById(R.id.item_chat_friend_notice_text);
        }
    }

    public class LeftViewHolder extends RecyclerView.ViewHolder{
        TextView item_chat_friend_otherid_id;
        TextView item_chat_friend_otherid_msg;
        TextView item_chat_friend_other_readstate;
        TextView item_chat_friend_other_senddate;

        ImageView item_chat_friend_otherid_img;

        LinearLayout invite_layout;
        TextView invite_msg;
        Button invite_btn;
        LeftViewHolder(View itemView) {
            super(itemView);
            item_chat_friend_otherid_id = itemView.findViewById(R.id.item_chat_friend_otherid_id);
            item_chat_friend_otherid_msg = itemView.findViewById(R.id.item_chat_friend_otherid_msg);
            item_chat_friend_otherid_img = itemView.findViewById(R.id.item_chat_friend_otherid_img);
            item_chat_friend_other_readstate = itemView.findViewById(R.id.item_chat_friend_other_readstate );
            item_chat_friend_other_senddate = itemView.findViewById(R.id.item_chat_friend_other_senddate );

            invite_layout = itemView.findViewById(R.id.invite_layout );
            invite_msg = itemView.findViewById(R.id.invite_msg );
            invite_btn = itemView.findViewById(R.id.invite_btn );

            invite_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int)invite_btn.getTag();
                    Intent intent;
                    Activity_Friend_Chat context = ((Activity_Friend_Chat) Activity_Friend_Chat.Activity_Friend_Chat_context);
                    String IP_ADDRESS = "3.36.188.116/opentalk";
                    Log.d(TAG, "onClick: 확인작업 : "+chat_msg_dataArrayList.get(position).getOpenchatroomid());
                    int adapter_room_id = Integer.valueOf(chat_msg_dataArrayList.get(position).getOpenchatroomid());
                    String[] senddateSplit = chat_msg_dataArrayList.get(position).getMessage().split(" ");
                    String title = senddateSplit[1];
                    senddateSplit = title.split("\\(");
                    title = senddateSplit[0];
                    if(chat_msg_dataArrayList.get(position).getMessage().contains("화상채팅")) {
                        HttpConnection_room_enter httpConnection_room_enter = new HttpConnection_room_enter(context,"face",adapter_room_id,title);
                        httpConnection_room_enter.execute("http://"+IP_ADDRESS+"/room_enter.php",String.valueOf(adapter_room_id),"공개방","");
                    }
                    else{
                        HttpConnection_room_enter httpConnection_room_enter = new HttpConnection_room_enter(context,"voice",adapter_room_id,title);
                        httpConnection_room_enter.execute("http://"+IP_ADDRESS+"/room_enter.php",String.valueOf(adapter_room_id),"공개방","");
                    }
                }
            });

//            item_chat_friend_otherid_img.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = (int) item_chat_friend_otherid_img.getTag();
//                    byte[] imgbyteFromBitmap = BitmapConverter.BitmapToByteArray(chat_msg_dataArrayList.get(position).getImgbitmap());
//                    Intent intent = new Intent(context, Activity_Imgexpand.class);
//                    intent.putExtra("imgString",imgbyteFromBitmap);
//                    context.startActivity(intent);
//                }
//            });
        }
    }

    public class RightViewHolder extends RecyclerView.ViewHolder{
        TextView item_chat_friend_myid_id;
        TextView item_chat_friend_myid_msg;
        TextView item_chat_friend_my_readstate;
        TextView item_chat_friend_my_senddate;
        ImageView item_chat_friend_myid_img;
        RightViewHolder(View itemView) {
            super(itemView);
            item_chat_friend_myid_id = itemView.findViewById(R.id.item_chat_friend_myid_id);
            item_chat_friend_myid_msg = itemView.findViewById(R.id.item_chat_friend_myid_msg);
            item_chat_friend_myid_img = itemView.findViewById(R.id.item_chat_friend_myid_img);
            item_chat_friend_my_readstate = itemView.findViewById(R.id.item_chat_friend_my_readstate);
            item_chat_friend_my_senddate = itemView.findViewById(R.id.item_chat_friend_my_senddate);

            item_chat_friend_myid_id.setVisibility(View.GONE);

//            item_chat_friend_myid_img.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = (int) item_chat_friend_myid_img.getTag();
//                    byte[] imgbyteFromBitmap = BitmapConverter.BitmapToByteArray(chat_msg_dataArrayList.get(position).getImgbitmap());
//                    Intent intent = new Intent(context, Activity_Imgexpand.class);
//                    intent.putExtra("imgString",imgbyteFromBitmap);
//                    context.startActivity(intent);
//                }
//            });
        }
    }



}
