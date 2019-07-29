package com.example.android.androidify.repository;

import android.util.Log;

import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.api.SpotifyApiResource;
import com.example.android.androidify.api.SpotifyWebService;
import com.example.android.androidify.api.models.Artist;
import com.example.android.androidify.api.models.ArtistTrackWrapper;
import com.example.android.androidify.api.models.Pager;
import com.example.android.androidify.api.models.RelatedArtistsWrapper;
import com.example.android.androidify.api.models.Track;
import com.example.android.androidify.api.models.TrackWrapper;
import com.example.android.androidify.model.MusicListItem;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpotifyRepo {
    private static final String TAG = "SPOTIFY_REPO";
    private final SpotifyWebService apiService;

    @Inject
    public SpotifyRepo(SpotifyWebService service) {
        this.apiService = service;
    }

    /** Artist Endpoints **/

    public LiveData<Artist> getArtist(String id) {
        final MutableLiveData<Artist> data = new MutableLiveData<>();
        apiService.getArtist(id).enqueue(new Callback<Artist>() {
            @Override
            public void onResponse(Call<Artist> call, Response<Artist> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<Artist> call, Throwable t) {

            }
        });
        return data;
    }


    public LiveData<ApiResponse<List<MusicListItem>>> getTopTracksByArtist(String id) {
        return new SpotifyApiResource<ArtistTrackWrapper, List<MusicListItem>>() {
            @Override
            protected Call<ArtistTrackWrapper> createCall() {
                return apiService.getArtistTopTracks(id);
            }

            @Override
            protected List<MusicListItem> processResponse(@Nullable ArtistTrackWrapper data) {
                List<MusicListItem> items = parseTracks(data.tracks);
                return items;
            }
        }.getAsLiveData();
    }


    public LiveData<ApiResponse<List<MusicListItem>>> getRelatedArtists(String id) {
        return new SpotifyApiResource<RelatedArtistsWrapper, List<MusicListItem>>() {
            @Override
            protected Call<RelatedArtistsWrapper> createCall() {
                return apiService.getRelatedArtists(id);
            }

            @Override
            protected List<MusicListItem> processResponse(@Nullable RelatedArtistsWrapper data) {
                List<MusicListItem> items = parseArtists(data.artists);
                return items;
            }
        }.getAsLiveData();
    }

    public MutableLiveData<Boolean> isFollowingArtist(String id) {
        final MutableLiveData<Boolean> data = new MutableLiveData<>();
        apiService.isFollowingArtist(id).enqueue(new Callback<Boolean[]>() {
            @Override
            public void onResponse(Call<Boolean[]> call, Response<Boolean[]> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body()[0]);
                } else {
                    onFailure(call, new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<Boolean[]> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
        return data;
    }

    public LiveData<ApiResponse<java.lang.Void>> followArtist(String id) {
        return new SpotifyApiResource<java.lang.Void, java.lang.Void>() {
            @Override
            protected Call<java.lang.Void> createCall() {
                return apiService.followArtist(id);
            }

            @Override
            protected java.lang.Void processResponse(@Nullable java.lang.Void data) {
                return data;
            }
        }.getAsLiveData();
    }

    public LiveData<ApiResponse<java.lang.Void>> unfollowArtist(String id) {
        return new SpotifyApiResource<java.lang.Void, java.lang.Void>() {
            @Override
            protected Call<java.lang.Void> createCall() { return  apiService.unfollowArtist(id); }

            @Override
            protected java.lang.Void processResponse(@Nullable java.lang.Void data) {
                return data;
            }
        }.getAsLiveData();
    }

    /** Track endpoints **/

    public LiveData<ApiResponse<Boolean[]>> containsTracks(List<MusicListItem> tracks) {
        String ids = getIdList(tracks);
        return new SpotifyApiResource<Boolean[], Boolean[]>() {
            @Override
            protected Call<Boolean[]> createCall() {
                return apiService.containsTrack(ids);
            }

            @Override
            protected Boolean[] processResponse(@Nullable Boolean[] data) {
                return data;
            }
        }.getAsLiveData();
    }

    public LiveData<ApiResponse<Void>> saveTrack(String id) {
        return new SpotifyApiResource<Void, Void>() {
            @Override
            protected Call<Void> createCall() {
                return apiService.saveTracks(id);
            }

            @Override
            protected Void processResponse(@Nullable Void data) {
                return data;
            }
        }.getAsLiveData();
    }

    public LiveData<ApiResponse<java.lang.Void>> removeTrack(String id) {
        return new SpotifyApiResource<Void, Void>() {
            @Override
            protected Call<Void> createCall() {
                return apiService.removeTracks(id);
            }

            @Override
            protected Void processResponse(@Nullable Void data) {
                return data;
            }
        }.getAsLiveData();
    }



    /** Top history endpoints **/

    public LiveData<List<Track>> getRecentlyPlayed() {

        final MutableLiveData<List<Track>> data = new MutableLiveData<>();
        apiService.getRecentlyPlayed().enqueue(new Callback<Pager<TrackWrapper>>() {
            @Override
            public void onResponse(Call<Pager<TrackWrapper>> call, Response<Pager<TrackWrapper>> response) {
                Log.i(TAG, response.toString());
                List<Track> tracks = parseTracks(response.body());
                data.setValue(tracks);
            }

            @Override
            public void onFailure(Call<Pager<TrackWrapper>> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
        return data;
    }

    public LiveData<ApiResponse<List<MusicListItem>>> getRecentlyPlayedTracks() {
        return new SpotifyApiResource<Pager<TrackWrapper>, List<MusicListItem>>() {
            @Override
            protected Call<Pager<TrackWrapper>> createCall() {
                return apiService.getRecentlyPlayed();
            }

            @Override
            protected List<MusicListItem> processResponse(@Nullable Pager<TrackWrapper> data) {
                List<MusicListItem> tracks = parseTrackWrapper(data);
                return tracks;
            }
        }.getAsLiveData();
    }

    public LiveData<List<Artist>> getTopArtists() {
        final MutableLiveData<List<Artist>> data = new MutableLiveData<>();
        apiService.getTopArtists().enqueue(new Callback<Pager<Artist>>() {
            @Override
            public void onResponse(Call<Pager<Artist>> call, Response<Pager<Artist>> response) {
                Log.i(TAG, response.toString());
                data.setValue(response.body().items);
            }

            @Override
            public void onFailure(Call<Pager<Artist>> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
        return data;
    }

    public LiveData<List<Track>> getTopTracks() {
        final MutableLiveData<List<Track>> data = new MutableLiveData<>();
        apiService.getTopTracks().enqueue(new Callback<Pager<Track>>() {
            @Override
            public void onResponse(Call<Pager<Track>> call, Response<Pager<Track>> response) {
                Log.i(TAG, response.toString());
                data.setValue(response.body().items);
            }

            @Override
            public void onFailure(Call<Pager<Track>> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
        return data;
    }

    public LiveData<ApiResponse<List<MusicListItem>>> getUserTopTracks() {
        return new SpotifyApiResource<Pager<Track>, List<MusicListItem>>() {
            @Override
            protected Call<Pager<Track>> createCall() {
                return apiService.getTopTracks();
            }

            @Override
            protected List<MusicListItem> processResponse(@Nullable Pager<Track> data) {
                List<MusicListItem> tracks = parseTracks(data.items);
                return tracks;
            }
        }.getAsLiveData();
    }

    public LiveData<List<Track>> getSavedTracks() {
        final MutableLiveData<List<Track>> data = new MutableLiveData<>();
        apiService.getSavedTracks().enqueue(new Callback<Pager<TrackWrapper>>() {
            @Override
            public void onResponse(Call<Pager<TrackWrapper>> call, Response<Pager<TrackWrapper>> response) {
                Log.i(TAG, response.toString());
                List<Track> tracks = parseTracks(response.body());
                data.setValue(tracks);
            }

            @Override
            public void onFailure(Call<Pager<TrackWrapper>> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
        return data;
    }

    /** helpers **/

    private List<Track> parseTracks(Pager<TrackWrapper> trackPager) {
        List<Track> tracks = new ArrayList<>();
        for (TrackWrapper trackWrapper : trackPager.items) {
            if (trackWrapper != null) {
                tracks.add(trackWrapper.track);
            }
        }
        return tracks;
    }

    private List<MusicListItem> parseTrackWrapper(Pager<TrackWrapper> trackWrapperPager) {
        List<MusicListItem> tracks = new ArrayList<>();
        for (TrackWrapper trackWrapper : trackWrapperPager.items) {
            if (trackWrapper != null) {
                tracks.add(new MusicListItem(trackWrapper.track));
            }
        }
        return tracks;
    }

    private List<MusicListItem> parseTracks(List<Track> tracks) {
        ArrayList<MusicListItem> tracksList = new ArrayList<>();

        for (Track track: tracks) {
            MusicListItem item = new MusicListItem(track);
            tracksList.add(item);
        }
        return tracksList;
    }

    private List<MusicListItem> parseArtists(List<Artist> artists) {
        ArrayList<MusicListItem> artistsList = new ArrayList<>();

        for (Artist artist: artists) {
            MusicListItem item = new MusicListItem(artist);
            artistsList.add(item);
        }
        return artistsList;
    }

    private String getIdList(List<MusicListItem> items) {
        StringBuilder builder = new StringBuilder(items.size() + items.size() - 1);
        for (int i = 0; i < items.size() - 1; i++) {
            MusicListItem item = items.get(i);
            builder.append(item.id + ",");
        }
        builder.append(items.get(items.size() - 1).id);
        Log.i(TAG, builder.toString());
        return builder.toString();
    }


}
