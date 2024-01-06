package com.example;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.alarm.AlarmDatabase;
import com.example.alarm.AlarmFragment;
import com.example.databinding.ActivityMainBinding;
import com.example.meditation.MeditationFragment;
import com.example.powernap.PowerNapFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {
    public static final int ALARM_REQ_CODE = 100;
    private ActivityMainBinding mainXml;
    public static AlarmDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainXml = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainXml.getRoot());

        database = Room.databaseBuilder(this, AlarmDatabase.class, "alarm_database")
                .build();

        setupTabs();
    }

    private void setupTabs() {
        // you have stored icons named sleepalarm, sleep, and meditation in the drawable folder
        Drawable sleepAlarmDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.sleepalarm, null);
        Drawable sleepDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.alarm, null);
        Drawable meditationDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.meditation, null);

        mainXml.tabLayout.addTab(mainXml.tabLayout.newTab().setIcon(sleepDrawable));
        mainXml.tabLayout.addTab(mainXml.tabLayout.newTab().setIcon(sleepAlarmDrawable));
        mainXml.tabLayout.addTab(mainXml.tabLayout.newTab().setIcon(meditationDrawable));

        mainXml.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), getLifecycle());
        mainXml.fragmentContainer.setAdapter(adapter);

        new TabLayoutMediator(mainXml.tabLayout, mainXml.fragmentContainer, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Alarm");
                    break;
                case 1:
                    tab.setText("Power Nap");
                    break;
                case 2:
                    tab.setText("Meditation");
                    break;
            }
        }).attach();

    }
}