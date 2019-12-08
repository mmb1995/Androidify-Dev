package com.example.android.androidify.viewmodel;

import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.api.PagedApiResponse;
import com.example.android.androidify.api.models.Playlist;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.repository.SpotifyRepo;
import com.example.android.androidify.repository.datasource.NetworkState;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagedList;

public class PlaylistViewModel extends ViewModel {
    private MutableLiveData<String> mPlaylistId = new MutableLiveData<>();
    private LiveData<ApiResponse<Playlist>> mPlaylist;
    private PagedApiResponse<MusicListItem> pagedPlaylistTrackData;
    private final SpotifyRepo mRepo;

    @Inject
    public PlaylistViewModel(SpotifyRepo repo) {
        this.mRepo = repo;
        mPlaylist = Transformations.switchMap(mPlaylistId, id -> mRepo.getPlaylist(id));
    }

    public LiveData<ApiResponse<Playlist>> getPlaylist() {
        return mPlaylist;
    }

    public LiveData<PagedList<MusicListItem>> getPlaylistTracks(String id) {
        if (pagedPlaylistTrackData == null) {
            pagedPlaylistTrackData = mRepo.getPlaylistTracks(id);
        }

        return pagedPlaylistTrackData.getPagedListLiveData();
    }

    public LiveData<NetworkState> getPlaylistTracksNetworkState() {
        if (pagedPlaylistTrackData != null) {
            return pagedPlaylistTrackData.getNetworkStateLiveData();
        }

        return null;
    }

    public void setPlaylistId(String id) {
        if (mPlaylistId.getValue() == null || !mPlaylistId.getValue().equals(id)) {
            mPlaylistId.setValue(id);
        }
    }
}
