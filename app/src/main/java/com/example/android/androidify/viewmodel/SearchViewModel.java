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

public class SearchViewModel extends ViewModel {
    private static final String TAG = "SEARCH_VIEW_MODEL";

/*    private LiveData<ApiResponse<List<MusicListItem>>> mTrackSearchResults;
    private LiveData<ApiResponse<List<MusicListItem>>> mArtistSearchResults;*/
 /*   private LiveData<ApiResponse<List<MusicListItem>>> mSearchResults;*/
    private LiveData<ApiResponse<List<MusicListItem>>> mAlbumSearchResults;
    private LiveData<ApiResponse<List<MusicListItem>>> mArtistSearchResults;
    private LiveData<ApiResponse<List<MusicListItem>>> mTrackSearchResults;
    private final MutableLiveData<String> mSearchQuery = new MutableLiveData<>();
    private final SpotifyRepo mRepo;


    @Inject
    public SearchViewModel(SpotifyRepo repo) {
        this.mRepo = repo;
    }

/*    public void init(String query, String type) {
        if (mSearchResults == null) {
            Log.i(TAG, "Getting search results");
            mSearchResults = mRepo.getSearchResultsByType(query, type);
        }
    }*/

    public LiveData<ApiResponse<List<MusicListItem>>> getAlbumSearchResults() {
        if (mAlbumSearchResults == null) {
            mAlbumSearchResults = Transformations.switchMap(mSearchQuery, query ->
                    mRepo.getSearchResultsByType(query, Constants.ALBUM));
        }

        return mAlbumSearchResults;
    }

    public LiveData<ApiResponse<List<MusicListItem>>> getArtistSearchResults() {
        if (mArtistSearchResults == null) {
            mArtistSearchResults = Transformations.switchMap(mSearchQuery, query ->
                    mRepo.getSearchResultsByType(query, Constants.ARTIST));
        }

        return mArtistSearchResults;
    }

    public LiveData<ApiResponse<List<MusicListItem>>> getTrackSearchResults() {
        if (mTrackSearchResults == null) {
            mTrackSearchResults = Transformations.switchMap(mSearchQuery, query ->
                    mRepo.getSearchResultsByType(query, Constants.TRACK));
        }

        return mTrackSearchResults;
    }

/*    public LiveData<ApiResponse<List<MusicListItem>>> getSearchResults() {
        return mSearchResults;
    }*/

    public void setQuery(String query) {
        Log.i(TAG, query);
        mSearchQuery.setValue(query);
    }

/*    public LiveData<ApiResponse<List<MusicListItem>>> getTrackSearchResults(String query) {
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
    }*/


}
