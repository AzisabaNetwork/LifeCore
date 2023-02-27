package com.github.mori01231.lifecore.util

import com.charleskorn.kaml.YamlMap
import org.bukkit.configuration.ConfigurationSection

fun ConfigurationSection.toReadonlyNode(): YamlMap = getValues(true).toYamlNode() as YamlMap

fun ConfigurationSection.getYamlMap(key: String) =
    getConfigurationSection(key)?.toReadonlyNode() ?: throw NullPointerException(key)
