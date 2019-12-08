package com.example.android.androidify.fragments.details;

import android.os.Bundle;
import android.view.View;

import com.example.android.androidify.R;
import com.example.android.androidify.api.models.Album;
import com.example.android.androidify.base.AbsMediaItemDetailsFragment;
import com.example.android.androidify.fragments.tracks.TrackListFragment;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.utils.Constants;
import com.example.android.androidify.viewmodel.AlbumViewModel;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class AlbumFragment extends AbsMediaItemDetailsFragment<AlbumViewModel, Album> {

    public static AlbumFragment newInstance(String id) {
        AlbumFragment albumFragment = new AlbumFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, id);
        albumFragment.setArguments(args);
        return albumFragment;
    }

    @Override
    protected int getViewStubLayout() {
        return R.layout.fragment_album;
    }

    @Override
    protected AlbumViewModel getDetailsViewModel() {
        return ViewModelProviders.of(this, mFactoryModel).get(AlbumViewModel.class);
    }

    @Override
    protected void initDetailsFragment() {
        if (getChildFragmentManager().findFragmentById(R.id.details_container) == null) {
            Fragment albumTracksFragment = TrackListFragment.newInstance(mId, Constants.ALBUM);
            getChildFragmentManager().beginTransaction()
                    .add(R.id.album_details_container, albumTracksFragment)
                    .commit();
        }

        mMediaItemDetailsViewModel.init(mId);
        mMediaItemDetailsViewModel.getAlbum().observe(getViewLifecycleOwner(), super::onDetailsResponse);

    }

    @Override
    protected void updateUi() {
        if (mDetailsItem != null) {
            displayImage(mDetailsItem.images);
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

