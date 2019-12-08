package com.example.android.androidify.viewmodel;

import android.util.Log;

import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.api.models.Artist;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.repository.SpotifyRepo;
import com.example.android.androidify.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class ArtistViewModel extends ViewModel {
    private static final String TAG = "ARTIST_VIEW_MODEL";

    private LiveData<ApiResponse<Artist>> mArtist;
    private LiveData<ApiResponse<List<MusicListItem>>> mRelatedArtists;
    private LiveData<ApiResponse<List<MusicListItem>>> mArtistAlbums;
    private final SpotifyRepo mSpotifyRepo;

    @Inject
    public ArtistViewModel(SpotifyRepo repo) {
        this.mSpotifyRepo = repo;
    }


    public void init(String id) {
        if (mArtist == null) {
            Log.i(TAG, "getting artist data");
            mArtist = mSpotifyRepo.getArtist(id);
        }
    }

    public LiveData<ApiResponse<Artist>> getArtist() {
        return mArtist;
    }

    public LiveData<ApiResponse<List<MusicListItem>>> getArtistResource(String id, String type) {
        switch (type) {
            case Constants.TYPE_RELATED_ARTISTS:
                return getRelatedArtists(id);
            case Constants.TYPE_ARTIST_ALBUMS:
                return getArtistAlbums(id);
            default:
                return null;
        }
    }

    private LiveData<ApiResponse<List<MusicListItem>>> getRelatedArtists(String artistId) {
        if (mRelatedArtists == null) {
            mRelatedArtists = mSpotifyRepo.getRelatedArtists(artistId);
        }
        return mRelatedArtists;
    }

    private LiveData<ApiResponse<List<MusicListItem>>> getArtistAlbums(String artistId) {
        if (mArtistAlbums == null) {
            mArtistAlbums = mSpotifyRepo.getAlbumsByArtist(artistId);
        }
        return mArtistAlbums;
    }

}
