package com.example.myapplication.api_ver;

import static com.example.myapplication.vision.get_api.getStaionBusData;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.api_notice.NoticeRe_api;
import com.example.myapplication.notice.Notice;
import com.example.myapplication.notice.NoticeApi;
import com.example.myapplication.vision.blind_main;
import com.example.myapplication.vision.blind_wait;
import com.example.myapplication.vision.get_api;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class Menu3_api extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
    private DatabaseReference databaseReference;
    NoticeApi noticeData;
    int notice_pos = 0;
    boolean busRide = false, first_bool = true;
    String sNodeord = null, eNodeord, getOnVehicleNo, nowNodeOrd;
    String[] busData_list;
    String[] busData_fast;
    String busData_arrt, fast_arrp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu3);

        TextView sTextView = (TextView) findViewById(R.id.textView3);
        TextView eTextView = (TextView) findViewById(R.id.textView4);
        Button noticeR_btn = (Button) findViewById(R.id.menu3button1);
        Button noticeC_btn = (Button) findViewById(R.id.menu3button2);

        /* 알림 변경 버튼 */
        noticeR_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg_noticeR = new AlertDialog.Builder(Menu3_api.this);
                dlg_noticeR.setTitle("목적지 변경 확인");

                dlg_noticeR.setMessage("목적지를 변경하시겠습니까?");
                dlg_noticeR.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                dlg_noticeR.setNegativeButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Menu3_api.this, NoticeRe_api.class);
                        startActivity(intent);
                    }
                });
                dlg_noticeR.show();
            }
        });

        TextView textView = (TextView) findViewById(R.id.textView5);
        databaseReference = database.getReference().child("Notice_api");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("Uid").getValue(String.class).equals(firebaseUser.getUid())) {
                        noticeData = snapshot.getValue(NoticeApi.class);
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        textView.setText("버스 번호 : " + noticeData.getBusNum() + "\n탑승 정류장 명 : ");
    }
}
