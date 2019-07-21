package com.example.android.androidify.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SpotifyApi {
    private String mAccessToken;
    private final SpotifyWebService mSpotifyService;

    private static final String BASE_URL = "https://api.spotify.com/v1";

    private class SpotifyApiInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (mAccessToken != null) {
                request = request.newBuilder()
                        .addHeader("Authorization", "Bearer " + mAccessToken)
                        .build();
            }
            return chain.proceed(request);
        }
    }

    public SpotifyApi() {
        // set up http client
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new SpotifyApiInterceptor());
        OkHttpClient client = httpClient.build();

        // Create Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        mSpotifyService = retrofit.create(SpotifyWebService.class);
    }

    /**
     *
     * @return the SpotifyApi instance
     */
    public SpotifyWebService getWebService() {
        return mSpotifyService;
    }

    public void setAccessToken(String accessToken) {
        this.mAccessToken = accessToken;
    }
}
