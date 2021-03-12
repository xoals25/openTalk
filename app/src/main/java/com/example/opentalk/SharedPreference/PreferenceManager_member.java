package com.example.opentalk.SharedPreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.opentalk.Data.Logindata;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;

public class PreferenceManager_member {

    public static final String PREFERENCES_NAME = "로그인정보";

    private static final String TAG = "회원";

    private static final String DEFAULT_VALUE_STRING = "";
    private static final boolean DEFAULT_VALUE_BOOLEAN = false;
    private static final int DEFAULT_VALUE_INT = -1;
    private static final long DEFAULT_VALUE_LONG = -1L;
    private static final float DEFAULT_VALUE_FLOAT = -1F;



    private static SharedPreferences getPreferences(Context context) {

        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

    }
    //로그인한 아이디 정보를 담고 싶을 경우에(어레이리스트 버전)
    public static void setLoignArrayPref(Context context,String key, ArrayList<Logindata> logindataArrayList){
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor =sharedPreferences.edit();
//        gson = new GsonBuilder().create();
        Gson gson = new Gson();
        String logindata = gson.toJson(logindataArrayList);
        editor.putString(key,logindata);
        editor.commit();
    }

    //로그인한 아이디 정보를 갖고오고 싶을 경우에(어레이리스트 버전)
    public static ArrayList<Logindata> getLoignArrayPref(Context context, String key,ArrayList<Logindata> logindataArrayList) {
        SharedPreferences sharedPreferences = getPreferences(context);
        logindataArrayList = new ArrayList<>();
        Gson gson = new Gson();
        String json = sharedPreferences.getString(key,null);
        Type type = new TypeToken<ArrayList<Logindata>>() {}.getType();
        logindataArrayList = gson.fromJson(json, type);
        return logindataArrayList;
    }
    //로그인한 아이디 정보를 담고 싶을 경우에(객체버전)
    public static void setLoignDataPref(Context context,String key, Logindata logindata){
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor =sharedPreferences.edit();
//        gson = new GsonBuilder().create();
        Gson gson = new Gson();
        //logindata를 json으로 변경후 string type 변수에 담기
        String login_data = gson.toJson(logindata);
        //sahred에 키값 설정
        editor.putString(key,login_data);
        editor.commit();
    }
    //로그인한 아이디 정보를 갖고오고 싶을 경우에(객체버전)
    /*
    *Logindata
    * String userid;
    * String userpwd;
    * String username;
    * String imgString_tobitmap;
    *
    * */
    public static Logindata getLoignDataPref(Context context, String key) {
        SharedPreferences sharedPreferences = getPreferences(context);
        String login_data = sharedPreferences.getString(key,"");
        Gson gson = new Gson();
        Logindata logindata;
        //fromJson의 첫번째 인자는 데이터를 갖고있는 json(내가 저장한 정보)이고 두번째 인자는 첫번째인자를 어떤 타입으로 바꿔줄지를 나타냄
        logindata = gson.fromJson(login_data,Logindata.class);
        return logindata;
    }


    //String 값 저장 하기
    public static void setSocketString(Context context, String key, String value) {

        SharedPreferences sharedPreferences = getPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(key, value);


        editor.commit();

    }
    //String 값 불러오기
    public static String getSocketString(Context context, String key) {

        SharedPreferences sharedPreferences = getPreferences(context);

        String value = sharedPreferences.getString(key, DEFAULT_VALUE_STRING);

        return value;

    }


    public static void setStringArrayPref(Context context, String key, ArrayList<String> values) {

        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray array = new JSONArray();

        for (int i =0  ; i < values.size() ; i++) {
            array.put(values.get(i));
        }

        if(!values.isEmpty()){
            editor.putString(key, array.toString());
        }
        else {
            editor.putString(key,null);
        }


        editor.commit();
        //commit apply 동기 비동기 확인할 것

    }

