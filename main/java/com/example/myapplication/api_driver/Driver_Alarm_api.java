package com.example.myapplication.api_driver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class Driver_Alarm_api extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent service_intent = new Intent(context, Driver_Service_api.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(service_intent);
        }
        else {
            context.startService(service_intent);
        }
    }
}
