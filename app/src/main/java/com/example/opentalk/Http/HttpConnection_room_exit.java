package com.example.opentalk.Http;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.opentalk.Activity.Activity_Room_Face;
import com.example.opentalk.Activity.Activity_Room_Voice;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnection_room_exit extends AsyncTask<String,Void,String> {
    String TAG ="HttpConnection_room_exit";
    String mJsonString;
    Context context;
//    int room_num;

    public HttpConnection_room_exit(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String doInBackground(String... params) {
        String serverURL = params[0];
        int do_room_id = Integer.valueOf(params[1]);//id(방고유번호)

        String postparameters = "room_id="+do_room_id;
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
        Log.d(TAG, "httpresult1: "+result);
    }
}



