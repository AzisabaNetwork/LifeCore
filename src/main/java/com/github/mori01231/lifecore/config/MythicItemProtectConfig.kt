package com.github.mori01231.lifecore.config

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class MythicItemProtectConfig(
    val players: MutableMap<String, MutableSet<String>> = mutableMapOf(),
) {
    /**
     * Returns the mutable set of MythicMobs item types that the player has enabled protection for.
     */
    operator fun get(uuid: UUID): MutableSet<String> =
        players.computeIfAbsent(uuid.toString()) { mutableSetOf() }

    /**
     * Returns true if the player has enabled protection for the given MythicMobs item type.
     */
    fun contains(uuid: UUID, mythicType: String): Boolean {
        return this[uuid].contains(mythicType)
    }

    /**
     * Toggles the protection for the given MythicMobs item type for the given player.
     * Returns true if protection was enabled, false if it was disabled.
     */
    fun toggle(uuid: UUID, mythicType: String): Boolean {
        return if (contains(uuid, mythicType)) {
            this[uuid].remove(mythicType)
            false
        } else {
            this[uuid].add(mythicType)
            true
        }
    }
}
