package com.example.android.androidify.adapter;

import com.example.android.androidify.fragments.search.SearchResultsFragment;
import com.example.android.androidify.utils.Constants;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SearchPageAdapter extends FragmentPagerAdapter {
    private static final int NUM_ITEMS = 3;

    private String mQuery;
    private String[] mTabLabels;

    public SearchPageAdapter(FragmentManager manager, String query, String[] labels) {
        super(manager);
        this.mQuery = query;
        this.mTabLabels = labels;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return SearchResultsFragment.newInstance(Constants.TRACK);
            case 1:
                return SearchResultsFragment.newInstance(Constants.ARTIST);
            case 2:
                return SearchResultsFragment.newInstance(Constants.ALBUM);
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
        return position < mTabLabels.length ? mTabLabels[position] : null;
    }
}
