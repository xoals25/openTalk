package com.example.opentalk.VoiceCommunication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class Private_IPAddress {
    String TAG = "Private_Public_IPAddress";
    Context context;

    public Private_IPAddress(Context context){
        this.context = context;
    }

    //wifi검사기
    public String checkAvailableConnection(){
        ConnectivityManager connMgr =(ConnectivityManager)this.context.getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        try {
            if(wifi.isAvailable()){

                WifiManager myWifiManager = (WifiManager)context.getApplicationContext().getSystemService(context.WIFI_SERVICE);
                WifiInfo myWifiInfo = myWifiManager.getConnectionInfo();
                int ipAddress = myWifiInfo.getIpAddress();
                InetAddress inetAddress_init = InetAddress.getByName(IpAddress());
                if(ipAddress==0){
                    Log.d(TAG, "0..0.0.0입니다.: ");
                    Log.d(TAG, "wifi 사설 ip가 0.0.0.0으로나와서 : "+IpAddress());
                    return IpAddress();
                }
                else{
                    InetAddress inetAddress;
                    inetAddress = InetAddress.getByName(android.text.format.Formatter.formatIpAddress(ipAddress));
                    Log.d(TAG, "checkAvailableConnection: asd : "+inetAddress);
                    return android.text.format.Formatter.formatIpAddress(ipAddress);
                }
            }
            else if(mobile.isAvailable()){
                GetLocalIpAddress();
//                Toast.makeText(this,"3G Available",Toast.LENGTH_SHORT).show();
                InetAddress inetAddress = InetAddress.getByName(GetLocalIpAddress());
                return GetLocalIpAddress();
            }
            else{
//                Toast.makeText(this,"No Network Available",Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    //이것도 제외하는게 좋을듯
    private String GetLocalIpAddress(){
        try {
            for(Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();){
                NetworkInterface intf = en.nextElement();
                for(Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();){
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if(!inetAddress.isLoopbackAddress()){
                        return Formatter.formatIpAddress(inetAddress.hashCode());
//                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        }catch (Exception e){
            return "ERROR Obtaining IP";
        }
        return "No IP Available";
    }

    public static String IpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {ex.printStackTrace();}
        return null;
    }

//    public String getLocalIpAddress2() {
//        try {
//            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
//            for(NetworkInterface intf : interfaces){
//                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
//                for(InetAddress addr : addrs){
//                    if(!addr.isLoopbackAddress()){
//                        String sAddr = addr.getHostAddress();
//                        return sAddr;
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            Log.e("Testing", ex.toString());
//        }
//        return "";
//    }

}
