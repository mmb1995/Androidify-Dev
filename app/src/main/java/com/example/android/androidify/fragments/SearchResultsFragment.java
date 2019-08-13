package com.example.android.androidify.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.android.androidify.R;
import com.example.android.androidify.adapter.SearchResultsAdapter;
import com.example.android.androidify.interfaces.SearchResultClickListener;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.utils.Constants;
import com.example.android.androidify.viewmodel.FactoryViewModel;
import com.example.android.androidify.viewmodel.MainActivityViewModel;
import com.example.android.androidify.viewmodel.SearchViewModel;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchResultsFragment extends Fragment implements SearchResultClickListener {
    private static final String TAG = "SEARCH_RESULTS_FRAG";
    private static final String ARG_QUERY = "arg_query";

    @BindView(R.id.search_results_rv)
    RecyclerView mSearchResultsRv;

    @BindView(R.id.search_progress_bar)
    ProgressBar mProgressBar;


    @Inject
    FactoryViewModel mFactoryModel;

    private String mQuery;

    private MainActivityViewModel mMainViewModel;
    private SearchViewModel mSearchModel;
    private SearchResultsAdapter mAdapter;


    public SearchResultsFragment() {
        // Required empty public constructor
    }

    public static SearchResultsFragment newInstance(String query) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUERY, query);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mQuery = getArguments().getString(ARG_QUERY);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search_results, container, false);
        ButterKnife.bind(this, rootView);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mAdapter = new SearchResultsAdapter(getContext(), this);
        mSearchResultsRv.setLayoutManager(manager);
        mSearchResultsRv.setNestedScrollingEnabled(true);
        mSearchResultsRv.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AndroidSupportInjection.inject(this);
        mMainViewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
        mSearchModel = ViewModelProviders.of(this, mFactoryModel).get(SearchViewModel.class);
        getSearchResults();
    }

    private void getSearchResults() {
        mSearchModel.getCombinedSearchResults(mQuery).observe(this, response -> {
            if (response != null) {
                switch (response.status) {
                    case LOADING:
                        mProgressBar.setVisibility(View.VISIBLE);
                        break;
                    case SUCCESS:
                        mProgressBar.setVisibility(View.INVISIBLE);
                        this.mAdapter.setSearchResults(response.data);
                        break;
                    case ERROR:
                        mProgressBar.setVisibility(View.INVISIBLE);
                        mMainViewModel.setSnackBarMessage(getContext().getString(R.string.error_message));
                        break;
                }
            }
        });
    }

    /**
    private void setUpRecyclerView(MusicListAdapter adapter, RecyclerView recyclerView) {
        MaterialShapeDrawable materialShapeDrawable = MaterialShapeDrawable.createWithElevationOverlay(getContext(), 2);
        recyclerView.setBackground(materialShapeDrawable);
        recyclerView.setElevation(materialShapeDrawable.getElevation());
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
    }
     **/

    /**
    private void getSearchResults() {
        mSearchModel.getTrackSearchResults(mQuery).observe(this, response -> {
            if (response != null) {
                switch (response.status) {
                    case LOADING:
                        mProgressBar.setVisibility(View.VISIBLE);
                        break;
                    case SUCCESS:
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Log.i(TAG, "" + response.data.size());
                        this.mTrackAdapter.setItems(response.data);
                        break;
                    case ERROR:
                        mProgressBar.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        });
        mSearchModel.getArtistSearchResults(mQuery).observe(this, response -> {
            if (response != null) {
                switch (response.status) {
                    case LOADING:
                        mProgressBar.setVisibility(View.VISIBLE);
                        break;
                    case SUCCESS:
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Log.i(TAG, "" + response.data.size());
                        this.mArtistAdapter.setItems(response.data);
                        break;
                    case ERROR:
                        mProgressBar.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        });
    }
     **/

    /**
    private void displayDropdown() {
        String[] dropdownArray = getResources().getStringArray(R.array.search_results_dropdown);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
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
                        mTrackViewModel.setTimeRange(Constants.LONG_TERM);
                        break;
                    case 1:
                        mTrackViewModel.setTimeRange(Constants.MEDIUM_TERM);
                        break;
                }
                mDropdown.setText(adapter.getItem(position), false);
            }
        });
        mDropdown.setVisibility(View.VISIBLE);
        mDropdown.setText(adapter.getItem(1), false);
    }
     **/

    @Override
    public void onSearchItemClicked(int position) {
        MusicListItem searchResult = mAdapter.getItemAtPosition(position);

        if (searchResult.type.equals(Constants.TRACK)) {
            mMainViewModel.setCurrentlyPlaying(searchResult.uri);
        } else {
            mMainViewModel.setCurrentArtistId(searchResult.id);
        }
    }
}
