package com.example.android.androidify.adapter;

import android.content.Context;

import com.example.android.androidify.R;
import com.example.android.androidify.fragments.library.LibraryListFragment;
import com.example.android.androidify.utils.Constants;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class LibraryPageAdapter extends FragmentPagerAdapter {

    private static final String TAG = "TOP_HIST_FRAG_PGR";
    private static final int NUM_ITEMS = 4;
    private String[] mTabLabels;

    public LibraryPageAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        mTabLabels = context.getResources().getStringArray(R.array.full_library_tab_labels);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return LibraryListFragment.newInstance(Constants.SAVED_ALBUMS);
            case 1:
                return LibraryListFragment.newInstance(Constants.FOLLOWED_ARTISTS);
            case 2:
                return LibraryListFragment.newInstance(Constants.SAVED_TRACKS);
            case 3:
                return LibraryListFragment.newInstance(Constants.PLAYLIST);
/*            case 0:
                return MediaItemGalleryFragment.newInstance(null, Constants.SAVED_ALBUMS);
            case 1:
                return MediaItemGalleryFragment.newInstance(null, Constants.FOLLOWED_ARTISTS);
            case 2:
                return MediaItemGalleryFragment.newInstance(null, Constants.SAVED_TRACKS);
            case 3:
                return MediaItemGalleryFragment.newInstance(null, Constants.PLAYLIST);*/
/*            case 4:
                return TopHistoryGalleryFragment.newInstance(Constants.TOP_ARTISTS);
            case 5:
                return TopHistoryGalleryFragment.newInstance(Constants.TOP_TRACKS);
            case 6:
                return TopHistoryGalleryFragment.newInstance(Constants.RECENTLY_PLAYED);*/
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
