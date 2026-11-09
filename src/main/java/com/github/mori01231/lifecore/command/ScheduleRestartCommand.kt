package com.github.mori01231.lifecore.command

import com.github.mori01231.lifecore.LifeCore
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.plugin.java.JavaPlugin
import java.util.Timer
import java.util.TimerTask

class ScheduleRestartCommand(private val plugin: LifeCore) : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage(Component.text("/schedulerestart <minutes>", NamedTextColor.RED))
            return true
        }
        if (args[0].equals("gc", ignoreCase = true)) {
            plugin.gcListener.triggerNow()
            return true
        }
        if (args[0].endsWith("s")) {
            schedule(args[0].dropLast(1).toInt())
            return true
        }
        schedule(args[0].toInt() * 60)
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> =
        listOf("gc")

    enum class Action {
        BROADCAST {
            override fun execute(seconds: Int) {
                runTask {
                    if (seconds >= 60) {
                        broadcast("§6[§dお知らせ§6] §aこのサーバーは§d" + seconds / 60 + "分後§aに再起動されます。")
                    } else {
                        broadcast("§6[§dお知らせ§6] §aこのサーバーは§c" + seconds + "秒後§aに再起動されます。")
                    }
                }
            }
        },
        ENABLE_WHITELIST {
            override fun execute(seconds: Int) {
                runTask {
                    whitelistWasOn = Bukkit.hasWhitelist()
                    Bukkit.setWhitelist(true)
                }
            }
        },
        SAVE_AND_KICK_ALL_PLAYERS {
            override fun execute(seconds: Int) {
                runTask {
                    for (player in Bukkit.getOnlinePlayers()) {
                        player.kick(Component.text("Server closed"))
                    }
                }
            }
        },
        SCHEDULE_SHUTDOWN_SERVER {
            override fun execute(seconds: Int) {
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(LifeCore::class.java), Runnable {
                    if (!whitelistWasOn) Bukkit.setWhitelist(false)
                    Bukkit.shutdown()
                }, 20 * 60)
            }
        },
        ;

        abstract fun execute(seconds: Int)
    }

    companion object {
        private val ACTIONS: Map<Int, Set<Action>> = mapOf(
            60 * 60 to setOf(Action.BROADCAST),
            55 * 60 to setOf(Action.BROADCAST),
            50 * 60 to setOf(Action.BROADCAST),
            45 * 60 to setOf(Action.BROADCAST),
            40 * 60 to setOf(Action.BROADCAST),
            35 * 60 to setOf(Action.BROADCAST),
            30 * 60 to setOf(Action.BROADCAST),
            25 * 60 to setOf(Action.BROADCAST),
            20 * 60 to setOf(Action.BROADCAST),
            15 * 60 to setOf(Action.BROADCAST),
            10 * 60 to setOf(Action.BROADCAST),
            5 * 60 to setOf(Action.BROADCAST),
            4 * 60 to setOf(Action.BROADCAST),
            3 * 60 to setOf(Action.BROADCAST),
            2 * 60 to setOf(Action.BROADCAST, Action.ENABLE_WHITELIST),
            60 to setOf(Action.BROADCAST),
            30 to setOf(Action.BROADCAST),
            10 to setOf(Action.BROADCAST),
            5 to setOf(Action.BROADCAST),
            4 to setOf(Action.BROADCAST),
            3 to setOf(Action.BROADCAST),
            2 to setOf(Action.BROADCAST),
            1 to setOf(Action.BROADCAST),
            0 to setOf(Action.SAVE_AND_KICK_ALL_PLAYERS, Action.SCHEDULE_SHUTDOWN_SERVER),
        )
        private val tasks = mutableListOf<TimerTask>()
        private val TIMER = Timer()
        private var whitelistWasOn = false

        private fun runTask(action: () -> Unit) {
            Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(LifeCore::class.java), Runnable(action))
        }

        private fun broadcast(legacyText: String) {
            Bukkit.broadcast(LegacyComponentSerializer.legacySection().deserialize(legacyText))
        }

        fun schedule(paramSeconds: Int) {
            tasks.forEach { it.cancel() }
            tasks.clear()
            broadcast("§6[§dお知らせ§6] §aこのサーバーは§d" + paramSeconds + "秒後§aに再起動されます。")
            ACTIONS.forEach { (seconds, actions) ->
                if (seconds <= paramSeconds) {
                    val timerTask = object : TimerTask() {
                        override fun run() {
                            runTask { actions.forEach { it.execute(seconds) } }
                        }
                    }
                    TIMER.schedule(timerTask, (paramSeconds - seconds) * 1000L)
                    tasks.add(timerTask)
                }
            }
        }
    }
}
