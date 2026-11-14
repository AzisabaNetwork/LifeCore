package com.github.mori01231.lifecore.firstjoinplus

import com.github.mori01231.lifecore.LifeCore
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

/**
 * 旧 FirstJoinPlus プラグインの設定管理。
 * LifeCore 本体の `config.yml` とは別に `firstjoin.yml` を使用する。
 */
class FirstJoinPlusManager(val plugin: LifeCore) {

    val noPVP = mutableListOf<String>()
    val godMode = mutableListOf<String>()

    private val file: File get() = File(plugin.dataFolder, "firstjoin.yml")

    lateinit var config: YamlConfiguration
        private set

    fun load() {
        if (!file.exists()) {
            // 旧 FirstJoinPlus の設定があれば引き継ぐ
            val legacy = File(plugin.dataFolder.parentFile, "FirstJoinPlus/config.yml")
            if (legacy.exists()) {
                legacy.copyTo(file)
            } else {
                if (!plugin.dataFolder.exists()) plugin.dataFolder.mkdirs()
                plugin.saveResource("firstjoin.yml", false)
            }
        }
        config = YamlConfiguration.loadConfiguration(file)
    }

    fun save() {
        config.save(file)
    }

    fun reload() {
        load()
    }
}
