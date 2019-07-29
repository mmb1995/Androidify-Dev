package com.example.android.androidify.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.androidify.R;
import com.example.android.androidify.adapter.TopHistoryPageAdapter;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class TopHistoryFragment extends androidx.fragment.app.Fragment {
    private static final String TAG = "TOP_HISTORY_FRAG";

    @BindView(R.id.top_history_view_pager)
    ViewPager mViewPager;

    @BindView(R.id.top_history_tabs)
    TabLayout mTabLayout;


    public TopHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_top_history, container, false);
        ButterKnife.bind(this, rootView);
        TopHistoryPageAdapter adapter = new TopHistoryPageAdapter(getContext(), getChildFragmentManager());
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        return rootView;
    }

}
