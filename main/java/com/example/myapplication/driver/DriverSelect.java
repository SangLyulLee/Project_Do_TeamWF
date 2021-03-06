package com.example.myapplication.driver;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.map.BusTime;
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

public class DriverSelect extends AppCompatActivity {
    private ArrayList<BusTime> busTimeArray = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private String input_str;
    private Intent intent;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
    private SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("mm");
    private Date date = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_select);

        databaseReference = database.getReference("Driver");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("Uid").getValue(String.class).equals(firebaseUser.getUid())) {
                        AlertDialog.Builder dlg_start = new AlertDialog.Builder(DriverSelect.this);
                        dlg_start.setTitle("?????? ?????? ??????");
                        dlg_start.setMessage("?????? ?????? ?????? ????????? ????????????. ?????? ??????????????????????");
                        dlg_start.setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                intent = new Intent(DriverSelect.this, DriverMain.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        dlg_start.setNegativeButton("???", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                databaseReference.child(snapshot.getKey()).removeValue();
                            }
                        });
                        dlg_start.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                databaseReference.child(snapshot.getKey()).removeValue();
                            }
                        });
                        dlg_start.show();
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        ListView list = (ListView) findViewById(R.id.list_bus);
        DriverAdapter adapter = new DriverAdapter();
        list.setAdapter(adapter);

        EditText edit = (EditText) findViewById(R.id.edit_busnum);
        Button select_btn = (Button) findViewById(R.id.btn_select);
        select_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input_str = edit.getText().toString();

                databaseReference = database.getReference("BusTime").child(input_str);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        busTimeArray.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            busTimeArray.add(snapshot.getValue(BusTime.class));
                            adapter.addList(snapshot.getValue(BusTime.class));
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
                            AlertDialog.Builder dlg = new AlertDialog.Builder(DriverSelect.this);
                            dlg.setTitle("?????? ??????");
                            dlg.setMessage("?????? ?????? : " + input_str + "???\n" + "?????? ?????? : " + busTimeArray.get(position).getHours() + "??? " + busTimeArray.get(position).getMinutes() + "???\n???????????? ????????? ?????????????");
                            dlg.setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(DriverSelect.this, "?????????????????????.", Toast.LENGTH_SHORT).show();
                                }
                            });
                            dlg.setNegativeButton("???", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(DriverSelect.this, "?????????????????????.", Toast.LENGTH_SHORT).show();

                                    databaseReference = database.getReference("Driver");
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            int j = 0;
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                j++;
                                            }
                                            databaseReference.child(Integer.toString(j)).child("Uid").setValue(firebaseUser.getUid());
                                            databaseReference.child(Integer.toString(j)).child("BusNum").setValue(input_str);
                                            databaseReference.child(Integer.toString(j)).child("BusTime").setValue(Integer.toString(position+1));
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) { }
                                    });

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            intent = new Intent(DriverSelect.this, DriverMain.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }, 1000);
                                }
                            });
                            dlg.show();
                        }
                        else {
                            Toast.makeText(DriverSelect.this, "?????? ????????? ???????????????. ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}