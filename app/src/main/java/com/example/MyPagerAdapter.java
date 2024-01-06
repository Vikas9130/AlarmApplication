package com.example;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.alarm.AlarmFragment;
import com.example.meditation.MeditationFragment;
import com.example.powernap.PowerNapFragment;

public class MyPagerAdapter extends FragmentStateAdapter {

    public MyPagerAdapter(FragmentManager supportFragmentManager, Lifecycle lifecycle) {
        super(supportFragmentManager,lifecycle);
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AlarmFragment();
            case 1:
                return new PowerNapFragment();
            case 2:
                return new MeditationFragment();
            default:
                return new AlarmFragment(); // Default to the first fragment
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Number of fragments
    }
}
