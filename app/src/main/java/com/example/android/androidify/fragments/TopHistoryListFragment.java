package com.example.android.androidify.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.androidify.R;
import com.example.android.androidify.adapter.ArtistListAdapter;
import com.example.android.androidify.adapter.TopTrackListAdapter;
import com.example.android.androidify.api.models.Artist;
import com.example.android.androidify.api.models.Track;
import com.example.android.androidify.interfaces.MusicPlaybackClickListener;
import com.example.android.androidify.viewmodel.FactoryViewModel;
import com.example.android.androidify.viewmodel.MusicPlaybackViewModel;
import com.example.android.androidify.viewmodel.UserHistoryViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;


public class TopHistoryListFragment extends Fragment implements MusicPlaybackClickListener {
    private static final String TAG = "TOP_HISTORY_FRAG";

    private static final String ARG_LIST_TITLE = "arg_list_title";

    public static final String RECENTLY_PLAYED = "Recently Played";
    public static final String TOP_TRACKS = "Top Tracks";
    public static final String TOP_ARTISTS = "Top Artists";

    @BindView(R.id.rv_music_cards)
    RecyclerView mMusicRecylcerView;
    @BindView(R.id.heading_text_view)
    TextView mHeadingTextView;
    @BindView(R.id.top_history_progress_spinner)
    ProgressBar mProgressBar;

    @Inject
    FactoryViewModel mFactoryViewModel;

    private TopTrackListAdapter mTrackAdapter;
    private ArtistListAdapter mArtistAdapter;
    private UserHistoryViewModel mViewModel;
    private MusicPlaybackViewModel mMusicPlaybackViewModel;
    private String mTitle;

    onTopHistorySelectedListener mCallback;


    public interface onTopHistorySelectedListener {
        void onTopHistorySelected(String id);
    }

    public TopHistoryListFragment() {
        // Required empty public constructor
    }

    public static TopHistoryListFragment newInstance(String title) {
        TopHistoryListFragment fragment = new TopHistoryListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LIST_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (onTopHistorySelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement listener");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.mTitle = getArguments().getString(ARG_LIST_TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_scrolling_music, container, false);
        ButterKnife.bind(this, rootView);
        setUpMusicList();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AndroidSupportInjection.inject(this);
        mMusicPlaybackViewModel = ViewModelProviders.of(getActivity()).get(MusicPlaybackViewModel.class);
        configureViewModel();
    }

    private void setUpMusicList() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false );
        mMusicRecylcerView.setLayoutManager(manager);
    }

   private void configureViewModel() {
        this.mViewModel = ViewModelProviders.of(this, mFactoryViewModel).get(UserHistoryViewModel.class);

        // Set the relevant adapter
       if (mTitle.equals(TOP_ARTISTS)) {
           this.mArtistAdapter = new ArtistListAdapter(getContext(), this);
           this.mMusicRecylcerView.setAdapter(mArtistAdapter);
       } else {
           this.mTrackAdapter = new TopTrackListAdapter(getContext(), this);
           this.mMusicRecylcerView.setAdapter(mTrackAdapter);
       }


        switch (mTitle) {
            case RECENTLY_PLAYED:
                mViewModel.getRecentlyPlayed().observe(this, response -> {
                    if (response != null) {
                        mProgressBar.setVisibility(View.GONE);
                        mHeadingTextView.setText(this.mTitle);
                        this.mTrackAdapter.setItems(response);
                    }
                });
                break;
            case TOP_TRACKS:
                mViewModel.getTopTracks().observe(this, response -> {
                    if (response != null) {
                        mProgressBar.setVisibility(View.GONE);
                        mHeadingTextView.setText(this.mTitle);
                        this.mTrackAdapter.setItems(response);
                    }
                });
                break;
            case TOP_ARTISTS:
                mViewModel.getTopArtists().observe(this, response -> {
                    if (response != null) {
                        mProgressBar.setVisibility(View.GONE);
                        mHeadingTextView.setText(this.mTitle);
                        this.mArtistAdapter.setItems(response);
                    }
                });
                break;
            default:
                break;
        }
   }

    @Override
    public void onItemClicked(int position) {
        String uri;
        if (mTitle.equals(TOP_ARTISTS)) {
            uri = "";
            Artist selectedArtist = this.mArtistAdapter.getItemAtPosition(position);
            mCallback.onTopHistorySelected(selectedArtist.id);
            /**
            Artist selectedArtist = this.mArtistAdapter.getItemAtPosition(position);
            Log.i(TAG, "name = " + selectedArtist.name + " uri = " + selectedArtist.uri);
            uri = selectedArtist.uri;
             **/
        } else {
            Track selectedTrack = this.mTrackAdapter.getItemAtPosition(position);
            uri = selectedTrack.uri;
        }
        mMusicPlaybackViewModel.setCurrentlyPlaying(uri);
    }
}
