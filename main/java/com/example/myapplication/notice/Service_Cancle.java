package com.example.myapplication.notice;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
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

public class Service_Cancle extends Service {
    private static final String TAG = Service_Cancle.class.getSimpleName();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        try {
            PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
            //wakelock.acquire(5000);
            wakelock.acquire();
            wakelock.release();
        }
        catch (Exception e){ e.printStackTrace(); }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= 26) {
            Intent clsIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getBroadcast(this, 0, clsIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            } else {
                pendingIntent = PendingIntent.getBroadcast(this, 0, clsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            NotificationCompat.Builder clsBuilder;
            String CHANNEL_ID = "0";
            NotificationChannel clsChannel = new NotificationChannel(CHANNEL_ID, "???", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(clsChannel);

            clsBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
            clsBuilder.setSmallIcon(R.drawable.logo)
                    .setContentTitle("DO! ??????")
                    .setContentText("????????? ?????????????????????.")
                    .setContentIntent(pendingIntent);

            startForeground(1, clsBuilder.build());
        }

        /*
        String busNum = Integer.toString(intent.getIntExtra("busNum", 0));
        String eBus = Integer.toString(intent.getIntExtra("eBus", 0));
        String sBus = Integer.toString(intent.getIntExtra("sBus", 0));
        String busTime = Integer.toString(intent.getIntExtra("busTime", 0));

        Toast.makeText(this, "test : "+eBus, Toast.LENGTH_SHORT).show();
        mDatabaseRef = database.getReference("BusRoute").child("1").child("route").child(busNum);
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                String busNum = Integer.toString(intent.getIntExtra("busNum", 0));
                int eBus = intent.getIntExtra("eBus", 0);
                String sBus = Integer.toString(intent.getIntExtra("sBus", 0));
                String busTime = Integer.toString(intent.getIntExtra("busTime", 0));

                int i = 0;
                for (DataSnapshot snapshot1 : dataSnapshot1.getChildren()) {
                    if (Integer.toString(eBus).equals(snapshot1.getValue(String.class))) {
                        break;
                    }
                    i++;
                }

                final int pos = i;

                i = 0;
                for (DataSnapshot snapshot1 : dataSnapshot1.getChildren()) {
                    if (sBus.equals(snapshot1.getValue(String.class))) {
                        break;
                    }
                    i++;
                }
                final int pos_s = i;
                for (int seat_pos = pos_s + 1; seat_pos <= pos + 1; seat_pos++) {
                    database.getReference("BusSeat")
                            .child(busNum)
                            .child(busTime)
                            .child("route" + Integer.toString(seat_pos)).setValue(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        */
        Toast.makeText(this, "??? ???????????? ???????????? ????????? ?????????????????????.\n?????? ??????????????????.", Toast.LENGTH_SHORT).show();

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("onDestory() ??????", "????????? ??????");
    }
}
