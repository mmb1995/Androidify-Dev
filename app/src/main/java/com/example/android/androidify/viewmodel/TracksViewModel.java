package com.example.android.androidify.viewmodel;

import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.model.RecommendationOptions;
import com.example.android.androidify.repository.SpotifyRepo;
import com.example.android.androidify.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class TracksViewModel extends ViewModel {

    private LiveData<ApiResponse<List<MusicListItem>>> mTracksByArtist;
    private LiveData<ApiResponse<List<MusicListItem>>> mTracksByAlbum;
    private LiveData<ApiResponse<List<MusicListItem>>> mTracksByRecommendation;
    private SpotifyRepo mRepo;

    public TracksViewModel() {}

    @Inject
    public TracksViewModel(SpotifyRepo repo) {
        this.mRepo = repo;
    }

    public LiveData<ApiResponse<List<MusicListItem>>> getTracks(String id, String type) {
        switch (type) {
            case Constants.ALBUM:
                return getTracksByAlbum(id);
            case Constants.ARTIST:
                return getTracksByArtist(id);
            case Constants.RECOMMEND_FROM_TRACKS:
                return getTracksByRecommendation(id);
            default:
                return null;
        }
    }

    private LiveData<ApiResponse<List<MusicListItem>>> getTracksByRecommendation(String id) {
        if (mTracksByRecommendation == null) {
            RecommendationOptions options = new RecommendationOptions();
            options.setSeedTracks(id);
            mTracksByRecommendation = mRepo.getRecommendations(options);
        }

        return mTracksByRecommendation;
    }

    private LiveData<ApiResponse<List<MusicListItem>>> getTracksByAlbum(String id) {
        if (mTracksByAlbum == null) {
            mTracksByAlbum = mRepo.getAlbumTracks(id);
        }
        return mTracksByAlbum;
    }

    private LiveData<ApiResponse<List<MusicListItem>>> getTracksByArtist(String id) {
        if (mTracksByArtist == null) {
            mTracksByArtist = mRepo.getTopTracksByArtist(id);
        }
        return mTracksByArtist;
    }

}
