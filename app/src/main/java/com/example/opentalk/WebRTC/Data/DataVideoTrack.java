package com.example.opentalk.WebRTC.Data;

import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoTrack;

public class DataVideoTrack {

    VideoTrack videoTrack;
    String socketId; //내자신은 me
    SurfaceViewRenderer surfaceViewRenderer;

    public DataVideoTrack(VideoTrack videoTrack, String socketId, SurfaceViewRenderer surfaceViewRenderer) {
        this.videoTrack = videoTrack;
        this.socketId = socketId;
        this.surfaceViewRenderer = surfaceViewRenderer;
    }

    public VideoTrack getVideoTrack() {
        return videoTrack;
    }

    public void setVideoTrack(VideoTrack videoTrack) {
        this.videoTrack = videoTrack;
    }

    public String getSocketId() {
        return socketId;
    }

    public void setSocketId(String socketId) {
        this.socketId = socketId;
    }

    public SurfaceViewRenderer getSurfaceViewRenderer() {
        return surfaceViewRenderer;
    }

    public void setSurfaceViewRenderer(SurfaceViewRenderer surfaceViewRenderer) {
        this.surfaceViewRenderer = surfaceViewRenderer;
    }
}
