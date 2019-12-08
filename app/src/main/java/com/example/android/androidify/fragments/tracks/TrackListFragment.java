package com.example.android.androidify.fragments.tracks;

import android.os.Bundle;
import android.view.View;

import com.example.android.androidify.R;
import com.example.android.androidify.adapter.TrackListAdapter;
import com.example.android.androidify.base.AbsMediaItemListFragment;
import com.example.android.androidify.interfaces.MediaItemClickListener;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.viewmodel.TracksViewModel;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

public class TrackListFragment extends AbsMediaItemListFragment<TrackListAdapter, LinearLayoutManager,
        TracksViewModel> implements MediaItemClickListener {

    private static final String TAG = "TRACK_LIST_FRAG";

    public static final String ARG_TYPE = "arg_track_type";
    public static final String ARG_ITEM_ID = "arg_item_id";

    private String mType;
    private String mId;

    public static TrackListFragment newInstance(String id, String type) {
        TrackListFragment fragment = new TrackListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ITEM_ID, id);
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initVariables(Bundle arguments, Bundle savedInstanceState) {
        mType = getArguments().getString(ARG_TYPE);
        mId = getArguments().getString(ARG_ITEM_ID);
    }

    @Override
    protected TrackListAdapter getAdapter() {
        return new TrackListAdapter(getContext(), this);
    }

    @Override
    protected LinearLayoutManager getLayoutManager() {
        return new LinearLayoutManager(getContext());
    }

    @Override
    protected TracksViewModel getMediaItemViewModel() {
        return ViewModelProviders.of(this, mFactoryModel).get(TracksViewModel.class);
    }

    @Override
    protected void configureMediaItemViewModel() {
/*        mMediaItemViewModel.init(mId, mType);
        mMediaItemViewModel.getTracks().observe(this, super::onResponse);
        mMediaItemViewModel.getSnackbarEvent().observe(this, new EventObserver<Action>(action -> {
            if (action.type == Action.Type.SAVE) {
                createSnackbarMessage(SnackbarMessage.create(R.string.track_liked_message));
            } else if (action.type == Action.Type.REMOVE) {
                createSnackbarMessage(SnackbarMessage.create(R.string.track_unlike_message));
            }
        }));*/
        mMediaItemViewModel.getTracks(mId, mType).observe(getViewLifecycleOwner(), super::onResponse);
    }

    @Override
    public void onItemClick(View view, int position) {
        MusicListItem item = mAdapter.getItemAtPosition(position);
        if (item != null) {
            if (view.getId() == R.id.track_context_menu) {
                /*mMediaItemViewModel.toggleTrackSaveStatus(item.id);*/
                showContextMenu(item);
            } else {
                startPlayback(item.uri);
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
