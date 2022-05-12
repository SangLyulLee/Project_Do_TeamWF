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
import androidx.core.content.ContextCompat;

import com.example.myapplication.ListAdapter;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.login.UserAccount;
import com.example.myapplication.map.BusStop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EbusSet extends AppCompatActivity {
    private ArrayList<BusStop> arrayList = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference("BusStop");
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
    private ArrayList<UserAccount> userArray  = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ebus_set);

        int busNum = getIntent().getIntExtra("busNum", 0);
        int s_pos = getIntent().getIntExtra("s_pos", 0);
        int sR_pos = getIntent().getIntExtra("sR_pos", 0);

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
                        listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.route1), str1_name.get(i));
                    }
                    else if (i == str1_name.size()-1) {
                        listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.route3), str1_name.get(i));
                    }
                    else {
                        listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.route2), str1_name.get(i));
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
                if (s_pos >= position) {
                    Toast.makeText(EbusSet.this, "출발지 이후의 정류장을 선택해주세요.", Toast.LENGTH_SHORT).show();
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
                                    databaseReference.child(Integer.toString(j)).child("Uid").setValue(firebaseUser.getUid());
                                    databaseReference.child(Integer.toString(j)).child("BusNum").setValue(Integer.toString(busNum));
                                    databaseReference.child(Integer.toString(j)).child("BusTime").setValue(Integer.toString(sR_pos+1));
                                    databaseReference.child(Integer.toString(j)).child("SbusStopNum").setValue(arrayList.get(s_pos).getBusStopNum());
                                    databaseReference.child(Integer.toString(j)).child("EbusStopNum").setValue(arrayList.get(position).getBusStopNum());
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
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

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
