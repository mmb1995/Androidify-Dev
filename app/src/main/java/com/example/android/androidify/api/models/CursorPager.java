package com.example.android.androidify.api.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class CursorPager<T> implements Parcelable {
    public String href;
    public List<T> items;
    public int limit;
    public String next;
    public com.example.android.androidify.api.models.Cursor cursors;
    public int total;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(href);
        dest.writeList(items);
        dest.writeInt(limit);
        dest.writeString(next);
        dest.writeParcelable(this.cursors, flags);
        dest.writeInt(total);
    }

    public CursorPager() {
    }

    protected CursorPager(Parcel in) {
        this.href = in.readString();
        this.items = in.readArrayList(ArrayList.class.getClassLoader());
        this.limit = in.readInt();
        this.next = in.readString();
        this.cursors = in.readParcelable(Cursor.class.getClassLoader());
        this.total = in.readInt();
    }

    public static final Creator<CursorPager> CREATOR = new Creator<CursorPager>() {
        public CursorPager createFromParcel(Parcel source) {
            return new CursorPager(source);
        }

        public CursorPager[] newArray(int size) {
            return new CursorPager[size];
        }
    };
}
