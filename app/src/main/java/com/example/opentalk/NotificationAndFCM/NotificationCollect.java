package com.example.opentalk.NotificationAndFCM;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.example.opentalk.Activity.Activity_Friend_Chat;
import com.example.opentalk.Activity.Activity_Friend_List;
import com.example.opentalk.Activity.Activity_Lobby;
import com.example.opentalk.BitmapConverter;
import com.example.opentalk.R;

public class NotificationCollect {

    public static final String NOTIFICATION_CHANNEL_ID = "10001";
//    /**
//     * 가장 기본적인 노티피케이션
//     */
//    public void showBasicNotification() {
//        NotificationCompat.Builder mBuilder = createNotification();
//        mBuilder.setContentIntent(createPendingIntent());
//
//        channerCreate(mBuilder);
//    }

//    /**
//     * 알림창을 보는 상태에서 자동적으로 확장하는 노티피케이션
//     */
//    public void showExpandedlayoutNotification() {
//
//        NotificationCompat.Builder mBuilder = createNotification();
//
//        NotificationCompat.InboxStyle inboxStyle =
//                new NotificationCompat.InboxStyle();
//        String[] events = new String[6];
//        events[0] = "Monday";
//        events[1] = "Tuesday";
//        events[2] = "Wedsnday";
//        events[3] = "Thursday";
//        events[4] = "Friday";
//        events[5] = "Saturday";
//        inboxStyle.setBigContentTitle("Event tracker details:");
//        inboxStyle.setSummaryText("Events summary");
//        NotificationCompat.Action action = new NotificationCompat.Action(R.mipmap.ic_launcher, "button1", createPendingIntent());
//        for (String str : events) {
//            inboxStyle.addLine(str);
//        }
//        //스타일 추가
//        mBuilder.setStyle(inboxStyle);
//        mBuilder.setContentIntent(createPendingIntent());
//
//        channerCreate(mBuilder);
//
//
//    }

    /**
     * XML로 직접만든 화면으로 보여주는 노티피케이션
     *
     * 채팅 혹은
     *
     * boolean foreandback =true -> foreground notification
     * boolean foreandback =false -> background notification
     *
     *
     */
    public static void showCustomLayoutNotification(Context context, String time, String roomnum,String friendId, String friendnickname, String userContent, Bitmap userimgBitmap,boolean foreandback)  {
        NotificationCompat.Builder mBuilder;

        if(foreandback==false){
            mBuilder = backcreateNotification(context,roomnum,friendId);
        }
        else {
            mBuilder = createNotification(context,roomnum,friendId);
        }


        //커스텀 화면 만들기
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.custom_notification_chatting);
//        remoteViews.setImageViewResource(R.id.img, R.mipmap.ic_launcher);
//        remoteViews.setTextViewText(R.id.title, "Title");
//        remoteViews.setTextViewText(R.id.message, "message");
        remoteViews.setTextViewText(R.id.notifi_time,time);
        remoteViews.setTextViewText(R.id.notifi_user_name,friendnickname);
        remoteViews.setTextViewText(R.id.notifi_user_content,userContent);
        if(userimgBitmap!=null) {
            remoteViews.setImageViewBitmap(R.id.notifi_user_img, BitmapConverter.getCircleBitmap(userimgBitmap));
        }
        else if(userimgBitmap==null){
            remoteViews.setImageViewResource(R.id.notifi_user_img,R.drawable.profile4);
        }

        //노티피케이션에 커스텀 뷰 장착
        mBuilder.setContent(remoteViews);
        channerCreate(context,mBuilder);

    }

//    /**
//     * 노티피케이션을 누르면 실행되는 기능을 가져오는 노티피케이션
//     * <p>
//     * 실제 기능을 추가하는 것
//     *
//     * 채팅 화면 가는곳
//     * @return
//     */
//    private static PendingIntent createPendingIntent(Context context) {
//        Intent resultIntent = new Intent(context, MainActivity.class);
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        stackBuilder.addParentStack(MainActivity.class);
//        stackBuilder.addNextIntent(resultIntent);
//
//        return stackBuilder.getPendingIntent(
//                0,
//                PendingIntent.FLAG_UPDATE_CURRENT
//        );
//    }

    /**
     * 포그라운드 상태에서
     * 노티피케이션 빌드
     *
     * @return
     */
    private static NotificationCompat.Builder createNotification(Context context,String roomnum,String friendid) {
        Intent notificationIntent = new Intent(context.getApplicationContext(), Activity_Friend_Chat.class);
        notificationIntent.putExtra("roomnum", roomnum); //전달할 값
        notificationIntent.putExtra("friendid", friendid); //전달할 값
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        Activity_Friend_List activityFriendListcontext = ((Activity_Friend_List) Activity_Friend_List.activity_Friend_Listcontext);
        //친구리스트 방에 있다면 바로 채팅화면으로 넘어가기
        if (activityFriendListcontext != null) {
            Activity_Friend_Chat friend_chat_context = ((Activity_Friend_Chat) Activity_Friend_Chat.Activity_Friend_Chat_context);
            if(friend_chat_context!=null) {
                if (!friend_chat_context.roomnum.equals(roomnum)) {
                    notificationIntent.putExtra("beforeroom", "yes");
                }
            }
            notificationIntent.setComponent(new ComponentName(context,Activity_Friend_Chat.class));
        }
        //만약 로비화면이나 다른 곳에 있다면
        else{
            notificationIntent.putExtra("notifi","notifiEnter");
            notificationIntent.setComponent(new ComponentName(context,Activity_Friend_List.class));
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
//                .setStyle(inboxStyle)
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.heart)) //BitMap 이미지 요구
                .setContentTitle("상태바 드래그시 보이는 타이틀")
                .setContentText("asd")
                .setContentText(context.getResources().getString(R.string.app_name))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // 사용자가 노티피케이션을 탭시 ResultActivity로 이동하도록 설정
                .setAutoCancel(true);

        return builder;
    }

    /**
     * 백그라운드 상태에서
     * 노티피케이션 빌드
     *
     * @return
     */
    private static NotificationCompat.Builder backcreateNotification(Context context,String roomnum,String friendid) {
        Intent notificationIntent = new Intent(context.getApplicationContext(), Activity_Friend_Chat.class);
//        notificationIntent.putExtra("notificationId", count); //전달할 값
        notificationIntent.putExtra("notifi","notifiEnter");
        notificationIntent.putExtra("roomnum", roomnum); //전달할 값
        notificationIntent.putExtra("friendid", friendid); //전달할 값
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.setComponent(new ComponentName(context, Activity_Lobby.class));
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
//                .setStyle(inboxStyle)
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.heart)) //BitMap 이미지 요구
                .setContentTitle("상태바 드래그시 보이는 타이틀")
                .setContentText("asd")
                .setContentText(context.getResources().getString(R.string.app_name))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // 사용자가 노티피케이션을 탭시 ResultActivity로 이동하도록 설정
                .setAutoCancel(true);

        return builder;
    }

    private static NotificationManager channerCreate(Context context,NotificationCompat.Builder builder) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.heart); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            CharSequence channelName = "노티페케이션 채널";
            String description = "오레오 이상을 위한 것임";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance);
            channel.setDescription(description);
            // 노티피케이션 채널을 시스템에 등록
//            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        } else {
            builder.setSmallIcon(R.mipmap.heart); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        }
        notificationManager.notify(1234, builder.build()); // 고유숫자로 노티피케이션 동작시킴
        return notificationManager;
    }
}
