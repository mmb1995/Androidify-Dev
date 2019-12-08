package com.example.android.androidify.fragments.search;

import android.os.Bundle;
import android.view.View;

import com.example.android.androidify.adapter.MediaItemListAdapter;
import com.example.android.androidify.base.AbsMediaItemListFragment;
import com.example.android.androidify.interfaces.MediaItemClickListener;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.utils.Constants;
import com.example.android.androidify.viewmodel.SearchViewModel;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

public class SearchResultsFragment extends AbsMediaItemListFragment<MediaItemListAdapter, LinearLayoutManager,
        SearchViewModel> implements MediaItemClickListener {

    private static final String ARG_SEARCH_TYPE = "arg_search_type";

    private String mSearchType;

    public static SearchResultsFragment newInstance(String searchType) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SEARCH_TYPE, searchType);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void initVariables(Bundle arguments, Bundle savedInstanceState) {
        mSearchType = arguments.getString(ARG_SEARCH_TYPE);
    }

    @Override
    protected MediaItemListAdapter getAdapter() {
        MediaItemListAdapter.ImageStyle imageStyle = mSearchType.equals(Constants.ARTIST) ?
                MediaItemListAdapter.ImageStyle.CIRCLE :
                MediaItemListAdapter.ImageStyle.SQUARE;

        return new MediaItemListAdapter(getContext(), this, imageStyle);
    }

    @Override
    protected LinearLayoutManager getLayoutManager() {
        return new LinearLayoutManager(getContext());
    }

    @Override
    protected SearchViewModel getMediaItemViewModel() {
        return ViewModelProviders.of(getParentFragment(), mFactoryModel).get(SearchViewModel.class);
    }

    @Override
    protected void configureMediaItemViewModel() {
        switch (mSearchType) {
            case Constants.ALBUM:
                mMediaItemViewModel.getAlbumSearchResults().observe(getViewLifecycleOwner(), super::onResponse);
                break;
            case Constants.ARTIST:
                mMediaItemViewModel.getArtistSearchResults().observe(getViewLifecycleOwner(), super::onResponse);
                break;
            case Constants.TRACK:
                mMediaItemViewModel.getTrackSearchResults().observe(getViewLifecycleOwner(), super::onResponse);
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        MusicListItem item = mAdapter.getItemAtPosition(position);

        if (item != null) {
            switch (item.type) {
                case TRACK:
                    //Log.i(TAG, item.name);
                    navigateToTrack(view, item.id);
                    break;
                case ARTIST:
                    navigateToArtist(view, item.id);
                    break;
                case ALBUM:
                    navigateToAlbum(view, item.id);
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

/*public class SearchResultsFragment extends AbsMediaItemListFragment<MusicCardGalleryAdapter, GridLayoutManager,
        SearchViewModel> implements OnItemClickListener {

    private static final String ARG_SEARCH_TYPE = "arg_search_type";

    private String mSearchType;

    public static SearchResultsFragment newInstance(String searchType) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SEARCH_TYPE, searchType);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void initVariables(Bundle arguments, Bundle savedInstanceState) {
        mSearchType = arguments.getString(ARG_SEARCH_TYPE);
    }

    @Override
    protected MusicCardGalleryAdapter getAdapter() {
        return new MusicCardGalleryAdapter(getContext(), this);
    }

    @Override
    protected GridLayoutManager getLayoutManager() {
        return new GridLayoutManager(getContext(), 2);
    }

    @Override
    protected SearchViewModel getMediaItemViewModel() {
        return ViewModelProviders.of(getParentFragment(), mFactoryModel).get(SearchViewModel.class);
    }

    @Override
    protected void configureMediaItemViewModel() {
        switch (mSearchType) {
            case Constants.ALBUM:
                mMediaItemViewModel.getAlbumSearchResults().observe(this, super::onResponse);
                break;
            case Constants.ARTIST:
                mMediaItemViewModel.getArtistSearchResults().observe(this, super::onResponse);
                break;
            case Constants.TRACK:
                mMediaItemViewModel.getTrackSearchResults().observe(this, super::onResponse);
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        MusicListItem item = mAdapter.getItemAtPosition(position);

        if (item != null) {
            switch (item.type) {
                case TRACK:
                    //Log.i(TAG, item.name);
                    navigateToTrack(view, item.id);
                    break;
                case ARTIST:
                    navigateToArtist(view, item.id);
                    break;
                case ALBUM:
                    navigateToAlbum(view, item.id);
            }
        }
    }
}*/


/*public class SearchResultsFragment extends AbsMediaItemListFragment<SearchAdapter, LinearLayoutManager,
        SearchViewModel> implements OnItemClickListener {

    private static final String ARG_SEARCH_TYPE = "arg_search_type";

    private String mSearchType;

    public static SearchResultsFragment newInstance(String searchType) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SEARCH_TYPE, searchType);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void initVariables(Bundle arguments, Bundle savedInstanceState) {
        mSearchType = arguments.getString(ARG_SEARCH_TYPE);
    }

    @Override
    protected SearchAdapter getAdapter() {
        return new SearchAdapter(getContext(), this);
    }

    @Override
    protected LinearLayoutManager getLayoutManager() {
        return new LinearLayoutManager(getContext());
    }

    @Override
    protected SearchViewModel getMediaItemViewModel() {
        return ViewModelProviders.of(getParentFragment(), mFactoryModel).get(SearchViewModel.class);
    }

    @Override
    protected void configureMediaItemViewModel() {
        switch (mSearchType) {
            case Constants.ALBUM:
                mMediaItemViewModel.getAlbumSearchResults().observe(this, super::onResponse);
                break;
            case Constants.ARTIST:
                mMediaItemViewModel.getArtistSearchResults().observe(this, super::onResponse);
                break;
            case Constants.TRACK:
                mMediaItemViewModel.getTrackSearchResults().observe(this, super::onResponse);
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        MusicListItem item = mAdapter.getItemAtPosition(position);

        if (item != null) {
            switch (item.type) {
                case Constants.TRACK:
                    //Log.i(TAG, item.name);
                    setCurrentPlayback(item.uri);
                    break;
                case Constants.ARTIST:
                    navigateToArtist(view, item.id);
                    break;
                case Constants.ALBUM:
                    navigateToAlbum(view, item.id);
            }
        }
    }
}*/

/**
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
 **/

