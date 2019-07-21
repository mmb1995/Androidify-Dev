package com.example.android.androidify.di.modules;

import com.example.android.androidify.fragments.ArtistFragment;
import com.example.android.androidify.fragments.ArtistGalleryFragment;
import com.example.android.androidify.fragments.TopHistoryFragment;
import com.example.android.androidify.fragments.TopHistoryListFragment;
import com.example.android.androidify.fragments.TrackListFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentModule {
    @ContributesAndroidInjector
    abstract ArtistFragment contributeArtistFragment();

    @ContributesAndroidInjector
    abstract ArtistGalleryFragment contributeArtistGalleryFragment();

    @ContributesAndroidInjector
    abstract TopHistoryListFragment contributeScrollingMusicFragment();

    @ContributesAndroidInjector
    abstract TopHistoryFragment contributeTopHistoryFragment();

    @ContributesAndroidInjector
    abstract TrackListFragment contributesTrackListFragment();
}
