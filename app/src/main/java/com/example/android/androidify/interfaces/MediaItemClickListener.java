package com.example.android.androidify.interfaces;

import android.view.View;

public interface MediaItemClickListener {
    void onItemClick(View v, int position);

    void openContextMenu(View v, int position);
}
