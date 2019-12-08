package com.example.android.androidify.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.androidify.BuildConfig;
import com.example.android.androidify.R;
import com.example.android.androidify.api.Session;
import com.example.android.androidify.api.models.PublicUser;
import com.example.android.androidify.fragments.common.MediaItemDialogFragment;
import com.example.android.androidify.fragments.search.SearchFragment;
import com.example.android.androidify.model.EventObserver;
import com.example.android.androidify.model.NavigationEvent;
import com.example.android.androidify.model.SnackbarMessage;
import com.example.android.androidify.repository.SpotifySearchSuggestionsProvider;
import com.example.android.androidify.utils.Constants;
import com.example.android.androidify.utils.MusicColorUtils;
import com.example.android.androidify.view.MediaPlaybar;
import com.example.android.androidify.view.MediaProgressBar;
import com.example.android.androidify.viewmodel.FactoryViewModel;
import com.example.android.androidify.viewmodel.MainActivityViewModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.android.appremote.api.error.AuthenticationFailedException;
import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp;
import com.spotify.android.appremote.api.error.LoggedOutException;
import com.spotify.android.appremote.api.error.NotLoggedInException;
import com.spotify.android.appremote.api.error.OfflineModeException;
import com.spotify.android.appremote.api.error.SpotifyConnectionTerminatedException;
import com.spotify.android.appremote.api.error.SpotifyDisconnectedException;
import com.spotify.android.appremote.api.error.SpotifyRemoteServiceException;
import com.spotify.android.appremote.api.error.UnsupportedFeatureVersionException;
import com.spotify.android.appremote.api.error.UserNotAuthorizedException;
import com.spotify.protocol.client.ErrorCallback;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.PlayerOptions;
import com.spotify.protocol.types.PlayerRestrictions;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Repeat;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.palette.graphics.Palette;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class MainActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    private static final String TAG = "SPOTIFY_ACTIVITY";

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Inject
    FactoryViewModel mFactoryModel;

    @Inject
    Session mSession;

    @BindView(R.id.top_app_bar)
    AppBarLayout mTopAppBar;

    @BindView(R.id.root_view)
    CoordinatorLayout mRootView;

    @BindView(R.id.now_playing_bottom_sheet_layout)
    ConstraintLayout mNowPlayingContainer;

    @BindView(R.id.now_playing_collapsed_bar)
    ConstraintLayout mCollapsedNowPlayingBar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.now_playing_title)
    TextView mNowPlayingTextView;

    @BindView(R.id.now_playing_card_title)
    TextView mNowPlayingCardTitle;

    @BindView(R.id.now_playing_card_artist)
    TextView mNowPlayingCardArtist;

    @BindView(R.id.now_playing_context_button)
    ImageButton mNowPlayingContextButton;

    @BindView(R.id.now_playing_collapsed_playback)
    ImageButton mCollapsedPlayControlsButton;

    @BindView(R.id.collapsed_progress_bar)
    MediaProgressBar mCollapsedProgressBar;

    @BindView(R.id.now_playing_album_art)
    ImageView mExpandingCoverArt;

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

    @BindView(R.id.now_playing_card)
    MaterialCardView mNowPlayingCard;

    @BindView(R.id.now_playing_playbar)
    MediaPlaybar mMediaPlaybar;

    @BindView(R.id.toolbar)
    MaterialToolbar mToolbar;

    @BindView(R.id.nav_drawer)
    NavigationView mNavView;

    @BindView(R.id.dim_overlay)
    View mDimOverlay;

    private AppBarConfiguration mAppBarConfiguration;

    private MainActivityViewModel mViewModel;

    // Used to play music and communicate with the Spotify app
    private SpotifyAppRemote mSpotifyAppRemote;

    private BottomSheetBehavior mBottomSheetBehavior;

    //private MediaSeekBar mNowPlayingSeekBar;
    private NavController mNavController;

    SearchView mSearchView;

    private Subscription<PlayerState> mPlayerStateListener;

    private final ErrorCallback mErrorCallback = throwable -> logError(throwable, "Boom!");

    private final Subscription.EventCallback<PlayerState> mPlayerStateEventCallback = new Subscription.EventCallback<PlayerState>() {
        @Override
        public void onEvent(PlayerState playerState) {
            if (playerState != null && playerState.track != null ) {
                // Update seekbar
         /*       Log.i(TAG, playerState.track.name + " paused = " + playerState.isPaused);
                Log.i(TAG, "player state update");*/

                if (mMediaPlaybar != null) {
                    if (playerState.playbackSpeed > 0 && !playerState.isPaused) {
                        mMediaPlaybar.start();
                        mCollapsedProgressBar.play();
                    } else if (playerState.isPaused || playerState.playbackSpeed <= 0) {
                        mMediaPlaybar.stop();
                        mCollapsedProgressBar.pause();
                    }
                }

                // Update play/pause controls
                if (playerState.isPaused) {
                    mCollapsedPlayControlsButton.setImageResource(R.drawable.ic_play_circle_24px);
                    mPlayControlsButton.setImageResource(R.drawable.ic_play_circle_filled);
                } else {
                    mCollapsedPlayControlsButton.setImageResource(R.drawable.ic_pause_circle_24px);
                    mPlayControlsButton.setImageResource(R.drawable.ic_pause_circle_filled);
                }

                String trackName = playerState.track.name;
                String artistName = playerState.track.artist.name;

                // Update track description if necessary
                if (!mNowPlayingTextView.getText().toString().equals(trackName)) {
                    mNowPlayingTextView.setText(trackName);
                    mNowPlayingCardTitle.setText(trackName);
                }

                mNowPlayingCardArtist.setText(artistName);


                setControlsEnabledStatus(playerState.playbackRestrictions);
                updateControls(playerState.playbackOptions);

/*            // Get image
            mSpotifyAppRemote.getImagesApi()
                    .getImage(playerState.track.imageUri, Image.Dimension.LARGE)
                    .setResultCallback(bitmap -> {
                        //mNowPlayingImage.setImageBitmap(bitmap);
                        mExpandingCoverArt.setImageBitmap(bitmap);
                        updateCardBackground(bitmap);
                    });*/

                MainActivity.this.onGetImage(playerState.track.imageUri);

                // Update seekbar duration and position

                long duration = playerState.track.duration;
                long position = playerState.playbackPosition;


                mMediaPlaybar.setDuration(duration);
                mMediaPlaybar.update(position);

                mCollapsedProgressBar.setDuration(duration);
                mCollapsedProgressBar.update(position);

                if (mBottomSheetBehavior != null && mNowPlayingContainer != null) {
                    mNowPlayingContainer.setVisibility(View.VISIBLE);
                }

            } else {
                pauseTracking();

                if (mNowPlayingContainer != null) {
                    mNowPlayingContainer.setVisibility(View.GONE);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        mNavController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                int id = destination.getId();


/*
                if (id == R.id.libraryFragment) {
                    mSession.invalidate();
                }
*/

                if (id == R.id.topHistoryFragment || id == R.id.searchFragment || id == R.id.libraryFragment) {
                    Objects.requireNonNull(getSupportActionBar()).show();
                } else if (getSupportActionBar() != null) {
                    Log.i(TAG, "removing app bar");
                    getSupportActionBar().hide();
                }
            }
        });
        mNowPlayingCardTitle.setSelected(true);
        mViewModel = ViewModelProviders.of(this, mFactoryModel).get(MainActivityViewModel.class);
        mViewModel.getUserProfile().observe(this, userResponse -> {
            if (userResponse != null) {
                switch (userResponse.status) {
                    case SUCCESS:
                        PublicUser user = userResponse.data;
                        View headerView = mNavView.inflateHeaderView(R.layout.layout_nav_header);
          /*              ImageView profileImageView = headerView.findViewById(R.id.user_profile_image);
                        ImageUtils.displayImage(profileImageView, user.images, R.drawable.ic_audio);*/
                        TextView displayName = headerView.findViewById(R.id.user_display_name_tv);
                        displayName.setText(user.display_name);
                        mViewModel.setCurrentUserId(user.id);
                        break;
                }
            }
        });
        setupDrawer();
        initBottomSheet();
        setupPlayerControls();
        setupViewModelObservers();
        handleIntent(getIntent());
    }

    @Override
    protected void onStart() {
        super.onStart();
        pauseTracking();
        connectToSpotify();
    }

    @Override
    protected void onStop() {
        super.onStop();
        pauseTracking();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    private void setupDrawer() {
       /* setSupportActionBar(mToolbar);*/
        Set<Integer> topDestinations = new HashSet<>();
        topDestinations.add(R.id.topHistoryFragment);
        topDestinations.add(R.id.searchFragment);
        topDestinations.add(R.id.libraryFragment);
        mAppBarConfiguration = new AppBarConfiguration.Builder(topDestinations)
                .setDrawerLayout(mDrawerLayout)
                .build();

        NavigationUI.setupWithNavController(mNavView, mNavController);

        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
/*        NavigationUI.setupWithNavController(mNavView, mNavController);
        mNavView.setNavigationItemSelectedListener(this);*/
    }

    private void configureToolbar(MaterialToolbar toolbar) {
        if (toolbar != null) {
/*            setSupportActionBar(toolbar);
            NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);*/
            /*mNavView.setNavigationItemSelectedListener(this);*/
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }


/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_bar, menu);
*//*        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        configureSearchView();*//*
        return true;
    }*/

