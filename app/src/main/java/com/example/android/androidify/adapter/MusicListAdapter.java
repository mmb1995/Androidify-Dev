package com.example.android.androidify.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.androidify.R;
import com.example.android.androidify.api.models.Image;
import com.example.android.androidify.interfaces.MusicPlaybackClickListener;
import com.example.android.androidify.model.MusicListItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.MusicListViewHolder> {
    private static final String TAG = "MUSIC_ITEM_ADAPTER";

    private List<MusicListItem> mItems;
    private Context mContext;
    private String mType;
    private final MusicPlaybackClickListener mListener;

    public MusicListAdapter(Context context, MusicPlaybackClickListener listener, String type) {
        this.mContext = context;
        this.mListener = listener;
        this.mType = type;
    }

    public MusicListAdapter(Context context, MusicPlaybackClickListener listener, String type,
                            List<MusicListItem> items) {
        this(context, listener, type);
        this.mItems = items;
    }

    @NonNull
    @Override
    public MusicListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rootView = inflater.inflate(R.layout.item_track, viewGroup, false);
        Log.i(TAG, "inflating track list view holder");
        return new MusicListViewHolder(rootView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicListViewHolder holder, int pos) {
        MusicListItem item = mItems.get(pos);
        holder.mTitleTextView.setText(item.name);
        holder.mArtistTextView.setText(item.artistName);

        /**
        if (mType.equals(Constants.TRACK)) {
            String artistName = item.artistName;
            holder.mArtistTextView.setText(artistName);
        } else {
            // Only name text view is needed for artists
            holder.mArtistTextView.setVisibility(View.GONE);
        }
         **/

        // Get Image

        List<Image> images = item.images;

        Picasso.get()
                .load(images.get(0).url)
                .placeholder(R.color.colorPrimary)
                .into(holder.mCoverArtImageView);
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    public MusicListItem getItemAtPosition(int position) {
        if (mItems != null && position < mItems.size()) {
            return mItems.get(position);
        } else {
            return null;
        }
    }

    public void setItems(ArrayList<MusicListItem> items) {
        this.mItems = items;
        this.notifyDataSetChanged();
    }

    public class MusicListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.track_art_image_view)
        ImageView mCoverArtImageView;
        @BindView(R.id.track_title_text_view)
        TextView mTitleTextView;
        @BindView(R.id.track_artist_text_view)
        TextView mArtistTextView;
        @BindView(R.id.track_like_button)
        ImageButton mLikeButton;


        private final MusicPlaybackClickListener mListener;

        public MusicListViewHolder(@NonNull View itemView, MusicPlaybackClickListener listener) {
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
