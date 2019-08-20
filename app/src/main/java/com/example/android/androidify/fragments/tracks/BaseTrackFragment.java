package com.example.android.androidify.fragments.tracks;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.android.androidify.R;
import com.example.android.androidify.viewmodel.FactoryViewModel;
import com.example.android.androidify.viewmodel.MainActivityViewModel;
import com.example.android.androidify.viewmodel.TrackListViewModel;

import javax.inject.Inject;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import dagger.android.support.AndroidSupportInjection;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseTrackFragment extends Fragment {

    @BindView(R.id.track_progress_bar)
    ProgressBar mTrackProgressBar;

    @BindView(R.id.track_rv)
    RecyclerView mTrackRecyclerView;

    @Inject
    FactoryViewModel mFactoryViewModel;

    protected TrackListViewModel mTrackViewModel;
    protected MainActivityViewModel mMainViewModel;


    public BaseTrackFragment() {
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
        mMainViewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
        mTrackViewModel = ViewModelProviders.of(this, mFactoryViewModel).get(TrackListViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base_track, container, false);
    }

    protected void playTrack(String uri) {
        if (uri != null) {
            mMainViewModel.setCurrentlyPlaying(uri);
        }
    }

}
