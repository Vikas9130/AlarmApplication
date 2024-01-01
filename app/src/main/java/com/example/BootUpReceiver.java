package com.example;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            // Start MainActivity on boot
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

            // Re-schedule the alarm after device reboot
            long savedMilliSeconds = getSavedAlarmTime(context);
            if (savedMilliSeconds > 0) {
                setAlarm(context, savedMilliSeconds);

            }
        }
    }

    private void setAlarm(Context context, long milliSeconds) {
        Log.d("BootUpReceiver", "Setting alarm after reboot.");
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                MainActivity.ALARM_REQ_CODE,
                alarmIntent,
                PendingIntent.FLAG_IMMUTABLE
        );
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + milliSeconds,
                    pendingIntent
            );
        }
    }

    private long getSavedAlarmTime(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getLong("alarm_time", 0);
    }
}
