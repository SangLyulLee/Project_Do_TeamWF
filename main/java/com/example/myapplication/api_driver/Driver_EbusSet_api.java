package com.example.myapplication.api_driver;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.myapplication.api_notice.NoticeApi;
import com.example.myapplication.api_ver.MainActivity_api;
import com.example.myapplication.api_ver.get_api;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Driver_EbusSet_api extends AppCompatActivity {
    Driver_Api driver_api = new Driver_Api();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
    private DatabaseReference mDatabaseRef;
    int s_pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ebus_set);

        ListView list = (ListView) findViewById(R.id.list_ebusSet);
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
                                break;
                            }
                        }
                        ArrayList<String> nodeIdList = new ArrayList<>();
                        if (!now_nodeOrd.equals("-1")) {
                            String[] api_split = get_api.getBusRoute(driver_api.getCityCode(), driver_api.getRouteId(), "1").split("\n");
                            listAdapter.list_clear();
                            for (int i = Integer.parseInt(now_nodeOrd) - 1; i < api_split.length; i++) {
                                String[] api_split2 = api_split[i].split(" ");
                                if (api_split2.length == 7) {
                                    if (api_split2[6].equals("0")) {
                                        listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.updowncd0), Integer.toString(i + 1) + ". " + api_split2[3], ContextCompat.getDrawable(getApplicationContext(), R.drawable.non));
                                        nodeIdList.add(api_split2[2]);
                                    } else if (api_split2[6].equals("1")) {
                                        listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.updowncd1), Integer.toString(i + 1) + ". " + api_split2[3], ContextCompat.getDrawable(getApplicationContext(), R.drawable.non));
                                        nodeIdList.add(api_split2[2]);
                                    }
                                }
                                else {
                                    listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.updowncd0), Integer.toString(i + 1) + ". " + api_split2[3], ContextCompat.getDrawable(getApplicationContext(), R.drawable.non));
                                    nodeIdList.add(api_split2[2]);
                                }
                                if (api_split2[2].equals(now_nodeId)) {
                                    listAdapter.list_clear();
                                    nodeIdList.clear();
                                    s_pos = i + 1;
                                }
                            }
                            listAdapter.notifyDataSetChanged();
                            String finalNow_nodeId = now_nodeId;
                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                                    // 기존 알림과 겹치는지 확인 후 기존 알림 삭제, 삭제된 사용자에게 알람 발송
                                    AlertDialog.Builder dlg = new AlertDialog.Builder(Driver_EbusSet_api.this);
                                    dlg.setTitle("알림 신청 확인");

                                    String[] api_split3 = api_split[s_pos + pos].split(" ");
                                    dlg.setMessage("도착 정류장 : " + api_split3[3] + "\n입력하신 정보가 맞습니까?");
                                    dlg.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Toast.makeText(Driver_EbusSet_api.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    dlg.setNegativeButton("예", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            ArrayList<NoticeApi> noticeApiArr = new ArrayList<>();
                                            ArrayList<String> notice_KeyArr = new ArrayList<>();

                                            mDatabaseRef = database.getReference("Notice_api");
                                            mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                                    for (DataSnapshot snapshot2 : dataSnapshot2.getChildren()) {
                                                        if (snapshot2.child("u_type").getValue(int.class) == 1
                                                                && snapshot2.child("Vehicleno").getValue(String.class).equals(driver_api.getVehicleNo())) {
                                                            noticeApiArr.add(snapshot2.getValue(NoticeApi.class));
                                                            notice_KeyArr.add(snapshot2.getKey());
                                                        }
                                                    }

                                                    for (int i = 0; i < noticeApiArr.size(); i++) {
                                                        for (int j = 0; j < pos; j++) {
                                                            if (noticeApiArr.get(i).getSbusStopNodeId().equals(nodeIdList.get(j))) {
                                                                database.getReference("Notice_api").child(notice_KeyArr.get(i)).removeValue();
                                                            }
                                                        }
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) { }
                                            });

                                            mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    int j = 1;
                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                        if (j != Integer.parseInt(snapshot.getKey()))
                                                            break;
                                                        j++;
                                                    }
                                                    database.getReference("Notice_api").child(Integer.toString(j)).child("Uid").setValue("No Use App");
                                                    database.getReference("Notice_api").child(Integer.toString(j)).child("BusNum").setValue("No Use App");
                                                    database.getReference("Notice_api").child(Integer.toString(j)).child("RouteId").setValue(driver_api.getRouteId());
                                                    database.getReference("Notice_api").child(Integer.toString(j)).child("SbusStopNodeId").setValue(finalNow_nodeId);
                                                    database.getReference("Notice_api").child(Integer.toString(j)).child("EbusStopNodeId").setValue(api_split3[2]);
                                                    database.getReference("Notice_api").child(Integer.toString(j)).child("CityCode").setValue(driver_api.getCityCode());
                                                    database.getReference("Notice_api").child(Integer.toString(j)).child("Vehicleno").setValue(driver_api.getVehicleNo());
                                                    database.getReference("Notice_api").child(Integer.toString(j)).child("busRide").setValue("1");
                                                    database.getReference("Notice_api").child(Integer.toString(j)).child("u_type").setValue(1);
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) { }
                                            });

                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(Driver_EbusSet_api.this, "앱 미사용자 탑승 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(Driver_EbusSet_api.this, DriverMain_Api.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }, 1000);
                                        }
                                    });
                                    dlg.show();
                                }
                            });
                        }
                        else {
                            Toast.makeText(Driver_EbusSet_api.this, "API 오류.. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
