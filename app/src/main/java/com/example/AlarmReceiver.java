package com.example;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.Settings;

public class AlarmReceiver extends BroadcastReceiver {
    MediaPlayer mp;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            mp = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI);
            if (mp != null) {
                mp.setLooping(true);
                mp.start();

                // Release the MediaPlayer when done
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mediaPlayer.release();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
