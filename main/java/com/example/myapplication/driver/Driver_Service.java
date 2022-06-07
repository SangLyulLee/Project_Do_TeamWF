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
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

public class Driver_Service extends Service {
    MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if( Build.VERSION.SDK_INT >= 26 )
        {
            Intent clsIntent = new Intent( this, MainActivity.class );
            PendingIntent pendingIntent;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getBroadcast(this, 0, clsIntent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            }
            else {
                pendingIntent = PendingIntent.getBroadcast(this, 0, clsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            NotificationCompat.Builder clsBuilder;
            String CHANNEL_ID = "0";
            NotificationChannel clsChannel = new NotificationChannel(CHANNEL_ID, "앱", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(clsChannel);

            clsBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
            clsBuilder.setSmallIcon(R.drawable.logo)
                    .setContentTitle("DO! 알림")
                    .setContentText("장애인 버스 서비스 실행중")
                    .setContentIntent(pendingIntent);

            startForeground(1, clsBuilder.build());
        }
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);

        mediaPlayer = MediaPlayer.create(this, R.raw.sound);
        mediaPlayer.start();


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
