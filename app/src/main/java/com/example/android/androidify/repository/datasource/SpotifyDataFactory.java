package com.example.android.androidify.repository.datasource;

import com.example.android.androidify.api.SpotifyWebService;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

public abstract class SpotifyDataFactory<RequestType, ResultType> extends DataSource.Factory {

    private MutableLiveData<SpotifyDataSource> sourceLiveData;
    private SpotifyDataSource<RequestType, ResultType> spotifyDataSource;
    private SpotifyWebService apiService;

    public SpotifyDataFactory() {
        this.sourceLiveData = new MutableLiveData<SpotifyDataSource>();
    }

    @NonNull
    @Override
    public DataSource create() {
        spotifyDataSource = createDataSource();
        sourceLiveData.postValue(spotifyDataSource);
        return spotifyDataSource;
    }

    public MutableLiveData<SpotifyDataSource> getSourceLiveData() {
        return sourceLiveData;
    }

    protected abstract SpotifyDataSource<RequestType, ResultType> createDataSource();
}


/*public class SpotifyDataFactory extends DataSource.Factory {

    private MutableLiveData<SpotifyDataSource> sourceLiveData;
    private SpotifyDataSource spotifyDataSource;
    private SpotifyWebService apiService;

    @Inject
    public SpotifyDataFactory(SpotifyWebService service) {
        this.apiService = service;
        this.sourceLiveData = new MutableLiveData<SpotifyDataSource>();
    }

    @NonNull
    @Override
    public DataSource create() {
        spotifyDataSource = new SpotifyDataSource(apiService);
        sourceLiveData.postValue(spotifyDataSource);
        return spotifyDataSource;
    }

    public MutableLiveData<SpotifyDataSource> getSourceLiveData() {
        return sourceLiveData;
    }
}*/
