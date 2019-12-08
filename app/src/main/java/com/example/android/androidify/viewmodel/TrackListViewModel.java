package com.example.android.androidify.viewmodel;

import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.model.Action;
import com.example.android.androidify.model.Event;
import com.example.android.androidify.model.EventObserver;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.model.RecommendationOptions;
import com.example.android.androidify.repository.SpotifyRepo;
import com.example.android.androidify.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TrackListViewModel extends ViewModel {
    private static final String TAG = "TRACK_VIEW_MODEL";

    private MediatorLiveData<ApiResponse<List<MusicListItem>>> mTracks;
    private LiveData<ApiResponse<List<MusicListItem>>> mTracksWithoutSavedStatus;
    private MutableLiveData<Event<String>> mSelectedTrackId = new MutableLiveData<>();
    private MutableLiveData<Event<Action>> mSnackbarMessageEvent = new MutableLiveData<>();
    private final SpotifyRepo mRepo;

    @Inject
    public TrackListViewModel(SpotifyRepo repo) {
        this.mRepo = repo;
    }

    public void init(String id, String type) {
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
            case Constants.PLAYLIST:
                /*mTracksWithoutSavedStatus = mRepo.getPlaylistTracks(id);*/
                break;
            case Constants.RECOMMEND_FROM_TRACKS:
                RecommendationOptions options = new RecommendationOptions();
                options.setSeedTracks(id);
                mTracksWithoutSavedStatus = mRepo.getRecommendations(options);
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


    public LiveData<ApiResponse<List<MusicListItem>>> getTracks() {
        return mTracks;
    }

    public void toggleTrackSaveStatus(String id) {
        mSelectedTrackId.setValue( new Event<String>(id));
    }

    public LiveData<Event<Action>> getSnackbarEvent() {
        return mSnackbarMessageEvent;
    }

    private void setSnackbarMessageEvent(Action snackbarAction) {
        mSnackbarMessageEvent.setValue(new Event<Action>(snackbarAction));
    }


    public void onToggleTrack(String id) {
        LiveData<ApiResponse<Action>> updateAction = mRepo.updateTrack(id);
        mTracks.addSource(updateAction, response -> {
            if (response != null && response.status == ApiResponse.Status.SUCCESS) {
                mTracks.removeSource(updateAction);
                boolean liked = response.data.type == Action.Type.SAVE;
                updateTrackStatus(id, liked);
            } else if (response != null && response.status == ApiResponse.Status.ERROR) {
                mTracks.removeSource(updateAction);
            }
        });
    }

    private void updateTrackStatus(String id, boolean isLiked) {
        if (mTracks != null && mTracks.getValue() != null) {
            List<MusicListItem> tracks = mTracks.getValue().data;
            for (int i = 0; i < tracks.size(); i++) {
                MusicListItem track = tracks.get(i);
                if (track != null && track.id.equals(id)) {
                    MusicListItem newTrack = new MusicListItem(track, isLiked);
                    tracks.set(i, newTrack);
                }
            }
            mTracks.setValue(ApiResponse.success(tracks));
            Action action = isLiked ? Action.save() : Action.remove();
            setSnackbarMessageEvent(action);
        }
    }
}