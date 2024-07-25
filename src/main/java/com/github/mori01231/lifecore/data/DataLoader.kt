package com.github.mori01231.lifecore.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.readText
import kotlin.io.path.relativeTo

class DataLoader(private val logger: Logger, private val dataFolder: Path) {
    val tags: List<Tag> by lazy { loadTags() }

    fun findTag(name: String): Tag? = tags.firstOrNull { it.name == name }

    fun loadTags(): List<Tag> {
        val tags = mutableListOf<Tag>()
        val tagsPath = dataFolder.resolve("tags")
        Files.walk(tagsPath).forEach { path ->
            if (path.isDirectory()) return@forEach
            val tagName =
                path.relativeTo(tagsPath)
                    .toString()
                    .let { it.substring(0, it.length - ".json".length) }
                    .let { it.substring(it.indexOf('/') + 1) }
            logger.info("Loading tag $tagName")
            val values = mutableListOf<TagValue>()
            Json.decodeFromString<RawTag>(path.readText()).values.forEach { tagValue ->
                if (tagValue.startsWith("#")) {
                    values.add(TagReference(this, tagValue.substring(1)))
                } else {
                    values.add(StringTagValue(tagValue))
                }
            }
            tags.add(Tag("minecraft:$tagName", values))
        }
        return tags
    }

    @Serializable
    data class RawTag(val values: List<String>)
}
