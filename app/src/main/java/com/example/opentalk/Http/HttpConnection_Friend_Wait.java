package com.example.opentalk.Http;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.example.opentalk.BitmapConverter;
import com.example.opentalk.Data.FriendWaitData;
import com.example.opentalk.Handler.Friend_wait_Handler;
import com.example.opentalk.Code.HandlerType_Code;
import com.example.opentalk.UrlToBitmapThread;
import com.google.gson.JsonObject;

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

public class HttpConnection_Friend_Wait extends AsyncTask<String,Void,String> {

    String TAG ="HttpConnection_Friend_Wait";
    ProgressDialog progressDialog;
    Context context;
    Friend_wait_Handler friend_wait_handler;
    ArrayList<FriendWaitData> request_email_arrayList;

    public HttpConnection_Friend_Wait(Context context, Friend_wait_Handler friend_wait_handler,ArrayList<FriendWaitData> request_email_arrayList) {
        this.context = context;
        this.friend_wait_handler = friend_wait_handler;
        this.request_email_arrayList = request_email_arrayList;
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

            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("POST");

            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();

            //???????????????(???????????????->??????)
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(post_parameters.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();

            //????????? ???????????? ???????????? ??????(?????? ???????????? ???????????? ???????????? ??????(http?????? ????????? ?????????.))??? ????????????(
            int responseStatusCode = httpURLConnection.getResponseCode();

            InputStream inputStream;
            //?????? ???????????????
            if(responseStatusCode == HttpURLConnection.HTTP_OK){
                inputStream = httpURLConnection.getInputStream();
            }
            else{
                inputStream = httpURLConnection.getErrorStream();
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            //??????????????? ???????????? StringBuilder??? ??????? ????????? ????????????
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

    //?????? sb.toString()??? result??? ????????????.
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        progressDialog.dismiss();
        Log.d(TAG, "onPostExecute: result : "+result);
        if(!result.equals("??????")){
            Log.d(TAG, "friend_wait_?????? ?????? : ");
            try {
                JSONArray jsonArray = new JSONArray(result);
                Log.d(TAG, "onPostExecute: jsonArray : "+jsonArray);
                synchronized (request_email_arrayList) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);
                        String touserid = item.getString("touserid"); //??? ??????
                        String imgpath = item.getString("imgpath"); // ??? ??????
                        Log.d(TAG, "friend_wait_?????? ??????: touserid : " + touserid);
                        Log.d(TAG, "friend_wait_?????? ??????: imgpath : " + imgpath);
                        FriendWaitData friendWaitData = new FriendWaitData(imgpath, touserid, null);
                        request_email_arrayList.add(friendWaitData);
                    }
                }
                friend_wait_handler.sendEmptyMessage(HandlerType_Code.HandlerType.FRIEND_WAIT_RECYCLERVIEW_CHANGE);
                UrlToBitmapThread urlToBitmapThread = new UrlToBitmapThread(request_email_arrayList,friend_wait_handler);
                urlToBitmapThread.start();
            } catch (JSONException e) {
                Log.d(TAG, "onPostExecute: JSONException e.message : "+e.getMessage());
            }
        }
    }
}
