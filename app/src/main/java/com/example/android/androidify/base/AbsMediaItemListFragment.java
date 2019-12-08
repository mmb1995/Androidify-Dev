package com.example.android.androidify.base;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.androidify.R;
import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.model.SnackbarMessage;
import com.example.android.androidify.utils.Constants;

import java.util.List;

import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public abstract class AbsMediaItemListFragment<A extends BaseRecyclerViewAdapter, LM extends RecyclerView.LayoutManager,
        VM extends ViewModel> extends BaseFragment {

    private static final String TAG = "ABS_MEDIA_LIST";

    @BindView(R.id.media_item_recycler_view)
    public RecyclerView mMediaItemRv;

    @BindView(R.id.media_item_progress_bar)
    ProgressBar mProgressBar;

    protected A mAdapter;
    protected LM mLayoutManager;
    protected VM mMediaItemViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "creating fragment");
        mMediaItemViewModel = getMediaItemViewModel();
        if (getArguments() != null) {
            initVariables(getArguments(), savedInstanceState);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_media_item_list;
    }

    @Override
    protected void init(View view) {
        configureRecyclerView();
        configureMediaItemViewModel();
    }

    private void configureRecyclerView() {
        mLayoutManager = getLayoutManager();
        mAdapter = getAdapter();
        mMediaItemRv.setLayoutManager(mLayoutManager);
        mMediaItemRv.setAdapter(mAdapter);
    }


    public void onResponse(ApiResponse<List<MusicListItem>> response) {
        if (response != null) {
            switch (response.status) {
                case LOADING:
                    Log.i(TAG, "data loading");
                    mProgressBar.setVisibility(View.VISIBLE);
                    mMediaItemRv.setVisibility(View.INVISIBLE);
                    break;
                case SUCCESS:
                    //Log.i(TAG, "success size = " + response.data.size());
                    mProgressBar.setVisibility(View.GONE);
                    mAdapter.setItems(response.data);
                    mMediaItemRv.setVisibility(View.VISIBLE);
                    break;
                case UNAUTHORIZED:
                    mProgressBar.setVisibility(View.GONE);
                    mMainViewModel.onErrorEvent(Constants.UNAUTHORIZED);
                case ERROR:
                    mProgressBar.setVisibility(View.GONE);
                    createSnackbarMessage(SnackbarMessage.error());
                    break;
            }
        }
    }

    protected abstract void initVariables(Bundle arguments, Bundle savedInstanceState);
    protected abstract A getAdapter();
    protected abstract LM getLayoutManager();
    protected abstract VM getMediaItemViewModel();
    protected abstract void configureMediaItemViewModel();

}

/*public abstract class AbsMediaItemListFragment<A extends BaseRecyclerViewAdapter, LM extends RecyclerView.LayoutManager,
        VM extends ViewModel> extends Fragment {

    private static final String TAG = "MEDIA_ITEM_FRAG";

*//*    @BindView(R.id.media_item_progress_bar)
    ProgressBar mMediaProgressBar;*//*

    @BindView(R.id.media_item_recycler_view)
    RecyclerView mMediaItemRv;

*//*    @BindView(R.id.no_results_tv)
    TextView mEmptyTextView;*//*

    *//**
     * Used to facilitate communication between fragments and the MainActivity
     *//*
    protected MainActivityViewModel mMainViewModel;

    @Inject
    protected FactoryViewModel mFactoryModel;


    protected A mAdapter;
    protected LM mLayoutManager;
    protected VM mMediaItemViewModel;


    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainViewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
        mMediaItemViewModel = getMediaItemViewModel();
        Bundle arguments = getArguments();
        if (arguments != null) {
            initVariables(arguments, savedInstanceState);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_media_item_list, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configureRecyclerView();
        configureMediaItemViewModel();
    }

    private void configureRecyclerView() {
        mLayoutManager = getLayoutManager();
        mAdapter = getAdapter();
        //mMediaItemRv.setNestedScrollingEnabled(false);
        mMediaItemRv.setLayoutManager(mLayoutManager);
        mMediaItemRv.setAdapter(mAdapter);
    }

    public void onResponse(ApiResponse<List<MusicListItem>> response) {
        if (response != null) {
            switch (response.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    Log.i(TAG, "success size = " + response.data.size());
                    mAdapter.setItems(response.data);
                    break;
                case UNAUTHORIZED:
                    mMainViewModel.onErrorEvent(Constants.UNAUTHORIZED);
                case ERROR:
                    //mMediaProgressBar.setVisibility(View.INVISIBLE);
                    showSnackbarMessage(getResources().getString(R.string.error_message));
                    break;
            }
        }
    }

    protected void showSnackbarMessage(String message) {
        if (message != null) {
            mMainViewModel.setSnackBarMessage(message);
        }
    }

    protected void setCurrentPlayback(String uri) {
        if (uri != null) {
            mMainViewModel.onPlayUri(uri);
        }
    }

*//*    protected void navigateToArtist(String artistId) {
        if (artistId != null) {
            mMainViewModel.setCurrentArtistId(artistId);
        }
    }*//*

    protected void navigateToArtist(View view, String artistId) {
        Bundle bundle = new Bundle();
        bundle.putString(BaseDetailsFragment.ARG_ID, artistId);
        Navigation.findNavController(view).navigate(R.id.action_global_artistFragment, bundle);
    }

    protected void navigateToAlbum(View view, String albumId) {
        Bundle bundle = new Bundle();
        bundle.putString(BaseDetailsFragment.ARG_ID, albumId);
        Navigation.findNavController(view).navigate(R.id.action_global_albumFragment, bundle);
    }

    protected void navigateToTrack(View view, String trackId) {
        Bundle bundle = new Bundle();
        bundle.putString(BaseDetailsFragment.ARG_ID, trackId);
        Navigation.findNavController(view).navigate(R.id.action_global_trackFragment, bundle);
    }

    protected void setToolbarTitle(String title) {
        if (title != null) {
            mMainViewModel.setToolbarTitle(title);
        }
    }

*//*    protected void navigateToAlbum(String albumId) {
        if (albumId != null) {
            mMainViewModel.setCurrentAlbumId(albumId);
        }
    }

    protected void navigateToTrack(String trackId) {
        if (trackId != null) {
            mMainViewModel.navigateToTrack(trackId);
        }
    }*//*

    protected abstract void initVariables(Bundle arguments, Bundle savedInstanceState);
    protected abstract A getAdapter();
    protected abstract LM getLayoutManager();
    protected abstract VM getMediaItemViewModel();
    protected abstract void configureMediaItemViewModel();

}*/
