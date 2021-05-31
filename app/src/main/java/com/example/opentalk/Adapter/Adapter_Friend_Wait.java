package com.example.opentalk.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.opentalk.Activity.Activity_Friend_List;
import com.example.opentalk.Activity.Activity_Lobby;
import com.example.opentalk.BitmapConverter;
import com.example.opentalk.Data.FriendWaitData;
import com.example.opentalk.R;
import com.example.opentalk.Socket_my.Socket_friend_choose_thread;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_Friend_Wait extends RecyclerView.Adapter<Adapter_Friend_Wait.CustomViewHolder> {

    String TAG = "Adapter_Friend_Wait";
    private ArrayList<FriendWaitData> request_email_arrayList;
    Context context;

    public Adapter_Friend_Wait(ArrayList<FriendWaitData> request_email_arrayList, Context context) {
        this.request_email_arrayList = request_email_arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_wait,parent,false);
        Adapter_Friend_Wait.CustomViewHolder holder = new Adapter_Friend_Wait.CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        holder.item_friend_wait_email.setText(request_email_arrayList.get(position).getUserid());
        if(!request_email_arrayList.get(position).getImgpath().equals("")){
            holder.item_friend_wait_img.setImageBitmap(request_email_arrayList.get(position).getBitmap());
            Log.d(TAG, "onBindViewHolder: 확인작업업 1");
        }
        else{
            holder.item_friend_wait_img.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.profile4));
            Log.d(TAG, "onBindViewHolder: 확인작업업 2");
        }
        holder.item_friend_wait_accept_btn.setTag(position);
        holder.item_friend_wait_refuse_btn.setTag(position);
    }

    @Override
    public int getItemCount() {
        return (null != request_email_arrayList ? request_email_arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView item_friend_wait_email;
        Button item_friend_wait_accept_btn;
        Button item_friend_wait_refuse_btn;
        CircleImageView item_friend_wait_img;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "CustomViewHolder: 2");
            item_friend_wait_email = (TextView)itemView.findViewById(R.id.item_friend_wait_email);
            item_friend_wait_accept_btn = (Button)itemView.findViewById(R.id.item_friend_wait_accept_btn);
            item_friend_wait_refuse_btn = (Button)itemView.findViewById(R.id.item_friend_wait_refuse_btn);
            item_friend_wait_img = (CircleImageView)itemView.findViewById(R.id.item_friend_wait_img);


            item_friend_wait_accept_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: position : "+item_friend_wait_accept_btn.getTag());
                    int position = (int)item_friend_wait_accept_btn.getTag();
                    try{
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("type","friend_add_agree");
                        jsonObject.put("my_email_id", Activity_Lobby.userid);
                        jsonObject.put("other_email_id",request_email_arrayList.get(position).getUserid());
                        Log.d(TAG, "onClick: my_email_id : "+Activity_Lobby.userid);
                        Log.d(TAG, "onClick: my_email_id : "+request_email_arrayList.get(position).getUserid());
                        Socket_friend_choose_thread socket_friend_choose_thread = new Socket_friend_choose_thread(jsonObject);
                        socket_friend_choose_thread.start();
                        request_email_arrayList.remove(position);
                        notifyItemRemoved(position);
                    }
                    catch (Exception e){
                        Log.d(TAG, "onClick: "+e.getMessage());

                    }

                }
            });
            item_friend_wait_refuse_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: position : "+item_friend_wait_refuse_btn.getTag());
                    int position = (int)item_friend_wait_accept_btn.getTag();
                    try{
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("type","friend_add_refuse");
                        jsonObject.put("my_email_id", Activity_Lobby.userid);
                        jsonObject.put("other_email_id",request_email_arrayList.get(position).getUserid());
                        Log.d(TAG, "onClick: my_email_id : "+Activity_Lobby.userid);
                        Log.d(TAG, "onClick: my_email_id : "+request_email_arrayList.get(position).getUserid());
                        Socket_friend_choose_thread socket_friend_choose_thread = new Socket_friend_choose_thread(jsonObject);
                        socket_friend_choose_thread.start();

                        request_email_arrayList.remove(position);
                        notifyDataSetChanged();
                    }
                    catch (Exception e){

                    }
                }
            });
        }
    }
}
