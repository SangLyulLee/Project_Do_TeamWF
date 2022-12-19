package com.example.myapplication.api_notice;

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
import com.example.myapplication.api_ver.MainActivity_api;
import com.example.myapplication.api_ver.get_api;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EbusSet_api extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
    int notice_pos = 1;
    int s_pos = 0;
    boolean noticeBool = true, seatpass = true, already_notice = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ebus_set);

        String citycode = getIntent().getStringExtra("citycode");
        String nodeid = getIntent().getStringExtra("nodeid");
        String routeid = getIntent().getStringExtra("routeid");
        String routeno = getIntent().getStringExtra("routeno");
        String veNo = getIntent().getStringExtra("veNo");

        ListView list = (ListView) findViewById(R.id.list_ebusSet);
        ListAdapter listAdapter = new ListAdapter();
        list.setAdapter(listAdapter);

        String[] api_split = get_api.getBusRoute(citycode, routeid, "1").split("\n");
        listAdapter.list_clear();
        for (int i=0; i<api_split.length; i++) {
            String[] api_split2 = api_split[i].split(" ");
            if (api_split2.length == 7) {
                if (api_split2[6].equals("0"))
                    listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.updowncd0), Integer.toString(i + 1) + ". " + api_split2[3], ContextCompat.getDrawable(getApplicationContext(), R.drawable.non));
                else if (api_split2[6].equals("1"))
                    listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.updowncd1), Integer.toString(i + 1) + ". " + api_split2[3], ContextCompat.getDrawable(getApplicationContext(), R.drawable.non));
            }
            else {
                listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.updowncd0), Integer.toString(i + 1) + ". " + api_split2[3], ContextCompat.getDrawable(getApplicationContext(), R.drawable.non));
            }
            if (api_split2[2].equals(nodeid)) {
                listAdapter.list_clear();
                s_pos = i+1;
            }
        }

        databaseReference = database.getReference("Notice_api");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (firebaseUser.getUid().equals(snapshot.child("Uid").getValue(String.class))) {
                        already_notice = false;
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        boolean[] seatBool_Arr = new boolean[api_split.length-s_pos];
        databaseReference = database.getReference().child("Notice_api");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NoticeApi noticeData = snapshot.getValue(NoticeApi.class);
                    assert noticeData != null;
                    if (noticeData.getVehicleno().equals(veNo) && noticeData.getU_type() == 1) {
                        boolean seatBool = false;
                        for (int i = s_pos; i < api_split.length; i++) {
                            String[] api_split2 = api_split[i].split(" ");
                            if (seatBool) {
                                if (api_split2[2].equals(noticeData.getEbusStopNodeId())) {
                                    seatBool = false;
                                    break;
                                } else {
                                    listAdapter.setListImg2(i - s_pos, ContextCompat.getDrawable(getApplicationContext(), R.drawable.seat));
                                    seatBool_Arr[i - s_pos] = true;
                                }
                            } else {
                                if (api_split2[2].equals(noticeData.getSbusStopNodeId())) {
                                    seatBool_Arr[i - s_pos] = true;
                                    listAdapter.setListImg2(i - s_pos, ContextCompat.getDrawable(getApplicationContext(), R.drawable.seat));
                                    seatBool = true;
                                    continue;
                                }
                            }
                            if (api_split2[2].equals(noticeData.getEbusStopNodeId())) {
                                for (int j = i - 1; j >= s_pos; j--) {
                                    listAdapter.setListImg2(j - s_pos, ContextCompat.getDrawable(getApplicationContext(), R.drawable.seat));
                                    seatBool_Arr[j - s_pos] = true;
                                }
                                break;
                            }
                        }
                        String[] test_split = api_split[s_pos-1].split(" ");
                        if (test_split[2].equals(nodeid)) {
                            seatpass = false;
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        listAdapter.notifyDataSetChanged();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int position = i + s_pos;
                for (int j=0; j<seatBool_Arr.length; j++) {
                    if (seatBool_Arr[i]) {
                        Toast.makeText(EbusSet_api.this, "빈 자리를 선택해주세요.", Toast.LENGTH_SHORT).show();
                        noticeBool = false;
                        break;
                    }
                }

                if (!already_notice) {
                    Toast.makeText(EbusSet_api.this, "이미 신청한 알림이 있습니다. 확인해주세요.", Toast.LENGTH_SHORT).show();
                    noticeBool = false;
                }
                else if (!seatpass) {
                    Toast.makeText(EbusSet_api.this, "탑승 정류장에 자리가 없습니다. 다른 출발지를 선택해주세요.", Toast.LENGTH_SHORT).show();
                    noticeBool = false;
                }

                if (noticeBool) {
                    String[] api_split2 = api_split[position].split(" ");

                    AlertDialog.Builder dlg = new AlertDialog.Builder(EbusSet_api.this);
                    dlg.setTitle("알림 신청 확인");

                    dlg.setMessage("도착 정류장 : " + api_split2[3] + "\n입력하신 정보가 맞습니까?");
                    dlg.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(EbusSet_api.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dlg.setNegativeButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            databaseReference = database.getReference().child("Notice_api");
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    int j = 1;
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        if (j != Integer.parseInt(snapshot.getKey()))
                                            break;
                                        j++;
                                    }
                                    notice_pos = j;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });

                            databaseReference = database.getReference("member").child("UserAccount");
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        if (firebaseUser.getUid().equals(snapshot.getKey())) {
                                            database.getReference("Notice_api").child(Integer.toString(notice_pos)).child("Uid").setValue(firebaseUser.getUid());
                                            database.getReference("Notice_api").child(Integer.toString(notice_pos)).child("BusNum").setValue(routeno);
                                            database.getReference("Notice_api").child(Integer.toString(notice_pos)).child("RouteId").setValue(routeid);
                                            database.getReference("Notice_api").child(Integer.toString(notice_pos)).child("SbusStopNodeId").setValue(nodeid);
                                            database.getReference("Notice_api").child(Integer.toString(notice_pos)).child("EbusStopNodeId").setValue(api_split2[2]);
                                            database.getReference("Notice_api").child(Integer.toString(notice_pos)).child("CityCode").setValue(citycode);
                                            database.getReference("Notice_api").child(Integer.toString(notice_pos)).child("Vehicleno").setValue(veNo);
                                            database.getReference("Notice_api").child(Integer.toString(notice_pos)).child("busRide").setValue("0");
                                            database.getReference("Notice_api").child(Integer.toString(notice_pos)).child("u_type").setValue(snapshot.child("u_type").getValue(int.class));

                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(EbusSet_api.this, "알림 신청이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(EbusSet_api.this, MainActivity_api.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }, 1000);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });

                        }
                    });
                    dlg.show();
                }
            }
        });
    }
}
