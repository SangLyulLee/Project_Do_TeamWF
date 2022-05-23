package com.example.myapplication.notice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myapplication.ListAdapter;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.map.BusStop;
import com.example.myapplication.map.BusTime;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class EbusSet extends AppCompatActivity {
    private ArrayList<BusStop> arrayList = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference("BusStop");
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
    private ArrayList<Integer> alarm_minArray = new ArrayList<>();
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private ArrayList<BusTime> busTimeArray = new ArrayList<>();
    private ArrayList<Integer> busSeatArray = new ArrayList<>();
    int sTime_m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ebus_set);

        int busNum = getIntent().getIntExtra("busNum", 0);
        int s_pos = getIntent().getIntExtra("s_pos", 0);
        int sR_pos = getIntent().getIntExtra("sR_pos", 0);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        final Calendar calendar = Calendar.getInstance();
        final Intent my_intent = new Intent(EbusSet.this, Alarm_Reciver.class);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BusStop busStop = snapshot.getValue(BusStop.class);
                    arrayList.add(busStop);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        databaseReference = database.getReference("BusSeat").child(Integer.toString(busNum)).child(Integer.toString(sR_pos+1));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                busSeatArray.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    busSeatArray.add(snapshot.getValue(Integer.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        ListView list = (ListView) findViewById(R.id.list_ebusSet);
        ListAdapter listAdapter = new ListAdapter();
        list.setAdapter(listAdapter);

        final ArrayList<String> str = new ArrayList<String>();
        final ArrayList<String> str1_name = new ArrayList<String>();

        databaseReference = database.getReference("BusRoute").child("1").child("route");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                str.clear();
                str1_name.clear();
                String str_a;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (Integer.toString(busNum).equals(snapshot.getKey())) {
                        for (DataSnapshot snapshot2 : dataSnapshot.child(Integer.toString(busNum)).getChildren()) {
                            str_a = snapshot2.getValue(String.class);
                            str.add(str_a);
                        }
                    }
                }
                for (int i=0; i<str.size(); i++) {
                    for (int j=0; j<arrayList.size(); j++) {
                        if (str.get(i).equals(arrayList.get(j).getBusStopNum())) {
                            str1_name.add(arrayList.get(j).getBusStopName());
                        }
                    }
                }
                listAdapter.list_clear();
                for (int i=0; i<str1_name.size(); i++) {
                    if (i == 0) {
                        if (busSeatArray.get(i) == 0) {
                            listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.route1), str1_name.get(i), ContextCompat.getDrawable(getApplicationContext(), R.drawable.non));
                        }
                        else {
                            listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.route1), str1_name.get(i), ContextCompat.getDrawable(getApplicationContext(), R.drawable.seat));
                        }
                    }
                    else if (i == str1_name.size()-1) {
                        if (busSeatArray.get(i) == 0) {
                            listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.route3), str1_name.get(i), ContextCompat.getDrawable(getApplicationContext(), R.drawable.non));
                        }
                        else {
                            listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.route3), str1_name.get(i), ContextCompat.getDrawable(getApplicationContext(), R.drawable.seat));
                        }
                    }
                    else {
                        if (busSeatArray.get(i) == 0) {
                            listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.route2), str1_name.get(i), ContextCompat.getDrawable(getApplicationContext(), R.drawable.non));
                        }
                        else {
                            listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.route2), str1_name.get(i), ContextCompat.getDrawable(getApplicationContext(), R.drawable.seat));
                        }
                    }
                }
                listAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int position = i;
                boolean seat_bool = false;
                for (int seat_pos=s_pos; seat_pos<=position; seat_pos++) {
                    if (busSeatArray.get(seat_pos) == 1) {
                        seat_bool = true;
                        break;
                    }
                }

                if (s_pos >= position) {
                    Toast.makeText(EbusSet.this, "출발지 이후의 정류장을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if (seat_bool) {
                    Toast.makeText(EbusSet.this, "자리가 없습니다. 다시 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    AlertDialog.Builder dlg = new AlertDialog.Builder(EbusSet.this);
                    dlg.setTitle("버스 확인");

                    dlg.setMessage("버스 번호 : " + busNum + "번\n" + "하차 장소 : " + str1_name.get(i) + "\n입력하신 정보가 맞습니까?");
                    dlg.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(EbusSet.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dlg.setNegativeButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(EbusSet.this, "알림 설정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            databaseReference = database.getReference().child("Notice");
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    int j = 1;
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        if (j != Integer.parseInt(snapshot.getKey()))
                                            break;
                                        j++;
                                    }

                                    // 알림 알람 설정
                                    databaseReference = database.getReference("BusRoute").child("1").child("timer");
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                            alarm_minArray.clear();
                                            int alarm_min = 0;
                                            int i=0;
                                            String str_busNum;
                                            for (DataSnapshot snapshot : dataSnapshot1.child(Integer.toString(busNum)).getChildren()) {
                                                str_busNum = snapshot.getValue(String.class);

                                                alarm_minArray.add(Integer.parseInt(str_busNum));
                                                if (i == s_pos)
                                                    sTime_m = Integer.parseInt(str_busNum)/60;
                                                i++;
                                            }
                                            if (s_pos == 0) {
                                                alarm_min = 3;
                                            }
                                            else {
                                                alarm_min = (alarm_minArray.get(s_pos)/60) - (alarm_minArray.get(s_pos-1)/60);
                                            }

                                            databaseReference = database.getReference("BusTime").child(Integer.toString(busNum));
                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    busTimeArray.clear();
                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                        BusTime busTime = snapshot.getValue(BusTime.class);
                                                        if (busTime.getMinutes()+sTime_m < 60)
                                                            busTime.setMinutes(busTime.getMinutes()+sTime_m);
                                                        else {
                                                            busTime.setMinutes((busTime.getMinutes()+sTime_m)%60);
                                                            busTime.setHours(busTime.getHours()+1);
                                                        }
                                                        busTimeArray.add(busTime);
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                }
                                            });

                                            calendar.setTimeInMillis(System.currentTimeMillis());
                                            if (busTimeArray.size() != 0) {
                                                if (busTimeArray.get(sR_pos).getMinutes() >= alarm_min) {
                                                    calendar.set(Calendar.HOUR_OF_DAY, busTimeArray.get(sR_pos).getHours());
                                                    calendar.set(Calendar.MINUTE, busTimeArray.get(sR_pos).getMinutes() - alarm_min);
                                                }
                                                else {
                                                    calendar.set(Calendar.HOUR_OF_DAY, busTimeArray.get(sR_pos).getHours() - 1);
                                                    calendar.set(Calendar.MINUTE, busTimeArray.get(sR_pos).getMinutes() + 60 - alarm_min);
                                                }
                                            }
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                pendingIntent = PendingIntent.getBroadcast(EbusSet.this, 0, my_intent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                                            }
                                            else {
                                                pendingIntent = PendingIntent.getBroadcast(EbusSet.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                            }

                                            /*
                                            my_intent.putExtra("alarm_min", alarm_min);
                                            my_intent.putExtra("sTime_m", sTime_m);
                                            my_intent.putExtra("busNum", busNum);
                                            my_intent.putExtra("sR_pos", sR_pos);*/

                                            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                                            /*
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                                                }
                                                else {
                                                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                                                }*/
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) { }
                                    });

                                    databaseReference = database.getReference("BusSeat").child(Integer.toString(busNum)).child(Integer.toString(sR_pos+1));
                                    for (int seat_pos=s_pos; seat_pos<=position; seat_pos++) {
                                        databaseReference.child("route"+Integer.toString(seat_pos+1)).setValue(1);
                                    }

                                    databaseReference = database.getReference().child("Notice");
                                    databaseReference.child(Integer.toString(j)).child("Uid").setValue(firebaseUser.getUid());
                                    databaseReference.child(Integer.toString(j)).child("BusNum").setValue(Integer.toString(busNum));
                                    databaseReference.child(Integer.toString(j)).child("BusTime").setValue(Integer.toString(sR_pos+1));
                                    databaseReference.child(Integer.toString(j)).child("SbusStopNum").setValue(str.get(s_pos));
                                    databaseReference.child(Integer.toString(j)).child("EbusStopNum").setValue(str.get(position));
                                    databaseReference = database.getReference("member").child("UserAccount");
                                    final int pos = j;
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            arrayList.clear();
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                if (firebaseUser.getUid().equals(snapshot.getKey())) {
                                                    database.getReference("Notice").child(Integer.toString(pos)).child("u_type").setValue(snapshot.child("u_type").getValue(int.class));
                                                    Intent intent = new Intent(EbusSet.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                                    });
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) { }
                            });
                        }
                    });
                    dlg.show();
                }
            }
        });
    }
}
