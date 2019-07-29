package com.example.android.androidify.fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.androidify.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TopHistoryFragment extends Fragment {


    public TopHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_top_history, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Fragment recentlyPlayedFragment = TopHistoryListFragment.newInstance(TopHistoryListFragment.RECENTLY_PLAYED);
        Fragment topTracksFragment = TopHistoryListFragment.newInstance(TopHistoryListFragment.TOP_TRACKS);
        Fragment topArtistsFragment = TopHistoryListFragment.newInstance(TopHistoryListFragment.TOP_ARTISTS);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_recent, recentlyPlayedFragment);
        transaction.replace(R.id.fragment_container_top_tracks, topTracksFragment);
        transaction.replace(R.id.fragment_container_top_artists, topArtistsFragment);
        transaction.commit();
    }
}
