package com.example.opentalk.Activity_Tab_Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.opentalk.Activity.Activity_Friend_List;
import com.example.opentalk.Activity.Activity_Lobby;
import com.example.opentalk.Data.Logindata;
import com.example.opentalk.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import static com.example.opentalk.SharedPreference.PreferenceManager_member.getLoignDataPref;

public class Fragment_friend_add extends Fragment {
    String TAG = "Fragment_friend_add";


    /*로그인 데이터 정보 가져오기, 담아줄 변수*/
    Logindata logindata;
//    String userid;
//    String usernickname;

    /*socket 연결*/
    PrintWriter printWriter = null;            //서버로 내보내기 위한 출력 스트림
    BufferedReader bufferedReader = null;        //Server로부터 데이터를 읽어들이기 위한 입력스트림

    EditText frag_friend_email;
    Button frag_friend_add_btn;

    /*친구요청할 이메일(아이디)*/
    String request_friend_email;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_friend_add,null);
        initfindViewById(view);

        Log.d(TAG, "onCreateView: socket : "+ Activity_Lobby.socket_friend_list);

        frag_friend_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //친구 이메일 담아주기
                request_friend_email = frag_friend_email.getText().toString();
                //소켓 전송

                ClientThread clientThread = new ClientThread(request_friend_email);
                clientThread.start();

            }
        });

        return view;
    }
    private void initfindViewById(View view) {
        /*로그인정보 입력*/
        logindata = getLoignDataPref(getContext(), "login_inform");
//        userid = logindata.getUserid();
//        usernickname = logindata.getUsername();
        /*친구요청 보내는 버튼,친구이메일 입력란*/
        frag_friend_add_btn = (Button)view.findViewById(R.id.frag_friend_add_btn);
        frag_friend_email = (EditText) view.findViewById(R.id.frag_friend_email);

    }

    class ClientThread extends Thread {

        String other_email_id;

        public ClientThread(String other_email_id){
            this.other_email_id = other_email_id;
        }
        @Override
        public void run() {
            request_friend_add(other_email_id);
        }
    }

    private void request_friend_add(String request_friend_email){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type","friend_add_request");
            jsonObject.put("my_email_id",Activity_Lobby.userid);
            jsonObject.put("other_email_id",request_friend_email);
            PrintWriter printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(Activity_Lobby.socket_friend_list.getOutputStream())));
            printWriter.write(jsonObject.toString()+"\n");
            //네트워크에 통신을 할때 즉,네트워크 관련 작업을 진행할때에는 메인 thread 말고 서브 thread를 생성하고 진행해야한다.
            printWriter.flush();
//            printWriter.close();
        }catch (JSONException | IOException e){
            Log.d(TAG, "request_friend_add: error message : "+e.getMessage());
        }
    }

}
