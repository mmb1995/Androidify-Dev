package com.example.android.androidify.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public class Session {
    private static final String TAG = "SESSION";

    private final String PREF_NAME = "spotify_pref";
    private final String ACCESS_TOKEN = "access_token";
    private final String EXPIRES_AT = "expires_At";

    private Context context;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String accessToken;

    public Session(Context context) {
        this.context = context;
        this.pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = pref.edit();
        if (isTokenValid()) {
            this.accessToken = pref.getString(ACCESS_TOKEN, null);
        } else {
            invalidate();
        }
    }

    public void setAccessToken(String accessToken, long expiresIn, TimeUnit unit) {
        if (accessToken != null) {
            Log.i(TAG, "token is not null");
            invalidate();
            this.accessToken = accessToken;
            long currentTime = SystemClock.elapsedRealtime();
            long expiresAt = currentTime + unit.toMillis(expiresIn);
            editor.putString(ACCESS_TOKEN, accessToken);
            editor.putLong(EXPIRES_AT, expiresAt);
            editor.apply();
        }
    }

    public String getToken() {
        return accessToken;
    }

/*    public String getAccessToken() {
        String token = pref.getString(ACCESS_TOKEN, null);
        Long expiresAt = pref.getLong(EXPIRES_AT, 0l);
        Log.i(TAG, "token = " + token);
        Log.i(TAG, "timeAcquired = " + expiresAt);
        if (token == null || expiresAt < SystemClock.elapsedRealtime()) {
            editor.remove(ACCESS_TOKEN);
            editor.remove(EXPIRES_AT);
            editor.apply();
            return null;
        }

        return token;
    }*/

    public boolean isTokenValid() {
        String token = pref.getString(ACCESS_TOKEN, null);
        Long expiresAt = pref.getLong(EXPIRES_AT, 0l);
        Log.i(TAG, "token = " + token);
        Log.i(TAG, "timeAcquired = " + expiresAt);
        return token != null && expiresAt >= SystemClock.elapsedRealtime();
    }

    public void invalidate() {
        this.accessToken = null;
        editor.clear();
        editor.commit();
    }
}
