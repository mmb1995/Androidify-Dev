package com.example.android.androidify.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.androidify.viewmodel.FactoryViewModel;
import com.example.android.androidify.viewmodel.MainActivityViewModel;

import javax.inject.Inject;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;

public abstract class BaseFragment extends Fragment {

    /**
     * Used to facilitate communication between fragments and the MainActivity
     */
    protected MainActivityViewModel mMainViewModel;

    @Inject
    protected FactoryViewModel mFactoryModel;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainViewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        injectViews(view);
        setupUi(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container, false);
    }

    protected void showSnackbarMessage(String message) {
        if (message != null) {
            mMainViewModel.setSnackBarMessage(message);
        }
    }

    protected void setCurrentPlayback(String uri) {
        if (uri != null) {
            mMainViewModel.setCurrentlyPlaying(uri);
        }
    }

    protected void navigateToArtist(String artistId) {
        if (artistId != null) {
            mMainViewModel.setCurrentArtistId(artistId);
        }
    }

    protected void navigateToAlbum(String albumId) {
        if (albumId != null) {
            mMainViewModel.setCurrentAlbumId(albumId);
        }
    }

    /**
     * Calls ButterKnife to inject fragment views
     * @param view
     */
    private void injectViews(final View view) {
        ButterKnife.bind(this, view);
    }

    /**
     * Gets the layout id associated with the child fragment
     * @return
     */
    protected abstract int getLayoutId();

    protected abstract void setupUi(View rootView);
}
