package com.example.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.map.BusStop;
import com.example.myapplication.notice.Alarm_Reciver;
import com.example.myapplication.notice.Notice;
import com.example.myapplication.notice.NoticeRe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Menu3 extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
    private DatabaseReference mDatabaseRef;
    private int sTime;
    private int sTimer, eTimer;
    private String sStopName, eStopName;
    private Notice notice = new Notice();
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu3);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        final Intent my_intent = new Intent(Menu3.this, Alarm_Reciver.class);

        TextView sTextView = (TextView) findViewById(R.id.textView3);
        TextView eTextView = (TextView) findViewById(R.id.textView4);
        Button noticeR_btn = (Button) findViewById(R.id.menu3button1);
        Button noticeC_btn = (Button) findViewById(R.id.menu3button2);

        /* ?????? ?????? ?????? */
        noticeR_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg_noticeR = new AlertDialog.Builder(Menu3.this);
                dlg_noticeR.setTitle("????????? ?????? ??????");

                dlg_noticeR.setMessage("???????????? ?????????????????????????");
                dlg_noticeR.setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                dlg_noticeR.setNegativeButton("???", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Menu3.this, NoticeRe.class);
                        startActivity(intent);
                    }
                });
                dlg_noticeR.show();
            }
        });

        /* ?????? ?????? ?????? */
        noticeC_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg_noticeC = new AlertDialog.Builder(Menu3.this);
                dlg_noticeC.setTitle("?????? ?????? ??????");

                dlg_noticeC.setMessage("?????? ????????? ?????????????????????????");
                dlg_noticeC.setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                dlg_noticeC.setNegativeButton("???", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String noKey = getIntent().getStringExtra("noticeKey");
                        mDatabaseRef = database.getReference("Notice");
                        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                mDatabaseRef = database.getReference("BusRoute").child("1").child("route").child(dataSnapshot.child(noKey).child("BusNum").getValue(String.class));
                                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                        int i = 0;
                                        for (DataSnapshot snapshot1 : dataSnapshot1.getChildren()) {
                                            if (dataSnapshot.child(noKey).child("EbusStopNum").getValue(String.class).equals(snapshot1.getValue(String.class)))
                                                break;
                                            i++;
                                        }
                                        int pos = i;

                                        i = 0;
                                        for (DataSnapshot snapshot2 : dataSnapshot1.getChildren()) {
                                            if (dataSnapshot.child(noKey).child("SbusStopNum").getValue(String.class).equals(snapshot2.getValue(String.class)))
                                                break;
                                            i++;
                                        }
                                        int pos_s = i;

                                        for (int seat_pos = pos_s + 1; seat_pos <= pos + 1; seat_pos++) {
                                            database.getReference("BusSeat")
                                                    .child(dataSnapshot.child(noKey).child("BusNum").getValue(String.class))
                                                    .child(dataSnapshot.child(noKey).child("BusTime").getValue(String.class))
                                                    .child("route"+Integer.toString(seat_pos)).setValue(0);
                                        }

                                        database.getReference("Notice").child(noKey).removeValue();
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) { }
                                });
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });

                        if (alarmManager != null) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                pendingIntent = PendingIntent.getBroadcast(Menu3.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                            } else {
                                pendingIntent = PendingIntent.getBroadcast(Menu3.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            }
                            alarmManager.cancel(pendingIntent);
                        }

                        Toast.makeText(Menu3.this, "????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Menu3.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                dlg_noticeC.show();
            }
        });

        mDatabaseRef = database.getReference("Notice");
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if ((snapshot.child("Uid").getValue(String.class)).equals(firebaseUser.getUid())) {
                        notice = snapshot.getValue(Notice.class);
                    }
                }
                mDatabaseRef = database.getReference("BusTime").child(notice.getBusNum()).child(notice.getBusTime()).child("hours");
                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        sTime = (snapshot.getValue(int.class)*60);
                        mDatabaseRef = database.getReference("BusTime").child(notice.getBusNum()).child(notice.getBusTime()).child("minutes");
                        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                sTime = sTime + snapshot.getValue(int.class);

                                mDatabaseRef = database.getReference("BusRoute").child(notice.getBusNum());
                                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int i=0, sIndex=0, eIndex=0;
                                        for (DataSnapshot snapshot : dataSnapshot.child("route").child("1").getChildren()) {
                                            if (snapshot.getValue(String.class).equals(notice.getSbusStopNum()))
                                                sIndex = i;
                                            else if (snapshot.getValue(String.class).equals(notice.getEbusStopNum()))
                                                eIndex = i;
                                            i++;
                                        }
                                        ArrayList<String> timer = new ArrayList<>();
                                        for (DataSnapshot snapshot : dataSnapshot.child("timer").child("1").getChildren()) {
                                            timer.add(snapshot.getValue(String.class));
                                        }
                                        sTimer = (Integer.parseInt(timer.get(sIndex))/60) + sTime;
                                        eTimer = (Integer.parseInt(timer.get(eIndex))/60) + sTime;

                                        mDatabaseRef = database.getReference("BusStop");
                                        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                ArrayList<BusStop> busStopArray = new ArrayList<>();
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    busStopArray.add(snapshot.getValue(BusStop.class));
                                                }
                                                for (int i=0; i<busStopArray.size(); i++) {
                                                    if (busStopArray.get(i).getBusStopNum().equals(notice.getSbusStopNum())) {
                                                        sStopName = busStopArray.get(i).getBusStopName();
                                                    }
                                                    if (busStopArray.get(i).getBusStopNum().equals(notice.getEbusStopNum())) {
                                                        eStopName = busStopArray.get(i).getBusStopName();
                                                    }
                                                }
                                                sTextView.setText("?????? ?????? : "+notice.getBusNum()+"???\n"+"?????? ?????? : "+(sTimer/60)+"??? "+(sTimer%60)+"??? ???\n"+"?????? ?????? : "+sStopName+"\n");
                                                eTextView.setText("?????? ?????? : "+notice.getBusNum()+"???\n"+"?????? ?????? : "+(eTimer/60)+"??? "+(eTimer%60)+"??? ???\n"+"?????? ?????? : "+eStopName+"\n");
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) { }
                                        });
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) { }
                                });
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}