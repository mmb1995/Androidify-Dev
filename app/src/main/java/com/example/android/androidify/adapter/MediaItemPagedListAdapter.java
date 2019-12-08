package com.example.android.androidify.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.androidify.R;
import com.example.android.androidify.api.models.Image;
import com.example.android.androidify.interfaces.MediaItemClickListener;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.repository.datasource.NetworkState;
import com.example.android.androidify.utils.ImageUtils;
import com.example.android.androidify.view.CircleImageView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MediaItemPagedListAdapter extends PagedListAdapter<MusicListItem, RecyclerView.ViewHolder> {
    private static final String TAG = "PAGED_ITEM_AD";

    private static final int TYPE_CIRCLE = 0;
    private static final int TYPE_SQUARE = 1;
    private static final int TYPE_PROGRESS = 2;

    private Context mContext;
    private MediaItemClickListener mListener;

    private NetworkState networkState;

    private int count;

    public MediaItemPagedListAdapter(Context context, MediaItemClickListener listener) {
        super(MusicListItem.DIFF_CALLBACK);
        this.mContext = context;
        this.mListener = listener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        count++;
        Log.i(TAG, "Creating view holder count = " + count);
        if (viewType == TYPE_PROGRESS) {
            Log.i(TAG, "creating networkstateviewholder");
            View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_network_state, parent, false);
            return new NetworkStateViewHolder(rootView);
        } else if (viewType == TYPE_SQUARE) {
            View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_track, parent, false);
            return new SquareImageViewHolder(rootView, mListener);
        } else {
            View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_artist, parent, false);
            return new CircleImageViewHolder(rootView, mListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MusicListItem item = getItem(position);
        if (holder instanceof NetworkStateViewHolder) {
            ((NetworkStateViewHolder)holder).bindView(networkState);
        } else  if (holder instanceof CircleImageViewHolder) {
            ((CircleImageViewHolder)holder).bindView(item);
        } else {
            ((SquareImageViewHolder)holder).bindView(item);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return TYPE_PROGRESS;
        }

        MusicListItem item = getItem(position);
        return item.type != MusicListItem.Type.ARTIST ? TYPE_SQUARE : TYPE_CIRCLE;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }


    @Override
    public void submitList(@Nullable PagedList<MusicListItem> pagedList) {
        Log.i(TAG, "submitting list");
        super.submitList(pagedList);
    }

    public MusicListItem getItemAtPosition(int position) {
        return getItem(position);
    }

    public void setNetworkState(NetworkState newNetworkState) {
        /*Log.i(TAG, "network state = " + newNetworkState);*/
        NetworkState previousState = this.networkState;
        boolean previousExtraRow = hasExtraRow();
        this.networkState = newNetworkState;
        boolean newExtraRow = hasExtraRow();
        Log.i(TAG, "has extra row = " + newExtraRow);
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(getItemCount());
            } else {
                notifyItemInserted(getItemCount());
            }
        } else if (newExtraRow && previousState != newNetworkState) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    private boolean hasExtraRow() {
        if (networkState != null && networkState != NetworkState.LOADED) {
            return true;
        } else {
            return false;
        }
    }

    public class CircleImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {

        @BindView(R.id.track_title_text_view)
        TextView mTitleTextView;

        @BindView(R.id.track_artist_text_view)
        TextView mArtistTextView;

        @BindView(R.id.track_context_menu)
        ImageButton mContextMenuButton;

        @BindView(R.id.track_art_image_view)
        CircleImageView mCoverArtImageView;

        private MediaItemClickListener mListener;

        public CircleImageViewHolder(@NonNull View itemView, MediaItemClickListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.mListener = listener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mContextMenuButton.setOnClickListener(this);
        }

        public void bindView(MusicListItem item) {
            mTitleTextView.setText(item.name);
            mArtistTextView.setText(item.displayInfo);

            if (item.images != null) {
                displayImage(item.images);
            }
        }

        public void displayImage(List<Image> images) {
            String imageUrl = ImageUtils.getImageUrl(images, ImageUtils.LARGE);
            ImageUtils.displayImage(mCoverArtImageView, imageUrl);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                if (view.getId() == R.id.track_context_menu) {
                    mListener.openContextMenu(view, getAdapterPosition());
                } else {
                    mListener.onItemClick(view, getAdapterPosition());
                }
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (mListener != null) {
                mListener.openContextMenu(view, getAdapterPosition());
                return true;
            }
            return false;
        }
    }

    public class SquareImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener{

        @BindView(R.id.track_title_text_view)
        TextView mTitleTextView;

        @BindView(R.id.track_artist_text_view)
        TextView mArtistTextView;

        @BindView(R.id.track_context_menu)
        ImageButton mContextMenuButton;

        @BindView(R.id.track_art_image_view)
        ImageView mCoverArtImageView;

        private MediaItemClickListener mListener;

        public SquareImageViewHolder(@NonNull View itemView, MediaItemClickListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.mListener = listener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mContextMenuButton.setOnClickListener(this);
        }

        public void bindView(MusicListItem item) {
            mTitleTextView.setText(item.name);
            mArtistTextView.setText(item.displayInfo);

            if (item.images != null) {
                displayImage(item.images);
            }
        }

        public void displayImage(List<Image> images) {
            String imageUrl = ImageUtils.getImageUrl(images, ImageUtils.LARGE);
            ImageUtils.displayImage(mCoverArtImageView, imageUrl);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                if (view.getId() == R.id.track_context_menu) {
                    mListener.openContextMenu(view, getAdapterPosition());
                } else {
                    mListener.onItemClick(view, getAdapterPosition());
                }
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (mListener != null) {
                mListener.openContextMenu(view, getAdapterPosition());
                return true;
            }
            return false;
        }
    }

    public class NetworkStateViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.network_state_progress_bar)
        ProgressBar mProgressBar;

        public NetworkStateViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindView(NetworkState networkState) {
            if (networkState != null && networkState.getStatus() == NetworkState.Status.RUNNING) {
                mProgressBar.setVisibility(View.VISIBLE);
            } else {
                mProgressBar.setVisibility(View.GONE);
            }
        }
    }

}
