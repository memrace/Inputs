package com.sozonovalexander.view.api;

import androidx.annotation.Nullable;

public class InputResult<T> {
    @Nullable
    public final T item;
    @Nullable
    public final String value;

    public InputResult(T item, String value) {
        this.item = item;
        this.value = value;
    }
}
