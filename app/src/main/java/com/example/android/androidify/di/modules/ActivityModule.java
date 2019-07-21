package com.example.android.androidify.di.modules;

import com.example.android.androidify.MainActivity;
import com.example.android.androidify.activities.SpotifyLoginActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityModule {
    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector
    abstract SpotifyLoginActivity contributeSpotifyLoginActivity();
}
