package com.example.myapplication.driver;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.vision.get_api;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriverSelect_api extends AppCompatActivity {
    String input_str, input_vehino;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
    int data_pos, api_data_pos;
    boolean searchBool = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_select_api);
        String citycode = getIntent().getStringExtra("citycode");

        EditText editText = (EditText) findViewById(R.id.edit_busnum_api);
        EditText editText1 = (EditText) findViewById(R.id.edit_vehicleno_api);
        Button search_btn = (Button) findViewById(R.id.btn_select_api);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input_str = editText.getText().toString();
                input_vehino = editText1.getText().toString();

                if (input_str.equals("") || input_vehino.equals("")) {
                    Toast.makeText(DriverSelect_api.this, "버스 번호와 차량 번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    String[] api_split = get_api.getBusRouteNoList(citycode, input_str, "1").split("\n");
                    for (int i = 0; i < api_split.length; i++) {
                        if (api_split[i].equals(input_str)) {
                            api_data_pos = i;
                            searchBool = true;
                            break;
                        }
                    }
                    if (!searchBool) {
                        System.out.println("버스 없음");
                        Toast.makeText(DriverSelect_api.this, "해당 버스 번호는 없는 버스 번호입니다.\n올바른 버스 번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        AlertDialog.Builder dlg = new AlertDialog.Builder(DriverSelect_api.this);
                        dlg.setTitle("버스 확인");
                        dlg.setMessage("운행하실 버스 번호는 " + input_str + "번 버스가 맞습니까?");
                        dlg.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(DriverSelect_api.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dlg.setNegativeButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(DriverSelect_api.this, "확인하였습니다.", Toast.LENGTH_SHORT).show();
                                databaseReference = database.getReference("Driver_api");
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int j = 1;
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            if (j != Integer.parseInt(snapshot.getKey()))
                                                break;
                                            j++;
                                        }
                                        data_pos = j;

                                        databaseReference.child(Integer.toString(data_pos)).child("Uid").setValue(firebaseUser.getUid());
                                        databaseReference.child(Integer.toString(data_pos)).child("routeid").setValue(api_split[0]);
                                        databaseReference.child(Integer.toString(data_pos)).child("vehicleno").setValue(input_vehino);
                                        databaseReference.child(Integer.toString(data_pos)).child("citycode").setValue(citycode);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(DriverSelect_api.this, DriverMain_Api.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 1000);
                            }
                        });
                        dlg.show();
                    }
                }
            }
        });
    }
}
