package com.example.android.androidify;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.androidify.activities.SpotifyLoginActivity;
import com.example.android.androidify.fragments.search.SearchFragment;
import com.example.android.androidify.repository.SpotifySearchSuggestionsProvider;
import com.example.android.androidify.utils.Constants;
import com.example.android.androidify.utils.MusicColorUtils;
import com.example.android.androidify.view.MediaPlaybar;
import com.example.android.androidify.view.MediaProgressBar;
import com.example.android.androidify.viewmodel.MainActivityViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.PlayerOptions;
import com.spotify.protocol.types.PlayerRestrictions;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Repeat;

import javax.inject.Inject;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.palette.graphics.Palette;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class SpotifyActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    private static final String TAG = "SPOTIFY_ACTIVITY";

    @BindView(R.id.root_view)
    CoordinatorLayout mRootView;

    @BindView(R.id.snackbar_container)
    CoordinatorLayout mSnackbarContainer;

    @BindView(R.id.now_playing_bottom_sheet_layout)
    ConstraintLayout mNowPlayingContainer;

    @BindView(R.id.now_playing_collapsed_bar)
    ConstraintLayout mCollapsedNowPlayingBar;

    @BindView(R.id.now_playing_title)
    TextView mNowPlayingTextView;

    @BindView(R.id.now_playing_card_title)
    TextView mNowPlayingCardTitle;

    @BindView(R.id.now_playing_card_artist)
    TextView mNowPlayingCardArtist;

    @BindView(R.id.now_playing_collapsed_playback)
    ImageButton mCollapsedPlayControlsButton;

/*    @BindView(R.id.now_playing_cover_art)
    ImageView mNowPlayingImage;*/

/*    @BindView(R.id.now_playing_expand)
    ImageButton mExpandNowPlayingButton;*/

/*
    @BindView(R.id.now_playing_collapse_button)
    ImageButton mCollapseNowPlayingButton;
*/

    @BindView(R.id.collapsed_progress_bar)
    MediaProgressBar mCollapsedProgressBar;

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

/*    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;*/

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.dim_overlay)
    View mDimOverlay;

/*    @BindView(R.id.search_view)
    SearchView mSearchView;*/

    @BindView(R.id.now_playing_album_art)
    ImageView mExpandingCoverArt;

/*    @BindView(R.id.collapse_button_scrim)
    View mCollapseButtonScrim;*/

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    private MainActivityViewModel mViewModel;

    // Used to play music and communicate with the Spotify app
    private SpotifyAppRemote mSpotifyAppRemote;

    private BottomSheetBehavior mBottomSheetBehavior;
    //private MediaSeekBar mNowPlayingSeekBar;
    private NavController mNavController;

    @ColorInt
    private Integer mBackgroundColor;

    SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        mNowPlayingCardTitle.setSelected(true);
        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        initBottomSheet();
        setupViewModelObservers();
        handleIntent(getIntent());

    }

    @Override
    protected void onStart() {
        super.onStart();
        //mViewModel.disconnect();
        pauseTracking();
        //connectToSpotify();
    }

    @Override
    protected void onStop() {
        super.onStop();
        pauseTracking();
        //mViewModel.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_bar, menu);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        configureSearchView();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_open_search) {
            mNavController.navigate(R.id.action_global_searchFragment);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


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
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(SpotifyActivity.this,
                SpotifySearchSuggestionsProvider.AUTHORITY, SpotifySearchSuggestionsProvider.MODE);
        suggestions.saveRecentQuery(s, null);
        displaySearchResults(s);
    }

/*    private void connectToSpotify() {
        //mViewModel.disconnect();
        // Attempt to connect with Spotify
        if (!mViewModel.appRemoteConnected()) {
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
                            SpotifyActivity.this.mViewModel.init(spotifyAppRemote);
                            SpotifyActivity.this.mViewModel.onConnect(true);
                            SpotifyActivity.this.onConnected();
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
                            *//*SpotifyActivity.this.mViewModel.onConnect(false);*//*
                        }
                    }
            );
        }
    }*/

    private void logError(Throwable throwable, String msg) {
        Toast.makeText(this, "Error: " + msg, Toast.LENGTH_SHORT).show();
        Log.e(TAG, msg, throwable);
    }

