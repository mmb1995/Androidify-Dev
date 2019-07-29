package com.example.android.androidify.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MusicPlaybackViewModel extends ViewModel {
    private final MutableLiveData<String> currentlyPlaying = new MutableLiveData<>();

    public MusicPlaybackViewModel() {}

    /**
     * Sets the uri to play
     */
    public void setCurrentlyPlaying(String uri) {
        currentlyPlaying.setValue(uri);
    }

    /**
     * Returns the selected uri
     * @return
     */
    public LiveData<String> getCurrentlyPlaying() {
        return currentlyPlaying;
    }
}
