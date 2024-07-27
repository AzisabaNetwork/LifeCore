package com.github.mori01231.lifecore

import com.github.mori01231.lifecore.util.ItemUtil
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class TrashInventory : InventoryHolder {
    companion object {
        val trashItem = ItemUtil.createItemStack(Material.BARRIER, 1) {
            it.itemMeta = it.itemMeta.apply {
                setDisplayName("${ChatColor.RED}ゴミを捨てる")
            }
        }
    }

    private val inventory = Bukkit.createInventory(this, 54, "ゴミ箱")

    init {
        inventory.setItem(53, trashItem)
    }

    override fun getInventory(): Inventory {
        return inventory
    }
}
