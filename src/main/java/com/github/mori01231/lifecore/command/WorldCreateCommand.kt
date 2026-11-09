package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.LifeCore
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Difficulty
import org.bukkit.GameRules
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import java.util.UUID

class WorldCreateCommand(private val plugin: LifeCore) : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.size < 2) return help(sender)
        val name = args[0]
        if (!WORLD_NAME_PATTERN.matches(name)) return failed(sender, "そのワールド名は無効です。")
        if (Bukkit.getWorld(name) != null) return failed(sender, "そのワールドはすでに存在します。")

        val environment = enumValueOrNull<World.Environment>(args[1])
            ?: return failed(sender, "environmentを正しく指定してください。")

        var difficulty = Difficulty.EASY
        var type = WorldType.NORMAL
        var seed = UUID.randomUUID().toString()
        var generateStructures = true

        var i = 2
        while (i < args.size - 1) {
            val value = args[i + 1]
            when (args[i]) {
                "-t" -> type = enumValueOrNull<WorldType>(value) ?: return failed(sender, "オプションを正しく入力してください。")
                "-a" -> generateStructures = value.toBoolean()
                "-s" -> seed = value
                "-d" -> difficulty = enumValueOrNull<Difficulty>(value) ?: return failed(sender, "オプションを正しく入力してください。")
                else -> {
                    i++
                    continue
                }
            }
            i += 2
        }

        sender.sendMessage(Component.text("ワールド $name を作成中です...", NamedTextColor.GRAY))
        val world = WorldCreator(name)
            .environment(environment)
            .type(type)
            .seed(seed.toLongOrNull() ?: seed.hashCode().toLong())
            .generateStructures(generateStructures)
            .createWorld()
            ?: return failed(sender, "ワールドの作成に失敗しました。")
        applySettings(world, difficulty)

        // register the world to Multiverse (if installed) so that it will be loaded on startup
        if (Bukkit.getPluginManager().isPluginEnabled("Multiverse-Core")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv import $name ${environment.name}")
        }
        plugin.slF4JLogger.info(
            "{} created world {} (environment: {}, type: {}, seed: {}, structures: {}, difficulty: {})",
            sender.name, name, environment, type, seed, generateStructures, difficulty
        )
        sender.sendMessage(Component.text("ワールド $name を作成しました。", NamedTextColor.GREEN))
        return true
    }

    private fun help(sender: CommandSender): Boolean {
        sender.sendMessage(
            Component.text(
                "/wc [<name>] [<environment>] <-t [<worldType>]> <-s [<seed>]> <-a [<false or true>]> <-d [<difficulty>]>",
                NamedTextColor.YELLOW,
                TextDecoration.BOLD
            )
        )
        return true
    }

    private fun failed(sender: CommandSender, reason: String): Boolean {
        sender.sendMessage(Component.text(reason, NamedTextColor.RED))
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> {
        if (args.size == 1) {
            return listOf("ワールド名を入力してください")
        }
        if (args.size == 2) {
            return World.Environment.entries.map { it.name }.filter { it.startsWith(args[1].uppercase()) }
        }
        return when (args[args.size - 2]) {
            "-t" -> WorldType.entries.map { it.name }
            "-a" -> listOf("true", "false")
            "-s" -> listOf(UUID.randomUUID().toString())
            "-d" -> Difficulty.entries.map { it.name }
            else -> listOf("-t", "-a", "-s", "-d")
        }
    }

    companion object {
        private val WORLD_NAME_PATTERN = Regex("[a-zA-Z0-9/._-]+")

        private inline fun <reified T : Enum<T>> enumValueOrNull(name: String): T? =
            enumValues<T>().firstOrNull { it.name.equals(name, ignoreCase = true) }

        private fun applySettings(w: World, difficulty: Difficulty) {
            w.setGameRule(GameRules.SPAWN_MOBS, false)
            w.setGameRule(GameRules.RANDOM_TICK_SPEED, 0)
            w.setGameRule(GameRules.IMMEDIATE_RESPAWN, true)
            w.setGameRule(GameRules.KEEP_INVENTORY, true)
            w.setGameRule(GameRules.RESPAWN_RADIUS, 0)
            @Suppress("DEPRECATION")
            w.pvp = false
            w.difficulty = difficulty
            w.worldBorder.setCenter(0.0, 0.0)
        }
    }
}
