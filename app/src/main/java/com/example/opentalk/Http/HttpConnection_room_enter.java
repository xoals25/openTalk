package com.example.opentalk.Http;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.opentalk.Activity.Activity_Lobby;
import com.example.opentalk.Activity.Activity_Login;
import com.example.opentalk.Activity.Activity_Room_Face;
import com.example.opentalk.Activity.Activity_Room_Voice;
import com.example.opentalk.Data.Logindata;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.opentalk.SharedPreference.PreferenceManager_member.setLoignDataPref;

public class HttpConnection_room_enter extends AsyncTask<String,Void,String> {
    String TAG ="HttpConnection_room_enter";
    String mJsonString;
    ProgressDialog progressDialog;
    Context context;
    String type;
    int room_num;
    String room_title;

    public HttpConnection_room_enter(Context context,String type,int room_num,String room_title) {
        this.context = context;
        this.type = type;
        this.room_num =room_num;
        this.room_title =room_title;
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
        int do_room_id = Integer.valueOf(params[1]);//id(방고유번호)
        String do_room_public = params[2];
        String do_room_pwd = params[3];

        String postparameters = "room_id="+do_room_id+
                "&room_pwd="+do_room_pwd +
                "&room_public="+do_room_public;
        try {
            URL url = new URL(serverURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("POST");
            //입력을 실시할 수 있게 해주는 메소드
            //setDoInput(boolean) : Server 통신에서 입력 가능한 상태로 만듬
            //어플리케이션으로 URL 접속에 write를 실시하는 경우는 doOutput플래그를 true로 설정한다.
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();

            //출력해주기
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(postparameters.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();

            //출력을 해줬으니 서버에서 데이터를 전송받기
            int responseStatusCode = httpURLConnection.getResponseCode();

            InputStream inputStream;
            if(responseStatusCode == HttpURLConnection.HTTP_OK){
                inputStream = httpURLConnection.getInputStream();
            }
            else{
                inputStream = httpURLConnection.getErrorStream();
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

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
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        progressDialog.dismiss();
        Log.d(TAG, "httpresult1: "+result);
        Log.d(TAG, "onClick: 확인3");
        if(result.equals("입장")){
            Log.d(TAG, "httpresult2: ");
            Log.d(TAG, "onClick: 확인4");
            if(type.equals("voice")) {
                Log.d(TAG, "onClick: 확인5");
                Intent intent = new Intent(context, Activity_Room_Voice.class);
                intent.putExtra("room_num",room_num);
                intent.putExtra("room_title",room_title);
                context.startActivity(intent);
            }
            else if(type.equals("face")){
                Intent intent = new Intent(context, Activity_Room_Face.class);
                intent.putExtra("room_num",room_num);
                intent.putExtra("room_title",room_title);
                context.startActivity(intent);
            }
        }
        else if(result.equals("비번불일치")){
            Log.d(TAG, "httpresult4: ");
            Toast.makeText(context,"비번이 일치하지 않습니다.",Toast.LENGTH_SHORT).show();
        }
        else if(result.equals("풀방")){
            Log.d(TAG, "httpresult3: ");
            Toast.makeText(context,"인원이 꽉찼습니다.",Toast.LENGTH_SHORT).show();
        }
        else if(result.equals("방없음")){
            Log.d(TAG, "httpresult5: ");
            Toast.makeText(context,"방이 존재하지 않습니다.",Toast.LENGTH_SHORT).show();
        }
    }
}



