package com.example.android.androidify.repository;

import android.util.Log;

import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.api.SpotifyApiResource;
import com.example.android.androidify.api.SpotifyWebService;
import com.example.android.androidify.api.models.Album;
import com.example.android.androidify.api.models.Artist;
import com.example.android.androidify.api.models.ArtistTrackWrapper;
import com.example.android.androidify.api.models.ArtistsPager;
import com.example.android.androidify.api.models.Pager;
import com.example.android.androidify.api.models.RelatedArtistsWrapper;
import com.example.android.androidify.api.models.SearchResultsPager;
import com.example.android.androidify.api.models.Track;
import com.example.android.androidify.api.models.TrackWrapper;
import com.example.android.androidify.api.models.TracksPager;
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

    public LiveData<ApiResponse<Artist>> getArtist(String id) {
        return new SpotifyApiResource<Artist, Artist>() {
            @Override
            protected Call<Artist> createCall() {
                return apiService.getArtist(id);
            }

            @Override
            protected Artist processResponse(@Nullable Artist data) {
                return data;
            }
        }.getAsLiveData();
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


    public LiveData<ApiResponse<Boolean>> isFollowingArtist(String id) {
        return new SpotifyApiResource<Boolean[], Boolean>() {
            @Override
            protected Call<Boolean[]> createCall() {
                return apiService.isFollowingArtist(id);
            }

            @Override
            protected Boolean processResponse(@Nullable Boolean[] data) {
                return data[0];
            }
        }.getAsLiveData();
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

    /** Album endpoints **/

    public LiveData<ApiResponse<Album>> getAlbum(String id) {
        return new SpotifyApiResource<Album, Album>() {
            @Override
            protected Call<Album> createCall() {
                return apiService.getAlbum(id);
            }

            @Override
            protected Album processResponse(@Nullable Album data) {
                if (data == null) {
                    Log.i(TAG, "no album found");
                } else {
                    Log.i(TAG, data.name);
                }
                return data;
            }
        }.getAsLiveData();
    }

    public LiveData<ApiResponse<List<MusicListItem>>> getAlbumsByArtist(String id) {
        return new SpotifyApiResource<Pager<Album>, List<MusicListItem>>() {
            @Override
            protected Call<Pager<Album>> createCall() {
                return apiService.getArtistAlbums(id);
            }

            @Override
            protected List<MusicListItem> processResponse(@Nullable Pager<Album> data) {
                return parseAlbums(data.items);
            }
        }.getAsLiveData();
    }

    /** Track endpoints **/

    public LiveData<ApiResponse<Boolean>> isTrackSaved(String id) {
        return new SpotifyApiResource<Boolean[], Boolean>() {
            @Override
            protected Call<Boolean[]> createCall() {
                return apiService.containsTrack(id);
            }

            @Override
            protected Boolean processResponse(@Nullable Boolean[] data) {
                Log.i(TAG, "track saved = " + data[0]);
                return data[0];
            }
        }.getAsLiveData();
    }

    public LiveData<ApiResponse<List<MusicListItem>>> checkForSavedTracks(List<MusicListItem> tracks) {
        String ids = getIdList(tracks);
        return new SpotifyApiResource<Boolean[], List<MusicListItem>>() {
            @Override
            protected Call<Boolean[]> createCall() {
                return apiService.containsTrack(ids);
            }

            @Override
            protected List<MusicListItem> processResponse(@Nullable Boolean[] data) {
                return handleTrackLikeStatus(tracks, data);
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
                Log.i(TAG, "saved track");
                return data;
            }
        }.getAsLiveData();
    }

    public LiveData<ApiResponse<Void>> removeTrack(String id) {
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

    public LiveData<ApiResponse<List<MusicListItem>>> getAlbumTracks(String albumId) {
        return new SpotifyApiResource<Pager<Track>, List<MusicListItem>>() {
            @Override
            protected Call<Pager<Track>> createCall() {
                return apiService.getAlbumTracks(albumId);
            }

            @Override
            protected List<MusicListItem> processResponse(@Nullable Pager<Track> data) {
                Log.i(TAG, "" + data.total);
                return parseTracks(data.items);
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

    public LiveData<ApiResponse<List<MusicListItem>>> getUserTopArtists(String range) {
        return new SpotifyApiResource<Pager<Artist>, List<MusicListItem>>() {
            @Override
            protected Call<Pager<Artist>> createCall() {
                return apiService.getTopArtists(range);
            }

            @Override
            protected List<MusicListItem> processResponse(@Nullable Pager<Artist> data) {
                List<MusicListItem> artists = parseArtists(data.items);
                return artists;
            }
        }.getAsLiveData();
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

    public LiveData<ApiResponse<List<MusicListItem>>> getUserTopTracks(String timeRange) {
        return new SpotifyApiResource<Pager<Track>, List<MusicListItem>>() {
            @Override
            protected Call<Pager<Track>> createCall() {
                return apiService.getTopTracks(timeRange);
            }

            @Override
            protected List<MusicListItem> processResponse(@Nullable Pager<Track> data) {
                Log.i(TAG, timeRange);
                Log.i(TAG, "" + data.items.size());
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

    /** Search endpoints **/

    public LiveData<ApiResponse<List<MusicListItem>>> getArtistSearchResults(String query) {
        return new SpotifyApiResource<ArtistsPager, List<MusicListItem>>() {
            @Override
            protected Call<ArtistsPager> createCall() {
                return apiService.searchForArtists(query);
            }

            @Override
            protected List<MusicListItem> processResponse(@Nullable ArtistsPager data) {
                return parseArtists(data.artists.items);
            }
        }.getAsLiveData();
    }

    public LiveData<ApiResponse<List<MusicListItem>>> getTrackSearchResults(String query) {
        return new SpotifyApiResource<TracksPager, List<MusicListItem>>() {
            @Override
            protected Call<TracksPager> createCall() {
                return apiService.searchForTracks(query);
            }

            @Override
            protected List<MusicListItem> processResponse(@Nullable TracksPager data) {
                return parseTracks(data.tracks.items);
            }
        }.getAsLiveData();
    }

    public LiveData<ApiResponse<List<MusicListItem>>> getSearchResults(String query) {
        return new SpotifyApiResource<SearchResultsPager, List<MusicListItem>>() {
            @Override
            protected Call<SearchResultsPager> createCall() {
                return apiService.getSearchResults(query);
            }

            @Override
            protected List<MusicListItem> processResponse(@Nullable SearchResultsPager data) {
                return parseSearchResults(data);
            }
        }.getAsLiveData();
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

    /**
     * Helper method to convert a list of tracks into a list of MusicListItems
     * @param tracks list of Track objects
     * @return list of MusicListItem objects
     */
    private List<MusicListItem> parseTracks(List<Track> tracks) {
        ArrayList<MusicListItem> tracksList = new ArrayList<>();

        for (Track track: tracks) {
            MusicListItem item = new MusicListItem(track);
            tracksList.add(item);
        }
        return tracksList;
    }

    /**
     * Helper method to convert a list of artists into a list of MusicListItems
     * @param artists list of Artist objects
     * @return list of MusicListItem objects
     */
    private List<MusicListItem> parseArtists(List<Artist> artists) {
        ArrayList<MusicListItem> artistsList = new ArrayList<>();

        for (Artist artist: artists) {
            MusicListItem item = new MusicListItem(artist);
            artistsList.add(item);
        }
        return artistsList;
    }

    /**
     * Helper method to convert a list of albums into a list of MusicListItems
     * @param albums list of Album objects
     * @return list of MusicListItem objects
     */
    private List<MusicListItem> parseAlbums(List<Album> albums) {
        ArrayList<MusicListItem> albumsList = new ArrayList<>();

        for (Album album: albums) {
            MusicListItem item = new MusicListItem(album);
            albumsList.add(item);
        }
        return albumsList;
    }

    private List<MusicListItem> parseSearchResults(SearchResultsPager data) {
        List<MusicListItem> results = new ArrayList<>();
        List<MusicListItem> tracks = parseTracks(data.tracks.items);
        List<MusicListItem> artists = parseArtists(data.artists.items);
        results.addAll(tracks);
        results.addAll(artists);
        return results;
    }

    /**
     * Compares a list of tracks with an array of booleans and returns a new list with the tracks
     * updated saved status
     * @param tracks
     * @param likedTracks
     * @return
     */
    private List<MusicListItem> handleTrackLikeStatus(List<MusicListItem> tracks, Boolean[] likedTracks) {
        List<MusicListItem> updatedTracks = new ArrayList<>();

        for (int i = 0; i < likedTracks.length; i++) {
            MusicListItem item = tracks.get(i);
            item.isLiked = likedTracks[i];
            updatedTracks.add(item);
        }

        return updatedTracks;
    }

    public String getIdList(List<MusicListItem> items) {
        if (items == null) {
            return null;
        }
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
