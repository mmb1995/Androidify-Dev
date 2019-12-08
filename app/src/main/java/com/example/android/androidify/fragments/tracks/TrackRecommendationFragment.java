package com.example.android.androidify.fragments.tracks;

import android.os.Bundle;
import android.view.View;

import com.example.android.androidify.R;
import com.example.android.androidify.adapter.TrackListAdapter;
import com.example.android.androidify.base.AbsMediaItemListFragment;
import com.example.android.androidify.interfaces.MediaItemClickListener;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.viewmodel.RecommendationsViewModel;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

public class TrackRecommendationFragment extends AbsMediaItemListFragment<TrackListAdapter, LinearLayoutManager,
        RecommendationsViewModel> implements MediaItemClickListener {

    private static final String ARG_RECOMMENDATION_TYPE = "arg_recommendation_type";
    private static final String ARG_ITEM_ID = "arg_item_id";

    private String mType;
    private String mId;

    public static TrackRecommendationFragment newInstance(String id, String type) {
        TrackRecommendationFragment fragment = new TrackRecommendationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ITEM_ID, id);
        args.putString(ARG_RECOMMENDATION_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initVariables(Bundle arguments, Bundle savedInstanceState) {
        mType = arguments.getString(ARG_RECOMMENDATION_TYPE);
        mId = arguments.getString(ARG_ITEM_ID);
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
    protected RecommendationsViewModel getMediaItemViewModel() {
        return ViewModelProviders.of(this, mFactoryModel).get(RecommendationsViewModel.class);
    }

    @Override
    protected void configureMediaItemViewModel() {
        mMediaItemViewModel.init(mId, mType);
        mMediaItemViewModel.getRecommendations().observe(this, super::onResponse);
    }

    @Override
    public void onItemClick(View view, int position) {
        MusicListItem item = mAdapter.getItemAtPosition(position);
        if (item != null) {
            if (view.getId() == R.id.track_context_menu) {
                //mMediaItemViewModel.toggleTrackSaveStatus(item.id);
                /*startPlayback(item.uri);*/
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
