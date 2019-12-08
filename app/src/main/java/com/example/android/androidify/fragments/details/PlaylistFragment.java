package com.example.android.androidify.fragments.details;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.androidify.R;
import com.example.android.androidify.adapter.MediaItemPagedListAdapter;
import com.example.android.androidify.api.models.Playlist;
import com.example.android.androidify.api.models.PublicUser;
import com.example.android.androidify.base.AbsMediaItemDetailsFragment;
import com.example.android.androidify.interfaces.MediaItemClickListener;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.model.SnackbarMessage;
import com.example.android.androidify.repository.datasource.NetworkState;
import com.example.android.androidify.viewmodel.PlaylistViewModel;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class PlaylistFragment extends AbsMediaItemDetailsFragment<PlaylistViewModel, Playlist>
        implements MediaItemClickListener {

    private static final String TAG = "PLAYLIST_FRAG";

    @BindView(R.id.media_item_recycler_view)
    public RecyclerView mPlaylistTracksRv;

    @BindView(R.id.media_item_progress_bar)
    ProgressBar mProgressBar;

    private MediaItemPagedListAdapter mAdapter;


    public static PlaylistFragment newInstance(String id) {
        PlaylistFragment playlistFragment = new PlaylistFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, id);
        playlistFragment.setArguments(args);
        return playlistFragment;
    }

    @Override
    protected int getViewStubLayout() {
        return R.layout.fragment_media_item_list;
    }

    @Override
    protected PlaylistViewModel getDetailsViewModel() {
        return ViewModelProviders.of(this, mFactoryModel).get(PlaylistViewModel.class);
    }

    @Override
    protected void initDetailsFragment() {
        mAdapter = new MediaItemPagedListAdapter(getContext(), this);
        mPlaylistTracksRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mPlaylistTracksRv.setAdapter(mAdapter);

        mMediaItemDetailsViewModel.setPlaylistId(mId);
        mMediaItemDetailsViewModel.getPlaylist().observe(getViewLifecycleOwner(), super::onDetailsResponse);

        mMediaItemDetailsViewModel.getPlaylistTracks(mId).observe(getViewLifecycleOwner(), tracks -> {
            if (tracks != null) {
                mAdapter.submitList(tracks);
            }
        });

        mMediaItemDetailsViewModel.getPlaylistTracksNetworkState().observe(getViewLifecycleOwner(), networkState -> {
            if (networkState != null && networkState.getStatus() == NetworkState.Status.FAILED) {
                createSnackbarMessage(SnackbarMessage.error());
            }

            mAdapter.setNetworkState(networkState);
        });
    }


    @Override
    protected void updateUi() {
        displayImage(mDetailsItem.images);
        mHeaderTextView.setText(mDetailsItem.name);
        PublicUser owner = mDetailsItem.owner;
        String authorString = owner != null ? "By " + owner.display_name : "Unknown";
        mMetaTextView.setText(authorString);
    }

    @Override
    protected String getTitle() {
        return mDetailsItem != null ? mDetailsItem.name : " ";
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.details_play_button:
                startPlayback(mDetailsItem.uri);
                break;
            case R.id.details_context_menu_button:
                mMainViewModel.openMediaItemDialog(new MusicListItem(mDetailsItem));
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        MusicListItem item = mAdapter.getItemAtPosition(position);
        if (item != null) {
            if (view.getId() == R.id.track_context_menu) {
                showContextMenu(item);
            } else {
                startPlayback(item.uri);
            }
        }
    }

    @Override
    public void openContextMenu(View v, int position) {
        MusicListItem item = mAdapter.getItemAtPosition(position);

        if (item != null) {
            showContextMenu(item);
        }
    }
}

