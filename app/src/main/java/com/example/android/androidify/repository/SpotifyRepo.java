package com.example.android.androidify.repository;

import android.util.Log;

import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.api.PagedApiResponse;
import com.example.android.androidify.api.SingleApiEvent;
import com.example.android.androidify.api.SpotifyApiResource;
import com.example.android.androidify.api.SpotifyLibraryActionResource;
import com.example.android.androidify.api.SpotifyWebService;
import com.example.android.androidify.api.models.Album;
import com.example.android.androidify.api.models.AlbumWrapper;
import com.example.android.androidify.api.models.Artist;
import com.example.android.androidify.api.models.ArtistCursorPager;
import com.example.android.androidify.api.models.ArtistTrackWrapper;
import com.example.android.androidify.api.models.ArtistsPager;
import com.example.android.androidify.api.models.Pager;
import com.example.android.androidify.api.models.Playlist;
import com.example.android.androidify.api.models.PlaylistSimple;
import com.example.android.androidify.api.models.PlaylistTrack;
import com.example.android.androidify.api.models.PublicUser;
import com.example.android.androidify.api.models.Recommendations;
import com.example.android.androidify.api.models.RelatedArtistsWrapper;
import com.example.android.androidify.api.models.SearchResultsPager;
import com.example.android.androidify.api.models.Track;
import com.example.android.androidify.api.models.TrackWrapper;
import com.example.android.androidify.api.models.TracksPager;
import com.example.android.androidify.model.Action;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.model.RecommendationOptions;
import com.example.android.androidify.repository.datasource.NetworkState;
import com.example.android.androidify.repository.datasource.SpotifyDataFactory;
import com.example.android.androidify.repository.datasource.SpotifyDataSource;
import com.example.android.androidify.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import retrofit2.Call;

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
                return parseTracks(data.tracks);
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
                return parseArtists(data.artists);
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

    public LiveData<ApiResponse<Action>> updateArtist(String id) {
        return new SpotifyLibraryActionResource() {
            @Override
            protected Call<Boolean[]> createCheckCall() {
                return apiService.isFollowingArtist(id);
            }

            @Override
            protected Call<Void> createUpdateCall(Action action) {
                return action.type == Action.Type.SAVE ? apiService.followArtist(id)
                        : apiService.unfollowArtist(id);
            }

            @Override
            protected Action getAction(Boolean[] statusArray) {
                return statusArray[0]  ? Action.remove() : Action.save();
            }
        }.getAsLiveData();
    }

    public PagedApiResponse<MusicListItem> getFollowedArtists() {
        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(50)
                        .setPrefetchDistance(10)
                        .setPageSize(50).build();

        Executor executor = Executors.newFixedThreadPool(5);

        MediatorLiveData<ApiResponse<PagedList<MusicListItem>>> data = new MediatorLiveData<>();

        SpotifyDataFactory<ArtistCursorPager, MusicListItem> dataFactory =
                new SpotifyDataFactory<ArtistCursorPager, MusicListItem>() {
                    @Override
                    protected SpotifyDataSource<ArtistCursorPager, MusicListItem> createDataSource() {
                        return new SpotifyDataSource<ArtistCursorPager, MusicListItem>() {
                            @Override
                            protected Call<ArtistCursorPager> createInitialCall(int pageSize) {
                                return apiService.getFollowedArtists(pageSize);
                            }

                            @Override
                            protected Call<ArtistCursorPager> getNextPage(String url) {
                                return apiService.getFollowedArtists(url);
                            }

                            @Override
                            protected String getKey(ArtistCursorPager data) {
                                return data.artists.next;
                            }

                            @Override
                            protected List<MusicListItem> processResponse(ArtistCursorPager data) {
                                return parseArtists(data.artists.items);
                            }
                        };
                    }
                };

        LiveData<NetworkState> networkStateLiveData = Transformations.switchMap(dataFactory.getSourceLiveData(),
                dataSource -> dataSource.getNetworkState());


        LiveData<PagedList<MusicListItem>> pagedListLiveData = new LivePagedListBuilder<String, MusicListItem>(
                dataFactory, pagedListConfig
        )
                .setFetchExecutor(executor)
                .build();

        return new PagedApiResponse<MusicListItem>(pagedListLiveData, networkStateLiveData);
    }


