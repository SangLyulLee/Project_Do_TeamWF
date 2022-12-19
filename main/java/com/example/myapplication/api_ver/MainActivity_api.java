package com.example.myapplication.api_ver;

import static com.example.myapplication.api_ver.get_api.getStaionBusData;
import static java.lang.Math.abs;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.api_driver.DriverMain_Api;
import com.example.myapplication.api_driver.Driver_Alarm_api;
import com.example.myapplication.api_notice.Alarm_Reciver_api;
import com.example.myapplication.kakaomap.kakaomapmain;
import com.example.myapplication.api_notice.NoticeApi;
import com.example.myapplication.notice.Alarm_Cancle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity_api extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
    private DatabaseReference mDatabaseRef = database.getReference("member").child("UserAccount").child(firebaseUser.getUid()).child("name");
    double longitude = 0, latitude = 0;
    NoticeApi noticeData;
    String sNodeord, eNodeord, nowNodeord, sNodeNm, eNodeNm;
    boolean termination = true;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    Timer timer = new Timer();
    String getKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_api);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity_api.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                latitude = 34.800774;
                longitude = 126.370871;
            } else {
                longitude = abs(location.getLongitude());
                latitude = location.getLatitude();
            }
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsLocationListener);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsLocationListener);
        }

        TextView textView = (TextView) findViewById(R.id.textView);
        ImageButton button1 = (ImageButton) findViewById(R.id.button1);
        ImageButton button2 = (ImageButton) findViewById(R.id.button2);
        ImageButton button3 = (ImageButton) findViewById(R.id.button3);
        ImageButton button4 = (ImageButton) findViewById(R.id.button4);
        Button button5 = (Button) findViewById(R.id.button_api);
        button5.setVisibility(View.INVISIBLE);

        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textView.setText(snapshot.getValue(String.class)+"님, 환영합니다");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity_api.this, Menu1_api.class);
                startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity_api.this, Menu2_api.class);
                String[] api_split = get_api.getBusStation_ByGps(latitude, longitude, 5).split("\n");
                String[] api_split2 = api_split[0].split(" ");
                intent.putExtra("citycode", api_split2[0]);
                startActivity(intent);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity_api.this, "회원님의 알림 내역이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity_api.this, kakaomapmain.class);
                startActivity(intent);
            }
        });

        mDatabaseRef = database.getReference("Notice_api");
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (firebaseUser.getUid().equals(snapshot.child("Uid").getValue(String.class))) {
                        noticeData = snapshot.getValue(NoticeApi.class);
                        button3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(MainActivity_api.this, "잠시 후에 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        });

                        String[] RouteData = get_api.getBusRoute(noticeData.getCityCode(), noticeData.getRouteId(), "1").split("\n");
                        for (int i=0; i<RouteData.length; i++) {
                            String[] RouteData_List = RouteData[i].split(" ");
                            if (RouteData_List[2].equals(noticeData.getSbusStopNodeId())) {
                                sNodeord = RouteData_List[5];
                                sNodeNm = RouteData_List[3];
                            }
                            if (RouteData_List[2].equals(noticeData.getEbusStopNodeId())) {
                                eNodeord = RouteData_List[5];
                                eNodeNm = RouteData_List[3];
                                break;
                            }
                        }

                        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
                        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                                "MyApp::MyWakelockTag");
                        TimerTask timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                mDatabaseRef = database.getReference("Notice_api");
                                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int j = 1;
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            if (firebaseUser.getUid().equals(snapshot.child("Uid").getValue(String.class))) {
                                                noticeData = snapshot.getValue(NoticeApi.class);
                                                getKey = snapshot.getKey();

                                                button3.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(MainActivity_api.this, Menu3_api.class);
                                                        intent.putExtra("sNodeNm", sNodeNm);
                                                        intent.putExtra("eNodeNm", eNodeNm);
                                                        intent.putExtra("sNodeord", sNodeord);
                                                        intent.putExtra("eNodeord", eNodeord);
                                                        startActivity(intent);
                                                    }
                                                });

                                                String[] busData;
                                                String[] RouteposData = get_api.getBusServiceData(noticeData.getCityCode(), noticeData.getRouteId(), "1").split("\n");
                                                for (int i = 0; i < RouteposData.length; i++) {
                                                    String[] Routepos_List = RouteposData[i].split(" ");
                                                    if (Routepos_List.length < 2) {
                                                        System.out.println("API 서버 오류");
                                                    }
                                                    else {
                                                        if (Routepos_List[2].equals(noticeData.getVehicleno())) {
                                                            nowNodeord = Routepos_List[1];
                                                            termination = false;
                                                            break;
                                                        }
                                                    }
                                                }
                                                if (termination) {
                                                    database.getReference("Notice_api").child(getKey).removeValue();
                                                    timer.cancel();
                                                    break;
                                                }
                                                if (Integer.parseInt(eNodeord) < Integer.parseInt(nowNodeord)) {
                                                    database.getReference("Notice_api").child(getKey).removeValue();
                                                    timer.cancel();
                                                    break;
                                                }

                                                if (noticeData.getbusRide().equals("0")) {
                                                    busData = getStaionBusData(noticeData.getCityCode(), noticeData.getRouteId(), noticeData.getSbusStopNodeId()).split("\n");
                                                    for (int i = 0; i < busData.length; i++) {
                                                        String[] busData_List = busData[i].split(" ");
                                                        // [0]:남은정류장수, [1]:남은시간, [2]:정류장명, [3]:버스번호
                                                        if (busData_List[0].equals("")) {
                                                            System.out.println("API서버 오류");
                                                        } else {
                                                            if (Integer.parseInt(busData_List[0]) + Integer.parseInt(nowNodeord) == Integer.parseInt(sNodeord)) {
                                                                if (busData_List[0].equals("1")) {
                                                                    Handler handler = new Handler(Looper.getMainLooper());
                                                                    handler.postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            Toast.makeText(MainActivity_api.this, "잠시 후 버스가 도착할 예정입니다. 탑승을 준비해주세요.", Toast.LENGTH_SHORT).show();
                                                                            Intent my_intent = new Intent(MainActivity_api.this, Alarm_Reciver_api.class);
                                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                                                pendingIntent = (PendingIntent.getBroadcast(MainActivity_api.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
                                                                            } else {
                                                                                pendingIntent = (PendingIntent.getBroadcast(MainActivity_api.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT));
                                                                            }
                                                                            if (Build.VERSION.SDK_INT >= 23) {
                                                                                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
                                                                            } else {
                                                                                alarmManager.set(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
                                                                            }
                                                                        }
                                                                    }, 0);
                                                                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                                                    vibrator.vibrate(new long[]{500, 500, 500, 500, 500, 500, 500, 500, 500, 500}, -1);
                                                                    database.getReference("Notice_api").child(getKey).child("busRide").setValue("1");
                                                                }
                                                                break;
                                                            }
                                                        }
                                                    }
                                                } else if (noticeData.getbusRide().equals("1")) {
                                                    busData = getStaionBusData(noticeData.getCityCode(), noticeData.getRouteId(), noticeData.getEbusStopNodeId()).split("\n");
                                                    for (int i = 0; i < busData.length; i++) {
                                                        String[] busData_List = busData[i].split(" ");
                                                        // [0]:남은정류장수, [1]:남은시간, [2]:정류장명, [3]:버스번호
                                                        if (busData_List[0].equals("")) {
                                                            System.out.println("API서버 오류");
                                                        } else {
                                                            if (Integer.parseInt(busData_List[0]) + Integer.parseInt(nowNodeord) == Integer.parseInt(eNodeord)) {
                                                                if (busData_List[0].equals("1")) {
                                                                    Handler handler = new Handler(Looper.getMainLooper());
                                                                    handler.postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            Toast.makeText(MainActivity_api.this, "잠시 후 목적지에 도착할 예정입니다. 하차를 준비해주세요.", Toast.LENGTH_SHORT).show();
                                                                            Intent my_intent = new Intent(MainActivity_api.this, Alarm_Reciver_api.class);
                                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                                                pendingIntent = (PendingIntent.getBroadcast(MainActivity_api.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
                                                                            } else {
                                                                                pendingIntent = (PendingIntent.getBroadcast(MainActivity_api.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT));
                                                                            }
                                                                            if (Build.VERSION.SDK_INT >= 23) {
                                                                                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
                                                                            } else {
                                                                                alarmManager.set(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
                                                                            }
                                                                        }
                                                                    }, 0);
                                                                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                                                    vibrator.vibrate(new long[]{500, 500, 500, 500, 500, 500, 500, 500, 500, 500}, -1);

                                                                    database.getReference("Notice_api").child(getKey).removeValue();
                                                                    button3.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            Toast.makeText(MainActivity_api.this, "회원님의 알림 내역이 없습니다.", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });

                                                                    Intent intent = new Intent(MainActivity_api.this, MainActivity_api.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                    timer.cancel();
                                                                }
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                                break;
                                            }
                                            j++;
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) { }
                                });
                            }
                        };
                        timer.schedule(timerTask, 0, 10*1000);
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        mDatabaseRef = database.getReference("Notice_api");
        mDatabaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Intent my_intent = new Intent(MainActivity_api.this, Alarm_Cancle.class);
                boolean noticeDelete = true;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (firebaseUser.getUid().equals(snapshot.child("Uid").getValue(String.class))) {
                        noticeDelete = false;
                        break;
                    }
                }
                if (noticeDelete) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        pendingIntent = (PendingIntent.getBroadcast(MainActivity_api.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
                    } else {
                        pendingIntent = (PendingIntent.getBroadcast(MainActivity_api.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT));
                    }
                    if (Build.VERSION.SDK_INT >= 23) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
                    }
                    button3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(MainActivity_api.this, "회원님의 알림 내역이 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    finish();
                    timer.cancel();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    final LocationListener gpsLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double altitude = location.getAltitude();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };
}
