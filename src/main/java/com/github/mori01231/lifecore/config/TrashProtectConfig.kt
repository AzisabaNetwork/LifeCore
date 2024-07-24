package com.github.mori01231.lifecore.config

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class TrashProtectConfig(
    val players: MutableMap<String, MutableSet<String>> = mutableMapOf(),
) {
    /**
     * Returns the mutable set of rarities that the player has enabled drop protection for.
     */
    operator fun get(uuid: UUID): MutableSet<String> =
        players.computeIfAbsent(uuid.toString()) { mutableSetOf() }

    /**
     * Returns true if the player has enabled drop protection for the given rarity.
     */
    fun contains(uuid: UUID, item: String): Boolean {
        return this[uuid].contains(item)
    }

    /**
     * Toggles the drop protection for the given rarity for the given player.
     */
    fun toggle(uuid: UUID, item: String) {
        if (contains(uuid, item)) {
            this[uuid].remove(item)
        } else {
            this[uuid].add(item)
        }
    }
}
