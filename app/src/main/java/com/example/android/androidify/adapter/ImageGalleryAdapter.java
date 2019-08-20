package com.example.android.androidify.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.androidify.R;
import com.example.android.androidify.base.BaseRecyclerViewAdapter;
import com.example.android.androidify.base.BaseViewHolder;
import com.example.android.androidify.interfaces.OnItemClickListener;
import com.example.android.androidify.model.MusicListItem;

import androidx.annotation.NonNull;
import butterknife.BindView;

public class ImageGalleryAdapter extends BaseRecyclerViewAdapter<MusicListItem, ImageGalleryAdapter.ImageGalleryViewHolder> {
    private static final String TAG = "IMAGE_GALLERY_ADAPTER";

    public ImageGalleryAdapter(Context context, OnItemClickListener listener) {
        super(context, listener);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_image_gallery;
    }

    @Override
    protected ImageGalleryViewHolder createViewHolder(View rootView) {
        ImageGalleryViewHolder holder = new ImageGalleryViewHolder(rootView, mListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageGalleryViewHolder holder, int position) {
        MusicListItem item = mItems.get(position);
        holder.mScrimTextView.setText(item.name);
        displayImage(holder.mBackdropImage, item.images);
    }

    public class ImageGalleryViewHolder extends BaseViewHolder {
        @BindView(R.id.gallery_backdrop_image)
        ImageView mBackdropImage;

        @BindView(R.id.gallery_scrim_text_view)
        TextView mScrimTextView;

        public ImageGalleryViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView, listener);
        }
    }
}
