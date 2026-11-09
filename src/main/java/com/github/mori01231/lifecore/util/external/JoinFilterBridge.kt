package com.github.mori01231.lifecore.util.external

import net.azisaba.joinfilter.JoinFilter
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID

object JoinFilterBridge {
    fun addPlayerWithExpire(uuid: UUID, server: String, expireSeconds: Long) {
        JavaPlugin.getPlugin(JoinFilter::class.java).addPlayerWithExpire(uuid, server, expireSeconds)
    }
}
