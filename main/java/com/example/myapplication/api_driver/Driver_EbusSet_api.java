package com.example.myapplication.api_driver;

import android.content.DialogInterface;
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
import com.example.myapplication.R;
import com.example.myapplication.api_notice.EbusSet_api;
import com.example.myapplication.api_ver.get_api;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Driver_EbusSet_api extends AppCompatActivity {
    Driver_Api driver_api = new Driver_Api();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ebus_set);

        ListView list = (ListView) findViewById(R.id.driver_elist);
        ListAdapter listAdapter = new ListAdapter();
        list.setAdapter(listAdapter);

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

                        String now_nodeId = null, now_nodeOrd = "-1";
                        String[] busServiceData = get_api.getBusServiceData(driver_api.getCityCode(), driver_api.getRouteId(), "1").split("\n");
                        for (int i=0; i<busServiceData.length; i++) {
                            String[] busData_List = busServiceData[i].split(" ");
                            if (busData_List[2].equals(driver_api.getVehicleNo())) {
                                now_nodeId = busData_List[0];
                                now_nodeOrd = busData_List[1];
                            }
                        }
                        if (!now_nodeOrd.equals("-1")) {
                            String[] api_split = get_api.getBusRoute(driver_api.getCityCode(), driver_api.getRouteId(), "1").split("\n");
                            listAdapter.list_clear();
                            for (int i = Integer.parseInt(now_nodeOrd) - 1; i < api_split.length; i++) {
                                String[] api_split2 = api_split[i].split(" ");
                                if (api_split2[6].equals("0"))
                                    listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.updowncd0), Integer.toString(i + 1) + ". " + api_split2[3], ContextCompat.getDrawable(getApplicationContext(), R.drawable.non));
                                else if (api_split2[6].equals("1"))
                                    listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.updowncd1), Integer.toString(i + 1) + ". " + api_split2[3], ContextCompat.getDrawable(getApplicationContext(), R.drawable.non));
                                if (api_split2[2].equals(now_nodeId)) {
                                    listAdapter.list_clear();
                                }
                            }
                        }

                        listAdapter.notifyDataSetChanged();
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                // 기존 알림과 겹치는지 확인 후 기존 알림 삭제, 삭제된 사용자에게 알람 발송


                                AlertDialog.Builder dlg = new AlertDialog.Builder(Driver_EbusSet_api.this);
                                dlg.setTitle("알림 신청 확인");

                                dlg.setMessage("도착 정류장 : " + "" + "\n입력하신 정보가 맞습니까?");
                                dlg.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(Driver_EbusSet_api.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dlg.setNegativeButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                            }
                        });
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });


    }
}
