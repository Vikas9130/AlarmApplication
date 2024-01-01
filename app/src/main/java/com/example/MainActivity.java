package com.example;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TimePicker;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    static final int ALARM_REQ_CODE = 100;
    ImageButton imageButtonBack;
    long totalSeconds;
    long currentSeconds;
    long milliSeconds;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        imageButtonBack = findViewById(R.id.imageButtonBack);
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });
    }

    public void setAlarm(long milliSeconds) {
        // Save the alarm time
        saveAlarmTime(milliSeconds);

        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, ALARM_REQ_CODE, intent, PendingIntent.FLAG_MUTABLE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis() + milliSeconds), pendingIntent);
        enableBootReceiver(true);


    }

    public void matchSeconds(long totalSeconds, long currentSeconds) {
        if (totalSeconds != currentSeconds) {
            milliSeconds = (totalSeconds - currentSeconds) * 1000;
            setAlarm(milliSeconds);
        }
    }

    public void showTimePicker() {
        // Get the current time
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        // Create a TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                MainActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Handle the selected time
                        String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                        totalSeconds = (long) (hourOfDay * 60L + minute) * 60;
                        System.out.println("milliseconds " + totalSeconds);

                        // Inside your activity or class
                        Calendar calendar = Calendar.getInstance();
                        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                        int currentMinute = calendar.get(Calendar.MINUTE);
                        currentSeconds = (currentHour * 60 + currentMinute) * 60;
                        System.out.println("currentSeconds " + currentSeconds);
                        matchSeconds(totalSeconds, currentSeconds);
                    }
                },
                currentHour, // Initial hour
                currentMinute, // Initial minute
                false // Set to true if you want 24-hour mode, false for 12-hour mode with AM/PM
        );

        // Show the TimePickerDialog
        timePickerDialog.show();
    }

    // Save the alarm time
    private void saveAlarmTime(long milliSeconds) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putLong("alarm_time", milliSeconds);
        editor.apply();
    }

    private void enableBootReceiver(boolean enable) {
        ComponentName receiver = new ComponentName(this, BootUpReceiver.class);
        PackageManager pm = getPackageManager();

        int newState = enable
                ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;

        pm.setComponentEnabledSetting(receiver, newState, PackageManager.DONT_KILL_APP);
    }

    // Cancel the alarm when the activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

}
