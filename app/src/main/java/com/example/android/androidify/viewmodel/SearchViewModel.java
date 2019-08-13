package com.example.android.androidify.viewmodel;

import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.repository.SpotifyRepo;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class SearchViewModel extends ViewModel {
    private LiveData<ApiResponse<List<MusicListItem>>> mTrackSearchResults;
    private LiveData<ApiResponse<List<MusicListItem>>> mArtistSearchResults;
    private LiveData<ApiResponse<List<MusicListItem>>> mSearchResults;
    private SpotifyRepo mRepo;

    @Inject
    public SearchViewModel(SpotifyRepo repo) {
        this.mRepo = repo;
    }

    public LiveData<ApiResponse<List<MusicListItem>>> getTrackSearchResults(String query) {
        if (mTrackSearchResults == null) {
            mTrackSearchResults = mRepo.getTrackSearchResults(query);
        }
        return mTrackSearchResults;
    }

    public LiveData<ApiResponse<List<MusicListItem>>> getArtistSearchResults(String query) {
        if (mArtistSearchResults == null) {
            mArtistSearchResults = mRepo.getArtistSearchResults(query);
        }
        return mArtistSearchResults;
    }

    public LiveData<ApiResponse<List<MusicListItem>>> getCombinedSearchResults(String query) {
        if (mSearchResults == null) {
            mSearchResults = mRepo.getSearchResults(query);
        }
        return mSearchResults;
    }


}
