package com.example.android.androidify.fragments.artist;

import android.os.Bundle;
import android.view.View;

import com.example.android.androidify.adapter.MusicCardGalleryAdapter;
import com.example.android.androidify.base.AbsMediaItemListFragment;
import com.example.android.androidify.interfaces.MediaItemClickListener;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.viewmodel.ArtistViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

public class ArtistGalleryFragment extends AbsMediaItemListFragment<MusicCardGalleryAdapter,
        GridLayoutManager, ArtistViewModel> implements MediaItemClickListener {

    public static final String ARG_RESOURCE_TYPE = "arg_type";
    public static final String ARG_ARTIST_ID = "arg_item_id";

    private String mArtistId;
    private String mArtistResourceType;

    public static ArtistGalleryFragment newInstance(@NonNull String id, @NonNull String type) {
        ArtistGalleryFragment fragment  = new ArtistGalleryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ARTIST_ID, id);
        args.putString(ARG_RESOURCE_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initVariables(Bundle arguments, Bundle savedInstanceState) {
        mArtistId = getArguments().getString(ARG_ARTIST_ID);
        mArtistResourceType = getArguments().getString(ARG_RESOURCE_TYPE);
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
    protected ArtistViewModel getMediaItemViewModel() {
        return ViewModelProviders.of(getParentFragment(), mFactoryModel).get(ArtistViewModel.class);
    }

    @Override
    protected void configureMediaItemViewModel() {
        mMediaItemViewModel.getArtistResource(mArtistId, mArtistResourceType).observe(getViewLifecycleOwner(),
                super::onResponse);
    }

    @Override
    public void onItemClick(View v, int position) {
        MusicListItem item = mAdapter.getItemAtPosition(position);

        if (item != null) {
            switch (item.type) {
                case ARTIST:
                    navigateToArtist(v, item.id);
                    break;
                case ALBUM:
                    navigateToAlbum(v, item.id);
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
