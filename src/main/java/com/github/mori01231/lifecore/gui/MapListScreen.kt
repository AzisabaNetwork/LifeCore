package com.github.mori01231.lifecore.gui

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.util.ItemUtil
import com.github.mori01231.lifecore.util.MapDatabaseUtil
import com.github.mori01231.lifecore.util.PromptSign
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID
import kotlin.math.min

class MapListScreen(val player: Player) : InventoryHolder {
    private val inventory = Bukkit.createInventory(this, 54, "${ChatColor.BLUE}マップリスト")
    private val itemsInCurrentPage = mutableListOf<MapDatabaseUtil.ItemData>()
    @set:JvmName("setPage0")
    var page = 0

    override fun getInventory() = inventory

    fun async(callback: () -> Unit) {
        Bukkit.getScheduler().runTaskAsynchronously(JavaPlugin.getPlugin(LifeCore::class.java), Runnable {
            callback()
        })
    }

    fun sync(callback: () -> Unit) {
        Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(LifeCore::class.java), Runnable {
            callback()
        })
    }

    fun resetAsync() {
        async {
            reset()
        }
    }

    fun reset() {
        inventory.clear()
        itemsInCurrentPage.clear()
        val items = MapDatabaseUtil.getAll(player)
        val start = page * 45
        val end = (page + 1) * 45
        for (i in start until end) {
            if (i >= items.size) break
            val item = items[i]
            inventory.setItem(i - start, createMenuItem(item))
            itemsInCurrentPage.add(item)
        }
        inventory.setItem(45, createGenericItemStack(Material.ARROW, "${ChatColor.YELLOW}前のページ"))
        inventory.setItem(49, ItemUtil.createItemStack(Material.NETHER_STAR, "${ChatColor.BLUE}ⓘ このページはなに？", listOf(
            "${ChatColor.WHITE}保存したマップの一覧がここに表示されています。",
            "${ChatColor.WHITE}それぞれのマップにカーソルを合わせると詳細が表示されます。",
            "${ChatColor.WHITE}インベントリ内のマップでShift+クリックすると",
            "${ChatColor.WHITE}名前を入力した後にこの画面にマップが追加されます。",
        )))
        inventory.setItem(53, createGenericItemStack(Material.ARROW, "${ChatColor.YELLOW}次のページ"))
    }

    fun createGenericItemStack(material: Material, name: String) =
        ItemStack(material, 1).apply {
            val meta = itemMeta
            meta.setDisplayName(name)
            itemMeta = meta
        }

    fun createMenuItem(itemData: MapDatabaseUtil.ItemData) =
        ItemStack(Material.FILLED_MAP, 1).apply {
            val meta = itemMeta
            meta.setDisplayName(itemData.name)
            val lore = mutableListOf(
                "${ChatColor.YELLOW}アイテム数: ${if (itemData.amount > 0) ChatColor.GREEN else ChatColor.RED}${itemData.amount}",
                "",
                "${ChatColor.YELLOW}✦ 左クリックで1個入手 (Shiftで64個)",
                "${ChatColor.YELLOW}✎ 右クリックで名前を編集"
            )
            if (itemData.amount <= 0L) {
                lore.add("${ChatColor.YELLOW}✖ Shift+右クリックで削除")
            } else {
                lore.add("${ChatColor.YELLOW}✦ Shift+右クリックですべて取り出す")
            }
            meta.lore = lore
            meta.addItemFlags(ItemFlag.HIDE_DESTROYS)
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
            meta.addItemFlags(ItemFlag.HIDE_PLACED_ON)
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)
            itemMeta = meta
        }

    fun getPreviousPage() = if (page == 0) 0 else page - 1

    fun getNextPage(): Int {
        val items = MapDatabaseUtil.getAll(player)
        val maxPage = (items.size - 1) / 45
        return if (page == maxPage) maxPage else page + 1
    }

    fun setPage(page: Int) {
        this.page = page
        resetAsync()
    }

    fun openAsync() {
        async {
            reset()
            sync {
                player.openInventory(inventory)
            }
        }
    }

    object EventListener : Listener {
        private val processing = mutableSetOf<UUID>()

        @EventHandler
        fun onInventoryClick(e: InventoryClickEvent) {
            val holder = e.inventory.holder
            if (holder !is MapListScreen) return
            e.isCancelled = true
            val player = e.whoClicked as? Player ?: return
            if (processing.contains(player.uniqueId)) return
            if (e.clickedInventory != holder.inventory) {
                val currentItem = e.currentItem
                if (currentItem?.type != Material.FILLED_MAP) return
                if (e.click != ClickType.SHIFT_LEFT && e.click != ClickType.SHIFT_RIGHT) return
                ItemUtil.getByteArrayTag(currentItem, "SerializedMapData")?.let { if (it.isEmpty()) null else true } ?: run {
                    player.closeInventory()
                    player.sendMessage("${ChatColor.RED}先に${ChatColor.AQUA}/lifecoreutil saveMapData${ChatColor.RED}でマップデータを保存してください")
                    return
                }
                e.currentItem = null
                holder.async {
                    MapDatabaseUtil.save(player, currentItem) {
                        holder.openAsync()
                    }
                }
                return
            }
            if (e.slot == 45) {
                holder.async {
                    holder.setPage(holder.getPreviousPage())
                }
                return
            } else if (e.slot == 53) {
                holder.async {
                    holder.setPage(holder.getNextPage())
                }
                return
            }
            val itemData = holder.itemsInCurrentPage.getOrNull(e.slot) ?: return
            fun takeItem(amount: Int) {
                processing.add(player.uniqueId)
                holder.async {
                    val item = try {
                        MapDatabaseUtil.take(player, itemData.data, amount)
                    } catch (e: Exception) {
                        player.sendMessage("${ChatColor.RED}アイテムが足りません。 ${ChatColor.DARK_GRAY}(${e.message})")
                        return@async
                    } finally {
                        processing.remove(player.uniqueId)
                    }
                    holder.sync {
                        player.inventory.addItem(item)
                        holder.resetAsync()
                    }
                }
            }
            when (e.click) {
                ClickType.LEFT -> {
                    if (player.inventory.firstEmpty() == -1) {
                        player.sendMessage("${ChatColor.RED}インベントリがいっぱいです")
                        return
                    }
                    takeItem(1)
                }
                ClickType.SHIFT_LEFT -> {
                    if (player.inventory.firstEmpty() == -1) {
                        player.sendMessage("${ChatColor.RED}インベントリがいっぱいです")
                        return
                    }
                    takeItem(min(64, itemData.amount.toInt()))
                }
                ClickType.RIGHT -> {
                    PromptSign.promptSign(player) { lines ->
                        val name = ChatColor.translateAlternateColorCodes('&', lines.joinToString(""))
                        MapDatabaseUtil.updateName(player, itemData.data, name)
                        holder.openAsync()
                    }
                }
                ClickType.SHIFT_RIGHT -> {
                    if (itemData.amount <= 0) {
                        holder.async {
                            MapDatabaseUtil.delete(player, itemData.data)
                            holder.resetAsync()
                        }
                    } else {
                        val emptySpace = player.inventory.contents.count {
                            @Suppress("SENSELESS_COMPARISON")
                            it == null || it.type == Material.AIR
                        }
                        if (emptySpace == 0) {
                            player.sendMessage("${ChatColor.RED}インベントリがいっぱいです")
                            return
                        }
                        val amount = if (itemData.amount.toInt() < 0) {
                            5000
                        } else {
                            itemData.amount.toInt()
                        }
                        takeItem(min(amount, emptySpace * 64))
                    }
                }
                else -> {}
            }
        }

        @EventHandler
        fun onInventoryDrag(e: InventoryClickEvent) {
            val holder = e.inventory.holder
            if (holder !is MapListScreen) return
            e.isCancelled = true
        }
    }
}
