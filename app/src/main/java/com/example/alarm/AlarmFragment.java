package com.example.alarm;

import static com.example.MainActivity.ALARM_REQ_CODE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MainActivity;
import com.example.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AlarmFragment extends Fragment {

    private static final String TAG = "AlarmFragment";
    ArrayList<DataAlarm> alarmList;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private long totalSeconds;
    private long currentSeconds;
    private long milliSeconds;
    private Spinner daySpinner;
    private boolean isAM;
    private boolean isPM;
    private AlarmAdapter alarmAdapter;
    private ImageButton imageButtonBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        // Set the title for the ActionBar
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Alarm");
        }


        daySpinner = view.findViewById(R.id.daySpinner);
        imageButtonBack = view.findViewById(R.id.imageButtonBack);
        alarmList = new ArrayList<DataAlarm>();
        // RecyclerView setup
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewAlarms);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        alarmAdapter = new AlarmAdapter(alarmList);
        recyclerView.setAdapter(alarmAdapter);

        imageButtonBack.setOnClickListener(v -> showTimePicker());

        // Load existing alarms from Room database
        loadAlarmsFromDatabaseAsync();
        return view;
    }

    private void setAlarm(long milliSeconds) {
        saveAlarmTime(milliSeconds);

        Intent intent = new Intent(requireActivity(), AlarmReceiver.class);
        intent.setAction(AlarmReceiver.ALARM_ACTION);
        intent.putExtra("milliSeconds", milliSeconds);

        pendingIntent = PendingIntent.getBroadcast(requireActivity(), ALARM_REQ_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis() + milliSeconds), pendingIntent);
        enableBootReceiver(true);
        // Notify the adapter that the data set has changed
        alarmAdapter.notifyDataSetChanged();
    }

    private void matchSeconds(long totalSeconds, long currentSeconds) {
        if (totalSeconds != currentSeconds) {
            milliSeconds = (totalSeconds - currentSeconds) * 1000;
            setAlarm(milliSeconds);
        } else {
            Toast.makeText(requireContext(), "Can't set an alarm.", Toast.LENGTH_LONG).show();
            return;
        }
    }

    private void showTimePicker() {

        DataAlarm dataAlarm = new DataAlarm();
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireActivity(),
                (view, hourOfDay, minute) -> {
                    // New code to handle day selection
                    String selectedDay = daySpinner.getSelectedItem().toString();
                    int selectedDayIndex = Arrays.asList(getResources().getStringArray(R.array.days_of_week)).indexOf(selectedDay) + 1;

                    int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                    // Setting values to DataAlarm class
                    dataAlarm.setHour(hourOfDay);
                    dataAlarm.setMinute(minute);
                    dataAlarm.setDay(selectedDay);

                    // Check if the selected hour is in the AM or PM
                    isAM = (hourOfDay >= 0 && hourOfDay < 12);
                    dataAlarm.setIsAm(isAM);
                    if (!isAM) {
                        isPM = false;
                    } else {
                        isPM = true;
                    }
                    dataAlarm.setIsAm(isPM);

                    // Add dataAlarm to the ArrayList
                    alarmList.add(dataAlarm);

                    // Save the new alarm to the Room database in a background thread
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            // Retrieve existing alarms from the Room database
                            List<DataAlarm> databaseAlarms = MainActivity.database.alarmDao().getAllAlarm(8);

                            // If the size exceeds 7, remove the oldest alarms to make room for the new one
                            while (databaseAlarms.size() >= 8) {
                                DataAlarm oldestAlarm = databaseAlarms.get(0);
                                MainActivity.database.alarmDao().deleteAlarm(oldestAlarm);
                                databaseAlarms.remove(oldestAlarm);
                            }

                            // Add the new alarm to the Room database
                            if (databaseAlarms.size() < 8) {
                                MainActivity.database.alarmDao().addAlarm(dataAlarm);
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);

                            // Calculate the totalSeconds for the selected day and time
                            totalSeconds = (selectedDayIndex - currentDayOfWeek + 7) % 7 * 24 * 60 * 60 + (hourOfDay * 60L + minute) * 60;

                            // Check if the calculated time is in the past for the current day
                            if (totalSeconds < currentSeconds) {
                                // Adjust for the next week if the selected time is in the past
                                totalSeconds += 7 * 24 * 60 * 60;
                            }

                            Log.d(TAG, "totalSeconds: " + (totalSeconds));

                            // Calculate current time in seconds
                            currentSeconds = (currentHour * 60 + currentMinute) * 60;
                            Log.d(TAG, "currentSeconds: " + currentSeconds);

                            // Calculate hours and minutes remaining
                            long secondsRemaining = totalSeconds - currentSeconds;
                            long hoursRemaining = secondsRemaining / 3600;
                            long minutesRemaining = (secondsRemaining % 3600) / 60;

                            String message = String.format("Alarm will ring in %d hours and %d minutes.", hoursRemaining, minutesRemaining);

                            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();

                            // Call your method to handle the alarm logic (e.g., matchSeconds)
                            matchSeconds((totalSeconds-60), currentSeconds);
                        }
                    }.execute();
                },
                currentHour,
                currentMinute,
                false
        );

        timePickerDialog.show();
    }

    private void enableBootReceiver(boolean enable) {
        ComponentName receiver = new ComponentName(requireActivity(), BootReceiver.class);
        PackageManager pm = requireActivity().getPackageManager();

        pm.setComponentEnabledSetting(
                receiver,
                enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
        );
    }

    private void saveAlarmTime(long milliSeconds) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("alarm_time", milliSeconds);
        editor.apply();
    }

    private void loadAlarmsFromDatabaseAsync() {
        new AsyncTask<Void, Void, List<DataAlarm>>() {
            @Override
            protected List<DataAlarm> doInBackground(Void... voids) {
                // Retrieve existing alarms from the Room database on a background thread
                List<DataAlarm> databaseAlarms = MainActivity.database.alarmDao().getAllAlarm(8);

                return databaseAlarms;
            }

            @Override
            protected void onPostExecute(List<DataAlarm> databaseAlarms) {
                super.onPostExecute(databaseAlarms);

                // Clear the existing list before adding retrieved alarms
                alarmList.clear();

                // Add retrieved alarms to the ArrayList
                alarmList.addAll(databaseAlarms);

                // Notify the adapter that the data set has changed
                alarmAdapter.notifyDataSetChanged();
            }
        }.execute();
    }
}
