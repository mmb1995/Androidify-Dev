package com.example.android.androidify.di.modules;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.android.androidify.di.ViewModelKey;
import com.example.android.androidify.viewmodel.ArtistViewModel;
import com.example.android.androidify.viewmodel.FactoryViewModel;
import com.example.android.androidify.viewmodel.UserHistoryViewModel;
import com.example.android.androidify.viewmodel.UserLibraryViewModel;

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
    abstract ViewModelProvider.Factory bindViewModelFactory(FactoryViewModel factory);
}
