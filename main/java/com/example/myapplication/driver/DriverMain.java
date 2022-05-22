package com.example.myapplication.driver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.map.BusTime;
import com.example.myapplication.notice.Notice;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class DriverMain extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
    private DatabaseReference mDatabaseRef = database.getReference("Driver");
    private Driver driver = new Driver();
    private ArrayList<Notice> noticeArray = new ArrayList<>();
    private ArrayList<String> routeArray = new ArrayList<>();
    private ArrayList<String> timerArray = new ArrayList<>();
    BusTime busTime = new BusTime();
    private AlarmManager alarmManager;
    private ArrayList<PendingIntent> pendingIntentArray = new ArrayList<>();
    private int alarm_num, s_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        s_time = 3;
        alarm_num = 0;

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        final Calendar calendar = Calendar.getInstance();
        final Intent my_intent = new Intent(DriverMain.this, Driver_Alarm.class);

        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("Uid").equals(mFirebaseAuth.getUid())) {
                        driver = snapshot.getValue(Driver.class);
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        mDatabaseRef = database.getReference("BusRoute").child("1").child("route").child(driver.getBusNum());
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                routeArray.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    routeArray.add(snapshot.getValue(String.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        mDatabaseRef = database.getReference("BusRoute").child("1").child("timer").child(driver.getBusNum());
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                timerArray.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    timerArray.add(snapshot.getValue(String.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        mDatabaseRef = database.getReference("BusTime").child(driver.getBusNum()).child(driver.getBusTime());
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                busTime = dataSnapshot.getValue(BusTime.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        mDatabaseRef = database.getReference("Notice");
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noticeArray.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Notice notice = snapshot.getValue(Notice.class);
                    if (notice.getBusNum().equals(driver.getBusNum()) && notice.getBusTime().equals(driver.getBusTime())) {
                        // 탑승 알람 추가
                        for (int i=0; i<routeArray.size(); i++) {
                            if (notice.getSbusStopNum().equals(routeArray.get(i))) {
                                if (i == 0) {
                                    s_time = 3;
                                }
                                else {
                                    s_time = Integer.parseInt(timerArray.get(i)) / 60;
                                }
                            }
                            break;
                        }

                        busTime.setHours(busTime.getHours() + (busTime.getMinutes()+s_time/60));
                        busTime.setMinutes((busTime.getMinutes()+s_time) % 60);

                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.set(Calendar.HOUR_OF_DAY, busTime.getHours());
                        calendar.set(Calendar.MINUTE, busTime.getMinutes());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            pendingIntentArray.add(PendingIntent.getBroadcast(DriverMain.this, alarm_num, my_intent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
                        }
                        else {
                            pendingIntentArray.add(PendingIntent.getBroadcast(DriverMain.this, alarm_num, my_intent, PendingIntent.FLAG_UPDATE_CURRENT));
                        }

                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntentArray.get(alarm_num++));

                        // 도착 알람 추가
                        for (int i=0; i<routeArray.size(); i++) {
                            if (notice.getEbusStopNum().equals(routeArray.get(i))) {
                                if (i == 0) {
                                    s_time = 3;
                                }
                                else {
                                    s_time = Integer.parseInt(timerArray.get(i)) / 60;
                                }
                            }
                            break;
                        }

                        busTime.setHours(busTime.getHours() + (busTime.getMinutes()+s_time/60));
                        busTime.setMinutes((busTime.getMinutes()+s_time) % 60);

                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.set(Calendar.HOUR_OF_DAY, busTime.getHours());
                        calendar.set(Calendar.MINUTE, busTime.getMinutes());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            pendingIntentArray.add(PendingIntent.getBroadcast(DriverMain.this, alarm_num, my_intent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
                        }
                        else {
                            pendingIntentArray.add(PendingIntent.getBroadcast(DriverMain.this, alarm_num, my_intent, PendingIntent.FLAG_UPDATE_CURRENT));
                        }

                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntentArray.get(alarm_num++));

                        noticeArray.add(notice);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        mDatabaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.child("BusNum").getValue(String.class).equals(driver.getBusNum())
                        && (snapshot.child("BusTime").getValue(String.class).equals(driver.getBusTime()))) {
                    // 탑승 알람 추가
                    for (int i=0; i<routeArray.size(); i++) {
                        if (snapshot.child("SbusStopNum").getValue(String.class).equals(routeArray.get(i))) {
                            if (i == 0) {
                                s_time = 3;
                            }
                            else {
                                s_time = Integer.parseInt(timerArray.get(i)) / 60;
                            }
                        }
                        break;
                    }

                    busTime.setHours(busTime.getHours() + (busTime.getMinutes()+s_time/60));
                    busTime.setMinutes((busTime.getMinutes()+s_time) % 60);

                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, busTime.getHours());
                    calendar.set(Calendar.MINUTE, busTime.getMinutes());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        pendingIntentArray.add(PendingIntent.getBroadcast(DriverMain.this, alarm_num, my_intent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
                    }
                    else {
                        pendingIntentArray.add(PendingIntent.getBroadcast(DriverMain.this, alarm_num, my_intent, PendingIntent.FLAG_UPDATE_CURRENT));
                    }

                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntentArray.get(alarm_num++));

                    // 도착 알람 추가
                    for (int i=0; i<routeArray.size(); i++) {
                        if (snapshot.child("EbusStopNum").getValue(String.class).equals(routeArray.get(i))) {
                            if (i == 0) {
                                s_time = 3;
                            }
                            else {
                                s_time = Integer.parseInt(timerArray.get(i)) / 60;
                            }
                        }
                        break;
                    }

                    busTime.setHours(busTime.getHours() + (busTime.getMinutes()+s_time/60));
                    busTime.setMinutes((busTime.getMinutes()+s_time) % 60);

                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, busTime.getHours());
                    calendar.set(Calendar.MINUTE, busTime.getMinutes());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        pendingIntentArray.add(PendingIntent.getBroadcast(DriverMain.this, alarm_num, my_intent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
                    }
                    else {
                        pendingIntentArray.add(PendingIntent.getBroadcast(DriverMain.this, alarm_num, my_intent, PendingIntent.FLAG_UPDATE_CURRENT));
                    }

                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntentArray.get(alarm_num++));

                    noticeArray.add(snapshot.getValue(Notice.class));
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for (int i=0; i<noticeArray.size(); i++) {
                    if ((noticeArray.get(i).getUid()).equals(snapshot.child("Uid").getValue(String.class))) {
                        // 해당 알람 변경
                        for (int j=0; j<routeArray.size(); j++) {
                            if (snapshot.child("EbusStopNum").getValue(String.class).equals(routeArray.get(j))) {
                                if (j == 0) {
                                    s_time = 3;
                                }
                                else {
                                    s_time = Integer.parseInt(timerArray.get(j)) / 60;
                                }
                            }
                            break;
                        }

                        busTime.setHours(busTime.getHours() + (busTime.getMinutes()+s_time/60));
                        busTime.setMinutes((busTime.getMinutes()+s_time) % 60);

                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.set(Calendar.HOUR_OF_DAY, busTime.getHours());
                        calendar.set(Calendar.MINUTE, busTime.getMinutes());

                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntentArray.get(i*2));

                        noticeArray.get(i).setEbusStopNum(snapshot.child("EbusStopNum").getValue(String.class));
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                for (int i=0; i<noticeArray.size(); i++) {
                    if ((noticeArray.get(i).getUid()).equals(snapshot.child("Uid").getValue(String.class))) {
                        // 해당 알람 삭제
                        alarmManager.cancel(pendingIntentArray.get((i*2)-1));
                        alarmManager.cancel(pendingIntentArray.get(i*2));

                        noticeArray.remove(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
