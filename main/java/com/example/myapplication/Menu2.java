package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myapplication.map.BusStop;
import com.example.myapplication.map.BusTime;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu2);

        final ArrayList<String> str = new ArrayList<String>();
        final ArrayList<String> str1_name = new ArrayList<String>();
        ListView list2 = (ListView) findViewById(R.id.listView2_menu2);
        ListAdapter listAdapter = new ListAdapter();
        list2.setAdapter(listAdapter);


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
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
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
                                listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.route1), str1_name.get(i));
                            }
                            else if (i == str1_name.size()-1) {
                                listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.route3), str1_name.get(i));
                            }
                            else {
                                listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.route2), str1_name.get(i));
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
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

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

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
                        listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.route1), str1_name.get(i));
                    }
                    else if (i == str1_name.size()-1) {
                        listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.route3), str1_name.get(i));
                    }
                    else {
                        listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.route2), str1_name.get(i));
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
    }
}
