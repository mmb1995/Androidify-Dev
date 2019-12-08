package com.example.android.androidify.api.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ArtistCursorPager implements Parcelable {
    public CursorPager<Artist> artists;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.artists, 0);
    }

    public ArtistCursorPager() {
    }

    protected ArtistCursorPager(Parcel in) {
        this.artists = in.readParcelable(Pager.class.getClassLoader());
    }

    public static final Creator<ArtistCursorPager> CREATOR = new Creator<ArtistCursorPager>() {
        public ArtistCursorPager createFromParcel(Parcel source) {
            return new ArtistCursorPager(source);
        }

        public ArtistCursorPager[] newArray(int size) {
            return new ArtistCursorPager[size];
        }
    };
}
