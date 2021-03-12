package com.example.opentalk.Http;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.opentalk.Adapter.Adapter_Lobby_rooms;
import com.example.opentalk.Data.Lobby_Rooms_Data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class HttpConnection_Lobby_Rooms extends AsyncTask<String,Void,String> {

    String TAG ="HttpConnection_room_enter";
    ProgressDialog progressDialog;
    Context context;
    Adapter_Lobby_rooms adapter_lobby_rooms;
    ArrayList<Lobby_Rooms_Data> lobby_rooms_dataArrayList = new ArrayList<>();
//    int room_num;

    public HttpConnection_Lobby_Rooms(Context context, Adapter_Lobby_rooms adapter_lobby_rooms, ArrayList<Lobby_Rooms_Data> lobby_rooms_dataArrayList) {
        this.context = context;
        this.adapter_lobby_rooms = adapter_lobby_rooms;
        this.lobby_rooms_dataArrayList = lobby_rooms_dataArrayList;
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
        int do_rooms_num = Integer.valueOf(params[1]);

        String postparameters = "rooms_num="+do_rooms_num;
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

            //출력해주기(클라이언트->서버)
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(postparameters.getBytes("UTF-8"));
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

        if(!result.equals("실패")){
            Log.d(TAG, "httpresult2: ");
            /*서버에서 json형태로 작성한 값을 string형태로 보내준값 담아주기*/
            //밑에서 다시 json값으로 변경해줄것이다.
            String TAG_JSON ="rooms_create"; //json에 담긴 배열에 있는 번호 순서대로 객체담아주기
            String TAG_ID="rooms_id"; //방 고유번호
            String TAG_TITLE = "rooms_title"; //방 제목
            String TAG_TYPE = "rooms_type"; //방 종류(음성채팅,화상채팅)
            String TAG_PUBLIC = "rooms_public"; //방 공개방인지 비공개방인지
            String TAG_PERSON_NUMLIMIT = "rooms_person_numlimit"; //방 최대인원
            String TAG_PERSON_ATTEND_NUM = "rooms_person_attend_num";//방 현재인원

            lobby_rooms_dataArrayList.clear();//
            try {
                Log.d(TAG, "httpresult3: ");
                /*String 데이터형식->json객체 형식으로 우선 변경*/
                //이유 {[{}]} 으로 보내주기 때문에
                //우선 json이 첫번째가 {}와 같이 객체 형식이기때문에 json객체로 우선 변경
                JSONObject jsonObject =  new JSONObject(result);
                /*json객체안에 있는 Array형식 가져오기*/
                //{}안에 []배열이 담겨져있어 원하는 name 여기선 rooms_create의 name에 해당하는 배열 가져온것이다.
                JSONArray jsonArray =jsonObject.getJSONArray(TAG_JSON);
                Log.d(TAG, "httpresult4: ");
                /*배열안에 담겨있는  [{}]객체 갯수 만큼 for문 돌려줘서 데이터뽑아줘서 arraylist에 담기*/
                for (int i=0; i<jsonArray.length(); i++){
                    JSONObject item = jsonArray.getJSONObject(i);
                    int room_id = item.getInt(TAG_ID); //방 고유번호
                    String room_title = item.getString(TAG_TITLE); //방 제목
                    String room_type = item.getString(TAG_TYPE); // 방 종류
                    String room_public = item.getString(TAG_PUBLIC); // 방 공개,비공개방 알리기
                    int room_people_numlimit = item.getInt(TAG_PERSON_NUMLIMIT); //방 최대인원
                    int room_people_attend_num = item.getInt(TAG_PERSON_ATTEND_NUM); //방 현재인원

                    //방마다 필요한 데이터를 담고 있는 객체(방 고유번호,방 제목,방 종류,방 공개유무,방 최대인원,방 현재인원)
                    Lobby_Rooms_Data lobby_rooms_data = new Lobby_Rooms_Data(room_id,room_title,room_type,room_public,room_people_numlimit,room_people_attend_num);
                    //로비에서 뿌려줄 어레이리스트에 데이터 담기기
                    lobby_rooms_dataArrayList.add(lobby_rooms_data);

                }
            }catch (Exception e){
                Log.d(TAG, "showResult : ", e);
            }
            /*어답터에 설정된 리사이클러뷰 갱신시켜주기*/
            //AsyncTask 마지막 순서인 onPostExecute에 해준이유는
            //첫번째 우선 AsyncTask는 비동기로 동작하기때문에 동기로 동작하는 코드가 컴파일 되고나서 동작하기때문에
            //위에다가 갱신시켜주면 shoResult에서 arraylist에 데이터를 담아주는데 데이터를 담기전에 갱신이 되기때문에 방들이 안보이게된다.
            //두번째 showResult에서 arraylist에 데이터를 담아주기때문에 showResult밑에다 해준것이다.
            adapter_lobby_rooms.notifyDataSetChanged();
        }
        else {

        }
    }
}
