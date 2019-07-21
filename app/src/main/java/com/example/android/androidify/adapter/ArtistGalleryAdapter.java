package com.example.android.androidify.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.androidify.R;
import com.example.android.androidify.api.models.Image;
import com.example.android.androidify.model.MusicListItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArtistGalleryAdapter extends RecyclerView.Adapter<ArtistGalleryAdapter.ArtistGalleryViewHolder> {
    private static final String TAG = "ARTIST_GALLERY_ADAPTER";

    private ArrayList<MusicListItem> mArtistItems;
    private Context mContext;

    public ArtistGalleryAdapter(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public ArtistGalleryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rootView = inflater.inflate(R.layout.item_artist_gallery, viewGroup, false);
        return new ArtistGalleryViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistGalleryViewHolder holder, int position) {
        MusicListItem item = mArtistItems.get(position);
        holder.mArtistScrimTextView.setText(item.name);

        Image image = item.images.get(0);

        Picasso.get()
                .load(image.url)
                .placeholder(R.color.colorPrimary)
                .into(holder.mArtistCoverImageView);
    }

    @Override
    public int getItemCount() {
        return mArtistItems != null ? mArtistItems.size() : 0;
    }

    public void setItems(ArrayList<MusicListItem> items) {
        this.mArtistItems = items;
        this.notifyDataSetChanged();
    }

    public class ArtistGalleryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.artist_cover_image)
        ImageView mArtistCoverImageView;
        @BindView(R.id.artist_scrim_text_view)
        TextView mArtistScrimTextView;

        public ArtistGalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
