package com.example.android.androidify.api;

import com.example.android.androidify.model.Action;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class SpotifyLibraryActionResource {

    private final MutableLiveData<ApiResponse<Action>> result = new MediatorLiveData<>();

    public SpotifyLibraryActionResource() {
        checkStatus();
    }

    private void checkStatus() {
        createCheckCall().enqueue(new Callback<Boolean[]>() {
            @Override
            public void onResponse(Call<Boolean[]> call, Response<Boolean[]> response) {
                if (response != null && response.body() != null && response.isSuccessful()) {
                    performUpdate(response.body());
                } else {
                    if (response.code() == 401) {
                        result.setValue(ApiResponse.unauthorized());
                    } else {
                        onFailure(call, null);
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean[]> call, Throwable t) {
                result.setValue(ApiResponse.error(null));
            }
        });
    }

    private void performUpdate(Boolean[] statusArray) {
        Action action = getAction(statusArray);
        createUpdateCall(action).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response != null && response.isSuccessful()) {
                    result.setValue(ApiResponse.success(action));
                } else if (response.code() == 401) {
                    result.setValue(ApiResponse.unauthorized());
                } else {
                    onFailure(call, null);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(ApiResponse.error(null));
            }
        });
    }

    public final LiveData<ApiResponse<Action>> getAsLiveData() { return result; }

    protected abstract Call<Boolean[]> createCheckCall();
    protected abstract Call<Void> createUpdateCall(Action action);
    protected abstract Action getAction(Boolean[] statusArray);

}
