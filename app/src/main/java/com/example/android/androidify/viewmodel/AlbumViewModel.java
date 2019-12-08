package com.example.android.androidify.viewmodel;

import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.api.models.Album;
import com.example.android.androidify.model.Action;
import com.example.android.androidify.repository.SpotifyRepo;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class AlbumViewModel extends ViewModel {
    private LiveData<ApiResponse<Album>> mAlbum;
    private MutableLiveData<String> mAlbumId;
    private MediatorLiveData<Boolean> isAlbumSaved;
    private final SpotifyRepo mRepo;

    @Inject
    public AlbumViewModel(SpotifyRepo repo) {
        this.mRepo = repo;
    }

    public void init(String albumId) {
        if (mAlbumId == null) {
            mAlbumId = new MutableLiveData<>();
        }

        if (mAlbum == null) {
            mAlbum = Transformations.switchMap(mAlbumId, id -> mRepo.getAlbum(id));
        }

        if (isAlbumSaved == null) {
  /*          isAlbumSaved = new MediatorLiveData<>();
            isAlbumSaved.addSource(mAlbumId, this::handleFavoriteCheck);*/
        }

        if (mAlbumId.getValue() == null) {
            setAlbumId(albumId);
        }

    }

    public LiveData<ApiResponse<Album>> getAlbum() {
        return mAlbum;
    }

    public LiveData<Boolean> getAlbumSavedStatus() {
        return isAlbumSaved;
    }

    private void setAlbumId(String id) {
        if (mAlbumId != null) {
            mAlbumId.setValue(id);
        }
    }

    public void toggleAlbum(String id) {
        LiveData<ApiResponse<Action>> updateAction = mRepo.updateAlbum(id);
        isAlbumSaved.addSource(updateAction, response -> {
            if (response != null && response.status == ApiResponse.Status.SUCCESS) {
                isAlbumSaved.removeSource(updateAction);
                assert response.data != null;
                if (response.data.type == Action.Type.SAVE) {
                    isAlbumSaved.setValue(true);
                } else if(response.data.type == Action.Type.REMOVE) {
                    isAlbumSaved.setValue(false);
                }
            } else if (response != null && response.status == ApiResponse.Status.ERROR) {
                isAlbumSaved.removeSource(updateAction);
            }
        });
    }

    private void handleFavoriteCheck(String id) {
        LiveData<ApiResponse<Boolean>> savedStatus = mRepo.isAlbumSaved(id);
        isAlbumSaved.addSource(savedStatus, response -> {
            switch (response.status) {
                case SUCCESS:
                    isAlbumSaved.setValue(response.data);
                    isAlbumSaved.removeSource(savedStatus);
                    break;
                case ERROR:
                    isAlbumSaved.removeSource(savedStatus);
                    break;
            }
        });
    }
}
