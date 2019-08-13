package com.example.android.androidify.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.androidify.R;
import com.example.android.androidify.api.models.Image;
import com.example.android.androidify.interfaces.ListItemClickListener;
import com.example.android.androidify.model.MusicListItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.MusicListViewHolder> {
    private static final String TAG = "MUSIC_ITEM_ADAPTER";

    private List<MusicListItem> mItems;
    private Context mContext;
    private String mType;
    private final ListItemClickListener mListener;

    public MusicListAdapter(Context context, ListItemClickListener listener, String type) {
        this.mContext = context;
        this.mListener = listener;
        this.mType = type;
    }

    public MusicListAdapter(Context context, ListItemClickListener listener, String type,
                            List<MusicListItem> items) {
        this(context, listener, type);
        this.mItems = items;
    }

    @NonNull
    @Override
    public MusicListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rootView = inflater.inflate(R.layout.item_track, viewGroup, false);
        return new MusicListViewHolder(rootView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicListViewHolder holder, int pos) {
        MusicListItem item = mItems.get(pos);
        holder.mTitleTextView.setText(item.name);
        if (item.artistName != null) {
            holder.mArtistTextView.setText(item.artistName);
        } else {
            holder.mArtistTextView.setText("Artist");
        }
        holder.mLikeButton.setSelected(item.isLiked);
        Log.i(TAG, item.name);

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

        if (images != null && images.size() > 0) {
            Picasso.get()
                    .load(images.get(0).url)
                    .placeholder(R.color.imageLoadingColor)
                    .into(holder.mCoverArtImageView);
        }
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

    public void setItems(List<MusicListItem> items) {
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
        @BindView(R.id.track_info)
        LinearLayout mTrackInfo;


        private final ListItemClickListener mListener;

        public MusicListViewHolder(@NonNull View itemView, ListItemClickListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.mListener = listener;
            mTrackInfo.setOnClickListener(this);
            mLikeButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.i(TAG, "clicked id = " + view.getId());
            Log.i(TAG, "like id =  " + R.id.track_like_button);
            if (view.getId() == R.id.track_like_button) {
                mListener.onLikeClicked(getAdapterPosition());
            } else {
                mListener.onItemSelected(getAdapterPosition());
            }
        }
    }
}
