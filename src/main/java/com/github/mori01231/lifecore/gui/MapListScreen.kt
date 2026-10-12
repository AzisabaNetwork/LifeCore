package com.github.mori01231.lifecore.gui

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.util.ItemUtil
import com.github.mori01231.lifecore.util.MapDatabaseUtil
import com.github.mori01231.lifecore.util.PromptSign
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.TooltipDisplay
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
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
    private val inventory = Bukkit.createInventory(this, 54, Component.text("マップリスト", NamedTextColor.BLUE))
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
        inventory.setItem(45, createGenericItemStack(Material.ARROW, Component.text("前のページ", NamedTextColor.YELLOW)))
        inventory.setItem(49, ItemUtil.createItemStack(Material.NETHER_STAR, "§9ⓘ このページはなに？", listOf(
            "§f保存したマップの一覧がここに表示されています。",
            "§fそれぞれのマップにカーソルを合わせると詳細が表示されます。",
            "§fインベントリ内のマップでShift+クリックすると",
            "§f名前を入力した後にこの画面にマップが追加されます。",
        )))
        inventory.setItem(53, createGenericItemStack(Material.ARROW, Component.text("次のページ", NamedTextColor.YELLOW)))
    }

    fun createGenericItemStack(material: Material, name: Component) =
        ItemStack(material, 1).apply {
            val meta = itemMeta
            meta.displayName(name)
            itemMeta = meta
        }

    @Suppress("UnstableApiUsage")
    fun createMenuItem(itemData: MapDatabaseUtil.ItemData) =
        ItemStack(Material.FILLED_MAP, 1).apply {
            val meta = itemMeta
            meta.displayName(LegacyComponentSerializer.legacySection().deserialize(itemData.name))
            val lore = mutableListOf<Component>(
                Component.text("アイテム数: ", NamedTextColor.YELLOW)
                    .append(Component.text("${itemData.amount}", if (itemData.amount > 0) NamedTextColor.GREEN else NamedTextColor.RED)),
                Component.empty(),
                Component.text("✦ 左クリックで1個入手 (Shiftで64個)", NamedTextColor.YELLOW),
                Component.text("✎ 右クリックで名前を編集", NamedTextColor.YELLOW)
            )
            if (itemData.amount <= 0L) {
                lore.add(Component.text("✖ Shift+右クリックで削除", NamedTextColor.YELLOW))
            } else {
                lore.add(Component.text("✦ Shift+右クリックですべて取り出す", NamedTextColor.YELLOW))
            }
            meta.lore(lore)
            meta.addItemFlags(ItemFlag.HIDE_DESTROYS)
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
            meta.addItemFlags(ItemFlag.HIDE_PLACED_ON)
            itemMeta = meta
            setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().addHiddenComponents(DataComponentTypes.MAP_ID).build())
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
                    player.sendMessage(
                        Component.text("先に", NamedTextColor.RED)
                            .append(Component.text("/lifecoreutil saveMapData", NamedTextColor.AQUA))
                            .append(Component.text("でマップデータを保存してください", NamedTextColor.RED))
                    )
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
                        player.sendMessage(
                            Component.text("アイテムが足りません。 ", NamedTextColor.RED)
                                .append(Component.text("(${e.message})", NamedTextColor.DARK_GRAY))
                        )
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
                        player.sendMessage(Component.text("インベントリがいっぱいです", NamedTextColor.RED))
                        return
                    }
                    takeItem(1)
                }
                ClickType.SHIFT_LEFT -> {
                    if (player.inventory.firstEmpty() == -1) {
                        player.sendMessage(Component.text("インベントリがいっぱいです", NamedTextColor.RED))
                        return
                    }
                    takeItem(min(64, itemData.amount.toInt()))
                }
                ClickType.RIGHT -> {
                    PromptSign.promptSign(player) { lines ->
                        val name = LegacyComponentSerializer.legacySection().serialize(
                            LegacyComponentSerializer.legacyAmpersand().deserialize(lines.joinToString(""))
                        )
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
                            player.sendMessage(Component.text("インベントリがいっぱいです", NamedTextColor.RED))
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
