package com.example.android.androidify.viewmodel;

import com.example.android.androidify.R;
import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.api.models.PublicUser;
import com.example.android.androidify.model.Action;
import com.example.android.androidify.model.Event;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.model.NavigationEvent;
import com.example.android.androidify.model.SnackbarMessage;
import com.example.android.androidify.repository.SpotifyRepo;
import com.google.android.material.appbar.MaterialToolbar;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {
    private static final String TAG = "MAIN_VIEW_MODEL";

    private MutableLiveData<Event<String>> mPlaySongEvent = new MutableLiveData<>();
    private MutableLiveData<String> mToolbarTitle = new MutableLiveData<>();
    /*private MutableLiveData<String> mSnackBarEvent = new MutableLiveData<>();*/
    private MediatorLiveData<Event<SnackbarMessage>> mSnackbarEvent = new MediatorLiveData<>();
    private MutableLiveData<Event<String>> mPlaybackErrorEvent = new MutableLiveData<>();
    private MutableLiveData<Event<Void>> mOpenDialogEvent = new MutableLiveData<>();
    private MutableLiveData<Event<MaterialToolbar>> mToolbarEvent = new MutableLiveData<>();
    private MutableLiveData<Event<NavigationEvent>> mNavigationEvent = new MutableLiveData<>();
    private MutableLiveData<MusicListItem> mSelectedDialogItem = new MutableLiveData<>();
    private LiveData<ApiResponse<Boolean>> mSelectedDialogItemSaveStatus;
    private LiveData<ApiResponse<PublicUser>> mUserProfile;

    private MusicListItem mCurrentSong;

    private String currentUserId;

    private SpotifyRepo mRepo;

    @Inject
    public MainActivityViewModel(SpotifyRepo repo) {
        this.mRepo = repo;
        mSelectedDialogItemSaveStatus = Transformations.switchMap(mSelectedDialogItem,
                item -> mRepo.getMediaItemSaveStatus(item, currentUserId));
    }

    public LiveData<ApiResponse<PublicUser>> getUserProfile() {
        if (mUserProfile == null) {
            mUserProfile = mRepo.getUserProfile();
        }

        return mUserProfile;
    }

    public void setCurrentUserId(String id) {
        currentUserId = id;
    }

    public MutableLiveData<MusicListItem> getSelectedDialogItem() {
        return mSelectedDialogItem;
    }

    public void setSelectedDialogItem(MusicListItem item) {
        mSelectedDialogItem.setValue(item);
    }

    public LiveData<ApiResponse<Boolean>> getSelectedDialogItemSaveStatus() {
        return mSelectedDialogItemSaveStatus;
    }


    public LiveData<Event<NavigationEvent>> getNavigationEvent() {
        return mNavigationEvent;
    }

    public void openMediaItemDialog(MusicListItem item) {
        mSelectedDialogItem.setValue(item);
        mOpenDialogEvent.setValue(new Event<Void>(null));
    }

    public LiveData<Event<Void>> getOpenDialogEvent() {
        return mOpenDialogEvent;
    }

    public void setNavigationEvent(NavigationEvent navigationEvent) {
        this.mNavigationEvent.setValue(new Event<NavigationEvent>(navigationEvent));
    }

    public void setCurrentlyPlaying(String uri) {
        if (uri != null) {
            mPlaySongEvent.setValue(new Event<String>(uri));
        }
    }

    public void setCurrentSong(MusicListItem item) {
        mCurrentSong = item;
    }

    public MusicListItem getCurrentSong() {
        return mCurrentSong;
    }

    public LiveData<Event<String>> getPlayEvent() {
        return mPlaySongEvent;
    }

    public void onErrorEvent(String error) {
        mPlaybackErrorEvent.setValue(new Event<>(error));
    }

    public LiveData<Event<String>> getErrorEvent() {
        return mPlaybackErrorEvent;
    }

    public void setToolbarTitle(String title) {
        mToolbarTitle.setValue(title);
    }

    public LiveData<String> getToolbarTitle() {
        return mToolbarTitle;
    }


    public void setSnackbarResource(SnackbarMessage snackbarMessage) {
        mSnackbarEvent.setValue(new Event<SnackbarMessage>(snackbarMessage));
    }

    public LiveData<Event<SnackbarMessage>> getSnackbarMessage() {
        return mSnackbarEvent;
    }

    public void setToolbar(MaterialToolbar toolbar) {
        mToolbarEvent.setValue(new Event<MaterialToolbar>(toolbar));
    }

    public LiveData<Event<MaterialToolbar>> getToolbar() {
        return mToolbarEvent;
    }


    public void toggleMediaItemLibraryStatus(MusicListItem item) {
        switch (item.type) {
            case ARTIST:
                LiveData<ApiResponse<Action>> updateArtistAction = mRepo.updateArtist(item.id);
                mSnackbarEvent.addSource(updateArtistAction, actionApiResponse -> {
                    if (actionApiResponse != null && actionApiResponse.status == ApiResponse.Status.SUCCESS) {
                        mSnackbarEvent.removeSource(updateArtistAction);
                        Action action = actionApiResponse.data;
                        int snackbarMessageResource = action.type == Action.Type.SAVE ?
                                R.string.artist_followed_message :
                                R.string.artist_unfollowed_message;

                        setSnackbarResource(SnackbarMessage.create(snackbarMessageResource));
                    }
                });
                break;
            case ALBUM:
                LiveData<ApiResponse<Action>> updateAlbumAction = mRepo.updateAlbum(item.id);
                mSnackbarEvent.addSource(updateAlbumAction, actionApiResponse -> {
                    if (actionApiResponse != null && actionApiResponse.status == ApiResponse.Status.SUCCESS) {
                        mSnackbarEvent.removeSource(updateAlbumAction);
                        Action action = actionApiResponse.data;
                        int snackbarMessageResource = action.type == Action.Type.SAVE ?
                                R.string.album_follow_message :
                                R.string.album_unfollow_message;

                        setSnackbarResource(SnackbarMessage.create(snackbarMessageResource));
                    }
                });
                break;
            case TRACK:
                LiveData<ApiResponse<Action>> updateTrackAction = mRepo.updateTrack(item.id);
                mSnackbarEvent.addSource(updateTrackAction, actionApiResponse -> {
                    if (actionApiResponse != null && actionApiResponse.status == ApiResponse.Status.SUCCESS) {
                        mSnackbarEvent.removeSource(updateTrackAction);
                        Action action = actionApiResponse.data;
                        int snackbarMessageResource = action.type == Action.Type.SAVE ?
                                R.string.track_liked_message :
                                R.string.track_unlike_message;

                        setSnackbarResource(SnackbarMessage.create(snackbarMessageResource));
                    }
                });
                break;
            case PLAYLIST:
                if (currentUserId != null) {
                    LiveData<ApiResponse<Action>> updatePlaylistAction = mRepo.updatePlaylist(item.id, currentUserId);
                    mSnackbarEvent.addSource(updatePlaylistAction, actionApiResponse -> {
                        if (actionApiResponse != null) {
                            switch (actionApiResponse.status) {
                                case SUCCESS:
                                    mSnackbarEvent.removeSource(updatePlaylistAction);
                                    Action action = actionApiResponse.data;
                                    int snackbarMessageResource = action.type == Action.Type.SAVE ?
                                            R.string.playlist_follow_message :
                                            R.string.playlist_unfollow_message;

                                    setSnackbarResource(SnackbarMessage.create(snackbarMessageResource));
                                    break;
                                case ERROR:
                                    setSnackbarResource(SnackbarMessage.error());
                                    break;
                            }
                        }
                    });
                } else  {
                    setSnackbarResource(SnackbarMessage.error());
                }
                break;
        }
    }




}

