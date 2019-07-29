package com.example.android.androidify.api.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Error implements Parcelable {
    public Integer status;
    public String message;

    public Error() {};

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    protected Error(Parcel in) {

    }

    public static final Parcelable.Creator<Error> CREATOR = new Parcelable.Creator<Error>() {
        public Error createFromParcel(Parcel source) {
            return new Error(source);
        }

        public Error[] newArray(int size) {
            return new Error[size];
        }
    };
}
