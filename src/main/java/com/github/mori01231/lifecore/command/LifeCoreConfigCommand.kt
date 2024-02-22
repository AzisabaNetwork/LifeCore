package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.region.CuboidRegion.Companion.isComplete
import com.github.mori01231.lifecore.region.CuboidRegion.Companion.isIncomplete
import com.github.mori01231.lifecore.region.PlayerRegionManager
import com.github.mori01231.lifecore.region.WorldLocation
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LifeCoreConfigCommand(private val plugin: LifeCore) : PlayerTabExecutor() {
    override fun execute(player: Player, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            return true
        }
        if (args[0] == "pos1" || args[0] == "pos2") {
            val which = if (args[0] == "pos1") "First" else "Second"
            val loc = WorldLocation(player.location)
            if (args[0] == "pos1") {
                plugin.playerRegionManager.put(player.uniqueId, pos1 = loc)
            } else {
                plugin.playerRegionManager.put(player.uniqueId, pos2 = loc)
            }
            val region = plugin.playerRegionManager[player.uniqueId] ?: error("Region is null")
            val suffix = if (region.isComplete()) " ${ChatColor.GRAY}(${region.size} blocks)" else ""
            player.sendMessage("${ChatColor.YELLOW}$which position set to ${ChatColor.LIGHT_PURPLE}${loc.x}, ${loc.y}, ${loc.z}$suffix")
        } else if (args[0] == "wand") {
            player.inventory.addItem(PlayerRegionManager.wandItem)
        } else if (args[0] == "setLobby") {
            val region = plugin.playerRegionManager[player.uniqueId]
            if (region.isIncomplete()) {
                player.sendMessage("${ChatColor.GOLD}/lifecoreconfig wand${ChatColor.RED}の斧で範囲を指定してください。")
                return true
            }
            plugin.lifeCoreConfig.lobbyRegion = region
            plugin.lifeCoreConfig.save(plugin)
            player.sendMessage("${ChatColor.GREEN}ロビーの範囲を設定しました。")
        } else if (args[0] == "removeLobby") {
            plugin.lifeCoreConfig.lobbyRegion = null
            plugin.lifeCoreConfig.save(plugin)
            player.sendMessage("${ChatColor.GREEN}ロビーの範囲を削除しました。")
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> {
        if (args.size == 1) {
            return listOf("pos1", "pos2", "wand", "setLobby", "removeLobby")
                .filter { it.lowercase().startsWith(args[0].lowercase()) }
        }
        return emptyList()
    }
}
