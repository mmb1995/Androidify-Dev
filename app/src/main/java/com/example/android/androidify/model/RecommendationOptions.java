package com.example.android.androidify.model;

import com.example.android.androidify.utils.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendationOptions {

    private Map<String, Object> options;

    public RecommendationOptions() {
        options = new HashMap<>();
    }

    public void setSeedTracks(String id) {
        options.put(Constants.SEED_TRACKS, id);
    }

    public void setSeedTracks(List<MusicListItem> tracks) {
        String trackIds = getIdList(tracks);
        options.put(Constants.SEED_TRACKS, trackIds);
    }

    public void setSeedArtists(MusicListItem artist) {
        options.put(Constants.SEED_ARTISTS, artist.id);
    }

    public void setSeedArtists(List<MusicListItem> artists) {
        String artistIds = getIdList(artists);
        options.put(Constants.SEED_TRACKS, artistIds);
    }

/*    public void setSeedGenres(List<MusicListItem> genres) {
        String trackIds = getIdList(tracks);
        options.put(Constants.SEED_TRACKS, trackIds);
    }*/

    public Map<String, Object> getOptions() {
        return options;
    }

    private String getIdList(List<MusicListItem> items) {
        if (items == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(items.size() + items.size() - 1);
        for (int i = 0; i < items.size() - 1; i++) {
            MusicListItem item = items.get(i);
            builder.append(item.id + ",");
        }

        builder.append(items.get(items.size() - 1).id);
        return builder.toString();
    }
}
