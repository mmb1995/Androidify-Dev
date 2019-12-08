package com.example.android.androidify.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.android.androidify.BuildConfig;
import com.example.android.androidify.R;
import com.example.android.androidify.api.Session;
import com.example.android.androidify.config.Keys;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

public class SpotifyLoginActivity extends AppCompatActivity implements HasActivityInjector {
    private static final String TAG = "SPOTIFY_LOGIN_ACTIVITY";
    private static final String SPOTIFY_PACKAGE_NAME = "com.spotify.music";

    public static final String EXTRA_UNAUTHORIZED_REDIRECT = "extra_unauthorized_redirect";

    @Inject
    Session mSession;

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_login);
        ButterKnife.bind(this);
        AndroidInjection.inject(this);

        // check if spotify is installed on user's device
        PackageManager pm = getPackageManager();
        boolean isSpotifyInstalled;
        try {
            pm.getPackageInfo(SPOTIFY_PACKAGE_NAME, 0);
            isSpotifyInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.i(TAG, "Spotify not installed on current device");
             isSpotifyInstalled = false;
        }

        if (isSpotifyInstalled) {
            handleUserAuthentication();
        } else {
            handleSpotifyInstallation();
        }
    }

    private void handleUserAuthentication() {
        if (mSession.isTokenValid() && mSession.getToken() != null) {
            /*onAuthSuccess();*/
            onAuthSuccess();
        } else {
            authenticateWithSpotify();
        }
    }

    /**
     * Opens Play Store to allow user to download the Spotify app
     *  **/
    private void handleSpotifyInstallation() {
        final String appPackageName = "com.spotify.music";
        final String referrer = "adjust_campaign=com.example.android.androidify&adjust_tracker=ndjczk&utm_source=adjust_preinstall";

        try {
            Uri uri = Uri.parse("market://details")
                    .buildUpon()
                    .appendQueryParameter("id", appPackageName)
                    .appendQueryParameter("referrer", referrer)
                    .build();
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (android.content.ActivityNotFoundException ignored) {
            Uri uri = Uri.parse("https://play.google.com/store/apps/details")
                    .buildUpon()
                    .appendQueryParameter("id", appPackageName)
                    .appendQueryParameter("referrer", referrer)
                    .build();
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }

    /**
     * Directs User to Spotify's auth flow
     */
    private void authenticateWithSpotify() {
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(BuildConfig.CLIENT_ID, AuthenticationResponse.Type.TOKEN, BuildConfig.REDIRECT_URL);

        builder.setScopes(new String[]{
                "app-remote-control",
                "streaming",
                "playlist-read-private",
                "playlist-modify-public",
                "playlist-modify-private",
                "user-top-read",
                "user-read-private",
                "user-read-recently-played",
                "user-library-read",
                "user-library-modify",
                "user-follow-read",
                "user-follow-modify"
        });
        builder.setShowDialog(true);
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, Keys.REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // check if result comes from the correct activity
        if (requestCode == Keys.REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch(response.getType()) {
                // Response was successful
                case TOKEN:
                    Log.i(TAG, "authentication successful");
                    Log.i(TAG, response.getAccessToken());
                    /*mLocalStorage.setAccessToken(response.getAccessToken());*/
                    mSession.setAccessToken(response.getAccessToken(), response.getExpiresIn(), TimeUnit.SECONDS);
                    onAuthSuccess();
                    break;
                case ERROR:
                    Log.e(TAG, "There was an error");
                    Log.e(TAG, response.getError());
                    /*mLocalStorage.setAccessToken(null);*/
                    break;
                default:
                    break;
            }
        }
    }

    private void onAuthSuccess() {
        Intent authenticatedIntent = new Intent(SpotifyLoginActivity.this,
                MainActivity.class);
        Log.i(TAG, "Authentication successful");
        //authenticatedIntent.putExtra(Constants.AUTH_TOKEN_INTENT, response.getAccessToken());
        startActivity(authenticatedIntent);
        finish();
    }


    public void removeLogin() {
        SpotifyLoginActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
       // Do nothing
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }
}

/**

 "user-top-read",
 "user-library-read",
 "user-read-private"
 **/