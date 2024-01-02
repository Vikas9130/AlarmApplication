package com.example.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.Settings;

public class AlarmReceiver extends BroadcastReceiver {

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
