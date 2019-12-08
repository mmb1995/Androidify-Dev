package com.example.android.androidify.viewmodel;

import com.example.android.androidify.model.MusicListItem;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MediaItemViewModel extends ViewModel {

    private MutableLiveData<MusicListItem> mSelectedMediaItem = new MutableLiveData<MusicListItem>();

    public LiveData<MusicListItem> getSelectedMediaItem() {
        return mSelectedMediaItem;
    }

    public void setSelectedMediaItem(MusicListItem item) {
        mSelectedMediaItem.setValue(item);
    }
}
