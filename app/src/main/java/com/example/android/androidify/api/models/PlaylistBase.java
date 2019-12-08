package com.example.android.androidify.api.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class PlaylistBase implements Parcelable {
    public Boolean collaborative;
    public Map<String, String> external_urls;
    public String href;
    public String id;
    public List<Image> images;
    public String name;
    public PublicUser owner;
    @SerializedName("public")
    public Boolean is_public;
    public String snapshot_id;
    public String type;
    public String uri;

    protected PlaylistBase() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected PlaylistBase(Parcel in) {
        this.collaborative = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.external_urls = in.readHashMap(Map.class.getClassLoader());
        this.href = (String) in.readValue(String.class.getClassLoader());
        this.id = (String) in.readValue(String.class.getClassLoader());
        this.images = in.createTypedArrayList(Image.CREATOR);
        this.name = (String) in.readValue(String.class.getClassLoader());
        this.owner = in.readParcelable(PublicUser.class.getClassLoader());
        this.is_public = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.snapshot_id = (String) in.readValue(String.class.getClassLoader());
        this.type = (String) in.readValue(String.class.getClassLoader());
        this.uri = (String) in.readValue(String.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeValue(this.collaborative);
        dest.writeMap(this.external_urls);
        dest.writeValue(this.href);
        dest.writeValue(this.id);
        dest.writeTypedList(this.images);
        dest.writeValue(this.name);
        dest.writeParcelable(owner, i);
        dest.writeValue(is_public);
        dest.writeValue(snapshot_id);
        dest.writeValue(type);
        dest.writeValue(uri);
    }
}
