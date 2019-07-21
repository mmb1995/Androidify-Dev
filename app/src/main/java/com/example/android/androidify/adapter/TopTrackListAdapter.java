package com.example.android.androidify.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class TopTrackListAdapter extends RecyclerView.Adapter<TopTrackListAdapter.TopTrackViewHolder> {
    private static final String TAG = "TOP_TRACK_ADAPTER";
    private List<Track> mTrackList;
    private Context mContext;
    private final MusicPlaybackClickListener mListener;

    public TopTrackListAdapter(Context context, MusicPlaybackClickListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public TopTrackViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rootView = inflater.inflate(R.layout.item_top_track, viewGroup, false);

        return new TopTrackViewHolder(rootView, mListener);
    }


    @Override
    public void onBindViewHolder(@NonNull TopTrackViewHolder holder, int pos) {
        Track currentTrack = mTrackList.get(pos);
        Log.i(TAG, "" + currentTrack.name);
        holder.mTitleTextView.setText(currentTrack.name);
        List<ArtistSimple> artists = currentTrack.artists;
        AlbumSimple album = currentTrack.album;
        List<Image> images = album.images;
        holder.mArtistTextView.setText(artists.get(0).name);

        Picasso.get()
                .load(images.get(0).url)
                .placeholder(R.color.colorPrimary)
                .into(holder.mCoverArtImageView);
    }

    @Override
    public int getItemCount() {
        return mTrackList != null ? mTrackList.size() : 0;
    }


    public void setItems(List<Track> items) {
        this.mTrackList = items;
        this.notifyDataSetChanged();
    }

    public Track getItemAtPosition(int position) {
        return mTrackList != null ? mTrackList.get(position) : null;
    }


    public class TopTrackViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.cover_art_imageView)
        ImageView mCoverArtImageView;
        @BindView(R.id.title_text_view)
        TextView mTitleTextView;
        @BindView(R.id.artist_text_view)
        TextView mArtistTextView;

        private final MusicPlaybackClickListener mListener;


        public TopTrackViewHolder(@NonNull View itemView, MusicPlaybackClickListener listener) {
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
