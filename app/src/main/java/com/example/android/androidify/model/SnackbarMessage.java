package com.example.android.androidify.model;

import com.example.android.androidify.R;

public class SnackbarMessage {

    public enum Type {
        POSITIVE,
        ERROR
    }

    private final Type type;
    private final Integer resourceId;

    public SnackbarMessage(Integer resourceId, Type type) {
        this.resourceId = resourceId;
        this.type = type;
    }

    public static SnackbarMessage create(Integer resourceId) {
        return new SnackbarMessage(resourceId, Type.POSITIVE);
    }

    public static SnackbarMessage error() {
        return new SnackbarMessage(R.string.error_message, Type.ERROR);
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public Type getType() {
        return type;
    }
}
