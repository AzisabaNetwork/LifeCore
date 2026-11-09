package com.github.mori01231.lifecore.util

import com.github.mori01231.lifecore.util.external.JoinFilterBridge
import org.bukkit.Bukkit
import java.util.UUID

object JoinFilterUtil {
    fun addPlayerWithExpire(uuid: UUID, server: String, expireSeconds: Long): Boolean {
        if (Bukkit.getPluginManager().isPluginEnabled("JoinFilter")) {
            JoinFilterBridge.addPlayerWithExpire(uuid, server, expireSeconds)
            return true
        }
        return false
    }
}
