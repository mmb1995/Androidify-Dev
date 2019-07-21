package com.example.android.androidify.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.Nullable;
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

    /**
    public LiveData<ArrayList<Track>> getTopTracksByArtist(String id) {
        final MutableLiveData<ArrayList<Track>> data = new MutableLiveData<>();
        apiService.getArtistTopTracks(id).enqueue(new Callback<ArtistTrackWrapper>() {
            @Override
            public void onResponse(Call<ArtistTrackWrapper> call, Response<ArtistTrackWrapper> response) {
                Log.i(TAG, "" + response);
                if (response.body() == null) {
                    data.setValue(null);
                } else {
                    data.setValue(response.body().tracks);
                }
            }

            @Override
            public void onFailure(Call<ArtistTrackWrapper> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
        return data;
    }
     **/

    public LiveData<ArrayList<MusicListItem>> getTopTracksByArtist(String id) {
        final MutableLiveData<ArrayList<MusicListItem>> data = new MutableLiveData<>();
        apiService.getArtistTopTracks(id).enqueue(new Callback<ArtistTrackWrapper>() {
            @Override
            public void onResponse(Call<ArtistTrackWrapper> call, Response<ArtistTrackWrapper> response) {
                if (response.isSuccessful()) {
                    List<Track> tracks = response.body().tracks;
                    ArrayList<MusicListItem> items = parseTracks(tracks);
                    Log.i(TAG, items.get(0).name);
                    data.setValue(items);
                } else {
                    onFailure(call, new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<ArtistTrackWrapper> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });

        return data;
    }

    public LiveData<ArrayList<MusicListItem>> getRelatedArtists(String id) {
        final MutableLiveData<ArrayList<MusicListItem>> data = new MutableLiveData<>();
        apiService.getRelatedArtists(id).enqueue(new Callback<RelatedArtistsWrapper>() {
            @Override
            public void onResponse(Call<RelatedArtistsWrapper> call, Response<RelatedArtistsWrapper> response) {
                if (response.isSuccessful()) {
                    List<Artist> artists = response.body().artists;
                    ArrayList<MusicListItem> items = parseArtists(artists);
                    data.setValue(items);
                } else {
                    onFailure(call, new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<RelatedArtistsWrapper> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });

        return data;
    }

    public LiveData<Boolean> isFollowingArtist(String id) {
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

    public LiveData<ApiResponse<Void>> followArtist(String id) {
        return new SpotifyApiResource<Void, Void>() {
            @Override
            protected Call<Void> createCall() {
                return apiService.followArtist(id);
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

    private List<Track> parseTracks(Pager<TrackWrapper> trackPager) {
        List<Track> tracks = new ArrayList<>();
        for (TrackWrapper trackWrapper : trackPager.items) {
            if (trackWrapper != null) {
                tracks.add(trackWrapper.track);
            }
        }
        return tracks;
    }

    private ArrayList<MusicListItem> parseTracks(List<Track> tracks) {
        ArrayList<MusicListItem> tracksList = new ArrayList<>();

        for (Track track: tracks) {
            MusicListItem item = new MusicListItem(track);
            tracksList.add(item);
        }
        return tracksList;
    }

    private ArrayList<MusicListItem> parseArtists(List<Artist> artists) {
        ArrayList<MusicListItem> artistsList = new ArrayList<>();

        for (Artist artist: artists) {
            MusicListItem item = new MusicListItem(artist);
            artistsList.add(item);
        }
        return artistsList;
    }


}
