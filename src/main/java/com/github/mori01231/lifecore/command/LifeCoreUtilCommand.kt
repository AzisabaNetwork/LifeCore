package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.util.ItemUtil
import net.minecraft.server.v1_15_R1.MojangsonParser
import net.minecraft.server.v1_15_R1.NBTTagCompound
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.craftbukkit.v1_15_R1.CraftServer
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.util.Vector

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
        args: Array<String>
    ): List<String> {
        if (args.isEmpty()) return emptyList()
        if (args.size == 1) {
            return Commands.entries.map { cmd -> cmd.commandName }.filter { it.startsWith(args[0], ignoreCase = true) }
        }
        val handler = Commands.findByName(args[0]) ?: return emptyList()
        return handler.suggest(plugin, sender as Player, args.copyOfRange(1, args.size))
    }

    enum class Commands(val description: String? = null) {
        UpdateCommands("コマンドの補完データを更新します") {
            override fun execute(plugin: LifeCore, player: Player, args: Array<String>) {
                CraftServer::class.java.getDeclaredMethod("syncCommands")
                    .apply { isAccessible = true }
                    .invoke(plugin.server as CraftServer)
            }
        },
        SetFallDistance {
            override fun execute(plugin: LifeCore, player: Player, args: Array<String>) {
                val amount = args.getOrNull(0)?.toDoubleOrNull()
                if (amount == null) {
                    player.sendMessage("${ChatColor.RED}Usage: /lifecoredebug $commandName <amount> [player]")
                    return
                }
                optionalPlayer(player, args, 1).fallDistance = amount.toFloat()
            }

            override fun suggest(plugin: LifeCore, player: Player, args: Array<String>): List<String> {
                if (args.size == 2) return suggestPlayers(args[1])
                return super.suggest(plugin, player, args)
            }
        },
        GetVelocity {
            override fun execute(plugin: LifeCore, player: Player, args: Array<String>) {
                optionalPlayer(player, args, 0).sendMessage(player.velocity.toString())
            }

            override fun suggest(plugin: LifeCore, player: Player, args: Array<String>): List<String> {
                if (args.size == 1) return suggestPlayers(args[0])
                return super.suggest(plugin, player, args)
            }
        },
        SetVelocity {
            override fun execute(plugin: LifeCore, player: Player, args: Array<String>) {
                if (args.size < 3) {
                    player.sendMessage("${ChatColor.RED}Usage: /lifecoredebug $commandName <x> <y> <z> [player]")
                    return
                }
                val x = args[0].toDoubleOrNull()
                val y = args[1].toDoubleOrNull()
                val z = args[2].toDoubleOrNull()
                if (x == null || y == null || z == null) {
                    player.sendMessage("${ChatColor.RED}Invalid number: ${args.joinToString(" ")}")
                    return
                }
                optionalPlayer(player, args, 3).velocity = Vector(x, y, z)
            }

            override fun suggest(plugin: LifeCore, player: Player, args: Array<String>): List<String> {
                if (args.size == 4) return suggestPlayers(args[3])
                return super.suggest(plugin, player, args)
            }
        },
        GetHealth("体力を取得します") {
            override fun execute(plugin: LifeCore, player: Player, args: Array<String>) {
                optionalPlayer(player, args, 0).sendMessage(player.health.toString())
            }

            override fun suggest(plugin: LifeCore, player: Player, args: Array<String>): List<String> {
                if (args.size == 1) return suggestPlayers(args[0])
                return super.suggest(plugin, player, args)
            }
        },
        SetHealth("体力をセットします") {
            override fun execute(plugin: LifeCore, player: Player, args: Array<String>) {
                if (args.isEmpty()) {
                    player.sendMessage("${ChatColor.RED}Usage: /lifecoredebug $commandName <amount> [player]")
                    return
                }
                val amount = args[0].toDoubleOrNull()
                if (amount == null) {
                    player.sendMessage("${ChatColor.RED}Invalid number: ${args[0]}")
                    return
                }
                optionalPlayer(player, args, 1).health = amount
            }

            override fun suggest(plugin: LifeCore, player: Player, args: Array<String>): List<String> {
                if (args.size == 2) return suggestPlayers(args[1])
                return super.suggest(plugin, player, args)
            }
        },
        IsEquippedInAnySlot {
            override fun execute(plugin: LifeCore, player: Player, args: Array<String>) {
                if (args.isEmpty()) {
                    player.sendMessage("${ChatColor.RED}Usage: /lifecoredebug $commandName <mythic type> [player]")
                    return
                }
                player.sendMessage(ItemUtil.isEquippedInAnySlot(optionalPlayer(player, args, 1), args[0]).toString())
            }

            override fun suggest(plugin: LifeCore, player: Player, args: Array<String>): List<String> {
                if (args.size == 2) return suggestPlayers(args[1])
                return super.suggest(plugin, player, args)
            }
        },
        GetTag {
            override fun execute(plugin: LifeCore, player: Player, args: Array<String>) {
                var tag = CraftItemStack.asNMSCopy(player.inventory.itemInMainHand).tag ?: run {
                    player.sendMessage("${ChatColor.RED}Item has no tag.")
                    return
                }
                if (args.isEmpty()) {
                    player.sendMessage(tag.toString())
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
        MergeTag {
            override fun execute(plugin: LifeCore, player: Player, args: Array<String>) {
                if (args.isEmpty()) {
                    player.sendMessage("${ChatColor.RED}Usage: /lifecoredebug $commandName <value>")
                    return
                }
                val item = CraftItemStack.asNMSCopy(player.inventory.itemInMainHand)
                val tag = item.tag ?: NBTTagCompound()
                tag.a(MojangsonParser.parse(args.joinToString(" ")))
                player.inventory.setItemInMainHand(CraftItemStack.asBukkitCopy(item))
            }
        },
        SetTag {
            override fun execute(plugin: LifeCore, player: Player, args: Array<String>) {
                if (args.isEmpty()) {
                    player.sendMessage("${ChatColor.RED}Usage: /lifecoredebug $commandName <value>")
                    return
                }
                val item = CraftItemStack.asNMSCopy(player.inventory.itemInMainHand)
                item.tag = MojangsonParser.parse(args.joinToString(" "))
                player.inventory.setItemInMainHand(CraftItemStack.asBukkitCopy(item))
            }
        },
        GetBlock {
            override fun execute(plugin: LifeCore, player: Player, args: Array<String>) {
                if (args.isEmpty()) {
                    player.sendMessage("${ChatColor.RED}ブロック名を指定してください。")
                    return
                }
                val block = plugin.customBlockManager.findBlockByName(args[0])
                if (block == null) {
                    player.sendMessage("${ChatColor.RED}ブロックが見つかりませんでした。")
                    return
                }
                player.inventory.addItem(block.getItemStack(null))
            }
        },
        ReloadBlocks {
            override fun execute(plugin: LifeCore, player: Player, args: Array<String>) {
                plugin.customBlockManager.reloadBlocks()
                player.sendMessage("${ChatColor.GREEN}カスタムブロックを再読み込みしました。")
            }
        },
        ;

        abstract fun execute(plugin: LifeCore, player: Player, args: Array<String>)

        open fun suggest(plugin: LifeCore, player: Player, args: Array<String>): List<String> = emptyList()

        protected fun optionalPlayer(player: Player, args: Array<String>, index: Int): Player {
            if (args.size > index) {
                return Bukkit.getPlayer(args[index]) ?: player
            }
            return player
        }

        protected fun suggestPlayers(partialName: String) =
            Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(partialName, ignoreCase = true) }

        val commandName = name.replaceFirstChar { it.lowercase() }

        companion object {
            fun findByName(name: String) = entries.find { it.name.equals(name, ignoreCase = true) }
        }
    }
}
