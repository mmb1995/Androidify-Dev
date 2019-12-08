package com.example.android.androidify.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import com.example.android.androidify.utils.ImageUtils;
import com.example.android.androidify.utils.MusicColorUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;
import butterknife.BindView;

public class MusicCardGalleryAdapter extends BaseRecyclerViewAdapter<MusicListItem, MusicCardGalleryAdapter.MusicCardGalleryViewHolder> {
    private int count;
    private static final String TAG = "MUSIC_CARD_ADAPTER";

    public MusicCardGalleryAdapter(Context context, MediaItemClickListener listener) {
        super(context, listener);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_music_card;
    }

    @Override
    protected MusicCardGalleryViewHolder createViewHolder(View rootView) {
        return new MusicCardGalleryViewHolder(rootView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicCardGalleryViewHolder holder, int position) {
        MusicListItem item = mItems.get(position);
        count++;
        Log.i(TAG, "onBindCalled = " + count);
        if (item != null) {
            holder.mMusicCardTitleTv.setText(item.name);
            holder.mMusicCardSecondaryTv.setText(item.displayInfo);

/*            if (item.type.equals(Constants.ARTIST)) {
                //holder.mMusicCardSecondaryTv.setVisibility(View.GONE);
                holder.mMusicCardSecondaryTv.setText(item.getPopularity());
            } else {
                holder.mMusicCardSecondaryTv.setText(item.artistName);
            }*/
            displayPaletteImage(ImageUtils.getImageUrl(item.images, ImageUtils.MEDIUM), holder);
        }

    }

    private void displayPaletteImage(String imageUri, MusicCardGalleryViewHolder holder) {
        Picasso.get()
                .load(imageUri)
                .placeholder(R.color.imageLoadingColor)
                .into(holder.mMusicCardImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) holder.mMusicCardImageView.getDrawable()).getBitmap();
                        if (bitmap != null) {
                            Palette palette = MusicColorUtils.generatePalette(bitmap);
                            Palette.Swatch swatch = MusicColorUtils.getSwatch(palette);
                            if (swatch != null) {
                                int swatchColor = swatch.getRgb();
                                holder.mScrimView.setBackgroundColor(swatchColor);
                                holder.mMusicCardTitleTv.setTextColor(swatch.getTitleTextColor());
                                holder.mMusicCardSecondaryTv.setTextColor(swatch.getBodyTextColor());
                                holder.mContextButton.setColorFilter(swatch.getBodyTextColor());
                            }
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
    }

    public class MusicCardGalleryViewHolder extends BaseViewHolder {
        private static final String TAG = "MUSIC_CARD_VH";
        @BindView(R.id.music_card_title_text_view)
        TextView mMusicCardTitleTv;

        @BindView(R.id.music_card_secondary_text_view)
        TextView mMusicCardSecondaryTv;

        @BindView(R.id.music_card_image_view)
        ImageView mMusicCardImageView;

        @BindView(R.id.context_menu_button)
        ImageButton mContextButton;

        @BindView(R.id.music_card_scrim)
        View mScrimView;

        public MusicCardGalleryViewHolder(@NonNull View itemView, MediaItemClickListener listener) {
            super(itemView, listener);
            mContextButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                if (view.getId() == R.id.context_menu_button) {
                    mListener.openContextMenu(view, getAdapterPosition());
                } else {
                    mListener.onItemClick(view, getAdapterPosition());
                }
            }
        }
    }
}
