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

public class NoticeRe_api extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
    private DatabaseReference databaseReference;
    NoticeApi noticeData;
    int s_pos = 0;
    String notice_pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ebus_set);

        databaseReference = database.getReference().child("Notice_api");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("Uid").getValue(String.class).equals(firebaseUser.getUid())) {
                        noticeData = snapshot.getValue(NoticeApi.class);
                        notice_pos = snapshot.getKey();

                        ListView list = (ListView) findViewById(R.id.list_ebusSet);
                        ListAdapter listAdapter = new ListAdapter();
                        list.setAdapter(listAdapter);

                        String[] api_split = get_api.getBusRoute(noticeData.getCityCode(), noticeData.getRouteId(), "1").split("\n");
                        listAdapter.list_clear();
                        for (int i=0; i<api_split.length; i++) {
                            String[] api_split2 = api_split[i].split(" ");
                            if (api_split2[6].equals("0"))
                                listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.updowncd0), Integer.toString(i+1)+". "+api_split2[3], ContextCompat.getDrawable(getApplicationContext(), R.drawable.non));
                            else if (api_split2[6].equals("1"))
                                listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.updowncd1), Integer.toString(i+1)+". "+api_split2[3], ContextCompat.getDrawable(getApplicationContext(), R.drawable.non));
                            if (api_split2[2].equals(noticeData.getSbusStopNodeId())) {
                                listAdapter.list_clear();
                                s_pos = i+1;
                            }
                        }
                        listAdapter.notifyDataSetChanged();

                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                int position = i + s_pos;
                                System.out.println("i : " + i + ", s_pos : " + s_pos);
                                String[] api_split2 = api_split[position].split(" ");

                                AlertDialog.Builder dlg = new AlertDialog.Builder(NoticeRe_api.this);
                                dlg.setTitle("알림 신청 확인");

                                dlg.setMessage("도착 정류장 : " + api_split2[3] + "\n입력하신 정보가 맞습니까?");
                                dlg.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(NoticeRe_api.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dlg.setNegativeButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        databaseReference = database.getReference("member").child("UserAccount");
                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    if (firebaseUser.getUid().equals(snapshot.getKey())) {
                                                        database.getReference("Notice_api").child(notice_pos).child("EbusStopNodeId").setValue(api_split2[2]);

                                                        Handler handler = new Handler();
                                                        handler.postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(NoticeRe_api.this, "알림 변경이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(NoticeRe_api.this, MainActivity_api.class);
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
