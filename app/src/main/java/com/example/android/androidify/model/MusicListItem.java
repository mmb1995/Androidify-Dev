package com.example.android.androidify.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.androidify.api.models.Album;
import com.example.android.androidify.api.models.Artist;
import com.example.android.androidify.api.models.Image;
import com.example.android.androidify.api.models.Playlist;
import com.example.android.androidify.api.models.PlaylistSimple;
import com.example.android.androidify.api.models.Track;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class MusicListItem implements Parcelable {
    public String name;
    public List<Image> images;
    public String id;
    public String uri;
    public String external_url;
    /*public String artistName;*/
    public Integer popularity;
    public String displayInfo;
    public Type type;
    public boolean isLiked;

    public enum Type { ALBUM, ARTIST, PLAYLIST, TRACK }

    public MusicListItem() {}

    public MusicListItem(Track track) {
        this.name = track.name;
        this.id = track.id;
        this.uri = track.uri;
        this.external_url = track.external_urls != null ? track.external_urls.get("spotify"): "";
        this.images = track.album != null ? track.album.images : null;
        /*this.artistName = track.artists.get(0).name;*/
        this.displayInfo = track.artists.get(0).name;
        this.popularity = track.popularity;
        this.type = Type.TRACK;
    }

    public MusicListItem(com.spotify.protocol.types.Track track) {
        this.name = track.name;
        this.uri = track.uri;
    }

    public MusicListItem(Artist artist) {
        this.name = artist.name;
        this.id = artist.id;
        this.images = artist.images;
        this.uri = artist.uri;
        this.external_url = artist.external_urls != null ? artist.external_urls.get("spotify"): "";
        /*this.artistName = null;*/
        this.displayInfo = getArtistDisplayInfo(artist);
        this.popularity = artist.popularity;
        /*this.type = Constants.ARTIST;*/
        this.type = Type.ARTIST;
    }

    public MusicListItem(Album album) {
        this.name = album.name;
        this.id = album.id;
        this.images = album.images;
        this.uri = album.uri;
        this.external_url = album.external_urls != null ? album.external_urls.get("spotify"): "";
        this.displayInfo = album.artists.get(0).name;
       /* this.artistName = album.artists.get(0).name;*/
        this.popularity = album.popularity;
        /*this.type = Constants.ALBUM;*/
        this.type = Type.ALBUM;
    }

    public MusicListItem(PlaylistSimple playlist) {
        this.name = playlist.name;
        this.id = playlist.id;
        this.images = playlist.images;
        this.uri = playlist.uri;
        this.external_url = playlist.external_urls != null ? playlist.external_urls.get("spotify"): "";
        Integer total = playlist.tracks != null ? playlist.tracks.total : -1;
        this.displayInfo = getPlaylistString(total);
        this.type = Type.PLAYLIST;
    }

    public MusicListItem(Playlist playlist) {
        this.name = playlist.name;
        this.id = playlist.id;
        this.images = playlist.images;
        this.uri = playlist.uri;
        this.external_url = playlist.external_urls != null ? playlist.external_urls.get("spotify"): "";
        Integer total = playlist.tracks != null ? playlist.tracks.total : -1;
        this.displayInfo = getPlaylistString(total);
        this.type = Type.PLAYLIST;
    }

    public MusicListItem(MusicListItem item, boolean liked) {
        this.name = item.name;
        this.id = item.id;
        this.images = item.images;
        this.uri = item.uri;
        this.external_url = item.external_url;
        this.displayInfo = item.displayInfo;
        /*this.artistName = item.artistName;*/
        this.popularity = item.popularity;
        /*this.type = Constants.TRACK;*/
        this.type = Type.TRACK;
        this.isLiked = liked;
    }

    private String getArtistDisplayInfo(Artist artist) {
        if (artist.followers != null) {
            int total = artist.followers.total;
            String end = total != 1 ? "Followers" : "Follower";
            return formatNumbers(total) + " " + end;
        } else {
            return getPopularity(artist.popularity);
        }
    }

    private String getPlaylistString(Integer totalSongs) {
        // Grammar edge case for when there is only 1 song
        String endText = totalSongs != 1 ? "songs" : "song";
        return formatNumbers(totalSongs) + " " + endText;
    }

    private String formatNumbers(Integer numberToFormat) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        return numberFormat.format(numberToFormat);
    }

    public String getPopularity() {
        if (this.popularity != null) {
            return "Popularity " + this.popularity;
        } else {
            return "Unknown";
        }
    }

    public String getPopularity(int popularity) {
        if (popularity >= 0) {
            return "Popularity " + popularity;
        } else {
            return "Unknown";
        }
    }

    private Type intToType(int n) {
        switch (n) {
            case 0:
                return Type.ALBUM;
            case 1:
                return Type.ARTIST;
            case 2:
                return Type.PLAYLIST;
            case 3:
                return Type.TRACK;
            default:
                return null;
        }
    }

    private int typeToInt(Type type) {
        switch (type) {
            case ALBUM:
                return 0;
            case ARTIST:
                return 1;
            case PLAYLIST:
                return 2;
            case TRACK:
                return 3;
            default:
                return -1;
        }
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
        dest.writeString(external_url);
        /*dest.writeString(artistName);*/
        dest.writeString(displayInfo);
        int typeAsInt = typeToInt(this.type);
        dest.writeInt(typeAsInt);
        dest.writeInt(this.popularity);
        dest.writeInt(this.isLiked ? 1 : 0);
    }

    protected MusicListItem(Parcel in) {
        this.images = in.createTypedArrayList(Image.CREATOR);
        this.name = in.readString();
        this.id = in.readString();
        this.uri = in.readString();
        this.external_url = in.readString();
        /*this.artistName = in.readString();*/
        this.displayInfo = in.readString();
        int typeAsInt = in.readInt();
        this.type = intToType(typeAsInt);
        this.popularity = in.readInt();
        int booleanInteger = in.readInt();
        this.isLiked = booleanInteger == 1;

    }

    public static final Creator<MusicListItem> CREATOR = new Creator<MusicListItem>() {
        public MusicListItem createFromParcel(Parcel source) {
            return new MusicListItem(source);
        }

        public MusicListItem[] newArray(int size) {
            return new MusicListItem[size];
        }
    };

    public static DiffUtil.ItemCallback<MusicListItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<MusicListItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull MusicListItem oldItem, @NonNull MusicListItem newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull MusicListItem oldItem, @NonNull MusicListItem newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;

        MusicListItem item = (MusicListItem) obj;
        return item.id == this.id;
    }
}
