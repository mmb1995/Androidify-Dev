package com.example.android.androidify.fragments.Details;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.androidify.R;
import com.example.android.androidify.view.CustomViewPager;
import com.example.android.androidify.viewmodel.FactoryViewModel;
import com.example.android.androidify.viewmodel.MainActivityViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.tabs.TabLayout;

import javax.inject.Inject;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseDetailsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "BASE_DETAILS_FRAG";

    private MainActivityViewModel mMainViewModel;

    public static final String ARG_ID = "id";

    @BindView(R.id.details_backdrop_image)
    ImageView mBackdropImage;

    @BindView(R.id.details_header_text_view)
    TextView mHeaderTextView;

    @BindView(R.id.details_info_text_view)
    TextView mMetaTextView;

    @BindView(R.id.details_favorite_button)
    ImageButton mFavoriteButton;

    @BindView(R.id.details_view_pager)
    CustomViewPager mViewPager;

    @BindView(R.id.details_tabs)
    TabLayout mTabLayout;

    @BindView(R.id.details_play_button)
    FloatingActionButton mPlayButton;

    @Inject
    public FactoryViewModel mFactoryViewModel;

    protected String mId;


    public BaseDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "creating fragment");
        if (getArguments() != null) {
            mMainViewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
            this.mId = getArguments().getString(ARG_ID);
            initializeViewModel();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_base_details, container, false);
        ButterKnife.bind(this, rootView);
        MaterialShapeDrawable materialShapeDrawable = MaterialShapeDrawable.createWithElevationOverlay(getContext(),
                2);
        mViewPager.setBackground(materialShapeDrawable);
        mViewPager.setElevation(materialShapeDrawable.getElevation());
        mPlayButton.setOnClickListener(this);
        mFavoriteButton.setOnClickListener(this);
        setupUi();
        return rootView;
    }

    /**
     * Tells MainActivity to start playback for the given uri
     * @param uri string uri representing a playable item on Spotify
     */
    public void startPlayback(String uri) {
        if (uri != null && uri.length() > 0) {
            mMainViewModel.setCurrentlyPlaying(uri);
        }
    }

    /**
     * Displays snackbar message in MainActivity
     * @param message the message to display in the snackbar
     */
    protected void createSnackbarMessage(String message) {
        if (message != null) {
            mMainViewModel.setSnackBarMessage(message);
        }
    }

    protected abstract void initializeViewModel();
    protected abstract void setupUi();

}
