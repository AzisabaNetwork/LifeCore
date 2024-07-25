package com.github.mori01231.lifecore.data

class TagReference(private val dataLoader: DataLoader, val name: String) : TagValue {
    val values by lazy {
        val tag = dataLoader.findTag(name) ?: error("missing tag $name")
        tag.values.flatMap(TagValue::resolve).toSet()
    }

    override fun resolve(): Set<String> = values
}
