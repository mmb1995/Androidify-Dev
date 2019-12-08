package com.example.android.androidify.base;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.androidify.R;
import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.api.models.Image;
import com.example.android.androidify.model.SnackbarMessage;
import com.example.android.androidify.utils.Constants;
import com.example.android.androidify.utils.ImageUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import butterknife.BindView;

public abstract class AbsMediaItemDetailsFragment<VM extends ViewModel, M> extends BaseFragment
        implements View.OnClickListener {

    private static final String TAG = "ABS_DETAILS_FRAG";

    public static final String ARG_ID = "id";

    @BindView(R.id.details_app_bar)
    AppBarLayout mAppBar;

    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbar;

    @BindView(R.id.collapsing_backdrop_image)
    ImageView mBackdropImage;

    @BindView(R.id.details_header_text_view)
    public TextView mHeaderTextView;

    @BindView(R.id.details_info_text_view)
    public TextView mMetaTextView;

    @BindView(R.id.details_context_menu_button)
    ImageButton mContextMenuButton;

    @BindView(R.id.details_play_button)
    FloatingActionButton mPlayButton;

    @BindView(R.id.details_toolbar)
    MaterialToolbar mToolbar;

    protected String mId;
    protected VM mMediaItemDetailsViewModel;
    protected M mDetailsItem;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_base_details_collapsing;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.i(TAG, "creating fragment");
        if (getArguments() != null) {
            this.mId = getArguments().getString(ARG_ID);
            mMediaItemDetailsViewModel = getDetailsViewModel();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(getLayoutId(), container, false);

        ViewStub viewStub = rootView.findViewById(R.id.details_container);
        viewStub.setLayoutResource(getViewStubLayout());
        viewStub.inflate();
        return rootView;
    }

    @Override
    protected void init(View view) {

        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(mCollapsingToolbar, mToolbar, navController, appBarConfiguration);
        setupToolbar();
        mPlayButton.setOnClickListener(this);
        mContextMenuButton.setOnClickListener(this);
        /*mCollapsingToolbar.setStatusBarScrimColor(R.color.scrimBackground);*/

        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean showTitle = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == - 1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    Log.i(TAG, "setting title");
                    /*mCollapsingToolbar.setTitle(getTitle());*/
                    mToolbar.setTitle(getTitle());
                    showTitle = true;
                } else if (showTitle) {
                    /*mCollapsingToolbar.setStatusBarScrimColor(getResources().getColor(R.color.colorSurface));*/
                    /*mCollapsingToolbar.setTitle("");*/
                    mToolbar.setTitle("");
                    showTitle = false;
                }
            }
        });

        initDetailsFragment();
    }

    private void setupToolbar() {
        mToolbar.inflateMenu(R.menu.menu_main_activity);
        mToolbar.setTitle("");
        mToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_open_search) {
                navigateToSearch();
                return true;
            }
            return false;
        });
    }

    public void onDetailsResponse(ApiResponse<M> response) {
        if (response != null) {
            switch (response.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    mDetailsItem = response.data;
                    updateUi();
                    break;
                case UNAUTHORIZED:
                    mMainViewModel.onErrorEvent(Constants.UNAUTHORIZED);
                    break;
                case ERROR:
                    createSnackbarMessage(SnackbarMessage.error());
                    break;
            }
        }
    }

    protected void displayImage(List<Image> images) {
        if (mBackdropImage != null) {
            String imageUrl = ImageUtils.getImageUrl(images, ImageUtils.LARGE);
            if (imageUrl != null) {
                Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.color.imageLoadingColor)
                        .into(mBackdropImage);
            }
        }
    }

/*
    protected void toggleFavoriteButton(boolean favorite, Integer resourceId) {
        mFavoriteButton.setSelected(favorite);
        if (mFavoriteButton.getVisibility() == View.INVISIBLE) {
            mFavoriteButton.setVisibility(View.VISIBLE);
        } else if (resourceId != null){
            createSnackbarMessage(SnackbarMessage.create(resourceId));
        }
    }
*/

    protected void setToolbarTitle(String title) {
        /*mToolbar.setTitle(title);*/
        mCollapsingToolbar.setTitle(title);
    }

    @LayoutRes
    protected abstract int getViewStubLayout();
    protected abstract VM getDetailsViewModel();
    protected abstract void initDetailsFragment();
    protected abstract void updateUi();
    protected abstract String getTitle();
}
