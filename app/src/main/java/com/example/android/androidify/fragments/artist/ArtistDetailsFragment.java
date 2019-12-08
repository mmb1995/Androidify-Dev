package com.example.android.androidify.fragments.artist;

import android.os.Bundle;
import android.view.View;

import com.example.android.androidify.R;
import com.example.android.androidify.adapter.ArtistPageAdapter;
import com.example.android.androidify.api.models.Artist;
import com.example.android.androidify.base.AbsMediaItemDetailsFragment;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.viewmodel.ArtistViewModel;
import com.google.android.material.tabs.TabLayout;

import java.text.NumberFormat;
import java.util.Locale;

import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;

public class ArtistDetailsFragment extends AbsMediaItemDetailsFragment<ArtistViewModel, Artist> {

    @BindView(R.id.artist_view_pager)
    public ViewPager mViewPager;

    @BindView(R.id.artist_tabs)
    public TabLayout mTabLayout;


    public static ArtistDetailsFragment newInstance(String id) {
        ArtistDetailsFragment artistDetailsFragment = new ArtistDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, id);
        artistDetailsFragment.setArguments(args);
        return artistDetailsFragment;
    }

    @Override
    protected int getViewStubLayout() {
        return R.layout.fragment_artist_details;
    }

    @Override
    protected ArtistViewModel getDetailsViewModel() {
        return ViewModelProviders.of(this, mFactoryModel).get(ArtistViewModel.class);
    }

    @Override
    protected void initDetailsFragment() {
        ArtistPageAdapter adapter = new ArtistPageAdapter(getContext(), getChildFragmentManager(), mId);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mMediaItemDetailsViewModel.init(mId);
        mMediaItemDetailsViewModel.getArtist().observe(getViewLifecycleOwner(), super::onDetailsResponse);

    }

    @Override
    protected void updateUi() {
        if (mDetailsItem != null) {
            /*setToolbarTitle(mDetailsItem.name);*/
            displayImage(mDetailsItem.images);
            mHeaderTextView.setText(mDetailsItem.name);
            int followers = mDetailsItem.followers.total;
            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
            String followersString = numberFormat.format(followers);
            mMetaTextView.setText(followersString + " followers");
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

