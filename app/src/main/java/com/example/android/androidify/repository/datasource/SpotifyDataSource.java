package com.example.android.androidify.repository.datasource;

import android.util.Log;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class SpotifyDataSource<RequestType, ResultType> extends PageKeyedDataSource<String, ResultType> {

    private static final String TAG = "SPOTIFY_DATA_SOURCE";

    private MutableLiveData networkState;
    private MutableLiveData initialLoading;

    public SpotifyDataSource() {

        networkState = new MutableLiveData();
        initialLoading = new MutableLiveData();
    }

    public MutableLiveData getNetworkState() {
        return networkState;
    }

    public MutableLiveData getInitialLoading() {
        return initialLoading;
    }

    @Override
    public void loadInitial(@NonNull PageKeyedDataSource.LoadInitialParams<String> params, @NonNull LoadInitialCallback<String, ResultType> callback) {
        Log.i(TAG, "INITIAL LOAD");
        initialLoading.postValue(NetworkState.LOADING);
        networkState.postValue(NetworkState.LOADING);

        createInitialCall(params.requestedLoadSize).enqueue(new Callback<RequestType>() {
            @Override
            public void onResponse(Call<RequestType> call, Response<RequestType> response) {
                if (response.isSuccessful()) {
                    callback.onResult(processResponse(response.body()), null, getKey(response.body()));
                    initialLoading.postValue(NetworkState.LOADED);
                    networkState.postValue(NetworkState.LOADED);
                }  else {
                    initialLoading.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                    networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));

                }
            }

            @Override
            public void onFailure(Call<RequestType> call, Throwable t) {
                String errorMessage = t == null ? "unknown error" : t.getMessage();
                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
            }
        });

    }

    @Override
    public void loadBefore(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, ResultType> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, ResultType> callback) {
        networkState.postValue(NetworkState.LOADING);

        int pageSize = params.requestedLoadSize;

        Log.i(TAG, "loading data url = " + params.key);

        getNextPage(params.key).enqueue(new Callback<RequestType>() {
            @Override
            public void onResponse(Call<RequestType> call, Response<RequestType> response) {
                if (response.isSuccessful()) {
                    callback.onResult(processResponse(response.body()), getKey(response.body()));
                    networkState.postValue(NetworkState.LOADED);
                } else networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
            }

            @Override
            public void onFailure(Call<RequestType> call, Throwable t) {
                String errorMessage = t == null ? "unknown error" : t.getMessage();
                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
            }
        });
    }

    private Integer getNewKey(int offset, int limit, int total) {
        return (offset + limit <= total) ? offset + limit : null;
    }

    protected abstract Call<RequestType> createInitialCall(int pageSize);
    protected abstract Call<RequestType> getNextPage(String url);
    protected abstract String getKey(RequestType data);
    protected abstract List<ResultType> processResponse(RequestType data);
}


/*
public abstract class SpotifyDataSource<RequestType, ResultType> extends PageKeyedDataSource<String, ResultType> {

    private static final String TAG = "SPOTIFY_DATA_SOURCE";

    private MutableLiveData networkState;
    private MutableLiveData initialLoading;

    public SpotifyDataSource() {

        networkState = new MutableLiveData();
        initialLoading = new MutableLiveData();
    }

    public MutableLiveData getNetworkState() {
        return networkState;
    }

    public MutableLiveData getInitialLoading() {
        return initialLoading;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<String> params, @NonNull LoadInitialCallback<String, ResultType> callback) {
        Log.i(TAG, "INITIAL LOAD");
        initialLoading.postValue(NetworkState.LOADING);
        networkState.postValue(NetworkState.LOADING);

        createInitialCall(params.requestedLoadSize).enqueue(new Callback<Pager<RequestType>>() {
            @Override
            public void onResponse(Call<Pager<RequestType>> call, Response<Pager<RequestType>> response) {
                if (response.isSuccessful()) {
                    callback.onResult(processResponse(response.body()), null, response.body().next);
                    initialLoading.postValue(NetworkState.LOADED);
                    networkState.postValue(NetworkState.LOADED);
                }  else {
                    initialLoading.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                    networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));

                }
            }

            @Override
            public void onFailure(Call<Pager<RequestType>> call, Throwable t) {
                String errorMessage = t == null ? "unknown error" : t.getMessage();
                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
            }
        });

    }

    @Override
    public void loadBefore(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, ResultType> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, ResultType> callback) {
        networkState.postValue(NetworkState.LOADING);

        int pageSize = params.requestedLoadSize;

        Log.i(TAG, "loading data url = " + params.key);

        getNextPage(params.key).enqueue(new Callback<Pager<RequestType>>() {
            @Override
            public void onResponse(Call<Pager<RequestType>> call, Response<Pager<RequestType>> response) {
                if (response.isSuccessful()) {
                    callback.onResult(processResponse(response.body()), response.body().next);
                    networkState.postValue(NetworkState.LOADED);
                } else networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
            }

            @Override
            public void onFailure(Call<Pager<RequestType>> call, Throwable t) {
                String errorMessage = t == null ? "unknown error" : t.getMessage();
                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
            }
        });
    }

    private Integer getNewKey(int offset, int limit, int total) {
        return (offset + limit <= total) ? offset + limit : null;
    }

    protected abstract Call<Pager<RequestType>> createInitialCall(int pageSize);
    protected abstract Call<Pager<RequestType>> getNextPage(String url);
    protected abstract List<ResultType> processResponse(Pager<RequestType> data);
}
*/

