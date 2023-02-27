package com.github.mori01231.lifecore.util

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.charleskorn.kaml.YamlNode
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.encodeToString

object YAML {
    @JvmStatic
    val yaml = Yaml(
        configuration = YamlConfiguration(
            strictMode = false
        )
    )
}

fun <T : Any> YamlNode.decode(strategy: DeserializationStrategy<T>): T {
    return YAML.yaml.decodeFromYamlNode(strategy, this)
}

inline fun <reified T : Any> T.encode() =
    YAML.yaml.encodeToString(this)
