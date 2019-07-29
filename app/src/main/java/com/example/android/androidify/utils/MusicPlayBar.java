package com.example.android.androidify.utils;

import android.os.Handler;
import android.widget.SeekBar;
import android.widget.TextView;

import com.spotify.android.appremote.api.SpotifyAppRemote;

public class MusicPlayBar {

    private static final int LOOP_DURATION = 500;
    private final SeekBar mSeekBar;
    private final SpotifyAppRemote mSpotifyAppRemote;
    private final Handler mHandler;
    private final TextView mTimeElapsed;
    private final TextView mDuration;

    private final SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mSpotifyAppRemote.getPlayerApi().seekTo(seekBar.getProgress())
                    .setErrorCallback(throwable -> {});
        }
    };

    private final Runnable mSeekRunnable = new Runnable() {
        @Override
        public void run() {
            int progress = mSeekBar.getProgress();
            mSeekBar.setProgress(progress + LOOP_DURATION);
            mTimeElapsed.setText(formatTime((long) progress));
            mHandler.postDelayed(mSeekRunnable, LOOP_DURATION);
        }
    };

    public MusicPlayBar(SeekBar seekBar, SpotifyAppRemote remote, TextView timeElapsed, TextView duration ) {
        this.mSeekBar = seekBar;
        this.mTimeElapsed = timeElapsed;
        this.mDuration = duration;
        this.mSpotifyAppRemote = remote;
        mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mHandler = new Handler();
    }

    public void setDuration(long duration) {
        mSeekBar.setMax((int) duration);
        mDuration.setText(formatTime(duration));
    }

    public void update(long progress) {
        mSeekBar.setProgress((int) progress);
        mTimeElapsed.setText(formatTime(progress));
    }

    public void pause() {
        mHandler.removeCallbacks(mSeekRunnable);
    }

    public void unpause() {
        mHandler.removeCallbacks(mSeekRunnable);
        mHandler.postDelayed(mSeekRunnable, LOOP_DURATION);
    }

    private String formatTime(long milliseconds) {
        long minutes = (milliseconds / 1000) / 60;
        long seconds = (milliseconds / 1000) % 60;
        String secondsStr = Long.toString(seconds);
        String secs;
        if (secondsStr.length() >= 2) {
            secs = secondsStr.substring(0, 2);
        } else {
            secs = "0" + secondsStr;
        }

        return minutes + ":" + secs;
    }
}
