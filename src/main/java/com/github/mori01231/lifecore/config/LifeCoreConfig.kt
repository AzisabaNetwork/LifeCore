package com.github.mori01231.lifecore.config

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.region.CuboidRegion
import com.github.mori01231.lifecore.util.YAML.yaml
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

@Serializable
data class LifeCoreConfig(
    var lobbyRegion: CuboidRegion? = null,
) {
    companion object {
        private const val NAME = "config-core.yml"

        fun load(plugin: LifeCore): LifeCoreConfig {
            plugin.dataFolder.resolve(NAME).let {
                if (!it.exists()) {
                    it.writeText(yaml.encodeToString(LifeCoreConfig()))
                }
                return yaml.decodeFromString(it.readText())
            }
        }
    }

    fun save(plugin: LifeCore) {
        plugin.dataFolder.resolve(NAME).writeText(yaml.encodeToString(this))
    }
}
