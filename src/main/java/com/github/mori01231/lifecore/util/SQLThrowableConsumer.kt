package com.github.mori01231.lifecore.util

import java.sql.SQLException

fun interface SQLThrowableConsumer<T> {
    @Throws(SQLException::class)
    fun accept(t: T)
}
