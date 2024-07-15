package com.github.mori01231.lifecore.gui

import com.github.mori01231.lifecore.config.DamageLogFile
import com.github.mori01231.lifecore.config.DropNotifyFile
import com.github.mori01231.lifecore.config.PetClickFile
import com.github.mori01231.lifecore.util.ItemUtil
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import kotlin.math.floor
import kotlin.math.min

class CommandListScreen(val player: Player) : InventoryHolder {
    companion object {
        val commands = listOf(
            CommandInfo(CommandType.Useful, "Flyオン(10分) / オフ", listOf("Flyがオフの場合は10分間オンにします。", "オンの場合は残りの秒数を計算して返金→オフにします。"), "/fly") {
                if (it.allowFlight) {
                    CommandInfo.State.Enabled
                } else {
                    CommandInfo.State.Disabled
                }
            },
            CommandInfo(CommandType.Toggle, "建築可否", listOf("建築をできるようにするかどうかを設定します。誤クリック防止などに..."), "/togglebuild"),
            CommandInfo(CommandType.Toggle, "CoreProtectログ表示", listOf("オンにすると、ブロックなどをクリックすると", "ログが表示されるようになります。"), "/co i"),
            CommandInfo(CommandType.Toggle, "CoreProtectExtensionログ表示", listOf("オンにすると、チェストなどを右クリックすると", "ログが表示されるようになります。"), "/cpe i"),
            CommandInfo(CommandType.Toggle, "アイテム自動整理", listOf("アイテム自動整理機能をトグルできます。", "オンの状態でインベントリやチェストを開いた状態で、", "枠外をダブルクリックすると", "アイテムを自動で整理できます。"), "/is"),
            CommandInfo(CommandType.Toggle, "Stash通知", listOf("アイテムがStashに入ったことを知らせる", "メッセージを表示するかの切り替えができます。"), "/stashnotify"),
            CommandInfo(CommandType.Toggle, "Stash無条件追加", listOf("インベントリが埋まってなくても", "ドロップアイテムが常にStashに入るようにします"), "/mlstash"),
            CommandInfo(CommandType.Toggle, "ダメージログ", listOf("受けた・与えたダメージの表示可否を切り替えられます。"), "/damagelog") {
                if (DamageLogFile.isEnabled(it.uniqueId)) {
                    CommandInfo.State.Enabled
                } else {
                    CommandInfo.State.Disabled
                }
            },
            CommandInfo(CommandType.Toggle, "ドロップ保護", listOf("レア度別でアイテムをドロップできるようにするかを", "切り替えられます。"), "/dropprotect"),
            CommandInfo(CommandType.Toggle, "ドロップ通知", listOf("アイテムをドロップしたときに通知を表示します。"), "/dropnotify") {
                if (DropNotifyFile.contains(it.uniqueId)) {
                    CommandInfo.State.Enabled
                } else {
                    CommandInfo.State.Disabled
                }
            },
            CommandInfo(CommandType.Toggle, "ドロップログ消去", listOf("ドロップログを表示するかどうかを設定します。"), "/mls"),
            CommandInfo(CommandType.Toggle, "ペットクリック無効", listOf("ペットに対する右クリックを無効にするかを設定します。"), "/petclick", permission = "lifecore.petclick") {
                if (PetClickFile.isDisabled(it.uniqueId)) {
                    CommandInfo.State.Enabled
                } else {
                    CommandInfo.State.Disabled
                }
            },
            CommandInfo(CommandType.Utility, "Stash受け取り", listOf("Stashの中身を受け取るGUIを開きます。"), "/pickupstash"),
            CommandInfo(CommandType.Misc, "アンパンチ", listOf(), "アンパーンチ！！！！！！！！！！！！！！１１", true)
        )
        val maxPage = floor(commands.size / 45.0).toInt()
    }

    private val inv = Bukkit.createInventory(this, 54, "コマンド一覧")
    private val commandsInCurrentPage = mutableListOf<CommandInfo>()
    private var page = 0
    private var filter = CommandType.All

    init {
        resetItems()
    }

    override fun getInventory(): Inventory = inv

