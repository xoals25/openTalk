package com.example.opentalk.VoiceCommunication.Data;

import java.net.InetAddress;

public class OtherUserIP_Voice_Data {

    String email_id;
    InetAddress inetAddress;
    int port;

    public OtherUserIP_Voice_Data(/*String email_id,*/ InetAddress inetAddress, int port) {
       /* this.email_id = email_id;*/
        this.inetAddress = inetAddress;
        this.port = port;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
