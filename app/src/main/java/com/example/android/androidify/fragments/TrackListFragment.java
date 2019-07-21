package com.example.android.androidify.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.androidify.R;
import com.example.android.androidify.adapter.MusicListAdapter;
import com.example.android.androidify.interfaces.MusicPlaybackClickListener;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.utils.Constants;
import com.example.android.androidify.viewmodel.ArtistViewModel;
import com.example.android.androidify.viewmodel.FactoryViewModel;
import com.example.android.androidify.viewmodel.MusicPlaybackViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;


public class TrackListFragment extends Fragment implements MusicPlaybackClickListener {
    private static final String TAG = "TRACK_LIST_FRAG";

    //private static final String ARG_MUSIC_LIST = "music_list";
    private static final String ARG_MUSIC_TYPE = "music_type";
    private static final String ARG_ID = "track_id";

    @BindView(R.id.track_list_rv)
    RecyclerView mMusicListRecyclerView;

    @Inject
    FactoryViewModel mFactoryModel;

    private MusicListAdapter mAdapter;
    private ArrayList<MusicListItem> mItems;
    private MusicPlaybackViewModel mMusicPlaybackViewModel;
    private String mId;
    private String mType;

    public TrackListFragment() {
        // Required empty public constructor
    }


    public static TrackListFragment newInstance(String id, String type) {
        TrackListFragment fragment = new TrackListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, id);
        args.putString(ARG_MUSIC_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mId = getArguments().getString(ARG_ID);
            mType = getArguments().getString(ARG_MUSIC_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_track_list, container, false);
        ButterKnife.bind(this, rootView);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mMusicListRecyclerView.setLayoutManager(manager);
        this.mAdapter = new MusicListAdapter(getContext(), this, this.mType);
        this.mMusicListRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AndroidSupportInjection.inject(this);
        mMusicPlaybackViewModel = ViewModelProviders.of(getActivity()).get(MusicPlaybackViewModel.class);
        getTracks();
    }

    private void getTracks() {
        switch (this.mType) {
            case Constants.ARTIST:
                ArtistViewModel model = ViewModelProviders.of(this, mFactoryModel).get(ArtistViewModel.class);
                model.getArtistTopTracks(mId).observe(this, (ArrayList<MusicListItem> topTracks) -> {
                    if (topTracks != null) {
                        this.mAdapter.setItems(topTracks);
                    }
                });
                break;
            default:
                Log.i(TAG, "default case");
        }
    }

    @Override
    public void onItemClicked(int position) {
        MusicListItem item = this.mAdapter.getItemAtPosition(position);
        if (item != null) {
            String uri = item.uri;
            mMusicPlaybackViewModel.setCurrentlyPlaying(uri);
        }
    }



    /**
    public static TrackListFragment newInstance(ArrayList<Track> tracks) {
        TrackListFragment fragment = new TrackListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_TRACK_LIST, tracks);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTracks = getArguments().getParcelableArrayList(ARG_TRACK_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_track_list, container, false);
        ButterKnife.bind(this, rootView);
        displayTrackList();
        return rootView;
    }


    private void displayTrackList() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mMusicListRecyclerView.setLayoutManager(manager);
        this.mAdapter = new TrackListAdapter(getContext(), mTracks);
        this.mMusicListRecyclerView.setAdapter(mAdapter);
    }

    /**
    private void configureViewModel() {
        this.mViewModel = ViewModelProviders.of(this, mFactoryViewModel).get(UserLibraryViewModel.class);
        mViewModel.getSavedTracks().observe(this, response -> {
            Log.i(TAG, "Received response");
            if (response != null) {
                this.mAdapter.setItems(response);
            }
        });
    }
     **/


    /**

     public static TrackListFragment newInstance(ArrayList<MusicListItem> items, String type) {
     TrackListFragment fragment = new TrackListFragment();
     Bundle args = new Bundle();
     args.putParcelableArrayList(ARG_MUSIC_LIST, items);
     args.putString(ARG_MUSIC_TYPE, type);
     fragment.setArguments(args);
     return fragment;
     }


     @Override
     public void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     if (getArguments() != null) {
     mItems = getArguments().getParcelableArrayList(ARG_MUSIC_LIST);
     mType = getArguments().getString(ARG_MUSIC_TYPE);
     }
     }

     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
     View rootView = inflater.inflate(R.layout.fragment_track_list, container, false);
     ButterKnife.bind(this, rootView);
     displayMusicItems();
     return rootView;
     }

     @Override
     public void onActivityCreated(Bundle savedInstanceState) {
     super.onActivityCreated(savedInstanceState);
     AndroidSupportInjection.inject(this);
     mMusicPlaybackViewModel = ViewModelProviders.of(getActivity()).get(MusicPlaybackViewModel.class);
     displayMusicItems();
     }


     private void displayMusicItems() {
     LinearLayoutManager manager = new LinearLayoutManager(getContext());
     mMusicListRecyclerView.setLayoutManager(manager);
     this.mAdapter = new MusicListAdapter(getContext(), this, mType, mItems);
     this.mMusicListRecyclerView.setAdapter(mAdapter);
     }

     @Override
     public void onItemClicked(int position) {
     MusicListItem item = this.mAdapter.getItemAtPosition(position);
     Log.i(TAG, item.name);
     if (item != null) {
     String uri = item.uri;
     mMusicPlaybackViewModel.setCurrentlyPlaying(uri);
     }
     }

     **/

}
