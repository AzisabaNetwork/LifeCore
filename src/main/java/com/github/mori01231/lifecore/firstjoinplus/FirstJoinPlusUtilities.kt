package com.github.mori01231.lifecore.firstjoinplus

import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Effect
import org.bukkit.FireworkEffect
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.attribute.Attribute
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.inventory.meta.FireworkMeta
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.logging.Level
import kotlin.random.Random

object FirstJoinPlusUtilities {

    @JvmStatic
    fun getFirstJoinLocation(): Location {
        val plugin = FirstJoinPlusIntegration.getInstance().plugin
        val config = FirstJoinPlusIntegration.getInstance().manager.config
        val world: World? = plugin.server.getWorld(config.getString("on-first-join.teleport.world") ?: "")
        val x = config.getInt("on-first-join.teleport.x")
        val y = config.getInt("on-first-join.teleport.y")
        val z = config.getInt("on-first-join.teleport.z")
        val yaw = config.getInt("on-first-join.teleport.yaw").toFloat()
        val pitch = config.getInt("on-first-join.teleport.pitch").toFloat()
        return Location(world, x + 0.5, y.toDouble(), z + 0.5, yaw, pitch)
    }

    @JvmStatic
    fun getFirstJoinKit(): List<ItemStack> {
        val config = FirstJoinPlusIntegration.getInstance().manager.config
        val kit = ArrayList<ItemStack>()
        for (s in config.getStringList("on-first-join.first-join-kit.items")) {
            val item = s.split(":".toRegex()).toTypedArray()
            val mat = Material.getMaterial(item[0].uppercase())
            if (mat == null) {
                val plugin = FirstJoinPlusIntegration.getInstance().plugin
                plugin.logger.log(Level.SEVERE, "Error encountered while attempting to give a new player the first join kit. Unknown item name: " + item[0])
                plugin.logger.log(Level.SEVERE, "Find and double check item names using this page:")
                plugin.logger.log(Level.SEVERE, "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html")
                continue
            }
            var amount = 1
            if (item.size > 1) {
                amount = item[1].toIntOrNull() ?: run {
                    val plugin = FirstJoinPlusIntegration.getInstance().plugin
                    plugin.logger.log(Level.SEVERE, "Error encountered while attempting to give a new player the first join kit.")
                    plugin.logger.log(Level.SEVERE, "The item amount must be a number: " + item[1])
                    -1
                }
            }
            if (amount != -1) {
                kit.add(ItemStack(mat, amount))
            }
        }
        return kit
    }

