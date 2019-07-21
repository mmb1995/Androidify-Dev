package com.example.android.androidify.api.models;


/**
public class Track implements Parcelable {
    public List<ArtistSimple> artists;
    public Boolean is_playable;
    public LinkedTrack linked_from;
    public long duration_ms;
    public Boolean explicit;
    public Map<String, String> external_urls;
    public String href;
    public String id;
    public String name;
    public String preview_url;
    public int track_number;
    public String type;
    public String uri;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(artists);
        dest.writeValue(this.is_playable);
        dest.writeParcelable(this.linked_from, 0);
        dest.writeLong(this.duration_ms);
        dest.writeValue(this.explicit);
        dest.writeMap(this.external_urls);
        dest.writeString(this.href);
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.preview_url);
        dest.writeInt(this.track_number);
        dest.writeString(this.type);
        dest.writeString(this.uri);
    }

    public Track() {}

    protected Track(Parcel in) {
        this.artists = in.createTypedArrayList(ArtistSimple.CREATOR);
        this.is_playable = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.linked_from = in.readParcelable(LinkedTrack.class.getClassLoader());
        this.duration_ms = in.readLong();
        this.explicit = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.external_urls = in.readHashMap(Map.class.getClassLoader());
        this.href = in.readString();
        this.id = in.readString();
        this.name = in.readString();
        this.preview_url = in.readString();
        this.track_number = in.readInt();
        this.type = in.readString();
        this.uri = in.readString();
    }

    public static final Creator<Track> CREATOR = new Creator<Track>() {
        public Track createFromParcel(Parcel source) {
            return new Track(source);
        }

        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

}
**/

import android.os.Parcel;

import java.util.Map;

public class Track extends TrackSimple {
    public AlbumSimple album;
    public Map<String, String> external_ids;
    public Integer popularity;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.album, 0);
        dest.writeMap(this.external_ids);
        dest.writeValue(this.popularity);
    }

    public Track() {
    }

    protected Track(Parcel in) {
        super(in);
        this.album = in.readParcelable(AlbumSimple.class.getClassLoader());
        this.external_ids = in.readHashMap(Map.class.getClassLoader());
        this.popularity = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<Track> CREATOR = new Creator<Track>() {
        public Track createFromParcel(Parcel source) {
            return new Track(source);
        }

        public Track[] newArray(int size) {
            return new Track[size];
        }
    };
}
