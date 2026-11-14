package com.github.mori01231.lifecore.antidrop

import com.github.mori01231.lifecore.LifeCore
import org.bukkit.ChatColor
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.util.StringUtil

/**
 * 旧 AntiDrop プラグイン。
 * 指定ワールドでのアイテムドロップを禁止する。設定は `config.yml` の `antidrop.disabled-worlds`。
 */
class AntiDropListener(private val plugin: LifeCore) : Listener {

    fun disabledWorlds(): MutableList<String> = plugin.config.getStringList("antidrop.disabled-worlds")

    @EventHandler
    fun onPlayerDrop(event: PlayerDropItemEvent) {
        val player = event.player
        val worldName = player.world.name
        if (disabledWorlds().contains(worldName) && !player.hasPermission("lifecore.antidrop.bypass")) {
            event.isCancelled = true
            player.sendMessage("${ChatColor.RED}このワールドではアイテムのドロップが禁止されています")
        }
    }
}

class AntiDropCommand(private val plugin: LifeCore) : CommandExecutor, TabCompleter {

    private fun worlds(): MutableList<String> = plugin.config.getStringList("antidrop.disabled-worlds")

    private fun save() {
        plugin.saveConfig()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val worlds = worlds()
        if (args.isEmpty()) {
            sender.sendMessage("${ChatColor.YELLOW}使用方法: /antidrop [add|remove|list] [ワールド名]")
            return true
        }
        when (args[0].lowercase()) {
            "add" -> {
                if (args.size < 2) {
                    sender.sendMessage("${ChatColor.RED}ワールド名を入力してください")
                    return true
                }
                val worldToAdd = args[1]
                if (worlds.contains(worldToAdd)) {
                    sender.sendMessage("${ChatColor.RED}$worldToAdd は既に登録されています")
                } else {
                    worlds.add(worldToAdd)
                    plugin.config.set("antidrop.disabled-worlds", worlds)
                    save()
                    sender.sendMessage("${ChatColor.GREEN}$worldToAdd をドロップ無効化ワールドに登録しました")
                }
            }
            "remove" -> {
                if (args.size < 2) {
                    sender.sendMessage("${ChatColor.RED}ワールド名を入力してください")
                    return true
                }
                val worldToRemove = args[1]
                if (!worlds.contains(worldToRemove)) {
                    sender.sendMessage("${ChatColor.RED}$worldToRemove は登録されていません")
                } else {
                    worlds.remove(worldToRemove)
                    plugin.config.set("antidrop.disabled-worlds", worlds)
                    save()
                    sender.sendMessage("${ChatColor.GREEN}$worldToRemove をドロップ無効化ワールドから削除しました")
                }
            }
            "list" -> {
                sender.sendMessage("${ChatColor.AQUA}--- ドロップ無効化ワールド一覧 ---")
                if (worlds.isEmpty()) {
                    sender.sendMessage("${ChatColor.GRAY}登録されているワールドはありません")
                } else {
                    for (world in worlds) {
                        sender.sendMessage("${ChatColor.WHITE}- $world")
                    }
                }
            }
            else -> sender.sendMessage("${ChatColor.RED}不明なサブコマンドです")
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>,
    ): List<String> {
        val completions = ArrayList<String>()
        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], listOf("add", "remove", "list"), completions)
            return completions
        }
        if (args.size == 2) {
            if (args[0].equals("add", ignoreCase = true)) {
                StringUtil.copyPartialMatches(args[1], plugin.server.worlds.map(World::getName), completions)
                return completions
            }
            if (args[0].equals("remove", ignoreCase = true)) {
                StringUtil.copyPartialMatches(args[1], worlds(), completions)
                return completions
            }
        }
        return completions
    }
}
