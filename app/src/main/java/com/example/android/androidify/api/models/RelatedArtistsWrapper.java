package com.example.android.androidify.api.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class RelatedArtistsWrapper implements Parcelable {
    public ArrayList<Artist> artists;

    public RelatedArtistsWrapper() {};

    protected RelatedArtistsWrapper(Parcel in) {
        this.artists = in.createTypedArrayList(Artist.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeTypedList(artists);
    }

    public static final Creator<RelatedArtistsWrapper> CREATOR = new Creator<RelatedArtistsWrapper>() {
        public RelatedArtistsWrapper createFromParcel(Parcel source) {
            return new RelatedArtistsWrapper(source);
        }

        public RelatedArtistsWrapper[] newArray(int size) {
            return new RelatedArtistsWrapper[size];
        }
    };
}
