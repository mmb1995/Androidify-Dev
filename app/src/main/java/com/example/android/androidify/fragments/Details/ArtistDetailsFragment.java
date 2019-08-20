package com.example.android.androidify.fragments.Details;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.android.androidify.R;
import com.example.android.androidify.adapter.ArtistPageAdapter;
import com.example.android.androidify.api.models.Artist;
import com.example.android.androidify.api.models.Image;
import com.example.android.androidify.viewmodel.ArtistViewModel;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

import androidx.lifecycle.ViewModelProviders;

public class ArtistDetailsFragment extends BaseDetailsFragment {
    private static final String TAG = "ARTIST_DETAILS_FRAG";

    private Artist mArtist;
    private ArtistViewModel mArtistModel;

    public static ArtistDetailsFragment newInstance(String id) {
        ArtistDetailsFragment fragment = new ArtistDetailsFragment();
        Bundle args = new Bundle();
        args.putString(BaseDetailsFragment.ARG_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initializeViewModel() {
        Log.i(TAG, "onCreate");
        mArtistModel = ViewModelProviders.of(this, mFactoryViewModel).get(ArtistViewModel.class);
    }

    @Override
    protected void setupUi() {
        ArtistPageAdapter adapter = new ArtistPageAdapter(getContext(), getChildFragmentManager(), mId);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        configureArtistObservers();
    }

    private void configureArtistObservers() {
        mArtistModel.init(mId);
        mArtistModel.getArtist().observe(this, response -> {
            if (response != null) {
                switch (response.status) {
                    case LOADING:
                        break;
                    case SUCCESS:
                        this.mArtist = response.data;
                        updateUi();
                        break;
                    case ERROR:
                        break;
                }
            }
        });

        mArtistModel.getArtistFollowStatus().observe(this, isFollowing -> {
            if (isFollowing != null) {
                mFavoriteButton.setSelected(isFollowing);
                if (mFavoriteButton.getVisibility() == View.INVISIBLE) {
                    // Dont't show Snackbar for initial value
                    mFavoriteButton.setVisibility(View.VISIBLE);
                } else {
                    // Show Snackbar
                    String message = isFollowing ? getString(R.string.artist_followed_message) :
                            getString(R.string.artist_unfollowed_message);
                    createSnackbarMessage(message);
                }
            }
        });
    }

    private void updateUi() {
        Image image = mArtist.images.get(0);
        String url = image.url;


        Picasso.get()
                .load(url)
                .placeholder(R.color.imageLoadingColor)
                .into(mBackdropImage);

        mHeaderTextView.setText(mArtist.name);
        int followers = mArtist.followers.total;
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        String followersString = numberFormat.format(followers);
        mMetaTextView.setText(followersString + " followers");
    }

    @Override
    public void onClick(View view) {
        if (mArtist != null) {
            switch (view.getId()) {
                case R.id.details_play_button:
                    startPlayback(mArtist.uri);
                    break;
                case R.id.details_favorite_button:
                    mArtistModel.toggleArtistFollow(mId);
                    break;
            }
        }
    }
}
