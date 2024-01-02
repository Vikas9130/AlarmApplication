package com.example.powernap;

import static android.provider.Settings.System.DEFAULT_RINGTONE_URI;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.R;

public class PowerNapFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private EditText editTextNapTime;
    private Button buttonStartTimer;
    private TextView textViewTimer;
    private TextView tvDND;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    // MediaPlayer for playing the ringtone
    private MediaPlayer mediaPlayer;

    // Handler to stop the ringtone after 5 seconds
    private Handler handler = new Handler();

    // Runnable to stop the ringtone after 5 seconds
    private Runnable stopRingtoneRunnable = new Runnable() {
        @Override
        public void run() {
            stopRingtone();
        }
    };

    public PowerNapFragment() {
        // Required empty public constructor
    }

    public static PowerNapFragment newInstance(String param1, String param2) {
        PowerNapFragment fragment = new PowerNapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_power_nap, container, false);

        editTextNapTime = view.findViewById(R.id.editTextNapTime);
        buttonStartTimer = view.findViewById(R.id.buttonStartTimer);
        textViewTimer = view.findViewById(R.id.textViewTimer);
        tvDND = view.findViewById(R.id.tvDND);

        buttonStartTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });

        return view;
    }

    private void startTimer() {
        tvDND.setVisibility(View.VISIBLE);
        String input = editTextNapTime.getText().toString();
        if (input.isEmpty()) {
            Toast.makeText(requireContext(), "Please Enter Minutes.", Toast.LENGTH_LONG).show();
            return;
        }

        long millisInput = Long.parseLong(input) * 60000; // Convert minutes to milliseconds
        if (millisInput <= 0) {
            Toast.makeText(requireContext(), "Please Enter greater than zero.", Toast.LENGTH_LONG).show();
            return;
        }

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        timeLeftInMillis = millisInput;
        updateTimerText();

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                // Timer finished, handle the event
                textViewTimer.setText("Timer Finished!");

                // Play the ringtone
                playRingtone();

                // Schedule the stop of the ringtone after 5 seconds
                handler.postDelayed(stopRingtoneRunnable, 5000);
                tvDND.setVisibility(View.INVISIBLE);

            }
        }.start();
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        textViewTimer.setText(timeLeftFormatted);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        // Stop the ringtone if the fragment is destroyed
        stopRingtone();

        // Remove any pending callbacks from the handler
        handler.removeCallbacks(stopRingtoneRunnable);
    }

    private void playRingtone() {
        mediaPlayer = MediaPlayer.create(requireContext(),DEFAULT_RINGTONE_URI); // Replace with your ringtone sound file
        mediaPlayer.start();
    }

    private void stopRingtone() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