/*public class MainActivityViewModel extends ViewModel {
    private static final String TAG = "MAIN_VIEW_MODEL";
    private final MutableLiveData<String> currentlyPlaying = new MutableLiveData<>();
    private MutableLiveData<Event<String>> mSnackBarEvent = new MutableLiveData<>();
    private MutableLiveData<Event<String>> mPlaybackErrorEvent = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> mOnConnectEvent = new MutableLiveData<>();
    private final MutableLiveData<PlayerState> mPlayerState = new MutableLiveData<>();
    private MutableLiveData<Bitmap> mImageBitmap = new MutableLiveData<>();
    private MutableLiveData<String> mToolbarTitle = new MutableLiveData<>();
    private SpotifyAppRemote mSpotifyAppRemote;

    private Subscription<PlayerState> mPlayerStateListener;


    private final Subscription.EventCallback<PlayerState> mPlayerStateCallback = new Subscription.EventCallback<PlayerState>() {
        @Override
        public void onEvent(PlayerState playerState) {
            Log.i(TAG, "player state event");
            Log.i(TAG, "name = " + playerState.track.name);
            mPlayerState.setValue(playerState);
        }
    };


    public MainActivityViewModel() {};


    public void init(SpotifyAppRemote remote) {
        this.mSpotifyAppRemote = remote;
    }

    *//**
     * Sets up subscription to observe player state changes
     *//*
    public void subscribeToPlayerState() {
        if (appRemoteConnected()) {
            unsubscribeToPlayerState();
            Log.i(TAG, "subscribing to player state");
            // Set up player state listener
            mPlayerStateListener = (Subscription<PlayerState>) mSpotifyAppRemote.getPlayerApi()
                    .subscribeToPlayerState()
                    .setEventCallback(mPlayerStateCallback)
                    .setErrorCallback(this::onPlaybackError);
        }
    }

    *//**
     * Handles updates sent by spotify
     * @param playerState the current state of the player
     *//*
    private void onPlayerStateUpdated(PlayerState playerState) {
        if (mPlayerState != null) {
            Log.i(TAG, playerState.track.name + " paused = " + playerState.isPaused + " position = " + playerState.playbackPosition);
            mPlayerState.setValue(playerState);
        }
    }

    public SpotifyAppRemote getSpotifyAppRemote() {
        return mSpotifyAppRemote;
    }

    public void onConnect(Boolean isConnected) {
        mOnConnectEvent.setValue(new Event<Boolean>(isConnected));
    }

    public LiveData<Event<Boolean>> getConnectStatus() {
        return mOnConnectEvent;
    }

    *//**
     * Sets the uri to play
     *//*
    public void setCurrentlyPlaying(String uri) {
        currentlyPlaying.setValue(uri);
    }

    *//**
     * Returns the selected uri
     * @return
     *//*
    public LiveData<String> getCurrentlyPlaying() {
        return currentlyPlaying;
    }

    *//**
     * Returns the snackbar message to be displayed
     *//*
    public LiveData<Event<String>> getSnackbarEvent() {
        return mSnackBarEvent;
    }

    public LiveData<Event<String>> getPlaybackErrorEvent() { return mPlaybackErrorEvent;}

    public LiveData<PlayerState> getPlayerState() { return mPlayerState;}
    *//**
     * Tells activity to display a Snackbar
     * @param message the message to display in the Snackbar
     *//*
    public void setSnackBarMessage(String message) {
        mSnackBarEvent.setValue(new Event(message));
    }


    public void setToolbarTitle(String title) {
        mToolbarTitle.setValue(title);
    }

    public LiveData<String> getToolbarTitle() {
        return mToolbarTitle;
    }


    *//**
     * Tells Spotify to start playback of selected track
     *//*
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

    *//**
     * Tells Spotify app remote to pause playback
     *//*
    public void onPauseTrack() {
        if (appRemoteConnected()) {
            mSpotifyAppRemote.getPlayerApi()
                    .pause()
                    .setErrorCallback(this::onPlaybackError);
        }
    }

    *//**
     * Tells Spotify app remote to resume playback
     *//*
    public void onResumeTrack() {
        if (appRemoteConnected()) {
            mSpotifyAppRemote.getPlayerApi()
                    .resume()
                    .setErrorCallback(this::onPlaybackError);
        }
    }

    *//**
     * Handles playback controls
     *//*
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

    *//**
     * Toggles shuffle for users playback
     *//*
    public void onToggleShuffleClicked() {
        if (appRemoteConnected()) {
            mSpotifyAppRemote.getPlayerApi()
                    .toggleShuffle()
                    .setErrorCallback(this::onPlaybackError);
        }
    }

    *//**
     * Toggles repeat for playback
     *//*
    public void onToggleRepeat() {
        if (appRemoteConnected()) {
            mSpotifyAppRemote.getPlayerApi()
                    .toggleRepeat()
                    .setErrorCallback(this::onPlaybackError);
        }
    }

    *//**
     * Seeks to given position in current track
     *//*
    public void onSeek(long position) {
        if (appRemoteConnected()) {
            mSpotifyAppRemote.getPlayerApi()
                    .seekTo(position)
                    .setErrorCallback(this::onPlaybackError);
        }
    }

    *//**
     * Sets playback to previous song
     *//*
    public void onSkipPrevious() {
        if (appRemoteConnected()) {
            mSpotifyAppRemote.getPlayerApi()
                    .skipPrevious()
                    .setErrorCallback(this::onPlaybackError);
        }
    }

    *//**
     * Sets playback to next song
     *//*
    public void onSkipNext() {
        if (appRemoteConnected()) {
            mSpotifyAppRemote.getPlayerApi()
                    .skipNext()
                    .setErrorCallback(this::onPlaybackError);
        }
    }

    public void onGetImage(ImageUri uri) {
        if (appRemoteConnected() && uri != null) {
            mSpotifyAppRemote.getImagesApi()
                    .getImage(uri, Image.Dimension.LARGE)
                    .setResultCallback(this::setImageBitmap)
                    .setErrorCallback(this::onPlaybackError);
        }
    }

    private void setImageBitmap(Bitmap bitmap) {
        mImageBitmap.setValue(bitmap);
    }

    public LiveData<Bitmap> getPlayerStateImage() {
        return mImageBitmap;
    }

    private void onPlaybackError(Throwable throwable) {
        Log.e(TAG, throwable.getMessage() + "Player error");
        mPlaybackErrorEvent.setValue(new Event("Playback Error"));
    }

    public void onErrorEvent(String error) {
        mPlaybackErrorEvent.setValue(new Event<>(error));
    }

    public boolean appRemoteConnected() {
        return (mSpotifyAppRemote != null && mSpotifyAppRemote.isConnected());
    }

    *//**
     * Disconnect the SpotifyAppRemote if it is active
     *//*
    public void disconnect() {
        if (appRemoteConnected()) {
            unsubscribeToPlayerState();
            SpotifyAppRemote.disconnect(mSpotifyAppRemote);
            mSpotifyAppRemote = null;
        }
    }

    public void unsubscribeToPlayerState() {
        if (mPlayerStateListener != null && !mPlayerStateListener.isCanceled()) {
            Log.i(TAG, "unsubscribing to player state");
            mPlayerStateListener.cancel();
            mPlayerStateListener = null;
        }
    }

    @Override
    protected void onCleared() {
        unsubscribeToPlayerState();
        disconnect();
        super.onCleared();
    }
}*/
