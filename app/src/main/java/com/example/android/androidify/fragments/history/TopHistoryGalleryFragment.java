package com.example.android.androidify.fragments.history;

import android.os.Bundle;
import android.view.View;

import com.example.android.androidify.R;
import com.example.android.androidify.adapter.MusicCardGalleryAdapter;
import com.example.android.androidify.base.AbsMediaItemListFragment;
import com.example.android.androidify.interfaces.MediaItemClickListener;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.utils.Constants;
import com.example.android.androidify.viewmodel.TopHistoryViewModel;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

public class TopHistoryGalleryFragment extends AbsMediaItemListFragment<MusicCardGalleryAdapter, GridLayoutManager, TopHistoryViewModel>
        implements MediaItemClickListener {

    private static final String ARG_TYPE = "arg_type";

    private String mType;

    public static TopHistoryGalleryFragment newInstance(String type) {
        TopHistoryGalleryFragment fragment = new TopHistoryGalleryFragment();
        Bundle args = new Bundle();
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
    protected TopHistoryViewModel getMediaItemViewModel() {
        return ViewModelProviders.of(getActivity(), mFactoryModel).get(TopHistoryViewModel.class);
    }

    @Override
    protected void configureMediaItemViewModel() {
        switch (mType) {
            case Constants.RECENTLY_PLAYED:
                mMediaItemViewModel.getRecentlyPlayed().observe(this, super::onResponse);
                break;
            case Constants.TOP_ARTISTS:
                mMediaItemViewModel.getTopArtists().observe(this, super::onResponse);
                break;
            case Constants.TOP_TRACKS:
                mMediaItemViewModel.getTopTracks().observe(this, super::onResponse);
                break;
        }
    }

    @Override
    protected void initVariables(Bundle arguments, Bundle savedInstanceState) {
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
                    case ARTIST:
                        navigateToArtist(view, item.id);
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


