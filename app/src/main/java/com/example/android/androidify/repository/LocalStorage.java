package com.example.android.androidify.repository;

import android.content.Context;

public class LocalStorage {
    private Context context;

    private final String ACCESS_TOKEN = "access_token";

    public LocalStorage(Context context) {
        this.context = context;
    }

    /**
     * Store accessToken returned by Spotify
     * @param token
     */
    public void setAccessToken(String token) {
        context.getSharedPreferences("sharedprefs", Context.MODE_PRIVATE)
                .edit().putString(ACCESS_TOKEN, token).apply();
    }

    public String getAccessToken() {
        return context.getSharedPreferences("sharedprefs", Context.MODE_PRIVATE)
                .getString(ACCESS_TOKEN, null);
    }
}