    @JvmStatic
    fun getWrittenBooks(viewingPlayer: Player): List<ItemStack> {
        val integration = FirstJoinPlusIntegration.getInstance()
        val config = integration.manager.config
        val books = ArrayList<ItemStack>()
        for (fileName in config.getStringList("on-first-join.give-written-books.book-files")) {
            val book = ItemStack(Material.WRITTEN_BOOK)
            val bm = book.itemMeta as BookMeta
            val f = File(integration.plugin.dataFolder, fileName)
            try {
                BufferedReader(FileReader(f)).use { br ->
                    var sb = StringBuilder()
                    var line: String? = br.readLine()
                    var i = 0
                    while (line != null) {
                        i++
                        line = replaceVariables(line, viewingPlayer)
                        if (i != 1 && i != 2) {
                            if (line.equals("/newpage", ignoreCase = true) || line.equals("/np", ignoreCase = true)) {
                                bm.addPage(sb.toString())
                                sb = StringBuilder()
                            } else {
                                sb.append(translateColors(line))
                                sb.append("\n")
                            }
                        } else {
                            if (i == 1) bm.setTitle(translateColors(line))
                            if (i == 2) bm.setAuthor(translateColors(line))
                        }
                        line = br.readLine()
                    }
                    bm.addPage(sb.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                integration.plugin.logger.log(Level.WARNING, "Error encountered while trying to give a new player a written book! (File: $fileName)")
                integration.plugin.logger.log(Level.WARNING, "Please check that the file exists and is readable.")
            }
            book.itemMeta = bm
            books.add(book)
        }
        return books
    }

    @JvmStatic
    fun launchRandomFirework(location: Location) {
        val random = Random.Default
        val fw = location.world.spawnEntity(location, EntityType.FIREWORK_ROCKET) as Firework
        val meta = fw.fireworkMeta
        meta.setPower(1 + random.nextInt(4))
        val builder = FireworkEffect.builder()
            .trail(random.nextBoolean())
            .flicker(random.nextBoolean())
        builder.with(FireworkEffect.Type.entries.toTypedArray()[random.nextInt(FireworkEffect.Type.entries.size)])
        val colors = HashSet<Color>()
        for (i in 0 until 3) {
            colors.add(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
        }
        builder.withColor(colors)
        meta.addEffect(builder.build())
        fw.fireworkMeta = meta
    }

    @JvmStatic
    fun getRandomPlayer(): Player? {
        val online = FirstJoinPlusIntegration.getInstance().plugin.server.onlinePlayers
        if (online.isEmpty()) return null
        return online.toList()[Random.nextInt(online.size)]
    }

    @JvmStatic
    fun replaceVariables(string: String, player: Player): String {
        var s = string
        s = s.replace("%player_name", player.name)
        s = s.replace("%player_display_name", player.displayName)
        s = s.replace("%player_uuid", player.uniqueId.toString())
        s = s.replace("%player_country", getCountry())
        s = s.replace("%player_city", getCity())
        s = s.replace("%total_players", getTotalPlayerCount().toString())
        s = s.replace("%random_player", getRandomPlayer()?.name ?: "")
        s = s.replace("%random_player_display_name", getRandomPlayer()?.displayName ?: "")
        s = s.replace("%new_line", "\n")
        return translateColors(s)
    }

    @JvmStatic
    fun replaceVariables(string: String, player: Player, reason: String): String {
        return replaceVariables(string.replace("%reason", reason), player)
    }

    private fun getTotalPlayerCount(): Int {
        return FirstJoinPlusIntegration.getInstance().plugin.server.offlinePlayers.size
    }

    /**
     * GeoIP 連携は統合時に省略したため、常に不明を返す。
     */
    private fun getCountry(): String = "N/A"

    private fun getCity(): String = "N/A"

    @JvmStatic
    fun translateColors(string: String): String {
        return ChatColor.translateAlternateColorCodes('&', string)
    }

    /**
     * 旧 FirstJoinPlus 設定で使われていた `entity_player_levelup` のような
     * アンダースコア形式のサウンド名を、現行の `entity.player.levelup` 形式に変換する。
     */
    @JvmStatic
    fun normalizeSoundName(soundName: String): String {
        if (soundName.contains('.')) return soundName
        return soundName.replace('_', '.')
    }

    @JvmStatic
    fun formatCommandResponse(string: String): String {
        return ChatColor.YELLOW.toString() + "[FJP] " + ChatColor.GRAY + string
    }

    @JvmStatic
    fun getNoPermissionMessage(): String {
        return formatCommandResponse(ChatColor.RED.toString() + "You don't have permission to do that.")
    }

    @JvmStatic
    fun copyDefaultFiles(integration: FirstJoinPlusIntegration) {
        val config = integration.manager.config
        config.options().copyDefaults(true)
        config.options().copyHeader(true)
        integration.manager.save()
        if (config.getBoolean("on-first-join.give-written-books.enabled")) {
            val rules = File(integration.plugin.dataFolder, "rules.txt")
            if (!rules.exists()) {
                try {
                    integration.plugin.saveResource("rules.txt", false)
                } catch (_: IllegalArgumentException) {
                    // rules.txt が jar に含まれていない場合はスキップ
                }
            }
        }
    }

    @JvmStatic
    fun debugPlayer(player: Player, b: Boolean) {
        val integration = FirstJoinPlusIntegration.getInstance()
        player.inventory.clear()
        player.inventory.setHelmet(null)
        player.inventory.setChestplate(null)
        player.inventory.setLeggings(null)
        player.inventory.setBoots(null)
        player.health = player.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0
        player.allowFlight = false
        player.isFlying = false
        player.exhaustion = 0f
        player.exp = 0f
        player.foodLevel = 20
        player.level = 0
        player.saturation = 0f
        player.closeInventory()
        player.gameMode = integration.plugin.server.defaultGameMode

        if (b) {
            integration.plugin.server.pluginManager.callEvent(
                FirstJoinEvent(PlayerJoinEvent(player, player.name + " joined for the first time!")),
            )
        }
    }
}
