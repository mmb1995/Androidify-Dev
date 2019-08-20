package com.example.android.androidify.viewmodel;

import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.api.models.Track;
import com.example.android.androidify.repository.SpotifyRepo;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TracksViewModel extends ViewModel {

    private SpotifyRepo mRepo;
    private MutableLiveData<String> mType;
    private MutableLiveData<String> mRange;
    private MutableLiveData<String> mId;
    private MediatorLiveData<ApiResponse<List<Track>>> mTracks;

    public TracksViewModel() {}

    /**
    public init(String type, String range) {
        mTracks = new MediatorLiveData<>();
        mTracks.addSource(mType, tracks -> );
    }
     **/

    @Inject
    public TracksViewModel(SpotifyRepo repo) {
        this.mRepo = repo;
    }

}
