package com.example.android.androidify.viewmodel;

import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.model.Action;
import com.example.android.androidify.model.Event;
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

public class RecommendationsViewModel extends ViewModel {

    private MediatorLiveData<ApiResponse<List<MusicListItem>>> mRecommendations;
    private MutableLiveData<Event<String>> mSelectedTrackId = new MutableLiveData<>();
    private MutableLiveData<Event<Action>> mSnackbarMessageEvent = new MutableLiveData<>();
    private SpotifyRepo mRepo;

    @Inject
    public RecommendationsViewModel(SpotifyRepo repo) {
        this.mRepo = repo;
    }

    public void init(String id, String type) {
        if (mRecommendations == null) {
            switch (type) {
                case Constants.TRACK:
                    mRecommendations = new MediatorLiveData<>();
                    RecommendationOptions options = new RecommendationOptions();
                    options.setSeedTracks(id);
                    LiveData<ApiResponse<List<MusicListItem>>> mDataWithoutSavedStatus =
                            mRepo.getRecommendations(options);
                    //mRecommendations.addSource(mDataWithoutSavedStatus -> response)
                    break;
            }
        }
    }

    public LiveData<ApiResponse<List<MusicListItem>>> getRecommendations() {
        return mRecommendations;
    }

    public LiveData<Event<Action>> getSnackbarEvent() {
        return mSnackbarMessageEvent;
    }

    private void setSnackbarMessageEvent(Action snackbarAction) {
        mSnackbarMessageEvent.setValue(new Event(snackbarAction));
    }

    public void onToggleTrack(String id) {
        LiveData<ApiResponse<Action>> updateAction = mRepo.updateTrack(id);
        mRecommendations.addSource(updateAction, response -> {
            if (response != null && response.status == ApiResponse.Status.SUCCESS) {
                mRecommendations.removeSource(updateAction);
                boolean liked = response.data.type == Action.Type.SAVE;
                updateTrackStatus(id, liked);
            } else if (response != null && response.status == ApiResponse.Status.ERROR) {
                mRecommendations.removeSource(updateAction);
            }
        });
    }

    private void updateTrackStatus(String id, boolean isLiked) {
        if (mRecommendations != null && mRecommendations.getValue() != null) {
            List<MusicListItem> tracks = mRecommendations.getValue().data;
            for (int i = 0; i < tracks.size(); i++) {
                MusicListItem track = tracks.get(i);
                if (track != null && track.id.equals(id)) {
                    MusicListItem newTrack = new MusicListItem(track, isLiked);
                    tracks.set(i, newTrack);
                }
            }
            mRecommendations.setValue(ApiResponse.success(tracks));
            Action action = isLiked ? Action.save() : Action.remove();
            setSnackbarMessageEvent(action);
        }
    }
}
