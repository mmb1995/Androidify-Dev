package com.example.android.androidify.di.modules;

import android.util.Log;

import com.example.android.androidify.api.SpotifyWebService;
import com.example.android.androidify.repository.LocalStorage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class ApiModule {

    @Provides
    OkHttpClient provideOkhttpClient(LocalStorage storage) {
        String accessToken = storage.getAccessToken();
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        httpClient.addInterceptor((chain) -> {
            //Log.i("INTERCEPTOR ", accessToken);
           // Log.i("INTERCEPTOR", chain.toString());
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer " + accessToken)
                    .method(original.method(), original.body());

            Request request = requestBuilder.build();
            Log.i("INTERCEPTOR", request.headers().toString());

            //Log.i("INTERCEPTOR", request.toString());
            return chain.proceed(request);
        });
        httpClient.addInterceptor(logging);
        //Log.i("INTERCEPTOR", logging.toString());
        //Log.i("INTERCEPTOR", httpClient.toString());
        return httpClient.build();
    }

    @Provides
    Retrofit provideRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl("https://api.spotify.com/v1/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    SpotifyWebService provideSpotifyWebService(Retrofit retrofit) {
        return retrofit.create(SpotifyWebService.class);
    }

}
