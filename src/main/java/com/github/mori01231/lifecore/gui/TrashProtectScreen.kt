package com.github.mori01231.lifecore.gui

import com.github.mori01231.lifecore.LifeCore
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class TrashProtectScreen(private val plugin: LifeCore, private val player: Player) : InventoryHolder {
    private val inventory: Inventory = Bukkit.createInventory(this, 27, Component.text("TrashProtect"))

    init {
        reset()
    }

    fun reset() {
        ProtectSettingsLayout.render(inventory, player) { plugin.trashProtectConfig.contains(player.uniqueId, it) }
    }

    override fun getInventory(): Inventory = inventory

    class EventListener(private val plugin: LifeCore) : Listener {
        @EventHandler
        fun onClick(e: InventoryClickEvent) {
            val screen = e.inventory.getHolder(false) as? TrashProtectScreen ?: return
            e.isCancelled = true
            if (e.clickedInventory?.getHolder(false) !is TrashProtectScreen) return
            if (e.slot == ProtectSettingsLayout.CLOSE_SLOT) {
                screen.player.closeInventory()
                return
            }
            ProtectSettingsLayout.SLOT_KEYS[e.slot]?.let { plugin.trashProtectConfig.toggle(screen.player.uniqueId, it) }
            screen.reset()
        }
    }
}
