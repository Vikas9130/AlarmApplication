package com.example.alarm;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;

import com.example.R;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlarmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlarmFragment extends Fragment {

    static final String TAG = "AlarmFragment";
    public static final int ALARM_REQ_CODE = 100;
    long totalSeconds;
    long currentSeconds;
    long milliSeconds;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    ImageButton imageButtonBack;

    public AlarmFragment() {
        // Required empty public constructor
    }

    public static AlarmFragment newInstance(String param1, String param2) {
        AlarmFragment fragment = new AlarmFragment();
        Bundle args = new Bundle();
        args.putString("ARG_PARAM1", param1);
        args.putString("ARG_PARAM2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        imageButtonBack = view.findViewById(R.id.imageButtonBack);

        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });

        return view;
    }

    private void setAlarm(long milliSeconds) {
        // Save the alarm time
        saveAlarmTime(milliSeconds);

        Intent intent = new Intent(requireActivity(), AlarmReceiver.class);
        intent.putExtra("milliSeconds", milliSeconds);
        pendingIntent = PendingIntent.getBroadcast(requireActivity(), ALARM_REQ_CODE, intent, PendingIntent.FLAG_MUTABLE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis() + milliSeconds), pendingIntent);
        enableBootReceiver(true);
    }

    private void matchSeconds(long totalSeconds, long currentSeconds) {
        if (totalSeconds != currentSeconds) {
            milliSeconds = (totalSeconds - currentSeconds) * 1000;
            setAlarm(milliSeconds);
        }
    }

    private void showTimePicker() {
        // Get the current time
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        // Create a TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireActivity(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Handle the selected time
                        String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                        totalSeconds = (long) (hourOfDay * 60L + minute) * 60;
                        Log.d(TAG, "totalSeconds: " + String.valueOf(totalSeconds));

                        // Inside your activity or class
                        Calendar calendar = Calendar.getInstance();
                        // Get the current time
                        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                        int currentMinute = calendar.get(Calendar.MINUTE);
                        currentSeconds = (currentHour * 60 + currentMinute) * 60;
                        Log.d(TAG, "currentSeconds: " + String.valueOf(currentSeconds));

                        matchSeconds((totalSeconds - 30), currentSeconds);
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
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(requireContext()).edit();
        editor.putLong("alarm_time", milliSeconds);
        editor.apply();
    }

    private void enableBootReceiver(boolean enable) {
        ComponentName receiver = new ComponentName(requireContext(), BootUpReceiver.class);
        PackageManager pm = requireActivity().getPackageManager();

        int newState = enable
                ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;

        pm.setComponentEnabledSetting(receiver, newState, PackageManager.DONT_KILL_APP);
    }

    // Cancel the alarm when the fragment is destroyed
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
