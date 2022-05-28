package com.example.myapplication.driver;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Driver_EndService extends Service {
    MediaPlayer mediaPlayer;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference("Driver");
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if ((firebaseUser.getUid()).equals(dataSnapshot.child("Uid").getValue(String.class))) {
                        database.getReference("BusSeat").child(dataSnapshot.child("BusNum").getValue(String.class)).child(dataSnapshot.child("BusTime").getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                for (DataSnapshot dataSnapshot2 : snapshot2.getChildren()) {
                                    database.getReference("BusSeat").child(dataSnapshot.child("BusNum").getValue(String.class)).child(dataSnapshot.child("BusTime").getValue(String.class)).child(dataSnapshot2.getKey()).setValue(0);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        Toast.makeText(this, "운행이 종료되었습니다.", Toast.LENGTH_SHORT).show();
        /*
        mediaPlayer = MediaPlayer.create(this, R.raw.sound);
        mediaPlayer.start();
        */
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
