package com.example.android.androidify.base;

import android.view.View;

import com.example.android.androidify.R;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;

public abstract class AbsViewPagerFragment<A extends FragmentPagerAdapter> extends BaseFragment {

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private A mViewPagerAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_view_pager;
    }

    @Override
    protected void init(View view) {
        mViewPagerAdapter = getViewPagerAdapter();
        mViewPager.setOffscreenPageLimit(getOffscreenPageLimit());
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    protected abstract A getViewPagerAdapter();
    protected abstract int getOffscreenPageLimit();
}
