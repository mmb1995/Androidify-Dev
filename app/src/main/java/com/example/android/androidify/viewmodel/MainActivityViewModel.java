package com.example.android.androidify.viewmodel;

import com.example.android.androidify.model.Event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {
    private final MutableLiveData<String> currentlyPlaying = new MutableLiveData<>();
    private MutableLiveData<Event<String>> currentArtistId = new MutableLiveData<>();

    public MainActivityViewModel() {};

    /**
     * Returns the currently selected artist
     * @return
     */
    public LiveData<Event<String>> getCurrentArtistId() {
        return currentArtistId;
    }

    /**
     * Sets the new artistId for the selected artist
     * @param artistId
     */
    public void setCurrentArtistId(String artistId) {
        currentArtistId.setValue(new Event(artistId));
    }

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
