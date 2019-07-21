package com.example.android.androidify.model;

import com.example.android.androidify.api.models.Artist;
import com.example.android.androidify.api.models.ArtistSimple;
import com.example.android.androidify.api.models.Image;
import com.example.android.androidify.api.models.Track;

import java.util.List;

public class TopHistory {
    public Type type;
    public String name;
    public List<Image> images;
    public String uri;
    public List<ArtistSimple> artists;

    public TopHistory(Track track) {
        this.name = track.name;
        this.images = track.album.images;
        this.uri = track.uri;
        this.artists = track.artists;
        this.type = Type.TRACK;
    }

    public TopHistory(Artist artist) {
        this.name = artist.name;
        this.images = artist.images;
        this.uri = artist.uri;
        this.type = Type.ARTIST;
    }

    public String getImageUrl(boolean largest) {
        if (this.images == null) {
            return null;
        }

        Image image =  largest ? this.images.get(0) : this.images.get(this.images.size() - 1);
        return image.url;
    }

    public enum Type { TRACK, ARTIST }

}
