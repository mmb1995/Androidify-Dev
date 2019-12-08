package com.example.android.androidify.api;

import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.ErrorCallback;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public abstract class PlayerApiResource<ResultType> {

    private final ErrorCallback mErrorCallback = throwable -> {};
    private final MutableLiveData<ApiResponse<ResultType>> result = new MutableLiveData<>();

    public PlayerApiResource() {
        callPlayerApi();
    }

    private void callPlayerApi() {
        createCall().setResultCallback(resultType -> result.setValue(ApiResponse.success(resultType)))
                .setErrorCallback(throwable -> result.setValue(ApiResponse.error(throwable.getMessage())));
    }

    public final LiveData<ApiResponse<ResultType>> getAsLiveData() { return result;}

    protected abstract CallResult<ResultType> createCall();
}
