package com.example.android.androidify.di.modules;

import com.example.android.androidify.SpotifyActivity;
import com.example.android.androidify.activities.MainActivity;
import com.example.android.androidify.activities.SpotifyLoginActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityModule {
    @ContributesAndroidInjector
    abstract SpotifyActivity contributeSpotifyActivity();

    @ContributesAndroidInjector
    abstract SpotifyLoginActivity contributeSpotifyLoginActivity();

    @ContributesAndroidInjector
    abstract MainActivity contributesMainActivity();
}
