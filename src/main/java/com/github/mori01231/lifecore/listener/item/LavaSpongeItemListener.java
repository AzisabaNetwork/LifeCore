package com.github.mori01231.lifecore.listener.item;

import com.github.mori01231.lifecore.util.ItemUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.RayTraceResult;

public class LavaSpongeItemListener implements Listener {
    private static final String ITEM_ID = "56fabea9-e1f9-4e7f-ae78-83e07e8b8767";

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.useItemInHand() == Event.Result.DENY) return;
        RayTraceResult result = e.getPlayer().rayTraceBlocks(4, FluidCollisionMode.ALWAYS);
        if (result == null || result.getHitBlock() == null) {
            return;
        }
        if (!ITEM_ID.equals(ItemUtil.getStringTag(e.getItem(), "LifeItemId"))) {
            // wrong item
            return;
        }
        int affected = 0;
        int radius = 2;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    Location loc = result.getHitBlock().getLocation().clone().add(dx, dy, dz);
                    Block block = loc.getBlock();
                    if (block.getType() != Material.LAVA && block.getType() != Material.WATER) {
                        // wrong block
                        continue;
                    }
                    if (new BlockBreakEvent(block, e.getPlayer()).callEvent()) {
                        block.setType(Material.AIR, true);
                        affected++;
                    }
                }
            }
        }
        if (affected > 0 && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            // TODO: reduce durability
        }
    }
}
