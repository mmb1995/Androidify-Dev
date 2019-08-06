package com.example.android.androidify;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.android.androidify.fragments.ArtistFragment;
import com.example.android.androidify.fragments.HomeFragment;
import com.example.android.androidify.fragments.TopHistoryFragment;
import com.example.android.androidify.model.EventObserver;
import com.example.android.androidify.utils.MusicPlayBar;
import com.example.android.androidify.viewmodel.MainActivityViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.snackbar.Snackbar;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.ErrorCallback;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.PlayerContext;
import com.spotify.protocol.types.PlayerOptions;
import com.spotify.protocol.types.PlayerRestrictions;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Repeat;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class MainActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    private static final String TAG = "MAIN_ACTIVITY";

    @BindView(R.id.now_playing_title)
    TextView mNowPlayingTextView;

    @BindView(R.id.now_playing_collapsed_playback)
    ImageButton mCollapsedPlayControlsButton;

    @BindView(R.id.now_playing)
    ConstraintLayout mNowPlayingBar;

    @BindView(R.id.snackbar_container)
    CoordinatorLayout mSnackbarContainer;

    @BindView(R.id.now_playing_cover_art)
    ImageView mNowPlayingImage;

    @BindView(R.id.now_playing_expand)
    ImageButton mExpandNowPlayingButton;

    @BindView(R.id.now_playing_shuffle)
    ImageButton mShuffleButton;

    @BindView(R.id.now_playing_repeat_button)
    ImageButton mRepeatButton;

    @BindView(R.id.now_playing_skip_prev_button)
    ImageButton mPlayPreviousButton;

    @BindView(R.id.now_playing_skip_next_button)
    ImageButton mPlayNextButton;

    @BindView(R.id.now_playing_playback_button)
    ImageButton mPlayControlsButton;

    @BindView(R.id.now_playing_time_elapsed)
    TextView mTimeElapsed;

    @BindView(R.id.now_playing_duration)
    TextView mDuration;

    @BindView(R.id.now_playing_seek_bar)
    SeekBar mSeekBar;

    @BindView(R.id.bottom_nav_bar)
    BottomNavigationView mBottomNavView;

    @BindView(R.id.toolbar)
    MaterialToolbar mToolbar;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    private MainActivityViewModel mViewModel;

    // Used to play music and communicate with the Spotify app
    private SpotifyAppRemote mSpotifyAppRemote;

    // Event callbacks
    Subscription<PlayerState> mPlayerStateListener;
    Subscription<PlayerContext> mPlayerContextListener;

    private BottomSheetBehavior mBottomSheetBehavior;
    private MusicPlayBar mMusicPlayBar;

    //Error callback
    private final ErrorCallback mErrorCallback = throwable -> { Log.e(TAG, "" + throwable.getMessage(), throwable);};


    private final Subscription.EventCallback<PlayerContext> mPlayerContextCallback = new Subscription.EventCallback<PlayerContext>() {
        @Override
        public void onEvent(PlayerContext playerContext) {
            Log.i(TAG, "title = " + playerContext.title);
            Log.i(TAG, "type = " + playerContext.type);
        }
    };


    /**
     * Callback to handle PlayerState updates sent by Spotify
     */
    private final Subscription.EventCallback<PlayerState> mPlayerStateCallback = new Subscription.EventCallback<PlayerState>() {
        @Override
        public void onEvent(PlayerState playerState) {
            if (playerState != null) {

                // Update seekbar
                if (mMusicPlayBar != null) {
                    if (playerState.playbackSpeed > 0) {
                        mMusicPlayBar.unpause();
                    } else {
                        mMusicPlayBar.pause();
                    }
                }


                // Update play/pause controls
                if (playerState.isPaused) {
                    mCollapsedPlayControlsButton.setImageResource(R.drawable.ic_play_circle_24px);
                    mPlayControlsButton.setImageResource(R.drawable.ic_play_circle_24px);
                } else {
                    mCollapsedPlayControlsButton.setImageResource(R.drawable.ic_pause_circle_24px);
                    mPlayControlsButton.setImageResource(R.drawable.ic_pause_circle_24px);
                }

                String trackName = playerState.track.name;

                // Update track description if necessary
                if (!mNowPlayingTextView.getText().toString().equals(trackName)) {
                    mNowPlayingTextView.setText(trackName);
                }

                setControlsEnabledStatus(playerState.playbackRestrictions);
                updateControls(playerState.playbackOptions);

                // Get image
                mSpotifyAppRemote.getImagesApi()
                        .getImage(playerState.track.imageUri, Image.Dimension.LARGE)
                        .setResultCallback(bitmap -> {
                            mNowPlayingImage.setImageBitmap(bitmap);
                        });

                // Update seekbar duration and position
                mMusicPlayBar.setDuration(playerState.track.duration);
                mMusicPlayBar.update(playerState.playbackPosition);

                // Update duration and position textview
                //mDuration.setText(formatTime(playerState.track.duration));
               // mTimeElapsed.setText(formatTime(playerState.playbackPosition));

                mSeekBar.setEnabled(true);
            }
        }
    };


    /**
     * Enables and disables player controls based off the current player state
     * @param restrictions status of restricted actions
     */
    private void setControlsEnabledStatus(PlayerRestrictions restrictions) {
        Log.i(TAG, "skip next = " + restrictions.canSkipNext);
        Log.i(TAG, "skip prev = " + restrictions.canSkipPrev);
        Log.i(TAG, "shuffle = " + restrictions.canToggleShuffle);
        Log.i(TAG, "repeat context = " + restrictions.canRepeatContext);
        Log.i(TAG, "repeat track = " + restrictions.canRepeatTrack);
        mPlayPreviousButton.setEnabled(restrictions.canSkipPrev);
        mPlayNextButton.setEnabled(restrictions.canSkipNext);
        mShuffleButton.setEnabled(restrictions.canToggleShuffle);
        mRepeatButton.setEnabled(restrictions.canRepeatContext);
    }

    /**
     * Updates state of player controls based on the current player state
     * @param playerOptions the status of player actions;
     */
    private void updateControls(PlayerOptions playerOptions) {
        mShuffleButton.setSelected(playerOptions.isShuffling);
        mRepeatButton.setSelected(playerOptions.repeatMode != Repeat.OFF);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        AndroidInjection.inject(this);
        setSupportActionBar(mToolbar);
        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        mSeekBar.setEnabled(false);
        MaterialShapeDrawable materialShapeDrawable = MaterialShapeDrawable.createWithElevationOverlay(this, 8);
        mNowPlayingBar.setBackground(materialShapeDrawable);
        mNowPlayingBar.setElevation(materialShapeDrawable.getElevation());
        if (savedInstanceState == null) {
            displayHomeFragment();
        }
        initBottomSheet();
        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        mBottomNavView.setOnNavigationItemSelectedListener((MenuItem item) -> {
            return onBottomNavItemClicked(item);
        });
        setupViewModelObservers();
    }

    private void setupViewModelObservers() {
        mViewModel.getCurrentArtistId().observe(this, new EventObserver<String>(artistId -> {
            displayArtistFragment(artistId);
        }));
        mViewModel.getCurrentlyPlaying().observe(this, (String uri) -> {
            onPlayUri(uri);
        });
        mViewModel.getSnackbarEvent().observe(this, new EventObserver<String>(message -> {
            onSnackBarEvent(message);
        }));
    }

    private void initBottomSheet() {
        mBottomSheetBehavior = BottomSheetBehavior.from(mNowPlayingBar);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        mBottomNavView.setVisibility(View.VISIBLE);
                        mExpandNowPlayingButton.setImageResource(R.drawable.ic_arrow_up_24px);
                        mExpandNowPlayingButton.setVisibility(View.VISIBLE);
                        mCollapsedPlayControlsButton.setVisibility(View.VISIBLE);
                        Log.i(TAG, "STATE_COLLAPSED");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        mExpandNowPlayingButton.setVisibility(View.INVISIBLE);
                        mBottomNavView.setVisibility(View.GONE);
                        Log.i(TAG, "STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        mExpandNowPlayingButton.setImageResource(R.drawable.ic_arrow_down_24px);
                        mExpandNowPlayingButton.setVisibility(View.VISIBLE);
                        mCollapsedPlayControlsButton.setVisibility(View.INVISIBLE);
                        Log.i(TAG, "STATE_EXPANDED");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.i(TAG, "STATE_HIDDEN");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.i(TAG, "STATE_SETTLING");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        mExpandNowPlayingButton.setOnClickListener((View v) -> {
            if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                mBottomSheetBehavior.setState(mBottomSheetBehavior.STATE_COLLAPSED);
                //mExpandNowPlayingButton.setChecked(false);
            } else if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                mBottomSheetBehavior.setState(mBottomSheetBehavior.STATE_EXPANDED);
                //mExpandNowPlayingButton.setChecked(true);
            }
        });
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


    private void displayHomeFragment() {
        Fragment topHistoryFragment = new HomeFragment();
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

    private void displayTopHistory() {
        Fragment topHistoryFragment = new TopHistoryFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, topHistoryFragment)
                .addToBackStack(null);
        transaction.commit();
    }

    /**
     * Setup listeners and controls for interacting with Spotify
     */
    private void onConnectSuccess() {
       subscribeToPlayerState();
       subscribeToPlayerContext();
       setupPlayerControls();
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
                       mMusicPlayBar = new MusicPlayBar(mSeekBar, mSpotifyAppRemote, mTimeElapsed, mDuration);
                       Log.i(TAG, "Successfully connected with Spotify");
                       MainActivity.this.onConnectSuccess();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e(TAG, "" + throwable.getMessage(), throwable);
                    }
                }
        );
    }

    private void subscribeToPlayerContext() {
        if (mPlayerContextListener != null && !mPlayerContextListener.isCanceled()) {
            mPlayerContextListener.cancel();
            mPlayerContextListener = null;
        }

        mPlayerContextListener = (Subscription<PlayerContext>) mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerContext()
                .setEventCallback(mPlayerContextCallback)
                .setErrorCallback(mErrorCallback);
    }

    /**
     * Sets up subscription to observe player state changes
     */
    private void subscribeToPlayerState() {
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
                    Log.e(TAG, "" + throwable.getMessage(), throwable);
                });
    }

    /**
     * Sets up listeners for player control buttons
     */
    private void setupPlayerControls() {
        mCollapsedPlayControlsButton.setOnClickListener((View v) -> { this.onTogglePlayClicked();});
        mPlayControlsButton.setOnClickListener((View v) -> { this.onTogglePlayClicked();});
        mShuffleButton.setOnClickListener((View v) -> { this.onToggleShuffleClicked();});
        mRepeatButton.setOnClickListener((View v) -> { this.onToggleRepeat();});
        mPlayPreviousButton.setOnClickListener((View v) -> { this.onSkipPrevious();});
        mPlayNextButton.setOnClickListener((View v) -> { this.onSkipNext();});
    }

    private boolean onBottomNavItemClicked(MenuItem item) {
        Log.i(TAG,"item id = " + item.getItemId());
        switch (item.getItemId()) {
            case R.id.navigation_library:
                displayTopHistory();
                return true;
            default:
                return false;
        }
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
                    .setErrorCallback(mErrorCallback);
        }
    }

    /**
     * Tells Spotify app remote to pause playback
     */
    private void onPauseTrack() {
        mSpotifyAppRemote.getPlayerApi()
                .pause()
                .setResultCallback(empty -> Log.i(TAG, "Playback paused"))
                .setErrorCallback(mErrorCallback);
    }

    /**
     * Tells Spotify app remote to resume playback
     */
    private void onResumeTrack() {
        mSpotifyAppRemote.getPlayerApi()
                .resume()
                .setResultCallback(empty -> Log.i(TAG, "Playback Resumed"))
                .setErrorCallback(mErrorCallback);
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
                    .setResultCallback(empty -> {Log.i(TAG, "Shuffle successful");})
                    .setErrorCallback(mErrorCallback);
        }
    }

    /**
     * Toggles repeat for playback
     */
    public void onToggleRepeat() {
        if (mSpotifyAppRemote != null || !mSpotifyAppRemote.isConnected()) {
            mSpotifyAppRemote.getPlayerApi()
                    .toggleRepeat()
                    .setResultCallback(empty -> { Log.i(TAG, "Repeat successful");})
                    .setErrorCallback(mErrorCallback);
        }
    }

    /**
     * Sets playback to previous song
     */
    public void onSkipPrevious() {
        if (appRemoteConnected()) {
            mSpotifyAppRemote.getPlayerApi()
                    .skipPrevious()
                    .setResultCallback(empty -> { Log.i(TAG, "Play previous successful");})
                    .setErrorCallback(mErrorCallback);
        }
    }

    /**
     * Sets playback to next song
     */
    private void onSkipNext() {
        if (appRemoteConnected()) {
            mSpotifyAppRemote.getPlayerApi()
                    .skipNext()
                    .setResultCallback(empty -> { Log.i(TAG, "Play next successful");})
                    .setErrorCallback(mErrorCallback);
        }
    }

    private boolean appRemoteConnected() {
        return (mSpotifyAppRemote != null && mSpotifyAppRemote.isConnected());
    }

    /**
     * Disconnect the SpotifyAppRemote if it is active
     */
    private void disconnect() {
        if (mSpotifyAppRemote != null) {
            SpotifyAppRemote.disconnect(mSpotifyAppRemote);
            //mSpotifyAppRemote = null;
        }
    }

    /**
     * Displays Snackbar when triggered
     * @param message
     */
    private void onSnackBarEvent(String message) {
        Snackbar snackbar = Snackbar.make(mSnackbarContainer, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private String formatTime(long milliseconds) {
        long minutes = (milliseconds / 1000) / 60;
        long seconds = (milliseconds / 1000) % 60;
        String secondsStr = Long.toString(seconds);
        String secs;
        if (secondsStr.length() >= 2) {
            secs = secondsStr.substring(0, 2);
        } else {
            secs = "0" + secondsStr;
        }

        return minutes + ":" + secs;
    }


    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
