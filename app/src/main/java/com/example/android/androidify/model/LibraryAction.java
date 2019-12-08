package com.example.android.androidify.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LibraryAction {
    @NonNull
    public final Operation operation;
    @NonNull
    public final Type type;
    @Nullable
    public final String itemId;

    private LibraryAction(@Nullable String id, @NonNull Type type, @NonNull Operation operation) {
        this.itemId = id;
        this.type = type;
        this.operation = operation;
    }

    public static LibraryAction checkArtist() {
        return new LibraryAction(null, Type.ARTIST, Operation.CHECK);
    }

    public static LibraryAction checkAlbum() {
        return new LibraryAction(null, Type.ALBUM, Operation.CHECK);
    }

    public static LibraryAction addArtistToLibrary(String id) {
        return new LibraryAction(id, Type.ARTIST, Operation.SAVE);
    }

    public static LibraryAction addAlbumToLibrary(String id) {
        return new LibraryAction(id, Type.ALBUM, Operation.SAVE);
    }

    public static LibraryAction removeArtistFromLibrary(String id) {
        return new LibraryAction(id, Type.ARTIST, Operation.REMOVE);
    }

    public static LibraryAction removeAlbumFromLibrary(String id) {
        return new LibraryAction(id, Type.ALBUM, Operation.REMOVE);
    }

    public enum Operation { CHECK, SAVE, REMOVE }
    public enum Type { ARTIST, ALBUM, TRACK }
}
