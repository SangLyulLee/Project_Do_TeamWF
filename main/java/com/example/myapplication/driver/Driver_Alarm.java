package com.example.myapplication.driver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class Driver_Alarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent service_intent = new Intent(context, Driver_Service.class);

        int uType = intent.getIntExtra("uType", 0);
        service_intent.putExtra("uType", uType);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(service_intent);
        }
        else {
            context.startService(service_intent);
        }
    }
}
