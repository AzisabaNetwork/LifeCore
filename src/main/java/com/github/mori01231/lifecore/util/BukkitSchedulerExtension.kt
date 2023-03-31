package com.github.mori01231.lifecore.util

import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitScheduler

fun BukkitScheduler.runTaskTimerAsynchronously(plugin: Plugin, delayTicks: Long, periodTicks: Long, task: () -> Unit) =
    runTaskTimerAsynchronously(plugin, Runnable(task), delayTicks, periodTicks)

fun BukkitScheduler.runTaskTimer(plugin: Plugin, delayTicks: Long, periodTicks: Long, task: () -> Unit) =
    runTaskTimer(plugin, Runnable(task), delayTicks, periodTicks)
