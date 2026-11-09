package com.github.mori01231.lifecore.listener

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.util.ItemUtil
import net.azisaba.itemstash.ItemStash
import net.azisaba.lifepvelevel.util.Util
import net.azisaba.rarity.api.RarityAPIProvider
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.Items
import org.bukkit.craftbukkit.entity.CraftItem
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.inventory.ItemStack

class DropProtectListener(private val plugin: LifeCore) : Listener {
    private val rarityAPI = RarityAPIProvider.get()
    private val itemStash = ItemStash.getInstance()

    @EventHandler(ignoreCancelled = true)
    fun onDrop(e: PlayerDropItemEvent) {
        val itemStack = e.itemDrop.itemStack
        val uuid = e.player.uniqueId

        // Check /protect first
        val protectKey = ItemUtil.getProtectKey(itemStack)
        if (protectKey != null && plugin.mythicItemProtectConfig.contains(uuid, protectKey)) {
            cancelDrop(e, itemStack, "/protect によるアイテム保護 ($protectKey)")
            return
        }

        val config = plugin.dropProtectConfig
        val requiredLevel = Util.getRequiredLevel(itemStack)
        val shouldCancel = when {
            config.contains(uuid, "has_pve_level") && requiredLevel > 0 -> true
            config.contains(uuid, "has_no_pve_level") && requiredLevel == 0L && !ItemUtil.isVanillaItem(itemStack) -> true
            else -> {
                val rarity = rarityAPI.getRarityByItemStack(itemStack)
                if (rarity == null) config.contains(uuid, "no_rarity") else config.contains(uuid, rarity.id)
            }
        }
        if (shouldCancel) {
            cancelDrop(e, itemStack, null)
        }
    }

    private fun cancelDrop(e: PlayerDropItemEvent, stack: ItemStack, reason: String?) {
        e.isCancelled = true
        // set item to air via reflection and data watcher
        e.itemDrop.remove()
        val entityItem = (e.itemDrop as CraftItem).handle as ItemEntity
        val dataItem = DATA_ITEM ?: return
        entityItem.entityData.set(dataItem, net.minecraft.world.item.ItemStack(Items.AIR))
        entityItem.entityData.markDirty(dataItem)

        val player = e.player
        player.sendMessage(Component.text("このアイテムはドロップできません。", NamedTextColor.RED))
        if (reason != null) {
            player.sendMessage(Component.text("理由: $reason", NamedTextColor.YELLOW))
            player.sendMessage(
                Component.text("/protect", NamedTextColor.AQUA)
                    .append(Component.text("でアイテムの保護設定を変更できます。", NamedTextColor.GOLD))
            )
        } else {
            player.sendMessage(
                Component.text("/dropprotect", NamedTextColor.AQUA)
                    .append(Component.text("で設定を変更できます。", NamedTextColor.GOLD))
            )
        }
        player.inventory.addItem(stack).values.forEach { s ->
            itemStash.addItemToStash(player.uniqueId, s)
            player.sendMessage(Component.text("インベントリがいっぱいのため、Stashに保管されました。", NamedTextColor.RED))
            player.sendMessage(
                Component.text("/pickupstash", NamedTextColor.AQUA)
                    .append(Component.text("で回収できます。", NamedTextColor.RED))
            )
        }
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        private val DATA_ITEM: EntityDataAccessor<net.minecraft.world.item.ItemStack>? by lazy {
            try {
                val f = ItemEntity::class.java.getDeclaredField("DATA_ITEM")
                f.isAccessible = true
                f.get(null) as EntityDataAccessor<net.minecraft.world.item.ItemStack>
            } catch (_: ReflectiveOperationException) {
                null
            }
        }
    }
}
