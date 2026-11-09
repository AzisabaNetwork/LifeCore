package com.github.mori01231.lifecore.util

import java.sql.SQLException

fun interface SQLThrowableFunction<T, R> {
    @Throws(SQLException::class)
    fun apply(t: T): R
}
