package com.example.android.androidify.model;

import androidx.lifecycle.Observer;

public class EventObserver<T> implements Observer<Event<T>> {
    public OnEventChanged onEventChanged;

    public EventObserver(OnEventChanged<T> onEventChanged) {
        this.onEventChanged = onEventChanged;
    }

    @Override
    public void onChanged(Event<T> tEvent) {
        if (tEvent != null) {
            T data = tEvent.getContentIfNotHandled();
            if (data != null && onEventChanged != null) {
                onEventChanged.onHandleEvent(data);
            }
        }
    }

    public interface OnEventChanged<T> {
        void onHandleEvent(T data);
    }
}
