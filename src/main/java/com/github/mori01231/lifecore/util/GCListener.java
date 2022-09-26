package com.github.mori01231.lifecore.util;

import com.github.mori01231.lifecore.LifeCore;
import com.sun.management.GarbageCollectionNotificationInfo;
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
    private long lastGcTime = 0L;
    private int count = 0;

    @Override
    public void handleNotification(Notification notification, Object handback) {
        if (Objects.equals(notification.getType(), GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {
            GarbageCollectionNotificationInfo gcInfo = GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());
            if (gcInfo.getGcName().equals("G1 Old Generation") && gcInfo.getGcCause().equals("G1 Evacuation Pause")) {
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

    public synchronized void handleGc() {
        if (count < 0) {
            // exceeded max count
            return;
        }
        long minutes = (System.currentTimeMillis() - lastGcTime) / 1000 / 60;
        lastGcTime = System.currentTimeMillis();
        if (minutes < LifeCore.getInstance().getConfig().getInt("gc-threshold-max-minutes-between", 5)) {
            count++;
        } else {
            count = 0;
            return;
        }
        if (count >= LifeCore.getInstance().getConfig().getInt("gc-threshold-count", 10)) {
            count = -1;
            for (String command : LifeCore.getInstance().getConfig().getStringList("gc-triggered-command")) {
                if (command.startsWith("@delay ")) {
                    try {
                        int delayTicks = Integer.parseInt(command.substring(7, command.indexOf(' ', 7)));
                        String actualCommand = command.substring(command.indexOf(' ', 7) + 1);
                        Bukkit.getScheduler().runTaskLater(
                                LifeCore.getInstance(),
                                () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), actualCommand),
                                delayTicks);
                    } catch (IllegalArgumentException e) {
                        LifeCore.getInstance().getLogger().warning("Invalid @delay: " + command);
                    }
                } else {
                    LifeCore.getInstance().getServer().dispatchCommand(LifeCore.getInstance().getServer().getConsoleSender(), command);
                }
            }
        }
    }
}
