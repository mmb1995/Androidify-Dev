package com.example.android.androidify.api.models;

import android.os.Parcel;
import android.os.Parcelable;

public class TrackWrapper implements Parcelable {
    public Track track;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.track, 0);

    }

    public TrackWrapper(){}

    protected TrackWrapper(Parcel in) {
        this.track = in.readParcelable(Track.class.getClassLoader());
    }

    public static final Creator<TrackWrapper> CREATOR = new Creator<TrackWrapper>() {
        public TrackWrapper createFromParcel(Parcel source) {
            return new TrackWrapper(source);
        }

        public TrackWrapper[] newArray(int size) {
            return new TrackWrapper[size];
        }
    };
}
