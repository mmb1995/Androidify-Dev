package com.example.android.androidify.adapter;

import android.content.Context;

import com.example.android.androidify.R;
import com.example.android.androidify.fragments.list.ImageGalleryFragment;
import com.example.android.androidify.fragments.tracks.TracksFragment;
import com.example.android.androidify.utils.Constants;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ArtistPageAdapter extends FragmentPagerAdapter {
    private static final int NUM_ITEMS = 3;
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
                return TracksFragment.newInstance(mId, Constants.ARTIST);
            case 1:
                return ImageGalleryFragment.newInstance(mId, Constants.ARTIST);
            case 2:
                return ImageGalleryFragment.newInstance(mId, Constants.ALBUM);
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
            case 2:
                return mContext.getString(R.string.artist_albums_tab);
            default:
                return null;
        }
    }
}
