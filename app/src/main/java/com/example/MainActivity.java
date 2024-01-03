package com.example;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.alarm.AlarmFragment;
import com.example.databinding.ActivityMainBinding;
import com.example.meditation.MeditationFragment;
import com.example.powernap.PowerNapFragment;

public class MainActivity extends AppCompatActivity {

    public static final int ALARM_REQ_CODE = 100;

    private ActivityMainBinding mainXml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainXml = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainXml.getRoot());

        initializeButtons();
        loadDefaultFragment(savedInstanceState);
    }

    private void initializeButtons() {
        mainXml.btnPowerNap.setOnClickListener(view -> replaceFragment(new PowerNapFragment()));

        mainXml.btnAlarm.setOnClickListener(view -> replaceFragment(new AlarmFragment()));

        mainXml.btnMeditation.setOnClickListener(view -> replaceFragment(new MeditationFragment()));
    }

    private void loadDefaultFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            replaceFragment(new AlarmFragment());
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}
