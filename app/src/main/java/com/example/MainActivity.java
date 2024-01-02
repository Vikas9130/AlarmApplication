package com.example;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TimePicker;

import com.example.alarm.AlarmFragment;
import com.example.alarm.AlarmReceiver;
import com.example.alarm.BootUpReceiver;
import com.example.databinding.ActivityMainBinding;
import com.example.powernap.PowerNapFragment;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "MainActivity";
    public static final int ALARM_REQ_CODE = 100;

    ActivityMainBinding mainXml;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainXml = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainXml.getRoot());
        // Load the default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new AlarmFragment())
                    .commit();
        }
        mainXml.btnPowerNap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Replace the fragment with PowerNapFragment
                replaceFragment(new PowerNapFragment());
            }
        });

        mainXml.btnAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new AlarmFragment());
            }
        });


    }


    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

}
