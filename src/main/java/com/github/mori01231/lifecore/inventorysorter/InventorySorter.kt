package com.github.mori01231.lifecore.inventorysorter

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import java.util.UUID

/**
 * 旧 InventorySorter プラグイン。
 * インベントリの範囲外をダブルクリックすると、インベントリ内のアイテムを整理する。
 * `/inventorysorter`(`/is`) コマンドで有効/無効を切り替えられる。
 */
class InventorySorterListener(private val plugin: Plugin) : Listener {

    private val clicked = HashMap<UUID, Long>()

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.clickedInventory != null) return
        val top = event.view.topInventory
        val bottom = event.view.bottomInventory
        val player = event.whoClicked as Player

        val isTopSortable = top.type == InventoryType.CHEST ||
            top.type == InventoryType.ENDER_CHEST ||
            top.type == InventoryType.SHULKER_BOX ||
            top.type == InventoryType.BARREL
        if (!isTopSortable && bottom.type != InventoryType.PLAYER) return

        if (!InventorySorterSettings.isEnabled(plugin, player)) return

        if (bottom.storageContents.any { it != null && it.amount > it.maxStackSize }) return

        val now = System.currentTimeMillis()
        val last = clicked[player.uniqueId]
        if (last != null && last + 200 > now) {
            if (top.type == InventoryType.BARREL ||
                top.type == InventoryType.SHULKER_BOX ||
                top.type == InventoryType.ENDER_CHEST ||
                (top.type == InventoryType.CHEST && !top.javaClass.name.contains("Custom"))
            ) {
                sort(top, 0, top.size)
            } else {
                sort(bottom, 9, 36)
            }
            player.playSound(player.location, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1f, 2f)
            player.updateInventory()
        } else {
            clicked[player.uniqueId] = now
        }
    }

    private fun sort(inventory: Inventory, start: Int, end: Int): Boolean {
        val items = inventory.storageContents
            .drop(start)
            .take(end - start)
            .filter { it != null && it.type != Material.AIR }
            .map { it!! }
        val optimized = optimizeInventory(items)
        for (i in start until end) {
            inventory.setItem(i, null)
        }
        val sorted = optimized.sortedWith(
            compareBy(
                { if (it.maxStackSize == 1) 0 else 1 },
                { if (it.hasDisplayName()) 0 else 1 },
                { it.type.name },
                { it.customModelData() },
                { it.displayNameOrNull() ?: "" },
            ),
        )
        for (i in start until minOf(end, start + sorted.size)) {
            inventory.setItem(i, sorted[i - start])
        }
        return true
    }

    private fun optimizeInventory(items: List<ItemStack>): List<ItemStack> {
        val totals = LinkedHashMap<ItemStack, Int>()
        for (item in items) {
            if (item.type == Material.AIR) continue
            val key = item.clone().apply { amount = 1 }
            totals[key] = (totals[key] ?: 0) + item.amount
        }
        val result = ArrayList<ItemStack>()
        for ((key, total) in totals) {
            // max_stack_size コンポーネントを考慮するため ItemStack 側の上限を使う
            val max = key.maxStackSize
            var done = 0
            while (done < total) {
                val stack = key.clone()
                stack.amount = minOf(max, total - done)
                result.add(stack)
                done += max
            }
        }
        return result
    }

    private fun ItemStack.hasDisplayName(): Boolean {
        val meta = itemMeta ?: return false
        return meta.hasDisplayName()
    }

    private fun ItemStack.displayNameOrNull(): String? {
        val meta = itemMeta ?: return null
        return if (meta.hasDisplayName()) meta.displayName else null
    }

    private fun ItemStack.customModelData(): Int {
        val meta = itemMeta ?: return 0
        return if (meta.hasCustomModelData()) meta.customModelData else 0
    }
}

class InventorySorterCommand(private val plugin: Plugin) : org.bukkit.command.CommandExecutor {
    override fun onCommand(
        sender: org.bukkit.command.CommandSender,
        command: org.bukkit.command.Command,
        label: String,
        args: Array<out String>,
    ): Boolean {
        if (sender !is Player) return false
        if (InventorySorterSettings.isEnabled(plugin, sender)) {
            InventorySorterSettings.setEnabled(plugin, sender, false)
            sender.sendMessage("${ChatColor.RED}ソートを無効にしました")
        } else {
            InventorySorterSettings.setEnabled(plugin, sender, true)
            sender.sendMessage("${ChatColor.GREEN}ソートを有効にしました")
        }
        return true
    }
}

object InventorySorterSettings {
    private val LEGACY_KEY = NamespacedKey("inventorysorter", "sort")

    private fun key(plugin: Plugin) = NamespacedKey(plugin, "sort")

    fun isEnabled(plugin: Plugin, player: Player): Boolean {
        val pdc = player.persistentDataContainer
        val key = key(plugin)
        val value = pdc.get(key, PersistentDataType.BYTE)
            ?: pdc.get(LEGACY_KEY, PersistentDataType.BYTE)?.also {
                pdc.set(key, PersistentDataType.BYTE, it)
                pdc.remove(LEGACY_KEY)
            }
        return value != null && value.toInt() != 0
    }

    fun setEnabled(plugin: Plugin, player: Player, enabled: Boolean) {
        player.persistentDataContainer.set(key(plugin), PersistentDataType.BYTE, (if (enabled) 1 else 0).toByte())
        player.persistentDataContainer.remove(LEGACY_KEY)
    }
}
