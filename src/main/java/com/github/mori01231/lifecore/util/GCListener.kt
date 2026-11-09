package com.github.mori01231.lifecore.util

import com.github.mori01231.lifecore.LifeCore
import com.github.mori01231.lifecore.command.ScheduleRestartCommand
import com.sun.management.GarbageCollectionNotificationInfo
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import java.lang.management.ManagementFactory
import javax.management.ListenerNotFoundException
import javax.management.Notification
import javax.management.NotificationEmitter
import javax.management.NotificationListener
import javax.management.openmbean.CompositeData

class GCListener(private val plugin: LifeCore) : NotificationListener {
    private var lastGcTime = 0L
    private var count = 0

    override fun handleNotification(notification: Notification, handback: Any?) {
        if (notification.type == GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION) {
            val gcInfo = GarbageCollectionNotificationInfo.from(notification.userData as CompositeData)
            if (gcInfo.gcName != "G1 Young Generation") {
                plugin.logger.info("GC Name: " + gcInfo.gcName)
                plugin.logger.info("GC Cause: " + gcInfo.gcCause)
            }
            if (gcInfo.gcName == "G1 Old Generation") {
                handleGc()
            }
        }
    }

    fun register() {
        for (gcBean in ManagementFactory.getGarbageCollectorMXBeans()) {
            (gcBean as NotificationEmitter).addNotificationListener(this, null, null)
        }
    }

    fun unregister() {
        for (gcBean in ManagementFactory.getGarbageCollectorMXBeans()) {
            try {
                (gcBean as NotificationEmitter).removeNotificationListener(this)
            } catch (_: ListenerNotFoundException) {
            }
        }
    }

    val isTriggered: Boolean
        get() = count < 0

    @Synchronized
    fun handleGc() {
        if (isTriggered) {
            // exceeded max count or triggered manually
            return
        }
        val minutes = (System.currentTimeMillis() - lastGcTime) / 1000 / 60
        lastGcTime = System.currentTimeMillis()
        if (minutes < plugin.config.getInt("gc-threshold-max-minutes-between", 5)) {
            count++
        } else {
            count = 0
            return
        }
        if (count >= plugin.config.getInt("gc-threshold-count", 10)) {
            triggerNow()
        }
    }

    @Synchronized
    fun triggerNow() {
        if (isTriggered) return
        count = -1
        for (command in plugin.config.getStringList("gc-triggered-command")) {
            try {
                if (command.startsWith("@delay ")) {
                    try {
                        val delayTicks = command.substring(7, command.indexOf(' ', 7)).toInt()
                        val actualCommand = command.substring(command.indexOf(' ', 7) + 1)
                        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), actualCommand)
                        }, delayTicks.toLong())
                    } catch (e: IllegalArgumentException) {
                        plugin.slF4JLogger.warn("Invalid @delay: $command", e)
                    }
                } else if (command.startsWith("@schedulerestart ")) {
                    try {
                        val delaySeconds = command.substring("@schedulerestart ".length).toInt()
                        ScheduleRestartCommand.schedule(delaySeconds)
                        val component = Component.text(
                            "再起動までの時間を延長するには、このメッセージをクリックしてください。",
                            NamedTextColor.AQUA,
                            TextDecoration.UNDERLINED
                        ).clickEvent(ClickEvent.runCommand("/gclistenerrestartextendtimecommand"))
                        Bukkit.broadcast(component)
                        Bukkit.broadcast(Component.text("5人が上のメッセージをクリックすると、15分に延長されます。", NamedTextColor.GREEN))
                    } catch (e: IllegalArgumentException) {
                        plugin.slF4JLogger.warn("Invalid @schedulerestart: $command", e)
                    }
                } else {
                    Bukkit.getScheduler().runTask(plugin, Runnable {
                        Bukkit.dispatchCommand(plugin.server.consoleSender, command)
                    })
                }
            } catch (e: Exception) {
                plugin.slF4JLogger.warn("Failed to process command: $command", e)
            }
        }
    }
}
