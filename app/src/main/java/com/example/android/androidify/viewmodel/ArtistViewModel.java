package com.example.android.androidify.viewmodel;

import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.api.models.Artist;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.repository.SpotifyRepo;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ArtistViewModel extends ViewModel {
    private static final String TAG = "ARTIST_VIEW_MODEL";

    private LiveData<Artist> mArtist;
    //private LiveData<ApiResponse<List<MusicListItem>>> mArtistTopTracks;
    private LiveData<ApiResponse<List<MusicListItem>>> mRelatedArtists;
    private MutableLiveData<Boolean> mIsFollowingArtist;
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

    /**
    public LiveData<ApiResponse<ArrayList<MusicListItem>>> getArtistTopTracks(String artistId) {
        if (mArtistTopTracks == null) {
            mArtistTopTracks = mSpotifyRepo.getTopTracksByArtist(artistId);
        }
        return mArtistTopTracks;
    }
     **/

    public LiveData<ApiResponse<List<MusicListItem>>> getRelatedArtists(String artistId) {
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

    public LiveData<ApiResponse<Void>> unfollowArtist(String artistId) {
        return mSpotifyRepo.unfollowArtist(artistId);
    }

    public void setIsFollowingArtist(Boolean following) {
        this.mIsFollowingArtist.setValue(following);
    }

    public Boolean getFollowing() {
        return mIsFollowingArtist.getValue();
    }

    public LiveData<ApiResponse<Boolean[]>> checkTracks(List<MusicListItem> tracks) {
        return mSpotifyRepo.containsTracks(tracks);
    }


}
