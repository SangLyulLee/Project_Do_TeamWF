package com.example.myapplication.notice;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.driver.DriverAdapter;
import com.example.myapplication.map.BusTime;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StimeSet extends AppCompatActivity {
    private ArrayList<BusTime> busTimeArray = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private int sTime_m;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
    private SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("mm");
    private Date date = new Date();
    private ArrayList<String> routeTimerArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stime_set);
        int busNum = getIntent().getIntExtra("busNum", 0);
        int s_pos = getIntent().getIntExtra("s_pos", 0);

        ListView list = (ListView) findViewById(R.id.list_sTimeSet);
        DriverAdapter adapter = new DriverAdapter();
        list.setAdapter(adapter);


        databaseReference = database.getReference("BusRoute").child("1").child("timer").child(Integer.toString(busNum));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                routeTimerArray.clear();
                int i=0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (i == s_pos)
                        sTime_m = Integer.parseInt(snapshot.getValue(String.class))/60;
                    i++;
                    routeTimerArray.add(snapshot.getValue(String.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

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
                    adapter.addList(busTime);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int position = i;

                String time_hours = simpleDateFormat.format(date);
                String time_minutes = simpleDateFormat2.format(date);
                int n_time = (Integer.parseInt(time_hours)*60) + Integer.parseInt(time_minutes);
                int s_time = (busTimeArray.get(position).getHours()*60) + busTimeArray.get(position).getMinutes();

                if (n_time < s_time) {
                    if (s_pos == 0) {
                        if (n_time > (s_time - 3)) {
                            Toast.makeText(StimeSet.this, "?????? ????????? ?????? ?????? ?????? 3??? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            AlertDialog.Builder dlg = new AlertDialog.Builder(StimeSet.this);
                            dlg.setTitle("?????? ??????");

                            dlg.setMessage("?????? ?????? : " + busNum + "???\n" + "?????? ?????? : " + busTimeArray.get(position).getHours() + "??? " + busTimeArray.get(position).getMinutes() + "???\n???????????? ????????? ?????????????");
                            dlg.setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(StimeSet.this, "?????????????????????.", Toast.LENGTH_SHORT).show();
                                }
                            });
                            dlg.setNegativeButton("???", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(StimeSet.this, "?????????????????????.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(StimeSet.this, EbusSet.class);
                                    intent.putExtra("busNum", busNum);
                                    intent.putExtra("s_pos", s_pos);
                                    intent.putExtra("sR_pos", position);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            dlg.show();
                        }
                    }
                    else {
                        if (n_time >= s_time - ((Integer.parseInt(routeTimerArray.get(s_pos))/60)-(Integer.parseInt(routeTimerArray.get(s_pos-1))/60))) {
                            Toast.makeText(StimeSet.this, "??? ???????????? ????????? ????????? ?????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            AlertDialog.Builder dlg = new AlertDialog.Builder(StimeSet.this);
                            dlg.setTitle("?????? ??????");

                            dlg.setMessage("?????? ?????? : " + busNum + "???\n" + "?????? ?????? : " + busTimeArray.get(position).getHours() + "??? " + busTimeArray.get(position).getMinutes() + "???\n???????????? ????????? ?????????????");
                            dlg.setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(StimeSet.this, "?????????????????????.", Toast.LENGTH_SHORT).show();
                                }
                            });
                            dlg.setNegativeButton("???", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(StimeSet.this, "?????????????????????.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(StimeSet.this, EbusSet.class);
                                    intent.putExtra("busNum", busNum);
                                    intent.putExtra("s_pos", s_pos);
                                    intent.putExtra("sR_pos", position);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            dlg.show();
                        }
                    }

                }
                else {
                    Toast.makeText(StimeSet.this, "????????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
