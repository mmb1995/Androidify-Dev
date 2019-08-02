package com.example.android.androidify.adapter;

import android.content.Context;

import com.example.android.androidify.R;
import com.example.android.androidify.fragments.ArtistGalleryFragment;
import com.example.android.androidify.fragments.TrackListFragment;
import com.example.android.androidify.utils.Constants;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ArtistPageAdapter extends FragmentPagerAdapter {
    private static final int NUM_ITEMS = 2;
    private final Context mContext;
    private String mId;

    public ArtistPageAdapter(Context context, FragmentManager fragmentManager,
                              String id) {
        super(fragmentManager);
        this.mContext = context;
        this.mId = id;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return TrackListFragment.newInstance(mId, Constants.ARTIST, null);
            case 1:
                return ArtistGalleryFragment.newInstance(mId, null);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return  mContext.getString(R.string.artist_tracks_tab_label);
            case 1:
                return mContext.getString(R.string.artist_related_tab_label);
            default:
                return null;
        }
    }
}
