package com.example.android.androidify.model;

public class NavigationAction {
    private final Destination destination;
    private final int itemId;

    private NavigationAction(Destination destination, int id) {
        this.destination = destination;
        this.itemId = id;
    }

    public static NavigationAction navigateToArtist(int id) {
        return new NavigationAction(Destination.ARTIST, id);
    }

    public static NavigationAction navigateToAlbum(int id) {
        return new NavigationAction(Destination.ALBUM, id);
    }

    public enum Destination { ALBUM, ARTIST }
}
