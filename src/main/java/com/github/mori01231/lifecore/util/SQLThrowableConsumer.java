package com.github.mori01231.lifecore.util;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public interface SQLThrowableConsumer<T> {
    void accept(@NotNull T t) throws SQLException;
}
