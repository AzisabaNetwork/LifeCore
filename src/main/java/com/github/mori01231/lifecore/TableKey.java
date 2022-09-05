package com.github.mori01231.lifecore;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum TableKey {
    MPDB,
    SpicyAzisaBan,
    ;

    private final String lowercase;

    TableKey() {
        this.lowercase = this.name().toLowerCase(Locale.ROOT);
    }

    @Contract(pure = true)
    @NotNull
    public String lowercase() {
        return lowercase;
    }
}
