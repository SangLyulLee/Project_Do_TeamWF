package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.map.BusTime;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.textView);

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

                        mDatabaseRef = database.getReference("BusTime").child(snapshot.child("BusNum").getValue(String.class));
                        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                int i=1;
                                for (DataSnapshot snapshot2 : dataSnapshot2.getChildren()) {
                                    if (i == Integer.parseInt(snapshot.child("BusTime").getValue(String.class))) {
                                        BusTime busTime = snapshot2.getValue(BusTime.class);
                                        int eTime = busTime.getHours()*60 + busTime.getMinutes();
                                        int time_int = (Integer.parseInt(simpleDateFormat.format(date))*60) + Integer.parseInt(simpleDateFormat2.format(date));
                                        if (eTime <= time_int) {
                                            database.getReference("Notice").child(snapshot.getKey()).removeValue();
                                        }
                                    }
                                    i++;
                                }
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