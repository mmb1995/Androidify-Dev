package com.example.android.androidify.model;

public class Action {
    public final Type type;
    private final Boolean inLibrary;

    private Action(Type type, Boolean inLibrary) {
        this.type = type;
        this.inLibrary = inLibrary;
    }

    public static Action check(Boolean contained) {
        return new Action(Type.CHECK, contained);
    }

    public static Action save() {
        return new Action(Type.SAVE, null);
    }

    public static Action remove() {
        return new Action(Type.REMOVE, null);
    }

    public enum Type { CHECK, SAVE, REMOVE}
}
