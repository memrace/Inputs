package com.sozonovalexander.view.api;

import org.jetbrains.annotations.NotNull;

public interface IInputDisplay<T> {
    @NotNull String display(@NotNull T item);
}
