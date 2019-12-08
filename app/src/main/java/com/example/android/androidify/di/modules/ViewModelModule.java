package com.example.android.androidify.di.modules;

import com.example.android.androidify.di.ViewModelKey;
import com.example.android.androidify.viewmodel.AlbumViewModel;
import com.example.android.androidify.viewmodel.ArtistViewModel;
import com.example.android.androidify.viewmodel.FactoryViewModel;
import com.example.android.androidify.viewmodel.ImageGalleryViewModel;
import com.example.android.androidify.viewmodel.LibraryViewModel;
import com.example.android.androidify.viewmodel.MainActivityViewModel;
import com.example.android.androidify.viewmodel.MediaItemDetailsViewModel;
import com.example.android.androidify.viewmodel.PlaylistViewModel;
import com.example.android.androidify.viewmodel.RecommendationsViewModel;
import com.example.android.androidify.viewmodel.SearchViewModel;
import com.example.android.androidify.viewmodel.TopHistoryViewModel;
import com.example.android.androidify.viewmodel.TrackListViewModel;
import com.example.android.androidify.viewmodel.TracksViewModel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ArtistViewModel.class)
    abstract ViewModel bindsArtistViewModel(ArtistViewModel artistViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AlbumViewModel.class)
    abstract ViewModel bindsAlbumViewModel(AlbumViewModel albumViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(LibraryViewModel.class)
    abstract ViewModel bindsLibraryViewModel(LibraryViewModel libraryViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PlaylistViewModel.class)
    abstract ViewModel bindsPlaylistViewModel(PlaylistViewModel playlistViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel.class)
    abstract ViewModel bindsMainActivityViewModel(MainActivityViewModel mainActivityViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MediaItemDetailsViewModel.class)
    abstract ViewModel bindsMediaItemDetailsViewModel(MediaItemDetailsViewModel mediaItemDetailsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(TrackListViewModel.class)
    abstract ViewModel bindsTrackListViewModel(TrackListViewModel trackListViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(TracksViewModel.class)
    abstract ViewModel bindsTracksViewModel(TracksViewModel tracksViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RecommendationsViewModel.class)
    abstract ViewModel bindsRecommendationsViewModel(RecommendationsViewModel recommendationsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(TopHistoryViewModel.class)
    abstract ViewModel bindsTopHistoryViewModel(TopHistoryViewModel topHistoryViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ImageGalleryViewModel.class)
    abstract ViewModel bindsImageGalleryViewModel(ImageGalleryViewModel imageGalleryViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel.class)
    abstract ViewModel bindsSearchViewModel(SearchViewModel searchViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(FactoryViewModel factory);
}
