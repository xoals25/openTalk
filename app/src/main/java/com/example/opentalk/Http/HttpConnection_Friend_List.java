package com.example.opentalk.Http;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.opentalk.Activity.Activity_Friend_List;
import com.example.opentalk.Data.Friend_List_Data;
import com.example.opentalk.Handler.Friend_list_Handler;
import com.example.opentalk.Code.HandlerType_Code;
import com.example.opentalk.ServerIp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

//AsyncTask<params,progress,result>이다
public class HttpConnection_Friend_List extends AsyncTask<String,Void,String>{

    String TAG = "HttpConnection_Friend_List";
    ProgressDialog progressDialog;
    Context context;
    Friend_list_Handler friend_list_handler;
    ArrayList<Friend_List_Data> friend_list_dataArrayList;

    public HttpConnection_Friend_List(Context context, Friend_list_Handler friend_list_handler, ArrayList<Friend_List_Data> friend_list_dataArrayList) {
        this.context = context;
        this.friend_list_handler = friend_list_handler;
        this.friend_list_dataArrayList = friend_list_dataArrayList;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context,
                "Please Wait", null, true, true);
    }

    @Override
    protected String doInBackground(String... params) {
        String serverURL = params[0];
        String my_email_id = params[1];

        String post_parameters = "my_email_id="+my_email_id;
        Log.d(TAG, "doInBackground: my_email_id : "+my_email_id);
        Log.d(TAG, "doInBackground: post_parameters : "+post_parameters);
        try{
            URL url = new URL(serverURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setRequestMethod("POST");

            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();

            //출력해주기(클라이언트->서버)
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(post_parameters.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();

            //출력을 해줬으니 서버에서 코드(통신 성공인지 에러인지 알려주는 코드(http통신 규약을 따른다.))를 전송받기(
            int responseStatusCode = httpURLConnection.getResponseCode();

            InputStream inputStream;
            //통신 성공했다면
            if(responseStatusCode == HttpURLConnection.HTTP_OK){
                inputStream = httpURLConnection.getInputStream();
            }
            else{
                inputStream = httpURLConnection.getErrorStream();
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            //위에까지는 알겠는데 StringBuilder가 뭘까? 나중에 찾아보자
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = bufferedReader.readLine()) != null){
                sb.append(line);
            }

            bufferedReader.close();

            return sb.toString();

        }catch (Exception e){
            Log.d(TAG, "Exception: "+e.getMessage());
            return new String("Error: " + e.getMessage());
        }
    }
    //위에 sb.toString()이 result로 넘어온다.
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        progressDialog.dismiss();
        if(!result.equals("실패")){
            try {
                /*노티피알림 클릭으로 들어올 때*/
//                Activity_Friend_List activityFriendListcontext = ((Activity_Friend_List) Activity_Friend_List.activity_Friend_Listcontext);
//                String notifiroomnum="";
//                if(!activityFriendListcontext.notifiroomnum().equals("")){
//                    notifiroomnum=activityFriendListcontext.notifiroomnum();
//                }
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i<jsonArray.length(); i++){
                    JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                    int friend_table_id = Integer.valueOf(jsonObject.get("friend_table_id").toString());
                    String friend_email = jsonObject.get("friend_email_id").toString();
                    String friend_nickname = jsonObject.get("friend_nickname").toString();
                    //친구 접속여부 알려주는 기능
                    String friend_connect_check = jsonObject.get("friend_connect_check").toString();
                    //친구 고유번호(채팅에 사용)
                    String uniquename = jsonObject.get("uniquename").toString();
                    String last_chat_msg = jsonObject.get("last_chat_msg").toString();
                    if(last_chat_msg==null){
                        last_chat_msg="";
                    }
                    int dontread_num = jsonObject.getInt("dontread_num");
//                    /*만약 노티피로 들어온 방의 안읽은 메세지는 0개로 만들어주기*/
//                    if(notifiroomnum.equals(uniquename)){
//                        dontread_num = 0;
//                    }
                    Log.d(TAG, "run: 확인작업 last_chat_msg : "+last_chat_msg);
                    Log.d(TAG, "run: 확인작업 dontread_num : "+dontread_num);
//                    Log.d(TAG, "run: uniquename : "+uniquename);
                    /****이미지는 나중에****/
                    //여기서는 이미지는 없음으로 받고 밑에서 이미지를 따로 받아오기.
                    Friend_List_Data friend_list_data = new Friend_List_Data(friend_table_id,friend_email,friend_nickname,friend_connect_check,"없음",uniquename,dontread_num,last_chat_msg);
                    friend_list_dataArrayList.add(friend_list_data);
                }
                Activity_Friend_List activityFriendListcontext = ((Activity_Friend_List) Activity_Friend_List.activity_Friend_Listcontext);
                if(activityFriendListcontext!=null){
                    activityFriendListcontext.notifiPassActivity();
                }
                //리사이클러뷰 아이템 리셋 해주는 핸들러
                friend_list_handler.sendEmptyMessage(HandlerType_Code.HandlerType.FRIEND_LIST_RECYCLERVIEW);
                //다하고 volley통신을 이용해서 이미지 받아오는 작업을 해서

                /*
                * 생각해 볼 것 : 친구가 수락을 하면 소켓으로 arraylist.add 되는데 이때 http통신으로 이미지를 받아오게된다면 중간에 꼬이지는 않을까???
                * */
                String serverurl = "http://"+ ServerIp.IP_ADDRESS_ADD_FOLDER_NAME +"/profile_img_load.php";
                for (int i=0; i<friend_list_dataArrayList.size(); i++){
                    VolleyprofileIMG_Friend_List volleyprofileIMG = new VolleyprofileIMG_Friend_List(friend_list_dataArrayList,friend_list_handler,friend_list_dataArrayList.get(i).getFriend_email(),i);
                    volleyprofileIMG.profile_upload(serverurl);
                }

            } catch (JSONException e) {
                Log.d(TAG, "onPostExecute: JSONException e.message : "+e.getMessage());
            }

        }
    }
}
