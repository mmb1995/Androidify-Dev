package com.example.android.androidify.di.modules;

import com.example.android.androidify.fragments.ArtistFragment;
import com.example.android.androidify.fragments.ArtistGalleryFragment;
import com.example.android.androidify.fragments.Details.AlbumFragment;
import com.example.android.androidify.fragments.Details.ArtistDetailsFragment;
import com.example.android.androidify.fragments.Details.BaseDetailsFragment;
import com.example.android.androidify.fragments.HomeFragment;
import com.example.android.androidify.fragments.SearchResultsFragment;
import com.example.android.androidify.fragments.TopHistoryFragment;
import com.example.android.androidify.fragments.TopHistoryListFragment;
import com.example.android.androidify.fragments.TrackListFragment;
import com.example.android.androidify.fragments.list.ImageGalleryFragment;
import com.example.android.androidify.fragments.tracks.TracksFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentModule {
    @ContributesAndroidInjector
    abstract ArtistFragment contributeArtistFragment();

    @ContributesAndroidInjector
    abstract ArtistDetailsFragment contributeArtistDetailsFragment();

    @ContributesAndroidInjector
    abstract ArtistGalleryFragment contributeArtistGalleryFragment();

    @ContributesAndroidInjector
    abstract AlbumFragment contributeAlbumFragment();

    @ContributesAndroidInjector
    abstract BaseDetailsFragment contributeBaseDetailsFragment();

    @ContributesAndroidInjector
    abstract TopHistoryListFragment contributeScrollingMusicFragment();

    @ContributesAndroidInjector
    abstract HomeFragment contributeHomeFragment();

    @ContributesAndroidInjector
    abstract ImageGalleryFragment contributeImageGalleryFragment();

    @ContributesAndroidInjector
    abstract TopHistoryFragment contributeTopHistoryFragment();

    @ContributesAndroidInjector
    abstract TrackListFragment contributesTrackListFragment();

    @ContributesAndroidInjector
    abstract TracksFragment contributesTracksFragment();

    @ContributesAndroidInjector
    abstract SearchResultsFragment contributesSearchResultsFragment();
}
