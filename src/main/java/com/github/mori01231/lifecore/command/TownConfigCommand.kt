package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.config.TownConfig
import com.github.mori01231.lifecore.listener.TownyOutlawListener
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TownConfigCommand(private val plugin: LifeCore) : PlayerTabExecutor() {
    override fun execute(player: Player, args: Array<String>): Boolean {
        val town = TownyOutlawListener.getTownAt(player.location)
        if (town == null) {
            player.sendMessage("${ChatColor.RED}町長ではないため、設定を編集できません。")
            return true
        }
        val mayorName = TownyOutlawListener.getNameTownyObject(TownyOutlawListener.getMayor(town))
        if (player.name != mayorName) {
            player.sendMessage("${ChatColor.RED}町長ではないため、設定を編集できません。")
            return true
        }
        val townName = TownyOutlawListener.getNameTownyObject(town)
        val townConfig = plugin.lifeCoreConfig.townConfig.computeIfAbsent(townName) { TownConfig() }
        if (args.size < 2) {
            townConfig::class.java.fields.filter { it.name != "Companion" }.forEach { field ->
                player.sendMessage("${ChatColor.GOLD}${field.name}${ChatColor.WHITE}: ${field.get(townConfig)}")
            }
            return true
        }
        val valueString = args[1]
        val field = TownConfig::class.java.getField(args[0])
        val value = toValue(field.type, valueString)
        field.set(townConfig, value)
        plugin.lifeCoreConfig.save(plugin)
        player.sendMessage("${ChatColor.GOLD}${args[0]}${ChatColor.GREEN}を${ChatColor.WHITE}$value${ChatColor.GREEN}に設定しました。")
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> {
        if (args.size == 1) {
            return TownConfig::class
                .java
                .fields
                .filter { it.name != "Companion" }
                .map { it.name }
                .filter { it.lowercase().startsWith(args[0].lowercase()) }
        }
        if (args.size == 2) {
            val fieldType = getFieldType(args[0]) ?: return emptyList()
            return getCompletions(fieldType).filter { it.lowercase().startsWith(args[1].lowercase()) }
        }
        return emptyList()
    }

    private fun getFieldType(field: String): Class<*>? =
        runCatching { TownConfig::class.java.getField(field).type }.getOrNull()

    private fun getCompletions(type: Class<*>): List<String> {
        if (type.isEnum) {
            return type.enumConstants.map { it as Enum<*> }.map { it.name.lowercase() }
        }
        return when (type) {
            Boolean::class.java -> listOf("true", "false")
            else -> emptyList()
        }
    }

    private fun toValue(type: Class<*>, value: String): Any {
        if (type.isEnum) {
            return type.enumConstants.first { (it as Enum<*>).name.lowercase() == value.lowercase() }
        }
        return when (type) {
            Boolean::class.java -> value.toBoolean()
            else -> error("Unknown type: $type")
        }
    }
}
