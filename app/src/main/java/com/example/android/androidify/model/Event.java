package com.example.android.androidify.model;

public class Event<T> {
    private T content;

    private Boolean hasBeenHandled = false;

    public Event(T content) {
        this.content = content;
    }

    /**
     * Returns the content and prevents its use again
     */
    public T getContentIfNotHandled() {
        if (hasBeenHandled) {
            return null;
        } else {
            hasBeenHandled = true;
            return content;
        }
    }

    /**
     * Returns the content, event if its already been handled
     * @return
     */
    public T peekContent() {
        return content;
    }
}