    public static ArrayList<String> getStringArrayPref(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<String> urls = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    public static boolean emailCheckPref(Context context, String email){
        //key는 키값, email은 현재 사용자가 에디트에 입력한 email을 뜻함
        SharedPreferences prefs = getPreferences(context);
        //필요없을 듯 함.

        if(prefs.contains(email)){
            return true;
        }
        else{
            return false;
        }


    }



    public static String getNickName(Context context, String email){

        SharedPreferences prefs = getPreferences(context);

        String string = getStringArrayPref(context, email).get(4);

        Log.d(TAG, "nickname : " + string);

        return string;

    }




    public static boolean passWordCheckPref(Context context, String email, String password){

        //key는 키값, email은 현재 사용자가 입력한 email을 뜻함
        //password도 현재 사용자가 입력한 password

        //SharedPreferences prefs = getPreferences(context);
        //필요없을 듯 함.

        if(emailCheckPref(context, email)){

            //입력한 이메일이 존재한다면,
            //해당 이메일 키값으로 리스트를 꺼내고,
            //그 리스트에서 패스워드 부분을 꺼내서,
            //맞는지 확인해야 함.
            //틀리면, false
            //맞으면, true.

            String string = getStringArrayPref(context, email).get(1);
            //리스트에서 두 번째 데이터, 즉 비밀번호에 해당함

            Log.d(TAG, "password : " + string);


            if(string.equals(password)){
                return true;
            }else{
                return false;
            }


        }else{
            return false;
        }


    }


    /**

     * String 값 저장

     * @param context

     * @param key

     * @param value

     */

    public static void setString(Context context, String key, String value) {

        SharedPreferences prefs = getPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(key, value);

        editor.commit();

    }

    /**

     * boolean 값 저장

     * @param context

     * @param key

     * @param value

     */
    public static void setBoolean(Context context, String key, boolean value) {

        SharedPreferences prefs = getPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(key, value);

        editor.commit();

    }

    /**

     * int 값 저장

     * @param context

     * @param key

     * @param value

     */

    public static void setInt(Context context, String key, int value) {

        SharedPreferences prefs = getPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(key, value);

        editor.commit();

    }

    /**

     * long 값 저장

     * @param context

     * @param key

     * @param value

     */

    public static void setLong(Context context, String key, long value) {

        SharedPreferences prefs = getPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong(key, value);

        editor.commit();

    }



    /**

     * float 값 저장

     * @param context

     * @param key

     * @param value

     */

    public static void setFloat(Context context, String key, float value) {

        SharedPreferences prefs = getPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putFloat(key, value);

        editor.commit();

    }



    /**

     * String 값 로드

     * @param context

     * @param key

     * @return

     */

    public static String getString(Context context, String key) {

        SharedPreferences prefs = getPreferences(context);

        String value = prefs.getString(key, DEFAULT_VALUE_STRING);

        return value;

    }



    /**

     * boolean 값 로드

     * @param context

     * @param key

     * @return

     */

    public static boolean getBoolean(Context context, String key) {

        SharedPreferences prefs = getPreferences(context);

        boolean value = prefs.getBoolean(key, DEFAULT_VALUE_BOOLEAN);

        return value;

    }



    /**

     * int 값 로드

     * @param context

     * @param key

     * @return

     */

    public static int getInt(Context context, String key) {

        SharedPreferences prefs = getPreferences(context);

        int value = prefs.getInt(key, DEFAULT_VALUE_INT);

        return value;

    }



    /**

     * long 값 로드

     * @param context

     * @param key

     * @return

     */

    public static long getLong(Context context, String key) {

        SharedPreferences prefs = getPreferences(context);

        long value = prefs.getLong(key, DEFAULT_VALUE_LONG);

        return value;

    }



    /**

     * float 값 로드

     * @param context

     * @param key

     * @return

     */

    public static float getFloat(Context context, String key) {

        SharedPreferences prefs = getPreferences(context);

        float value = prefs.getFloat(key, DEFAULT_VALUE_FLOAT);

        return value;

    }



    /**

     * 키 값 삭제

     * @param context

     * @param key

     */

    public static void removeKey(Context context, String key) {

        SharedPreferences prefs = getPreferences(context);

        SharedPreferences.Editor edit = prefs.edit();

        edit.remove(key);

        edit.commit();

    }



    /**

     * 모든 저장 데이터 삭제

     * @param context

     */

    public static void clear(Context context) {

        SharedPreferences prefs = getPreferences(context);

        SharedPreferences.Editor edit = prefs.edit();

        edit.clear();
        edit.commit();
    }


}