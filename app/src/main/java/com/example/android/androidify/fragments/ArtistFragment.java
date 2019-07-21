package com.example.android.androidify.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.androidify.R;
import com.example.android.androidify.adapter.ArtistPageAdapter;
import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.api.models.Artist;
import com.example.android.androidify.api.models.Image;
import com.example.android.androidify.view.CustomViewPager;
import com.example.android.androidify.viewmodel.ArtistViewModel;
import com.example.android.androidify.viewmodel.FactoryViewModel;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistFragment extends Fragment {
    private static final String TAG = "ARTIST_FRAGMENT";
    private static final String ARG_ARTIST_ID = "artist_id";

    @BindView(R.id.artist_backdrop_image)
    ImageView mArtistBackdropImage;

    @BindView(R.id.artist_header_text_view)
    TextView mArtistHeaderTextView;

    @BindView(R.id.artist_followers_text_view)
    TextView mArtistFollowersTextView;

    @BindView(R.id.shuffle_button)
    FloatingActionButton mShuffleButton;

    @BindView(R.id.artist_favorite_button)
    CheckBox mFavoriteButton;

    @BindView(R.id.artist_view_pager)
    CustomViewPager mViewPager;

    @BindView(R.id.artist_tabs)
    TabLayout mTabLayout;

    @Inject
    FactoryViewModel mFactoryViewModel;

    private String mArtistId;

    private ArtistViewModel mModel;


    public ArtistFragment() {
        // Required empty public constructor
    }

    public static ArtistFragment newInstance(String id) {
        ArtistFragment fragment = new ArtistFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ARTIST_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.mArtistId = getArguments().getString(ARG_ARTIST_ID);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_artist, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AndroidSupportInjection.inject(this);
        getArtistDetails();
        configureViewPager();
    }

    private void getArtistDetails() {
        mModel = ViewModelProviders.of(this, mFactoryViewModel).get(ArtistViewModel.class);

        // Set up observers
        mModel.getArtist(mArtistId).observe(this, (Artist artist) -> {
            if (artist != null) {
                setupUi(artist);
            }
        });

        mModel.getArtistFollowStatus(mArtistId).observe(this, (Boolean isFollowing) -> {
            if (isFollowing != null) {
                handleFollowStatus(isFollowing);
            }
        });

        /**
        model.getArtistTopTracks(mArtistId).observe( this, (ArrayList<MusicListItem> items) -> {
            if (items != null) {
                mArtistTopTracks = items;
                mRelatedArtists = items;
                configureViewPager();
            }
        });
        **/
    }

    private void configureViewPager() {
        ArtistPageAdapter adapter = new ArtistPageAdapter(getContext(), getChildFragmentManager(),
                this.mArtistId);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void handleFollowStatus(Boolean following) {
        mFavoriteButton.setChecked(following);
        mFavoriteButton.setOnClickListener((View v) -> {
            mModel.followArtist(mArtistId).observe(this, (ApiResponse<Void> response) -> {
                switch (response.status) {
                    case LOADING:
                        mFavoriteButton.setEnabled(false);
                        break;
                    case SUCCESS:
                        mFavoriteButton.setChecked(true);
                        mFavoriteButton.setEnabled(true);
                        Toast.makeText(getActivity(), "Following artist!", Toast.LENGTH_SHORT).show();
                        break;
                    case ERROR:
                        mFavoriteButton.setEnabled(true);
                        Log.e(TAG, response.error);
                        Toast.makeText(getActivity(), "there was an error", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            });
        });
    }

    private void setupUi(Artist artist) {
        Image image = artist.images.get(0);
        String url = image.url;


        Picasso.get()
                .load(url)
                .placeholder(R.color.colorPrimary)
                .into(mArtistBackdropImage);

        mArtistHeaderTextView.setText(artist.name);
        int followers = artist.followers.total;
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        String followersString = numberFormat.format(followers);
        mArtistFollowersTextView.setText(followersString + " followers");
    }


}
