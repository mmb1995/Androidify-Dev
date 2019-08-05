package com.example.android.androidify.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.android.material.textfield.TextInputLayout;

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
    private static final String ARG_RANGE = "time_range";

    @BindView(R.id.track_list_rv)
    RecyclerView mMusicListRecyclerView;

    @BindView(R.id.track_list_progress_bar)
    ProgressBar mProgressBar;

    /**
    @Nullable
    @BindView(R.id.dropdown_container)
    TextInputLayout mDropdownContainer;
     **/

    @BindView(R.id.filled_exposed_dropdown)
    AutoCompleteTextView mDropdown;

    @Inject
    FactoryViewModel mFactoryModel;

    private MusicListAdapter mAdapter;
    private List<MusicListItem> mItems;
    private MainActivityViewModel mMainViewModel;
    private TrackListViewModel mTrackViewModel;
    private String mId;
    private String mType;
    private String mRange;

    public TrackListFragment() {
        // Required empty public constructor
    }

    public static TrackListFragment newInstance(String id, String type, String range) {
        TrackListFragment fragment = new TrackListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, id);
        args.putString(ARG_MUSIC_TYPE, type);
        args.putString(ARG_RANGE, range);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mId = getArguments().getString(ARG_ID, null);
            mType = getArguments().getString(ARG_MUSIC_TYPE);
            mRange = getArguments().getString(ARG_RANGE, Constants.MEDIUM_TERM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_track_list, container, false);
        ButterKnife.bind(this, rootView);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mMusicListRecyclerView.setNestedScrollingEnabled(false);
        mMusicListRecyclerView.setLayoutManager(manager);
        this.mAdapter = new MusicListAdapter(getContext(), this, this.mType);
        this.mMusicListRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    private void displayDropdown() {
        String[] dropdownArray = getResources().getStringArray(R.array.top_history_dropdown_array);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                R.layout.item_dropdown_menu,
                dropdownArray
        );
        mDropdown.setAdapter(adapter);
        mDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.i(TAG, "pos = " + position);
                switch (position) {
                    case 0:
                        mTrackViewModel.setTimeRange(Constants.LONG_TERM);
                        break;
                    case 1:
                        mTrackViewModel.setTimeRange(Constants.MEDIUM_TERM);
                        break;
                    case 2:
                        mTrackViewModel.setTimeRange(Constants.SHORT_TERM);
                        break;
                }
                mDropdown.setText(adapter.getItem(position), false);
                //mDropdown.setSelection();
            }
        });
        mDropdown.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AndroidSupportInjection.inject(this);
        mMainViewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
        mTrackViewModel = ViewModelProviders.of(this, mFactoryModel).get(TrackListViewModel.class);
        configureViewModel();
    }

    private void configureViewModel() {
        if (mType.equals(Constants.TOP_TRACKS) || mType.equals(Constants.TOP_ARTISTS)) {
            mTrackViewModel.initTopTracks();
            mTrackViewModel.setTimeRange(mRange);
            displayDropdown();
        } else {
            mDropdown.setVisibility(View.GONE);
            TextInputLayout mTextInput = getView().findViewById(R.id.dropdown_container);
            if (mTextInput != null) {
                mTextInput.setEndIconMode(TextInputLayout.END_ICON_NONE);
                mTextInput.setVisibility(View.GONE);
            }
        }
        getTracks();
    }

    private void getTracks() {
        switch (this.mType) {
            case Constants.ARTIST:
                mTrackViewModel.getTracks(mId, mType, mRange).observe(this, (ApiResponse<List<MusicListItem>> response) -> {
                    handleTrackResponse(response);
                });
                break;
            case Constants.RECENTLY_PLAYED:
                mTrackViewModel.getTracks(null, mType, mRange).observe(this, (ApiResponse<List<MusicListItem>> response) -> {
                    handleTrackResponse(response);
                });
                break;
            case Constants.TOP_TRACKS:
                mTrackViewModel.getTopTracks().observe(this, (ApiResponse<List<MusicListItem>> response) -> {
                    handleTrackResponse(response);
                });
                break;
        }
    }

    private void handleTrackResponse(ApiResponse<List<MusicListItem>> response) {
        //Log.i(TAG, mRange + " " + mType);
        switch (response.status) {
            case LOADING:
                mMusicListRecyclerView.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                break;
            case ERROR:
                mProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity(), "Failed to load tracks", Toast.LENGTH_SHORT).show();
                break;
            case SUCCESS:
                mProgressBar.setVisibility(View.INVISIBLE);
                mMusicListRecyclerView.setVisibility(View.VISIBLE);
                this.mItems = response.data;
                this.mAdapter.setItems(mItems);
                getTracksSavedStatus(mItems);
                break;
            default:
                break;
        }
    }

    private void getTracksSavedStatus(List<MusicListItem> tracks) {
        mTrackViewModel.checkTracks(tracks).observe(this, (ApiResponse<Boolean[]> response) -> {
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
        switch (response.status) {
            case LOADING:
                break;
            case SUCCESS:
                //Log.i(TAG, "liked before = " + item.isLiked);
                item.isLiked = !item.isLiked;
                //Log.i(TAG, "liked after = " + item.isLiked);
                this.mAdapter.notifyItemChanged(position);
                break;
            case ERROR:
                Log.e(TAG, "Failed");
                break;
        }
    }
}
