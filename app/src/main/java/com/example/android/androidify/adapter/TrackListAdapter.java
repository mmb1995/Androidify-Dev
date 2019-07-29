package com.example.android.androidify.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.androidify.R;
import com.example.android.androidify.api.models.AlbumSimple;
import com.example.android.androidify.api.models.ArtistSimple;
import com.example.android.androidify.api.models.Image;
import com.example.android.androidify.api.models.Track;
import com.example.android.androidify.interfaces.MusicPlaybackClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.TrackListViewHolder> {
    private static final String TAG = "TRACK_LIST_ADAPTER";
    private List<Track> mTrackList;
    private Context mContext;

    private final MusicPlaybackClickListener mListener;

    public TrackListAdapter(Context context, MusicPlaybackClickListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    public TrackListAdapter(Context context, MusicPlaybackClickListener listener, List<Track> tracks) {
        this.mContext = context;
        this.mTrackList = tracks;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public TrackListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rootView = inflater.inflate(R.layout.item_track, viewGroup, false);
        Log.i(TAG, "inflating track list view holder");
        return new TrackListViewHolder(rootView, mListener);
    }


    @Override
    public void onBindViewHolder(@NonNull TrackListViewHolder holder, int pos) {
        Track currentTrack = mTrackList.get(pos);
        Log.i(TAG, "" + currentTrack.name);
        holder.mTitleTextView.setText(currentTrack.name);
        List<ArtistSimple> artists = currentTrack.artists;
        AlbumSimple album = currentTrack.album;
        List<Image> images = album.images;
        holder.mArtistTextView.setText(artists.get(0).name);

        Picasso.get()
                .load(images.get(0).url)
                .placeholder(R.color.imageLoadingColor)
                .into(holder.mCoverArtImageView);
    }

    @Override
    public int getItemCount() {
        return mTrackList != null ? mTrackList.size() : 0;
    }

    public void setItems(List<Track> items) {
        if (items != null) {
            Log.i(TAG, items.get(0).name);
        }
        this.mTrackList = items;
        this.notifyDataSetChanged();
    }

    public Track getItemAtPosition(int position) {
        if (mTrackList != null && position < mTrackList.size()) {
            return mTrackList.get(position);
        } else {
            return null;
        }
    }

    public class TrackListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.track_art_image_view)
        ImageView mCoverArtImageView;
        @BindView(R.id.track_title_text_view)
        TextView mTitleTextView;
        @BindView(R.id.track_artist_text_view)
        TextView mArtistTextView;
        @BindView(R.id.track_like_button)
        ImageButton mLikeButton;

        private MusicPlaybackClickListener mListener;




        public TrackListViewHolder(@NonNull View itemView, MusicPlaybackClickListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.mListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onItemClicked(getAdapterPosition());
        }
    }
}
