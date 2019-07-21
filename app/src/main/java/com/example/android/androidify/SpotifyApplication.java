package com.example.android.androidify;

import android.app.Activity;
import android.app.Application;

import com.example.android.androidify.di.components.AppComponent;
import com.example.android.androidify.di.components.DaggerAppComponent;
import com.example.android.androidify.di.modules.AppModule;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

public class SpotifyApplication extends Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        DaggerAppComponent.builder()
                .application(this)
                .appModule(new AppModule(this))
                .build()
                .inject(this);
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }

}
