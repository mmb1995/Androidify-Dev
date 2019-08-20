package com.example.android.androidify.base;

import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.repository.SpotifyRepo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class BaseViewModel<T> extends ViewModel {
    protected LiveData<ApiResponse<T>> mData;

    protected SpotifyRepo mRepo;

    public BaseViewModel(){};

    public BaseViewModel(SpotifyRepo repo) {
        this.mRepo = repo;
    }

    public LiveData<ApiResponse<T>> getData() {
        return mData;
    };

}
