package com.example.android.androidify.api.models;

import android.os.Parcel;
import android.os.Parcelable;

public class AlbumWrapper implements Parcelable {

    public Album album;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.album, 0);

    }

    public AlbumWrapper(){}

    protected AlbumWrapper(Parcel in) {
        this.album = in.readParcelable(Album.class.getClassLoader());
    }

    public static final Creator<AlbumWrapper> CREATOR = new Creator<AlbumWrapper>() {
        public AlbumWrapper createFromParcel(Parcel source) {
            return new AlbumWrapper(source);
        }

        public AlbumWrapper[] newArray(int size) {
            return new AlbumWrapper[size];
        }
    };
}
