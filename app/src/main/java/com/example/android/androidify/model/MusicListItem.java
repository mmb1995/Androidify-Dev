package com.example.android.androidify.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.androidify.api.models.Artist;
import com.example.android.androidify.api.models.Image;
import com.example.android.androidify.api.models.Track;
import com.example.android.androidify.utils.Constants;

import java.util.List;

public class MusicListItem implements Parcelable {
    public String name;
    public List<Image> images;
    public String id;
    public String uri;
    public String artistName;
    public String type;
    public boolean isLiked;

    public MusicListItem() {}

    public MusicListItem(Track track) {
        this.name = track.name;
        this.id = track.id;
        this.uri = track.uri;
        this.images = track.album.images;
        this.artistName = track.artists.get(0).name;
        this.type = Constants.TRACK;
    }

    public MusicListItem(Artist artist) {
        this.name = artist.name;
        this.id = artist.id;
        this.images = artist.images;
        this.uri = artist.uri;
        this.artistName = null;
        this.type = Constants.ARTIST;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeTypedList(images);
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(uri);
        dest.writeString(artistName);
    }

    protected MusicListItem(Parcel in) {
        this.images = in.createTypedArrayList(Image.CREATOR);
        this.name = in.readString();
        this.id = in.readString();
        this.uri = in.readString();
        this.artistName = in.readString();
    }

    public static final Creator<MusicListItem> CREATOR = new Creator<MusicListItem>() {
        public MusicListItem createFromParcel(Parcel source) {
            return new MusicListItem(source);
        }

        public MusicListItem[] newArray(int size) {
            return new MusicListItem[size];
        }
    };
}
