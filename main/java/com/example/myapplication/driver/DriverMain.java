package com.example.myapplication.driver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.map.BusTime;
import com.example.myapplication.notice.Notice;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class DriverMain extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
    private DatabaseReference mDatabaseRef = database.getReference("Driver");
    private Driver driver = new Driver();
    private ArrayList<Notice> noticeArray = new ArrayList<>();
    private ArrayList<String> routeArray = new ArrayList<>();
    private ArrayList<String> timerArray = new ArrayList<>();
    BusTime busTime = new BusTime();
    private AlarmManager alarmManager;
    PendingIntent pendingIntent, pendingIntent2;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
    private SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("mm");
    int uType, sType, drv_pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ImageView driver_image = (ImageView) findViewById(R.id.driver_image);
        //driver_image.setImageResource(R.drawable.driver_non);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        final Calendar calendar = Calendar.getInstance();
        final Intent my_intent = new Intent(DriverMain.this, Driver_Alarm.class);

        drv_pos = 0;

        mDatabaseRef = database.getReference("Driver");
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if ((firebaseUser.getUid()).equals(snapshot.child("Uid").getValue(String.class))) {
                        driver.setBusNum(snapshot.child("BusNum").getValue(String.class));
                        driver.setBusTime(snapshot.child("BusTime").getValue(String.class));
                        driver.setUid(snapshot.child("Uid").getValue(String.class));

                        Button seat_btn = (Button) findViewById(R.id.btn_seat);
                        seat_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent btn_intent = new Intent(DriverMain.this, Driver_EbusSet.class);
                                btn_intent.putExtra("busNum", driver.getBusNum());
                                btn_intent.putExtra("busTime", driver.getBusTime());
                                startActivity(btn_intent);
                            }
                        });

                        mDatabaseRef = database.getReference("BusRoute").child("1").child("route").child(driver.getBusNum());
                        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                routeArray.clear();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    routeArray.add(snapshot.getValue(String.class));
                                }
                                mDatabaseRef = database.getReference("Notice");
                                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        noticeArray.clear();
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            Notice notice = snapshot.getValue(Notice.class);
                                            if (notice.getBusNum().equals(driver.getBusNum()) && notice.getBusTime().equals(driver.getBusTime())) {
                                                noticeArray.add(notice);
                                            }
                                        }

                                        for (int j = 0; j < noticeArray.size(); j++) {
                                            if (noticeArray.get(j).getSbusStopNum().equals(routeArray.get(0))) {
                                                if (noticeArray.get(j).getU_type() == 1) {
                                                    uType += 1;
                                                    //Toast.makeText(DriverMain.this, "j : "+Integer.toString(j)+"\ni_pos : "+Integer.toString(finalI_pos[0]), Toast.LENGTH_SHORT).show();
                                                } else if (noticeArray.get(j).getU_type() == 2) {
                                                    uType += 2;
                                                }
                                                sType += 1;
                                            } else if (noticeArray.get(j).getEbusStopNum().equals(routeArray.get(0))) {
                                                if (noticeArray.get(j).getU_type() == 1) {
                                                    uType += 1;
                                                } else if (noticeArray.get(j).getU_type() == 2) {
                                                    uType += 2;
                                                }
                                                sType += 2;
                                            }
                                        }
                                        switch (sType) {
                                            case 1:
                                                switch (uType) {
                                                    case 1:
                                                        driver_image.setImageResource(R.drawable.driver1_1);
                                                        break;
                                                    case 2:
                                                        driver_image.setImageResource(R.drawable.driver1_2);
                                                        break;
                                                    case 3:
                                                        driver_image.setImageResource(R.drawable.driver1_3);
                                                        break;
                                                    default:
                                                        break;
                                                }
                                                Toast.makeText(DriverMain.this, "잠시 후 장애인이 탑승할 예정입니다.", Toast.LENGTH_SHORT).show();
                                                break;
                                            default:
                                                driver_image.setImageResource(R.drawable.driver_non);
                                                break;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) { }
                                });
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

                        mDatabaseRef = database.getReference("BusRoute").child("1").child("timer").child(driver.getBusNum());
                        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                timerArray.clear();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    timerArray.add(snapshot.getValue(String.class));
                                }

                                int i_pos = 1;
                                Date date = new Date();
                                String time_hours = simpleDateFormat.format(date);
                                String time_minutes = simpleDateFormat2.format(date);
                                int intStime = Integer.parseInt(time_hours)*60 + Integer.parseInt(time_minutes);
                                int busStime = busTime.getHours()*60 + busTime.getMinutes();
                                for (int timer_pos=0; timer_pos<timerArray.size(); timer_pos++) {
                                    if (timer_pos == 0) {
                                        if (intStime < busStime) {
                                            i_pos = 1;
                                            break;
                                        }
                                    }
                                    else {
                                        if (intStime < busStime+(Integer.parseInt(timerArray.get(timer_pos))/60)) {
                                            i_pos = timer_pos;
                                            break;
                                        }
                                    }
                                }

                                final int[] finalI_pos = {i_pos};
                                //Toast.makeText(DriverMain.this, "finalI_pos[0] : "+Integer.toString(finalI_pos[0]), Toast.LENGTH_SHORT).show();

                                TimerTask timerTask = new TimerTask() {
                                    @Override
                                    public void run() {
                                        uType = 0;
                                        sType = 0;
                                        mDatabaseRef = database.getReference("Notice");
                                        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                noticeArray.clear();
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    Notice notice = snapshot.getValue(Notice.class);
                                                    if (notice.getBusNum().equals(driver.getBusNum()) && notice.getBusTime().equals(driver.getBusTime())) {
                                                        noticeArray.add(notice);
                                                    }
                                                }

                                                mDatabaseRef = database.getReference("BusRoute").child("1").child("route").child(driver.getBusNum());
                                                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        routeArray.clear();
                                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                            routeArray.add(snapshot.getValue(String.class));
                                                        }
                                                        for (int j = 0; j < noticeArray.size(); j++) {
                                                            if (noticeArray.get(j).getSbusStopNum().equals(routeArray.get(finalI_pos[0]))) {
                                                                if (noticeArray.get(j).getU_type() == 1) {
                                                                    uType += 1;
                                                                    //Toast.makeText(DriverMain.this, "j : "+Integer.toString(j)+"\ni_pos : "+Integer.toString(finalI_pos[0]), Toast.LENGTH_SHORT).show();
                                                                } else if (noticeArray.get(j).getU_type() == 2) {
                                                                    uType += 2;
                                                                }
                                                                sType += 1;
                                                            } else if (noticeArray.get(j).getEbusStopNum().equals(routeArray.get(finalI_pos[0]))) {
                                                                if (noticeArray.get(j).getU_type() == 1) {
                                                                    uType += 1;
                                                                } else if (noticeArray.get(j).getU_type() == 2) {
                                                                    uType += 2;
                                                                }
                                                                sType += 2;
                                                            }
                                                        }
                                                        switch (sType) {
                                                            case 1:
                                                                switch (uType) {
                                                                    case 1:
                                                                        driver_image.setImageResource(R.drawable.driver1_1);
                                                                        break;
                                                                    case 2:
                                                                        driver_image.setImageResource(R.drawable.driver1_2);
                                                                        break;
                                                                    case 3:
                                                                        driver_image.setImageResource(R.drawable.driver1_3);
                                                                        break;
                                                                    default:
                                                                        break;
                                                                }
                                                                Toast.makeText(DriverMain.this, "잠시 후 장애인이 탑승할 예정입니다.", Toast.LENGTH_SHORT).show();
                                                                break;
                                                            case 2:
                                                                switch (uType) {
                                                                    case 1:
                                                                        driver_image.setImageResource(R.drawable.driver2_1);
                                                                        break;
                                                                    case 2:
                                                                        driver_image.setImageResource(R.drawable.driver2_2);
                                                                        break;
                                                                    case 3:
                                                                        driver_image.setImageResource(R.drawable.driver2_3);
                                                                        break;
                                                                    default:
                                                                        break;
                                                                }
                                                                Toast.makeText(DriverMain.this, "잠시 후 장애인이 하차할 예정입니다.", Toast.LENGTH_SHORT).show();
                                                                break;
                                                            case 3:
                                                                switch (uType) {
                                                                    case 2:
                                                                        driver_image.setImageResource(R.drawable.driver3_1);
                                                                        break;
                                                                    case 3:
                                                                        driver_image.setImageResource(R.drawable.driver3_2);
                                                                        break;
                                                                    case 4:
                                                                        driver_image.setImageResource(R.drawable.driver3_3);
                                                                        break;
                                                                    default:
                                                                        break;
                                                                }
                                                                Toast.makeText(DriverMain.this, "잠시 후 장애인이 탑승/하차 할 예정입니다.", Toast.LENGTH_SHORT).show();
                                                                break;
                                                            default:
                                                                driver_image.setImageResource(R.drawable.driver_non);
                                                                break;
                                                        }

                                                        if (!(uType == 0 && sType == 0)) {
                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                                pendingIntent = (PendingIntent.getBroadcast(DriverMain.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
                                                            } else {
                                                                pendingIntent = (PendingIntent.getBroadcast(DriverMain.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT));
                                                            }

                                                            if (Build.VERSION.SDK_INT >= 23) {
                                                                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
                                                            } else {
                                                                alarmManager.set(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
                                                            }
                                                        }

                                                        finalI_pos[0]++;
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) { }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                                    }
                                };

                                Timer timer = new Timer();
                                calendar.setTimeInMillis(System.currentTimeMillis());
                                int eTimer_min = 0;
                                if (timerArray.size() != 0) {
                                    eTimer_min = (Integer.parseInt(timerArray.get(timerArray.size()-1))/60) / (timerArray.size()-1);
                                }
                                calendar.set(Calendar.HOUR_OF_DAY, busTime.getHours());
                                calendar.set(Calendar.MINUTE, busTime.getMinutes());
                                timer.schedule(timerTask, calendar.getTime(), eTimer_min*60*1000);

                                // 운행 종료
                                /*
                                Timer timer_end = new Timer();
                                TimerTask ttend = new TimerTask() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(DriverMain.this, "운행을 종료합니다.", Toast.LENGTH_SHORT).show();
                                        database.getReference("driver").child(Integer.toString(drv_pos)).removeValue();
                                        finish();
                                    }
                                };
                                */

                                calendar.setTimeInMillis(System.currentTimeMillis());
                                if (timerArray.size() != 0) {
                                    eTimer_min = Integer.parseInt(timerArray.get(timerArray.size()-1))/60;
                                    if (eTimer_min + busTime.getMinutes() < 60) {
                                        calendar.set(Calendar.HOUR_OF_DAY, busTime.getHours());
                                        calendar.set(Calendar.MINUTE, busTime.getMinutes() + eTimer_min);
                                    }
                                    else {
                                        calendar.set(Calendar.HOUR_OF_DAY, busTime.getHours() + 1);
                                        calendar.set(Calendar.MINUTE, busTime.getMinutes() + eTimer_min - 60);
                                    }
                                }
                                Intent end_intent = new Intent(DriverMain.this, Driver_EndAlarm.class);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    pendingIntent2 = (PendingIntent.getBroadcast(DriverMain.this, 1, end_intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
                                } else {
                                    pendingIntent2 = (PendingIntent.getBroadcast(DriverMain.this, 1, end_intent, PendingIntent.FLAG_UPDATE_CURRENT));
                                }

                                if (Build.VERSION.SDK_INT >= 23) {
                                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent2);
                                } else {
                                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent2);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });

                        break;
                    }
                    drv_pos++;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        // 기사 - API 버전
        mDatabaseRef = database.getReference("Driver_api");
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if ((firebaseUser.getUid()).equals(snapshot.child("Uid").getValue(String.class))) {
                        //
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
/*
        mDatabaseRef = database.getReference("Notice");
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noticeArray.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Notice notice = snapshot.getValue(Notice.class);

                    if (notice.getBusNum().equals(driver.getBusNum()) && notice.getBusTime().equals(driver.getBusTime())) {
                        Timer timer2 = new Timer();
                        TimerTask timerTask2 = new TimerTask() {
                            @Override
                            public void run() {
                                driver_image.setImageResource(R.drawable.non);
                            }
                        };

                        Timer timer = new Timer();
                        TimerTask timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                switch (notice.getU_type()) {
                                    case 1:
                                        //driver_image.setImageResource(R.drawable.non);
                                        break;
                                    case 2:
                                        //driver_image.setImageResource(R.drawable.non);
                                        break;
                                }
                                timer2.schedule(timerTask2, 4900, 0);
                            }
                        };*/

                        /*
                        // 탑승 알람 추가
                        for (int i=0; i<routeArray.size(); i++) {
                            if (notice.getSbusStopNum().equals(routeArray.get(i))) {
                                if (i == 0) {
                                    s_time = 3;
                                }
                                else {
                                    s_time = (Integer.parseInt(timerArray.get(i)) / 60) - (Integer.parseInt(timerArray.get(i-1)) / 60);
                                }
                            }
                            break;
                        }

                        if (busTime.getMinutes() >= s_time) {
                            busTime.setMinutes(busTime.getMinutes() - s_time);
                        }
                        else {
                            busTime.setHours(busTime.getHours() - 1);
                            busTime.setMinutes(busTime.getMinutes() + 60 - s_time);
                        }

                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.set(Calendar.HOUR_OF_DAY, busTime.getHours());
                        calendar.set(Calendar.MINUTE, busTime.getMinutes());

                        my_intent.putExtra("uType", notice.getU_type());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            pendingIntentArray.add(PendingIntent.getBroadcast(DriverMain.this, alarm_num, my_intent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
                        }
                        else {
                            pendingIntentArray.add(PendingIntent.getBroadcast(DriverMain.this, alarm_num, my_intent, PendingIntent.FLAG_UPDATE_CURRENT));
                        }

                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntentArray.get(alarm_num++));

                    //    String time_hours = simpleDateFormat.format(date);
                    //    String time_minutes = simpleDateFormat2.format(date);
                    //    int timerTime = (busTime.getMinutes() + (busTime.getHours()*60)) - ((Integer.parseInt(time_hours)*60)+Integer.parseInt(time_minutes));
                    //    timer.schedule(timerTask, timerTime*60*1000, 0);

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
        });*/
    }
}
