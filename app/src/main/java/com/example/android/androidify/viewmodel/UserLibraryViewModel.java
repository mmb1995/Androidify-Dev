package com.example.android.androidify.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.android.androidify.api.models.Track;
import com.example.android.androidify.repository.SpotifyRepo;

import java.util.List;

import javax.inject.Inject;

public class UserLibraryViewModel extends ViewModel {
    private LiveData<List<Track>> mTopTracks;

    private final SpotifyRepo mRepo;

    @Inject
    public UserLibraryViewModel(SpotifyRepo repo) {
        this.mRepo = repo;
    }

    public LiveData<List<Track>> getSavedTracks() {
        if (mTopTracks == null) {
            mTopTracks = mRepo.getSavedTracks();
        }
        return mTopTracks;
    }
}
