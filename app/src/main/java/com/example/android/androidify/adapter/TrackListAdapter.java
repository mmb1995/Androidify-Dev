package com.example.android.androidify.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.androidify.R;
import com.example.android.androidify.base.BaseRecyclerViewAdapter;
import com.example.android.androidify.base.BaseViewHolder;
import com.example.android.androidify.interfaces.MediaItemClickListener;
import com.example.android.androidify.model.MusicListItem;

import androidx.annotation.NonNull;
import butterknife.BindView;

public class TrackListAdapter extends BaseRecyclerViewAdapter<MusicListItem, TrackListAdapter.TrackListViewHolder> {
    private static final String TAG = "TRACK_LIST_ADAPTER";
    private int count;

    public TrackListAdapter(Context context, MediaItemClickListener listener) {
        super(context, listener);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_track;
    }

    @Override
    protected TrackListViewHolder createViewHolder(View rootView) {
        return new TrackListViewHolder(rootView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackListViewHolder holder, int position) {
        MusicListItem item = mItems.get(position);
        holder.mTitleTextView.setText(item.name);
        count++;
        Log.i(TAG, "onBindCalled name = " + item.name + " count = " + count);
        if (item.displayInfo != null) {
            holder.mArtistTextView.setText(item.displayInfo);
        } else {
            holder.mArtistTextView.setText("Artist");
        }

        /*holder.mLikeButton.setSelected(item.isLiked);*/

        if (item.images == null) {
            // Remove ImageView if the track has no available images
            holder.mCoverArtImageView.setVisibility(View.GONE);
        } else {
            displayImage(holder.mCoverArtImageView, item.images);
        }
    }

    public class TrackListViewHolder extends BaseViewHolder {

        @BindView(R.id.track_art_image_view)
        ImageView mCoverArtImageView;

        @BindView(R.id.track_title_text_view)
        TextView mTitleTextView;

        @BindView(R.id.track_artist_text_view)
        TextView mArtistTextView;

        @BindView(R.id.track_context_menu)
        ImageButton mContextMenuButton;

        public TrackListViewHolder(@NonNull View itemView, MediaItemClickListener listener) {
            super(itemView, listener);
            mContextMenuButton.setOnClickListener(this);
        }
    }

}
