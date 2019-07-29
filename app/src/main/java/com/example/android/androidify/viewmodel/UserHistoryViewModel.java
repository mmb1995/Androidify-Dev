package com.example.android.androidify.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.android.androidify.api.models.Artist;
import com.example.android.androidify.api.models.Track;
import com.example.android.androidify.repository.SpotifyRepo;

import java.util.List;

import javax.inject.Inject;

public class UserHistoryViewModel extends ViewModel {
    private LiveData<List<Track>> mTopHistory;
    private LiveData<List<Artist>> mTopArtists;

    private final SpotifyRepo mRepo;

    @Inject
    public UserHistoryViewModel(SpotifyRepo repo) {
        this.mRepo = repo;
    }

    public LiveData<List<Track>> getTopTracks() {
        if (mTopHistory == null) {
            mTopHistory = mRepo.getTopTracks();
        }
        return mTopHistory;
    }

    public LiveData<List<Track>> getRecentlyPlayed() {
        if (mTopHistory == null) {
            mTopHistory = mRepo.getRecentlyPlayed();
        }
        return mTopHistory;
    }

    public LiveData<List<Artist>> getTopArtists() {
        if (mTopArtists == null) {
            mTopArtists = mRepo.getTopArtists();
        }
        return mTopArtists;
    }
}
