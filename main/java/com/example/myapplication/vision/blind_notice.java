package com.example.myapplication.vision;

import static com.example.myapplication.R.*;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.driver.DriverMain;
import com.example.myapplication.driver.DriverSelect;
import com.example.myapplication.map.BusTime;
import com.example.myapplication.notice.Alarm_Reciver;
import com.example.myapplication.notice.EbusSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class blind_notice extends AppCompatActivity {
    TextToSpeech tts;
    Intent intent;
    SpeechRecognizer mRecognizer;
    RecognitionListener listener;
    final int PERMISSION = 1;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    int notice_pos;

    @Override
    protected void onCreate(Bundle savedIntancdState) {
        super.onCreate(savedIntancdState);
        setContentView(layout.blind_notice);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //final Calendar calendar = Calendar.getInstance();
        final Intent my_intent = new Intent(blind_notice.this, Alarm_Reciver.class);

        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO}, PERMISSION);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET,
                                Manifest.permission.RECORD_AUDIO}, PERMISSION);
            }

            // RecognizerIntent 객체 생성
            intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        }
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() { //tts구현
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) { //tts 잘되면
                    tts.setLanguage(Locale.KOREAN);     //한국어로 설정
                    //tts.setSpeechRate(0.8f); //말하기 속도 지정 1.0이 기본값
                }
            }
        });

        String startRoute_nodenm = getIntent().getStringExtra("startRoute_nodenm");
        String endRoute_nodenm = getIntent().getStringExtra("endRoute_nodenm");
        String fastRouteInfo1 = getIntent().getStringExtra("fastRouteInfo[1]");
        String fastRouteInfo0 = getIntent().getStringExtra("fastRouteInfo[0]");
        String fastRoute_routeId = getIntent().getStringExtra("fastRoute_routeId");
        String startRoute_nodeId = getIntent().getStringExtra("startnodeID");
        String endRoute_nodeId = getIntent().getStringExtra("endnodeID");
        String startRoute_cityCode = getIntent().getStringExtra("fastRoute_cityCode");

        String speakString = startRoute_nodenm + "에서 승차하고, " + endRoute_nodenm + "에서 하차합니다. " + "버스 번호는 " + fastRouteInfo1 + "번, " + fastRouteInfo0 + "분 후에 도착합니다. 알림 신청을 하시려면 네 라고 말해주세요.";
        System.out.println("speakString : " + speakString);

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{500, 500, 500, 500, 500}, -1);

        StringBuilder matchStr = new StringBuilder();
        Button btn = (Button) findViewById(R.id.notice_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts.speak(speakString, TextToSpeech.QUEUE_ADD, null);
                while (true) {
                    if (!tts.isSpeaking())
                        break;
                }
                System.out.println("종료확인, 음성인식시작");
                matchStr.delete(0, matchStr.length());
                mRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
                mRecognizer.setRecognitionListener(listener);
                mRecognizer.startListening(intent);
            }
        });

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
                System.out.println("음성인식성공");
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                for (int i = 0; i < matches.size(); i++) {
                    matchStr.append(matches.get(i));
                }
                System.out.println("matchStr : " + matchStr.toString());

                if (matchStr.toString().equals("네")) {
                    tts.speak("해당 루트로 알림을 신청합니다.", TextToSpeech.QUEUE_ADD, null);

                    // 알림 알람 설정
                    //calendar.setTimeInMillis(System.currentTimeMillis());
                    /*
                    String[] alarmRouteInfo;
                    alarmRouteInfo = get_api.getStaionBus(startRoute_cityCode, fastRoute_routeId, startRoute_nodeId).split(" ");
                    //calendar.set(Calendar.HOUR_OF_DAY, (Integer.parseInt(alarmRouteInfo[0])-2)/60);
                    //calendar.set(Calendar.MINUTE, (Integer.parseInt(alarmRouteInfo[0])-2)%60);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        pendingIntent = PendingIntent.getBroadcast(blind_notice.this, 0, my_intent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    }
                    else {
                        pendingIntent = PendingIntent.getBroadcast(blind_notice.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    }

                    if (Build.VERSION.SDK_INT >= 23) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + ((Integer.parseInt(alarmRouteInfo[0])-2)* 60000L), pendingIntent);
                    }
                    else {
                        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + ((Integer.parseInt(alarmRouteInfo[0])-2)* 60000L), pendingIntent);
                    }
                    ////*/

                    databaseReference = database.getReference().child("Notice_api");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int j = 1;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (j != Integer.parseInt(snapshot.getKey()))
                                    break;
                                j++;
                            }
                            notice_pos = j;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });

                    databaseReference = database.getReference("member").child("UserAccount");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (firebaseUser.getUid().equals(snapshot.getKey())) {
                                    database.getReference("Notice_api").child(Integer.toString(notice_pos)).child("Uid").setValue(firebaseUser.getUid());
                                    database.getReference("Notice_api").child(Integer.toString(notice_pos)).child("BusNum").setValue(fastRouteInfo1);
                                    database.getReference("Notice_api").child(Integer.toString(notice_pos)).child("RouteId").setValue(fastRoute_routeId);
                                    database.getReference("Notice_api").child(Integer.toString(notice_pos)).child("SbusStopNodeId").setValue(startRoute_nodeId);
                                    database.getReference("Notice_api").child(Integer.toString(notice_pos)).child("EbusStopNodeId").setValue(endRoute_nodeId);
                                    database.getReference("Notice_api").child(Integer.toString(notice_pos)).child("CityCode").setValue(startRoute_cityCode);
                                    database.getReference("Notice_api").child(Integer.toString(notice_pos)).child("u_type").setValue(snapshot.child("u_type").getValue(int.class));
                                    while (true) {
                                        if (!tts.isSpeaking())
                                            break;
                                    }
                                    tts.speak("알림 신청이 완료되었습니다.", TextToSpeech.QUEUE_ADD, null);
                                    Toast.makeText(blind_notice.this, "알림 설정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    while (true) {
                                        if (!tts.isSpeaking())
                                            break;
                                    }
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(blind_notice.this, blind_wait.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }, 1000);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        };
    }
}

