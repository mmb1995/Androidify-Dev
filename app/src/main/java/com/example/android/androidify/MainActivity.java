package com.example.android.androidify;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.androidify.fragments.ArtistFragment;
import com.example.android.androidify.fragments.TopHistoryFragment;
import com.example.android.androidify.fragments.TopHistoryListFragment;
import com.example.android.androidify.viewmodel.MusicPlaybackViewModel;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class MainActivity extends AppCompatActivity implements HasSupportFragmentInjector,
        TopHistoryListFragment.onTopHistorySelectedListener {

    private static final String TAG = "MAIN_ACTIVITY";

    @BindView(R.id.now_playing_text_view)
    TextView mNowPlayingTextView;

    @BindView(R.id.play_controls_button)
    ImageButton mPlayControlsButton;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    // Used to play music and communicate with the Spotify app
    private SpotifyAppRemote mSpotifyAppRemote;

    // Event callbacks
    Subscription<PlayerState> mPlayerStateListener;


    // Handles player state events sent by Spotify
    private final Subscription.EventCallback<PlayerState> mPlayerStateCallback = new Subscription.EventCallback<PlayerState>() {
        @Override
        public void onEvent(PlayerState playerState) {
            if (playerState != null) {

                // Update play/pause controls
                if (playerState.isPaused) {
                    mPlayControlsButton.setImageResource(R.drawable.ic_play_circle_24px);
                } else {
                    mPlayControlsButton.setImageResource(R.drawable.ic_pause_circle_24px);
                }

                String trackName = playerState.track.name;

                // Update track description if necessary
                if (!mNowPlayingTextView.getText().toString().equals(trackName)) {
                    mNowPlayingTextView.setText(trackName);
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        AndroidInjection.inject(this);
        mPlayControlsButton.setOnClickListener((View v) -> { this.onTogglePlayClicked();});
        MusicPlaybackViewModel model = ViewModelProviders.of(this).get(MusicPlaybackViewModel.class);
        model.getCurrentlyPlaying().observe(this, (String uri) -> {
            onPlayUri(uri);
        });
        if (savedInstanceState == null) {
            displayTopHistory();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        disconnect();
        connectToSpotify();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.disconnect();
    }


    private void displayTopHistory() {
        Fragment topHistoryFragment = new TopHistoryFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, topHistoryFragment);
        transaction.commit();
    }

    private void displayArtistFragment(String id) {
        Fragment artistFragment = ArtistFragment.newInstance(id);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, artistFragment)
                .addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onTopHistorySelected(String id) {
        displayArtistFragment(id);
    }


    /**
     * Set up event listeners to communicate remotely with Spotify
     */
    private void onConnectSuccess() {
        if (mPlayerStateListener != null && !mPlayerStateListener.isCanceled()) {
            mPlayerStateListener.cancel();
            mPlayerStateListener = null;
        }

        // Set up player state listener
        mPlayerStateListener = (Subscription<PlayerState>) mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(mPlayerStateCallback)
                .setLifecycleCallback(new Subscription.LifecycleCallback() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onStop() {

                    }
                })
                .setErrorCallback(throwable -> {
                    Log.e(TAG, "Failed to subscribe to player state");
                });
    }

    private void connectToSpotify() {
        //disconnect();
        // Attempt to connect with Spotify
        SpotifyAppRemote.connect(
                this,
                new ConnectionParams.Builder(BuildConfig.CLIENT_ID)
                        .setRedirectUri(BuildConfig.REDIRECT_URL)
                        .showAuthView(false)
                        .build(),
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                       mSpotifyAppRemote = spotifyAppRemote;
                       Log.i(TAG, "Successfully connected with Spotify");
                       onConnectSuccess();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e(TAG, throwable.getMessage());
                    }
                }
        );
    }

    /**
     * Tells Spotify to start playback of selected track
     */
    private void onPlayUri(String uri) {
        if (uri != null && mSpotifyAppRemote != null) {
            Log.i(TAG, uri);

            // Start playback
            mSpotifyAppRemote.getPlayerApi()
                    .play(uri)
                    .setResultCallback(empty -> Log.i(TAG, "Play successful"))
                    .setErrorCallback(error -> Log.e(TAG, error.getMessage()));
        }
    }

    /**
     * Tells Spotify app remote to pause playback
     */
    private void onPauseTrack() {
        mSpotifyAppRemote.getPlayerApi()
                .pause()
                .setResultCallback(empty -> Log.i(TAG, "Playback paused"))
                .setErrorCallback(throwable -> Log.e(TAG, throwable.getMessage()));
    }

    /**
     * Tells Spotify app remote to resume playback
     */
    private void onResumeTrack() {
        mSpotifyAppRemote.getPlayerApi()
                .resume()
                .setResultCallback(empty -> Log.i(TAG, "Playback Resumed"))
                .setErrorCallback(throwable -> Log.e(TAG, throwable.getMessage()));
    }

    /**
     * Handles playback controls
     */
    private void onTogglePlayClicked() {
        if (mSpotifyAppRemote != null) {
            mSpotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(playerState -> {
                if (playerState.isPaused) {
                    this.onResumeTrack();
                } else {
                    this.onPauseTrack();
                }
            });
        }
    }

    /**
     * Toggles shuffle for users playback
     */
    public void onToggleShuffleClicked() {
        if (mSpotifyAppRemote != null) {
            mSpotifyAppRemote.getPlayerApi()
                    .toggleShuffle()
                    .setResultCallback(empty -> {})
                    .setErrorCallback(throwable -> {});
        }
    }

    /**
    private void displayTopHistoryFragment() {
        Fragment recentlyPlayedFragment = TopHistoryListFragment.newInstance(TopHistoryListFragment.RECENTLY_PLAYED);
        Fragment topTracksFragment = TopHistoryListFragment.newInstance(TopHistoryListFragment.TOP_TRACKS);
        Fragment topArtistsFragment = TopHistoryListFragment.newInstance(TopHistoryListFragment.TOP_ARTISTS);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_recent, recentlyPlayedFragment);
        transaction.replace(R.id.fragment_container_top_tracks, topTracksFragment);
        transaction.replace(R.id.fragment_container_top_artists, topArtistsFragment);
        transaction.commit();
    }
    **/

    /**
     * Disconnect the SpotifyAppRemote if it is active
     */
    private void disconnect() {
        if (mSpotifyAppRemote != null) {
            SpotifyAppRemote.disconnect(mSpotifyAppRemote);
            mSpotifyAppRemote = null;
        }
    }


    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
