package com.example.myapplication.notice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class Alarm_Reciver extends BroadcastReceiver {
    /*
    private ArrayList<BusTime> busTimeArray = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
*/
    @Override
    public void onReceive(Context context, Intent intent) {
/*
        int alarm_min = intent.getIntExtra("alarm_min", 0);
        int sTime_m = intent.getIntExtra("sTime_m", 0);
        int busNum = intent.getIntExtra("busNum", 0);
        int sR_pos = intent.getIntExtra("sR_pos", 0);

        final Calendar calendar = Calendar.getInstance();

        databaseReference = database.getReference("BusTime").child(Integer.toString(busNum));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                busTimeArray.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BusTime busTime = snapshot.getValue(BusTime.class);
                    if (busTime.getMinutes()+sTime_m < 60)
                        busTime.setMinutes(busTime.getMinutes()+sTime_m);
                    else {
                        busTime.setMinutes((busTime.getMinutes()+sTime_m)%60);
                        busTime.setHours(busTime.getHours()+1);
                    }
                    busTimeArray.add(busTime);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            calendar.setTimeInMillis(System.currentTimeMillis());
            if (busTimeArray.size() != 0) {
                if (busTimeArray.get(sR_pos).getMinutes() <= alarm_min) {
                    calendar.set(Calendar.HOUR_OF_DAY, busTimeArray.get(sR_pos).getHours());
                    calendar.set(Calendar.MINUTE, busTimeArray.get(sR_pos).getMinutes() - alarm_min);
                }
                else {
                    calendar.set(Calendar.HOUR_OF_DAY, busTimeArray.get(sR_pos).getHours() - 1);
                    calendar.set(Calendar.MINUTE, busTimeArray.get(sR_pos).getMinutes() + 60 - alarm_min);
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getBroadcast(context, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            }
            else {
                pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000*60, pendingIntent);
        }
*/
    //    Toast.makeText(context, "잠시 후 버스가 도착할 예정입니다.", Toast.LENGTH_SHORT).show();

        Intent service_intent = new Intent(context, RingtonePlayingService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(service_intent);
        }
        else {
            context.startService(service_intent);
        }
        /*
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent; // = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        }
        else {
            pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        }
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.background_radius).setTicker("HETT").setWhen(System.currentTimeMillis())
                .setNumber(1).setContentTitle("푸쉬 제목").setContentText("푸쉬내용")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent).setAutoCancel(true);
        notificationmanager.notify(1, builder.build());*/
    }
}
