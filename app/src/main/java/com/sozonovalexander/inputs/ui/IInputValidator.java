package com.sozonovalexander.inputs.ui;

import org.jetbrains.annotations.NotNull;

public interface IInputValidator {
    @NotNull String getError();

    @NotNull Boolean isValid(String item);
}
