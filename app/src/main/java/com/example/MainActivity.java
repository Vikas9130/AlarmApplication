package com.example;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.alarm.AlarmFragment;
import com.example.databinding.ActivityMainBinding;
import com.example.meditation.MeditationFragment;
import com.example.powernap.PowerNapFragment;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    public static final int ALARM_REQ_CODE = 100;
    private ActivityMainBinding mainXml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainXml = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainXml.getRoot());



        setupTabs();
        loadDefaultFragment(savedInstanceState);
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

        mainXml.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        replaceFragment(new AlarmFragment());
                        break;
                    case 1:
                        replaceFragment(new PowerNapFragment());
                        break;
                    case 2:
                        replaceFragment(new MeditationFragment());
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void loadDefaultFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            replaceFragment(new AlarmFragment());
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