    fun resetItems() {
        inv.clear()
        commandsInCurrentPage.clear()
        val filtered =
            commands
                .filter { filter == CommandType.All || it.type == filter }
                .filter { it.permission == null || player.hasPermission(it.permission) }
        val fromIndex = page * 45
        val toIndex = min((page + 1) * 45, filtered.size)
        commandsInCurrentPage.addAll(filtered.subList(fromIndex, toIndex))
        commandsInCurrentPage.forEachIndexed { index, info ->
            val state = info.stateGetter(player)
            val type = when (state) {
                CommandInfo.State.Enabled -> Material.LIME_DYE
                CommandInfo.State.Disabled -> Material.GRAY_DYE
                CommandInfo.State.Unknown -> Material.NETHER_STAR
            }
            val color = when (state) {
                CommandInfo.State.Enabled -> ChatColor.GREEN
                CommandInfo.State.Disabled -> ChatColor.RED
                CommandInfo.State.Unknown -> ChatColor.YELLOW
            }
            inv.setItem(index, ItemUtil.createItemStack(type, "$color${info.name}", info.lore.map { "${ChatColor.LIGHT_PURPLE}$it" }))
        }
        inv.setItem(45, ItemUtil.createItemStack(Material.ARROW, "${if (page == 0) "§7" else "§a"}<< $page", listOf("§e前のページ")))
        inv.setItem(49, ItemUtil.createItemStack(Material.BARRIER, "§c閉じる", listOf()))
        val filterLore = CommandType.entries.map {
            val color = if (it == filter) {
                ChatColor.YELLOW
            } else {
                ChatColor.GRAY
            }
            " ${ChatColor.WHITE}- $color${it.label}"
        }
        inv.setItem(50, ItemUtil.createItemStack(Material.REDSTONE_TORCH, "${ChatColor.GREEN}フィルター", filterLore))
        inv.setItem(53, ItemUtil.createItemStack(Material.ARROW, "${if (page < maxPage) "§a" else "§7"}${page + 2} >>", listOf("§e次のページ")))
    }

    enum class CommandType(val label: String) {
        All("すべて"),
        Useful("便利系"), // /isがトグルじゃなかったらここに入るはずだった
        Toggle("トグル"), // /togglebuild, /co i, /isなど
        Utility("ユーティリティ"), // /pickupstashなど
        Misc("その他"), // どのカテゴリにも属さないもの
    }

    data class CommandInfo(
        val type: CommandType,
        val name: String,
        val lore: List<String>,
        val command: String,
        val closeInventory: Boolean = false,
        val permission: String? = null,
        val stateGetter: (player: Player) -> State = { State.Unknown },
    ) {
        init {
            if (type == CommandType.All) {
                error("CommandType.All is invalid")
            }
        }

        enum class State {
            Enabled,
            Disabled,
            Unknown,
        }
    }

    class EventListener : Listener {
        @EventHandler
        fun onInventoryDrag(e: InventoryDragEvent) {
            if (e.inventory.holder is CommandListScreen) {
                e.isCancelled = true
            }
        }

        @EventHandler
        fun onInventoryClick(e: InventoryClickEvent) {
            if (e.inventory.holder !is CommandListScreen) return
            e.isCancelled = true
            if (e.clickedInventory?.holder !is CommandListScreen) return
            val screen = e.inventory.holder as CommandListScreen
            if (e.slot < 45) {
                screen.commandsInCurrentPage.getOrNull(e.slot)?.let { info ->
                    screen.player.chat(info.command)
                    if (info.closeInventory) {
                        screen.player.closeInventory()
                    }
                }
            }
            if (e.slot == 45 && screen.page > 0) {
                screen.page--
            }
            if (e.slot == 49) {
                e.whoClicked.closeInventory()
                return
            }
            if (e.slot == 50) {
                var newIndex = if (e.isRightClick) {
                    screen.filter.ordinal - 1
                } else {
                    screen.filter.ordinal + 1
                }
                if (newIndex < 0) {
                    newIndex += CommandType.entries.size
                }
                screen.filter = CommandType.entries[newIndex % CommandType.entries.size]
            }
            if (e.slot == 53 && screen.page < maxPage) {
                screen.page++
            }
            screen.resetItems()
        }
    }
}
