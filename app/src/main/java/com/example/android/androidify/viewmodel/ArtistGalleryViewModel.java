package com.example.android.androidify.viewmodel;

import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.repository.SpotifyRepo;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class ArtistGalleryViewModel extends ViewModel {
    private LiveData<ApiResponse<List<MusicListItem>>> mRelatedArtists;
    private LiveData<ApiResponse<List<MusicListItem>>> mTopArtists;
    private MutableLiveData<String> mTimeRange;

    private SpotifyRepo mRepo;

    @Inject
    public ArtistGalleryViewModel(SpotifyRepo repo) {
        this.mRepo = repo;
    }

    public void initTopArtists() {
        mTimeRange = new MutableLiveData<>();
        mTopArtists = Transformations.switchMap(mTimeRange, range -> {
           return mRepo.getUserTopArtists(range);
        });
    }

    public LiveData<ApiResponse<List<MusicListItem>>> getArtistsById(String id) {
        if (mRelatedArtists == null) {
            mRelatedArtists = mRepo.getRelatedArtists(id);
        }
        return mRelatedArtists;
    }


    public LiveData<ApiResponse<List<MusicListItem>>> getTopArtists() {
        return mTopArtists;
    }

    public void setTimeRange(String timeRange) {
        this.mTimeRange.setValue(timeRange);
    }
}
