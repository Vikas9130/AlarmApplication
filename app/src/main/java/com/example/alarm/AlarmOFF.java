package com.example.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;

import com.example.MainActivity;
import com.example.databinding.ActivityAlarmOffBinding;

public class AlarmOFF extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private Context appContext;
    private ActivityAlarmOffBinding alarmOffXml;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private long milliSeconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alarmOffXml = ActivityAlarmOffBinding.inflate(getLayoutInflater());
        setContentView(alarmOffXml.getRoot());

        // Retrieve the application context
        appContext = this;

        // Initialize the AlarmManager using the application context
        alarmMgr = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);

        // PendingIntent for AlarmReceiver
        alarmIntent = PendingIntent.getBroadcast(
                this,
                MainActivity.ALARM_REQ_CODE,
                new Intent(this, AlarmReceiver.class),
                PendingIntent.FLAG_IMMUTABLE
        );


        // Initialize and start the MediaPlayer
        mediaPlayer = MediaPlayer.create(appContext, Settings.System.DEFAULT_RINGTONE_URI);
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }

        // Retrieve the necessary information from the intent extras
        Intent intent = getIntent();
        if (intent != null) {
            // Retrieve the milliSeconds value
            milliSeconds = intent.getLongExtra("milliSeconds", 0);

            // Set up the button click listener
            alarmOffXml.btnAlarmOFF.setOnClickListener(v -> cancelAlarm());
        }
    }

    private void cancelAlarm() {
        // Cancel the alarm
        if (alarmMgr != null && alarmIntent != null) {
            alarmMgr.cancel(alarmIntent);
        }

        // Stop and release the MediaPlayer
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // Finish the activity
        finish();
    }
}
