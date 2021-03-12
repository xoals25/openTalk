package com.example.opentalk.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.opentalk.Activity.Activity_Imgexpand;
import com.example.opentalk.BitmapConverter;
import com.example.opentalk.Data.Chat_Msg_Data;
import com.example.opentalk.R;
import com.example.opentalk.Code.ViewType_Code;

import java.util.ArrayList;

public class Adapter_Chat extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ArrayList<Chat_Msg_Data> chat_msg_dataArrayList;

    public Adapter_Chat(Context context, ArrayList<Chat_Msg_Data> chat_msg_dataArrayList) {
        this.context = context;
        this.chat_msg_dataArrayList = chat_msg_dataArrayList;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        if(viewType == ViewType_Code.ViewType.CENTER_CONTENT){
            view = inflater.inflate(R.layout.item_chat_notice_centerview,parent,false);
            return new CenterViewHolder(view);
        }
        else if(viewType == ViewType_Code.ViewType.LEFT_CONTENT){
            view = inflater.inflate(R.layout.item_chat_otherid_leftview, parent, false);
            return new LeftViewHolder(view);
        }
        /*만약 마지막을 esle if로 하게되면 return을 밖에도 해줘야한다.*/
        //이유는 esle if로 끝나면 내가 정의하지않은 조건문을 벗어나면 어떤 것을 return을 해야하나 컴퓨터는 모르기때문에
        //else 즉 위의 조건 제외한 모든것은 여기서 return한다고 컴퓨터에게 알려주는 것이다.
        else{
            view = inflater.inflate(R.layout.item_chat_myid_rightview, parent, false);
            Log.d("Adapter_Chat", "onBindViewHolder: 들어왔는지 확인작업2");
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
            ((CenterViewHolder) holder).item_chat_notice_text.setText(chat_msg_dataArrayList.get(position).getMessage());
        }
        else if(holder instanceof LeftViewHolder){
            if (chat_msg_dataArrayList.get(position).getImgbitmap()==null) {
                ((LeftViewHolder) holder).item_chat_otherid_msg.setVisibility(View.VISIBLE);
                ((LeftViewHolder) holder).item_chat_otherid_img.setVisibility(View.GONE);
                ((LeftViewHolder) holder).item_chat_otherid_msg.setText(chat_msg_dataArrayList.get(position).getMessage());
                ((LeftViewHolder) holder).item_chat_otherid_id.setText(chat_msg_dataArrayList.get(position).getUserid());
            }
            else{
                ((LeftViewHolder) holder).item_chat_otherid_msg.setVisibility(View.GONE);
                ((LeftViewHolder) holder).item_chat_otherid_img.setVisibility(View.VISIBLE);
                ((LeftViewHolder) holder).item_chat_otherid_img.setImageBitmap(chat_msg_dataArrayList.get(position).getImgbitmap());
                ((LeftViewHolder) holder).item_chat_otherid_id.setText(chat_msg_dataArrayList.get(position).getUserid());
                ((LeftViewHolder)holder).item_chat_otherid_img.setTag(position);
            }
        }
        else if(holder instanceof RightViewHolder){
            if (chat_msg_dataArrayList.get(position).getImgbitmap()==null) {
                ((RightViewHolder) holder).item_chat_myid_msg.setVisibility(View.VISIBLE);
                ((RightViewHolder) holder).item_chat_myid_img.setVisibility(View.GONE);
                ((RightViewHolder) holder).item_chat_myid_msg.setText(chat_msg_dataArrayList.get(position).getMessage());
                ((RightViewHolder) holder).item_chat_myid_id.setText(chat_msg_dataArrayList.get(position).getUserid());
            }
            else {
                ((RightViewHolder) holder).item_chat_myid_msg.setVisibility(View.GONE);
                ((RightViewHolder) holder).item_chat_myid_img.setVisibility(View.VISIBLE);
                ((RightViewHolder) holder).item_chat_myid_img.setImageBitmap(chat_msg_dataArrayList.get(position).getImgbitmap());
                ((RightViewHolder) holder).item_chat_myid_id.setText(chat_msg_dataArrayList.get(position).getUserid());
                ((RightViewHolder)holder).item_chat_myid_img.setTag(position);
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
        TextView item_chat_notice_text;

        CenterViewHolder(View itemView) {
            super(itemView);
            item_chat_notice_text = itemView.findViewById(R.id.item_chat_notice_text);
        }
    }

    public class LeftViewHolder extends RecyclerView.ViewHolder{
        TextView item_chat_otherid_id;
        TextView item_chat_otherid_msg;
        ImageView item_chat_otherid_img;
        LeftViewHolder(View itemView) {
            super(itemView);
            item_chat_otherid_id = itemView.findViewById(R.id.item_chat_otherid_id);
            item_chat_otherid_msg = itemView.findViewById(R.id.item_chat_otherid_msg);
            item_chat_otherid_img = itemView.findViewById(R.id.item_chat_otherid_img);
            item_chat_otherid_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) item_chat_otherid_img.getTag();
                    byte[] imgbyteFromBitmap = BitmapConverter.BitmapToByteArray(chat_msg_dataArrayList.get(position).getImgbitmap());
                    Intent intent = new Intent(context, Activity_Imgexpand.class);
                    intent.putExtra("imgString",imgbyteFromBitmap);
                    context.startActivity(intent);
                }
            });
        }
    }

    public class RightViewHolder extends RecyclerView.ViewHolder{
        TextView item_chat_myid_id;
        TextView item_chat_myid_msg;
        ImageView item_chat_myid_img;
        RightViewHolder(View itemView) {
            super(itemView);
            item_chat_myid_id = itemView.findViewById(R.id.item_chat_myid_id);
            item_chat_myid_msg = itemView.findViewById(R.id.item_chat_myid_msg);
            item_chat_myid_img = itemView.findViewById(R.id.item_chat_myid_img);
            item_chat_myid_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) item_chat_myid_img.getTag();
                    byte[] imgbyteFromBitmap = BitmapConverter.BitmapToByteArray(chat_msg_dataArrayList.get(position).getImgbitmap());
                    Intent intent = new Intent(context, Activity_Imgexpand.class);
                    intent.putExtra("imgString",imgbyteFromBitmap);
                    context.startActivity(intent);
                }
            });
        }
    }



}
