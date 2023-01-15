package com.github.mori01231.lifecore.listener.item;

import com.github.mori01231.lifecore.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class OreOnlyItemListener implements Listener {
    private static final String ITEM_ID = "3f7cec47-aaec-41b3-a47e-c3f87e16932a";
    private static final Set<Material> EXCEPTION_TYPES;

    static {
        Set<Material> types = new HashSet<>();
        types.add(Material.CHEST);
        types.add(Material.ENDER_CHEST);
        types.add(Material.BARREL);
        types.add(Material.BLAST_FURNACE);
        types.add(Material.FURNACE);
        types.add(Material.TRAPPED_CHEST);
        types.add(Material.DISPENSER);
        types.add(Material.DROPPER);
        types.add(Material.HOPPER);
        types.add(Material.SMOKER);
        types.add(Material.BEACON);
        types.add(Material.CONDUIT);
        types.add(Material.DIAMOND_BLOCK);
        EXCEPTION_TYPES = Collections.unmodifiableSet(types);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockDropItem(@NotNull BlockDropItemEvent e) {
        if (!e.getPlayer().getWorld().getName().startsWith("resource")) {
            // wrong world
            return;
        }
        if (!ITEM_ID.equals(ItemUtil.getStringTag(e.getPlayer().getInventory().getItemInOffHand(), "LifeItemId"))) {
            // wrong offhand item
            return;
        }
        Material type = e.getBlockState().getType();
        if (type.name().endsWith("_SIGN") || type.name().endsWith("_DOOR") || type.name().endsWith("_ORE") ||
                type.name().endsWith("SHULKER_BOX")) {
            return;
        }
        if (EXCEPTION_TYPES.contains(type)) {
            // wrong block type
            return;
        }
        e.setCancelled(true);
    }
}
