package com.example.android.androidify.api.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ArtistTrackWrapper implements Parcelable {
    public ArrayList<Track> tracks;

    public ArtistTrackWrapper() {};

    protected ArtistTrackWrapper(Parcel in) {
        this.tracks = in.createTypedArrayList(Track.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeTypedList(tracks);
    }

    public static final Creator<ArtistTrackWrapper> CREATOR = new Creator<ArtistTrackWrapper>() {
        public ArtistTrackWrapper createFromParcel(Parcel source) {
            return new ArtistTrackWrapper(source);
        }

        public ArtistTrackWrapper[] newArray(int size) {
            return new ArtistTrackWrapper[size];
        }
    };
}
