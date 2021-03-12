package com.example.opentalk.VoiceCommunication.Data;

import java.net.InetAddress;

public class OtherUserIP_Data {

    InetAddress private_inetAddress;
    int private_port;
    InetAddress public_inetAddress;
    int public_port;

    public OtherUserIP_Data(InetAddress private_inetAddress, int private_port, InetAddress public_inetAddress, int public_port) {
        this.private_inetAddress = private_inetAddress;
        this.private_port = private_port;
        this.public_inetAddress = public_inetAddress;
        this.public_port = public_port;
    }

    public InetAddress getPrivate_inetAddress() {
        return private_inetAddress;
    }

    public void setPrivate_inetAddress(InetAddress private_inetAddress) {
        this.private_inetAddress = private_inetAddress;
    }

    public int getPrivate_port() {
        return private_port;
    }

    public void setPrivate_port(int private_port) {
        this.private_port = private_port;
    }

    public InetAddress getPublic_inetAddress() {
        return public_inetAddress;
    }

    public void setPublic_inetAddress(InetAddress public_inetAddress) {
        this.public_inetAddress = public_inetAddress;
    }

    public int getPublic_port() {
        return public_port;
    }

    public void setPublic_port(int public_port) {
        this.public_port = public_port;
    }
}
