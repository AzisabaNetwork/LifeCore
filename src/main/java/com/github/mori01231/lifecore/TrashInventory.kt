package com.github.mori01231.lifecore

import com.github.mori01231.lifecore.util.ItemUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class TrashInventory : InventoryHolder {
    companion object {
        val trashItem = ItemUtil.createItemStack(Material.BARRIER, 1) {
            it.itemMeta = it.itemMeta.apply {
                displayName(Component.text("ゴミを捨てる", NamedTextColor.RED))
            }
        }
    }

    private val inventory = Bukkit.createInventory(this, 54, Component.text("ゴミ箱"))

    init {
        inventory.setItem(53, trashItem)
    }

    override fun getInventory(): Inventory {
        return inventory
    }
}
