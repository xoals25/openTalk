package com.example.opentalk.Socket_my;

import android.graphics.Bitmap;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.example.opentalk.Activity.Activity_Friend_Add;
import com.example.opentalk.Activity.Activity_Friend_Chat;
import com.example.opentalk.Activity.Activity_Lobby;
import com.example.opentalk.Activity_Tab_Fragment.Fragment_friend_wait;
import com.example.opentalk.BitmapConverter;
import com.example.opentalk.Code.Connect_Code;
import com.example.opentalk.Code.ViewType_Code;
import com.example.opentalk.Data.Chat_Msg_Data_Friend;
import com.example.opentalk.Data.FriendWaitData;
import com.example.opentalk.Data.Friend_List_Data;
import com.example.opentalk.Handler.Friend_list_Handler;
import com.example.opentalk.Code.HandlerType_Code;
import com.example.opentalk.R;
import com.example.opentalk.TimeProcess;
import com.example.opentalk.UrlToBitmapThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

public class Socket_friend_list_receive_thread extends Thread {
    String TAG = "Socket_friend_list_receive_thread";
    Friend_list_Handler friend_list_handler;
//    Friend_wait_Handler friend_wait_handler;
    ArrayList<Friend_List_Data> friend_list_dataArrayList;
    BufferedReader bufferedReader;
    Socket socket_friend_list;
    public boolean onoff =true;

