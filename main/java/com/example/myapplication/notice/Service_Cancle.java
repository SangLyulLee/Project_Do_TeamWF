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

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

public class Service_Cancle extends Service {
    private static final String TAG = Service_Cancle.class.getSimpleName();

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
                    .setContentText("알림이 취소되었습니다.")
                    .setContentIntent(pendingIntent);

            startForeground(1, clsBuilder.build());
        }

        Toast.makeText(this, "앱 미사용자 탑승으로 알림이 취소되었습니다.\n다시 선택해주세요.", Toast.LENGTH_SHORT).show();

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("onDestory() 실행", "서비스 파괴");
    }
}
