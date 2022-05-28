package com.example.myapplication.driver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
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

                        databaseReference = database.getReference("Notice");
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot3) {
                                for (DataSnapshot dataSnapshot3 : snapshot3.getChildren()) {
                                    if (dataSnapshot3.child("BusNum").getValue(String.class).equals(dataSnapshot.child("BusNum").getValue(String.class))
                                    && dataSnapshot3.child("BusTime").getValue(String.class).equals(dataSnapshot.child("BusTime").getValue(String.class))) {
                                        databaseReference.child(dataSnapshot3.getKey()).removeValue();
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });

                        database.getReference("Driver").child(dataSnapshot.getKey()).removeValue();
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        if( Build.VERSION.SDK_INT >= 26 )
        {
            Intent clsIntent = new Intent( this, MainActivity.class );
            PendingIntent pendingIntent;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getBroadcast(this, 2, clsIntent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            }
            else {
                pendingIntent = PendingIntent.getBroadcast(this, 2, clsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            NotificationCompat.Builder clsBuilder;
            String CHANNEL_ID = "0";
            NotificationChannel clsChannel = new NotificationChannel(CHANNEL_ID, "앱", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(clsChannel);

            clsBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
            clsBuilder.setSmallIcon(R.drawable.logo)
                    .setContentTitle("DO! 알림")
                    .setContentText("운행이 종료되었습니다!")
                    .setContentIntent(pendingIntent);

            startForeground(1, clsBuilder.build());
        }

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
