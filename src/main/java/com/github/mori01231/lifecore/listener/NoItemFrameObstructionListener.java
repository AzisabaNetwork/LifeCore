package com.github.mori01231.lifecore.listener;

import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;

public class NoItemFrameObstructionListener implements Listener {
    @EventHandler
    public void cancelItemFrameObstruction(HangingBreakEvent e) {
        if (e.getCause() == HangingBreakEvent.RemoveCause.OBSTRUCTION && e.getEntity() instanceof ItemFrame) {
            e.setCancelled(true);
        }
    }
}
