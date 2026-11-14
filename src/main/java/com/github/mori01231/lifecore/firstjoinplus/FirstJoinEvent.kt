package com.github.mori01231.lifecore.firstjoinplus

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerJoinEvent

class FirstJoinEvent(val joinEvent: PlayerJoinEvent) : Event() {

    val player: Player
        get() = joinEvent.player

    fun setFirstJoinMessage(message: String) {
        joinEvent.setJoinMessage(message)
    }

    val firstJoinLocation: Location
        get() {
            if (FirstJoinPlusIntegration.getInstance().manager.config.getBoolean("on-first-join.teleport.enabled")) {
                return FirstJoinPlusUtilities.getFirstJoinLocation()
            }
            return joinEvent.player.location
        }

    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }

    override fun getHandlers(): HandlerList = handlerList
}
