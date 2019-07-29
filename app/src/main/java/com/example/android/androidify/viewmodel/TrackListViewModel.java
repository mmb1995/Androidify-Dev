package com.example.android.androidify.viewmodel;

import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.repository.SpotifyRepo;
import com.example.android.androidify.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class TrackListViewModel extends ViewModel {
    private LiveData<ApiResponse<List<MusicListItem>>> mTracks;

    private SpotifyRepo mRepo;

    @Inject
    public TrackListViewModel(SpotifyRepo repo) { this.mRepo = repo; }

    public LiveData<ApiResponse<List<MusicListItem>>> getTracks(String id, String type) {
        if (type.equals(Constants.ARTIST)) {
            if (mTracks == null) {
                mTracks = mRepo.getTopTracksByArtist(id);
            }
            return mTracks;
        } else {
            return null;
        }
    }

    public LiveData<ApiResponse<Boolean[]>> checkTracks() {
        return mRepo.containsTracks(mTracks.getValue().data);
    }

    public LiveData<ApiResponse<Void>> saveTrack(String id) {
        return this.mRepo.saveTrack(id);
    }

    public LiveData<ApiResponse<Void>> removeTrack(String id) {
        return this.mRepo.removeTrack(id);
    }


}
