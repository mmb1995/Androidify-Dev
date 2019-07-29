package com.example.android.androidify.adapter.viewholder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.androidify.R;
import com.example.android.androidify.interfaces.MusicPlaybackClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TopHistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = "TOP_HISTORY_VH";
    @BindView(R.id.artist_image_view)
    public ImageView mCoverArtImageView;
    @BindView(R.id.artist_name_text_view)
    public TextView mTitleTextView;

    private final MusicPlaybackClickListener mListener;

    public TopHistoryViewHolder(@NonNull View itemView, MusicPlaybackClickListener listener) {
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
