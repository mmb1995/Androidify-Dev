package com.example.android.androidify.repository;

import android.graphics.Bitmap;

import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.api.PlayerApiResource;
import com.spotify.android.appremote.api.ImagesApi;
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.Empty;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.PlayerState;

import androidx.lifecycle.LiveData;

public class PlayerRepository {

    private final SpotifyAppRemote mSpotifyAppRemote;
    private final PlayerApi mPlayerApi;
    private final ImagesApi mImagesApi;
    private static PlayerRepository mInstance;

    private PlayerRepository(SpotifyAppRemote remote) {
        this.mSpotifyAppRemote = remote;
        this.mPlayerApi = mSpotifyAppRemote.getPlayerApi();
        this.mImagesApi = mSpotifyAppRemote.getImagesApi();
    }

    // Singleton constructor
    public static PlayerRepository getPlayerRepository(SpotifyAppRemote remote) {
        if (mInstance == null) {
            mInstance = new PlayerRepository(remote);
        }
        return mInstance;
    }

    /**
    public PlayerRepository(SpotifyAppRemote remote) {
        this.mSpotifyAppRemote = remote;
        this.mPlayerApi = remote.getPlayerApi();
        this.mImagesApi = remote.getImagesApi();
    }
     **/

    /**
    @Inject
    public PlayerRepository(Application context) {
        SpotifyAppRemote.connect(context,
                new ConnectionParams.Builder(BuildConfig.CLIENT_ID)
                        .setRedirectUri(BuildConfig.REDIRECT_URL)
                        .showAuthView(false)
                        .build(),
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        PlayerRepository.this.mSpotifyAppRemote = spotifyAppRemote;
                        PlayerRepository.this.mPlayerApi = mSpotifyAppRemote.getPlayerApi();
                        PlayerRepository.this.mImagesApi = mSpotifyAppRemote.getImagesApi();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                });
    }
    **/

    /** PlayerApi **/

    public LiveData<ApiResponse<PlayerState>> getPlayerState() {
        return new PlayerApiResource<PlayerState>() {
            @Override
            protected CallResult<PlayerState> createCall() {
                return mPlayerApi.getPlayerState();
            }
        }.getAsLiveData();
    }

    public LiveData<ApiResponse<Empty>> playUri(String uri) {
        return new PlayerApiResource<Empty>() {
            @Override
            protected CallResult<Empty> createCall() {
                return mPlayerApi.play(uri);
            }
        }.getAsLiveData();
    }

    public LiveData<ApiResponse<Empty>> pausePlayback() {
        return new PlayerApiResource<Empty>() {
            @Override
            protected CallResult<Empty> createCall() {
                return mPlayerApi.pause();
            }
        }.getAsLiveData();
    }

    public LiveData<ApiResponse<Empty>> resumePlayback() {
        return new PlayerApiResource<Empty>() {
            @Override
            protected CallResult<Empty> createCall() {
                return mPlayerApi.resume();
            }
        }.getAsLiveData();
    }

    public LiveData<ApiResponse<Empty>> toggleShuffle() {
        return new PlayerApiResource<Empty>() {
            @Override
            protected CallResult<Empty> createCall() {
                return mPlayerApi.toggleShuffle();
            }
        }.getAsLiveData();
    }

    public LiveData<ApiResponse<Empty>> toggleRepeat() {
        return new PlayerApiResource<Empty>() {
            @Override
            protected CallResult<Empty> createCall() {
                return mPlayerApi.toggleRepeat();
            }
        }.getAsLiveData();
    }

    public LiveData<ApiResponse<Empty>> skipToNext() {
        return new PlayerApiResource<Empty>() {
            @Override
            protected CallResult<Empty> createCall() {
                return mPlayerApi.skipNext();
            }
        }.getAsLiveData();
    }

    public LiveData<ApiResponse<Empty>> skipToPrevious() {
        return new PlayerApiResource<Empty>() {
            @Override
            protected CallResult<Empty> createCall() {
                return mPlayerApi.skipPrevious();
            }
        }.getAsLiveData();
    }

    public LiveData<ApiResponse<Empty>> seekTo(long position) {
        return new PlayerApiResource<Empty>() {
            @Override
            protected CallResult<Empty> createCall() {
                return mPlayerApi.seekTo(position);
            }
        }.getAsLiveData();
    }

    /** ImagesApi **/

    public LiveData<ApiResponse<Bitmap>> getImage(ImageUri uri, Image.Dimension dimension) {
        return new PlayerApiResource<Bitmap>() {
            @Override
            protected CallResult<Bitmap> createCall() {
                return mImagesApi.getImage(uri, dimension);
            }
        }.getAsLiveData();
    }

    public void disconnect() {
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }
}
