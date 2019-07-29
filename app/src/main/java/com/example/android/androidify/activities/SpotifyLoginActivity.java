package com.example.android.androidify.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.android.androidify.BuildConfig;
import com.example.android.androidify.MainActivity;
import com.example.android.androidify.R;
import com.example.android.androidify.config.Keys;
import com.example.android.androidify.repository.LocalStorage;
import com.example.android.androidify.utils.Constants;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

public class SpotifyLoginActivity extends AppCompatActivity implements HasActivityInjector {
    private static final String TAG = "SPOTIFY_LOGIN_ACTIVITY";

    @BindView(R.id.login_button)
    Button mLoginButton;
    @BindView(R.id.logout_button)
    Button mLogoutButton;

    @Inject
    LocalStorage mLocalStorage;

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_login);
        ButterKnife.bind(this);
        AndroidInjection.inject(this);
        String accessToken = mLocalStorage.getAccessToken();
        if (accessToken != null) {
            Log.i(TAG, accessToken);
        }
        mLoginButton.setOnClickListener((View v) -> {
            this.authenticateWithSpotify();
        });
        mLogoutButton.setOnClickListener((View v) -> {
            this.logOut();
        });
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



    private void logOut() {
        Log.i(TAG, "logout");
        /**
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(Keys.CLIENT_ID,
                AuthenticationResponse.Type.TOKEN, Keys.REDIRECT_URI)
                .setShowDialog(true)
                .setScopes(scopes).build();
         **/
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
                    mLocalStorage.setAccessToken(response.getAccessToken());
                    Intent authenticatedIntent = new Intent(SpotifyLoginActivity.this,
                            MainActivity.class);
                    authenticatedIntent.putExtra(Constants.AUTH_TOKEN_INTENT, response.getAccessToken());
                    startActivity(authenticatedIntent);
                    removeLogin();
                    break;
                case ERROR:
                    Log.e(TAG, "There was an error");
                    Log.e(TAG, response.getError());
                    mLocalStorage.setAccessToken(null);
                    break;
                default:
                    break;
            }
        }
    }

    public void removeLogin() {
        SpotifyLoginActivity.this.finish();
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