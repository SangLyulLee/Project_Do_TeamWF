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
import com.example.myapplication.map.BusStop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NoticeRe extends AppCompatActivity {
    private ArrayList<BusStop> arrayList = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference("BusStop");
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
    private Notice notice;
    private String noticeKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ebus_set);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BusStop busStop = snapshot.getValue(BusStop.class);
                    arrayList.add(busStop);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        databaseReference = database.getReference("Notice");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("Uid").getValue(String.class).equals(firebaseUser.getUid())) {
                        notice = snapshot.getValue(Notice.class);
                        noticeKey = snapshot.getKey();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
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
                    if (notice.getBusNum().equals(snapshot.getKey())) {
                        for (DataSnapshot snapshot2 : dataSnapshot.child(notice.getBusNum()).getChildren()) {
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
                        listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.route1), str1_name.get(i), ContextCompat.getDrawable(getApplicationContext(), R.drawable.non));
                    }
                    else if (i == str1_name.size()-1) {
                        listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.route3), str1_name.get(i), ContextCompat.getDrawable(getApplicationContext(), R.drawable.non));
                    }
                    else {
                        listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.route2), str1_name.get(i), ContextCompat.getDrawable(getApplicationContext(), R.drawable.non));
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
                int sPos = 0;
                for (int j=0; j<str.size(); j++) {
                    if (str.get(j).equals(notice.getSbusStopNum())) {
                        sPos = j;
                    }
                }

                if (position > sPos) {
                    AlertDialog.Builder dlg = new AlertDialog.Builder(NoticeRe.this);
                    dlg.setTitle("목적지 주소 확인");

                    dlg.setMessage("도착 지점 : " + str1_name.get(position) + "\n목적지를 변경하시겠습니까?");
                    dlg.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    dlg.setNegativeButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            databaseReference = FirebaseDatabase.getInstance().getReference("Notice");
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    databaseReference.child(noticeKey).child("EbusStopNum").setValue(str.get(position));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            Toast.makeText(NoticeRe.this, "목적지가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(NoticeRe.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                    dlg.show();
                }
                else {
                    Toast.makeText(NoticeRe.this, "출발지 이후의 정류장을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
