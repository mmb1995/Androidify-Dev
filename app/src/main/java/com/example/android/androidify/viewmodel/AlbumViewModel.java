package com.example.android.androidify.viewmodel;

import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.api.models.Album;
import com.example.android.androidify.repository.SpotifyRepo;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class AlbumViewModel extends ViewModel {
    private LiveData<ApiResponse<Album>> mAlbum;
    private MutableLiveData<String> mAlbumId;
    private SpotifyRepo mRepo;

    @Inject
    public AlbumViewModel(SpotifyRepo repo) {
        this.mRepo = repo;
    }

    public void init() {
        mAlbumId = new MutableLiveData<>();
        mAlbum = Transformations.switchMap(mAlbumId, id -> {
            return mRepo.getAlbum(id);
        });
    }

    public LiveData<ApiResponse<Album>> getAlbum(String id) {
        if (mAlbum == null) {
            mAlbum = mRepo.getAlbum(id);
        }
        return mAlbum;
    }

    /**
    public LiveData<ApiResponse<Album>> getAlbum() {
        return mAlbum;
    }
    **/

    private void setAlbumId(String id) {
        if (mAlbumId != null) {
            mAlbumId.setValue(id);
        }
    }

}
