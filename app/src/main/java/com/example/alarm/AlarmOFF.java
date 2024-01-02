package com.example.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.MainActivity;
import com.example.databinding.ActivityAlarmOffBinding;

public class AlarmOFF extends AppCompatActivity {
    MediaPlayer mp;
    Context appContext;

    ActivityAlarmOffBinding alarmOffXml;
    AlarmManager alarmMgr;
    PendingIntent alarmIntent;
    long milliSeconds;  // Variable to store the received milliSeconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alarmOffXml = ActivityAlarmOffBinding.inflate(getLayoutInflater());
        setContentView(alarmOffXml.getRoot());

        // Retrieve the application context
        appContext = this;

        // Initialize the AlarmManager using the application context
        alarmMgr = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);

        // Intent for AlarmReceiver
//        Intent receiverIntent = new Intent(this, AlarmReceiver.class);
        Intent receiverIntent = getIntent();

        // PendingIntent for AlarmReceiver
        alarmIntent = PendingIntent.getBroadcast(
                this,
                MainActivity.ALARM_REQ_CODE,
                receiverIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Now you can use the application context as needed
        mp = MediaPlayer.create(appContext, Settings.System.DEFAULT_RINGTONE_URI);
        if (mp != null) {
            mp.setLooping(true);
            mp.start();
        }

        // Retrieve the necessary information from the intent extras
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            // Retrieve the milliSeconds value
            milliSeconds = intent.getLongExtra("milliSeconds", 0);

            alarmOffXml.btnAlarmOFF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // If the alarm has been set, cancel it
                    cancelAlarm();
                    // Add any other actions you want to perform when the button is clicked
                }
            });
        }
    }

    private void cancelAlarm() {

        // Cancel the alarm
        if (alarmMgr != null && alarmIntent != null) {
            alarmMgr.cancel(alarmIntent);
        }

        // Stop and release the MediaPlayer
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }

        // Finish the activity
        finish();
    }
}
