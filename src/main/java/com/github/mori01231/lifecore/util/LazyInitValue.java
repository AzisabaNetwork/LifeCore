package com.github.mori01231.lifecore.util;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

public class LazyInitValue<T> {
    private boolean supplied = false;
    private T value;
    private final Supplier<T> supplier;

    public LazyInitValue(@NotNull Supplier<T> supplier) {
        this.supplier = Objects.requireNonNull(supplier, "supplier");
    }

    public T get() {
        if (!supplied) {
            value = supplier.get();
            supplied = true;
        }
        return value;
    }
}
