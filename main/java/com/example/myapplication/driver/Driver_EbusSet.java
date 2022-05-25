package com.example.myapplication.driver;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myapplication.ListAdapter;
import com.example.myapplication.R;
import com.example.myapplication.map.BusStop;
import com.example.myapplication.map.BusTime;
import com.example.myapplication.notice.Notice;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Driver_EbusSet extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference("BusStop");
    private ArrayList<BusStop> arrayList = new ArrayList<>();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
    private SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("mm");
    private ArrayList<String> timerArray = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_ebusset);


        ListView list = (ListView) findViewById(R.id.driver_elist);
        ListAdapter listAdapter = new ListAdapter();
        list.setAdapter(listAdapter);

        String busNum = getIntent().getStringExtra("busNum");
        String busTime = getIntent().getStringExtra("busTime");
        final ArrayList<String> str = new ArrayList<String>();
        final ArrayList<String> str1_name = new ArrayList<String>();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 파이어베이스 DB의 데이터를 받아옴
                arrayList.clear();  // 기존 배열리스트 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {  // 반복문으로 데이터 리스트 추출
                    BusStop busStop = snapshot.getValue(BusStop.class);     // 만들어둔 BusStop 객체에 데이터를 담음
                    arrayList.add(busStop);     // 담은 데이터를 배열 리스트에 추가
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        databaseReference = database.getReference("BusRoute").child("1").child("timer").child(busNum);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    timerArray.add(dataSnapshot.getValue(String.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        databaseReference = database.getReference("BusRoute").child("1").child("route");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                str.clear();
                str1_name.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (busNum.equals(snapshot.getKey())) {
                        for (DataSnapshot snapshot2 : dataSnapshot.child(busNum).getChildren()) {
                            str.add(snapshot2.getValue(String.class));
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
                        listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.route1), str1_name.get(i), ContextCompat.getDrawable(getApplicationContext(), R.drawable.non));
                    }
                    else if (i == str1_name.size()-1) {
                        listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.route3), str1_name.get(i), ContextCompat.getDrawable(getApplicationContext(), R.drawable.non));
                    }
                    else {
                        listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.route2), str1_name.get(i), ContextCompat.getDrawable(getApplicationContext(), R.drawable.non));
                    }
                }
                listAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Date date = new Date();
                String time_hours = simpleDateFormat.format(date);
                String time_minutes = simpleDateFormat2.format(date);
                int position = i;

                databaseReference = database.getReference("BusTime").child(busNum);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int bTime_h = 0, bTime_m = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.getKey().equals(busTime)) {
                                BusTime bTime = snapshot.getValue(BusTime.class);
                                bTime_h = bTime.getHours();
                                bTime_m = bTime.getMinutes();
                                break;
                            }
                        }
                        int i1 = (Integer.parseInt(time_hours) * 60) + Integer.parseInt(time_minutes);

                        if (((bTime_h*60) + (bTime_m) + Integer.parseInt(timerArray.get(position))) <= i1) {
                            Toast.makeText(Driver_EbusSet.this, "이미 해당 정류장을 지났습니다.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            AlertDialog.Builder dlg = new AlertDialog.Builder(Driver_EbusSet.this);
                            dlg.setTitle("장애인 탑승 확인");

                            dlg.setMessage("도착 지점 : "+str1_name.get(position)+"\n입력하신 정보가 맞습니까?");
                            dlg.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(Driver_EbusSet.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                            int bTime = (bTime_h*60) + (bTime_m);
                            dlg.setNegativeButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    for (int j=0; j<timerArray.size()-1; j++) {
                                        if ((bTime + Integer.parseInt(timerArray.get(j))) <= i1
                                        && (bTime + Integer.parseInt(timerArray.get(j+1))) > i1) {
                                            for (int sRoute_pos = j; sRoute_pos<position; sRoute_pos++) {
                                                databaseReference = database.getReference("Notice");
                                                int finalSRoute_pos = sRoute_pos;
                                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                            Notice notice = snapshot.getValue(Notice.class);

                                                            if (notice.getBusNum().equals(busNum) && notice.getBusTime().equals(busTime)) {
                                                                if (str.get(finalSRoute_pos).equals(snapshot.child("SbusStopNum").getValue(String.class))
                                                                        || str.get(finalSRoute_pos).equals(snapshot.child("EbusStopNum").getValue(String.class))) {

                                                                    databaseReference.child(snapshot.getKey()).removeValue();
                                                                }
                                                            }
                                                        }
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) { }
                                                });
                                            }
                                            break;
                                        }
                                    }
                                    Toast.makeText(Driver_EbusSet.this, "확인하였습니다.", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                            dlg.show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
        });
    }
}
