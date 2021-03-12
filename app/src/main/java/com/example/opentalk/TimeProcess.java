package com.example.opentalk;

/*
* 2021-01-28 생성한 클래스
* 서버에서 받은 년-월-일 시간:분:초 데이터를 수정해주는 클래스
* */


import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeProcess {

    //오후,오전 시간:분으로 변경해주는 메소드
    public static String hourminProcess(String datetime){
        String[] senddateSplit = datetime.split(" ");
        //Log.d(TAG, "run: senddate senddateSplit : "+senddateSplit[0]); // 년-월-일
        //Log.d(TAG, "run: senddate senddateSplit : "+senddateSplit[1]); // 시간:분:초
        String[] timeSplit = senddateSplit[1].split(":");
        //Log.d(TAG, "run: senddate timeSplit : "+timeSplit[0]);
        //Log.d(TAG, "run: senddate timeSplit : "+timeSplit[1]);
        //Log.d(TAG, "run: senddate timeSplit : "+timeSplit[2]);
        String hour = timeSplit[0];
        String min = timeSplit[1];
        if(Integer.valueOf(hour)>12){
            hour = "오후 "+(Integer.valueOf(hour)-12);
        }
        else if(Integer.valueOf(hour)==12){
            hour = "오후 "+hour;
        }
        else if(Integer.valueOf(hour)<12 || Integer.valueOf(hour)==24){
            if(Integer.valueOf(hour)==24){
                hour = "오전 : 0";
            }
            else{
                hour = "오전 "+hour;
            }
        }
        String processtime = hour+":"+min;

        return processtime;
    }
    //년월일 요일로 변경해주는 클래스
    public static String yearmonthday(String datetime){
        String[] senddateSplit = datetime.split(" ");
        String[] timeSplit = senddateSplit[0].split("-");
        String year = timeSplit[0];
        String month = timeSplit[1];
        String day = timeSplit[2];

        String dayofweek = null;
        String dateFormatString = year+month+day;
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = dateFormat.parse(dateFormatString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        switch(calendar.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                dayofweek = "일요일";
                break;
            case 2:
                dayofweek = "월요일";
                break;
            case 3:
                dayofweek = "화요일";
                break;
            case 4:
                dayofweek = "수요일";
                break;
            case 5:
                dayofweek = "목요일";
                break;
            case 6:
                dayofweek = "금요일";
                break;
            case 7:
                dayofweek = "토요일";
                break;
        }
        String processtime = year+"년 "+month+"월 "+day+"일 "+dayofweek;
        return processtime;
    }
}
