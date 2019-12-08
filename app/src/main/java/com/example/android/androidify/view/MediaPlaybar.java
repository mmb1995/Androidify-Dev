package com.example.android.androidify.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.android.androidify.R;

import androidx.constraintlayout.widget.ConstraintLayout;

public class MediaPlaybar extends ConstraintLayout {
    private static final int LOOP_DURATION = 1000;

    private TextView mTimeElapsed, mDuration;
    private SeekBar mSeekBar;
    private Handler mHandler;

    private OnSeekListener mListener;

    private final SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            MediaPlaybar.this.removeCallbacks();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
           if (mListener != null) {
               mListener.onSeek((long) seekBar.getProgress());
           }
        }
    };

    private final Runnable mSeekRunnable = new Runnable() {
        @Override
        public void run() {
            int progress = mSeekBar.getProgress();
            mSeekBar.setProgress(progress + LOOP_DURATION);
            if (mTimeElapsed != null) {
                mTimeElapsed.setText(formatTime(progress));
            }
            MediaPlaybar.this.setCallback();
        }
    };

    public MediaPlaybar(Context context) {
        this(context, null);
    }

    public MediaPlaybar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaPlaybar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks();
    }

    public void setOnSeekListener(OnSeekListener listener) {
        this.mListener = listener;
        mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
    }

    private void init() {
        inflate(getContext(), R.layout.media_playbar, this);

        mTimeElapsed = findViewById(R.id.media_time_elapsed);
        mDuration = findViewById(R.id.media_duration);
        mSeekBar = findViewById(R.id.media_seekbar);
        mHandler = new Handler();
    }

    public void stop() {
        removeCallbacks();
    }

    public void start() {
        removeCallbacks();
        setCallback();
    }

    public void setDuration(long duration) {
        mSeekBar.setMax((int) duration);
        if (mDuration != null) {
            mDuration.setText(formatTime(duration));
        }
    }

    public void update(long progress) {
        mSeekBar.setProgress((int) progress);
        if (mTimeElapsed != null) {
            mTimeElapsed.setText(formatTime(progress));
        }
    }

    public boolean isTracking() {
        return mListener != null;
    }

    private void setCallback() {
        this.mHandler.removeCallbacks(mSeekRunnable);
        this.mHandler.postDelayed(mSeekRunnable, LOOP_DURATION);
    }

    private void removeCallbacks() {
        this.mHandler.removeCallbacks(mSeekRunnable);
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

    public interface OnSeekListener {
        void onSeek(long position);
    }

}
