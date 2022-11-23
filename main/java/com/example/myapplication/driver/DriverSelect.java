package com.example.myapplication.driver;

import static java.lang.Math.abs;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.api_driver.DriverAdapter_api;
import com.example.myapplication.api_driver.DriverMain_Api;
import com.example.myapplication.api_driver.DriverSelect_api;
import com.example.myapplication.map.BusTime;
import com.example.myapplication.api_ver.get_api;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
    Boolean ApiUse = false, api_error = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_select);
        ListView list = (ListView) findViewById(R.id.list_bus);
        EditText edit = (EditText) findViewById(R.id.edit_busnum);
        Button select_btn = (Button) findViewById(R.id.btn_select);
        Button api_button = (Button) findViewById(R.id.api_button);

        databaseReference = database.getReference("Driver");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("Uid").getValue(String.class).equals(firebaseUser.getUid())) {
                        AlertDialog.Builder dlg_start = new AlertDialog.Builder(DriverSelect.this);
                        dlg_start.setTitle("버스 등록 확인");
                        dlg_start.setMessage("이미 운행 중인 버스가 있습니다. 다시 고르시겠습니까?");
                        dlg_start.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                intent = new Intent(DriverSelect.this, DriverMain.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        dlg_start.setNegativeButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                System.out.println("snapshot.getKey() : " + snapshot.getKey());
                                databaseReference.child(snapshot.getKey()).removeValue();
                            }
                        });
                        dlg_start.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                intent = new Intent(DriverSelect.this, DriverMain.class);
                                startActivity(intent);
                                finish();
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

        // 기사 - API 버전 등록 검사
        databaseReference = database.getReference("Driver_api");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("Uid").getValue(String.class).equals(firebaseUser.getUid())) {
                        AlertDialog.Builder dlg_start = new AlertDialog.Builder(DriverSelect.this);
                        dlg_start.setTitle("버스 등록 확인");
                        dlg_start.setMessage("이미 운행 중인 버스가 있습니다. 다시 고르시겠습니까?");
                        dlg_start.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                intent = new Intent(DriverSelect.this, DriverMain_Api.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        dlg_start.setNegativeButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                databaseReference.child(snapshot.getKey()).removeValue();
                                ApiUse = true;
                            }
                        });
                        dlg_start.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                intent = new Intent(DriverSelect.this, DriverMain_Api.class);
                                startActivity(intent);
                                finish();
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

        /////////////////////

        customMode(list, edit, select_btn);

        api_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (api_button.getText().equals("API MODE")) {
                    api_button.setText("CUSTOM");
                    // 기사 - API 버전
                    TextView textView = (TextView) findViewById(R.id.textView9);
                    textView.setText("운행할 지역을 선택해주세요");
                    EditText editText = (EditText) findViewById(R.id.edit_busnum);
                    editText.setHint("지역을 입력하세요");

                    String[] api_split = get_api.getCitycode().split("\n");
                    String[] citycodeArr = new String[api_split.length], citynameArr = new String[api_split.length], citynameArr_copy = new String[api_split.length];
                    for (int i = 0; i < api_split.length; i++) {
                        String[] api_split2 = api_split[i].split(" ");
                        if (api_split2.length == 1) {
                            Toast.makeText(DriverSelect.this, "API 서버 오류.", Toast.LENGTH_SHORT).show();
                            api_error = true;
                        }
                        else {
                            citycodeArr[i] = api_split2[0];
                            citynameArr[i] = api_split2[1];
                        }
                    }
                    if (!api_error) {
                        System.arraycopy(citynameArr, 0, citynameArr_copy, 0, api_split.length);

                        Arrays.sort(citynameArr_copy);
                        DriverAdapter_api adapter_api = new DriverAdapter_api();
                        list.setAdapter(adapter_api);
                        for (int i = 0; i < citynameArr_copy.length; i++) {
                            adapter_api.addList(citynameArr_copy[i]);
                        }
                        adapter_api.notifyDataSetChanged();

                        select_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                input_str = edit.getText().toString();
                                ArrayList<String> sameCitynameArr = new ArrayList<>();
                                for (int i = 0; i < citynameArr_copy.length; i++) {
                                    String cityname = adapter_api.getItem(i);
                                    if (cityname.toLowerCase().contains(input_str.toLowerCase())) {
                                        sameCitynameArr.add(cityname);
                                    }
                                }
                                adapter_api.clear();
                                for (int i = 0; i < sameCitynameArr.size(); i++) {
                                    adapter_api.addList(sameCitynameArr.get(i));
                                }
                                adapter_api.notifyDataSetChanged();
                            }
                        });

                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                                Intent intent = new Intent(DriverSelect.this, DriverSelect_api.class);
                                for (int i = 0; i < citynameArr.length; i++) {
                                    if (citynameArr[i].equals(adapter_api.getItem(pos))) {
                                        System.out.println("citycode : " + citycodeArr[i] + ", cityname : " + citynameArr[i]);
                                        intent.putExtra("citycode", citycodeArr[i]);
                                        break;
                                    }
                                }
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }
                else if (api_button.getText().equals("CUSTOM")) {
                    api_button.setText("API MODE");
                    customMode(list, edit, select_btn);
                }
            }
        });
    }

    final void customMode(ListView list, EditText edit, Button select_btn) {
        DriverAdapter adapter = new DriverAdapter();
        list.setAdapter(adapter);

        select_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textView = (TextView) findViewById(R.id.textView9);
                textView.setText("운행할 버스를 선택해주세요");
                EditText editText = (EditText) findViewById(R.id.edit_busnum);
                editText.setHint("버스 번호를 입력하세요");
                input_str = edit.getText().toString();
                if (input_str.equals("")) {
                    Toast.makeText(DriverSelect.this, "버스 번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    adapter.list_clear();
                    adapter.notifyDataSetChanged();
                }
                else {

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
                            int n_time = (Integer.parseInt(time_hours) * 60) + Integer.parseInt(time_minutes);
                            int s_time = (busTimeArray.get(position).getHours() * 60) + busTimeArray.get(position).getMinutes();

                            if (n_time < s_time) {
                                AlertDialog.Builder dlg = new AlertDialog.Builder(DriverSelect.this);
                                dlg.setTitle("버스 확인");
                                dlg.setMessage("버스 번호 : " + input_str + "번\n" + "출발 시간 : " + busTimeArray.get(position).getHours() + "시 " + busTimeArray.get(position).getMinutes() + "분\n입력하신 정보가 맞습니까?");
                                dlg.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(DriverSelect.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dlg.setNegativeButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(DriverSelect.this, "확인하였습니다.", Toast.LENGTH_SHORT).show();

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
                                                databaseReference.child(Integer.toString(j)).child("BusTime").setValue(Integer.toString(position + 1));
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
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
                            } else {
                                Toast.makeText(DriverSelect.this, "운행 시간이 지났습니다. 다시 선택해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}