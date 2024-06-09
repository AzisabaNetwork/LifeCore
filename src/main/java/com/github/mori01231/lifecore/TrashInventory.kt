package com.github.mori01231.lifecore

import com.github.mori01231.lifecore.util.ItemUtil
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

class TrashInventory : InventoryHolder {
    private val inventory = Bukkit.createInventory(this, 54, "ゴミ箱")

    init {
        val item = ItemUtil.createItemStack(Material.BARRIER, 1) {
            it.itemMeta = it.itemMeta.apply {
                setDisplayName("ゴミを捨てる")
            }
        }
        inventory.setItem(53, item)
    }

    override fun getInventory(): Inventory {
        return inventory
    }
}
