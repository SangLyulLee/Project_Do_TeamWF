package com.example.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.map.BusTime;
import com.example.myapplication.notice.Alarm_Reciver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
    private DatabaseReference mDatabaseRef = database.getReference("member").child("UserAccount").child(firebaseUser.getUid()).child("name");
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
    private SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("mm");
    private Date date = new Date();
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.textView);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);

        /* name 출력 */
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textView.setText(snapshot.getValue(String.class)+"님, 환영합니다");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        /* 버튼 기능 */
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Menu1.class);
                startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Menu2.class);
                startActivity(intent);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "회원님의 알림 내역이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        /* 알림 자동 삭제 */
        mDatabaseRef = database.getReference("Notice");
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("Uid").getValue(String.class).equals(firebaseUser.getUid())) {
                        mDatabaseRef = database.getReference("BusRoute").child("1").child("route").child(snapshot.child("BusNum").getValue(String.class));
                        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                int i = 0;
                                for (DataSnapshot snapshot1 : dataSnapshot1.getChildren()) {
                                    if (snapshot.child("EbusStopNum").getValue(String.class).equals(snapshot1.getValue(String.class))) {
                                        break;
                                    }
                                    i++;
                                }

                                final int pos = i;

                                i = 0;
                                for (DataSnapshot snapshot1 : dataSnapshot1.getChildren()) {
                                    if (snapshot.child("SbusStopNum").getValue(String.class).equals(snapshot1.getValue(String.class))) {
                                        break;
                                    }
                                    i++;
                                }
                                final int pos_s = i;

                                mDatabaseRef = database.getReference("BusTime").child(snapshot.child("BusNum").getValue(String.class));
                                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                        int j=1;
                                        for (DataSnapshot snapshot2 : dataSnapshot2.getChildren()) {
                                            if (j == Integer.parseInt(snapshot.child("BusTime").getValue(String.class))) {
                                                BusTime busTime = snapshot2.getValue(BusTime.class);
                                                mDatabaseRef = database.getReference("BusRoute").child("1").child("timer").child(snapshot.child("BusNum").getValue(String.class));
                                                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                                        int j = 0;
                                                        for (DataSnapshot snapshot1 : dataSnapshot1.getChildren()) {
                                                            if (j == pos) {
                                                                int eTime = (busTime.getHours()*60) + busTime.getMinutes() + (Integer.parseInt(snapshot1.getValue(String.class))/60);
                                                                int time_int = (Integer.parseInt(simpleDateFormat.format(date))*60) + Integer.parseInt(simpleDateFormat2.format(date));
                                                                if (eTime <= time_int) {
                                                                    database.getReference("Notice").child(snapshot.getKey()).removeValue();

                                                                    for (int seat_pos = pos_s+1; seat_pos<=pos+1; seat_pos++) {
                                                                        database.getReference("BusSeat")
                                                                                .child(snapshot.child("BusNum").getValue(String.class))
                                                                                .child(snapshot.child("BusTime").getValue(String.class))
                                                                                .child("route" + Integer.toString(seat_pos)).setValue(0);
                                                                    }

                                                                    if (alarmManager != null) {
                                                                        Intent my_intent = new Intent(MainActivity.this, Alarm_Reciver.class);
                                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                                            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                                                                        } else {
                                                                            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                                                        }
                                                                        alarmManager.cancel(pendingIntent);
                                                                    }
                                                                }
                                                                else {
                                                                    /* 알림 확인 버튼 활성화 */
                                                                    Button button3 = (Button) findViewById(R.id.button3);
                                                                    button3.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            Intent intent = new Intent(MainActivity.this, Menu3.class);
                                                                            intent.putExtra("noticeKey", snapshot.getKey());
                                                                            startActivity(intent);
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                            j++;
                                                        }
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) { }
                                                });
                                            }
                                            j++;
                                        }
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
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mDatabaseRef = database.getReference("Notice");
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("Uid").getValue(String.class).equals(firebaseUser.getUid())) {
                        mDatabaseRef = database.getReference("BusRoute").child("1").child("route").child(snapshot.child("BusNum").getValue(String.class));
                        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                int i = 0;
                                for (DataSnapshot snapshot1 : dataSnapshot1.getChildren()) {
                                    if (snapshot.child("EbusStopNum").getValue(String.class).equals(snapshot1.getValue(String.class))) {
                                        break;
                                    }
                                    i++;
                                }

                                final int pos = i;

                                i = 0;
                                for (DataSnapshot snapshot1 : dataSnapshot1.getChildren()) {
                                    if (snapshot.child("SbusStopNum").getValue(String.class).equals(snapshot1.getValue(String.class))) {
                                        break;
                                    }
                                    i++;
                                }
                                final int pos_s = i;

                                mDatabaseRef = database.getReference("BusTime").child(snapshot.child("BusNum").getValue(String.class));
                                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                        int j=1;
                                        for (DataSnapshot snapshot2 : dataSnapshot2.getChildren()) {
                                            if (j == Integer.parseInt(snapshot.child("BusTime").getValue(String.class))) {
                                                BusTime busTime = snapshot2.getValue(BusTime.class);
                                                mDatabaseRef = database.getReference("BusRoute").child("1").child("timer").child(snapshot.child("BusNum").getValue(String.class));
                                                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                                        int j = 0;
                                                        for (DataSnapshot snapshot1 : dataSnapshot1.getChildren()) {
                                                            if (j == pos) {
                                                                int eTime = (busTime.getHours()*60) + busTime.getMinutes() + (Integer.parseInt(snapshot1.getValue(String.class))/60);
                                                                int time_int = (Integer.parseInt(simpleDateFormat.format(date))*60) + Integer.parseInt(simpleDateFormat2.format(date));
                                                                if (eTime <= time_int) {
                                                                    database.getReference("Notice").child(snapshot.getKey()).removeValue();

                                                                    for (int seat_pos = pos_s+1; seat_pos<=pos+1; seat_pos++) {
                                                                        database.getReference("BusSeat")
                                                                                .child(snapshot.child("BusNum").getValue(String.class))
                                                                                .child(snapshot.child("BusTime").getValue(String.class))
                                                                                .child("route" + Integer.toString(seat_pos)).setValue(0);
                                                                    }

                                                                    if (alarmManager != null) {
                                                                        Intent my_intent = new Intent(MainActivity.this, Alarm_Reciver.class);
                                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                                            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                                                                        } else {
                                                                            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                                                        }
                                                                        alarmManager.cancel(pendingIntent);
                                                                    }
                                                                }
                                                                else {
                                                                    /* 알림 확인 버튼 활성화 */
                                                                    Button button3 = (Button) findViewById(R.id.button3);
                                                                    button3.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            Intent intent = new Intent(MainActivity.this, Menu3.class);
                                                                            intent.putExtra("noticeKey", snapshot.getKey());
                                                                            startActivity(intent);
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                            j++;
                                                        }
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) { }
                                                });
                                            }
                                            j++;
                                        }
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
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}