package com.example.android.androidify.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.androidify.R;
import com.example.android.androidify.adapter.ArtistPageAdapter;
import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.api.models.Artist;
import com.example.android.androidify.api.models.Image;
import com.example.android.androidify.view.CustomViewPager;
import com.example.android.androidify.viewmodel.ArtistViewModel;
import com.example.android.androidify.viewmodel.FactoryViewModel;
import com.example.android.androidify.viewmodel.MainActivityViewModel;
import com.example.android.androidify.viewmodel.MusicPlaybackViewModel;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
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

    @BindView(R.id.artist_favorite_button)
    MaterialCheckBox mFavoriteButton;

    @BindView(R.id.artist_view_pager)
    CustomViewPager mViewPager;

    @BindView(R.id.artist_tabs)
    TabLayout mTabLayout;

    @BindView(R.id.artist_play_button)
    FloatingActionButton mPlayButton;

    @Inject
    FactoryViewModel mFactoryViewModel;

    private String mArtistId;

    private ArtistViewModel mModel;
    private MainActivityViewModel mMainViewModel;

    private Artist mArtist;

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
        MaterialShapeDrawable materialShapeDrawable = MaterialShapeDrawable.createWithElevationOverlay(getContext(),
                2);
        mViewPager.setBackground(materialShapeDrawable);
        mViewPager.setElevation(materialShapeDrawable.getElevation());
        mFavoriteButton.setOnClickListener((View v) -> {
            onFollowClicked();
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AndroidSupportInjection.inject(this);
        getArtistDetails();
        mMainViewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
        configurePlayButton();
        configureViewPager();
    }

    private void getArtistDetails() {
        mModel = ViewModelProviders.of(this, mFactoryViewModel).get(ArtistViewModel.class);

        // Set up observers
        mModel.getArtist(mArtistId).observe(this, (Artist artist) -> {
            if (artist != null) {
                this.mArtist = artist;
                setupUi();
            }
        });

        mModel.getArtistFollowStatus(mArtistId).observe(this, (Boolean isFollowing) -> {
            if (isFollowing != null) {
                setFollowButton(isFollowing);
            }
        });
    }

    private void configurePlayButton() {
        mPlayButton.setOnClickListener((View v) -> {
            if (mArtist != null) {
                mMainViewModel.setCurrentlyPlaying(mArtist.uri);
            }
        });
    }

    private void configureViewPager() {
        ArtistPageAdapter adapter = new ArtistPageAdapter(getContext(), getChildFragmentManager(),
                this.mArtistId);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void updateFollowStatus(Boolean following, ApiResponse<Void> response) {
        String successMessage = following ? getString(R.string.artist_followed_message) :
                getString(R.string.artist_unfollowed_message);

        Boolean undo = !following;

        switch (response.status) {
            case LOADING:
                mFavoriteButton.setEnabled(false);
                break;
            case SUCCESS:
                mModel.setIsFollowingArtist(following);
                createSnackbarEvent(successMessage);
                break;
            case ERROR:
                Log.e(TAG, response.error);
                //showSnackbarMessage(getString(R.string.artist_follow_error), undo);
                break;
            default:
                break;
        }

    }

    private void onFollowClicked() {
        removeFollowObservers();
        Boolean following = mModel.getFollowing();
        if (!following) {
            mModel.followArtist(mArtistId).observe(this, (ApiResponse<Void> response) -> {
                updateFollowStatus(true, response);
            });
        } else {
            mModel.unfollowArtist(mArtistId).observe(this, (ApiResponse<Void> response) -> {
                updateFollowStatus(false, response);
            });
        }
    }

    private void removeFollowObservers() {
        if (mModel.followArtist(this.mArtistId).hasObservers()) {
            mModel.followArtist(this.mArtistId).removeObservers(this);
        }
        if (mModel.unfollowArtist(this.mArtistId).hasObservers()) {
            mModel.unfollowArtist(this.mArtistId).removeObservers(this);
        }
    }

    private void setFollowButton(Boolean following) {
        mFavoriteButton.setChecked(following);
        mFavoriteButton.setEnabled(true);
    }


    private void setupUi() {
        Image image = mArtist.images.get(0);
        String url = image.url;


        Picasso.get()
                .load(url)
                .placeholder(R.color.imageLoadingColor)
                .into(mArtistBackdropImage);

        mArtistHeaderTextView.setText(mArtist.name);
        int followers = mArtist.followers.total;
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        String followersString = numberFormat.format(followers);
        mArtistFollowersTextView.setText(followersString + " followers");
    }

    private void createSnackbarEvent(String message) {
        mMainViewModel.setSnackBarMessage(message);
    }


}
