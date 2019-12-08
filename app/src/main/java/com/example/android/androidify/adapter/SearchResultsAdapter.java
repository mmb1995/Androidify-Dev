package com.example.android.androidify.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.androidify.R;
import com.example.android.androidify.api.models.Image;
import com.example.android.androidify.interfaces.SearchResultClickListener;
import com.example.android.androidify.model.MusicListItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.SearchResultsViewHolder> {
    private static final String TAG = "SEARCH_RES_AD";

    private final Context mContext;
    private List<MusicListItem> mSearchResults;
    private final SearchResultClickListener mListener;

    public SearchResultsAdapter(Context context, SearchResultClickListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public SearchResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rootView = inflater.inflate(R.layout.item_search_result, parent, false);
        return new SearchResultsViewHolder(rootView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultsViewHolder holder, int position) {
        MusicListItem result = mSearchResults.get(position);
        holder.mSearchName.setText(result.name);
        holder.mSearchType.setText("track");

        List<Image> images = result.images;

        if (images != null && images.size() > 0) {
            if (result.type == MusicListItem.Type.ARTIST) {
                // Make artist search results have circular images
                Log.i(TAG, "making circular image");
                holder.mSearchImage.setBackgroundResource(R.drawable.circular_image);
            }

            Picasso.get()
                    .load(images.get(0).url)
                    .placeholder(R.drawable.ic_music_note_12px)
                    .into(holder.mSearchImage);
        } else {
            // No image available
            Picasso.get()
                    .load(R.drawable.ic_music_note_12px)
                    .into(holder.mSearchImage);
        }
    }

    @Override
    public int getItemCount() {
        return mSearchResults != null ? mSearchResults.size() : 0;
    }

    public MusicListItem getItemAtPosition(int position) {
        if (mSearchResults != null && position < mSearchResults.size()) {
            return mSearchResults.get(position);
        }
        return null;
    }

    public void setSearchResults(List<MusicListItem> results) {
        this.mSearchResults = results;
        this.notifyDataSetChanged();
    }

    public class SearchResultsViewHolder extends RecyclerView.ViewHolder  {

        @BindView(R.id.search_image)
        ImageView mSearchImage;

        @BindView(R.id.search_name)
        TextView mSearchName;

        @BindView(R.id.search_result_secondary_text_view)
        TextView mSearchType;

        private final SearchResultClickListener mListener;

        public SearchResultsViewHolder(@NonNull View itemView, SearchResultClickListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mListener = listener;
            itemView.setOnClickListener((View v) -> {
                mListener.onSearchItemClicked(getAdapterPosition());
            });
        }
    }
}