/*
public class SpotifyDataSource extends PageKeyedDataSource<Integer, MusicListItem> {
    private static final String TAG = "SPOTIFY_DATA_SOURCE";

    private MutableLiveData networkState;
    private MutableLiveData initialLoading;

    private final SpotifyWebService apiService;


    public SpotifyDataSource(SpotifyWebService apiService) {

        this.apiService = apiService;

        networkState = new MutableLiveData();
        initialLoading = new MutableLiveData();
    }

    public MutableLiveData getNetworkState() {
        return networkState;
    }

    public MutableLiveData getInitialLoading() {
        return initialLoading;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, MusicListItem> callback) {
        Log.i(TAG, "INITIAL LOAD");
        initialLoading.postValue(NetworkState.LOADING);
        networkState.postValue(NetworkState.LOADING);

        int pageSize = params.requestedLoadSize;
        int offset = 0;

        apiService.getSavedTracks(offset, pageSize)
                .enqueue(new Callback<Pager<TrackWrapper>>() {
                    @Override
                    public void onResponse(Call<Pager<TrackWrapper>> call, Response<Pager<TrackWrapper>> response) {
                        if (response.isSuccessful()) {
                            Integer nextKey = getNewKey(offset, pageSize, response.body().total);
                            callback.onResult(parseTrackWrapper(response.body()), null, nextKey);
                            initialLoading.postValue(NetworkState.LOADED);
                            networkState.postValue(NetworkState.LOADED);

                        } else {
                            initialLoading.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                            networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));

                        }
                    }

                    @Override
                    public void onFailure(Call<Pager<TrackWrapper>> call, Throwable t) {
                        String errorMessage = t == null ? "unknown error" : t.getMessage();
                        networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
                    }
                });
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, MusicListItem> callback) {
        networkState.postValue(NetworkState.LOADING);

        int pageSize = params.requestedLoadSize;
        int offset = params.key;

        Log.i(TAG, "loading data offset = " + offset);

        apiService.getSavedTracks(offset, pageSize)
                .enqueue(new Callback<Pager<TrackWrapper>>() {
                    @Override
                    public void onResponse(Call<Pager<TrackWrapper>> call, Response<Pager<TrackWrapper>> response) {
                        if (response.isSuccessful()) {
                            Integer nextKey = getNewKey(offset, pageSize, response.body().total);
                            callback.onResult(parseTrackWrapper(response.body()), nextKey);
                            networkState.postValue(NetworkState.LOADED);
                        } else networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                    }

                    @Override
                    public void onFailure(Call<Pager<TrackWrapper>> call, Throwable t) {
                        String errorMessage = t == null ? "unknown error" : t.getMessage();
                        networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
                    }
                });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, MusicListItem> callback) {

    }

    private Integer getNewKey(int offset, int limit, int total) {
        return (offset + limit <= total) ? offset + limit : null;
    }

    private List<MusicListItem> parseTrackWrapper(Pager<TrackWrapper> trackWrapperPager) {
        List<MusicListItem> tracks = new ArrayList<>();
        for (TrackWrapper trackWrapper : trackWrapperPager.items) {
            if (trackWrapper != null) {
                tracks.add(new MusicListItem(trackWrapper.track));
            }
        }
        return tracks;
    }
}
*/
