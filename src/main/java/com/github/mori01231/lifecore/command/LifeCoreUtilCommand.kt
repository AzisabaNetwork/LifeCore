package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.util.ItemUtil
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack
import org.bukkit.entity.Player

class LifeCoreUtilCommand(val plugin: LifeCore) : TabExecutor {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}You must be player to execute this command.")
            return true
        }
        if (args.isEmpty()) {
            sendHelp(sender)
            return true
        }
        val handler = Commands.findByName(args[0])
        if (handler == null) {
            sendHelp(sender)
            return true
        }
        handler.execute(plugin, sender, args.copyOfRange(1, args.size))
        return true
    }

    private fun sendHelp(sender: CommandSender) {
        Commands.entries.forEach { cmd ->
            if (cmd.description == null) {
                sender.sendMessage("${ChatColor.AQUA}/lifecoredebug ${cmd.commandName}")
            } else {
                sender.sendMessage("${ChatColor.AQUA}/lifecoredebug ${cmd.commandName} ${ChatColor.GRAY}- ${ChatColor.GREEN}${cmd.description}")
            }
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        p1: Command,
        p2: String,
        args: Array<out String>
    ): List<String> {
        if (args.size == 1) {
            return Commands.entries.map { cmd -> cmd.commandName }
        }
        return emptyList()
    }

    enum class Commands(val description: String? = null) {
        SetHealth("体力をセットします") {
            override fun execute(plugin: LifeCore, player: Player, args: Array<String>) {
                if (args.isEmpty()) {
                    player.sendMessage("${ChatColor.RED}Usage: /lifecoredebug $commandName <amount>")
                    return
                }
                val amount = args[0].toDoubleOrNull()
                if (amount == null) {
                    player.sendMessage("${ChatColor.RED}Invalid number: ${args[0]}")
                    return
                }
                player.health = amount
            }
        },
        IsEquippedInAnySlot {
            override fun execute(plugin: LifeCore, player: Player, args: Array<String>) {
                if (args.isEmpty()) {
                    player.sendMessage("${ChatColor.RED}Usage: /lifecoredebug $commandName <mythic type>")
                    return
                }
                player.sendMessage(ItemUtil.isEquippedInAnySlot(player, args[0]).toString())
            }
        },
        GetTag {
            override fun execute(plugin: LifeCore, player: Player, args: Array<String>) {
                if (args.isEmpty()) {
                    player.sendMessage("${ChatColor.RED}Usage: /lifecoredebug $commandName <tag names...>")
                    return
                }
                var tag = CraftItemStack.asNMSCopy(player.inventory.itemInMainHand).tag ?: run {
                    player.sendMessage("${ChatColor.RED}Item has no tag.")
                    return
                }
                for (i in args.indices) {
                    val key = args[i]
                    if (i == args.size - 1) {
                        player.sendMessage("$key: ${tag.get(key)}")
                    } else {
                        tag = tag.getCompound(key)
                    }
                }
            }
        },
        ;

        abstract fun execute(plugin: LifeCore, player: Player, args: Array<String>)

        val commandName = name.replaceFirstChar { it.lowercase() }

        companion object {
            fun findByName(name: String) = entries.find { it.name.equals(name, ignoreCase = true) }
        }
    }
}
