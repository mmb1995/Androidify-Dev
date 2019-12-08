package com.example.android.androidify.base;

import android.view.View;

import com.example.android.androidify.interfaces.MediaItemClickListener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.ButterKnife;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnLongClickListener {

    /*private OnItemClickListener mListener;*/
    private MediaItemClickListener mListener;

    public BaseViewHolder(@NonNull View itemView, MediaItemClickListener listener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.mListener = listener;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onItemClick(v, getAdapterPosition());
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

