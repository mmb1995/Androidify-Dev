package com.example.android.androidify.viewmodel;

import android.util.Log;

import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.model.Event;
import com.example.android.androidify.model.EventObserver;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.repository.SpotifyRepo;
import com.example.android.androidify.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class TrackListViewModel extends ViewModel {
    private static final String TAG = "TRACK_VIEW_MODEL";

    private MediatorLiveData<ApiResponse<List<MusicListItem>>> mTracks;
    private LiveData<ApiResponse<List<MusicListItem>>> mTracksWithoutSavedStatus;
    private MutableLiveData<String> mRange;
    private LiveData<ApiResponse<List<MusicListItem>>> mTopTracks;
    private MutableLiveData<Event<String>> mSelectedTrackId;


    private SpotifyRepo mRepo;

    @Inject
    public TrackListViewModel(SpotifyRepo repo) {
        this.mRepo = repo;
    }

    public void init(String id, String type) {
        mSelectedTrackId = new MutableLiveData<>();
        mTracks = new MediatorLiveData<>();

        switch (type) {
            case Constants.ALBUM:
                mTracksWithoutSavedStatus = mRepo.getAlbumTracks(id);
                break;
            case Constants.ARTIST:
                mTracksWithoutSavedStatus = mRepo.getTopTracksByArtist(id);
                break;
            case Constants.RECENTLY_PLAYED:
                mTracksWithoutSavedStatus = mRepo.getRecentlyPlayedTracks();
                break;
        }
        mTracks.addSource(mTracksWithoutSavedStatus, response -> {
            if (response != null) {
                switch (response.status) {
                    case SUCCESS:
                        mTracks.removeSource(mTracksWithoutSavedStatus);
                        mTracks.addSource(mRepo.checkForSavedTracks(response.data), value -> mTracks.setValue(value));
                        break;
                    default:
                        mTracks.setValue(response);
                }
            }
        });
        mTracks.addSource(mSelectedTrackId, new EventObserver<String>(trackId -> onToggleTrack(trackId)));
    }

    public void initUserTopTracks() {
        if (mSelectedTrackId == null) {
            mSelectedTrackId = new MutableLiveData<>();
        }

        if (mRange == null) {
            mRange = new MutableLiveData<>();
        }

        if (mTracks == null) {
            mTracks = new MediatorLiveData<>();
            mTracks.addSource(mRange, range -> getTopTracks(range));
            mTracks.addSource(mSelectedTrackId, new EventObserver<String>(id -> onToggleTrack(id)));
            if (mRange.getValue() == null) {
                setTimeRange(Constants.MEDIUM_TERM);
            } else {
                Log.i(TAG, "range = " + mRange.getValue());
                setTimeRange(mRange.getValue());
            }
        }
    }

    public LiveData<ApiResponse<List<MusicListItem>>> getTracks() {
        return mTracks;
    }

    public void toggleTrackSaveStatus(String id) {
        mSelectedTrackId.setValue( new Event<String>(id));
    }

    public void onToggleTrack(String id) {
        LiveData<ApiResponse<Boolean>> isSaved = mRepo.isTrackSaved(id);
        mTracks.addSource(isSaved, value -> { ;
            if (value != null) {
                if (value.status == ApiResponse.Status.SUCCESS) {
                    mTracks.removeSource(isSaved);
                    LiveData<ApiResponse<Void>> trackUpdate;
                    if (value.data == true) {
                        trackUpdate = mRepo.removeTrack(id);
                    } else {
                        trackUpdate = mRepo.saveTrack(id);
                    }
                    mTracks.addSource(trackUpdate, response -> {
                        if (response != null) {
                            //Log.e(TAG, response.error);
                        }
                        if (response != null && response.status == ApiResponse.Status.SUCCESS) {
                            mTracks.removeSource(trackUpdate);
                            List<MusicListItem> tracks = mTracks.getValue().data;
                            if (tracks != null) {
                                for (MusicListItem track : tracks) {
                                    if (track.id.equals(id)) {
                                        track.isLiked = !track.isLiked;
                                        break;
                                    }
                                }
                                mTracks.setValue(mTracks.getValue());
                            }
                        }
                    });
                }
            }
        });
    }

    public void initTopTracks() {
        mRange = new MutableLiveData<>();
        mTopTracks = Transformations.switchMap(mRange, range -> {
            return mRepo.getUserTopTracks(range);
        });
    }

    private void getTopTracks(String range) {
        if (range != null && mTracks != null) {
            LiveData<ApiResponse<List<MusicListItem>>> topTracks = mRepo.getUserTopTracks(range);
            getSavedTracks(topTracks);
        }
    }

    private void getRecentlyPlayed() {
        if (mTracks != null) {
            LiveData<ApiResponse<List<MusicListItem>>> recentlyPlayed = mRepo.getRecentlyPlayedTracks();
            getSavedTracks(recentlyPlayed);
        }
    }

    private void getSavedTracks(LiveData<ApiResponse<List<MusicListItem>>> source) {
        mTracks.addSource(source, response -> {
            if (response != null && response.status == ApiResponse.Status.SUCCESS) {
                mTracks.removeSource(source);
                LiveData<ApiResponse<List<MusicListItem>>> savedTracks = mRepo.checkForSavedTracks(response.data);
                mTracks.addSource(savedTracks, value -> {
                    if (value != null && value.status == ApiResponse.Status.SUCCESS) {
                        mTracks.removeSource(savedTracks);
                    };
                    mTracks.setValue(value);
                });
            } else {
                //mTracks.setValue(response);
            }
        });
    }

    public void setTimeRange(String range) {
        this.mRange.setValue(range);
    }

    public LiveData<ApiResponse<List<MusicListItem>>> getTopTracks() {
        return mTopTracks;
    }
}


/**
 public LiveData<ApiResponse<Boolean[]>> checkTracks(List<MusicListItem> tracks) {
 return mRepo.containsTracks(tracks);
 }

 public LiveData<ApiResponse<Void>> saveTrack(String id) {
 return this.mRepo.saveTrack(id);
 }

 public LiveData<ApiResponse<Void>> removeTrack(String id) {
 return this.mRepo.removeTrack(id);
 }
 }   **/