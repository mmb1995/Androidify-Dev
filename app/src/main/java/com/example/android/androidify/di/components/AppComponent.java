package com.example.android.androidify.di.components;

import com.example.android.androidify.SpotifyApplication;
import com.example.android.androidify.di.modules.ActivityModule;
import com.example.android.androidify.di.modules.AppModule;
import com.example.android.androidify.di.modules.FragmentModule;
import com.example.android.androidify.di.modules.ViewModelModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        AppModule.class,
        ActivityModule.class,
        FragmentModule.class,
        ViewModelModule.class
})
public interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(SpotifyApplication application);
        Builder appModule(AppModule appModule);
        AppComponent build();
    }

    void inject(SpotifyApplication application);
}
