package com.example.android.androidify.api;

import com.example.android.androidify.repository.datasource.NetworkState;

import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

public class PagedApiResponse<T> {

    private LiveData<PagedList<T>> pagedListLiveData;
    private LiveData<NetworkState> networkStateLiveData;

    public PagedApiResponse(LiveData<PagedList<T>> pagedListLiveData, LiveData<NetworkState> networkStateLiveData) {
        this.pagedListLiveData = pagedListLiveData;
        this.networkStateLiveData = networkStateLiveData;
    }

    public LiveData<NetworkState> getNetworkStateLiveData() {
        return networkStateLiveData;
    }

    public LiveData<PagedList<T>> getPagedListLiveData() {
        return pagedListLiveData;
    }
}
