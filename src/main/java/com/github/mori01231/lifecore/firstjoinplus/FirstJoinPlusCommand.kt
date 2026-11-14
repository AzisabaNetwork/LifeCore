package com.github.mori01231.lifecore.firstjoinplus

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class FirstJoinPlusCommand(private val integration: FirstJoinPlusIntegration) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage(
                "${ChatColor.YELLOW}[FirstJoinPlus] ${ChatColor.GRAY}Version " +
                    "${ChatColor.AQUA}${integration.plugin.description.version}${ChatColor.GRAY} by Azisaba Network.",
            )
            sender.sendMessage(FirstJoinPlusUtilities.formatCommandResponse("http://dev.bukkit.org/bukkit-plugins/firstjoinplus/"))
            return true
        }

        if (args.size != 1) {
            sender.sendMessage(FirstJoinPlusUtilities.formatCommandResponse("Usage: /firstjoinplus <reload|setspawn|debug>"))
            return true
        }

        if (args[0].equals("help", ignoreCase = true)) {
            sender.sendMessage(FirstJoinPlusUtilities.formatCommandResponse("Available Commands:"))
            sender.sendMessage(FirstJoinPlusUtilities.formatCommandResponse("${ChatColor.AQUA}/fjp${ChatColor.GRAY}: General plugin information."))
            sender.sendMessage(FirstJoinPlusUtilities.formatCommandResponse("${ChatColor.AQUA}/fjp reload${ChatColor.GRAY}: Reloads the configuration."))
            sender.sendMessage(FirstJoinPlusUtilities.formatCommandResponse("${ChatColor.AQUA}/fjp setspawn${ChatColor.GRAY}: Sets the first-join spawnpoint."))
            sender.sendMessage(FirstJoinPlusUtilities.formatCommandResponse("${ChatColor.AQUA}/fjp debug${ChatColor.GRAY}: Become a new player!"))
            return true
        }

        if (args[0].equals("reload", ignoreCase = true)) {
            if (sender.hasPermission("firstjoinplus.reload")) {
                integration.manager.reload()
                integration.manager.save()
                FirstJoinPlusUtilities.copyDefaultFiles(integration)
                sender.sendMessage(FirstJoinPlusUtilities.formatCommandResponse("Configuration reloaded."))
            } else {
                sender.sendMessage(FirstJoinPlusUtilities.getNoPermissionMessage())
            }
            return true
        }

        if (sender !is Player) {
            sender.sendMessage(FirstJoinPlusUtilities.formatCommandResponse("You must be a player to do that."))
            return true
        }

        val player: Player = sender
        if (args[0].equals("setspawn", ignoreCase = true)) {
            if (sender.hasPermission("firstjoinplus.setspawn")) {
                val config = integration.manager.config
                config.set("on-first-join.teleport.enabled", true)
                config.set("on-first-join.teleport.x", player.location.blockX)
                config.set("on-first-join.teleport.y", player.location.blockY)
                config.set("on-first-join.teleport.z", player.location.blockZ)
                config.set("on-first-join.teleport.pitch", player.location.pitch)
                config.set("on-first-join.teleport.yaw", player.location.yaw)
                config.set("on-first-join.teleport.world", player.location.world?.name)
                integration.manager.save()
                integration.manager.reload()
                sender.sendMessage(FirstJoinPlusUtilities.formatCommandResponse("Successfully set the first-join spawn location."))
            } else {
                sender.sendMessage(FirstJoinPlusUtilities.getNoPermissionMessage())
            }
            return true
        }

        if (args[0].equals("debug", ignoreCase = true)) {
            if (sender.hasPermission("firstjoinplus.debug")) {
                FirstJoinPlusUtilities.debugPlayer(player, true)
            } else {
                sender.sendMessage(FirstJoinPlusUtilities.getNoPermissionMessage())
            }
            return true
        }

        sender.sendMessage(FirstJoinPlusUtilities.formatCommandResponse("Unknown command. Type " + ChatColor.AQUA + "/fjp help" + ChatColor.GRAY + " for help."))
        return true
    }
}
