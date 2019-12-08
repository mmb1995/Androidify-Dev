package com.example.android.androidify.api.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Map;

public class PublicUser implements Parcelable {
    public String display_name;
    public Map<String, String> external_urls;
    public Followers followers;
    public String href;
    public String id;
    public List<Image> images;
    public String type;
    public String uri;

    public PublicUser() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.display_name);
        dest.writeMap(this.external_urls);
        dest.writeParcelable(this.followers, 0);
        dest.writeString(this.href);
        dest.writeString(this.id);
        dest.writeTypedList(images);
        dest.writeString(this.type);
        dest.writeString(this.uri);
    }

    protected PublicUser(Parcel in) {
        this.display_name = in.readString();
        this.external_urls = in.readHashMap(Map.class.getClassLoader());
        this.followers = in.readParcelable(Followers.class.getClassLoader());
        this.href = in.readString();
        this.id = in.readString();
        this.images = in.createTypedArrayList(Image.CREATOR);
        this.type = in.readString();
        this.uri = in.readString();
    }

    public static final Creator<PublicUser> CREATOR = new Creator<PublicUser>() {
        public PublicUser createFromParcel(Parcel source) {
            return new PublicUser(source);
        }

        public PublicUser[] newArray(int size) {
            return new PublicUser[size];
        }
    };
}
