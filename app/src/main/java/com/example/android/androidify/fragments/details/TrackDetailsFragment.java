package com.example.android.androidify.fragments.details;

import android.os.Bundle;
import android.view.View;

import com.example.android.androidify.R;
import com.example.android.androidify.api.models.Track;
import com.example.android.androidify.base.AbsMediaItemDetailsFragment;
import com.example.android.androidify.fragments.tracks.TrackListFragment;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.utils.Constants;
import com.example.android.androidify.viewmodel.MediaItemDetailsViewModel;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class TrackDetailsFragment extends AbsMediaItemDetailsFragment<MediaItemDetailsViewModel, Track> {

    public static TrackDetailsFragment newInstance(String id) {
        TrackDetailsFragment trackDetailsFragment = new TrackDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, id);
        trackDetailsFragment.setArguments(args);
        return trackDetailsFragment;
    }

    @Override
    protected int getViewStubLayout() {
        return R.layout.fragment_track_details;
    }

    @Override
    protected MediaItemDetailsViewModel getDetailsViewModel() {
        return ViewModelProviders.of(this, mFactoryModel).get(MediaItemDetailsViewModel.class);
    }

    @Override
    protected void initDetailsFragment() {

        if (getChildFragmentManager().findFragmentById(R.id.track_recommendations_container) == null) {
            Fragment recommendationFragment = TrackListFragment.newInstance(mId, Constants.RECOMMEND_FROM_TRACKS);
            getChildFragmentManager().beginTransaction()
                    .add(R.id.track_recommendations_container, recommendationFragment)
                    .commit();
        }

        mMediaItemDetailsViewModel.init(mId);
        mMediaItemDetailsViewModel.getTrack().observe(getViewLifecycleOwner(), super::onDetailsResponse);

    }

    @Override
    protected void updateUi() {
        if (mDetailsItem != null) {
            displayImage(mDetailsItem.album.images);
            mHeaderTextView.setText(mDetailsItem.name);
            mMetaTextView.setText(mDetailsItem.artists.get(0).name);
        }
    }

    @Override
    protected String getTitle() {
        return mDetailsItem != null ? mDetailsItem.name : " ";
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.details_play_button:
                startPlayback(mDetailsItem.uri);
                break;
            case R.id.details_context_menu_button:
                mMainViewModel.openMediaItemDialog(new MusicListItem(mDetailsItem));
                break;
        }
    }
}


