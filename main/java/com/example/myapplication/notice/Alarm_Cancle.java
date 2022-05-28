package com.example.myapplication.notice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class Alarm_Cancle extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service_intent = new Intent(context, Service_Cancle.class);

        service_intent.putExtra("eBus", intent.getStringExtra("eBus"));
        service_intent.putExtra("sBus", intent.getStringExtra("sBus"));
        service_intent.putExtra("busNum", intent.getStringExtra("busNum"));
        service_intent.putExtra("busTime", intent.getStringExtra("busTime"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(service_intent);
        }
        else {
            context.startService(service_intent);
        }
    }
}
