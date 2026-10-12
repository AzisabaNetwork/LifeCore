package com.github.mori01231.lifecore.util;

import com.github.mori01231.lifecore.LifeCore;
import com.github.mori01231.lifecore.command.ScheduleRestartCommand;
import com.sun.management.GarbageCollectionNotificationInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;

import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Objects;

public class GCListener implements NotificationListener {
    private final LifeCore plugin;
    private long lastGcTime = 0L;
    private int count = 0;

    public GCListener(LifeCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handleNotification(Notification notification, Object handback) {
        if (Objects.equals(notification.getType(), GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {
            GarbageCollectionNotificationInfo gcInfo = GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());
            if (!gcInfo.getGcName().equals("G1 Young Generation")) {
                plugin.getLogger().info("GC Name: " + gcInfo.getGcName());
                plugin.getLogger().info("GC Cause: " + gcInfo.getGcCause());
            }
            if (gcInfo.getGcName().equals("G1 Old Generation")) {
                handleGc();
            }
        }
    }

    public void register() {
        for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            NotificationEmitter emitter = (NotificationEmitter) gcBean;
            emitter.addNotificationListener(this, null, null);
        }
    }

    public void unregister() {
        for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            NotificationEmitter emitter = (NotificationEmitter) gcBean;
            try {
                emitter.removeNotificationListener(this);
            } catch (ListenerNotFoundException ignored) {}
        }
    }

    public boolean isTriggered() {
        return count < 0;
    }

    public synchronized void handleGc() {
        if (isTriggered()) {
            // exceeded max count or triggered manually
            return;
        }
        long minutes = (System.currentTimeMillis() - lastGcTime) / 1000 / 60;
        lastGcTime = System.currentTimeMillis();
        if (minutes < plugin.getConfig().getInt("gc-threshold-max-minutes-between", 5)) {
            count++;
        } else {
            count = 0;
            return;
        }
        if (count >= plugin.getConfig().getInt("gc-threshold-count", 10)) {
            triggerNow();
        }
    }

    public synchronized void triggerNow() {
        if (isTriggered()) return;
        count = -1;
        for (String command : plugin.getConfig().getStringList("gc-triggered-command")) {
            try {
                if (command.startsWith("@delay ")) {
                    try {
                        int delayTicks = Integer.parseInt(command.substring(7, command.indexOf(' ', 7)));
                        String actualCommand = command.substring(command.indexOf(' ', 7) + 1);
                        Bukkit.getScheduler().runTaskLater(
                                plugin,
                                () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), actualCommand),
                                delayTicks);
                    } catch (IllegalArgumentException e) {
                        plugin.getSLF4JLogger().warn("Invalid @delay: " + command, e);
                    }
                } else if (command.startsWith("@schedulerestart ")) {
                    try {
                        int delaySeconds = Integer.parseInt(command.substring("@schedulerestart ".length()));
                        ScheduleRestartCommand.schedule(delaySeconds);
                        Component component = Component.text("再起動までの時間を延長するには、このメッセージをクリックしてください。", NamedTextColor.AQUA, TextDecoration.UNDERLINED)
                                .clickEvent(ClickEvent.runCommand("/gclistenerrestartextendtimecommand"));
                        Bukkit.broadcast(component);
                        Bukkit.broadcast(Component.text("5人が上のメッセージをクリックすると、30分に延長されます。", NamedTextColor.GREEN));
                    } catch (IllegalArgumentException e) {
                        plugin.getSLF4JLogger().warn("Invalid @schedulerestart: " + command, e);
                    }
                } else {
                    Bukkit.getScheduler().runTask(plugin, () ->
                            Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), command));
                }
            } catch (Exception e) {
                plugin.getSLF4JLogger().warn("Failed to process command: " + command, e);
            }
        }
    }
}
