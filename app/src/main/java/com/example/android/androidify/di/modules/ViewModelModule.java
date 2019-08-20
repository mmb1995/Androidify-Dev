package com.example.android.androidify.di.modules;

import com.example.android.androidify.di.ViewModelKey;
import com.example.android.androidify.viewmodel.AlbumViewModel;
import com.example.android.androidify.viewmodel.ArtistGalleryViewModel;
import com.example.android.androidify.viewmodel.ArtistViewModel;
import com.example.android.androidify.viewmodel.FactoryViewModel;
import com.example.android.androidify.viewmodel.ImageGalleryViewModel;
import com.example.android.androidify.viewmodel.SearchViewModel;
import com.example.android.androidify.viewmodel.TrackListViewModel;
import com.example.android.androidify.viewmodel.UserHistoryViewModel;
import com.example.android.androidify.viewmodel.UserLibraryViewModel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(UserHistoryViewModel.class)
    abstract ViewModel bindUserHistoryViewModel(UserHistoryViewModel userHistoryViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(UserLibraryViewModel.class)
    abstract ViewModel bindUserLibraryViewModel(UserLibraryViewModel userLibraryViewModel);

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
    @ViewModelKey(ArtistGalleryViewModel.class)
    abstract ViewModel bindsArtistGalleryViewModel(ArtistGalleryViewModel artistGalleryViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(TrackListViewModel.class)
    abstract ViewModel bindsTrackListViewModel(TrackListViewModel trackListViewModel);

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
