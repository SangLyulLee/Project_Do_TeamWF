package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.map.BusStop;
import com.example.myapplication.notice.Notice;
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
    private DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("Notice");
    private int sTime;
    private int sTimer, eTimer;
    private String sStopName, eStopName;
    private Notice notice = new Notice();

    /*
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
    private SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("mm");
    private Date date = new Date();
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu3);
        TextView sTextView = (TextView) findViewById(R.id.textView3);
        TextView eTextView = (TextView) findViewById(R.id.textView4);

        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if ((snapshot.child("Uid").getValue(String.class)).equals(firebaseUser.getUid())) {
                        notice = snapshot.getValue(Notice.class);
                    }
                }
                mDatabaseRef = FirebaseDatabase.getInstance().getReference("BusTime").child(notice.getBusNum()).child(notice.getBusTime()).child("hours");
                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        sTime = (snapshot.getValue(int.class)*60);
                        mDatabaseRef = FirebaseDatabase.getInstance().getReference("BusTime").child(notice.getBusNum()).child(notice.getBusTime()).child("minutes");
                        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                sTime = sTime + snapshot.getValue(int.class);

                                mDatabaseRef = FirebaseDatabase.getInstance().getReference("BusRoute").child(notice.getBusNum());
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

                                        mDatabaseRef = FirebaseDatabase.getInstance().getReference("BusStop");
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

                                                /*
                                                String time_hours = simpleDateFormat.format(date);
                                                String time_minutes = simpleDateFormat2.format(date);
                                                int nTime = (Integer.parseInt(time_hours)*60) + Integer.parseInt(time_minutes);
                                                */

                                                sTextView.setText("버스 번호 : "+notice.getBusNum()+"번\n"+"탑승 시간 : "+(sTimer/60)+"시 "+(sTimer%60)+"분 경\n"+"탑승 장소 : "+sStopName+"\n");
                                                eTextView.setText("버스 번호 : "+notice.getBusNum()+"번\n"+"하차 시간 : "+(eTimer/60)+"시 "+(eTimer%60)+"분 경\n"+"하차 장소 : "+eStopName+"\n");
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}