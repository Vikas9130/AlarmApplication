// AlarmReceiver.java
package com.example.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String ALARM_ACTION = "com.example.alarm.ALARM_TRIGGERED";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Create an Intent for the AlarmOFF activity
        Intent alarmOffIntent = new Intent(context, AlarmOFF.class);

        // Add FLAG_ACTIVITY_NEW_TASK flag
        alarmOffIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Start the AlarmOFF activity
        context.startActivity(alarmOffIntent);
    }
}
