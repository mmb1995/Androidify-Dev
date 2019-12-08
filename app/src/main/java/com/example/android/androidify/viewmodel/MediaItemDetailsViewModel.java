package com.example.android.androidify.viewmodel;

import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.api.models.Track;
import com.example.android.androidify.repository.SpotifyRepo;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class MediaItemDetailsViewModel extends ViewModel {
    private LiveData<ApiResponse<Track>> mTrack;
    private MutableLiveData<String> mId;
    private MediatorLiveData<Boolean> mTrackSavedStatus;
    private SpotifyRepo mRepo;

    @Inject
    public MediaItemDetailsViewModel(SpotifyRepo repo) {
        this.mRepo = repo;
    }

    public void init(String id) {
        if (mId == null) {
            mId = new MutableLiveData<>();
        }
        if (mTrack == null) {
            mTrack = Transformations.switchMap(mId, trackId -> mRepo.getTrack(trackId));
        }
        if (mTrackSavedStatus == null) {
            mTrackSavedStatus = new MediatorLiveData<>();
            mTrackSavedStatus.addSource(mId, this::checkTrack);
        }

        if (mId.getValue() == null || !mId.getValue().equals(id)) {
            setId(id);
        }
    }

    public LiveData<ApiResponse<Track>> getTrack()  {
        return mTrack;
    }

    public LiveData<Boolean> getTrackSaveStatus() {
        return mTrackSavedStatus;
    }

    private void setId(String id) {
        this.mId.setValue(id);
    }

    public void toggleTrack() {
        if (mId != null && mId.getValue() != null) {
            String id = mId.getValue();
            LiveData<ApiResponse<Boolean>> savedStatus =  checkSaveStatus(id);
            mTrackSavedStatus.addSource(savedStatus, response -> {
                if (response != null) {
                    switch (response.status) {
                        case SUCCESS:
                            mTrackSavedStatus.removeSource(savedStatus);
                            boolean saved = response.data;
                            if (saved) {
                                removeTrack(id);
                            } else {
                                saveTrack(id);
                            }
                            break;
                        case ERROR:
                            mTrackSavedStatus.removeSource(savedStatus);
                            break;
                    }
                }
            });
        }
    }

    private void checkTrack(String id) {
        LiveData<ApiResponse<Boolean>> savedStatus = checkSaveStatus(id);
        mTrackSavedStatus.addSource(savedStatus, response -> {
            if (response != null) {
                switch (response.status) {
                    case SUCCESS:
                        mTrackSavedStatus.setValue(response.data);
                        mTrackSavedStatus.removeSource(savedStatus);
                        break;
                    case ERROR:
                        mTrackSavedStatus.removeSource(savedStatus);
                        break;
                }
            }
        });
    }

    private void saveTrack(String id) {
        LiveData<ApiResponse<Void>> updateTrackStatus = mRepo.saveTrackToLibrary(id);
        mTrackSavedStatus.addSource(updateTrackStatus, response -> {
            if (response != null) {
                switch (response.status) {
                    case SUCCESS:
                        mTrackSavedStatus.setValue(true);
                        mTrackSavedStatus.removeSource(updateTrackStatus);
                        break;
                    case ERROR:
                        mTrackSavedStatus.removeSource(updateTrackStatus);
                        break;
                }
            }
        });
    }

    private void removeTrack(String id) {
        LiveData<ApiResponse<Void>> updateTrackStatus = mRepo.removeTrackFromLibrary(id);
        mTrackSavedStatus.addSource(updateTrackStatus, response -> {
            if (response != null) {
                switch (response.status) {
                    case SUCCESS:
                        mTrackSavedStatus.setValue(false);
                        mTrackSavedStatus.removeSource(updateTrackStatus);
                        break;
                    case ERROR:
                        mTrackSavedStatus.removeSource(updateTrackStatus);
                        break;
                }
            }
        });
    }

    private LiveData<ApiResponse<Boolean>> checkSaveStatus(String id) {
        return mRepo.isTrackSaved(id);
    }
}
