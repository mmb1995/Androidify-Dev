package com.example.android.androidify.fragments.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.androidify.R;
import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.utils.ImageUtils;
import com.example.android.androidify.view.CircleImageView;
import com.example.android.androidify.viewmodel.FactoryViewModel;
import com.example.android.androidify.viewmodel.MainActivityViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;

public class MediaItemDialogFragment extends BottomSheetDialogFragment implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "MEDIA_ITEM_DIALOG";

    @BindView(R.id.media_item_context_menu)
    NavigationView mNavView;

    private TextView mTitleTextView;

    private TextView mSecondaryTextView;

    private View mHeaderView;

    protected MainActivityViewModel mMainViewModel;

    @Inject
    protected FactoryViewModel mFactoryModel;

    private MusicListItem mItem;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainViewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
        mMainViewModel.getSelectedDialogItem().observe(this, this::onMediaItemSelected);
        mMainViewModel.getSelectedDialogItemSaveStatus().observe(this, this::handleSaveStatus );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_media_item_modal, container, false);
        ButterKnife.bind(this, rootview);
        mNavView.setNavigationItemSelectedListener(this);
        mNavView.getMenu().findItem(R.id.action_like).setEnabled(false);
        return rootview;
    }

    private void onMediaItemSelected(MusicListItem item) {
        this.mItem = item;

        if (item.type == MusicListItem.Type.ARTIST) {
            mHeaderView = mNavView.inflateHeaderView(R.layout.layout_dialog_header_artist);
            CircleImageView circleImageView = mHeaderView.findViewById(R.id.cover_art_image_view);
            ImageUtils.displayImage(circleImageView, item.images);
        } else {
            mHeaderView = mNavView.inflateHeaderView(R.layout.layout_dialog_header);
            ImageView imageView = mHeaderView.findViewById(R.id.cover_art_image_view);
            ImageUtils.displayImage(imageView, item.images);
        }

        mTitleTextView = mHeaderView.findViewById(R.id.title_text_view);
        mSecondaryTextView = mHeaderView.findViewById(R.id.secondary_text_view);
        mTitleTextView.setText(mItem.name);
        mSecondaryTextView.setText(mItem.displayInfo);

    }

    private void handleSaveStatus(ApiResponse<Boolean> response) {
        if (response != null) {
            switch (response.status) {
                case SUCCESS:
                    MenuItem item = mNavView.getMenu().findItem(R.id.action_like);

                    if (response.data) {
                        item.setIcon(R.drawable.ic_favorite_24px);
                    } else {
                        item.setIcon(R.drawable.ic_favorite_border_24px);
                    }

                    item.setEnabled(true);
                    break;
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_link_to_spotify:
                openContentInSpotify();
                break;
            case R.id.action_like:
                mMainViewModel.toggleMediaItemLibraryStatus(mItem);
                break;
            case R.id.action_share:
                shareItem();
                break;
        }

        super.dismiss();

        return false;
    }

    private void shareItem() {
        if (mItem != null && mItem.external_url != null) {
            Log.i(TAG, "url = " + mItem.external_url);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mItem.external_url);
            startActivity(Intent.createChooser(shareIntent, null));
        }
    }

    /**
     * Uses the media item's spotify uri to open the selected content in the Spotify app
     */
    private void openContentInSpotify() {
        if (mItem != null && mItem.uri != null) {
            Intent spotifyIntent = new Intent(Intent.ACTION_VIEW);
            spotifyIntent.setData(Uri.parse(mItem.uri));
            spotifyIntent.putExtra(Intent.EXTRA_REFERRER,
                    Uri.parse("android-app://" + getContext().getPackageName()));
            startActivity(spotifyIntent);
        }

    }
}
