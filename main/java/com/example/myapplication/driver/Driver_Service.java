package com.example.myapplication.driver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import java.util.Date;

public class Driver_Service extends Service {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseRef;
    private ArrayList<Notice> noticeArray = new ArrayList<>();
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
    private Driver driver = new Driver();
    private BusTime busTime = new BusTime();
    private ArrayList<String> busRouteArray = new ArrayList<>();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
    private SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("mm");
    private Date date = new Date();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mDatabaseRef = database.getReference("Driver");
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("Uid").getValue(String.class).equals(firebaseUser.getUid())) {
                        driver = snapshot.getValue(Driver.class);
                        break;
                    }
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

        mDatabaseRef = database.getReference("BusRoute").child("1").child("route").child(driver.getBusNum());
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                busRouteArray.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    busRouteArray.add(snapshot.getValue(String.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mDatabaseRef = database.getReference("Notice");
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noticeArray.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("BusNum").getValue(String.class).equals(driver.getBusNum())
                            && snapshot.child("BusTime").getValue(String.class).equals(driver.getBusTime()))
                        noticeArray.add(snapshot.getValue(Notice.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });


        String time_hours = simpleDateFormat.format(date);
        String time_minutes = simpleDateFormat2.format(date);
        for (int i=0; i<noticeArray.size(); i++) {
            if (noticeArray.get(i).getSbusStopNum())
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
