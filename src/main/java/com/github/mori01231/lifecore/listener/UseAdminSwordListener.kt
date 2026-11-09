package com.github.mori01231.lifecore.listener

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.util.ItemUtil
import com.github.mori01231.lifecore.util.WebhookUtil
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import java.util.UUID

class UseAdminSwordListener(private val plugin: LifeCore) : Listener {
    private val cooldown = mutableMapOf<UUID, Long>()

    // sendDiscordWebhook with config path "adminSwordNotifyWebhookURL" if player uses an item with > 10000 damage attribute modifier
    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        val item = e.item
        if (ItemUtil.isProbablyAdminSword(item) && !e.player.hasPermission("lifecore.canuseadminsword")) {
            e.isCancelled = true
            notify(e.player, "use", item!!)
        }
    }

    @EventHandler
    fun onPlayerDropItem(e: PlayerDropItemEvent) {
        val item = e.itemDrop.itemStack
        if (ItemUtil.isProbablyAdminSword(item)) {
            e.isCancelled = true
            notify(e.player, "drop", item)
        }
    }

    @EventHandler
    fun onPlayerAttemptPickupItem(e: PlayerAttemptPickupItemEvent) {
        val item = e.item.itemStack
        if (ItemUtil.isProbablyAdminSword(item) && !e.player.hasPermission("lifecore.canuseadminsword")) {
            e.isCancelled = true
            notify(e.player, "pick up", item)
        }
    }

    private fun notify(player: Player, action: String, item: ItemStack) {
        if (checkCooldown(player.uniqueId)) return
        WebhookUtil.sendDiscordWebhook(
            plugin,
            "adminSwordNotifyWebhookURL",
            plugin.config.getString("adminSwordNotifyWebhookUsername"),
            player.name + " tried to " + action + " an item with >= 9999 generic.attack_damage\n" +
                    "Display name: `" + LegacyComponentSerializer.legacySection().serializeOr(item.itemMeta.displayName(), "") + "`\n" +
                    "Location: " + player.location
        )
    }

    /**
     * Checks for cooldown and returns false if player is not on cooldown
     * @param uuid UUID of player
     * @return true if player is on cooldown
     */
    private fun checkCooldown(uuid: UUID): Boolean {
        // cooldown of 2 minutes
        val last = cooldown[uuid]
        if (last != null && System.currentTimeMillis() - last < 120000) {
            return true
        }
        cooldown[uuid] = System.currentTimeMillis()
        return false
    }
}
