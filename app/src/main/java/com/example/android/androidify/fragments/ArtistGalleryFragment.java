package com.example.android.androidify.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.androidify.R;
import com.example.android.androidify.adapter.ArtistGalleryAdapter;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.viewmodel.ArtistViewModel;
import com.example.android.androidify.viewmodel.FactoryViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistGalleryFragment extends Fragment {
    private static final String TAG = "ARTIST_GALLERY";

    private static final String ARG_ARTIST_ID = "arg_artist_id";

    @BindView(R.id.artist_gallery_recycler_view)
    RecyclerView mArtistGalleryRv;

    @Inject
    FactoryViewModel mFactoryModel;

    private ArtistGalleryAdapter mAdapter;
    private String mArtistId;


    public ArtistGalleryFragment() {
        // Required empty public constructor
    }

    public static ArtistGalleryFragment newInstance(String id) {
        ArtistGalleryFragment fragment = new ArtistGalleryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ARTIST_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mArtistId = getArguments().getString(ARG_ARTIST_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_artist_gallery, container, false);
        ButterKnife.bind(this, rootView);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        this.mArtistGalleryRv.setLayoutManager(manager);
        this.mAdapter = new ArtistGalleryAdapter(getContext());
        this.mArtistGalleryRv.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AndroidSupportInjection.inject(this);
        getRelatedArtists();
    }

    private void getRelatedArtists() {
        ArtistViewModel model = ViewModelProviders.of(this, mFactoryModel).get(ArtistViewModel.class);
        model.getRelatedArtists(mArtistId).observe(this, (ArrayList<MusicListItem> relatedArtists) -> {
            if (relatedArtists != null) {
                this.mAdapter.setItems(relatedArtists);
            }
        });
    }
}
