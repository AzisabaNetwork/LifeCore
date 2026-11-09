package com.github.mori01231.lifecore.event

import net.minecraft.world.entity.Entity
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import java.util.UUID

class AsyncPlayerPreInteractEntityEvent(val player: Player, val interactedEntityId: Int) : CallableEvent(true), Cancellable {
    private var cancelled = false

    val interactedEntity: Entity
        get() = requireNotNull((player.world as CraftWorld).handle.getEntity(interactedEntityId)) {
            "entity no longer exists or never existed"
        }

    val entityUniqueID: UUID?
        get() = interactedEntity.bukkitEntity.uniqueId

    override fun isCancelled(): Boolean = cancelled

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

    override fun getHandlers(): HandlerList = HANDLER_LIST

    companion object {
        private val HANDLER_LIST = HandlerList()

        @Suppress("unused") // required for bukkit
        @JvmStatic
        fun getHandlerList(): HandlerList = HANDLER_LIST
    }
}
