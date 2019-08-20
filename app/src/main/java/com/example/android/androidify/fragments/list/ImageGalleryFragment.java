package com.example.android.androidify.fragments.list;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import com.example.android.androidify.R;
import com.example.android.androidify.adapter.ImageGalleryAdapter;
import com.example.android.androidify.adapter.MaterialSpinnerAdapter;
import com.example.android.androidify.api.ApiResponse;
import com.example.android.androidify.base.BaseFragment;
import com.example.android.androidify.interfaces.OnItemClickListener;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.utils.Constants;
import com.example.android.androidify.viewmodel.ImageGalleryViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class ImageGalleryFragment extends BaseFragment implements OnItemClickListener {
    private static final String TAG = "IMAGE_GALLERY_FRAG";
    private static final String ARG_ID = "arg_id";
    private static final String ARG_TYPE = "arg_type";

    @BindView(R.id.image_gallery_rv)
    RecyclerView mGalleryRv;

    @BindView(R.id.gallery_progress_bar)
    ProgressBar mProgressBar;

    @Nullable
    @BindView(R.id.filled_exposed_dropdown)
    AutoCompleteTextView mDropdown;

    private ImageGalleryViewModel mModel;
    private ImageGalleryAdapter mAdapter;

    private String mId;
    private String mType;

    public static ImageGalleryFragment newInstance(@Nullable String id, @NonNull String type) {
        ImageGalleryFragment fragment = new ImageGalleryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, id);
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mModel = ViewModelProviders.of(this, mFactoryModel).get(ImageGalleryViewModel.class);
            this.mType = getArguments().getString(ARG_TYPE);
            if (!mType.equals(Constants.TOP_ARTISTS)) {
                this.mId = getArguments().getString(ARG_ID);
            }
        }
    }

    @Override
    protected int getLayoutId() {
        if (mType.equals(Constants.TOP_ARTISTS)) {
            return R.layout.fragment_image_gallery_dropdown;
        } else {
            return R.layout.fragment_image_gallery;
        }
    }

    @Override
    protected void setupUi(View rootView) {
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        mGalleryRv.setLayoutManager(manager);
        mGalleryRv.setNestedScrollingEnabled(false);
        mAdapter = new ImageGalleryAdapter(getContext(), this);
        mGalleryRv.setAdapter(mAdapter);
        getData();
    }

    private void getData() {
        switch (mType) {
            case Constants.ALBUM:
                mModel.getAlbums(mId).observe(this, response -> handleResponse(response));
                break;
            case Constants.ARTIST:
                mModel.getRelatedArtists(mId).observe(this, response -> handleResponse(response));
                break;
            case Constants.TOP_ARTISTS:
                mModel.initTopArtists();
                setupDropdown();
                mModel.getTopArtists().observe(this, response -> handleResponse(response));
                break;
        }
    }

    /**
     * Configure dropdown to allow user to select the time range for which they wish to view
     * their most played artists. For other types of ImageGalleryFragment there will be no dropdown.
     */
    private void setupDropdown() {
        if (mType != null && mType.equals(Constants.TOP_ARTISTS) && mDropdown != null) {
            String[] dropdownArray = getResources().getStringArray(R.array.top_history_dropdown_array);

            MaterialSpinnerAdapter<String> adapter = new MaterialSpinnerAdapter<>(
                    getContext(),
                    R.layout.item_dropdown_menu,
                    dropdownArray
            );
            mDropdown.setAdapter(adapter);
            mDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    switch (position) {
                        case 0:
                            mModel.setTimeRange(Constants.LONG_TERM);
                            break;
                        case 1:
                            mModel.setTimeRange(Constants.MEDIUM_TERM);
                            break;
                        case 2:
                            mModel.setTimeRange(Constants.SHORT_TERM);
                            break;
                    }
                    mDropdown.setText(adapter.getItem(position), false);
                }
            });
            mDropdown.setText(adapter.getItem(1), false);
        }
    }

    private void handleResponse(ApiResponse<List<MusicListItem>> response) {
        if (response != null) {
            switch (response.status) {
                case LOADING:
                    mGalleryRv.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    mAdapter.setItems(response.data);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mGalleryRv.setVisibility(View.VISIBLE);
                    break;
                case ERROR:
                    mProgressBar.setVisibility(View.INVISIBLE);
                    showSnackbarMessage(getString(R.string.error_message));
                    break;
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        MusicListItem item = mAdapter.getItemAtPosition(position);
        if (item != null) {
            if (mType.equals(Constants.ALBUM)) {
                navigateToAlbum(item.id);
            } else {
                navigateToArtist(item.id);
            }
        }
    }
}
