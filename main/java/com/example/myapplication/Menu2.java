package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myapplication.map.BusStop;
import com.example.myapplication.map.BusTime;
import com.example.myapplication.map.RouteMapActivity;
import com.example.myapplication.notice.StimeSet;
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

public class Menu2 extends AppCompatActivity {
    private ArrayList<BusStop> arrayList = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference("BusStop");
    private String str_a;
    private String str_b;
    private ArrayList<BusTime> busTimeArray = new ArrayList<>();
    private int st_time;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
    private SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("mm");
    private Date date = new Date();
    private int time_int;
    private String input_str;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu2);

        final ArrayList<String> str = new ArrayList<String>();
        final ArrayList<String> str1_name = new ArrayList<String>();
        ListView list = (ListView) findViewById(R.id.listView2_menu2);
        ListAdapter listAdapter = new ListAdapter();
        list.setAdapter(listAdapter);


        Button imgButton = (Button) findViewById(R.id.refreshBtn);

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

        Button routeMap_btn = (Button) findViewById(R.id.routeMap_btn);
        routeMap_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Menu2.this, "노선 검색 후 이용해주세요", Toast.LENGTH_SHORT).show();
            }
        });

        ArrayList<String> str_route = new ArrayList<String>();
        ArrayList<Integer> timer_arr = new ArrayList<Integer>();
        EditText edit2 = (EditText) findViewById(R.id.editText2);
        Button route_btn = (Button) findViewById(R.id.route_btn);

        route_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input_str = edit2.getText().toString();

                /* 버스 노선, 이미지 */
                databaseReference = database.getReference("BusRoute").child("1").child("route");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        str.clear();
                        str1_name.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (input_str.equals(snapshot.getKey())) {
                                for (DataSnapshot snapshot2 : dataSnapshot.child(input_str).getChildren()) {
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
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

                /* 시간 정보 */
                databaseReference = database.getReference("BusRoute").child("1").child("timer");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        str.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (input_str.equals(snapshot.getKey())) {
                                for (DataSnapshot snapshot2 : dataSnapshot.child(input_str).getChildren()) {
                                    str_b = snapshot2.getValue(String.class);
                                    str.add(str_b);
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
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

                databaseReference = database.getReference("BusTime").child(input_str);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        busTimeArray.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (input_str.equals(snapshot.getKey())) {
                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                    busTimeArray.add(snapshot1.getValue(BusTime.class));
                                }
                            }
                        }

                        String time_hours = simpleDateFormat.format(date);
                        String time_minutes = simpleDateFormat2.format(date);
                        time_int = (Integer.parseInt(time_hours)*60) + Integer.parseInt(time_minutes);

                        for (int i=0; i<busTimeArray.size(); i++) {
                            st_time = (busTimeArray.get(i).getHours()*60) + busTimeArray.get(i).getMinutes();
                            if (time_int - st_time >= 0) {
                                for (int j = 0; j < str.size(); j++) {
                                    if ((time_int - st_time < (Integer.parseInt(str.get(j))/60)) && (time_int - st_time >= (Integer.parseInt(str.get(j-1))/60))) {
                                        if (j == 0) {
                                            listAdapter.setListImg(0, ContextCompat.getDrawable(getApplicationContext(), R.drawable.route1_1));
                                            break;
                                        }
                                        else {
                                            listAdapter.setListImg(j - 1, ContextCompat.getDrawable(getApplicationContext(), R.drawable.route2_1));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        listAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        int position = i;
                        if (position == str1_name.size()-1) {
                            Toast.makeText(Menu2.this, "마지막 정류장은 선택할 수 없습니다.\n 다시 선택해주세요.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            AlertDialog.Builder dlg = new AlertDialog.Builder(Menu2.this);
                            dlg.setTitle("버스 확인");
                            dlg.setMessage("버스 번호 : " + input_str + "번\n" + "탑승 장소 : " + str1_name.get(position) + "\n알림 설정하려는 버스 정보가 맞습니까?");
                            dlg.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(Menu2.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                            dlg.setNegativeButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


                                    databaseReference = database.getReference().child("Notice");
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            boolean notice_c = true;
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                if (firebaseUser.getUid().equals(snapshot.child("Uid").getValue(String.class))) {
                                                    Toast.makeText(Menu2.this, "이미 알림이 있습니다. 알림 변경을 하시거나 취소해주세요.", Toast.LENGTH_SHORT).show();
                                                    notice_c = false;
                                                }
                                            }
                                            if (notice_c) {
                                                Intent intent = new Intent(Menu2.this, StimeSet.class);
                                                intent.putExtra("busNum", Integer.parseInt(input_str));
                                                intent.putExtra("s_pos", position);
                                                startActivity(intent);
                                                finish();
                                            }
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
                routeMap_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intentMap = new Intent(Menu2.this, RouteMapActivity.class);
                        intentMap.putExtra("busNum", Integer.parseInt(input_str));
                        intentMap.putExtra("api_bool", "0");
                        startActivity(intentMap);
                    }
                });
            }
        });

        /* 새로고침 버튼 */
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input_str = edit2.getText().toString();
                Date date2 = new Date();
                String time_hours = simpleDateFormat.format(date2);
                String time_minutes = simpleDateFormat2.format(date2);
                time_int = (Integer.parseInt(time_hours)*60) + Integer.parseInt(time_minutes);

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

                for (int i=0; i<busTimeArray.size(); i++) {
                    st_time = (busTimeArray.get(i).getHours()*60) + busTimeArray.get(i).getMinutes();
                    if (time_int - st_time < 0) { continue; }
                    else {
                        for (int j = 1; j < str.size(); j++) {
                            if (time_int - st_time < (Integer.parseInt(str.get(j))/60) && time_int - st_time >= (Integer.parseInt(str.get(j-1))/60)) {
                                if (j == 1) {
                                    listAdapter.setListImg(j - 1, ContextCompat.getDrawable(getApplicationContext(), R.drawable.route1_1));
                                    break;
                                }
                                else {
                                    listAdapter.setListImg(j - 1, ContextCompat.getDrawable(getApplicationContext(), R.drawable.route2_1));
                                    break;
                                }
                            }
                        }
                    }
                }
                listAdapter.notifyDataSetChanged();
            }
        });

        String searchBusNum = "null";
        searchBusNum = getIntent().getStringExtra("searchBusNum");
        if (!"null".equals(searchBusNum)) {
            edit2.setText(searchBusNum);
            input_str = edit2.getText().toString();
            databaseReference = database.getReference("BusRoute").child("1").child("route");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    str.clear();
                    str1_name.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (input_str.equals(snapshot.getKey())) {
                            for (DataSnapshot snapshot2 : dataSnapshot.child(input_str).getChildren()) {
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
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });

            /* 시간 정보 */
            databaseReference = database.getReference("BusRoute").child("1").child("timer");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    str.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (input_str.equals(snapshot.getKey())) {
                            for (DataSnapshot snapshot2 : dataSnapshot.child(input_str).getChildren()) {
                                str_b = snapshot2.getValue(String.class);
                                str.add(str_b);
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
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });

            databaseReference = database.getReference("BusTime").child(input_str);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    busTimeArray.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (input_str.equals(snapshot.getKey())) {
                            for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                busTimeArray.add(snapshot1.getValue(BusTime.class));
                            }
                        }
                    }

                    String time_hours = simpleDateFormat.format(date);
                    String time_minutes = simpleDateFormat2.format(date);
                    time_int = (Integer.parseInt(time_hours)*60) + Integer.parseInt(time_minutes);

                    for (int i=0; i<busTimeArray.size(); i++) {
                        st_time = (busTimeArray.get(i).getHours()*60) + busTimeArray.get(i).getMinutes();
                        if (time_int - st_time >= 0) {
                            for (int j = 0; j < str.size(); j++) {
                                if ((time_int - st_time < (Integer.parseInt(str.get(j))/60)) && (time_int - st_time >= (Integer.parseInt(str.get(j-1))/60))) {
                                    if (j == 0) {
                                        listAdapter.setListImg(0, ContextCompat.getDrawable(getApplicationContext(), R.drawable.route1_1));
                                        break;
                                    }
                                    else {
                                        listAdapter.setListImg(j - 1, ContextCompat.getDrawable(getApplicationContext(), R.drawable.route2_1));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    listAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    int position = i;
                    if (position == str1_name.size()-1) {
                        Toast.makeText(Menu2.this, "마지막 정류장은 선택할 수 없습니다.\n 다시 선택해주세요.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        AlertDialog.Builder dlg = new AlertDialog.Builder(Menu2.this);
                        dlg.setTitle("버스 확인");
                        dlg.setMessage("버스 번호 : " + input_str + "번\n" + "탑승 장소 : " + str1_name.get(position) + "\n알림 설정하려는 버스 정보가 맞습니까?");
                        dlg.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(Menu2.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dlg.setNegativeButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                                databaseReference = database.getReference().child("Notice");
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        boolean notice_c = true;
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            if (firebaseUser.getUid().equals(snapshot.child("Uid").getValue(String.class))) {
                                                Toast.makeText(Menu2.this, "이미 알림이 있습니다. 알림 변경을 하시거나 취소해주세요.", Toast.LENGTH_SHORT).show();
                                                notice_c = false;
                                            }
                                        }
                                        if (notice_c) {
                                            Intent intent = new Intent(Menu2.this, StimeSet.class);
                                            intent.putExtra("busNum", Integer.parseInt(input_str));
                                            intent.putExtra("s_pos", position);
                                            startActivity(intent);
                                            finish();
                                        }
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
            routeMap_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentMap = new Intent(Menu2.this, RouteMapActivity.class);
                    intentMap.putExtra("busNum", Integer.parseInt(input_str));
                    startActivity(intentMap);
                }
            });
        }
    }
}
