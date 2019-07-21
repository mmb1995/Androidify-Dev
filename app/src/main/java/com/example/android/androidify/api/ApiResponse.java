package com.example.android.androidify.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ApiResponse<T> {
    @NonNull
    public final Status status;
    @Nullable
    public final T data;
    @Nullable public final String error;
    private ApiResponse(@NonNull Status status, @Nullable T data,
                     @Nullable String message) {
        this.status = status;
        this.data = data;
        this.error = message;
    }

    public static <T> ApiResponse<T> success(@NonNull T data) {
        return new ApiResponse(Status.SUCCESS, data, null);
    }

    public static <T> ApiResponse<T> error(String error) {
        return new ApiResponse(Status.ERROR, null, error);
    }

    public static <T> ApiResponse<T> loading() {
        return new ApiResponse(Status.LOADING, null, null);
    }

    public enum Status { SUCCESS, ERROR, LOADING }
}
