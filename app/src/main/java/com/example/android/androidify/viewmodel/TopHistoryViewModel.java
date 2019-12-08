package com.example.android.androidify.viewmodel;

import android.util.Log;

import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.repository.SpotifyRepo;
import com.example.android.androidify.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class TopHistoryViewModel extends ViewModel {
    private static final String TAG = "TOP_HISTORY_VM";

    private final MutableLiveData<String> mTimeRange = new MutableLiveData<>();
    private LiveData<ApiResponse<List<MusicListItem>>> mTopTracks;
    private LiveData<ApiResponse<List<MusicListItem>>> mTopArtists;
    private LiveData<ApiResponse<List<MusicListItem>>> mRecentlyPlayed;
    private MutableLiveData<Boolean> loadData = new MutableLiveData<>();
    private final SpotifyRepo mRepo;

    @Inject
    public TopHistoryViewModel(SpotifyRepo repo) { this.mRepo = repo;}

    public LiveData<ApiResponse<List<MusicListItem>>> getTopTracks() {
        if (mTopTracks == null) {
            Log.i(TAG, "Fetching top tracks");
           /* mTopTracks = mRepo.getUserTopTracks(Constants.MEDIUM_TERM);*/
            mTopTracks = Transformations.switchMap(mTimeRange, range -> mRepo.getUserTopTracks(range));
        }
        return mTopTracks;
    }

    public LiveData<ApiResponse<List<MusicListItem>>> getTopArtists() {
        if (mTopArtists == null) {
            /*mTopArtists = mRepo.getUserTopArtists(Constants.MEDIUM_TERM);*/
            mTopArtists = Transformations.switchMap(mTimeRange, range -> mRepo.getUserTopArtists(range));
        }
        return mTopArtists;
    }

    public LiveData<ApiResponse<List<MusicListItem>>> getRecentlyPlayed() {
        if (mRecentlyPlayed == null) {
            Log.i(TAG, "fetching recently played");
            mRecentlyPlayed = mRepo.getRecentlyPlayedTracks();
        }
        return mRecentlyPlayed;
    }

    public LiveData<String> getTimeRange() {
        if (mTimeRange.getValue() == null) {
            setTimeRange(Constants.MEDIUM_TERM);
        }

        return mTimeRange;
    }

    public int getSelectedPosition() {
        if (mTimeRange.getValue() == null) {
            return -1;
        }

        switch (mTimeRange.getValue()) {
            case Constants.LONG_TERM:
                return 0;
            case Constants.MEDIUM_TERM:
                return 1;
            case Constants.SHORT_TERM:
                return 2;
            default:
                return -1;
        }
    }

    public void setTimeRange(String range) {
        Log.i(TAG, "setting range = " + range);
        mTimeRange.setValue(range);
    }

    public void refreshData() {
        loadData.setValue(true);
    }
}
