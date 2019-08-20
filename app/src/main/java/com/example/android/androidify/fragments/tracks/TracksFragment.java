package com.example.android.androidify.fragments.tracks;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import com.example.android.androidify.R;
import com.example.android.androidify.adapter.MaterialSpinnerAdapter;
import com.example.android.androidify.adapter.MusicListAdapter;
import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.base.BaseFragment;
import com.example.android.androidify.interfaces.ListItemClickListener;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.utils.Constants;
import com.example.android.androidify.viewmodel.TrackListViewModel;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class TracksFragment extends BaseFragment implements ListItemClickListener {
    private static final String TAG = "TRACKS_FRAGMENT";

    public static final String ARG_TYPE = "arg_track_type";
    public static final String ARG_ITEM_ID = "arg_item_id";

    @BindView(R.id.track_progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.track_rv)
    RecyclerView mTrackRecyclerView;

    @Nullable
    @BindView(R.id.filled_exposed_dropdown)
    AutoCompleteTextView mDropdown;

    private TrackListViewModel mTrackModel;
    private MusicListAdapter mAdapter;
    private String mType;
    private String mId;

    private List<MusicListItem> mTracks;

    public static TracksFragment newInstance(String id, String type) {
        TracksFragment fragment = new TracksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ITEM_ID, id);
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getString(ARG_TYPE);
            mId = getArguments().getString(ARG_ITEM_ID);
        }
    }

    @Override
    protected int getLayoutId() {
        if (mType.equals(Constants.TOP_TRACKS)) {
            return R.layout.fragment_track_dropdown;
        } else {
            return R.layout.fragment_track;
        }
    }

    @Override
    protected void setupUi(View rootView) {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mTrackRecyclerView.setNestedScrollingEnabled(false);
        mTrackRecyclerView.setLayoutManager(manager);
        mAdapter = new MusicListAdapter(getContext(), this, mType);
        mTrackRecyclerView.setAdapter(mAdapter);
        mTrackModel = ViewModelProviders.of(this, mFactoryModel).get(TrackListViewModel.class);
        setupDropdown();
        getTracks();
    }

    private void setupDropdown() {
        if (mType != null && mType.equals(Constants.TOP_TRACKS) && mDropdown != null) {
            String[] dropdownArray = getResources().getStringArray(R.array.top_history_dropdown_array);

            MaterialSpinnerAdapter<String> adapter = new MaterialSpinnerAdapter<>(
                    getContext(),
                    R.layout.item_dropdown_menu,
                    dropdownArray
            );
            mDropdown.setAdapter(adapter);
            mDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    switch (position) {
                        case 0:
                            mTrackModel.setTimeRange(Constants.LONG_TERM);
                            break;
                        case 1:
                            mTrackModel.setTimeRange(Constants.MEDIUM_TERM);
                            break;
                        case 2:
                            mTrackModel.setTimeRange(Constants.SHORT_TERM);
                            break;
                    }
                    mDropdown.setText(adapter.getItem(position), false);
                }
            });
            mDropdown.setText(adapter.getItem(1), false);
        }
    }

    private void getTracks() {
        if (mType.equals(Constants.TOP_TRACKS)) {
            mTrackModel.initUserTopTracks();
        } else {
            mTrackModel.init(mId, mType);
        }
        mTrackModel.getTracks().observe(this, response -> handleTrackResponse(response));
    }

    private void handleTrackResponse(ApiResponse<List<MusicListItem>> response) {
        if (response != null) {
            switch (response.status) {
                case LOADING:
                    Log.i(TAG, "loading tracks");
                    mTrackRecyclerView.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    Log.i(TAG, "size = " + response.data.size());
                    mAdapter.setItems(response.data);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mTrackRecyclerView.setVisibility(View.VISIBLE);
                    break;
                case ERROR:
                    mProgressBar.setVisibility(View.INVISIBLE);
                    showSnackbarMessage(getResources().getString(R.string.error_message));
                    break;
            }
        } else {
            Log.i(TAG, "response is null");
        }
    }


    @Override
    public void onItemSelected(int position) {
        MusicListItem selectedTrack = mAdapter.getItemAtPosition(position);
        if (selectedTrack != null) {
            setCurrentPlayback(selectedTrack.uri);
        }
    }

    @Override
    public void onLikeClicked(int position) {
        MusicListItem selectedTrack = mAdapter.getItemAtPosition(position);
        if (selectedTrack != null) {
            mTrackModel.toggleTrackSaveStatus(selectedTrack.id);
        }
    }
}
