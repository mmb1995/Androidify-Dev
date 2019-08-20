package com.example.android.androidify.viewmodel;

import android.util.Log;

import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.api.models.Artist;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.repository.SpotifyRepo;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

public class ArtistViewModel extends ViewModel {
    private static final String TAG = "ARTIST_VIEW_MODEL";

    private LiveData<ApiResponse<Artist>> mArtist;
    private LiveData<ApiResponse<List<MusicListItem>>> mRelatedArtists;
    private MediatorLiveData<Boolean> mIsFollowingArtist;
    private SpotifyRepo mSpotifyRepo;

    @Inject
    public ArtistViewModel(SpotifyRepo repo) {
        this.mSpotifyRepo = repo;
    }


    public void init(String id) {
        if (mArtist == null) {
            Log.i(TAG, "getting artist data");
            mArtist = mSpotifyRepo.getArtist(id);
        }

        if (mIsFollowingArtist == null) {
            mIsFollowingArtist = new MediatorLiveData<>();
            LiveData<ApiResponse<Boolean>> followStatus = mSpotifyRepo.isFollowingArtist(id);
            mIsFollowingArtist.addSource(followStatus, response -> {
                if (response != null && response.status == ApiResponse.Status.SUCCESS) {
                    mIsFollowingArtist.removeSource(followStatus);
                    mIsFollowingArtist.setValue(response.data);
                }
            });
        }
    }

    public LiveData<ApiResponse<Artist>> getArtist() {
        return mArtist;
    }

    public LiveData<ApiResponse<List<MusicListItem>>> getRelatedArtists(String artistId) {
        if (mRelatedArtists == null) {
            mRelatedArtists = mSpotifyRepo.getRelatedArtists(artistId);
        }
        return mRelatedArtists;
    }

    public LiveData<Boolean> getArtistFollowStatus() {
        return mIsFollowingArtist;
    }

    public void toggleArtistFollow(String id) {
        LiveData<ApiResponse<Boolean>> following = mSpotifyRepo.isFollowingArtist(id);
        mIsFollowingArtist.addSource(following, isFollowing -> {
            if (isFollowing != null) {
                if (isFollowing.status == ApiResponse.Status.SUCCESS) {
                    mIsFollowingArtist.removeSource(following);
                    LiveData<ApiResponse<Void>> updateArtist;
                    boolean followStatus;
                    if (isFollowing.data) {
                        updateArtist = mSpotifyRepo.unfollowArtist(id);
                        followStatus = false;
                    } else {
                        updateArtist = mSpotifyRepo.followArtist(id);
                        followStatus = true;
                    }
                    mIsFollowingArtist.addSource(updateArtist, response -> {
                        if (response != null && response.status == ApiResponse.Status.SUCCESS) {
                            mIsFollowingArtist.removeSource(updateArtist);
                            mIsFollowingArtist.setValue(followStatus);
                        }
                    });
                }
            }
        });
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

}
