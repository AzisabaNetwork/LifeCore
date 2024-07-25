package com.github.mori01231.lifecore.data

data class StringTagValue(val value: String) : TagValue {
    override fun resolve(): Set<String> = setOf(value)
}
