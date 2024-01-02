package com.example.powernap;

import static android.provider.Settings.System.DEFAULT_RINGTONE_URI;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.R;

public class PowerNapFragment extends Fragment {

    private EditText editTextNapTime;
    private Button buttonStartTimer;
    private TextView textViewTimer;
    private TextView tvDND;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();

    private static final int MILLIS_IN_MINUTE = 60000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_power_nap, container, false);

        editTextNapTime = view.findViewById(R.id.editTextNapTime);
        buttonStartTimer = view.findViewById(R.id.buttonStartTimer);
        textViewTimer = view.findViewById(R.id.textViewTimer);
        tvDND = view.findViewById(R.id.tvDND);

        buttonStartTimer.setOnClickListener(v -> startTimer());
        view.setOnTouchListener((v, event) -> {
            hideKeyboard();
            return false;
        });

        return view;
    }

    private void startTimer() {
        tvDND.setVisibility(View.VISIBLE);
        buttonStartTimer.setVisibility(View.GONE);
        editTextNapTime.setVisibility(View.GONE);

        String input = editTextNapTime.getText().toString();
        if (input.isEmpty()) {
            showToast("Please Enter Minutes.");
            return;
        }

        long millisInput = Long.parseLong(input) * MILLIS_IN_MINUTE;
        if (millisInput <= 0) {
            showToast("Please Enter a value greater than zero.");
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
                handleTimerFinish();
            }
        }.start();
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        textViewTimer.setText(timeLeftFormatted);
    }

    private void handleTimerFinish() {
        textViewTimer.setText("Timer Finished!");
        playRingtone();
        handler.postDelayed(this::stopRingtone, 5000);
        tvDND.setVisibility(View.INVISIBLE);
        buttonStartTimer.setVisibility(View.VISIBLE);
        editTextNapTime.setVisibility(View.VISIBLE);
    }

    private void playRingtone() {
        mediaPlayer = MediaPlayer.create(requireContext(), DEFAULT_RINGTONE_URI);
        mediaPlayer.start();
    }

    private void stopRingtone() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextNapTime.getWindowToken(), 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        stopRingtone();
        handler.removeCallbacksAndMessages(null);
    }
}
