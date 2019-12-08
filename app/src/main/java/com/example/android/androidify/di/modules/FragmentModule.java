package com.example.android.androidify.di.modules;

import com.example.android.androidify.fragments.artist.ArtistGalleryFragment;
import com.example.android.androidify.fragments.common.MediaItemDialogFragment;
import com.example.android.androidify.fragments.details.AlbumFragment;
import com.example.android.androidify.fragments.artist.ArtistDetailsFragment;
import com.example.android.androidify.fragments.details.PlaylistFragment;
import com.example.android.androidify.fragments.details.TrackDetailsFragment;
import com.example.android.androidify.fragments.search.SearchResultsFragment;
import com.example.android.androidify.fragments.TopHistoryFragment;
import com.example.android.androidify.fragments.history.TopHistoryGalleryFragment;
import com.example.android.androidify.fragments.library.LibraryFragment;
import com.example.android.androidify.fragments.library.LibraryListFragment;
import com.example.android.androidify.fragments.list.MediaItemGalleryFragment;
import com.example.android.androidify.fragments.search.SearchFragment;
import com.example.android.androidify.fragments.tracks.TrackListFragment;
import com.example.android.androidify.fragments.tracks.TrackRecommendationFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract ArtistDetailsFragment contributeArtistDetailsFragment();

    @ContributesAndroidInjector
    abstract ArtistGalleryFragment contributeArtistGalleryFragment();

    @ContributesAndroidInjector
    abstract AlbumFragment contributeAlbumFragment();

    @ContributesAndroidInjector
    abstract PlaylistFragment contributePlaylistFragment();

    @ContributesAndroidInjector
    abstract LibraryFragment contributesLibraryFragment();

    @ContributesAndroidInjector
    abstract LibraryListFragment contributesLibraryListFragment();

    @ContributesAndroidInjector
    abstract MediaItemDialogFragment contributesMediaItemDialogFragment();

    @ContributesAndroidInjector
    abstract TopHistoryFragment contributeTopHistoryFragment();

    @ContributesAndroidInjector
    abstract TrackListFragment contributesTrackListFragment();

    @ContributesAndroidInjector
    abstract TrackRecommendationFragment contributeTrackRecommendationFragment();

    @ContributesAndroidInjector
    abstract TrackDetailsFragment contributesTrackDetailsFragment();

    @ContributesAndroidInjector
    abstract TopHistoryGalleryFragment contributesTopHistoryGalleryFragment();

    @ContributesAndroidInjector
    abstract SearchFragment contributesSearchFragment();

    @ContributesAndroidInjector
    abstract SearchResultsFragment contributesSearchResultsFragment();

    @ContributesAndroidInjector
    abstract MediaItemGalleryFragment contributesMediaItemGalleryFragment();
}
