package com.example.android.androidify.fragments.library;

/*
public class LibraryFragment extends BaseFragment {

    @BindView(R.id.media_item_recycler_view)
    public RecyclerView mMediaItemRv;

    @BindView(R.id.media_item_progress_bar)
    ProgressBar mProgressBar;

    private MediaItemPagedListAdapter mAdapter;
    private LibraryViewModel mViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, mFactoryModel).get(LibraryViewModel.class);
        */
/*mViewModel.init();*//*

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_media_item_list;
    }

    @Override
    protected void init(View view) {
        setHasOptionsMenu(true);
        mMediaItemRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new MediaItemPagedListAdapter(getContext());
        mMediaItemRv.setAdapter(mAdapter);
        mViewModel.getSavedTracks().observe(this, new Observer<PagedList<MusicListItem>>() {
            @Override
            public void onChanged(PagedList<MusicListItem> musicListItems) {
                if (musicListItems != null) {
                    mAdapter.submitList(musicListItems);
                }
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_activity, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_open_search:
                navigateToSearch();
                return true;
            case R.id.action_reset_auth:
                mMainViewModel.onErrorEvent(Constants.UNAUTHORIZED);
                return true;
        }

        return super.onOptionsItemSelected(item); // important line
    }

}
*/

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.android.androidify.R;
import com.example.android.androidify.adapter.LibraryPageAdapter;
import com.example.android.androidify.base.BaseFragment;
import com.example.android.androidify.utils.Constants;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;

public class LibraryFragment extends BaseFragment {

    @BindView(R.id.toolbar)
    MaterialToolbar mToolbar;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_view_pager;
    }

    @Override
    protected void init(View view) {
        setHasOptionsMenu(true);
/*        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        configureToolbar();*/
        LibraryPageAdapter adapter = new LibraryPageAdapter(getContext(), getChildFragmentManager());
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void configureToolbar() {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        Set<Integer> topDestinations = new HashSet<>();
        topDestinations.add(R.id.topHistoryFragment);
        topDestinations.add(R.id.searchFragment);
        topDestinations.add(R.id.libraryFragment);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(topDestinations).build();
        NavigationUI.setupWithNavController(mToolbar, navController, appBarConfiguration);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_activity, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_open_search:
                navigateToSearch();
                return true;
            case R.id.action_reset_auth:
                mMainViewModel.onErrorEvent(Constants.UNAUTHORIZED);
                return true;
        }

        return super.onOptionsItemSelected(item); // important line
    }

}
