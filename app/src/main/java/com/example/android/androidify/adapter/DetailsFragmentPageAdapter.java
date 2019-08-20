package com.example.android.androidify.adapter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class DetailsFragmentPageAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragments;
    private String[] mLabels;

    public DetailsFragmentPageAdapter(FragmentManager fragmentManager, List<Fragment> fragments,
                                      String[] labels) {
        super(fragmentManager);
        this.mFragments = fragments;
        this.mLabels = labels;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragments != null ? mFragments.get(position) : null;
    }

    @Override
    public int getCount() {
        return mFragments != null ? mFragments.size(): 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return position < mLabels.length ? mLabels[position] : null;
    }
}
