package com.github.mori01231.lifecore.listener

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.TrashInventory
import com.github.mori01231.lifecore.util.ItemUtil
import net.azisaba.itemstash.ItemStash
import net.azisaba.lifepvelevel.util.Util
import net.azisaba.rarity.api.Rarity
import net.azisaba.rarity.api.RarityAPIProvider
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemStack


class TrashListener(private val plugin: LifeCore) : Listener {
    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.clickedInventory?.holder is TrashInventory) {
            if (e.slot == 53) {
                e.isCancelled = true
                e.inventory.setItem(53, null)
                for (i in 0..<e.inventory.size) {
                    val item = e.inventory.getItem(i) ?: continue

                    // Check /protect first
                    val protectKey = ItemUtil.getProtectKey(item)
                    if (protectKey != null && plugin.mythicItemProtectConfig.contains(e.whoClicked.uniqueId, protectKey)) {
                        e.inventory.setItem(i, null)
                        e.whoClicked.inventory.addItem(item).forEach { (_, s) ->
                            ItemStash.getInstance().addItemToStash(e.whoClicked.uniqueId, s)
                            e.whoClicked.sendMessage(Component.text("インベントリがいっぱいのため、Stashに保管されました。", NamedTextColor.RED))
                            e.whoClicked.sendMessage(
                                Component.text("/pickupstash", NamedTextColor.AQUA)
                                    .append(Component.text("で回収できます。", NamedTextColor.RED))
                            )
                        }
                        e.whoClicked.sendMessage(Component.text("アイテム \"$protectKey\" は保護されています。", NamedTextColor.YELLOW))
                        e.whoClicked.sendMessage(
                            Component.text("/protect", NamedTextColor.AQUA)
                                .append(Component.text("で保護設定を変更できます。", NamedTextColor.GOLD))
                        )
                        continue
                    }

                    val rarity: Rarity? = RarityAPIProvider.get().getRarityByItemStack(item)
                    val requiredLevel = Util.getRequiredLevel(item)
                    val shouldCancel =
                        if (plugin.trashProtectConfig.contains(e.whoClicked.uniqueId, "has_pve_level") && requiredLevel > 0) {
                            true
                        } else if (plugin.trashProtectConfig.contains(e.whoClicked.uniqueId, "has_no_pve_level") && requiredLevel == 0L && !ItemUtil.isVanillaItem(item)) {
                            true
                        } else {
                            if (rarity == null) {
                                if (plugin.trashProtectConfig.contains(e.whoClicked.uniqueId, "no_rarity")) {
                                    true
                                } else {
                                    continue
                                }
                            } else {
                                plugin.trashProtectConfig.contains(e.whoClicked.uniqueId, rarity.id)
                            }
                        }
                    if (shouldCancel) {
                        e.inventory.setItem(i, null)
                        e.whoClicked.inventory.addItem(item).forEach { (_, s) ->
                            ItemStash.getInstance().addItemToStash(e.whoClicked.uniqueId, s)
                            e.whoClicked.sendMessage(Component.text("インベントリがいっぱいのため、Stashに保管されました。", NamedTextColor.RED))
                            e.whoClicked.sendMessage(
                                Component.text("/pickupstash", NamedTextColor.AQUA)
                                    .append(Component.text("で回収できます。", NamedTextColor.RED))
                            )
                        }
                    }
                }
                val trashMoneyPerItem = plugin.config.getInt("TrashMoneyPerItem", 0)
                var moneyCounter = 0
                var moneyMultiplier = trashMoneyPerItem
                val items: MutableList<ItemStack> = ArrayList()
                for (item in e.inventory.contents) {
                    item ?: continue
                    try {
                        if (item.amount > 0) {
                            val displayName = item.itemMeta?.displayName()?.let {
                                LegacyComponentSerializer.legacySection().serialize(it)
                            } ?: ""
                            for (line in plugin.config.getStringList("Trash.Items")) {
                                if (displayName == line) {
                                    moneyMultiplier = plugin.config.getInt("TrashMoneyPerSpecialItem")
                                }
                            }

                            moneyCounter += item.amount * moneyMultiplier
                            items.add(item.clone())
                            item.amount = 0
                        }
                    } catch (ignored: Exception) {
                    }
                }
                e.whoClicked.sendMessage(
                    Component.text("ゴミ箱に" + moneyCounter + "個のアイテムを捨てました。", NamedTextColor.DARK_AQUA)
                )
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give ${e.whoClicked.name} $moneyCounter")
                plugin.slF4JLogger.info("Player {} has trashed {} items:", e.whoClicked.name, moneyCounter)
                for (item in items) {
                    plugin.logger.info("  " + ItemUtil.toString(item))
                    e.whoClicked.sendMessage("  " + ItemUtil.toString(item))
                }
                e.clickedInventory?.clear()
                e.whoClicked.closeInventory()
            }
        }
    }
    @EventHandler
    fun onInventoryDrag(e: InventoryDragEvent) {
        if (e.inventory.holder is TrashInventory) {
            e.isCancelled = true
        }
    }
    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        if (e.inventory.holder is TrashInventory) {
            val inv = e.inventory
            for (slot in 0..<inv.size) {
                val item = inv.getItem(slot) ?: continue
                if (item.type.isAir || item == TrashInventory.trashItem) continue
                e.player.inventory.addItem(item).forEach { (_, s) ->
                    ItemStash.getInstance().addItemToStash(e.player.uniqueId, s)
                    e.player.sendMessage(Component.text("インベントリがいっぱいのため、Stashに保管されました。", NamedTextColor.RED))
                    e.player.sendMessage(
                        Component.text("/pickupstash", NamedTextColor.AQUA)
                            .append(Component.text("で回収できます。", NamedTextColor.RED))
                    )
                }
            }
        }
    }
}
