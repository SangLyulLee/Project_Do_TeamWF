package com.example.myapplication.api_ver;

import static com.example.myapplication.api_ver.get_api.getStaionBusData;

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

import java.util.Timer;
import java.util.TimerTask;

public class Menu3_api extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
    private DatabaseReference databaseReference;
    NoticeApi noticeData;
    String noticeKey;
    String nowNodeord;
    Timer timer = new Timer();
    TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu3_api);

        TextView textView = (TextView) findViewById(R.id.textView5);
        Button noticeR_btn = (Button) findViewById(R.id.menu3button1);
        Button noticeC_btn = (Button) findViewById(R.id.menu3button2);

        String sNodeNm = getIntent().getStringExtra("sNodeNm");
        String eNodeNm = getIntent().getStringExtra("eNodeNm");
        String sNodeord = getIntent().getStringExtra("sNodeord");
        String eNodeord = getIntent().getStringExtra("eNodeord");

        timerTask = new TimerTask() {
            @Override
            public void run() {
                databaseReference = database.getReference().child("Notice_api");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.child("Uid").getValue(String.class).equals(firebaseUser.getUid())) {
                                noticeData = snapshot.getValue(NoticeApi.class);
                                noticeKey = snapshot.getKey();

                                String[] RouteposData = get_api.getBusServiceData(noticeData.getCityCode(), noticeData.getRouteId(), "1").split("\n");
                                for (int i = 0; i < RouteposData.length; i++) {
                                    String[] Routepos_List = RouteposData[i].split(" ");
                                    if (Routepos_List.length < 2) {
                                        System.out.println("API 서버 오류");
                                    } else {
                                        if (Routepos_List[2].equals(noticeData.getVehicleno())) {
                                            nowNodeord = Routepos_List[1];
                                            break;
                                        }
                                    }
                                }

                                if (noticeData.getbusRide().equals("0")) {
                                    String[] busDataList = getStaionBusData(noticeData.getCityCode(), noticeData.getRouteId(), noticeData.getSbusStopNodeId()).split("\n");
                                    for (int i = 0; i < busDataList.length; i++) {
                                        String[] busData_List = busDataList[i].split(" ");
                                        // [0]:남은정류장수, [1]:남은시간, [2]:정류장명, [3]:버스번호
                                        if (busData_List[0].equals("")) {
                                            System.out.println("API서버 오류");
                                        } else {
                                            if (Integer.parseInt(busData_List[0]) + Integer.parseInt(nowNodeord) == Integer.parseInt(sNodeord)) {
                                                textView.setText("탑승 대기 중...\n\n탑승 정류장 : " + busData_List[2] + "\n버스 번호 : " + busData_List[3] + "\n남은 시간 : " + busData_List[1] + " 분\n남은 정류장 수 : " + busData_List[0] + "\n\n하차 정류장 : " + eNodeNm);
                                                break;
                                            }
                                        }
                                    }
                                } else if (noticeData.getbusRide().equals("1")) {
                                    String[] busDataList = getStaionBusData(noticeData.getCityCode(), noticeData.getRouteId(), noticeData.getEbusStopNodeId()).split("\n");
                                    for (int i = 0; i < busDataList.length; i++) {
                                        String[] busData_List = busDataList[i].split(" ");
                                        // [0]:남은정류장수, [1]:남은시간, [2]:정류장명, [3]:버스번호
                                        if (busData_List[0].equals("")) {
                                            System.out.println("API서버 오류");
                                        } else {
                                            if (Integer.parseInt(busData_List[0]) + Integer.parseInt(nowNodeord) == Integer.parseInt(eNodeord)) {
                                                textView.setText("하차 대기 중...\n\n탑승 정류장 : " + sNodeNm + "\n\n하차 정류장 : " + busData_List[2] + "\n버스 번호 : " + busData_List[3] + "\n남은 시간 : " + busData_List[1] + " 분\n남은 정류장 수 : " + busData_List[0]);
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
        };
        timer.schedule(timerTask, 0, 10*1000);

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
    protected void OnPause() {
        timer.cancel();
    }
    protected void OnResume() {
        timer.schedule(timerTask, 0, 10*1000);
    }
}
