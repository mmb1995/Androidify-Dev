package com.example.android.androidify.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.androidify.R;
import com.example.android.androidify.adapter.MusicListAdapter;
import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.interfaces.ListItemClickListener;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.utils.Constants;
import com.example.android.androidify.viewmodel.FactoryViewModel;
import com.example.android.androidify.viewmodel.MainActivityViewModel;
import com.example.android.androidify.viewmodel.TrackListViewModel;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;


public class TrackListFragment extends Fragment implements ListItemClickListener {
    private static final String TAG = "TRACK_LIST_FRAG";

    //private static final String ARG_MUSIC_LIST = "music_list";
    private static final String ARG_MUSIC_TYPE = "music_type";
    private static final String ARG_ID = "track_id";

    @BindView(R.id.track_list_rv)
    RecyclerView mMusicListRecyclerView;

    @BindView(R.id.track_list_progress_bar)
    ProgressBar mProgressBar;

    @Inject
    FactoryViewModel mFactoryModel;

    private MusicListAdapter mAdapter;
    private List<MusicListItem> mItems;
    private MainActivityViewModel mMainViewModel;
    private TrackListViewModel mTrackViewModel;
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
        mMainViewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
        mTrackViewModel = ViewModelProviders.of(this, mFactoryModel).get(TrackListViewModel.class);
        getTracks();
    }

    private void getTracks() {
        switch (this.mType) {
            case Constants.ARTIST:
                mTrackViewModel.getTracks(mId, mType).observe(this, (ApiResponse<List<MusicListItem>> response) -> {
                    switch (response.status) {
                        case LOADING:
                            mProgressBar.setVisibility(View.VISIBLE);
                            break;
                        case ERROR:
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Failed to load tracks", Toast.LENGTH_SHORT).show();
                            break;
                        case SUCCESS:
                            mProgressBar.setVisibility(View.GONE);
                            this.mItems = response.data;
                            this.mAdapter.setItems(mItems);
                            getTracksSavedStatus(mItems);
                            break;
                        default:
                            break;
                    }
                });
                break;
            default:
                Log.i(TAG, "default case");
        }
    }

    private void getTracksSavedStatus(List<MusicListItem> tracks) {
        mTrackViewModel.checkTracks().observe(this, (ApiResponse<Boolean[]> response) -> {
            switch (response.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    updateLikeStatus(response.data);
                    break;
                case ERROR:
                    break;
            }
        });
    }

    private void updateLikeStatus(Boolean[] likedTracks) {
        for (int i = 0; i < likedTracks.length; i++) {
           MusicListItem track = mItems.get(i);
           track.isLiked = likedTracks[i];
        }
        this.mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemSelected(int position) {
        MusicListItem item = this.mAdapter.getItemAtPosition(position);
        if (item != null) {
            String uri = item.uri;
            mMainViewModel.setCurrentlyPlaying(uri);
        }
    }

    @Override
    public void onLikeClicked(int position) {
        MusicListItem item = this.mAdapter.getItemAtPosition(position);
        if (item.isLiked) {
            mTrackViewModel.removeTrack(item.id).observe(this, (ApiResponse<Void> response) -> {
                handleLikeAction(item, position, response);
            });
        } else {
            mTrackViewModel.saveTrack(item.id).observe(this, (ApiResponse<Void> response) -> {
                handleLikeAction(item, position, response);
            });
        }
    }

    private void handleLikeAction(MusicListItem item, int position, ApiResponse<Void> response) {
        Log.i(TAG, "handle like click");
        switch (response.status) {
            case LOADING:
                break;
            case SUCCESS:
                Log.i(TAG, "liked before = " + item.isLiked);
                item.isLiked = !item.isLiked;
                Log.i(TAG, "liked after = " + item.isLiked);
                this.mAdapter.notifyItemChanged(position);
                break;
            case ERROR:
                Log.e(TAG, "Failed");
                break;
        }
    }
}
