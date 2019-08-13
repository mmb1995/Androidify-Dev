package com.example.android.androidify.repository;

import android.content.SearchRecentSuggestionsProvider;

public class SpotifySearchSuggestionsProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.example.android.androidify.repository.SpotifySearchSuggestionsProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SpotifySearchSuggestionsProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
