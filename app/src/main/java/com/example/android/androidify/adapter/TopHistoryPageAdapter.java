package com.example.android.androidify.adapter;

import android.content.Context;

import com.example.android.androidify.R;
import com.example.android.androidify.fragments.history.TopHistoryGalleryFragment;
import com.example.android.androidify.utils.Constants;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TopHistoryPageAdapter extends FragmentPagerAdapter {
    private static final String TAG = "TOP_HIST_FRAG_PGR";
    private static final int NUM_ITEMS = 3;

    private final Context mContext;

    public TopHistoryPageAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.mContext = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return TopHistoryGalleryFragment.newInstance(Constants.TOP_ARTISTS);
            case 1:
                return TopHistoryGalleryFragment.newInstance(Constants.TOP_TRACKS);
            case 2:
                return TopHistoryGalleryFragment.newInstance(Constants.RECENTLY_PLAYED);
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
                return mContext.getString(R.string.top_history_artist_tab);
            case 1:
                return mContext.getString(R.string.top_history_track_tab);
            case 2:
                return mContext.getString(R.string.top_history_recent_tab);
            default:
                return null;
        }
    }
}
