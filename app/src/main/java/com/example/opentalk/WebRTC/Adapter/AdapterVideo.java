package com.example.opentalk.WebRTC.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.opentalk.R;
import com.example.opentalk.WebRTC.Data.DataVideoTrack;

import org.webrtc.EglBase;
import org.webrtc.PeerConnection;
import org.webrtc.SurfaceViewRenderer;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterVideo extends RecyclerView.Adapter<AdapterVideo.CustomViewHolder> {

    ArrayList<DataVideoTrack> dataVideoTrackArrayList;
    HashMap<String, PeerConnection> peerConnectionMap;
    EglBase.Context eglBaseContext;
    int devicewidth;
    int deviceheight;
    int x;
    float y;


    public AdapterVideo(ArrayList<DataVideoTrack> dataVideoTrackArrayList, HashMap<String, PeerConnection> peerConnectionMap, EglBase.Context eglBaseContext, int devicewidth, int deviceheight,int x,float y) {
        this.dataVideoTrackArrayList = dataVideoTrackArrayList;
        this.peerConnectionMap = peerConnectionMap;
        this.eglBaseContext = eglBaseContext;
        this.devicewidth = devicewidth;
        this.deviceheight = deviceheight;
        this.x = x;
        this.y = y;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video,parent,false);
        AdapterVideo.CustomViewHolder holder = new AdapterVideo.CustomViewHolder(view);
        Log.d("RECYCLERVIEW", "onCreateViewHolder: ");
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.rootView.setTag(position);
//        Log.d("ADAPTER", "onBindViewHolder: dataVideoTrackArrayList 사이즈 확인 : "+dataVideoTrackArrayList.size());
//        Log.d("ADAPTER", "onBindViewHolder: dataVideoTrackArrayList position 확인 : "+position);
//        Log.d("ADAPTER", "onBindViewHolder: dataVideoTrackArrayList socketid확인 : "+dataVideoTrackArrayList.get(position).getSocketId());
        if(dataVideoTrackArrayList.get(position).getSocketId()!=null) {
            dataVideoTrackArrayList.get(position).getVideoTrack().addSink(holder.item_SurfaceViewRender);
            dataVideoTrackArrayList.get(position).setSurfaceViewRenderer(holder.item_SurfaceViewRender);
        }
//        Log.d("ADAPTER", "onBindViewHolder: dataVideoTrackArrayList.get(position).getVideoTrack() : "+dataVideoTrackArrayList.get(position).getVideoTrack());
    }



    @Override
    public int getItemCount() {
        return (null != dataVideoTrackArrayList ? dataVideoTrackArrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        SurfaceViewRenderer item_SurfaceViewRender;
        LinearLayout item_linearlayout;
        View rootView;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            item_SurfaceViewRender = (SurfaceViewRenderer)itemView.findViewById(R.id.item_SurfaceViewRender);
            item_linearlayout =(LinearLayout)itemView.findViewById(R.id.item_linearlayout);
            item_SurfaceViewRender.setMirror(true);
            item_SurfaceViewRender.init(eglBaseContext, null);
            item_linearlayout.getLayoutParams().width = (int)(devicewidth / x);
            item_linearlayout.getLayoutParams().height = (int) (deviceheight / y);
            rootView = itemView;
        }
    }

    public interface VideoSetting{

    }
}