/*    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_open_search) {
            mNavController.navigate(R.id.action_global_searchFragment);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/


    private void configureSearchView() {
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        mSearchView.setQueryRefinementEnabled(true);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                handleSearch(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            handleSearch(query);
        }
    }

    private void handleSearch(String s) {
        mSearchView.clearFocus();
        //searchView.setIconified(true);
        Log.i(TAG, s);
        mSearchView.setQuery(s, false);
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(MainActivity.this,
                SpotifySearchSuggestionsProvider.AUTHORITY, SpotifySearchSuggestionsProvider.MODE);
        suggestions.saveRecentQuery(s, null);
        displaySearchResults(s);
    }

    private void displaySearchResults(String query) {
        Bundle bundle = new Bundle();
        bundle.putString(SearchFragment.ARG_SEARCH_QUERY, query);
        mNavController.navigate(R.id.action_global_searchFragment, bundle);
    }

    private void setupViewModelObservers() {

        mViewModel.getSnackbarMessage().observe(this,
                new EventObserver<SnackbarMessage>(this::onSnackBarEvent));

        mViewModel.getPlayEvent().observe(this, new EventObserver<String>(uri -> {
            onPlayUri(uri);
        }));

        mViewModel.getErrorEvent().observe(this, new EventObserver<String>(this::onError));

        mViewModel.getToolbarTitle().observe(this, title -> {
            if (title != null && getSupportActionBar() != null) {
                /*getSupportActionBar().setTitle(title);*/
            }
        });

        mViewModel.getOpenDialogEvent().observe(this, new EventObserver<Void>(empty -> {
            MediaItemDialogFragment mediaItemDialogFragment = (MediaItemDialogFragment) getSupportFragmentManager()
                    .findFragmentByTag(MediaItemDialogFragment.TAG);
            if (mediaItemDialogFragment == null) {
                Log.i(TAG, " Creating dialog fragment");
                mediaItemDialogFragment = new MediaItemDialogFragment();
            }

            mediaItemDialogFragment.show(getSupportFragmentManager(), MediaItemDialogFragment.TAG);
        }));

        mViewModel.getNavigationEvent().observe(this, new EventObserver<NavigationEvent>(this::onNavigationEvent));

        mViewModel.getToolbar().observe(this, new EventObserver<MaterialToolbar>(this::configureToolbar));
    }

    private void onError(String error) {
        if (error.equals(Constants.UNAUTHORIZED)) {
            Log.e(TAG, "Api 401 error");
            mSession.invalidate();
            Intent authIntent = new Intent(MainActivity.this, SpotifyLoginActivity.class);
            /*mViewModel.disconnect();*/
            startActivity(authIntent);
        } else  {
            /*onSnackBarEvent(getResources().getString(R.string.error_message), true);*/
        }
        Log.e(TAG, "Playback error");
    }

    private void connectToSpotify() {
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);

        // Attempt to connect with Spotify
        SpotifyAppRemote.connect(
                getApplication(),
                new ConnectionParams.Builder(BuildConfig.CLIENT_ID)
                        .setRedirectUri(BuildConfig.REDIRECT_URL)
                        .showAuthView(false)
                        .build(),
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        //SpotifyActivity.this.mSpotifyAppRemote = spotifyAppRemote;
                        Log.i(TAG, "connected to spotify");
                        mSpotifyAppRemote = spotifyAppRemote;
                        MainActivity.this.onConnected();
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        if (error instanceof SpotifyRemoteServiceException) {
                            if (error.getCause() instanceof SecurityException) {
                                logError(error, "SecurityException");
                            } else if (error.getCause() instanceof IllegalStateException) {
                                logError(error, "IllegalStateException");
                            }
                        } else if (error instanceof NotLoggedInException) {
                            logError(error, "NotLoggedInException");
                        } else if (error instanceof AuthenticationFailedException) {
                            logError(error, "AuthenticationFailedException");
                        } else if (error instanceof CouldNotFindSpotifyApp) {
                            logError(error, "CouldNotFindSpotifyApp");
                        } else if (error instanceof LoggedOutException) {
                            logError(error, "LoggedOutException");
                        } else if (error instanceof OfflineModeException) {
                            logError(error, "OfflineModeException");
                        } else if (error instanceof UserNotAuthorizedException) {
                            logError(error, "UserNotAuthorizedException");
                        } else if (error instanceof UnsupportedFeatureVersionException) {
                            logError(error, "UnsupportedFeatureVersionException");
                        } else if (error instanceof SpotifyDisconnectedException) {
                            logError(error, "SpotifyDisconnectedException");
                        } else if (error instanceof SpotifyConnectionTerminatedException) {
                            logError(error, "SpotifyConnectionTerminatedException");
                        } else {
                            logError(error, String.format("Connection failed: %s", error));
                        }
                    }
                }
        );

    }

    private void onConnected() {
        Log.i(TAG, "connecting to player state");
        if (appRemoteConnected()) {
            if (mPlayerStateListener != null && !mPlayerStateListener.isCanceled()) {
                mPlayerStateListener.cancel();
                mPlayerStateListener = null;
            }

            mPlayerStateListener = (Subscription<PlayerState>) mSpotifyAppRemote.getPlayerApi()
                    .subscribeToPlayerState()
                    .setEventCallback(mPlayerStateEventCallback)
                    .setLifecycleCallback(new Subscription.LifecycleCallback() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onStop() {

                        }
                    })
                    .setErrorCallback(throwable -> {
                        logError(throwable, "Failed to subscribe to player state");
                    });
        }
    }

    private void onNavigationEvent(NavigationEvent navigationEvent) {

    }

    /**
     * Sets up listeners for player control buttons
     */
    private void setupPlayerControls() {
        mNowPlayingContextButton.setOnClickListener((View v) -> this.onContextMenuClicked());
        mCollapsedPlayControlsButton.setOnClickListener((View v) -> this.onTogglePlayClicked());
        mPlayControlsButton.setOnClickListener((View v) -> { this.onTogglePlayClicked();});
        mShuffleButton.setOnClickListener((View v) -> { this.onToggleShuffleClicked();});
        mRepeatButton.setOnClickListener((View v) -> { this.onToggleRepeat();});
        mPlayPreviousButton.setOnClickListener((View v) -> { this.onSkipPrevious();});
        mPlayNextButton.setOnClickListener((View v) -> { this.onSkipNext();});
        mMediaPlaybar.setOnSeekListener((long position) -> this.onSeek(position));
    }

    /**
     * Enables and disables player controls based off the current player state
     * @param restrictions status of restricted actions
     */
    private void setControlsEnabledStatus(PlayerRestrictions restrictions) {
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

    private void initBottomSheet() {
        mBottomSheetBehavior = BottomSheetBehavior.from(mNowPlayingContainer);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        mCollapsedProgressBar.setVisibility(View.VISIBLE);
                        fadeView(mNowPlayingCard, 0f);
                        updateBottomSheetLayout(0f);
                        mCollapsedPlayControlsButton.setVisibility(View.VISIBLE);
                        //mExpandingCoverArt.setImageAlpha(100);
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        /*mCollapsedNowPlayingBar.setVisibility(View.GONE);*/
                        fadeView(mNowPlayingCard, 1f);
                        mCollapsedProgressBar.setVisibility(View.INVISIBLE);
                        updateBottomSheetLayout(1f);
                        //mExpandingCoverArt.setImageAlpha(85);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                updateBottomSheetLayout(slideOffset);
                fadeView(mNowPlayingCard, slideOffset);
                if (slideOffset > 0) {
                    mDimOverlay.setVisibility(View.VISIBLE);
                    mDimOverlay.setAlpha(slideOffset);
                } else if (slideOffset == 0f) {
                    mDimOverlay.setVisibility(View.GONE);

                }
            }
        });

        /*mExpandNowPlayingButton.setOnClickListener((view -> mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)));*/
        /*mCollapseNowPlayingButton.setOnClickListener((view -> mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)));*/
    }

    private void updateBottomSheetLayout(float slideOffset) {
        //Log.i(TAG, "slideOffset = " + slideOffset);
        int parentHeight = mRootView.getHeight();
        int maxHeight = (int) (parentHeight * 0.55);
        int defaultDimensions = getDefaultDimensions();
        //int expandedHeight = (int)(((parentHeight * slideOffset) / 2) + getDefaultDimensions());
        int expandedHeight = (int)((maxHeight * slideOffset) + (defaultDimensions - (defaultDimensions * slideOffset)));
        //int minHeight = Math.max(getDefaultDimensions(), expandedHeight);
        int newHeight = expandedHeight < maxHeight ? expandedHeight : maxHeight;
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(mRootView.getWidth(),
                newHeight);
        //int imgWidth = (int) Math.max(getDefaultDimensions(), mRootView.getWidth() * slideOffset);
        int expandedImgWidth = (int)((mRootView.getWidth() * slideOffset) + (defaultDimensions - (defaultDimensions * slideOffset)));
        int imgWidth = expandedImgWidth < mRootView.getWidth() ? expandedImgWidth : -1;
        mCollapsedNowPlayingBar.setLayoutParams(params);
        mExpandingCoverArt.setLayoutParams(new ConstraintLayout.LayoutParams(imgWidth, newHeight));
    }


    private int getDefaultDimensions() {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                64,
                getResources().getDisplayMetrics());
    }

    private void fadeView(View v, float offset) {
        if (offset > 0) {
            v.setAlpha(offset);
        }
    }

    public void onContextMenuClicked() {

    }

    /**
     * Tells Spotify to start playback of selected track
     */
    public void onPlayUri(String uri) {
        //Log.i(TAG, uri);
        Log.i(TAG, " connected = " + appRemoteConnected());
        if (appRemoteConnected() && uri != null) {
            Log.i(TAG, "starting playback");
            mSpotifyAppRemote.getPlayerApi()
                    .play(uri)
                    .setErrorCallback(this::onPlaybackError);
        }
    }

    /**
     * Tells Spotify app remote to pause playback
     */
    public void onPauseTrack() {
        if (appRemoteConnected()) {
            mSpotifyAppRemote.getPlayerApi()
                    .pause()
                    .setErrorCallback(this::onPlaybackError);
        }
    }

    /**
     * Tells Spotify app remote to resume playback
     */
    public void onResumeTrack() {
        if (appRemoteConnected()) {
            mSpotifyAppRemote.getPlayerApi()
                    .resume()
                    .setErrorCallback(this::onPlaybackError);
        }
    }

    /**
     * Handles playback controls
     */
    public void onTogglePlayClicked() {
        if (appRemoteConnected()) {
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
        if (appRemoteConnected()) {
            mSpotifyAppRemote.getPlayerApi()
                    .toggleShuffle()
                    .setErrorCallback(this::onPlaybackError);
        }
    }

    /**
     * Toggles repeat for playback
     */
    public void onToggleRepeat() {
        if (appRemoteConnected()) {
            mSpotifyAppRemote.getPlayerApi()
                    .toggleRepeat()
                    .setErrorCallback(this::onPlaybackError);
        }
    }

    /**
     * Seeks to given position in current track
     */
    public void onSeek(long position) {
        if (appRemoteConnected()) {
            mSpotifyAppRemote.getPlayerApi()
                    .seekTo(position)
                    .setErrorCallback(this::onPlaybackError);
        }
    }

    /**
     * Sets playback to previous song
     */
    public void onSkipPrevious() {
        if (appRemoteConnected()) {
            mSpotifyAppRemote.getPlayerApi()
                    .skipPrevious()
                    .setErrorCallback(this::onPlaybackError);
        }
    }

    /**
     * Sets playback to next song
     */
    public void onSkipNext() {
        if (appRemoteConnected()) {
            mSpotifyAppRemote.getPlayerApi()
                    .skipNext()
                    .setErrorCallback(this::onPlaybackError);
        }
    }

    public void onGetImage(ImageUri uri) {
        Log.i(TAG, "spotify image uri = " + uri);
        if (appRemoteConnected() && uri != null) {
            mSpotifyAppRemote.getImagesApi()
                    .getImage(uri, Image.Dimension.LARGE)
                    .setResultCallback(this::onImageUpdated)
                    .setErrorCallback(this::onPlaybackError);
        }
    }

    private void onImageUpdated(Bitmap bitmap) {
        mExpandingCoverArt.setImageBitmap(bitmap);
        updateCardBackground(bitmap);
    }

    private void updateCardBackground(Bitmap bitmap) {
        Palette palette = MusicColorUtils.generatePalette(bitmap);
        int swatchColor = MusicColorUtils.getColor(palette, getResources().getColor(R.color.defaultNowPlayingBackground));
        int modifiedColor = MusicColorUtils.modifyBackgroundColor(swatchColor);
        mNowPlayingCard.setCardBackgroundColor(modifiedColor);

        //animateColorChange(modifiedColor);
    }

    private void onPlaybackError(Throwable throwable) {
        Log.e(TAG, throwable.getMessage() + "Player error");
        //mPlaybackErrorEvent.setValue(new Event("Playback Error"));
    }

    public boolean appRemoteConnected() {
        return (mSpotifyAppRemote != null && mSpotifyAppRemote.isConnected());
    }

    private void logError(Throwable throwable, String msg) {
        Toast.makeText(this, "Error: " + msg, Toast.LENGTH_SHORT).show();
        Log.e(TAG, msg, throwable);
    }



    private void pauseTracking() {
        if (mMediaPlaybar.isTracking()) {
            mMediaPlaybar.stop();
        }

        if (mCollapsedProgressBar != null) {
            mCollapsedProgressBar.pause();
        }
    }

    /**
     * Displays Snackbar when triggered
     * @param snackbarMessage
     */
    private void onSnackBarEvent(SnackbarMessage snackbarMessage) {
        String message = getResources().getString(snackbarMessage.getResourceId());
        Snackbar snackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        if (snackbarMessage.getType() == SnackbarMessage.Type.ERROR) {
            view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryVariant));
        } else {
            TextView tv = view.findViewById(com.google.android.material.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
        }
        snackbar.setAnchorView(mNowPlayingContainer);
        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (mBottomSheetBehavior != null && mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
