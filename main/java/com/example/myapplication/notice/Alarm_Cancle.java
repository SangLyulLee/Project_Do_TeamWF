package com.example.myapplication.notice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class Alarm_Cancle extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service_intent = new Intent(context, Service_Cancle.class);

        int eBus = intent.getIntExtra("eBus", 0);
        int sBus = intent.getIntExtra("sBus", 0);
        int busNum = intent.getIntExtra("busNum", 0);
        int busTime = intent.getIntExtra("busTime", 0);

        service_intent.putExtra("eBus", eBus);
        service_intent.putExtra("sBus", sBus);
        service_intent.putExtra("busNum", busNum);
        service_intent.putExtra("busTime", busTime);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(service_intent);
        }
        else {
            context.startService(service_intent);
        }
    }
}
