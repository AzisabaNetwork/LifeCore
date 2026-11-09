package com.github.mori01231.lifecore.util

open class LRUCache<K, V>(protected var maxElements: Int) : LinkedHashMap<K, V>(maxElements, 0.75f, true) {
    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean = size > maxElements
}
