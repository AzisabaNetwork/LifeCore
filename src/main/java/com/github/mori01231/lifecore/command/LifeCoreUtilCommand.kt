package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.DBConnector
import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.util.EncodeUtil
import com.github.mori01231.lifecore.util.ItemUtil
import com.github.mori01231.lifecore.util.MapUtil
import com.github.mori01231.lifecore.util.MapUtil.getCanvases
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.ByteArrayTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.TagParser
import net.minecraft.world.item.component.CustomData
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.craftbukkit.map.CraftMapRenderer
import org.bukkit.craftbukkit.map.CraftMapView
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.util.Vector

class LifeCoreUtilCommand(val plugin: LifeCore) : TabExecutor {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            sendHelp(sender)
            return true
        }
        val handler = Commands.findByName(args[0])
        if (handler == null) {
            sendHelp(sender)
            return true
        }
        if (!sender.hasPermission("lifecore.lifecoreutil.${handler.name}")) {
            sender.sendMessage("${ChatColor.RED}You don't have permission to execute this command.")
            return true
        }
        handler.execute(plugin, sender, args.copyOfRange(1, args.size))
        return true
    }

    private fun sendHelp(sender: CommandSender) {
        Commands.entries.forEach { cmd ->
            if (!sender.hasPermission("lifecore.lifecoreutil.${cmd.name}"))
            if (cmd.description == null) {
                sender.sendMessage("${ChatColor.AQUA}/lifecoreutil ${cmd.commandName}")
            } else {
                sender.sendMessage("${ChatColor.AQUA}/lifecoreutil ${cmd.commandName} ${ChatColor.GRAY}- ${ChatColor.GREEN}${cmd.description}")
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
            return Commands.entries
                .map { cmd -> cmd.commandName }
                .filter { it.startsWith(args[0], ignoreCase = true) && sender.hasPermission("lifecore.lifecoreutil.$it") }
        }
        val handler = Commands.findByName(args[0]) ?: return emptyList()
        return handler.suggest(plugin, sender as Player, args.copyOfRange(1, args.size))
    }

    enum class Commands(val description: String? = null) {
        UpdateCommands("コマンドの補完データを更新します") {
            override fun execute(plugin: LifeCore, player: CommandSender, args: Array<String>) {
                CraftServer::class.java.getDeclaredMethod("syncCommands")
                    .apply { isAccessible = true }
                    .invoke(plugin.server as CraftServer)
            }
        },
        Glowing("発光状態を切り替えます") {
            override fun execute(plugin: LifeCore, player: CommandSender, args: Array<String>) {
                if (args.isEmpty()) {
                    player.sendMessage("${ChatColor.RED}Usage: /lifecoreutil $commandName <true|false> [player]")
                    return
                }
                val glowing = args[0].toBoolean()
                optionalPlayer(player, args, 1).isGlowing = glowing
            }
        },
        AddPassenger("<sender>の上に<player>を乗せます") {
            override fun execute(plugin: LifeCore, player: CommandSender, args: Array<String>) {
                if (args.size < 2) {
                    player.sendMessage("${ChatColor.RED}Usage: /lifecoreutil $commandName <player> [sender]")
                    return
                }
                val passenger = Bukkit.getPlayerExact(args[1]) ?: run {
                    player.sendMessage("${ChatColor.RED}Player not found: ${args[1]}")
                    return
                }
                optionalPlayer(player, args, 2).addPassenger(passenger)
            }

            override fun suggest(plugin: LifeCore, player: Player, args: Array<String>): List<String> {
                if (args.size == 1 || args.size == 2) return suggestPlayers(args.last())
                return super.suggest(plugin, player, args)
            }
        },
        Eject("<sender>からプレイヤーを降ろします") {
            override fun execute(plugin: LifeCore, player: CommandSender, args: Array<String>) {
                optionalPlayer(player, args, 0).eject()
            }

            override fun suggest(plugin: LifeCore, player: Player, args: Array<String>): List<String> {
                if (args.size == 1) return suggestPlayers(args[0])
                return super.suggest(plugin, player, args)
            }
        },
        SetFallDistance {
            override fun execute(plugin: LifeCore, player: CommandSender, args: Array<String>) {
                val amount = args.getOrNull(0)?.toDoubleOrNull()
                if (amount == null) {
                    player.sendMessage("${ChatColor.RED}Usage: /lifecoreutil $commandName <amount> [player]")
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
            override fun execute(plugin: LifeCore, player: CommandSender, args: Array<String>) {
                optionalPlayer(player, args, 0).sendMessage((player as Player).velocity.toString())
            }

            override fun suggest(plugin: LifeCore, player: Player, args: Array<String>): List<String> {
                if (args.size == 1) return suggestPlayers(args[0])
                return super.suggest(plugin, player, args)
            }
        },
        SetVelocity {
            override fun execute(plugin: LifeCore, player: CommandSender, args: Array<String>) {
                if (args.size < 3) {
                    player.sendMessage("${ChatColor.RED}Usage: /lifecoreutil $commandName <x> <y> <z> [player]")
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
            override fun execute(plugin: LifeCore, player: CommandSender, args: Array<String>) {
                optionalPlayer(player, args, 0).sendMessage((player as Player).health.toString())
            }

            override fun suggest(plugin: LifeCore, player: Player, args: Array<String>): List<String> {
                if (args.size == 1) return suggestPlayers(args[0])
                return super.suggest(plugin, player, args)
            }
        },
        SetHealth("体力をセットします") {
            override fun execute(plugin: LifeCore, player: CommandSender, args: Array<String>) {
                if (args.isEmpty()) {
                    player.sendMessage("${ChatColor.RED}Usage: /lifecoreutil $commandName <amount> [player]")
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
            override fun execute(plugin: LifeCore, player: CommandSender, args: Array<String>) {
                if (args.isEmpty()) {
                    player.sendMessage("${ChatColor.RED}Usage: /lifecoreutil $commandName <mythic type> [player]")
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
            override fun execute(plugin: LifeCore, player: CommandSender, args: Array<String>) {
                var tag = ItemUtil.getCustomData((player as Player).inventory.itemInMainHand) ?: run {
                    player.sendMessage(Component.text("Item has no tag.", NamedTextColor.RED))
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
            override fun execute(plugin: LifeCore, player: CommandSender, args: Array<String>) {
                if (args.isEmpty()) {
                    player.sendMessage("${ChatColor.RED}Usage: /lifecoreutil $commandName <value>")
                    return
                }
                val item = CraftItemStack.asNMSCopy((player as Player).inventory.itemInMainHand)
                val tag = ItemUtil.getCustomData((player as Player).inventory.itemInMainHand) ?: CompoundTag()
                tag.merge(TagParser.parseTag(args.joinToString(" ")))
                item.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
                player.inventory.setItemInMainHand(CraftItemStack.asBukkitCopy(item))
            }
        },
        SetTag {
            override fun execute(plugin: LifeCore, player: CommandSender, args: Array<String>) {
                if (args.isEmpty()) {
                    player.sendMessage(Component.text("Usage: /lifecoreutil $commandName <value>", NamedTextColor.RED))
                    return
                }
                val tag = TagParser.parseTag(args.joinToString(" "))
                val item = CraftItemStack.asNMSCopy((player as Player).inventory.itemInMainHand)
                item.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
                player.inventory.setItemInMainHand(CraftItemStack.asBukkitCopy(item))
            }
        },
        GetBlock {
            override fun execute(plugin: LifeCore, player: CommandSender, args: Array<String>) {
                if (args.isEmpty()) {
                    player.sendMessage(Component.text("ブロック名を指定してください。", NamedTextColor.RED))
                    return
                }
                val block = plugin.customBlockManager.findBlockByName(args[0])
                if (block == null) {
                    player.sendMessage(Component.text("ブロックが見つかりませんでした。", NamedTextColor.RED))
                    return
                }
                (player as Player).inventory.addItem(block.getItemStack(null))
            }

            override fun suggest(plugin: LifeCore, player: Player, args: Array<String>): List<String> {
                if (args.size == 1) {
                    return plugin.customBlockManager.getBlocks()
                        .map { it.name }
                        .filter { it.lowercase().startsWith(args[0].lowercase()) }
                }
                return super.suggest(plugin, player, args)
            }
        },
        ReloadBlocks {
            override fun execute(plugin: LifeCore, player: CommandSender, args: Array<String>) {
                plugin.customBlockManager.reloadBlocks()
                player.sendMessage("${ChatColor.GREEN}カスタムブロックを再読み込みしました。")
            }
        },
        ReloadConfig("指定したプラグインのconfig.ymlをリロードさせます") {
            override fun execute(plugin: LifeCore, player: CommandSender, args: Array<String>) {
                if (args.isEmpty()) {
                    player.sendMessage("${ChatColor.RED}Usage: /lifecoreutil $commandName <plugin>")
                    return
                }
                val target = args[0]
                val targetPlugin = Bukkit.getPluginManager().getPlugin(target)
                if (targetPlugin == null) {
                    player.sendMessage("${ChatColor.RED}プラグインが見つかりませんでした。")
                    return
                }
                targetPlugin.reloadConfig()
                player.sendMessage("${ChatColor.GREEN}${target}のconfig.ymlをリロードしました。")
            }

            override fun suggest(plugin: LifeCore, player: Player, args: Array<String>): List<String> {
                if (args.size == 1) return Bukkit.getPluginManager().plugins.map { it.name }
                return super.suggest(plugin, player, args)
            }
        },
        ClearPlot("イマサラタウンのplotを消し飛ばします") {
            override fun execute(plugin: LifeCore, player: CommandSender, args: Array<String>) {
                val minX = (player as Player).chunk.x * 16
                val minZ = player.chunk.z * 16
                val maxX = minX + 15
                val maxZ = minZ + 15
                scheduleLater(plugin, 0) { Bukkit.dispatchCommand(player, "/chunk") }
                scheduleLater(plugin, 1) { Bukkit.dispatchCommand(player, "/regen") }
                scheduleLater(plugin, 2) { Bukkit.dispatchCommand(player, "/pos1 $minX,3,$minZ") }
                scheduleLater(plugin, 3) { Bukkit.dispatchCommand(player, "/pos2 $maxX,30,$maxZ") }
                scheduleLater(plugin, 4) { Bukkit.dispatchCommand(player, "/cut -e") }
                scheduleLater(plugin, 5) { Bukkit.dispatchCommand(player, "/pos2 $maxX,3,$maxZ") }
                scheduleLater(plugin, 6) { Bukkit.dispatchCommand(player, "/set minecraft:grass_block") }
                scheduleLater(plugin, 7) { Bukkit.dispatchCommand(player, "/pos1 $minX,4,$minZ") }
                scheduleLater(plugin, 8) { Bukkit.dispatchCommand(player, "/pos2 $maxX,4,$maxZ") }
                scheduleLater(plugin, 9) { Bukkit.dispatchCommand(player, "/walls minecraft:smooth_stone_slab") }
                scheduleLater(plugin, 10) { Bukkit.dispatchCommand(player, "plot fs") }
            }

            private fun scheduleLater(plugin: LifeCore, delayTicks: Long, action: () -> Unit) {
                Bukkit.getScheduler().runTaskLater(plugin, Runnable(action), delayTicks)
            }
        },
        GetOfflineMoney("offline_moneyを取得します") {
            @Suppress("DEPRECATION")
            override fun execute(plugin: LifeCore, player: CommandSender, args: Array<String>) {
                if (args.isEmpty()) {
                    player.sendMessage("${ChatColor.RED}Usage: /lifecoreutil $commandName <player>")
                    return
                }
                Bukkit.getScheduler().runTaskAsynchronously(LifeCore.instance, Runnable {
                    val target = Bukkit.getOfflinePlayer(args[0])
                    val result =
                        DBConnector.getPrepareStatement("SELECT * FROM `mpdb_economy` WHERE `player_uuid` = ?") { statement ->
                            statement.setString(1, target.uniqueId.toString())
                            statement.executeQuery().use { result ->
                                if (result.next()) {
                                    result.getDouble("offline_money")
                                } else {
                                    null
                                }
                            }
                        }
                    Bukkit.getScheduler().runTask(LifeCore.instance, Runnable {
                        if (result == null) {
                            player.sendMessage("${ChatColor.RED}プレイヤーが見つかりませんでした。")
                        } else {
                            player.sendMessage("${ChatColor.GREEN}${target.name}のoffline_money: $result")
                        }
                    })
                })
            }

            override fun suggest(plugin: LifeCore, player: Player, args: Array<String>): List<String> {
                if (args.size == 1) return suggestPlayers(args[0])
                return super.suggest(plugin, player, args)
            }
        },
        SetOfflineMoney("offline_moneyをセットします") {
            @Suppress("DEPRECATION")
            override fun execute(plugin: LifeCore, player: CommandSender, args: Array<String>) {
                if (args.size < 2) {
                    player.sendMessage("${ChatColor.RED}Usage: /lifecoreutil $commandName <player> <amount>")
                    return
                }
                val amount = args[1].toDoubleOrNull() ?: run {
                    player.sendMessage("${ChatColor.RED}無効な数値: ${args[1]}")
                    return
                }
                Bukkit.getScheduler().runTaskAsynchronously(LifeCore.instance, Runnable {
                    val target = Bukkit.getOfflinePlayer(args[0])
                    val result =
                        DBConnector.getPrepareStatement("UPDATE `mpdb_economy` SET `offline_money` = ? WHERE `player_uuid` = ?") { statement ->
                            statement.setDouble(1, amount)
                            statement.setString(2, target.uniqueId.toString())
                            statement.executeUpdate()
                        }
                    Bukkit.getScheduler().runTask(LifeCore.instance, Runnable {
                        player.sendMessage("${ChatColor.GREEN}$result rows affected")
                    })
                })
            }

            override fun suggest(plugin: LifeCore, player: Player, args: Array<String>): List<String> {
                if (args.size == 1) return suggestPlayers(args[0])
                return super.suggest(plugin, player, args)
            }
        },
        AddOfflineMoney("offline_moneyを追加(add)します") {
            @Suppress("DEPRECATION")
            override fun execute(plugin: LifeCore, player: CommandSender, args: Array<String>) {
                if (args.size < 2) {
                    player.sendMessage("${ChatColor.RED}Usage: /lifecoreutil $commandName <player> <amount>")
                    return
                }
                val amount = args[1].toDoubleOrNull() ?: run {
                    player.sendMessage("${ChatColor.RED}無効な数値: ${args[1]}")
                    return
                }
                Bukkit.getScheduler().runTaskAsynchronously(LifeCore.instance, Runnable {
                    val target = Bukkit.getOfflinePlayer(args[0])
                    val result =
                        DBConnector.getPrepareStatement("UPDATE `mpdb_economy` SET `offline_money` = `offline_money` + ? WHERE `player_uuid` = ?") { statement ->
                            statement.setDouble(1, amount)
                            statement.setString(2, target.uniqueId.toString())
                            statement.executeUpdate()
                        }
                    Bukkit.getScheduler().runTask(LifeCore.instance, Runnable {
                        player.sendMessage("${ChatColor.GREEN}$result rows affected")
                    })
                })
            }

            override fun suggest(plugin: LifeCore, player: Player, args: Array<String>): List<String> {
                if (args.size == 1) return suggestPlayers(args[0])
                return super.suggest(plugin, player, args)
            }
        },
        SaveMapData("地図をサーバー移動可能な形に変換します") {
            override fun execute(plugin: LifeCore, player: CommandSender, args: Array<String>) {
                val meta = (player as Player).inventory.itemInMainHand.itemMeta as? MapMeta? ?: return player.sendMessage("地図を手に持って再度実行してください")
                val mapView = meta.mapView ?: return player.sendMessage("mapView is null")
                if (mapView.renderers.getOrNull(0) !is CraftMapRenderer) return player.sendMessage("すでに変換済みのようです")
                if (mapView is CraftMapView) mapView.render(player as CraftPlayer)
                val canvas =
                    mapView.getCanvases()[mapView.renderers.first()]?.get(player as CraftPlayer)
                        ?: mapView.getCanvases()[mapView.renderers.first()]?.get(null as CraftPlayer?)
                        ?: return player.sendMessage("canvas not found")
                val data = MapUtil.serializeCanvasToString(canvas)
                player.inventory.setItemInMainHand(ItemUtil.setTag(
                    player.inventory.itemInMainHand,
                    "SerializedMapData",
                    ByteArrayTag(EncodeUtil.encodeBase64AndGzip(data.toByteArray()))
                ))
            }
        },
        LoadMapData("地図を読み込みます") {
            override fun execute(plugin: LifeCore, player: CommandSender, args: Array<String>) {
                MapUtil.checkMapView((player as Player).inventory.itemInMainHand)?.let {
                    player.inventory.setItemInMainHand(it)
                }
                MapUtil.initializeMapRenderer(player, player.inventory.itemInMainHand)
            }
        },
        ResetMapId("地図IDをリセットします") {
            override fun execute(plugin: LifeCore, player: CommandSender, args: Array<String>) {
                player as Player
                val meta = player.inventory.itemInMainHand.itemMeta as? MapMeta? ?: return player.sendMessage("this is not a map")
                val mapView = meta.mapView ?: return player.sendMessage("mapView is null")
                if (mapView.renderers.isEmpty() || mapView.renderers.filterIsInstance<CraftMapRenderer>().isNotEmpty()) {
                    return player.sendMessage("renderers[0] is an instance of CraftMapRenderer or is null")
                }
                meta.mapView = Bukkit.createMap(player.world)
                player.inventory.setItemInMainHand(player.inventory.itemInMainHand.apply { itemMeta = meta })
            }
        },
        FixItem("displayタグを修正します") {
            override fun execute(plugin: LifeCore, player: CommandSender, args: Array<String>) {
                val item = (player as Player).inventory.itemInMainHand
                val meta = item.itemMeta
                if (meta.hasDisplayName()) {
                    val origName = meta.displayName
                    meta.setDisplayName(null)
                    meta.setDisplayName(origName)
                }
                if (meta.hasLore()) {
                    val origLore = meta.lore
                    meta.lore = null
                    meta.lore = origLore
                }
                item.itemMeta = meta
                player.inventory.setItemInMainHand(item)
            }
        },
        ;

        abstract fun execute(plugin: LifeCore, player: CommandSender, args: Array<String>)

        open fun suggest(plugin: LifeCore, player: Player, args: Array<String>): List<String> = emptyList()

        protected fun optionalPlayer(player: CommandSender, args: Array<String>, index: Int): Player {
            if (args.size > index) {
                return Bukkit.getPlayerExact(args[index]) ?: player as Player
            }
            return player as Player
        }

        protected fun suggestPlayers(partialName: String) =
            Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(partialName, ignoreCase = true) }

        val commandName = name.replaceFirstChar { it.lowercase() }

        companion object {
            fun findByName(name: String) = entries.find { it.name.equals(name, ignoreCase = true) }
        }
    }
}
