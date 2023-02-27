package com.github.mori01231.lifecore.util

import com.charleskorn.kaml.YamlList
import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlNode
import com.charleskorn.kaml.YamlNull
import com.charleskorn.kaml.YamlPath
import com.charleskorn.kaml.YamlScalar
import org.bukkit.configuration.ConfigurationSection
import java.util.Properties

fun <T> T?.nonNull(message: String? = null) = this ?: throw NullPointerException(message)

fun Any?.toYamlNode(): YamlNode =
    when (this) {
        is ConfigurationSection -> this.toReadonlyNode()
        is Map<*, *> -> YamlMap(this.map { (key, value) -> YamlScalar(key.toString(), YamlPath.root) to value.toYamlNode() }.toMap(), YamlPath.root)
        is List<*> -> YamlList(this.map { it.toYamlNode() }, YamlPath.root)
        null -> YamlNull(YamlPath.root)
        else -> YamlScalar(this.toString(), YamlPath.root)
    }

fun Map<*, *>.toProperties(): Properties = Properties().apply {
    this@toProperties.forEach { (k, v) -> this.setProperty(k.toString(), v.toString()) }
}
