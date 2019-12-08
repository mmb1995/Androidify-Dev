package com.example.android.androidify.viewmodel;

import android.util.Log;

import com.example.android.androidify.api.PagedApiResponse;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.repository.SpotifyRepo;
import com.example.android.androidify.repository.datasource.NetworkState;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagedList;

public class LibraryViewModel extends ViewModel {
    private static final String TAG = "LIBRARY_VIEW_MODEL";

    private PagedApiResponse<MusicListItem> mPlaylistData;
    private PagedApiResponse<MusicListItem> mSavedAlbums;
    private PagedApiResponse<MusicListItem> mFollowedArtists;
    private PagedApiResponse<MusicListItem> mSavedTracks;

    private final SpotifyRepo mRepo;

    @Inject
    public LibraryViewModel(SpotifyRepo repo) {
        this.mRepo = repo;
    }

    public LiveData<PagedList<MusicListItem>> getSavedTracks() {
        if (mSavedTracks == null) {
            mSavedTracks = mRepo.getSavedTracks();
        }

        return mSavedTracks.getPagedListLiveData();
    }

    public LiveData<NetworkState> getTrackNetworkState() {
        if (mSavedTracks != null) {
            return mSavedTracks.getNetworkStateLiveData();
        }

        return null;
    }

    public LiveData<PagedList<MusicListItem>> getUserPlaylists() {
        if (mPlaylistData == null) {
            Log.i(TAG, "Getting user playlists");
            mPlaylistData = mRepo.getPlaylists();
        }

        return mPlaylistData.getPagedListLiveData();
    }

    public LiveData<NetworkState> getPlaylistNetworkState() {
        if (mPlaylistData != null) {
            return mPlaylistData.getNetworkStateLiveData();
        }

        return null;
    }

    public LiveData<PagedList<MusicListItem>> getSavedAlbums() {
        if (mSavedAlbums == null) {
            mSavedAlbums = mRepo.getSavedAlbums();
        }

        return mSavedAlbums.getPagedListLiveData();
    }

    public LiveData<NetworkState> getAlbumNetworkState() {
        if (mSavedAlbums != null) {
            return mSavedAlbums.getNetworkStateLiveData();
        }

        return null;
    }

    public LiveData<PagedList<MusicListItem>> getFollowedArtists() {
        if (mFollowedArtists == null) {
            mFollowedArtists = mRepo.getFollowedArtists();
        }

        return mFollowedArtists.getPagedListLiveData();
    }

    public LiveData<NetworkState> getArtistNetworkState() {
        if (mFollowedArtists != null) {
            return mFollowedArtists.getNetworkStateLiveData();
        }

        return null;
    }
}
