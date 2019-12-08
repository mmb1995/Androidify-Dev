package com.example.android.androidify.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.androidify.R;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.model.SnackbarMessage;
import com.example.android.androidify.viewmodel.FactoryViewModel;
import com.example.android.androidify.viewmodel.MainActivityViewModel;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Objects;

import javax.inject.Inject;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.AndroidSupportInjection;

public abstract class BaseFragment extends Fragment {

    /**
     * Used to facilitate communication between fragments and the MainActivity
     */
    protected MainActivityViewModel mMainViewModel;

    @Inject
    protected FactoryViewModel mFactoryModel;

    private Unbinder mUnbinder;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity()), mFactoryModel).get(MainActivityViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        injectViews(view);
        init(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Release reference to  views
        mUnbinder.unbind();
    }

    /* Calls ButterKnife to inject fragment views
     * @param view
     */
    private void injectViews(final View view) {
        mUnbinder = ButterKnife.bind(this, view);
    }

    public void startPlayback(String uri) {
        if (uri != null && uri.length() > 0) {
            mMainViewModel.setCurrentlyPlaying(uri);
        }
    }

    protected void navigateToArtist(View view, String artistId) {
        Bundle bundle = new Bundle();
        bundle.putString(AbsMediaItemDetailsFragment.ARG_ID, artistId);
        Navigation.findNavController(view).navigate(R.id.action_global_artistFragment, bundle);
    }

    protected void navigateToAlbum(View view, String albumId) {
        Bundle bundle = new Bundle();
        bundle.putString(AbsMediaItemDetailsFragment.ARG_ID, albumId);
        Navigation.findNavController(view).navigate(R.id.action_global_albumFragment, bundle);
    }

    protected void navigateToTrack(View view, String trackId) {
        Bundle bundle = new Bundle();
        bundle.putString(AbsMediaItemDetailsFragment.ARG_ID, trackId);
        Navigation.findNavController(view).navigate(R.id.action_global_trackFragment, bundle);
    }

    protected void navigateToPlaylist(View view, String playlistId) {
        Bundle bundle = new Bundle();
        bundle.putString(AbsMediaItemDetailsFragment.ARG_ID, playlistId);
        Navigation.findNavController(view).navigate(R.id.action_global_playlistFragment, bundle);
    }

    protected void navigateToSearch() {
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_global_searchFragment);
    }

    protected void showContextMenu(MusicListItem item) {
       mMainViewModel.openMediaItemDialog(item);
    }

    protected void setToolbar(MaterialToolbar toolbar) {
        mMainViewModel.setToolbar(toolbar);
    }

    protected void setToolbarTitle(String title) {
        if (title != null) {
            mMainViewModel.setToolbarTitle(title);
        }
    }

    protected void createSnackbarMessage(SnackbarMessage snackbarMessage) {
        if (snackbarMessage != null) {
            mMainViewModel.setSnackbarResource(snackbarMessage);
        }
    }

    protected void toggleLibraryStatus(MusicListItem item) {
        if (item != null && item.id != null) {
            mMainViewModel.toggleMediaItemLibraryStatus(item);
        }
    }

    /*
     * Gets the layout id associated with the child fragment
     * @return
     */
    protected abstract int getLayoutId();

    protected abstract void init(View view);

}


