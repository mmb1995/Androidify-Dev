package com.example.android.androidify.di.modules;

import com.example.android.androidify.api.SpotifyWebService;
import com.example.android.androidify.repository.SpotifyRepo;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {

    @Provides
    @Singleton
    SpotifyRepo providesSpotifyRepo(SpotifyWebService service) {
        return new SpotifyRepo(service);
    }


}
