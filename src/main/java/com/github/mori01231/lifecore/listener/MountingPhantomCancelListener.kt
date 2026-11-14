package com.github.mori01231.lifecore.listener

import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityMountEvent

/**
 * 旧 MountingPhantomCanceler プラグイン。
 * プレイヤーがファントムに騎乗するのをキャンセルする。
 */
class MountingPhantomCancelListener : Listener {

    @EventHandler
    fun onMount(event: EntityMountEvent) {
        if (event.entity.type == EntityType.PLAYER && event.mount.type == EntityType.PHANTOM) {
            event.isCancelled = true
        }
    }
}
