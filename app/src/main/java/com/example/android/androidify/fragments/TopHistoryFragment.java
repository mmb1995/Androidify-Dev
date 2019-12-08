package com.example.android.androidify.fragments;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.android.androidify.R;
import com.example.android.androidify.adapter.TopHistoryPageAdapter;
import com.example.android.androidify.base.BaseFragment;
import com.example.android.androidify.utils.Constants;
import com.example.android.androidify.viewmodel.TopHistoryViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;


public class TopHistoryFragment extends BaseFragment implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.top_history_view_pager)
    ViewPager mViewPager;

    @BindView(R.id.top_history_tabs)
    TabLayout mTabLayout;

    @BindView(R.id.top_history_toolbar)
    MaterialToolbar mToolbar;

/*    @BindView(R.id.filled_exposed_dropdown)
    AutoCompleteTextView mDropdown;*/

    private Spinner mSpinner;
    private TopHistoryViewModel mViewModel;

    private boolean spinnerSynched;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity(), mFactoryModel).get(TopHistoryViewModel.class);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_top_history;
    }

    @Override
    protected void init(View view) {
        /*setToolbarTitle(getResources().getString(R.string.top_history_title));*/
/*        setHasOptionsMenu(true);
        setupToolbar();*/
        setHasOptionsMenu(true);
        /*setupToolbar();*/
        TopHistoryPageAdapter adapter = new TopHistoryPageAdapter(getContext(), getChildFragmentManager());
        /*mViewPager.setOffscreenPageLimit(2);*/
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_dropdown, menu);

        MenuItem item = menu.findItem(R.id.action_dropdown);
        this.mSpinner = (Spinner) item.getActionView();

        // Create the mSpinner's adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.top_history_dropdown_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(this);

        // Sync spinner position with TopHistoryViewModel
        int currentPosition = mViewModel.getSelectedPosition();
        if (currentPosition == -1) {
            spinnerSynched = true;
            mSpinner.setSelection(1);
        } else if (currentPosition != mSpinner.getSelectedItemPosition()) {
            // Avoids unnecessary reload of data
            spinnerSynched = false;
            mSpinner.setSelection(currentPosition);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                navigateToSearch();
                return true;
        }

        return super.onOptionsItemSelected(item); // important line
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if (!spinnerSynched) {
            spinnerSynched = true;
            return;
        }

        switch (position) {
            case 0:
                mViewModel.setTimeRange(Constants.LONG_TERM);
                break;
            case 1:
                mViewModel.setTimeRange(Constants.MEDIUM_TERM);
                break;
            case 2:
                mViewModel.setTimeRange(Constants.SHORT_TERM);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Mandatory override
    }

}