/*    private void onConnected() {
        //mNowPlayingSeekBar = new MediaSeekBar(mSeekBar, mTimeElapsed, mDuration, mViewModel);
        Log.i(TAG, "setting up observers");
        mViewModel.subscribeToPlayerState();
        mViewModel.getPlayerState().observe(this, this::onPlayerStateChanged);
        mViewModel.getPlayerStateImage().observe(this, this::onImageUpdated);
        setupPlayerControls();
    }*/

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
                        mCollapsedPlayControlsButton.setVisibility(View.VISIBLE);;
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

    private void setupViewModelObservers() {
/*
        mViewModel.getSnackbarMessage().observe(this, new EventObserver<Integer>(resourceId -> {
            String message = getResources().getString(resourceId);
            onSnackBarEvent(message);
        }));
*/

        /*mViewModel.getPlaybackErrorEvent().observe(this, new EventObserver<String>(this::onError));*/

        mViewModel.getToolbarTitle().observe(this, title -> {
            if (title != null && getSupportActionBar() != null) {
                getSupportActionBar().setTitle(title);
            }
        });
    }

/*    *//**
     * Sets up listeners for player control buttons
     *//*
    private void setupPlayerControls() {
        mCollapsedPlayControlsButton.setOnClickListener((View v) -> { mViewModel.onTogglePlayClicked();});
        mPlayControlsButton.setOnClickListener((View v) -> { mViewModel.onTogglePlayClicked();});
        mShuffleButton.setOnClickListener((View v) -> { mViewModel.onToggleShuffleClicked();});
        mRepeatButton.setOnClickListener((View v) -> { mViewModel.onToggleRepeat();});
        mPlayPreviousButton.setOnClickListener((View v) -> { mViewModel.onSkipPrevious();});
        mPlayNextButton.setOnClickListener((View v) -> { mViewModel.onSkipNext();});
        mMediaPlaybar.setOnSeekListener((long position) -> mViewModel.onSeek(position));
    }*/

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

    private void displaySearchResults(String query) {
        Bundle bundle = new Bundle();
        bundle.putString(SearchFragment.ARG_SEARCH_QUERY, query);
        mNavController.navigate(R.id.action_global_searchFragment, bundle);
    }

    private void onError(String error) {
        if (error.equals(Constants.UNAUTHORIZED)) {
            Intent authIntent = new Intent(SpotifyActivity.this, SpotifyLoginActivity.class);
            /*mViewModel.disconnect();*/
            startActivity(authIntent);
            SpotifyActivity.this.finish();
        }
        Log.e(TAG, "Playback error");
    }


    private void onPlayerStateChanged(PlayerState playerState) {
        if (playerState != null) {
            // Update seekbar
            Log.i(TAG, playerState.track.name + " paused = " + playerState.isPaused);
            Log.i(TAG, "player state update");
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

            /*mViewModel.onGetImage(playerState.track.imageUri);*/

            // Update seekbar duration and position

            long duration = playerState.track.duration;
            long position = playerState.playbackPosition;


            mMediaPlaybar.setDuration(duration);
            mMediaPlaybar.update(position);

            mCollapsedProgressBar.setDuration(duration);
            mCollapsedProgressBar.update(position);
        } else {
            pauseTracking();
        }
    }

    private void onImageUpdated(Bitmap bitmap) {
        mExpandingCoverArt.setImageBitmap(bitmap);
        updateCardBackground(bitmap);
    }

    private void pauseTracking() {
        if (mMediaPlaybar.isTracking()) {
            mMediaPlaybar.stop();
        }

        if (mCollapsedProgressBar != null) {
            mCollapsedProgressBar.pause();
        }
    }

    private void updateCardBackground(Bitmap bitmap) {
        Palette palette = MusicColorUtils.generatePalette(bitmap);
        int swatchColor = MusicColorUtils.getColor(palette, getResources().getColor(R.color.defaultNowPlayingBackground));
        int modifiedColor = MusicColorUtils.modifyBackgroundColor(swatchColor);
        mNowPlayingCard.setCardBackgroundColor(modifiedColor);
        //animateColorChange(modifiedColor);
    }

    /**
     * Displays Snackbar when triggered
     * @param message
     */
    private void onSnackBarEvent(String message) {
        Snackbar snackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        TextView tv = view.findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        //tv.setTypeface(null, Typeface.BOLD);
        snackbar.setAnchorView(mNowPlayingContainer);
        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        if (mBottomSheetBehavior != null && mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
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
