package com.github.mori01231.lifecore.util

import java.util.function.Supplier

class LazyInitValue<T>(private val supplier: Supplier<T>) {
    private var supplied = false
    private var value: T? = null

    @Suppress("UNCHECKED_CAST")
    fun get(): T {
        if (!supplied) {
            value = supplier.get()
            supplied = true
        }
        return value as T
    }
}
