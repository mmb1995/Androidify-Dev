package com.example.android.androidify.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class MediaProgressBar extends ProgressBar {
    private static final int LOOP_DURATION = 500;
    private final Handler mHandler;

    private final Runnable mProgressRunnable = new Runnable() {
        @Override
        public void run() {
            int progress = getProgress();
            setProgress(progress + LOOP_DURATION);
            mHandler.postDelayed(mProgressRunnable, LOOP_DURATION);
        }
    };

    public MediaProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mHandler = new Handler();
    }

    public MediaProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mHandler = new Handler();
    }

    public MediaProgressBar(Context context) {
        super(context);
        this.mHandler = new Handler();
    }

    public MediaProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mHandler = new Handler();
    }


    public void pause() {
        mHandler.removeCallbacks(mProgressRunnable);
    }

    public void play() {
        mHandler.removeCallbacks(mProgressRunnable);
        mHandler.postDelayed(mProgressRunnable, LOOP_DURATION);
    }

    public void update(long progress) {
        setProgress((int) progress);
    }

    public void setDuration(long duration) {
        setMax((int) duration);
    }

}
