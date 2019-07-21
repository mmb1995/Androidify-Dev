package com.example.android.androidify.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.androidify.R;
import com.example.android.androidify.adapter.viewholder.TopHistoryViewHolder;
import com.example.android.androidify.api.models.Artist;
import com.example.android.androidify.api.models.Image;
import com.example.android.androidify.interfaces.MusicPlaybackClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArtistListAdapter extends RecyclerView.Adapter<TopHistoryViewHolder>  {
    private static final String TAG = "ARTIST_LIST_ADAPTER";

    private List<Artist> mArtists;
    private Context mContext;
    private final MusicPlaybackClickListener mListener;

    public ArtistListAdapter(Context context, MusicPlaybackClickListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public TopHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rootView = inflater.inflate(R.layout.item_top_artist, viewGroup, false);

        return new TopHistoryViewHolder(rootView, mListener);
    }

    @Override
    public void onBindViewHolder(TopHistoryViewHolder holder, int pos)  {
        Artist currentArtist = mArtists.get(pos);
        holder.mTitleTextView.setText(currentArtist.name);
        Image artistImage = currentArtist.images.get(0);


        Picasso.get()
                .load(artistImage.url)
                .placeholder(R.color.colorPrimary)
                .into(holder.mCoverArtImageView);

        /**
        Picasso.get()
                .load(artistImage.url)
                .placeholder(R.color.colorPrimary)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        BitmapDrawable bitmapDrawable = new BitmapDrawable(mContext.getResources(), bitmap);
                        bitmapDrawable.setColorFilter(new PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.OVERLAY));
                        holder.mCoverArtImageView.setImageBitmap(bitmapDrawable.getBitmap());
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
        **/
    }

    @Override
    public int getItemCount() {
        return mArtists != null ? mArtists.size() : 0;
    }

    public void setArtists(List<Artist> artists) {
        mArtists = artists;
        this.notifyDataSetChanged();
    }

    public void setItems(List<Artist> items) {
        this.mArtists = items;
        this.notifyDataSetChanged();
    }

    public Artist getItemAtPosition(int position) {
        return mArtists != null ? mArtists.get(position) : null;
    }
}
