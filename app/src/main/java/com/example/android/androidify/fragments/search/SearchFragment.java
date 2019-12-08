package com.example.android.androidify.fragments.search;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.example.android.androidify.R;
import com.example.android.androidify.activities.MainActivity;
import com.example.android.androidify.adapter.SearchPageAdapter;
import com.example.android.androidify.base.BaseFragment;
import com.example.android.androidify.repository.SpotifySearchSuggestionsProvider;
import com.example.android.androidify.viewmodel.SearchViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;

public class SearchFragment extends BaseFragment {
    private static final String TAG = "SEARCH_FRAG";

    public static final String ARG_SEARCH_QUERY = "arg_search_query";

    @BindView(R.id.search_tabs)
    TabLayout mTabLayout;

    @BindView(R.id.search_view_pager)
    ViewPager mViewPager;

    @BindView(R.id.search_toolbar)
    MaterialToolbar mToolbar;

/*    @BindView(R.id.search_view)
    SearchView mSearchView;*/

    private String mQuery;

    private SearchViewModel mSearchModel;

    SearchView mSearchView;


    public static SearchFragment newInstance(@NonNull String query) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SEARCH_QUERY, query);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Creating search fragment");
        mSearchModel = ViewModelProviders.of(this, mFactoryModel).get(SearchViewModel.class);
        if (getArguments() != null) {
            mQuery = getArguments().getString(ARG_SEARCH_QUERY);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search;
    }

    @Override
    protected void init(View rootView) {
        setHasOptionsMenu(true);
        String[] labels = getResources().getStringArray(R.array.search_tab_labels);
        SearchPageAdapter adapter = new SearchPageAdapter(getChildFragmentManager(), null, labels);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);

/*        MainActivity mainActivity = (MainActivity) getActivity();
        SearchManager searchManager = (SearchManager) mainActivity.getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(mainActivity.getComponentName()));
        mSearchView.setQueryRefinementEnabled(true);
        mSearchView.setQueryHint(getResources().getString(R.string.search_hint));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                handleSearch(query);
                mSearchView.setQuery(query, false);
                mSearchView.clearFocus();
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions((MainActivity)getActivity(),
                        SpotifySearchSuggestionsProvider.AUTHORITY, SpotifySearchSuggestionsProvider.MODE);
                suggestions.saveRecentQuery(query, null);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = mSearchView.getSuggestionsAdapter().getCursor();
                cursor.moveToPosition(position);
                mSearchView.setQuery(cursor.getString(2), true);
                return true;
            }
        });*/
    }


    @SuppressLint("NewApi")
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search_bar, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        MainActivity mainActivity = (MainActivity) getActivity();
        SearchManager searchManager = (SearchManager) mainActivity.getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) item.getActionView();
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(mainActivity.getComponentName()));
        mSearchView.setQueryRefinementEnabled(true);
        /*mSearchView.setQueryHint(getResources().getString(R.string.search_hint));*/

        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                return true;
            }
        });

        // Make searchview suggestions occupy full screen
        final SearchView.SearchAutoComplete searchAutoComplete =
                (SearchView.SearchAutoComplete) mSearchView.findViewById(androidx.appcompat.R.id.search_src_text);

        final AutoCompleteTextView autoCompleteTextView =
                (AutoCompleteTextView) searchAutoComplete.findViewById(R.id.search_src_text);

        final View dropDownAnchor = mSearchView.findViewById(autoCompleteTextView.getDropDownAnchor());


        if (dropDownAnchor != null) {
            dropDownAnchor.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                    Rect screenSize = new Rect();
                    getActivity().getWindowManager().getDefaultDisplay().getRectSize(screenSize);
                    autoCompleteTextView.setDropDownWidth(screenSize.width());
                }
            });
        }

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG, "query = " + query);
                handleSearch(query);
                mSearchView.setQuery(query, false);
                mSearchView.clearFocus();
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions((MainActivity)getActivity(),
                        SpotifySearchSuggestionsProvider.AUTHORITY, SpotifySearchSuggestionsProvider.MODE);
                suggestions.saveRecentQuery(query, null);
                /*searchAutoComplete.showDropDown()*/;

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });

        mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = mSearchView.getSuggestionsAdapter().getCursor();
                cursor.moveToPosition(position);
                mSearchView.setQuery(cursor.getString(2), true);
                return true;
            }
        });

        item.expandActionView();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                Log.i(TAG, "search clicked");
                return false;
        }
        Log.i(TAG, "id = " + id);
        return super.onOptionsItemSelected(item); // important line
    }

    private void handleSearch(String query) {
        Log.i(TAG, query);
        mSearchModel.setQuery(query);
    }
}
