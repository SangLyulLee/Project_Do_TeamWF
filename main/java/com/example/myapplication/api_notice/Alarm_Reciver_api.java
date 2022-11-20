package com.example.myapplication.api_notice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.myapplication.notice.Alarm_Service;

public class Alarm_Reciver_api extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service_intent = new Intent(context, Alarm_Service_api.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(service_intent);
        }
        else {
            context.startService(service_intent);
        }
    }
}
