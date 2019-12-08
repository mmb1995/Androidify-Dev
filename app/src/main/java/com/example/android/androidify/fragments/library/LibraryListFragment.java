package com.example.android.androidify.fragments.library;

import android.os.Bundle;
import android.view.View;

import com.example.android.androidify.adapter.MediaItemPagedListAdapter;
import com.example.android.androidify.base.AbsPagedMediaItemListFragment;
import com.example.android.androidify.interfaces.MediaItemClickListener;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.model.SnackbarMessage;
import com.example.android.androidify.repository.datasource.NetworkState;
import com.example.android.androidify.utils.Constants;
import com.example.android.androidify.viewmodel.LibraryViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

public class LibraryListFragment extends AbsPagedMediaItemListFragment<MediaItemPagedListAdapter, LinearLayoutManager,
        LibraryViewModel> implements MediaItemClickListener {

    private static final String ARG_TYPE = "arg_type";

    private String mType;

    public static LibraryListFragment newInstance(@NonNull String type) {
        LibraryListFragment fragment = new LibraryListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void initVariables(Bundle arguments, Bundle savedInstanceState) {
        this.mType = arguments.getString(ARG_TYPE);
    }

    @Override
    protected MediaItemPagedListAdapter getAdapter() {
        return new MediaItemPagedListAdapter(getContext(), this);
    }

    @Override
    protected LinearLayoutManager getLayoutManager() {
        return new LinearLayoutManager(getContext());
    }

    @Override
    protected LibraryViewModel getMediaItemViewModel() {
        return ViewModelProviders.of(this, mFactoryModel).get(LibraryViewModel.class);
    }

    @Override
    protected void configureMediaItemViewModel() {
        switch (mType) {
            case Constants.PLAYLIST:
                mMediaItemViewModel.getUserPlaylists().observe(getViewLifecycleOwner(), super::handleResponse);
                mMediaItemViewModel.getPlaylistNetworkState().observe(getViewLifecycleOwner(), this::handleNetworkState);
                break;
            case Constants.SAVED_ALBUMS:
                mMediaItemViewModel.getSavedAlbums().observe(getViewLifecycleOwner(), super::handleResponse);
                mMediaItemViewModel.getAlbumNetworkState().observe(getViewLifecycleOwner(), this::handleNetworkState);
                break;
            case Constants.SAVED_TRACKS:
                mMediaItemViewModel.getSavedTracks().observe(getViewLifecycleOwner(), super::handleResponse);
                mMediaItemViewModel.getTrackNetworkState().observe(getViewLifecycleOwner(), this::handleNetworkState);
                break;
            case Constants.FOLLOWED_ARTISTS:
                mMediaItemViewModel.getFollowedArtists().observe(getViewLifecycleOwner(), super::handleResponse);
                mMediaItemViewModel.getArtistNetworkState().observe(getViewLifecycleOwner(), this::handleNetworkState);
                break;
        }
    }

    private void handleNetworkState(NetworkState networkState) {
        if (networkState != null && networkState.getStatus() == NetworkState.Status.FAILED) {
            createSnackbarMessage(SnackbarMessage.error());
        }

        mAdapter.setNetworkState(networkState);
    }

    @Override
    public void onItemClick(View view, int position) {
        MusicListItem item = mAdapter.getItemAtPosition(position);

        if (item != null) {
            switch (item.type) {
                case ALBUM:
                    navigateToAlbum(view, item.id);
                    break;
                case ARTIST:
                    navigateToArtist(view, item.id);
                    break;
                case PLAYLIST:
                    navigateToPlaylist(view, item.id);
                    break;
                case TRACK:
                    navigateToTrack(view, item.id);
                    break;
            }
        }
    }

    @Override
    public void openContextMenu(View v, int position) {
        MusicListItem item = mAdapter.getItemAtPosition(position);

        if (item != null) {
            showContextMenu(item);
        }
    }
}

/*public class LibraryListFragment extends AbsMediaItemListFragment<MediaItemListAdapter, LinearLayoutManager,
        LibraryViewModel> implements MediaItemClickListener {

    private static final String ARG_TYPE = "arg_type";

    private String mType;

    public static LibraryListFragment newInstance(@NonNull String type) {
        LibraryListFragment fragment = new LibraryListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initVariables(Bundle arguments, Bundle savedInstanceState) {
        this.mType = arguments.getString(ARG_TYPE);
    }

    @Override
    protected MediaItemListAdapter getAdapter() {
        MediaItemListAdapter.ImageStyle imageStyle = mType.equals(Constants.FOLLOWED_ARTISTS) ?
                MediaItemListAdapter.ImageStyle.CIRCLE :
                MediaItemListAdapter.ImageStyle.SQUARE;

        return new MediaItemListAdapter(getContext(), this, imageStyle);
    }

    @Override
    protected LinearLayoutManager getLayoutManager() {
        return new LinearLayoutManager(getContext());
    }

    @Override
    protected LibraryViewModel getMediaItemViewModel() {
        return ViewModelProviders.of(this, mFactoryModel).get(LibraryViewModel.class);
    }

    @Override
    protected void configureMediaItemViewModel() {
        mMediaItemRv.addOnScrollListener(new EndlessScrollListener(mLayoutManager) {
            @Override
            protected void onLoadMore() {

            }
        });
        switch (mType) {
            case Constants.PLAYLIST:
                mMediaItemViewModel.getUserPlaylists().observe(this, this::handleLibraryResponse);
                break;
            case Constants.SAVED_ALBUMS:
                mMediaItemViewModel.getSavedAlbums().observe(this, this::handleLibraryResponse);
                break;
            case Constants.SAVED_TRACKS:
                *//*mMediaItemViewModel.getSavedTracks().observe(this, this::handleLibraryResponse);*//*
                break;
            case Constants.FOLLOWED_ARTISTS:
                mMediaItemViewModel.getFollowedArtists().observe(this, this::handleLibraryResponse);
                break;
        }
    }

*//*    @Override
    protected void configureMediaItemViewModel() {
        switch (mType) {
            case Constants.PLAYLIST:
                mMediaItemViewModel.getUserPlaylists().observe(this, super::onResponse);
                break;
            case Constants.SAVED_ALBUMS:
                mMediaItemViewModel.getSavedAlbums().observe(this, super::onResponse);
                break;
            case Constants.SAVED_TRACKS:
                mMediaItemViewModel.getSavedTracks().observe(this, super::onResponse);
                break;
            case Constants.FOLLOWED_ARTISTS:
                mMediaItemViewModel.getFollowedArtists().observe(this, super::onResponse);
                break;
        }
    }*//*

    private void handleLibraryResponse(ApiResponse<List<MusicListItem>> response) {
        if (response != null) {
            switch (response.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    mAdapter.addItems(response.data);
                    break;
                case ERROR:
                    break;
                case UNAUTHORIZED:
                    break;
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        MusicListItem item = mAdapter.getItemAtPosition(position);
        if (item != null) {
            if (view.getId() == R.id.context_menu_button) {
                showContextMenu(item);
            } else {
                switch (item.type) {
                    case ALBUM:
                        navigateToAlbum(view, item.id);
                        break;
                    case ARTIST:
                        navigateToArtist(view, item.id);
                        break;
                    case PLAYLIST:
                        navigateToPlaylist(view, item.id);
                        break;
                    case TRACK:
                        navigateToTrack(view, item.id);
                        break;
                }
            }
        }
    }

    @Override
    public void openContextMenu(View v, int position) {
        MusicListItem item = mAdapter.getItemAtPosition(position);

        if (item != null) {
            showContextMenu(item);
        }
    }
}*/
