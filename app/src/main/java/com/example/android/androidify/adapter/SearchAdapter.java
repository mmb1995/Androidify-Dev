package com.example.android.androidify.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.androidify.R;
import com.example.android.androidify.base.BaseRecyclerViewAdapter;
import com.example.android.androidify.base.BaseViewHolder;
import com.example.android.androidify.interfaces.MediaItemClickListener;
import com.example.android.androidify.model.MusicListItem;

import androidx.annotation.NonNull;
import butterknife.BindView;

public class SearchAdapter extends BaseRecyclerViewAdapter<MusicListItem, SearchAdapter.SearchViewHolder> {

    public SearchAdapter(Context context, MediaItemClickListener listener) {
        super(context, listener);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_search_result;
    }

    @Override
    protected SearchViewHolder createViewHolder(View rootView) {
        return new SearchViewHolder(rootView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        MusicListItem item = mItems.get(position);
        if (item != null) {
            holder.mSearchName.setText(item.name);

            if (item.type == MusicListItem.Type.ARTIST) {
                holder.mSearchSecondaryTextView.setText(item.displayInfo);
            } else {
                holder.mSearchSecondaryTextView.setText(item.displayInfo);
            }

            displayImage(holder.mSearchImage, item.images);
        }
    }

    public class SearchViewHolder extends BaseViewHolder {
        @BindView(R.id.search_image)
        ImageView mSearchImage;

        @BindView(R.id.search_name)
        TextView mSearchName;

        @BindView(R.id.search_result_secondary_text_view)
        TextView mSearchSecondaryTextView;

        public SearchViewHolder(@NonNull View itemView, MediaItemClickListener listener) {
            super(itemView, listener);
        }
    }
}
