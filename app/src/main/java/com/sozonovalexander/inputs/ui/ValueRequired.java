package com.sozonovalexander.inputs.ui;

import org.jetbrains.annotations.NotNull;

public class ValueRequired implements IInputValidator {
    @Override
    public @NotNull String getError() {
        return "Не должно быть пустым";
    }

    @Override
    public @NotNull Boolean isValid(String item) {
        return !item.isEmpty();
    }
}
