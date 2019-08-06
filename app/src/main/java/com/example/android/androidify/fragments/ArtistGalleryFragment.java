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

import com.example.android.androidify.R;
import com.example.android.androidify.adapter.ArtistGalleryAdapter;
import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.interfaces.MusicPlaybackClickListener;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.utils.Constants;
import com.example.android.androidify.viewmodel.ArtistGalleryViewModel;
import com.example.android.androidify.viewmodel.ArtistViewModel;
import com.example.android.androidify.viewmodel.FactoryViewModel;
import com.example.android.androidify.viewmodel.MainActivityViewModel;
import com.example.android.androidify.viewmodel.TopHistoryViewModel;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistGalleryFragment extends Fragment implements MusicPlaybackClickListener {
    private static final String TAG = "ARTIST_GALLERY";

    private static final String ARG_ARTIST_ID = "arg_artist_id";
    private static final String ARG_TIME_RANGE = "arg_time_range";
    private static final String GET_TOP_ARTISTS = "get_top_artists";

    @BindView(R.id.artist_gallery_recycler_view)
    RecyclerView mArtistGalleryRv;

    @BindView(R.id.dropdown)
    TextInputLayout mDropdownContainer;

    @BindView(R.id.filled_exposed_dropdown)
    AutoCompleteTextView mDropdown;

    @BindView(R.id.artist_gallery_progress_bar)
    ProgressBar mProgressBar;

    @Inject
    FactoryViewModel mFactoryModel;

    private ArtistGalleryAdapter mAdapter;
    private String mArtistId;
    private String mTimeRange;
    private MainActivityViewModel mViewModel;
    private ArtistGalleryViewModel mArtistGalleryViewModel;

    public ArtistGalleryFragment() {
        // Required empty public constructor
    }

    public static ArtistGalleryFragment newInstance(String id, String range) {
        ArtistGalleryFragment fragment = new ArtistGalleryFragment();
        Bundle args = new Bundle();
        if (id != null) {
            args.putString(ARG_ARTIST_ID, id);
        } else if(range != null) {
            args.putString(ARG_TIME_RANGE, range);
        }
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mArtistId = getArguments().getString(ARG_ARTIST_ID);
            mTimeRange = getArguments().getString(ARG_TIME_RANGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_artist_gallery, container, false);
        ButterKnife.bind(this, rootView);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        this.mArtistGalleryRv.setNestedScrollingEnabled(false);
        this.mArtistGalleryRv.setLayoutManager(manager);
        this.mAdapter = new ArtistGalleryAdapter(getContext(), this);
        this.mArtistGalleryRv.setAdapter(mAdapter);
        return rootView;
    }

    /**
     * Sets up dropdown to allow user to select time range for user history data
     */
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
                        mArtistGalleryViewModel.setTimeRange(Constants.LONG_TERM);
                        break;
                    case 1:
                        mArtistGalleryViewModel.setTimeRange(Constants.MEDIUM_TERM);
                        break;
                    case 2:
                        mArtistGalleryViewModel.setTimeRange(Constants.SHORT_TERM);
                        break;
                }
                mDropdown.setText(adapter.getItem(position), false);
            }
        });
        mDropdown.setVisibility(View.VISIBLE);
        mDropdown.setText(adapter.getItem(1), false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AndroidSupportInjection.inject(this);
        mViewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
        mArtistGalleryViewModel = ViewModelProviders.of(this, mFactoryModel).get(ArtistGalleryViewModel.class);
        configureArtistData();
    }

    private void configureArtistData() {
        if (mTimeRange != null) {
            mArtistGalleryViewModel.initTopArtists();
            mArtistGalleryViewModel.setTimeRange(mTimeRange);
            getTopArtists();
            displayDropdown();
        } else {
            mDropdown.setVisibility(View.GONE);
            mDropdownContainer.setVisibility(View.GONE );
            getRelatedArtists();
        }
    }

    private void getRelatedArtists() {
        ArtistViewModel model = ViewModelProviders.of(this, mFactoryModel).get(ArtistViewModel.class);
        model.getRelatedArtists(mArtistId).observe(this, (ApiResponse<List<MusicListItem>> response) -> {
          handleArtistResponse(response);
        });
    }

    private void getTopArtists() {
        mArtistGalleryViewModel.getTopArtists().observe(this, (ApiResponse<List<MusicListItem>> response) -> {
            handleArtistResponse(response);
        });
    }

    private void handleArtistResponse(ApiResponse<List<MusicListItem>> response) {
        if (response != null) {
            switch (response.status) {
                case LOADING:
                    mArtistGalleryRv.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    this.mAdapter.setItems(response.data);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mArtistGalleryRv.setVisibility(View.VISIBLE);
                    break;
                case ERROR:
                    mProgressBar.setVisibility(View.INVISIBLE);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onItemClicked(int position) {
        MusicListItem item = mAdapter.getItemAtPosition(position);
        Log.i(TAG, item.name);
        if (item != null) {
            mViewModel.setCurrentArtistId(item.id);
        }
    }
}
