package com.example.opentalk.Code;

public class HandlerType_Code {

    public static class HandlerType{
        //친구요청을 받으면 서브스레드에서 friend_wait의 메인스레드 리사이클러뷰 UI를 변경하게 해주는 handler message코드
        //그리고 이건 arraylist를 handler에서 add를 해준다. -> 실수 handler는 ui만 건드는 것이다. 그외에는 밖에서 해줘야한다.
        public static final int FRIEND_WAIT_RECYCLERVIEW = 0;
        //친구요청한 사람이 회원 탈퇴할 경우
        public static final int FRIEND_WAIT_REMOVE_RECYCLERVIEW =11;

        //FRIEND_WAIT_RECYCLERVIEW 수정 버전(arraylist.add는 밖에서해줌 최종적으로 notifyItemchanged만 해줌)
        public static final int FRIEND_WAIT_RECYCLERVIEW_CHANGE = 4;

        //친구요청 수락이되면 friend_List의 메인스레드 리사이클러뷰 UI를 변경하게 해주는 handler message코드
        public static final int FRIEND_LIST_RECYCLERVIEW = 1;
        public static final int FRIEND_LIST_RECYCLERVIEW_CHANGE_ONE = 15;

        //친구요청 할때 본인 아이디를 입력해서 요청할 경우 Toast를 띄어주는 handler message 코드
        public static final int FRIEND_ADD_REQUEST_MYSELF_FAILURE = 2;
        //친구요청 할때 가입되지 않은 아이디를 요청할 경우 Toast를 띄어주는 handler message 코드
        public static final int FRIEND_ADD_REQUEST_NOEXIST_FAILURE = 3;
        public static final int FRIEND_ADD_REQUEST_ALEADY_FRIEND = 5;
        public static final int FRIEND_ADD_REQUEST_AWAITING = 6;

        //화면 애니메이션 조정할때 버튼 색 조정하기 위함
        // 버튼 비활성화
        public static final int CHAT_BTN_CLOSE = 7;
        //버튼 활성화
        public static final int CHAT_BTN_OPEN = 8;

        //마이크,스피커 ON,OFF
        public static final int ADAPTER_VOICE_PARICIPANT_LIST_NOTIFYDATASETCHANGED = 9;
        //화상채팅 참여자 리사이클러뷰
        public static final int ADAPTER_FACE_PARICIPANT_LIST_NOTIFYDATASETCHANGED = 10;

        //친구 채팅 관련 마지막 부분만 지워주기
        public static final int ADAPTER_FRIEND_CHAT_LIST_NOTIFYDATASETCHANGED_ONE = 12;
        //안읽은부분만 셋팅해주기
        public static final int ADAPTER_FRIEND_CHAT_LIST_NOTIFYDATASETCHANGED_READ_SETTING = 13;
        //전부 셋팅
        public static final int ADAPTER_FRIEND_CHAT_LIST_NOTIFYDATASETCHANGED_READ_ALL = 14;

        //강제 로그아웃시 화면에 다른기기에서 접속해서 종료되었다고 알려주기 위한 코드
        public static final int COMPULSORY_LOGOUT = 16;

        //TEST용
        public static final int TEST = 999;


    }
}
