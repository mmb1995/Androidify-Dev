package com.example.android.androidify.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TopHistoryViewModel extends ViewModel {
    private MutableLiveData<String> mTimeRange = new MutableLiveData<>();

    public TopHistoryViewModel() {};

    public LiveData<String> getTimeRange() {
        return mTimeRange;
    }

    public void setTimeRange(String range) {
        this.mTimeRange.setValue(range);
    }
}
