package com.sozonovalexander.view.validators;

import com.sozonovalexander.view.api.IInputValidator;

import org.jetbrains.annotations.NotNull;

public final class ValueRequired implements IInputValidator {
    @Override
    public @NotNull String getError() {
        return "Не должно быть пустым";
    }

    @Override
    public @NotNull Boolean isValid(String item) {
        return !item.isEmpty();
    }
}
