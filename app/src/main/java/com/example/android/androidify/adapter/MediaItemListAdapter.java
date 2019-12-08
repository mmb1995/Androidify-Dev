package com.example.android.androidify.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.androidify.R;
import com.example.android.androidify.base.BaseRecyclerViewAdapter;
import com.example.android.androidify.base.BaseViewHolder;
import com.example.android.androidify.interfaces.MediaItemClickListener;
import com.example.android.androidify.model.MusicListItem;
import com.example.android.androidify.utils.ImageUtils;
import com.example.android.androidify.view.CircleImageView;
import com.example.android.androidify.view.SquareImageView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import butterknife.BindView;

public class MediaItemListAdapter extends BaseRecyclerViewAdapter<MusicListItem, MediaItemListAdapter.MediaItemListViewHolder> {

    public enum ImageStyle { CIRCLE, SQUARE }

    private ImageStyle mImageStyle;

    public MediaItemListAdapter(Context context, MediaItemClickListener listener, ImageStyle imageStyle) {
        super(context, listener);
        this.mImageStyle = imageStyle;
    }

    @Override
    protected int getItemLayoutId() {
        if (mImageStyle == ImageStyle.CIRCLE) {
            return R.layout.item_artist;
        } else {
            return R.layout.item_square_image;
        }
    }

    @Override
    protected MediaItemListViewHolder createViewHolder(View rootView) {
        if (mImageStyle == ImageStyle.CIRCLE) {
            return new CircleImageViewHolder(rootView, mListener);
        } else {
            return new SquareImageViewHolder(rootView, mListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MediaItemListViewHolder holder, int position) {
        MusicListItem item = mItems.get(position);
        holder.mTitleTextView.setText(item.name);
        holder.mArtistTextView.setText(item.displayInfo);

        if (item.images == null || item.images.size() < 1) {
            // Remove ImageView if the track has no available images
            /*holder.getImageView().setVisibility(View.GONE);*/
        } else if (mImageStyle == ImageStyle.CIRCLE) {
            CircleImageView circleImageView = (CircleImageView) holder.getImageView();
            String imageUrl = ImageUtils.getImageUrl(item.images, ImageUtils.LARGE);
            ImageUtils.displayImage(circleImageView, imageUrl);
        } else {
            SquareImageView squareImageView = (SquareImageView) holder.getImageView();
            String imageUrl = ImageUtils.getImageUrl(item.images, ImageUtils.LARGE);
            ImageUtils.displayImage(squareImageView, imageUrl);
        }
    }

    public void addItems(List<MusicListItem> itemList) {
        if (mItems == null) {
            setItems(itemList);
        } else {
            mItems.addAll(itemList);
        }
    }

    public abstract class MediaItemListViewHolder<IV extends AppCompatImageView> extends BaseViewHolder {

        @BindView(R.id.track_title_text_view)
        TextView mTitleTextView;

        @BindView(R.id.track_artist_text_view)
        TextView mArtistTextView;

        @BindView(R.id.track_context_menu)
        ImageButton mContextMenuButton;

        public MediaItemListViewHolder(@NonNull View itemView, MediaItemClickListener listener) {
            super(itemView, listener);
            mContextMenuButton.setOnClickListener(this);
        }

        protected abstract IV getImageView();
    }

    public class CircleImageViewHolder extends MediaItemListViewHolder<CircleImageView> {
        @BindView(R.id.track_art_image_view)
        CircleImageView mCoverArtImageView;

        public CircleImageViewHolder(@NonNull View itemView, MediaItemClickListener listener) {
            super(itemView, listener);
        }

        @Override
        protected CircleImageView getImageView() {
            return mCoverArtImageView;
        }
    }

    public class SquareImageViewHolder extends MediaItemListViewHolder<SquareImageView> {
        @BindView(R.id.track_art_image_view)
        SquareImageView mCoverArtImageView;

        public SquareImageViewHolder(@NonNull View itemView, MediaItemClickListener listener) {
            super(itemView, listener);
        }

        @Override
        protected SquareImageView getImageView() {
            return mCoverArtImageView;
        }
    }
}
