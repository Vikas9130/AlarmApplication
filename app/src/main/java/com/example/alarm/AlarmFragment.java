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
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.R;

import java.util.Calendar;

public class AlarmFragment extends Fragment {

    private static final String TAG = "AlarmFragment";
    private static final int ALARM_REQ_CODE = 100;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private long totalSeconds;
    private long currentSeconds;
    private long milliSeconds;

    private ImageButton imageButtonBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        imageButtonBack = view.findViewById(R.id.imageButtonBack);

        imageButtonBack.setOnClickListener(v -> showTimePicker());

        return view;
    }

    private void setAlarm(long milliSeconds) {

        saveAlarmTime(milliSeconds);

        Intent intent = new Intent(requireActivity(), AlarmReceiver.class);
        intent.putExtra("milliSeconds", milliSeconds);

        pendingIntent = PendingIntent.getBroadcast(requireActivity(), ALARM_REQ_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis() + milliSeconds), pendingIntent);
        enableBootReceiver(true);
    }

    private void matchSeconds(long totalSeconds, long currentSeconds) {
        if (totalSeconds != currentSeconds) {
            milliSeconds = (totalSeconds - currentSeconds) * 1000;
            setAlarm(milliSeconds);
        }else{
            Toast.makeText(requireContext(), "Can't set an alarm.", Toast.LENGTH_LONG).show();
            return;
        }
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireActivity(),
                (view, hourOfDay, minute) -> {
                    totalSeconds = (long) (hourOfDay * 60L + minute) * 60;
                    Log.d(TAG, "totalSeconds: " + (totalSeconds - 30));

                    int currentHourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                    int currentMinuteOfDay = calendar.get(Calendar.MINUTE);
                    currentSeconds = (currentHourOfDay * 60 + currentMinuteOfDay) * 60;
                    Log.d(TAG, "currentSeconds: " + currentSeconds);

                    matchSeconds((totalSeconds - 30), currentSeconds);
                },
                currentHour,
                currentMinute,
                false
        );

        timePickerDialog.show();
    }

    private void saveAlarmTime(long milliSeconds) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(requireContext()).edit();
        editor.putLong("alarm_time", milliSeconds);
        editor.apply();
    }

    private void enableBootReceiver(boolean enable) {
        ComponentName receiver = new ComponentName(requireContext(), BootUpReceiver.class);
        PackageManager pm = requireActivity().getPackageManager();
        int newState = enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        pm.setComponentEnabledSetting(receiver, newState, PackageManager.DONT_KILL_APP);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

}
