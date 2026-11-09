package com.github.mori01231.lifecore.listener

import com.github.mori01231.lifecore.util.ItemUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class PreventStorageBoxSellListener : Listener {
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        // Check if the inventory title contains "Menu" and "Sell"
        if (!isSellScreen(event.view)) return
        val clicked = event.clickedInventory ?: return

        // Check if player is moving an item TO the sell screen (clicked inventory is the top inventory)
        if (clicked == event.view.topInventory) {
            val item: ItemStack? = when (event.action) {
                InventoryAction.PLACE_ALL,
                InventoryAction.PLACE_ONE,
                InventoryAction.PLACE_SOME,
                InventoryAction.SWAP_WITH_CURSOR -> event.cursor
                InventoryAction.MOVE_TO_OTHER_INVENTORY -> event.currentItem
                else -> null
            }
            if (item != null && hasStorageBoxType(item)) {
                event.isCancelled = true
                event.whoClicked.sendMessage(Component.text("StorageBoxは売却できません。", NamedTextColor.RED))
            }
        }

        // Handle shift-clicking from player inventory to sell screen
        if (event.isShiftClick && clicked == event.view.bottomInventory) {
            val item = event.currentItem
            if (item != null && hasStorageBoxType(item)) {
                event.isCancelled = true
                event.whoClicked.sendMessage(Component.text("ストレージボックスは売却できません。", NamedTextColor.RED))
            }
        }
    }

    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        // Check if the inventory title contains "Menu" and "Sell"
        if (!isSellScreen(event.view)) return
        // Check if any of the dragged slots are in the top inventory
        val topSize = event.view.topInventory.size
        if (event.rawSlots.any { it < topSize } && hasStorageBoxType(event.oldCursor)) {
            event.isCancelled = true
            event.whoClicked.sendMessage(Component.text("ストレージボックスは売却できません。", NamedTextColor.RED))
        }
    }

    private fun isSellScreen(view: InventoryView): Boolean {
        val title = PlainTextComponentSerializer.plainText().serialize(view.title())
        return title.contains("Menu") && title.contains("Sell")
    }

    private fun hasStorageBoxType(item: ItemStack): Boolean =
        !ItemUtil.getStringTag(item, "storageBoxType").isNullOrEmpty()
}
