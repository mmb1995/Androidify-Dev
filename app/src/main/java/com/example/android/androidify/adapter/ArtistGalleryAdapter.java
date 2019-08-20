package com.example.android.androidify.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.androidify.R;
import com.example.android.androidify.api.models.Image;
import com.example.android.androidify.interfaces.MusicPlaybackClickListener;
import com.example.android.androidify.model.MusicListItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ArtistGalleryAdapter extends RecyclerView.Adapter<ArtistGalleryAdapter.ArtistGalleryViewHolder> {
    private static final String TAG = "ARTIST_GALLERY_ADAPTER";

    private List<MusicListItem> mArtistItems;
    private Context mContext;
    private final MusicPlaybackClickListener mListener;

    public ArtistGalleryAdapter(Context context, MusicPlaybackClickListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ArtistGalleryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rootView = inflater.inflate(R.layout.item_artist_gallery, viewGroup, false);
        return new ArtistGalleryViewHolder(rootView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistGalleryViewHolder holder, int position) {
        MusicListItem item = mArtistItems.get(position);
        holder.mArtistScrimTextView.setText(item.name);

        Image image = item.images.get(0);

        Picasso.get()
                .load(image.url)
                .placeholder(R.color.imageLoadingColor)
                .into(holder.mArtistCoverImageView);
    }

    @Override
    public int getItemCount() {
        return mArtistItems != null ? mArtistItems.size() : 0;
    }

    public void setItems(List<MusicListItem> items) {
        this.mArtistItems = items;
        this.notifyDataSetChanged();
    }

    public MusicListItem getItemAtPosition(int position) {
        if (mArtistItems != null && position < mArtistItems.size()) {
            return mArtistItems.get(position);
        }

        return null;
    }

    public class ArtistGalleryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.artist_cover_image)
        ImageView mArtistCoverImageView;
        @BindView(R.id.artist_scrim_text_view)
        TextView mArtistScrimTextView;

        private final MusicPlaybackClickListener mListener;

        public ArtistGalleryViewHolder(@NonNull View itemView, MusicPlaybackClickListener listener) {
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
