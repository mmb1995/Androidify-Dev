package com.example.android.androidify.fragments.list;

import android.os.Bundle;
import android.view.View;

import com.example.android.androidify.R;
import com.example.android.androidify.adapter.MusicCardGalleryAdapter;
import com.example.android.androidify.base.AbsMediaItemListFragment;
import com.example.android.androidify.interfaces.MediaItemClickListener;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.utils.Constants;
import com.example.android.androidify.viewmodel.ImageGalleryViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

public class MediaItemGalleryFragment extends AbsMediaItemListFragment<MusicCardGalleryAdapter, GridLayoutManager,
        ImageGalleryViewModel> implements MediaItemClickListener {

    private static final String TAG = "MEDIA_GALL_FRAG";

    private static final String ARG_ID = "arg_id";
    private static final String ARG_TYPE = "arg_type";

    private String mId;
    private String mType;

    public static MediaItemGalleryFragment newInstance(@Nullable String id, @NonNull String type) {
        MediaItemGalleryFragment fragment = new MediaItemGalleryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, id);
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected MusicCardGalleryAdapter getAdapter() {
        return new MusicCardGalleryAdapter(getContext(), this);
    }

    @Override
    protected GridLayoutManager getLayoutManager() {
        return new GridLayoutManager(getContext(), 2);
    }

    @Override
    protected ImageGalleryViewModel getMediaItemViewModel() {
        return ViewModelProviders.of(this, mFactoryModel).get(ImageGalleryViewModel.class);
    }

    @Override
    protected void configureMediaItemViewModel() {
        switch (mType) {
            case Constants.ALBUM:
                mMediaItemViewModel.getAlbums(mId).observe(this, super::onResponse);
                break;
            case Constants.ARTIST:
                mMediaItemViewModel.getRelatedArtists(mId).observe(this, super::onResponse);
                break;
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
    }

    @Override
    protected void initVariables(Bundle arguments, Bundle savedInstanceState) {
        this.mId = arguments.getString(ARG_ID);
        this.mType = arguments.getString(ARG_TYPE);
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
}
