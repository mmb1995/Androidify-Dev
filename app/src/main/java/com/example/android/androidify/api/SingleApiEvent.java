package com.example.android.androidify.api;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class SingleApiEvent<T> {

    private MutableLiveData<ApiResponse<T>> data = new MutableLiveData<>();

    public SingleApiEvent() {
        makeCall();
    }

    private void makeCall() {
        createCall().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    data.setValue(ApiResponse.success(null));
                } else {
                    if (response.code() == 401) {
                        data.setValue(ApiResponse.unauthorized());
                    } else {
                        onFailure(call, null);
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                data.setValue(ApiResponse.error("Failed"));
            }
        });
    }

    public final LiveData<ApiResponse<T>> getAsLiveData() { return data; }

    protected abstract Call<Void> createCall();


}