/*    public LiveData<PagedList<MusicListItem>> getFollowedArtists() {
        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(50)
                        .setPrefetchDistance(30)
                        .setPageSize(50).build();

        Executor executor = Executors.newFixedThreadPool(5);

        return new LivePagedListBuilder<String, MusicListItem>(
                new SpotifyDataFactory<ArtistCursorPager, MusicListItem>() {
                    @Override
                    protected SpotifyDataSource<ArtistCursorPager, MusicListItem> createDataSource() {
                        return new SpotifyDataSource<ArtistCursorPager, MusicListItem>() {
                            @Override
                            protected Call<ArtistCursorPager> createInitialCall(int pageSize) {
                                return apiService.getFollowedArtists(pageSize);
                            }

                            @Override
                            protected Call<ArtistCursorPager> getNextPage(String url) {
                                return apiService.getFollowedArtists(url);
                            }

                            @Override
                            protected String getKey(ArtistCursorPager data) {
                                return data.artists.next;
                            }

                            @Override
                            protected List<MusicListItem> processResponse(ArtistCursorPager data) {
                                return parseArtists(data.artists.items);
                            }
                        };
                    }
                },
                pagedListConfig)
                .setFetchExecutor(executor)
                .build();
    }*/

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

    public LiveData<ApiResponse<Boolean>> isAlbumSaved(String id) {
        return new SpotifyApiResource<Boolean[], Boolean>() {
            @Override
            protected Call<Boolean[]> createCall() {
                return apiService.containsAlbum(id);
            }

            @Override
            protected Boolean processResponse(@Nullable Boolean[] data) {
                return data[0];
            }
        }.getAsLiveData();
    }


    public LiveData<ApiResponse<Action>> updateAlbum(String id) {
        return new SpotifyLibraryActionResource() {
            @Override
            protected Call<Boolean[]> createCheckCall() {
                return apiService.containsAlbum(id);
            }

            @Override
            protected Call<Void> createUpdateCall(Action action) {
                return action.type == Action.Type.SAVE ? apiService.saveAlbum(id)
                        : apiService.removeAlbum(id);
            }

            @Override
            protected Action getAction(Boolean[] statusArray) {
                return statusArray[0] ? Action.remove() : Action.save();
            }
        }.getAsLiveData();
    }


    /** Track endpoints **/

    public LiveData<ApiResponse<Track>> getTrack(String id) {
        return new SpotifyApiResource<Track, Track>() {
            @Override
            protected Call<Track> createCall() {
                return apiService.getTrack(id);
            }

            @Override
            protected Track processResponse(@Nullable Track data) {
                return data;
            }
        }.getAsLiveData();
    }

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

    public LiveData<ApiResponse<Void>> saveTrackToLibrary(String id) {
        return new SingleApiEvent<Void>() {
            @Override
            protected Call<Void> createCall() {
                return apiService.saveTracks(id);
            }
        }.getAsLiveData();
    }

    public LiveData<ApiResponse<Void>> removeTrackFromLibrary(String id) {
        return new SingleApiEvent<Void>() {
            @Override
            protected Call<Void> createCall() {
                return apiService.removeTracks(id);
            }
        }.getAsLiveData();
    }

    public LiveData<ApiResponse<Action>> updateTrack(String id) {
        return new SpotifyLibraryActionResource() {
            @Override
            protected Call<Boolean[]> createCheckCall() {
                return apiService.containsTrack(id);
            }

            @Override
            protected Call<Void> createUpdateCall(Action action) {
                return action.type == Action.Type.SAVE ? apiService.saveTracks(id)
                        : apiService.removeTracks(id);
            }

            @Override
            protected Action getAction(Boolean[] statusArray) {
                return statusArray[0] ? Action.remove() : Action.save();
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

    /** Library endpoints **/

    public LiveData<ApiResponse<Boolean>> getMediaItemSaveStatus(MusicListItem item, String userId) {
        switch (item.type) {
            case ARTIST:
                return isFollowingArtist(item.id);
            case ALBUM:
                return isAlbumSaved(item.id);
            case TRACK:
                return isTrackSaved(item.id);
            case PLAYLIST:
                return isFollowingPlaylist(item.id, userId);
            default:
                return null;
        }
    }



    /** Top history endpoints **/


    public LiveData<ApiResponse<List<MusicListItem>>> getRecentlyPlayedTracks() {
        return new SpotifyApiResource<Pager<TrackWrapper>, List<MusicListItem>>() {
            @Override
            protected Call<Pager<TrackWrapper>> createCall() {
                return apiService.getRecentlyPlayed();
            }

            @Override
            protected List<MusicListItem> processResponse(@Nullable Pager<TrackWrapper> data) {
                return parseTrackWrapper(data);
            }
        }.getAsLiveData();
    }


    public LiveData<ApiResponse<List<MusicListItem>>> getUserTopArtists(String range) {
        return new SpotifyApiResource<Pager<Artist>, List<MusicListItem>>() {
            @Override
            protected Call<Pager<Artist>> createCall() {
                return apiService.getTopArtists(range);
            }

            @Override
            protected List<MusicListItem> processResponse(@Nullable Pager<Artist> data) {
                return parseArtists(data.items);
            }
        }.getAsLiveData();
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
                return parseTracks(data.items);
            }
        }.getAsLiveData();
    }

    public PagedApiResponse<MusicListItem> getSavedTracks() {
        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(50)
                        .setPrefetchDistance(10)
                        .setPageSize(50).build();

        Executor executor = Executors.newFixedThreadPool(5);

        MediatorLiveData<ApiResponse<PagedList<MusicListItem>>> data = new MediatorLiveData<>();

        SpotifyDataFactory<Pager<TrackWrapper>, MusicListItem> dataFactory =
                new SpotifyDataFactory<Pager<TrackWrapper>, MusicListItem>() {
                    @Override
                    protected SpotifyDataSource<Pager<TrackWrapper>, MusicListItem> createDataSource() {
                        return new SpotifyDataSource<Pager<TrackWrapper>, MusicListItem>() {
                            @Override
                            protected Call<Pager<TrackWrapper>> createInitialCall(int pageSize) {
                                return apiService.getSavedTracks(0 ,pageSize);
                            }

                            @Override
                            protected Call<Pager<TrackWrapper>> getNextPage(String url) {
                                return apiService.getSavedTracks(url);
                            }

                            @Override
                            protected String getKey(Pager<TrackWrapper> data) {
                                return data.next;
                            }

                            @Override
                            protected List<MusicListItem> processResponse(Pager<TrackWrapper> data) {
                                return parseTrackWrapper(data);
                            }
                        };
                    }
                };

        LiveData<NetworkState> networkStateLiveData = Transformations.switchMap(dataFactory.getSourceLiveData(),
                dataSource -> dataSource.getNetworkState());


        LiveData<PagedList<MusicListItem>> pagedListLiveData = new LivePagedListBuilder<String, MusicListItem>(
                dataFactory, pagedListConfig
        )
                .setFetchExecutor(executor)
                .build();

        return new PagedApiResponse<MusicListItem>(pagedListLiveData, networkStateLiveData);
    }


/*    public LiveData<PagedList<MusicListItem>> getSavedTracks() {
        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(50)
                        .setPrefetchDistance(30)
                        .setPageSize(50).build();

        Executor executor = Executors.newFixedThreadPool(5);

        return new LivePagedListBuilder<String, MusicListItem>(
                new SpotifyDataFactory<Pager<TrackWrapper>, MusicListItem>() {
                    @Override
                    protected SpotifyDataSource<Pager<TrackWrapper>, MusicListItem> createDataSource() {
                        return new SpotifyDataSource<Pager<TrackWrapper>, MusicListItem>() {
                            @Override
                            protected Call<Pager<TrackWrapper>> createInitialCall(int pageSize) {
                                return apiService.getSavedTracks(0 ,pageSize);
                            }

                            @Override
                            protected Call<Pager<TrackWrapper>> getNextPage(String url) {
                                return apiService.getSavedTracks(url);
                            }

                            @Override
                            protected String getKey(Pager<TrackWrapper> data) {
                                return data.next;
                            }

                            @Override
                            protected List<MusicListItem> processResponse(Pager<TrackWrapper> data) {
                                return parseTrackWrapper(data);
                            }
                        };
                    }
                },
                pagedListConfig)
                .setFetchExecutor(executor)
                .build();
    }*/


    public PagedApiResponse<MusicListItem> getSavedAlbums() {
        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(50)
                        .setPrefetchDistance(10)
                        .setPageSize(50).build();

        Executor executor = Executors.newFixedThreadPool(5);

        MediatorLiveData<ApiResponse<PagedList<MusicListItem>>> data = new MediatorLiveData<>();

        SpotifyDataFactory<Pager<AlbumWrapper>, MusicListItem> dataFactory =
                new SpotifyDataFactory<Pager<AlbumWrapper>, MusicListItem>() {
                    @Override
                    protected SpotifyDataSource<Pager<AlbumWrapper>, MusicListItem> createDataSource() {
                        return new SpotifyDataSource<Pager<AlbumWrapper>, MusicListItem>() {
                            @Override
                            protected Call<Pager<AlbumWrapper>> createInitialCall(int pageSize) {
                                return apiService.getSavedAlbums(0, pageSize);
                            }

                            @Override
                            protected Call<Pager<AlbumWrapper>> getNextPage(String url) {
                                return apiService.getSavedAlbums(url);
                            }

                            @Override
                            protected String getKey(Pager<AlbumWrapper> data) {
                                return data.next;
                            }

                            @Override
                            protected List<MusicListItem> processResponse(Pager<AlbumWrapper> data) {
                                return parseAlbumWrapper(data);
                            }
                        };
                    }
                };

        LiveData<NetworkState> networkStateLiveData = Transformations.switchMap(dataFactory.getSourceLiveData(),
                dataSource -> dataSource.getNetworkState());


        LiveData<PagedList<MusicListItem>> pagedListLiveData = new LivePagedListBuilder<String, MusicListItem>(
                dataFactory, pagedListConfig
        )
                .setFetchExecutor(executor)
                .build();

        return new PagedApiResponse<MusicListItem>(pagedListLiveData, networkStateLiveData);
    }

/*    public LiveData<PagedList<MusicListItem>> getSavedAlbums() {
        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(50)
                        .setPrefetchDistance(30)
                        .setPageSize(50).build();

        Executor executor = Executors.newFixedThreadPool(5);

        return new LivePagedListBuilder<String, MusicListItem>(
                new SpotifyDataFactory<Pager<AlbumWrapper>, MusicListItem>() {

                    @Override
                    protected SpotifyDataSource<Pager<AlbumWrapper>, MusicListItem> createDataSource() {
                        return new SpotifyDataSource<Pager<AlbumWrapper>, MusicListItem>() {
                            @Override
                            protected Call<Pager<AlbumWrapper>> createInitialCall(int pageSize) {
                                return apiService.getSavedAlbums(0, pageSize);
                            }

                            @Override
                            protected Call<Pager<AlbumWrapper>> getNextPage(String url) {
                                return apiService.getSavedAlbums(url);
                            }

                            @Override
                            protected String getKey(Pager<AlbumWrapper> data) {
                                return data.next;
                            }

                            @Override
                            protected List<MusicListItem> processResponse(Pager<AlbumWrapper> data) {
                                return parseAlbumWrapper(data);
                            }
                        };
                    }
                },
                pagedListConfig)
                .setFetchExecutor(executor)
                .build();
    }*/


    /** Playlist endpoints **/

    public PagedApiResponse<MusicListItem> getPlaylists() {
        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(50)
                        .setPrefetchDistance(10)
                        .setPageSize(50).build();

        Executor executor = Executors.newFixedThreadPool(5);

        MediatorLiveData<ApiResponse<PagedList<MusicListItem>>> data = new MediatorLiveData<>();

        SpotifyDataFactory<Pager<PlaylistSimple>, MusicListItem> dataFactory =
                new SpotifyDataFactory<Pager<PlaylistSimple>, MusicListItem>() {
                    @Override
                    protected SpotifyDataSource<Pager<PlaylistSimple>, MusicListItem> createDataSource() {
                        return new SpotifyDataSource<Pager<PlaylistSimple>, MusicListItem>() {
                            @Override
                            protected Call<Pager<PlaylistSimple>> createInitialCall(int pageSize) {
                                return apiService.getUserPlaylists(0, pageSize);
                            }

                            @Override
                            protected Call<Pager<PlaylistSimple>> getNextPage(String url) {
                                return apiService.getUserPlaylists(url);
                            }

                            @Override
                            protected String getKey(Pager<PlaylistSimple> data) {
                                return data.next;
                            }

                            @Override
                            protected List<MusicListItem> processResponse(Pager<PlaylistSimple> data) {
                                return parsePlaylists(data.items);
                            }
                        };
                    }
                };

        LiveData<NetworkState> networkStateLiveData = Transformations.switchMap(dataFactory.getSourceLiveData(),
                dataSource -> dataSource.getNetworkState());


        LiveData<PagedList<MusicListItem>> pagedListLiveData = new LivePagedListBuilder<String, MusicListItem>(
                dataFactory, pagedListConfig
        )
                .setFetchExecutor(executor)
                .build();

        return new PagedApiResponse<MusicListItem>(pagedListLiveData, networkStateLiveData);
    }

/*    public LiveData<PagedList<MusicListItem>> getPlaylists() {
        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(50)
                        .setPrefetchDistance(30)
                        .setPageSize(50).build();

        Executor executor = Executors.newFixedThreadPool(5);

        return new LivePagedListBuilder<String, MusicListItem>(
                new SpotifyDataFactory<Pager<PlaylistSimple>, MusicListItem>() {
                    @Override
                    protected SpotifyDataSource<Pager<PlaylistSimple>, MusicListItem> createDataSource() {
                        return new SpotifyDataSource<Pager<PlaylistSimple>, MusicListItem>() {
                            @Override
                            protected Call<Pager<PlaylistSimple>> createInitialCall(int pageSize) {
                                return apiService.getUserPlaylists(0, pageSize);
                            }

                            @Override
                            protected Call<Pager<PlaylistSimple>> getNextPage(String url) {
                                return apiService.getUserPlaylists(url);
                            }

                            @Override
                            protected String getKey(Pager<PlaylistSimple> data) {
                                return data.next;
                            }

                            @Override
                            protected List<MusicListItem> processResponse(Pager<PlaylistSimple> data) {
                                return parsePlaylists(data.items);
                            }
                        };
                    }
                },
                pagedListConfig)
                .setFetchExecutor(executor)
                .build();
    }*/

/*
    public LiveData<ApiResponse<List<MusicListItem>>> getUserPlaylists() {
        return new SpotifyApiResource<Pager<PlaylistSimple>, List<MusicListItem>>() {
            @Override
            protected Call<Pager<PlaylistSimple>> createCall() {
                return apiService.getUserPlaylists();
            }

            @Override
            protected List<MusicListItem> processResponse(@Nullable Pager<PlaylistSimple> data) {
                return parsePlaylists(data.items);
            }
        }.getAsLiveData();
    }
*/

    public LiveData<ApiResponse<Playlist>> getPlaylist(String id) {
        return new SpotifyApiResource<Playlist, Playlist>() {
            @Override
            protected Call<Playlist> createCall() {
                return apiService.getPlaylist(id);
            }

            @Override
            protected Playlist processResponse(@Nullable Playlist data) {
                return data;
            }
        }.getAsLiveData();
    }

/*    public LiveData<ApiResponse<List<MusicListItem>>> getPlaylistTracks(String id) {
        return new SpotifyApiResource<Pager<PlaylistTrack>, List<MusicListItem>>() {
            @Override
            protected Call<Pager<PlaylistTrack>> createCall() {
                return apiService.getPlaylistTracks(id);
            }

            @Override
            protected List<MusicListItem> processResponse(@Nullable Pager<PlaylistTrack> data) {
                return parsePlaylistTracks(data.items);
            }
        }.getAsLiveData();
    }*/

    public LiveData<ApiResponse<Boolean>> isFollowingPlaylist(String playlistId, String userId) {
        return new SpotifyApiResource<Boolean[], Boolean>() {
            @Override
            protected Call<Boolean[]> createCall() {
                return apiService.areFollowingPlaylist(playlistId, userId);
            }

            @Override
            protected Boolean processResponse(@Nullable Boolean[] data) {
                Log.i(TAG, "track saved = " + data[0]);
                return data[0];
            }
        }.getAsLiveData();
    }

    public LiveData<ApiResponse<Action>> updatePlaylist(String playlistId, String userId) {
        return new SpotifyLibraryActionResource() {
            @Override
            protected Call<Boolean[]> createCheckCall() {
                return apiService.areFollowingPlaylist(playlistId, userId);
            }

            @Override
            protected Call<Void> createUpdateCall(Action action) {
                return action.type == Action.Type.SAVE ? apiService.followPlaylist(playlistId)
                        : apiService.unfollowPlaylist(playlistId);
            }

            @Override
            protected Action getAction(Boolean[] statusArray) {
                return statusArray[0] ? Action.remove() : Action.save();
            }
        }.getAsLiveData();
    }

    public PagedApiResponse<MusicListItem> getPlaylistTracks(String id) {
        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(100)
                        .setPrefetchDistance(10)
                        .setPageSize(100).build();

        Executor executor = Executors.newFixedThreadPool(5);

        MediatorLiveData<ApiResponse<PagedList<MusicListItem>>> data = new MediatorLiveData<>();

        SpotifyDataFactory<Pager<PlaylistTrack>, MusicListItem> dataFactory =
                new SpotifyDataFactory<Pager<PlaylistTrack>, MusicListItem>() {
                    @Override
                    protected SpotifyDataSource<Pager<PlaylistTrack>, MusicListItem> createDataSource() {
                        return new SpotifyDataSource<Pager<PlaylistTrack>, MusicListItem>() {
                            @Override
                            protected Call<Pager<PlaylistTrack>> createInitialCall(int pageSize) {
                                return apiService.getPlaylistTracks(id, pageSize);
                            }

                            @Override
                            protected Call<Pager<PlaylistTrack>> getNextPage(String url) {
                                return apiService.getAdditionalPlaylistTracks(url);
                            }

                            @Override
                            protected String getKey(Pager<PlaylistTrack> data) {
                                return data.next;
                            }

                            @Override
                            protected List<MusicListItem> processResponse(Pager<PlaylistTrack> data) {
                                return parsePlaylistTracks(data.items);
                            }
                        };
                    }
                };

        LiveData<NetworkState> networkStateLiveData = Transformations.switchMap(dataFactory.getSourceLiveData(),
                dataSource -> dataSource.getNetworkState());


        LiveData<PagedList<MusicListItem>> pagedListLiveData = new LivePagedListBuilder<String, MusicListItem>(
                dataFactory, pagedListConfig
        )
                .setFetchExecutor(executor)
                .build();

        return new PagedApiResponse<MusicListItem>(pagedListLiveData, networkStateLiveData);
    }

/*        return new LivePagedListBuilder<String, MusicListItem>(
                new SpotifyDataFactory<Pager<PlaylistTrack>, MusicListItem>() {
                    @Override
                    protected SpotifyDataSource<Pager<PlaylistTrack>, MusicListItem> createDataSource() {
                        return new SpotifyDataSource<Pager<PlaylistTrack>, MusicListItem>() {
                            @Override
                            protected Call<Pager<PlaylistTrack>> createInitialCall(int pageSize) {
                                Log.i(TAG, "page size = " + pageSize);
                                return apiService.getPlaylistTracks(id, pageSize);
                            }

                            @Override
                            protected Call<Pager<PlaylistTrack>> getNextPage(String url) {
                                return apiService.getAdditionalPlaylistTracks(url);
                            }

                            @Override
                            protected String getKey(Pager<PlaylistTrack> data) {
                                return data.next;
                            }

                            @Override
                            protected List<MusicListItem> processResponse(Pager<PlaylistTrack> data) {
                                return parsePlaylistTracks(data.items);
                            }
                        };
                    }
                },
                pagedListConfig)
                .setFetchExecutor(executor)
                .build();*/


/*    public LiveData<PagedList<MusicListItem>> getPlaylistTracks(String id) {
        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(100)
                        .setPrefetchDistance(50)
                        .setPageSize(100).build();

        Executor executor = Executors.newFixedThreadPool(5);

        return new LivePagedListBuilder<String, MusicListItem>(
                new SpotifyDataFactory<Pager<PlaylistTrack>, MusicListItem>() {
                    @Override
                    protected SpotifyDataSource<Pager<PlaylistTrack>, MusicListItem> createDataSource() {
                        return new SpotifyDataSource<Pager<PlaylistTrack>, MusicListItem>() {
                            @Override
                            protected Call<Pager<PlaylistTrack>> createInitialCall(int pageSize) {
                                Log.i(TAG, "page size = " + pageSize);
                                return apiService.getPlaylistTracks(id, pageSize);
                            }

                            @Override
                            protected Call<Pager<PlaylistTrack>> getNextPage(String url) {
                                return apiService.getAdditionalPlaylistTracks(url);
                            }

                            @Override
                            protected String getKey(Pager<PlaylistTrack> data) {
                                return data.next;
                            }

                            @Override
                            protected List<MusicListItem> processResponse(Pager<PlaylistTrack> data) {
                                return parsePlaylistTracks(data.items);
                            }
                        };
                    }
                },
                pagedListConfig)
                .setFetchExecutor(executor)
                .build();
    }*/

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

    public LiveData<ApiResponse<List<MusicListItem>>> getSearchResultsByType(String query, String type) {
        return new SpotifyApiResource<SearchResultsPager, List<MusicListItem>>() {
            @Override
            protected Call<SearchResultsPager> createCall() {
                return apiService.getSearchResultsByType(query, type);
            }

            @Override
            protected List<MusicListItem> processResponse(@Nullable SearchResultsPager data) {
                return parseSearchResults(data, type);
            }
        }.getAsLiveData();
    }

    /** Recommendations **/

    public LiveData<ApiResponse<List<MusicListItem>>> getRecommendations(RecommendationOptions options) {
        return new SpotifyApiResource<Recommendations, List<MusicListItem>>() {
            @Override
            protected Call<Recommendations> createCall() {
                return apiService.getRecommendations(options.getOptions());
            }

            @Override
            protected List<MusicListItem> processResponse(@Nullable Recommendations data) {
                return parseTracks(data.tracks);
            }
        }.getAsLiveData();
    }

    /**********
     *  User  *
     **********/

    public LiveData<ApiResponse<PublicUser>> getUserProfile() {
        return new SpotifyApiResource<PublicUser, PublicUser>() {
            @Override
            protected Call<PublicUser> createCall() {
                return apiService.getUserProfile();
            }

            @Override
            protected PublicUser processResponse(@Nullable PublicUser data) {
                return data;
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

    private List<MusicListItem> parseAlbumWrapper(Pager<AlbumWrapper> albumWrapperPager) {
        List<MusicListItem> albums = new ArrayList<>();
        for (AlbumWrapper albumWrapper : albumWrapperPager.items) {
            if (albumWrapper != null) {
                albums.add(new MusicListItem(albumWrapper.album));
            }
        }

        return albums;
    }

    public List<MusicListItem> parsePlaylists(List<PlaylistSimple> playlists) {
        List<MusicListItem> playlistsList = new ArrayList<>();
        for (PlaylistSimple playlist : playlists) {
            if (playlist.tracks != null && playlist.tracks.total > 0) {
                // Ignore empty playlists
                playlistsList.add(new MusicListItem(playlist));
            }
        }
        return playlistsList;
    }

    public List<MusicListItem> parsePlaylistTracks(List<PlaylistTrack> playlistTracks) {
        List<MusicListItem> tracks = new ArrayList<>();
        for (PlaylistTrack playlistTrack: playlistTracks) {
            Track track = playlistTrack.track;
            if (track != null) {
                tracks.add(new MusicListItem(track));
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
        List<MusicListItem> albums = parseAlbums(data.albums.items);
        results.addAll(tracks);
        results.addAll(artists);
        results.addAll(albums);
        return results;
    }

    private List<MusicListItem> parseSearchResults(SearchResultsPager data, String type) {
        switch (type) {
            case Constants.TRACK:
                return parseTracks(data.tracks.items);
            case Constants.ARTIST:
                return parseArtists(data.artists.items);
            case Constants.ALBUM:
                return parseAlbums(data.albums.items);
            default:
                return null;
        }
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
            builder.append(item.id).append(",");
        }
        builder.append(items.get(items.size() - 1).id);
        Log.i(TAG, builder.toString());
        return builder.toString();
    }


}
