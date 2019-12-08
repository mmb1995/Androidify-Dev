package com.example.android.androidify.api;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class SpotifyApiResource<RequestType, ResultType> {
    private static final String TAG = "API_RESPONSE";

    private final MutableLiveData<ApiResponse<ResultType>> result = new MutableLiveData<>();

    public SpotifyApiResource() {
        result.setValue(ApiResponse.loading());
        makeApiCall();
    }

    private void makeApiCall() {
        createCall().enqueue(new Callback<RequestType>() {
            @Override
            public void onResponse(Call<RequestType> call, Response<RequestType> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, response.message());
                    result.setValue(ApiResponse.success(processResponse(response.body())));
                } else {
                    Log.e(TAG, response.message());
                    if (response.code() == 401) {
                        result.setValue(ApiResponse.unauthorized());
                    } else {
                        onFailure(call, new Throwable(response.message()));
                    }
                }
            }

            @Override
            public void onFailure(Call<RequestType> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                result.setValue(ApiResponse.error(t.getMessage()));
            }
        });
    }


    public final LiveData<ApiResponse<ResultType>> getAsLiveData() {
        return result;
    }

    protected abstract Call<RequestType> createCall();

    protected abstract ResultType processResponse(@Nullable RequestType data);
}
