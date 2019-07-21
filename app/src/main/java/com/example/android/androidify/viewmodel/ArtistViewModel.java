package com.example.android.androidify.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.api.models.Artist;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.repository.SpotifyRepo;

import java.util.ArrayList;

import javax.inject.Inject;

public class ArtistViewModel extends ViewModel {
    private static final String TAG = "ARTIST_VIEW_MODEL";

    private LiveData<Artist> mArtist;
    private LiveData<ArrayList<MusicListItem>> mArtistTopTracks;
    private LiveData<ArrayList<MusicListItem>> mRelatedArtists;
    private LiveData<Boolean> mIsFollowingArtist;
    private final SpotifyRepo mSpotifyRepo;

    @Inject
    public ArtistViewModel(SpotifyRepo repo) {
        this.mSpotifyRepo = repo;
    }

    public LiveData<Artist> getArtist(String artistId) {
        if (mArtist == null) {
            mArtist = mSpotifyRepo.getArtist(artistId);
        }
        return mArtist;
    }

    public LiveData<ArrayList<MusicListItem>> getArtistTopTracks(String artistId) {
        if (mArtistTopTracks == null) {
            mArtistTopTracks = mSpotifyRepo.getTopTracksByArtist(artistId);
        }
        return mArtistTopTracks;
    }

    public LiveData<ArrayList<MusicListItem>> getRelatedArtists(String artistId) {
        if (mRelatedArtists == null) {
            mRelatedArtists = mSpotifyRepo.getRelatedArtists(artistId);
        }
        return mRelatedArtists;
    }

    public LiveData<Boolean> getArtistFollowStatus(String artistId) {
        if (mIsFollowingArtist == null) {
            mIsFollowingArtist = mSpotifyRepo.isFollowingArtist(artistId);
        }
        return mIsFollowingArtist;
    }

    public LiveData<ApiResponse<Void>> followArtist(String artistId) {
        return mSpotifyRepo.followArtist(artistId);
    }


}
