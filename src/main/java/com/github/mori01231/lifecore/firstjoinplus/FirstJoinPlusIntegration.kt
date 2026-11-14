package com.github.mori01231.lifecore.firstjoinplus

import com.github.mori01231.lifecore.LifeCore

/**
 * 旧 FirstJoinPlus プラグインの統合エントリポイント。
 * LifeCore からリスナー・コマンドの登録を行う。
 */
class FirstJoinPlusIntegration(val plugin: LifeCore) {

    val manager = FirstJoinPlusManager(plugin)

    init {
        instance = this
    }

    fun register() {
        manager.load()
        plugin.server.pluginManager.registerEvents(FirstJoinPlayerListener(this), plugin)
        plugin.server.pluginManager.registerEvents(FirstJoinListener(this), plugin)
        plugin.getCommand("firstjoinplus")?.setExecutor(FirstJoinPlusCommand(this))
        FirstJoinPlusUtilities.copyDefaultFiles(this)
        if (manager.config.getBoolean("on-first-join.give-written-books.enabled")) {
            val rules = java.io.File(plugin.dataFolder, "rules.txt")
            if (!rules.exists()) {
                try {
                    plugin.saveResource("rules.txt", false)
                } catch (_: IllegalArgumentException) {
                    // rules.txt が jar に含まれていない場合はスキップ
                }
            }
        }
    }

    companion object {
        lateinit var instance: FirstJoinPlusIntegration
            private set

        @JvmStatic
        @JvmName("getFirstJoinPlusInstance")
        fun getInstance(): FirstJoinPlusIntegration = instance
    }
}
