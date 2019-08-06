package com.example.android.androidify.viewmodel;

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

public class TrackListViewModel extends ViewModel {
    private LiveData<ApiResponse<List<MusicListItem>>> mTracks;
    private MutableLiveData<String> mRange;
    private LiveData<ApiResponse<List<MusicListItem>>> mTopTracks;

    private SpotifyRepo mRepo;

    @Inject
    public TrackListViewModel(SpotifyRepo repo) { this.mRepo = repo; }

    public LiveData<ApiResponse<List<MusicListItem>>> getTracks(String id, String type, String range) {
        if (mTracks != null) {
            return mTracks;
        }

        switch (type) {
            case Constants.ARTIST:
                mTracks = mRepo.getTopTracksByArtist(id);
                break;
            case Constants.RECENTLY_PLAYED:
                mTracks = mRepo.getRecentlyPlayedTracks();
                break;
            case Constants.TOP_TRACKS:
                mTracks = mRepo.getUserTopTracks(range);
                break;
        }
        return mTracks;
    }

    public void initTopTracks() {
        mRange = new MutableLiveData<>();
        mTopTracks = Transformations.switchMap(mRange, range -> {
            return mRepo.getUserTopTracks(range);
        });
    }

    public void setTimeRange(String range) {
        this.mRange.setValue(range);
    }

    /**
    public LiveData<ApiResponse<List<MusicListItem>>> getTracksByRange(String range) {
        if (mTracks != null && mRange != null && mRange.equals(range)) {
            return mTracks;
        } else {
            //this.mRange = range;
            mTracks = mRepo.getUserTopTracks(range);
            return mTracks;
        }
    }
     **/

    public LiveData<ApiResponse<List<MusicListItem>>> getTopTracks() {
        return mTopTracks;
    }

    public LiveData<ApiResponse<Boolean[]>> checkTracks(List<MusicListItem> tracks) {
        return mRepo.containsTracks(tracks);
    }

    public LiveData<ApiResponse<Void>> saveTrack(String id) {
        return this.mRepo.saveTrack(id);
    }

    public LiveData<ApiResponse<Void>> removeTrack(String id) {
        return this.mRepo.removeTrack(id);
    }
}
