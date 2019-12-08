package com.example.android.androidify.base;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.androidify.R;
import com.example.android.androidify.model.MusicListItem;

import androidx.lifecycle.ViewModel;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public abstract class AbsPagedMediaItemListFragment<A extends PagedListAdapter, LM extends RecyclerView.LayoutManager,
        VM extends ViewModel> extends BaseFragment {

    private static final String TAG = "ABS_PAGED_MEDIA_LIST";

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

    public void handleResponse(PagedList<MusicListItem> items) {
        Log.i(TAG, "submitting paged list size = " + items.size());
        mAdapter.submitList(items);
    }

    protected abstract void initVariables(Bundle arguments, Bundle savedInstanceState);
    protected abstract A getAdapter();
    protected abstract LM getLayoutManager();
    protected abstract VM getMediaItemViewModel();
    protected abstract void configureMediaItemViewModel();

}
