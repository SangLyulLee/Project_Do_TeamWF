package com.example.myapplication.api_driver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.api_notice.NoticeApi;
import com.example.myapplication.api_ver.get_api;
import com.example.myapplication.driver.DriverSelect;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class DriverMain_Api extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
    private DatabaseReference mDatabaseRef;
    private AlarmManager alarmManager;
    PendingIntent pendingIntent;
    Driver_Api driver_api = new Driver_Api();
    String now_nodeid, now_nodeord = "1", d_nodeord = "x";
    String dataKey;
    Timer timer = new Timer();
    boolean ridingBus = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ImageView driver_image = (ImageView) findViewById(R.id.driver_image);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        final Intent my_intent = new Intent(DriverMain_Api.this, Driver_Alarm_api.class);

        Button seat_btn = (Button) findViewById(R.id.btn_seat);
        seat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent btn_intent = new Intent(DriverMain_Api.this, Driver_EbusSet_api.class);
                startActivity(btn_intent);
            }
        });

        // 기사 - API 버전
        mDatabaseRef = database.getReference("Driver_api");
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if ((firebaseUser.getUid()).equals(snapshot.child("Uid").getValue(String.class))) {
                        driver_api.setRouteId(snapshot.child("routeid").getValue(String.class));
                        driver_api.setUid(snapshot.child("Uid").getValue(String.class));
                        driver_api.setVehicleNo(snapshot.child("vehicleno").getValue(String.class));
                        driver_api.setCityCode(snapshot.child("citycode").getValue(String.class));
                        dataKey = snapshot.getKey();

                        // 루트 전체 정보
                        String[] api_split4 = get_api.getBusRoute(driver_api.getCityCode(), driver_api.getRouteId(), "1").split("\n");
                        String[] route_nodeidArr = new String[api_split4.length], route_nodeordArr = new String[api_split4.length];
                        // [0] : nodeid, [1] : nodeno, [2] : nodeord
                        for (int i=0; i<api_split4.length; i++) {
                            String[] api_split3 = api_split4[i].split(" ");
                            route_nodeidArr[i] = api_split3[2];
                            route_nodeordArr[i] = api_split3[5];
                        }


                        // 반복하여 갱신하며 nodeid 비교
                        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
                        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                                "MyApp::MyWakelockTag");

                        TimerTask timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                System.out.println("알림 검사 테스트");
                                String[] api_split = get_api.getBusServiceData(driver_api.getCityCode(), driver_api.getRouteId(), "1").split("\n");
                                // [0] : 정류장id, [1] : 정류장순번, [2] : 차량번호
                                for (int i = 0; i < api_split.length; i++) {
                                    String[] api_split2 = api_split[i].split(" ");
                                    if (api_split2.length < 2) {
                                        System.out.println("API 서버 오류");
                                    }
                                    else {
                                        if (api_split2[2].equals(driver_api.getVehicleNo())) {
                                            now_nodeid = api_split2[0];
                                            now_nodeord = api_split2[1];
                                            if (api_split2[1].equals(Integer.toString(api_split4.length))) {
                                                System.out.println("운행 종료");
                                                database.getReference("Driver_api").child(Objects.requireNonNull(snapshot.getKey())).removeValue();

                                                mDatabaseRef = database.getReference("Notice_api");
                                                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                                        for (DataSnapshot snapshot2 : dataSnapshot2.getChildren()) {
                                                            if (snapshot2.child("Vehicleno").getValue(String.class).equals(driver_api.getVehicleNo())) {
                                                                mDatabaseRef.child(Objects.requireNonNull(snapshot2.getKey())).removeValue();
                                                            }
                                                        }
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) { }
                                                });
                                                timer.cancel();
                                                ridingBus = false;
                                                Intent intent = new Intent(DriverMain_Api.this, DriverSelect.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            break;
                                        }
                                    }
                                }
                                if (ridingBus) {
                                    if (d_nodeord.equals(now_nodeord)) {
                                        System.out.println("다음 정류장으로 이동 중..");
                                    } else {
                                        d_nodeord = now_nodeord;

                                        ArrayList<NoticeApi> noticeArray = new ArrayList<>();
                                        noticeArray.clear();
                                        databaseReference = database.getReference().child("Notice_api");
                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    if (snapshot.child("RouteId").getValue(String.class).equals(driver_api.getRouteId())) {
                                                        noticeArray.add(snapshot.getValue(NoticeApi.class));
                                                    }
                                                }

                                                int notice_getOn = 0;
                                                int notice_getOff = 0;
                                                for (int i = 0; i < noticeArray.size(); i++) {
                                                    if (Integer.parseInt(now_nodeord) != 1) {
                                                        System.out.println("noticeArray.get(i).getSbusStopNodeId() : " + noticeArray.get(i).getSbusStopNodeId());
                                                        System.out.println("route_nodeidArr[Integer.parseInt(now_nodeord)]) : " + route_nodeidArr[Integer.parseInt(now_nodeord)]);
                                                        if (noticeArray.get(i).getSbusStopNodeId().equals(route_nodeidArr[Integer.parseInt(now_nodeord)])) {
                                                            switch (noticeArray.get(i).getU_type()) {
                                                                case 1:
                                                                    if (notice_getOn == 0)
                                                                        notice_getOn = 1;
                                                                    else if (notice_getOn == 2)
                                                                        notice_getOn = 3;
                                                                    break;
                                                                case 2:
                                                                    if (notice_getOn == 0)
                                                                        notice_getOn = 2;
                                                                    else if (notice_getOn == 1)
                                                                        notice_getOn = 3;
                                                                    break;
                                                                default:
                                                                    break;
                                                            }
                                                            continue;
                                                        } else if (noticeArray.get(i).getEbusStopNodeId().equals(route_nodeidArr[Integer.parseInt(now_nodeord)])) {
                                                            switch (noticeArray.get(i).getU_type()) {
                                                                case 1:
                                                                    if (notice_getOff == 0)
                                                                        notice_getOff = 1;
                                                                    else if (notice_getOff == 2)
                                                                        notice_getOff = 3;
                                                                    break;
                                                                case 2:
                                                                    if (notice_getOff == 0)
                                                                        notice_getOff = 2;
                                                                    else if (notice_getOff == 1)
                                                                        notice_getOff = 3;
                                                                    break;
                                                                default:
                                                                    break;
                                                            }
                                                        }
                                                    }
                                                }

                                                int finalNotice_getOn = notice_getOn;
                                                int finalNotice_getOff = notice_getOff;
                                                System.out.println("finalNotice_getOn, Off : " + finalNotice_getOn + ", " + finalNotice_getOff);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        switch (finalNotice_getOn) {
                                                            case 0:
                                                                switch (finalNotice_getOff) {
                                                                    case 0:
                                                                        driver_image.setImageResource(R.drawable.driver_non);
                                                                        break;
                                                                    case 1:
                                                                        driver_image.setImageResource(R.drawable.driver2_1);
                                                                        Toast.makeText(DriverMain_Api.this, "잠시 후 장애인이 하차할 예정입니다.", Toast.LENGTH_SHORT).show();
                                                                        break;
                                                                    case 2:
                                                                        driver_image.setImageResource(R.drawable.driver2_2);
                                                                        Toast.makeText(DriverMain_Api.this, "잠시 후 장애인이 하차할 예정입니다.", Toast.LENGTH_SHORT).show();
                                                                        break;
                                                                    case 3:
                                                                        driver_image.setImageResource(R.drawable.driver2_3);
                                                                        Toast.makeText(DriverMain_Api.this, "잠시 후 장애인이 하차할 예정입니다.", Toast.LENGTH_SHORT).show();
                                                                        break;
                                                                }
                                                                break;
                                                            case 1:
                                                                switch (finalNotice_getOff) {
                                                                    case 0:
                                                                        driver_image.setImageResource(R.drawable.driver1_1);
                                                                        Toast.makeText(DriverMain_Api.this, "잠시 후 장애인이 탑승할 예정입니다.", Toast.LENGTH_SHORT).show();
                                                                        break;
                                                                    case 1:
                                                                        driver_image.setImageResource(R.drawable.driver3_1);
                                                                        Toast.makeText(DriverMain_Api.this, "잠시 후 장애인이 탑승/하차할 예정입니다.", Toast.LENGTH_SHORT).show();
                                                                        break;
                                                                    case 2:
                                                                        driver_image.setImageResource(R.drawable.driver3_3);
                                                                        Toast.makeText(DriverMain_Api.this, "잠시 후 장애인이 탑승/하차할 예정입니다.", Toast.LENGTH_SHORT).show();
                                                                        break;
                                                                    case 3:
                                                                        driver_image.setImageResource(R.drawable.driver3_3);
                                                                        Toast.makeText(DriverMain_Api.this, "잠시 후 장애인이 탑승/하차할 예정입니다.", Toast.LENGTH_SHORT).show();
                                                                        break;
                                                                }
                                                                break;
                                                            case 2:
                                                                switch (finalNotice_getOff) {
                                                                    case 0:
                                                                        driver_image.setImageResource(R.drawable.driver1_2);
                                                                        Toast.makeText(DriverMain_Api.this, "잠시 후 장애인이 탑승할 예정입니다.", Toast.LENGTH_SHORT).show();
                                                                        break;
                                                                    case 1:
                                                                        driver_image.setImageResource(R.drawable.driver3_3);
                                                                        Toast.makeText(DriverMain_Api.this, "잠시 후 장애인이 탑승/하차할 예정입니다.", Toast.LENGTH_SHORT).show();
                                                                        break;
                                                                    case 2:
                                                                        driver_image.setImageResource(R.drawable.driver3_2);
                                                                        Toast.makeText(DriverMain_Api.this, "잠시 후 장애인이 탑승/하차할 예정입니다.", Toast.LENGTH_SHORT).show();
                                                                        break;
                                                                    case 3:
                                                                        driver_image.setImageResource(R.drawable.driver3_3);
                                                                        Toast.makeText(DriverMain_Api.this, "잠시 후 장애인이 탑승/하차할 예정입니다.", Toast.LENGTH_SHORT).show();
                                                                        break;
                                                                }
                                                                break;
                                                            case 3:
                                                                switch (finalNotice_getOff) {
                                                                    case 0:
                                                                        driver_image.setImageResource(R.drawable.driver1_3);
                                                                        Toast.makeText(DriverMain_Api.this, "잠시 후 장애인이 탑승할 예정입니다.", Toast.LENGTH_SHORT).show();
                                                                        break;
                                                                    case 1:
                                                                        driver_image.setImageResource(R.drawable.driver3_3);
                                                                        Toast.makeText(DriverMain_Api.this, "잠시 후 장애인이 탑승/하차할 예정입니다.", Toast.LENGTH_SHORT).show();
                                                                        break;
                                                                    case 2:
                                                                        driver_image.setImageResource(R.drawable.driver3_3);
                                                                        Toast.makeText(DriverMain_Api.this, "잠시 후 장애인이 탑승/하차할 예정입니다.", Toast.LENGTH_SHORT).show();
                                                                        break;
                                                                    case 3:
                                                                        driver_image.setImageResource(R.drawable.driver3_3);
                                                                        Toast.makeText(DriverMain_Api.this, "잠시 후 장애인이 탑승/하차할 예정입니다.", Toast.LENGTH_SHORT).show();
                                                                        break;
                                                                }
                                                                break;
                                                            default:
                                                                break;
                                                        }
                                                    }
                                                });
                                                if (!(notice_getOn == 0 && notice_getOff == 0)) {
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                        pendingIntent = (PendingIntent.getBroadcast(DriverMain_Api.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
                                                    } else {
                                                        pendingIntent = (PendingIntent.getBroadcast(DriverMain_Api.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT));
                                                    }

                                                    if (Build.VERSION.SDK_INT >= 23) {
                                                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
                                                    } else {
                                                        alarmManager.set(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                                    }
                                }
                            }
                        };
                        timer.schedule(timerTask, 5*1000, 5*1000);
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
