package com.example.android.androidify.fragments.Details;


import android.os.Bundle;
import android.view.View;

import com.example.android.androidify.R;
import com.example.android.androidify.adapter.DetailsFragmentPageAdapter;
import com.example.android.androidify.api.models.Album;
import com.example.android.androidify.fragments.tracks.TracksFragment;
import com.example.android.androidify.utils.Constants;
import com.example.android.androidify.viewmodel.AlbumViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;


public class AlbumFragment extends BaseDetailsFragment {
    private static final String TAG = "ALBUM_FRAG";

    private Album mAlbum;
    private AlbumViewModel mAlbumViewModel;

    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    protected void initializeViewModel() {
        this.mAlbumViewModel = ViewModelProviders.of(this, mFactoryViewModel).get(AlbumViewModel.class);
    }

    @Override
    protected void setupUi() {
        getAlbum();
    }

    private void configureViewPager() {
        String[] labels = getResources().getStringArray(R.array.album_tab_labels);
        List<Fragment> fragments = new ArrayList<>();
        Fragment tracksFragOne = createAlbumTracksFragment();
        fragments.add(tracksFragOne);
        Fragment tracksFragTwo = createAlbumTracksFragment();
        fragments.add(tracksFragTwo);
        DetailsFragmentPageAdapter adapter = new DetailsFragmentPageAdapter(getChildFragmentManager(),
                fragments, labels);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private TracksFragment createAlbumTracksFragment() {
        TracksFragment tracksFragment = new TracksFragment();
        Bundle args = new Bundle();
        args.putString(TracksFragment.ARG_ITEM_ID, mId);
        args.putString(TracksFragment.ARG_TYPE, Constants.ALBUM);
        tracksFragment.setArguments(args);
        return tracksFragment;
    }

    private void getAlbum() {
        this.mAlbumViewModel.getAlbum(mId).observe(this, response -> {
            if (response != null) {
                switch (response.status) {
                    case LOADING:
                        break;
                    case SUCCESS:
                        this.mAlbum = response.data;
                        updateUi();
                    case ERROR:
                        break;
                }
            }
        });
    }

    private void updateUi() {
        if (mAlbum.images != null && mAlbum.images.size() > 0) {
            String imageUrl = mAlbum.images.get(0).url;
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.color.imageLoadingColor)
                    .into(mBackdropImage);
        }
        mHeaderTextView.setText(mAlbum.name);
        mMetaTextView.setText(mAlbum.artists.get(0).name);
        configureViewPager();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.details_play_button:
                startPlayback(mAlbum.uri);
                break;
            case R.id.details_favorite_button:
                break;
        }
    }
}
