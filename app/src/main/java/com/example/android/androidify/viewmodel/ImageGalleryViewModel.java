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

public class ImageGalleryViewModel extends ViewModel {
    private static final String TAG = "IMG_GALLERY_VM";

    private LiveData<ApiResponse<List<MusicListItem>>> mRelatedArtists;
    private LiveData<ApiResponse<List<MusicListItem>>> mTopArtists;
    private LiveData<ApiResponse<List<MusicListItem>>> mAlbums;
    private MutableLiveData<String> mRange;

    private SpotifyRepo mRepo;

    @Inject
    public ImageGalleryViewModel(SpotifyRepo repo) {
        this.mRepo = repo;
    }

    public void initTopArtists() {
        if (mRange == null) {
            mRange = new MutableLiveData<>();
        }

        if (mTopArtists == null) {
            mTopArtists = Transformations.switchMap(mRange, range -> mRepo.getUserTopArtists(range));

            if (mRange.getValue() == null) {
                // Set default value for range
                setTimeRange(Constants.MEDIUM_TERM);
            } else {
                setTimeRange(mRange.getValue());
            }
        }
    }

    public LiveData<ApiResponse<List<MusicListItem>>> getRelatedArtists(String id) {
        if (mRelatedArtists == null) {
            mRelatedArtists = mRepo.getRelatedArtists(id);
        }

        return mRelatedArtists;
    }

    public LiveData<ApiResponse<List<MusicListItem>>> getTopArtists() {
        return mTopArtists;
    }

    public LiveData<ApiResponse<List<MusicListItem>>> getAlbums(String id) {
        if (mAlbums == null) {
            Log.i(TAG, "Getting albums");
            mAlbums = mRepo.getAlbumsByArtist(id);
        }

        return mAlbums;
    }

    public void setTimeRange(String range) {
        if (mRange != null) {
            mRange.setValue(range);
        }
    }

}
