package com.sozonovalexander.inputs.ui;

import org.jetbrains.annotations.NotNull;

public interface IInputDisplay<T> {
    @NotNull String display(T item);
}