    public Socket_friend_list_receive_thread(Friend_list_Handler friend_list_handler,ArrayList<Friend_List_Data> friend_list_dataArrayList,Socket socket){
        this.friend_list_dataArrayList = friend_list_dataArrayList;
        this.friend_list_handler = friend_list_handler;
        this.socket_friend_list = socket;
        Log.d(TAG, "Socket_friend_list_receive_thread: friend_wait_handler : "+Fragment_friend_wait.friend_wait_handler_static);
        try{
            Log.d(TAG, "Socket_friend_list_receive_thread: socket : "+socket);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch (Exception e){
            Log.d(TAG, "Socket_friend_list_receive_thread: inputStream error message : "+e.getMessage());
        }
    }

    @Override
    public void run() {
        super.run();
        String friend_list_socket_data = null;
        try{
            while((friend_list_socket_data = bufferedReader.readLine()) != null && onoff==true){
                Log.d(TAG, "run: friend_list_socket_data : "+friend_list_socket_data);
//                Log.d(TAG, "Socket_friend_list_receive_thread: friend_wait_handler : "+Fragment_friend_wait.friend_wait_handler_static);
                String type = null;
                String friend_email = null;
                String friend_nickname = null;
                int friend_table_id = -1;
                String friend_profile_img = null;
                String request_email_id = null;

                JSONObject jsonObject = new JSONObject(friend_list_socket_data);

                type = jsonObject.getString("type");

                //?????? ?????????
                if(type.equals("friend_connect")){
                    String connect_friend_email = jsonObject.get("friend_email").toString();
                    Log.d(TAG, "run: connect_friend_email : "+connect_friend_email);
                    //??????????????? ???????????? arraylist?????? ????????? ????????? ????????? ????????????
                    for (int i=0; i<friend_list_dataArrayList.size(); i++){
                        //????????????
                        if(friend_list_dataArrayList.get(i).getFriend_email().equals(connect_friend_email)){
                            Log.d(TAG, "run: ????????? ?????? ?????? ?????????.");
                            //?????? ????????? ????????? ????????? ???????????? online?????? ??????
                            friend_list_dataArrayList.get(i).setConnect(Connect_Code.Connect_Type.CONNECT_ONLINE);
                            //?????????????????? ????????? ??????
                            friend_list_handler.sendEmptyMessage(HandlerType_Code.HandlerType.FRIEND_LIST_RECYCLERVIEW);
                            break;
                        }
                    }

                }
                else if(type.equals("friend_disconnect")){
                    String dis_connect_friend_email = jsonObject.getString("friend_email");
                    //1??? friend_list?????? ?????? ???????????? connect??? online -> offline?????? ???????????????
                    for (int i=0; i<friend_list_dataArrayList.size(); i++){
                        //????????????
                        if(friend_list_dataArrayList.get(i).getFriend_email().equals(dis_connect_friend_email)){
                            Log.d(TAG, "run: ????????? ?????? ?????? ?????????.");
                            //?????? ????????? ????????? ????????? ???????????? online?????? ??????
                            friend_list_dataArrayList.get(i).setConnect(Connect_Code.Connect_Type.CONNECT_OFFLINE);
                            //?????????????????? ????????? ??????
                            friend_list_handler.sendEmptyMessage(HandlerType_Code.HandlerType.FRIEND_LIST_RECYCLERVIEW);
                            break;
                        }
                    }
                }
                else if(type.equals("friend_request_accept")){
                    friend_email = jsonObject.getString("friend_email");
                    friend_nickname = jsonObject.getString("friend_nickname");
                    friend_table_id = jsonObject.getInt("friend_table_id");
                    friend_profile_img = jsonObject.getString("friend_profile_img");
                    String uniquename = jsonObject.getString("uniquename");
                    //?????? ?????? ?????? ?????????
                    String friend_connect_check = jsonObject.getString("friend_connect_check");

                    Friend_List_Data friend_list_data = new Friend_List_Data(friend_table_id,friend_email,friend_nickname, friend_connect_check,friend_profile_img,uniquename,0,"");
                    friend_list_dataArrayList.add(friend_list_data);
                    Log.d(TAG, "run: friend_list_dataArrayList.size()"+friend_list_dataArrayList.size());
                    friend_list_handler.sendEmptyMessage(HandlerType_Code.HandlerType.FRIEND_LIST_RECYCLERVIEW);
                }
                else if(type.equals("friend_request")){
                    //????????? ????????? id ??????
                    request_email_id = jsonObject.getString("request_email_id");

                    Activity_Friend_Add AFA_context = ((Activity_Friend_Add)Activity_Friend_Add.context_Activity_Friend_Add);
                    if(AFA_context!=null) {
                        if (request_email_id.equals("myself")) {
                            Fragment_friend_wait.friend_wait_handler_static.sendEmptyMessage(HandlerType_Code.HandlerType.FRIEND_ADD_REQUEST_MYSELF_FAILURE);
                        } else if (request_email_id.equals("noexist")) {
                            Fragment_friend_wait.friend_wait_handler_static.sendEmptyMessage(HandlerType_Code.HandlerType.FRIEND_ADD_REQUEST_NOEXIST_FAILURE);
                        } else if (request_email_id.equals("aleady friend")) {
                            Fragment_friend_wait.friend_wait_handler_static.sendEmptyMessage(HandlerType_Code.HandlerType.FRIEND_ADD_REQUEST_ALEADY_FRIEND);
                        } else if (request_email_id.equals("awaiting")) {
                            Fragment_friend_wait.friend_wait_handler_static.sendEmptyMessage(HandlerType_Code.HandlerType.FRIEND_ADD_REQUEST_AWAITING);
                        } else {
                            //?????? Fragment_friend_wait??? ????????????????????? ?????????????????? handler?????? ?????? ??????
                            String imgpath = jsonObject.getString("imgpath");
                            FriendWaitData friendWaitData = new FriendWaitData(imgpath,request_email_id,null);
                            Fragment_friend_wait.request_email_arrayList.add(friendWaitData);
                            try {
                                Fragment_friend_wait.friend_wait_handler_static.sendEmptyMessage(HandlerType_Code.HandlerType.FRIEND_WAIT_RECYCLERVIEW_CHANGE);
                                UrlToBitmapThread urlToBitmapThread = new UrlToBitmapThread(Fragment_friend_wait.request_email_arrayList,Fragment_friend_wait.friend_wait_handler_static);
                                urlToBitmapThread.start();
                            } catch (Exception e) {
                                Log.d(TAG, "run: Fagment_friend_wait.friend_wait_handler_static Exception e.getMessage : " + e.getMessage());
                            }
                        }
                    }
                }
                /*????????? ????????????????????? ??? ?????????????????? ??????*/
                else if(type.equals("disconnect_friend_list")){
                    String disconnectuserid = jsonObject.getString("userid");
                    int size = friend_list_dataArrayList.size();
                    for (int i=0; i<size; i++){
                        String friendid = friend_list_dataArrayList.get(i).getFriend_email();
                        if(friendid.equals(disconnectuserid)){
                            friend_list_dataArrayList.remove(i);
                            friend_list_handler.sendEmptyMessage(HandlerType_Code.HandlerType.FRIEND_LIST_RECYCLERVIEW);
                        }
                    }
                }
                /*??????????????? ????????? ????????????????????? ?????????????????? ??????*/
                else if(type.equals("disconnect_friend_request")){
                    String disconnectuserid = jsonObject.getString("userid");
                    Activity_Friend_Add AFA_context = ((Activity_Friend_Add)Activity_Friend_Add.context_Activity_Friend_Add);
                    if(AFA_context!=null) {
                        if (Fragment_friend_wait.request_email_arrayList!=null) {
                            int size = Fragment_friend_wait.request_email_arrayList.size();
                            int position = -1;
                            for (int i = 0; i < size; i++) {
                                if(Fragment_friend_wait.request_email_arrayList.get(i).getUserid().equals(disconnectuserid)){
                                    Fragment_friend_wait.request_email_arrayList.remove(i);
                                    position=i;
                                    if (Fragment_friend_wait.friend_wait_handler_static != null) {
                                        Message message = new Message();
                                        message.what = HandlerType_Code.HandlerType.FRIEND_WAIT_REMOVE_RECYCLERVIEW;
                                        message.arg1 = position;
                                        Fragment_friend_wait.friend_wait_handler_static.sendMessage(message);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                /*?????????????????? ?????? ?????????*/
                else if(type.equals("friend_enter")){
                    Activity_Friend_Chat context = ((Activity_Friend_Chat)Activity_Friend_Chat.Activity_Friend_Chat_context);
                    String roomnum = jsonObject.getString("roomnum");
                    Log.d(TAG, "run: ?????? ?????? 1 : roomnum : "+roomnum);
                    if(context!=null) {
                        if(roomnum.equals(context.roomnum)) {
                            int size = context.chat_msg_data_ArrayList.size();
                            //???????????? ????????? ???????????? ???????????????
                            for (int i=(size-1);i>=0;i--) {
                                Log.d(TAG, "run: i???? : "+i);
                                if (context.chat_msg_data_ArrayList.get(i).getUsernickname().equals(Activity_Lobby.usernickname)) {
                                    //??????????????? ???????????? ??????????????? ??? ???????????? ????????? ?????????
                                    if (context.chat_msg_data_ArrayList.get(i).getReadstate().equals("read")) {
                                        break;
                                    } else {
                                        Log.d(TAG, "run:context.chat_msg_data_ArrayList.get(i).getReadstate() :" + context.chat_msg_data_ArrayList.get(i).getReadstate());
                                        context.chat_msg_data_ArrayList.get(i).setReadstate("read");
                                        //????????? i?????? ?????? ???????????????.
//                                        Message message = new Message();
//                                        message.what = HandlerType_Code.HandlerType.ADAPTER_FRIEND_CHAT_LIST_NOTIFYDATASETCHANGED_READ_SETTING;
//                                        message.arg1 = i;
//                                        context.face_chat_friend_handler.sendMessage(message);
                                    }
                                }
                            }
                            context.face_chat_friend_handler.sendEmptyMessage(HandlerType_Code.HandlerType.ADAPTER_FRIEND_CHAT_LIST_NOTIFYDATASETCHANGED_READ_ALL);
                        }
                    }
                }
                /*?????????????????? ?????? ?????????*/
                else if(type.equals("friend_enter_me")){
                    Log.d(TAG, "run:  ??????  friend_enter_me ");
                    JSONArray jsonArray = jsonObject.getJSONArray("allmsg");
                    Activity_Friend_Chat context = ((Activity_Friend_Chat)Activity_Friend_Chat.Activity_Friend_Chat_context);
                    if(context!=null) {
                        synchronized (context.chat_msg_data_ArrayList) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Log.d(TAG, "run: jsonArray.length : "+jsonArray.length());
                                if(jsonArray.get(i) instanceof JSONObject) {
                                    Log.d(TAG, "run: ?????? instancoof JSONObject");
                                    JSONObject item = jsonArray.getJSONObject(i);
                                    String content = item.getString("content");
                                    String fromnickname = item.getString("nickname");
                                    String readstate = item.getString("readstate");
                                    String senddatebefore = item.getString("datetime");
                                    String senddate = TimeProcess.hourminProcess(senddatebefore);
                                    String msgtype = item.getString("msgtype");
                                    String roomtitle = "";
                                    String openchatroomid = "-1";
//                                    Log.d(TAG, "run: ?????? ?????? ?????? : content : "+content);
                                    if (msgtype.equals("textmsg")) {

                                    } else if (msgtype.equals("invitemsg")) {
                                        openchatroomid = item.getString("openchatroomid");
                                    }
                                    //?????? ???????????????
                                    yearmonthdatSet(context, senddatebefore, readstate, senddate, "day");
                                    if (fromnickname.equals(Activity_Lobby.usernickname)) {
                                        Chat_Msg_Data_Friend chat_msg_data_friend = new Chat_Msg_Data_Friend(fromnickname, content, ViewType_Code.ViewType.RIGHT_CONTENT, readstate, senddate, null, msgtype, openchatroomid, roomtitle);
                                        context.chat_msg_data_ArrayList.add(chat_msg_data_friend);
                                        Log.d(TAG, "run: ?????? ?????? ??????1 : content : "+content);

                                    } else {
                                        Chat_Msg_Data_Friend chat_msg_data_friend = new Chat_Msg_Data_Friend(fromnickname, content, ViewType_Code.ViewType.LEFT_CONTENT, readstate, senddate, null, msgtype, openchatroomid, roomtitle);
                                        context.chat_msg_data_ArrayList.add(chat_msg_data_friend);
                                        Log.d(TAG, "run: ?????? ?????? ??????2 : content : "+content);
                                    }
                                }
                                else if(jsonArray.get(i) instanceof String){
                                    String[] args = jsonArray.getString(i).split("/");
                                    int numberOfchat = Integer.valueOf(args[0]);
                                    int lastChatId = Integer.valueOf(args[1]);
                                    context.firstMysqlCahtnum = numberOfchat;
                                    context.lastChatId = lastChatId;
                                }
                            }
                        }
                        //
                        context.face_chat_friend_handler.sendEmptyMessage(HandlerType_Code.HandlerType.ADAPTER_FRIEND_CHAT_LIST_NOTIFYDATASETCHANGED_ONE);
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "run: 1");
                                context.friend_chat_recyclerview.setVisibility(View.VISIBLE);
                                context.pagingCheck=true;
                            }
                        });
                    }

                }
                /*????????? ????????? ?????????*/
                else if(type.equals("friend_chat")){
                    JSONObject friend_chat_json = jsonObject.getJSONObject("chat_data_json");
                    Activity_Friend_Chat context = ((Activity_Friend_Chat)Activity_Friend_Chat.Activity_Friend_Chat_context);
                    String msg_send_id = friend_chat_json.getString("from");
                    String msg_send_nickname = friend_chat_json.getString("from_nickname");
                    String msg = friend_chat_json.getString("msg");
                    String readstate = friend_chat_json.getString("readstate");
                    String senddatebefore = friend_chat_json.getString("date");
                    String senddate = TimeProcess.hourminProcess(senddatebefore); //????????? ?????? ????????? ???,???
                    String friendchat_roomnum = friend_chat_json.getString("roomnum");
                    String msgtype = friend_chat_json.getString("msgtype");
                    String roomtitle="";
                    String openchatroomid="-1";
                    if(msgtype.equals("textmsg")){

                    }else if(msgtype.equals("invitemsg")){
                        roomtitle = friend_chat_json.getString("roomtitle");
                        openchatroomid = friend_chat_json.getString("openchatroomid");
                    }
                    if(context!=null){
                        if(context.roomnum.equals(friendchat_roomnum)){
                            //?????? ???????????????
                            yearmonthdatSet(context,senddatebefore,readstate,senddate,msgtype);
                            Chat_Msg_Data_Friend chat_msg_data_friend = new Chat_Msg_Data_Friend(msg_send_nickname,msg, ViewType_Code.ViewType.LEFT_CONTENT,readstate,senddate,null,msgtype,openchatroomid,roomtitle);
                            context.chat_msg_data_ArrayList.add(chat_msg_data_friend);
                            context.face_chat_friend_handler.sendEmptyMessage(HandlerType_Code.HandlerType.ADAPTER_FRIEND_CHAT_LIST_NOTIFYDATASETCHANGED_ONE);

                            friend_list_dataArrayList.get(context.roomPosition).setLastchatmsg(msg);
                            Message message = new Message();
                            message.what = HandlerType_Code.HandlerType.FRIEND_LIST_RECYCLERVIEW_CHANGE_ONE;
                            message.arg1 = context.roomPosition;
                            friend_list_handler.sendMessage(message);
                        }
                        //????????? ????????? ?????? ????????? ?????? ?????? ???????????? ?????? ?????? ????????? ????????????
                        else{

                            friendlist_chatSet(friendchat_roomnum,msg);
                        }
                    }
                    //????????? ?????? ?????? ????????? ????????????
                    else{

                        friendlist_chatSet(friendchat_roomnum,msg);
                    }
                }
                /*?????? ?????? ??????*/
                else if(type.equals("friend_chat_me")){
                    JSONObject friend_chat_json = jsonObject.getJSONObject("chat_data_json");
                    Activity_Friend_Chat context = ((Activity_Friend_Chat)Activity_Friend_Chat.Activity_Friend_Chat_context);
                    String msg_send_id = friend_chat_json.getString("from");
                    String msg_send_nickname = friend_chat_json.getString("from_nickname");
                    String msg = friend_chat_json.getString("msg");
                    String readstate = friend_chat_json.getString("readstate");
                    Log.d(TAG, "run: friend_chat_me readstate : "+readstate);
                    String senddatebefore = friend_chat_json.getString("date");
                    String senddate = TimeProcess.hourminProcess(senddatebefore);
                    String msgtype = friend_chat_json.getString("msgtype");
                    String roomnum = friend_chat_json.getString("roomnum");
                    if(context!=null){
                        Log.d(TAG, "friend_chat ?????? : "+roomnum);
                        if(context.roomnum.equals(roomnum)){
                            //?????? ???????????????
                            yearmonthdatSet(context,senddatebefore,readstate,senddate,"yearmonthday");
                            Chat_Msg_Data_Friend chat_msg_data_friend = new Chat_Msg_Data_Friend(msg_send_nickname,msg, ViewType_Code.ViewType.RIGHT_CONTENT,readstate,senddate,null,msgtype,"-1","");
                            context.chat_msg_data_ArrayList.add(chat_msg_data_friend);
                            context.face_chat_friend_handler.sendEmptyMessage(HandlerType_Code.HandlerType.ADAPTER_FRIEND_CHAT_LIST_NOTIFYDATASETCHANGED_ONE);

                            friend_list_dataArrayList.get(context.roomPosition).setLastchatmsg(msg);
                            Log.d(TAG, "run: roomPosition : "+context.roomPosition);
                            Message message = new Message();
                            message.what = HandlerType_Code.HandlerType.FRIEND_LIST_RECYCLERVIEW_CHANGE_ONE;
                            message.arg1 = context.roomPosition;
                            friend_list_handler.sendMessage(message);
                        }
                        //????????? ????????? ?????? ????????? ?????? ?????? ???????????? ?????? ?????? ????????? ????????????
                        else{
                            friendlist_chatSet(roomnum,msg);
                        }
                    }
                    //????????? ?????? ?????? ????????? ????????????
                    else{
                        friendlist_chatSet(roomnum,msg);
                    }
                }
            }
        }catch (IOException | JSONException e){
            Log.d(TAG, "run: error message : "+e.getMessage());
        }
    }


    //?????? ??? ??????, ????????? ?????? ?????? ????????????.
    private void yearmonthdatSet(Activity_Friend_Chat context,String senddatebefore,String readstate,String senddate,String msgtype){
        String yearmonthday = TimeProcess.yearmonthday(senddatebefore);//????????? ??????
        if(context.todayyearmonthday.equals("")){
            Chat_Msg_Data_Friend chat_msg_data_friend = new Chat_Msg_Data_Friend("nonickname",yearmonthday, ViewType_Code.ViewType.CENTER_CONTENT,readstate,senddate,null,msgtype,"-1","");
            context.chat_msg_data_ArrayList.add(chat_msg_data_friend);
            context.todayyearmonthday=yearmonthday;
        }
        else{
            if(!context.todayyearmonthday.equals(yearmonthday)){
                Chat_Msg_Data_Friend chat_msg_data_friend = new Chat_Msg_Data_Friend("nonickname",yearmonthday, ViewType_Code.ViewType.CENTER_CONTENT,readstate,senddate,null,msgtype,"-1","");
                context.chat_msg_data_ArrayList.add(chat_msg_data_friend);
                context.todayyearmonthday=yearmonthday;
            }
        }
    }
    /*????????????????????? ?????? ?????????, ????????? ????????? ?????? ??????*/
    private void friendlist_chatSet(String roomnum,String msg){
        int listsize = friend_list_dataArrayList.size();
        for(int i=0; i<listsize; i++){
            if(friend_list_dataArrayList.get(i).getUniquename().equals(roomnum)){
                    friend_list_dataArrayList.get(i).setChatnum(friend_list_dataArrayList.get(i).getChatnum()+1);
                    friend_list_dataArrayList.get(i).setLastchatmsg(msg);
                    Message message = new Message();
                    message.what = HandlerType_Code.HandlerType.FRIEND_LIST_RECYCLERVIEW_CHANGE_ONE;
                    message.arg1 = i;
                    friend_list_handler.sendMessage(message);
                    break;
            }
        }
    }
}
