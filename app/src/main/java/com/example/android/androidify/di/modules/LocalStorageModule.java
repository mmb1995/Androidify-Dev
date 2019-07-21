package com.example.android.androidify.di.modules;

import android.app.Application;

import com.example.android.androidify.repository.LocalStorage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class LocalStorageModule {

    @Provides
    @Singleton
    public LocalStorage provideLocalStorage(Application context) {
        return new LocalStorage(context);
    }
}
