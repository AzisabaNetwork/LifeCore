package com.github.mori01231.lifecore.data

data class Tag(val name: String, val values: List<TagValue>) : TagValue {
    override fun resolve(): Set<String> = values.flatMap(TagValue::resolve).toSet()
}
