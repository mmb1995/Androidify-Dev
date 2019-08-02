package com.example.android.androidify.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.android.androidify.R;
import com.example.android.androidify.adapter.TopHistoryPageAdapter;
import com.example.android.androidify.utils.Constants;
import com.example.android.androidify.view.CustomViewPager;
import com.example.android.androidify.viewmodel.TopHistoryViewModel;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.tabs.TabLayout;

import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class TopHistoryFragment extends androidx.fragment.app.Fragment {
    private static final String TAG = "TOP_HISTORY_FRAG";

    @BindView(R.id.top_history_view_pager)
    CustomViewPager mViewPager;

    @BindView(R.id.top_history_tabs)
    TabLayout mTabLayout;

    @BindView(R.id.filled_exposed_dropdown)
    AutoCompleteTextView mDropdown;

    private TopHistoryViewModel mViewModel;


    public TopHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_top_history, container, false);
        ButterKnife.bind(this, rootView);
        TopHistoryPageAdapter adapter = new TopHistoryPageAdapter(getContext(), getChildFragmentManager());
        MaterialShapeDrawable materialShapeDrawable = MaterialShapeDrawable.createWithElevationOverlay(getContext(),
                1);
        mViewPager.setBackground(materialShapeDrawable);
        mViewPager.setElevation(materialShapeDrawable.getElevation());

        // set up dropdown menu
        displayDropdown();
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewModel = ViewModelProviders.of(this).get(TopHistoryViewModel.class);

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
                        mViewModel.setTimeRange(Constants.LONG_TERM);
                        break;
                    case 1:
                        mViewModel.setTimeRange(Constants.MEDIUM_TERM);
                        break;
                    case 2:
                        mViewModel.setTimeRange(Constants.SHORT_TERM);
                        break;
                }
                mDropdown.setText(adapter.getItem(position), false);
                //mDropdown.setSelection();
            }
        });
        mDropdown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mDropdown.setText("", false);
                mDropdown.showDropDown();
                return false;
            }
        });
        mDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mDropdown.setText(adapter.getItem(position), false);
                mDropdown.showDropDown();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //mDropdown.setListSelection(1);
        //mDropdown.setText(adapter.getItem(1), false);
    }
}
