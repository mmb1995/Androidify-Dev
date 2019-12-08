package com.example.android.androidify.model;

public class NavigationEvent<T>{
    private int destination;
    private T data;

    private NavigationEvent(int destination, T data) {
        this.destination = destination;
        this.data = data;
    }

    public static NavigationEvent navigateTo(int destination) {
        return new NavigationEvent(destination, null);
    }

    public static <T>NavigationEvent<T> navigateTo(int destination, T content) {
        return new NavigationEvent(destination, content);
    }

    public int getDestination() {
        return destination;
    }

    public T getData() {
        return data;
    }
}
