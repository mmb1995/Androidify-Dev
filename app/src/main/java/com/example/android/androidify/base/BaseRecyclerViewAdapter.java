package com.example.android.androidify.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.androidify.R;
import com.example.android.androidify.api.models.Image;
import com.example.android.androidify.interfaces.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseRecyclerViewAdapter<T, VH extends BaseViewHolder> extends
        RecyclerView.Adapter<VH> {

    private static final String TAG = "BASE_RV_ADAPTER";
    protected final Context mContext;
    protected final OnItemClickListener mListener;
    protected List<T> mItems;

    public BaseRecyclerViewAdapter(Context context, OnItemClickListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rootView = inflater.inflate(getItemLayoutId(), viewGroup, false);
        return createViewHolder(rootView);
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    public void setItems(List<T> items) {
        this.mItems = items;
        this.notifyDataSetChanged();
    };

    public T getItemAtPosition(int position) {
        if (mItems != null && position > 0 && position < mItems.size()) {
            return mItems.get(position);
        }
        return null;
    }

    protected void displayImage(ImageView imageView, List<Image> images) {
        if (imageView != null) {
            if (images != null && images.size() > 0) {
                String imageUrl = images.get(0).url;
                Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.color.imageLoadingColor)
                        .into(imageView);
            }
        }
    }


    protected abstract int getItemLayoutId();
    protected abstract VH createViewHolder(View rootView);
}
