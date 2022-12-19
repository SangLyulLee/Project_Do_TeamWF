package com.example.myapplication.vision;

import static com.example.myapplication.api_ver.get_api.getStaionBusData;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.api_notice.Alarm_Reciver_api;
import com.example.myapplication.api_notice.NoticeApi;
import com.example.myapplication.api_ver.get_api;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class blind_wait extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
    NoticeApi noticeData;
    String[] busData_list;
    String[] busData_fast;
    String busData_arrt;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    TextToSpeech tts;
    String sNodeord = null, eNodeord, nowNodeOrd;
    String getKey;
    boolean noticeBool = true;
    RecognitionListener listener;
    SpeechRecognizer mRecognizer;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedIntancdState) {
        super.onCreate(savedIntancdState);
        setContentView(R.layout.blind_wait);

        Button textView = (Button) findViewById(R.id.busData_text);
        Intent my_intent = new Intent(blind_wait.this, Alarm_Reciver_api.class);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() { //tts구현
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) { //tts 잘되면
                    tts.setLanguage(Locale.KOREAN);     //한국어로 설정
                    //tts.setSpeechRate(0.8f); //말하기 속도 지정 1.0이 기본값
                }
            }
        });

        databaseReference = database.getReference().child("Notice_api");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("Uid").getValue(String.class).equals(firebaseUser.getUid())) {
                        noticeData = snapshot.getValue(NoticeApi.class);
                        getKey = snapshot.getKey();
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                String[] RouteData = get_api.getBusRoute(noticeData.getCityCode(), noticeData.getRouteId(), "1").split("\n");
                for (int i=0; i<RouteData.length; i++) {
                    String[] RouteData_List = RouteData[i].split(" ");
                    if (RouteData_List[2].equals(noticeData.getSbusStopNodeId())) {
                        sNodeord = RouteData_List[5];
                    }
                    if (RouteData_List[2].equals(noticeData.getEbusStopNodeId())) {
                        eNodeord = RouteData_List[5];
                        break;
                    }
                }
            }
        };
        timer.schedule(timerTask, 1000);

        StringBuilder matchStr = new StringBuilder();
        listener = new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Toast.makeText(getApplicationContext(), "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float rmsdB) {
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
            }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onError(int error) {
                String message;

                switch (error) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        message = "오디오 에러";
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        message = "클라이언트 에러";
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        message = "퍼미션 없음";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        message = "네트워크 에러";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        message = "네트웍 타임아웃";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        message = "찾을 수 없음";
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        message = "RECOGNIZER가 바쁨";
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        message = "서버가 이상함";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        message = "말하는 시간초과";
                        break;
                    default:
                        message = "알 수 없는 오류임";
                        break;
                }

                Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message, Toast.LENGTH_SHORT).show();
                System.out.println(getApplicationContext() + " " + message);
            }

            @Override
            public void onResults(Bundle results) {
                // 말을 하면 ArrayList에 단어를 넣고 textView에 단어 연결
                System.out.println("음성인식성공");
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                for (int i = 0; i < matches.size(); i++) {
                    matchStr.append(matches.get(i));
                }
                System.out.println("matchStr : " + matchStr.toString());

                if (matchStr.toString().equals("삭제")) {

                    database.getReference("Notice_api").child(getKey).removeValue();
                    tts.speak("알림이 삭제되었습니다.", TextToSpeech.QUEUE_ADD, null);

                    Intent intent1 = new Intent(blind_wait.this, blind_main.class);
                    startActivity(intent1);
                    finish();
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        };

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        timerTask = new TimerTask() {
            @Override
            public void run() {
                databaseReference = database.getReference().child("Notice_api");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.child("Uid").getValue(String.class).equals(firebaseUser.getUid())) {
                                noticeData = snapshot.getValue(NoticeApi.class);
                                getKey = snapshot.getKey();

                                if (noticeData.getbusRide().equals("0")) {
                                    String[] busData;
                                    busData = getStaionBusData(noticeData.getCityCode(), noticeData.getRouteId(), noticeData.getSbusStopNodeId()).split("\n");
                                    for (int i = 0; i < busData.length; i++) {
                                        busData_list = busData[i].split(" ");
                                        if (busData_list.length == 1) {
                                            textView.setText("API 서버 오류... 대기중");
                                        } else {
                                            // [0]:남은정류장수, [1]:남은시간, [2]:정류장명, [3]:버스번호
                                            if (i == 0) {
                                                busData_arrt = busData_list[1];
                                                busData_fast = busData_list;
                                            } else {
                                                if (Integer.parseInt(busData_arrt) > Integer.parseInt(busData_list[1])) {
                                                    busData_arrt = busData_list[1];
                                                    busData_fast = busData_list;
                                                }
                                            }
                                        }
                                    }
                                    String tts_str = "탑승 정류장 : " + busData_fast[2] + "\n버스 번호 : " + busData_fast[3] + "번\n남은 정류장 수 : " + busData_fast[0] + "개\n남은 시간 : " + busData_fast[1] + " 분\n알림을 삭제하시려면\n삭제라고 말해주세요";
                                    textView.setText(tts_str);
                                    textView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            tts.speak(tts_str, TextToSpeech.QUEUE_ADD, null);
                                            while (true) {
                                                if (!tts.isSpeaking())
                                                    break;
                                            }
                                            matchStr.delete(0, matchStr.length());
                                            mRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
                                            mRecognizer.setRecognitionListener(listener);
                                            mRecognizer.startListening(intent);
                                        }
                                    });
                                    if (busData_fast[0].equals("1")) {
                                        Handler handler = new Handler(Looper.getMainLooper());
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                tts.speak("잠시 후 버스가 도착할 예정입니다. 탑승을 준비해주세요.", TextToSpeech.QUEUE_ADD, null);
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                    pendingIntent = (PendingIntent.getBroadcast(blind_wait.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
                                                } else {
                                                    pendingIntent = (PendingIntent.getBroadcast(blind_wait.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT));
                                                }
                                                if (Build.VERSION.SDK_INT >= 23) {
                                                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
                                                } else {
                                                    alarmManager.set(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
                                                }
                                            }
                                        }, 0);

                                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                        vibrator.vibrate(new long[]{500, 500, 500, 500, 500, 500, 500, 500, 500, 500}, -1);
                                        database.getReference("Notice_api").child(getKey).child("busRide").setValue("1");

                                        String[] busServiceData = get_api.getBusServiceData(noticeData.getCityCode(), noticeData.getRouteId(), "1").split("\n");
                                        for (int i = 0; i < busServiceData.length; i++) {
                                            String[] busSD_List = busServiceData[i].split(" ");
                                            if (busSD_List[1].equals(Integer.toString(Integer.parseInt(sNodeord) - 1))) {
                                                database.getReference("Notice_api").child(getKey).child("Vehicleno").setValue(busSD_List[2]);
                                                break;
                                            }
                                        }
                                    }

                                } else if (noticeData.getbusRide().equals("1")) {
                                    String[] RouteposData = get_api.getBusServiceData(noticeData.getCityCode(), noticeData.getRouteId(), "1").split("\n");
                                    for (int i = 0; i < RouteposData.length; i++) {
                                        String[] Routepos_List = RouteposData[i].split(" ");
                                        if (Routepos_List[2].equals(noticeData.getVehicleno())) {
                                            nowNodeOrd = Routepos_List[1];
                                            if (Integer.parseInt(sNodeord) > Integer.parseInt(nowNodeOrd)+1) {
                                                tts.speak("지난 알림입니다. 알림이 자동으로 삭제됩니다.", TextToSpeech.QUEUE_ADD, null);
                                                database.getReference("Notice_api").child(getKey).removeValue();
                                                while (true) {
                                                    if (!tts.isSpeaking()) {
                                                        break;
                                                    }
                                                }
                                                timer.cancel();
                                                noticeBool = false;
                                                Intent finish_intent = new Intent(blind_wait.this, blind_main.class);
                                                startActivity(finish_intent);
                                                finish();
                                            }
                                            break;
                                        }
                                        if (i == RouteposData.length - 1 && !noticeData.getVehicleno().equals(Routepos_List[2])) {
                                            tts.speak("운행이 종료된 버스입니다. 알림이 자동으로 삭제됩니다.", TextToSpeech.QUEUE_ADD, null);
                                            database.getReference("Notice_api").child(getKey).removeValue();
                                            while (true) {
                                                if (!tts.isSpeaking()) {
                                                    break;
                                                }
                                            }
                                            noticeBool = false;
                                            timer.cancel();
                                            Intent finish_intent = new Intent(blind_wait.this, blind_main.class);
                                            startActivity(finish_intent);
                                            finish();
                                            break;
                                        }
                                    }


                                    if (noticeBool) {
                                        String[] busData = getStaionBusData(noticeData.getCityCode(), noticeData.getRouteId(), noticeData.getEbusStopNodeId()).split("\n");
                                        for (int i = 0; i < busData.length; i++) {
                                            busData_list = busData[i].split(" ");
                                            if (busData_list.length == 1) {
                                                textView.setText("API 서버 오류... 대기중");
                                            } else {
                                                // [0]:남은정류장수, [1]:남은시간, [2]:정류장명, [3]:버스번호
                                                if (Integer.parseInt(busData_list[0]) + Integer.parseInt(nowNodeOrd) == Integer.parseInt(eNodeord)) {
                                                    String tts_str = "하차 정류장 : " + busData_list[2] + "\n버스 번호 : " + busData_list[3];
                                                    textView.setText(tts_str);
                                                    textView.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            tts.speak(tts_str, TextToSpeech.QUEUE_ADD, null);
                                                        }
                                                    });
                                                    textView.setText("하차 정류장 : " + busData_list[2] + "\n버스 번호 : " + busData_list[3]);
                                                    if (busData_list[0].equals("1")) {

                                                        if (nowNodeOrd.equals(Integer.toString(Integer.parseInt(eNodeord) - 1))) {
                                                            Handler handler = new Handler(Looper.getMainLooper());
                                                            handler.postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    tts.speak("잠시 후 목적지에 도착할 예정입니다. 하차를 준비해주세요.", TextToSpeech.QUEUE_ADD, null);
                                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                                        pendingIntent = (PendingIntent.getBroadcast(blind_wait.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
                                                                    } else {
                                                                        pendingIntent = (PendingIntent.getBroadcast(blind_wait.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT));
                                                                    }
                                                                    if (Build.VERSION.SDK_INT >= 23) {
                                                                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
                                                                    } else {
                                                                        alarmManager.set(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
                                                                    }
                                                                }
                                                            }, 0);

                                                            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                                            vibrator.vibrate(new long[]{500, 500, 500, 500, 500, 500, 500, 500, 500, 500}, -1);

                                                            while (true) {
                                                                if (!tts.isSpeaking()) {
                                                                    break;
                                                                }
                                                            }
                                                            database.getReference("Notice_api").child(getKey).removeValue();
                                                            Intent finish_intent = new Intent(blind_wait.this, blind_main.class);
                                                            startActivity(finish_intent);
                                                            finish();
                                                            timer.cancel();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
        };

        timer.schedule(timerTask, 10*1000, 10*1000);
    }
}
