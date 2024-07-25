package com.github.mori01231.lifecore.data

interface TagValue {
    fun resolve(): Set<String>
}
