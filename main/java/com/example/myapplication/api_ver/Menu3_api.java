package com.example.myapplication.api_ver;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.api_notice.NoticeRe_api;
import com.example.myapplication.api_notice.NoticeApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Menu3_api extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
    private DatabaseReference databaseReference;
    NoticeApi noticeData;
    String noticeKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu3_api);

        TextView textView = (TextView) findViewById(R.id.textView5);
        Button noticeR_btn = (Button) findViewById(R.id.menu3button1);
        Button noticeC_btn = (Button) findViewById(R.id.menu3button2);

        String busData = getIntent().getStringExtra("busDataList");
        String sNodeNm = getIntent().getStringExtra("sNodeNm");
        String eNodeNm = getIntent().getStringExtra("eNodeNm");
        String[] busDataList = busData.split(" ");

        databaseReference = database.getReference().child("Notice_api");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("Uid").getValue(String.class).equals(firebaseUser.getUid())) {
                        noticeData = snapshot.getValue(NoticeApi.class);
                        noticeKey = snapshot.getKey();
                        if (noticeData.getbusRide().equals("0")) {
                            textView.setText("탑승 대기 중...\n\n탑승 정류장 : " + busDataList[2] + "\n버스 번호 : " + busDataList[3] + "\n남은 시간 : " + busDataList[1] + "\n남은 정류장 수 : " + busDataList[0] + "\n\n하차 정류장 : " + eNodeNm);
                        }
                        else if (noticeData.getbusRide().equals("1")) {
                            textView.setText("하차 대기 중...\n\n탑승 정류장 : " + sNodeNm + "\n\n하차 정류장 : " + busDataList[2] + "\n버스 번호 : " + busDataList[3] + "\n남은 시간 : " + busDataList[1] + "\n남은 정류장 수 : " + busDataList[0]);
                        }
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

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

        /* 알림 취소 버튼 */
        noticeC_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg_noticeC = new AlertDialog.Builder(Menu3_api.this);
                dlg_noticeC.setTitle("알림 취소 확인");

                dlg_noticeC.setMessage("정말 알림을 취소하시겠습니까?");
                dlg_noticeC.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { }
                });
                dlg_noticeC.setNegativeButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        databaseReference.child(noticeKey).removeValue();
                        Toast.makeText(Menu3_api.this, "알림이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Menu3_api.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                dlg_noticeC.show();
            }
        });
    }
}
